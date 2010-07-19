/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.net.channel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;


import net.sf.odinms.client.BuddyList;
import net.sf.odinms.client.BuddylistEntry;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.BuddyList.BuddyAddResult;
import net.sf.odinms.client.BuddyList.BuddyOperation;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.ByteArrayMaplePacket;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.remote.ChannelWorldInterface;
import net.sf.odinms.net.world.CharacterTransfer;
import net.sf.odinms.net.world.MapleMessenger;
import net.sf.odinms.net.world.MapleMessengerCharacter;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.PartyOperation;
import net.sf.odinms.net.world.guild.MapleGuildSummary;
import net.sf.odinms.net.world.remote.CheaterData;
import net.sf.odinms.server.ShutdownServer;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.CollectionUtil;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Packets.GuildPacket;

/**
 *
 * @author Matze
 */
public class ChannelWorldInterfaceImpl extends UnicastRemoteObject implements ChannelWorldInterface {

    private static final long serialVersionUID = 7815256899088644192L;
    private ChannelServer server;

    public ChannelWorldInterfaceImpl() throws RemoteException {
	super(0, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
    }

    public ChannelWorldInterfaceImpl(ChannelServer server) throws RemoteException {
	super(0, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
	this.server = server;
    }

    @Override
    public void setChannelId(int id) throws RemoteException {
	server.setChannel(id);
    }

    @Override
    public int getChannelId() throws RemoteException {
	return server.getChannel();
    }

    @Override
    public String getIP() throws RemoteException {
	return server.getIP();
    }

    @Override
    public void broadcastMessage(byte[] message) throws RemoteException {
	server.broadcastPacket(new ByteArrayMaplePacket(message));
    }

    @Override
    public void broadcastSmega(byte[] message) throws RemoteException {
	server.broadcastSmegaPacket(new ByteArrayMaplePacket(message));
    }

    @Override
    public void broadcastASmega(byte[] message) throws RemoteException {
	server.broadcastASmegaPacket(new ByteArrayMaplePacket(message));
    }

    @Override
    public void broadcastGMMessage(byte[] message) throws RemoteException {
	server.broadcastGMPacket(new ByteArrayMaplePacket(message));
    }

    @Override
    public void whisper(String sender, String target, int channel, String message) throws RemoteException {
	if (isConnected(target)) {
	    server.getPlayerStorage().getCharacterByName(target).getClient().getSession().write(MaplePacketCreator.getWhisper(sender, channel, message));
	}
    }

    @Override
    public boolean isConnected(String charName) throws RemoteException {
	return server.getPlayerStorage().getCharacterByName(charName) != null;
    }

    @Override
    public boolean isCharacterListConnected(List<String> charName) throws RemoteException {
	for (final String c : charName) {
	    if (server.getPlayerStorage().getCharacterByName(c) != null) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public void toggleMegaphoneMuteState() throws RemoteException {
	server.toggleMegaponeMuteState();
    }

    @Override
    public boolean hasMerchant(int accountId) throws RemoteException {
	return server.constainsMerchant(accountId);
    }

    @Override
    public void shutdown(int time) throws RemoteException {
	TimerManager.getInstance().schedule(new ShutdownServer(server.getChannel()), time);
    }

    @Override
    public int getConnected() throws RemoteException {
	return server.getPlayerStorage().getConnectedClients();
    }

    @Override
    public void loggedOff(String name, int characterId, int channel, int[] buddies) throws RemoteException {
	updateBuddies(characterId, channel, buddies, true);
    }

    @Override
    public void loggedOn(String name, int characterId, int channel, int buddies[]) throws RemoteException {
	updateBuddies(characterId, channel, buddies, false);
    }

    private void updateBuddies(int characterId, int channel, int[] buddies, boolean offline) {
	final PlayerStorage playerStorage = server.getPlayerStorage();
	for (int buddy : buddies) {
	    final MapleCharacter chr = playerStorage.getCharacterById(buddy);
	    if (chr != null) {
		final BuddylistEntry ble = chr.getBuddylist().get(characterId);
		if (ble != null && ble.isVisible()) {
		    int mcChannel;
		    if (offline) {
			ble.setChannel(-1);
			mcChannel = -1;
		    } else {
			ble.setChannel(channel);
			mcChannel = channel - 1;
		    }
		    chr.getBuddylist().put(ble);
		    chr.getClient().getSession().write(MaplePacketCreator.updateBuddyChannel(ble.getCharacterId(), mcChannel));
		}
	    }
	}
    }

    @Override
    public void updateParty(MapleParty party, PartyOperation operation, MaplePartyCharacter target) throws RemoteException {
	for (MaplePartyCharacter partychar : party.getMembers()) {
	    if (partychar.getChannel() == server.getChannel()) {
		final MapleCharacter chr = server.getPlayerStorage().getCharacterByName(partychar.getName());
		if (chr != null) {
		    if (operation == PartyOperation.DISBAND) {
			chr.setParty(null);
		    } else {
			chr.setParty(party);
		    }
		    chr.getClient().getSession().write(MaplePacketCreator.updateParty(chr.getClient().getChannel(), party, operation, target));
		}
	    }
	}
	switch (operation) {
	    case LEAVE:
	    case EXPEL:
		if (target.getChannel() == server.getChannel()) {
		    final MapleCharacter chr = server.getPlayerStorage().getCharacterByName(target.getName());
		    if (chr != null) {
			chr.getClient().getSession().write(MaplePacketCreator.updateParty(chr.getClient().getChannel(), party, operation, target));
			chr.setParty(null);
		    }
		}
	}
    }

    @Override
    public void partyChat(MapleParty party, String chattext, String namefrom) throws RemoteException {
	for (MaplePartyCharacter partychar : party.getMembers()) {
	    if (partychar.getChannel() == server.getChannel() && !(partychar.getName().equals(namefrom))) {
		final MapleCharacter chr = server.getPlayerStorage().getCharacterByName(partychar.getName());
		if (chr != null) {
		    chr.getClient().getSession().write(MaplePacketCreator.multiChat(namefrom, chattext, 1));
		}
	    }
	}
    }

    @Override
    public boolean isAvailable() throws RemoteException {
	return true;
    }

    @Override
    public int getLocation(String name) throws RemoteException {
	MapleCharacter chr = server.getPlayerStorage().getCharacterByName(name);
	if (chr != null) {
	    return chr.getMapId();
	}
	return -1;
    }

    @Override
    public List<CheaterData> getCheaters() throws RemoteException {
	List<CheaterData> cheaters = server.getPlayerStorage().getCheaters();

	Collections.sort(cheaters);
	return CollectionUtil.copyFirst(cheaters, 20);
    }

    @Override
    public BuddyAddResult requestBuddyAdd(String addName, int channelFrom, int cidFrom, String nameFrom) {
	final MapleCharacter addChar = server.getPlayerStorage().getCharacterByName(addName);
	if (addChar != null) {
	    final BuddyList buddylist = addChar.getBuddylist();
	    if (buddylist.isFull()) {
		return BuddyAddResult.BUDDYLIST_FULL;
	    }
	    if (!buddylist.contains(cidFrom)) {
		buddylist.addBuddyRequest(addChar.getClient(), cidFrom, nameFrom, channelFrom);
	    } else {
		if (buddylist.containsVisible(cidFrom)) {
		    return BuddyAddResult.ALREADY_ON_LIST;
		}
	    }
	}
	return BuddyAddResult.OK;
    }

    @Override
    public boolean isConnected(int characterId) throws RemoteException {
	return server.getPlayerStorage().getCharacterById(characterId) != null;
    }

    @Override
    public void buddyChanged(int cid, int cidFrom, String name, int channel, BuddyOperation operation) {
	final MapleCharacter addChar = server.getPlayerStorage().getCharacterById(cid);
	if (addChar != null) {
	    final BuddyList buddylist = addChar.getBuddylist();
	    switch (operation) {
		case ADDED:
		    if (buddylist.contains(cidFrom)) {
			buddylist.put(new BuddylistEntry(name, "ETC", cidFrom,  channel, true));
			addChar.getClient().getSession().write(MaplePacketCreator.updateBuddyChannel(cidFrom, channel - 1));
		    }
		    break;
		case DELETED:
		    if (buddylist.contains(cidFrom)) {
			buddylist.put(new BuddylistEntry(name, "ETC", cidFrom, -1, buddylist.get(cidFrom).isVisible()));
			addChar.getClient().getSession().write(MaplePacketCreator.updateBuddyChannel(cidFrom, -1));
		    }
		    break;
	    }
	}
    }

    @Override
    public void buddyChat(int[] recipientCharacterIds, int cidFrom, String nameFrom, String chattext) throws RemoteException {
	final PlayerStorage playerStorage = server.getPlayerStorage();
	for (int characterId : recipientCharacterIds) {
	    final MapleCharacter chr = playerStorage.getCharacterById(characterId);
	    if (chr != null) {
		if (chr.getBuddylist().containsVisible(cidFrom)) {
		    chr.getClient().getSession().write(MaplePacketCreator.multiChat(nameFrom, chattext, 0));
		}
	    }
	}
    }

    @Override
    public int[] multiBuddyFind(int charIdFrom, int[] characterIds) throws RemoteException {
	List<Integer> ret = new ArrayList<Integer>(characterIds.length);
	final PlayerStorage playerStorage = server.getPlayerStorage();
	for (int characterId : characterIds) {
	    MapleCharacter chr = playerStorage.getCharacterById(characterId);
	    if (chr != null) {
		if (chr.getBuddylist().containsVisible(charIdFrom)) {
		    ret.add(characterId);
		}
	    }
	}
	int[] retArr = new int[ret.size()];
	int pos = 0;
	for (Integer i : ret) {
	    retArr[pos++] = i.intValue();
	}
	return retArr;
    }

    @Override
    public void ChannelChange_Data(CharacterTransfer transfer, int characterid) throws RemoteException {
	server.getPlayerStorage().registerPendingPlayer(transfer, characterid);
    }

    @Override
    public void sendPacket(List<Integer> targetIds, MaplePacket packet, int exception) throws RemoteException {
	MapleCharacter c;
	for (int i : targetIds) {
	    if (i == exception) {
		continue;
	    }
	    c = server.getPlayerStorage().getCharacterById(i);
	    if (c != null) {
		c.getClient().getSession().write(packet);
	    }
	}
    }

    @Override
    public void setGuildAndRank(List<Integer> cids, int guildid, int rank, int exception) throws RemoteException {
	for (int cid : cids) {
	    if (cid != exception) {
		setGuildAndRank(cid, guildid, rank);
	    }
	}
    }

    @Override
    public void setGuildAndRank(int cid, int guildid, int rank) throws RemoteException {
	final MapleCharacter mc = server.getPlayerStorage().getCharacterById(cid);
	if (mc == null) {
	    // System.out.println("ERROR: cannot find player in given channel");
	    return;
	}

	boolean bDifferentGuild;
	if (guildid == -1 && rank == -1) { //just need a respawn
	    bDifferentGuild = true;
	} else {
	    bDifferentGuild = guildid != mc.getGuildId();
	    mc.setGuildId(guildid);
	    mc.setGuildRank(rank);
	    mc.saveGuildStatus();
	}
	if (bDifferentGuild) {
	    mc.getMap().broadcastMessage(mc, MaplePacketCreator.removePlayerFromMap(cid), false);
	    mc.getMap().broadcastMessage(mc, MaplePacketCreator.spawnPlayerMapobject(mc), false);
	}
    }

    @Override
    public void setOfflineGuildStatus(int guildid, byte guildrank, int cid) throws RemoteException {
	try {
	    java.sql.Connection con = DatabaseConnection.getConnection();
	    java.sql.PreparedStatement ps = con.prepareStatement("UPDATE characters SET guildid = ?, guildrank = ? WHERE id = ?");
	    ps.setInt(1, guildid);
	    ps.setInt(2, guildrank);
	    ps.setInt(3, cid);
	    ps.execute();
	    ps.close();
	} catch (SQLException se) {
	    System.out.println("SQLException: " + se.getLocalizedMessage() + se);
	}
    }

    @Override
    public void changeEmblem(int gid, List<Integer> affectedPlayers, MapleGuildSummary mgs) throws RemoteException {
	ChannelServer.getInstance(this.getChannelId()).updateGuildSummary(gid, mgs);
	this.sendPacket(affectedPlayers, GuildPacket.guildEmblemChange(gid, mgs.getLogoBG(), mgs.getLogoBGColor(), mgs.getLogo(), mgs.getLogoColor()), -1);
	this.setGuildAndRank(affectedPlayers, -1, -1, -1);	//respawn player
    }

    @Override
    public void messengerInvite(String sender, int messengerid, String target, int fromchannel) throws RemoteException {
	if (isConnected(target)) {
	    final MapleMessenger messenger = server.getPlayerStorage().getCharacterByName(target).getMessenger();
	    if (messenger == null) {
		server.getPlayerStorage().getCharacterByName(target).getClient().getSession().write(MaplePacketCreator.messengerInvite(sender, messengerid));

		ChannelServer.getInstance(fromchannel).getPlayerStorage().getCharacterByName(sender).getClient().getSession().write(MaplePacketCreator.messengerNote(target, 4, 1));
	    } else {
		ChannelServer.getInstance(fromchannel).getPlayerStorage().getCharacterByName(sender).getClient().getSession().write(MaplePacketCreator.messengerChat(sender + " : " + target + " is already using Maple Messenger"));
	    }
	}
    }

    @Override
    public void addMessengerPlayer(MapleMessenger messenger, String namefrom, int fromchannel, int position) throws RemoteException {
	for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
	    if (messengerchar.getChannel() == server.getChannel() && !(messengerchar.getName().equals(namefrom))) {
		final MapleCharacter chr = server.getPlayerStorage().getCharacterByName(messengerchar.getName());
		if (chr != null) {
		    final MapleCharacter from = ChannelServer.getInstance(fromchannel).getPlayerStorage().getCharacterByName(namefrom);
		    chr.getClient().getSession().write(MaplePacketCreator.addMessengerPlayer(namefrom, from, position, fromchannel - 1));
		    from.getClient().getSession().write(MaplePacketCreator.addMessengerPlayer(chr.getName(), chr, messengerchar.getPosition(), messengerchar.getChannel() - 1));
		}
	    } else if (messengerchar.getChannel() == server.getChannel() && (messengerchar.getName().equals(namefrom))) {
		final MapleCharacter chr = server.getPlayerStorage().getCharacterByName(messengerchar.getName());
		if (chr != null) {
		    chr.getClient().getSession().write(MaplePacketCreator.joinMessenger(messengerchar.getPosition()));
		}
	    }
	}
    }

    @Override
    public void removeMessengerPlayer(MapleMessenger messenger, int position) throws RemoteException {
	for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
	    if (messengerchar.getChannel() == server.getChannel()) {
		final MapleCharacter chr = server.getPlayerStorage().getCharacterByName(messengerchar.getName());
		if (chr != null) {
		    chr.getClient().getSession().write(MaplePacketCreator.removeMessengerPlayer(position));
		}
	    }
	}
    }

    @Override
    public void messengerChat(MapleMessenger messenger, String chattext, String namefrom) throws RemoteException {
	for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
	    if (messengerchar.getChannel() == server.getChannel() && !(messengerchar.getName().equals(namefrom))) {
		final MapleCharacter chr = server.getPlayerStorage().getCharacterByName(messengerchar.getName());
		if (chr != null) {
		    chr.getClient().getSession().write(MaplePacketCreator.messengerChat(chattext));
		}
	    }
	}
    }

    @Override
    public void declineChat(String target, String namefrom) throws RemoteException {
	if (isConnected(target)) {
	    final MapleCharacter chr = server.getPlayerStorage().getCharacterByName(target);
	    final MapleMessenger messenger = chr.getMessenger();
	    if (messenger != null) {
		chr.getClient().getSession().write(MaplePacketCreator.messengerNote(namefrom, 5, 0));
	    }
	}
    }

    @Override
    public void updateMessenger(MapleMessenger messenger, String namefrom, int position, int fromchannel) throws RemoteException {
	for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
	    if (messengerchar.getChannel() == server.getChannel() && !(messengerchar.getName().equals(namefrom))) {
		final MapleCharacter chr = server.getPlayerStorage().getCharacterByName(messengerchar.getName());
		if (chr != null) {
		    MapleCharacter from = ChannelServer.getInstance(fromchannel).getPlayerStorage().getCharacterByName(namefrom);
		    chr.getClient().getSession().write(MaplePacketCreator.updateMessengerPlayer(namefrom, from, position, fromchannel - 1));
		}
	    }
	}
    }

}
