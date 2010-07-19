/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.net.channel.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.List;
import net.sf.odinms.client.BuddylistEntry;
import net.sf.odinms.client.Buffs.MapleBuffStat;
import net.sf.odinms.client.CharacterNameAndId;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleQuestStatus;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.CharacterIdChannelPair;
import net.sf.odinms.net.world.CharacterTransfer;
import net.sf.odinms.net.world.MapleMessenger;
import net.sf.odinms.net.world.MapleMessengerCharacter;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.PartyOperation;
import net.sf.odinms.net.world.PlayerBuffValueHolder;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.constants.SpecialStuff;
import net.sf.odinms.server.maps.FieldLimitType;
import net.sf.odinms.server.maps.SavedLocationType;
import net.sf.odinms.server.shops.IMaplePlayerShop;
import net.sf.odinms.tools.FileoutputUtil;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Packets.FamilyPacket;
import net.sf.odinms.tools.Packets.GuildPacket;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class InterServerHandler {

    public static final void ChangeChannel(final byte channel, final MapleClient c, final MapleCharacter chr) {
        if (!chr.isAlive()) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        final ChannelServer toch = ChannelServer.getInstance(channel);

        if (FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit()) || channel == c.getChannel()) {
            c.getSession().close();
            return;
        } else if (toch == null || toch.isShutdown()) {
            c.getSession().write(MaplePacketCreator.serverBlocked(1));
            return;
        }

        {
            if (chr.getTrade() != null) {
                MapleTrade.cancelTrade(chr.getTrade());
            }
            if (chr.getPets() != null) {
                chr.unequipAllPets();
            }
            if (chr.getCheatTracker() != null) {
                chr.getCheatTracker().dispose();
            }
            if (chr.getBuffedValue(MapleBuffStat.SUMMON) != null) {
                chr.cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
            }
            if (chr.getBuffedValue(MapleBuffStat.PUPPET) != null) {
                chr.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
            }
            final IMaplePlayerShop shop = chr.getPlayerShop();
            if (shop != null) {
                shop.removeVisitor(chr);
                if (shop.isOwner(chr)) {
                    shop.setOpen(true);
                }
            }
            chr.removeClones();
        }

        final ChannelServer ch = ChannelServer.getInstance(c.getChannel());
        try {
            final WorldChannelInterface wci = ch.getWorldInterface();

            if (chr.getMessenger() != null) {
                wci.silentLeaveMessenger(chr.getMessenger().getId(), new MapleMessengerCharacter(chr));
            }
            wci.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
            wci.addCooldownsToStorage(chr.getId(), chr.getAllCoolDowns());
            wci.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
            wci.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), channel);
        } catch (RemoteException e) {
            e.printStackTrace();
            c.getChannelServer().reconnectWorld();
        }
        ch.removePlayer(chr);
        c.updateLoginState(MapleClient.CHANGE_CHANNEL, c.getSessionIPAddress());

        try {
            final String[] socket = ch.getIP(channel).split(":");
            c.getSession().write(MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
        } catch (UnknownHostException e) {
            // Ignore
        }
        chr.saveToDB(true, false);
        chr.getMap().removePlayer(chr);
        c.setPlayer(null);
    }

    public static final void Loggedin(final int playerid, final MapleClient c) {
        final ChannelServer channelServer = c.getChannelServer();
        MapleCharacter player;
        final CharacterTransfer transfer = channelServer.getPlayerStorage().getPendingCharacter(playerid);

        if (transfer == null) { // Player isn't in storage, probably isn't CC
            player = MapleCharacter.loadCharFromDB(playerid, c, true);
        } else {
            player = MapleCharacter.ReconstructChr(transfer, c, true);
        }
        c.setPlayer(player);
        c.setAccID(player.getAccountID());

        if (!c.CheckIPAddress()) { // Remote hack
            c.getSession().close();
            return;
        }

        final int state = c.getLoginState();
        boolean allowLogin = false;

        try {
            if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL) {
                if (!channelServer.getWorldInterface().isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
                    allowLogin = true;
                }
            }
        } catch (RemoteException e) {
            channelServer.reconnectWorld();
        }
        if (!allowLogin) {
            c.setPlayer(null);
            c.getSession().close();
            return;
        }
        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());

        final ChannelServer cserv = ChannelServer.getInstance(c.getChannel());
        cserv.addPlayer(player);

        c.getSession().write(MaplePacketCreator.getCharInfo(player));
        player.getMap().addPlayer(player);

        try {
            // Start of cooldown, buffs
            final WorldChannelInterface wci = ChannelServer.getInstance(c.getChannel()).getWorldInterface();

            final List<PlayerBuffValueHolder> buffs = wci.getBuffsFromStorage(player.getId());
            if (buffs != null) {
                player.silentGiveBuffs(buffs);
            }
            c.getPlayer().giveCoolDowns(wci.getCooldownsFromStorage(player.getId()));
            c.getPlayer().giveSilentDebuff(wci.getDiseaseFromStorage(player.getId()));

            // Start of buddylist
            final int buddyIds[] = player.getBuddylist().getBuddyIds();
            cserv.getWorldInterface().loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds);
            if (player.getParty() != null) {
                channelServer.getWorldInterface().updateParty(player.getParty().getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));
            }
            final CharacterIdChannelPair[] onlineBuddies = cserv.getWorldInterface().multiBuddyFind(player.getId(), buddyIds);
            for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                final BuddylistEntry ble = player.getBuddylist().get(onlineBuddy.getCharacterId());
                ble.setChannel(onlineBuddy.getChannel());
                player.getBuddylist().put(ble);
            }
            c.getSession().write(MaplePacketCreator.updateBuddylist(player.getBuddylist().getBuddies()));

            // Start of Messenger
            final MapleMessenger messenger = player.getMessenger();
            final int messenger_pos = player.getMessengerPosition();
            if (player.getMessenger() != null && messenger_pos < 4 && messenger_pos > -1) {
                MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer(), messenger_pos);
                wci.silentJoinMessenger(messenger.getId(), messengerplayer, messenger_pos);
                wci.updateMessenger(c.getPlayer().getMessenger().getId(), c.getPlayer().getName(), c.getChannel());
            }

            // Start of Guild and alliance
            if (player.getGuildId() > 0) {
                c.getChannelServer().getWorldInterface().setGuildMemberOnline(player.getMGC(), true, c.getChannel());
                c.getSession().write(GuildPacket.showGuildInfo(player));
                /*		if (player.getGuild().getAllianceId() > 0) {
                c.getSession().write(MaplePacketCreator.showAllianceInfo(player));
                c.getSession().write(MaplePacketCreator.showAllianceMembers(player));
                }*/
            }
        } catch (RemoteException e) {
            channelServer.reconnectWorld();
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.Login_Error, e);
        }
        c.getSession().write(FamilyPacket.getFamilyData());
        player.sendMacros();
        player.showNote();
        player.updatePartyMemberHP();

        c.getSession().write(MaplePacketCreator.getKeymap(player.getKeyLayout()));

        for (MapleQuestStatus status : player.getStartedQuests()) {
            if (status.hasMobKills()) {
                c.getSession().write(MaplePacketCreator.updateQuestMobKills(status));
            }
        }
        final CharacterNameAndId pendingBuddyRequest = player.getBuddylist().pollPendingRequest();
        if (pendingBuddyRequest != null) {
            player.getBuddylist().put(new BuddylistEntry(pendingBuddyRequest.getName(), pendingBuddyRequest.getId(), -1, false));
            c.getSession().write(MaplePacketCreator.requestBuddylistAdd(pendingBuddyRequest.getId(), pendingBuddyRequest.getName()));
        }
        player.expirationTask();
        if (player.getJob() == 132) { // DARKKNIGHT
            player.checkBerserk();
        }
    }

    public static final void EnterCS(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (!chr.isAlive()) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        } else if (!SpecialStuff.getInstance().canWarpFrom(chr)) {
            c.showMessage("[KrystleCruz]You cannot Enter CashShop from this map!");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        chr.removeClones();
        final ChannelServer ch = ChannelServer.getInstance(c.getChannel());

        String CSIP = null;
        try {
            CSIP = ch.getWorldInterface().getCSIP();
        } catch (RemoteException e) {
            c.getChannelServer().reconnectWorld();
        }

        if (CSIP == null) { // Cash Shop not init yet
            c.getSession().write(MaplePacketCreator.serverBlocked(2));
            return;
        }

        {
            if (chr.getTrade() != null) {
                MapleTrade.cancelTrade(chr.getTrade());
            }
            if (chr.getCheatTracker() != null) {
                chr.getCheatTracker().dispose();
            }
            if (chr.getBuffedValue(MapleBuffStat.SUMMON) != null) {
                chr.cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
            }
            if (chr.getBuffedValue(MapleBuffStat.PUPPET) != null) {
                chr.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
            }
            final IMaplePlayerShop shop = chr.getPlayerShop();
            if (shop != null) {
                shop.removeVisitor(chr);
                if (shop.isOwner(chr)) {
                    shop.setOpen(true);
                }
            }
        }
        try {
            final WorldChannelInterface wci = ch.getWorldInterface();
            if (chr.getMessenger() != null) {
                MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
                wci.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
            }
            wci.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
            wci.addCooldownsToStorage(chr.getId(), chr.getAllCoolDowns());
            wci.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
            wci.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), -10);
        } catch (RemoteException e) {
            c.getChannelServer().reconnectWorld();
        }
        ch.removePlayer(chr);
        c.updateLoginState(MapleClient.CHANGE_CHANNEL, c.getSessionIPAddress());
        try {
            final String[] socket = CSIP.split(":");
            c.getSession().write(MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
        } catch (UnknownHostException e) {
            // Ignore
        }
        chr.saveToDB(false, false);
        chr.getMap().removePlayer(chr);
        c.setPlayer(null);
    }

    public static void EnterMTS(MapleClient c) {
        if (!(c.getPlayer().isAlive())) {
            c.showMessage("You cannot warp to the Free Market when you're dead!");
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (!SpecialStuff.getInstance().canWarpFrom(c.getPlayer())) {
            c.showMessage("You cannot warp to the Free Market from this map!");
            c.getSession().write(MaplePacketCreator.enableActions());
        } else {
            if (c.getPlayer().getMapId() != 910000000) {
                new ServernoticeMapleClientMessageCallback(5, c).dropMessage("You are being warped to the Free Market.");
                c.getSession().write(MaplePacketCreator.enableActions());
                c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET);
                c.getPlayer().changeMap(c.getChannelServer().getMapFactory().getMap(910000000), c.getChannelServer().getMapFactory().getMap(910000000).getPortal("out00"));
            } else {
                c.showMessage("You're already in the Free Market!");
                c.getSession().write(MaplePacketCreator.enableActions());
            }
        }    
    }
}

