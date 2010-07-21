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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.CashShop.CashShopServer;
import net.sf.odinms.net.world.CharacterTransfer;
import net.sf.odinms.net.world.remote.CashShopInterface;
import net.sf.odinms.server.CashItemFactory;
import net.sf.odinms.server.CashItemInfo;
import net.sf.odinms.server.MapleInventoryManipulator;
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
        showCS(c);
        c.getSession().write(MTSCSPacket.sendWishList(chr, false));
    }

    public static final void buyCashItem(SeekableLittleEndianAccessor slea, MapleClient c) {
        int action = slea.readByte();
        if (action == 3) {
            slea.skip(1);
            int useNX = slea.readInt();
            int snCS = slea.readInt();
            CashItemInfo item = CashItemFactory.getItem(snCS);
            if (item.getBlock() == 1) {
                c.getPlayer().dropMessage(1, "This Item is Blocked from being bought. too bad so sad");
                showCS(c);
                return;
            }
            if (!MapleInventoryManipulator.checkSpace(c, item.getId(), item.getCount(), "")) {
                c.getPlayer().dropMessage(1, "You do not have enough inventory space!");
            } else {
                if (c.getPlayer().getCSPoints(useNX) >= item.getPrice()) {
                    c.getPlayer().addCSPoints(useNX, -item.getPrice());
                } else {
                    c.getPlayer().dropMessage(1, "You do not have enough Nx!");
                    showCS(c);
                    return;
                }
                MapleInventoryManipulator.addById(c, item.getId(), (short) item.getCount(), "");
                c.getSession().write(MTSCSPacket.showBoughtCSItem(c, item));
            }
            showCS(c);
        } else if (action == 4) { // Gifting, not GMS like without the cash inventories
            showCS(c);
        } else if (action == 5) {
            c.getPlayer().clearWishlist();
            int[] wishlist = new int[10];
            for (int i = 0; i < 10; i++) {
                wishlist[i] = slea.readInt();
            }
            c.getSession().write(MTSCSPacket.sendWishList(c.getPlayer(), true));
            showCS(c);
        } else if (action == 12) {
            c.getPlayer().dropMessage(1, "This item is already in your inventory!");
            showCS(c);
        } else if (action == 31) {
            c.getPlayer().dropMessage(1, "You don't need this item any way");
            showCS(c);
        }

    }

    private static final void showCS(MapleClient c) {
        c.getSession().write(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
        c.getSession().write(MTSCSPacket.enableUse0());
        c.getSession().write(MTSCSPacket.enableUse1());
        c.getSession().write(MTSCSPacket.enableUse2());
        c.getSession().write(MTSCSPacket.enableUse3());
        c.getSession().write(MaplePacketCreator.enableActions());
    }
}
