/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.net.channel.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import net.sf.odinms.client.Buffs.MapleBuffStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MapleMessengerCharacter;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.maps.FieldLimitType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class InterServerHandler {

     public static final void ChangeChannel(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
	if (!chr.isAlive()) {
	    c.getSession().write(MaplePacketCreator.enableActions());
	    return;
	}
	final int channel = slea.readByte() + 1;
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
		MapleTrade.cancelTrade(chr);
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
	}

	final ChannelServer ch = ChannelServer.getInstance(c.getChannel());
	try {
	    final WorldChannelInterface wci = ch.getWorldInterface();

	    if (chr.getMessenger() != null) {
		wci.silentLeaveMessenger(chr.getMessenger().getId(), new MapleMessengerCharacter(chr));
	    }
	    wci.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
	    wci.addCooldownsToStorage(chr.getId(), chr.getAllCooldowns());
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
	chr.saveToDB();
	chr.getMap().removePlayer(chr);
	c.setPlayer(null);
    }

    public static void playerLoggedIn(SeekableLittleEndianAccessor slea, MapleClient c) {

    }
}
