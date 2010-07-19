/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
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
package net.sf.odinms.net.CashShop.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.CashShop.CashShopServer;
import net.sf.odinms.net.world.CharacterTransfer;
import net.sf.odinms.net.world.remote.CashShopInterface;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Packets.MTSCSPacket;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class CashShopOperation {

    public static final void LeaveCS(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final CashShopServer cs = CashShopServer.getInstance();

        cs.getPlayerStorage().deregisterPlayer(chr);

        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());

        try {
            final CashShopInterface wci = cs.getCSInterface();

            wci.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), c.getChannel());

            final String ip = wci.getChannelIP(c.getChannel());
            final String[] socket = ip.split(":");
            c.getSession().write(MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));

        } catch (RemoteException e) {
            c.getChannelServer().reconnectWorld();
        } catch (UnknownHostException e) {
        } finally {
            c.getSession().close();

            chr.saveToDB(false, true);
            c.setPlayer(null);
        }
    }

    public static final void EnterCS(final int playerid, final MapleClient c) {
        final CashShopServer cs = CashShopServer.getInstance();
        final CharacterTransfer transfer = cs.getPlayerStorage().getPendingCharacter(playerid);

        if (transfer == null) {
            c.getSession().close();
            return;
        }
        MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, c, false);

        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());

        if (!c.CheckIPAddress()) { // Remote hack
            c.getSession().close();
            return;
        }

        final int state = c.getLoginState();
        boolean allowLogin = false;

        try {
            if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL) {
                if (!cs.getCSInterface().isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
                    allowLogin = true;
                }
            }
        } catch (RemoteException e) {
            cs.reconnectWorld();
        }
        if (!allowLogin) {
            c.setPlayer(null);
            c.getSession().close();
            return;
        }
        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());

        cs.getPlayerStorage().registerPlayer(chr);

        c.getSession().write(MTSCSPacket.warpCS(c));
        c.getSession().write(MTSCSPacket.enableUse0());
        c.getSession().write(MTSCSPacket.enableUse1());
        c.getSession().write(MTSCSPacket.enableUse2());
        c.getSession().write(MTSCSPacket.enableUse3());
        c.getSession().write(MTSCSPacket.showNXMapleTokens(chr));
        c.getSession().write(MTSCSPacket.sendWishList(chr, false));
    }
}
