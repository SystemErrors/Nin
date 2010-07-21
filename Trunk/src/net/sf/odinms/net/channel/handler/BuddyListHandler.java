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
package net.sf.odinms.net.channel.handler;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.odinms.client.BuddyList;
import net.sf.odinms.client.BuddyList.BuddyAddResult;
import net.sf.odinms.client.BuddyList.BuddyOperation;
import net.sf.odinms.client.BuddylistEntry;
import net.sf.odinms.client.CharacterNameAndId;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.remote.ChannelWorldInterface;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class BuddyListHandler {

    private static final class CharacterIdNameBuddyCapacity extends CharacterNameAndId {

        private int gmlvl;

        public CharacterIdNameBuddyCapacity(int id, String name, int gmlvl) {
            super(id, name);
            this.gmlvl = gmlvl;
        }

        private int gmlvl() {
            return gmlvl;
        }
    }

    private static final void nextPendingRequest(final MapleClient c) {
        CharacterNameAndId pendingBuddyRequest = c.getPlayer().getBuddylist().pollPendingRequest();
        if (pendingBuddyRequest != null) {
            c.getSession().write(MaplePacketCreator.requestBuddylistAdd(pendingBuddyRequest.getId(), pendingBuddyRequest.getName()));
        }
    }

    private static final CharacterIdNameBuddyCapacity getCharacterIdAndNameFromDatabase(final String name) throws SQLException {
        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = con.prepareStatement("SELECT id, name, gm FROM characters WHERE name LIKE ?");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        CharacterIdNameBuddyCapacity ret = null;
        if (rs.next()) {
            ret = new CharacterIdNameBuddyCapacity(rs.getInt("id"), rs.getString("name"), rs.getInt("gm"));
        }
        rs.close();
        ps.close();

        return ret;
    }

    public static final void BuddyOperation(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int mode = slea.readByte();
        final WorldChannelInterface worldInterface = c.getChannelServer().getWorldInterface();
        final BuddyList buddylist = c.getPlayer().getBuddylist();

        if (mode == 1) { // add
            final String addName = slea.readMapleAsciiString();
            final String groupName = slea.readMapleAsciiString();
            addBuddy(addName, groupName, c);
        } else if (mode == 2) { // accept buddy
            int otherCid = slea.readInt();
            if (!buddylist.isFull()) {
                try {
                    final int channel = worldInterface.find(otherCid);
                    String otherName = null;
                    final MapleCharacter otherChar = c.getChannelServer().getPlayerStorage().getCharacterById(otherCid);
                    if (otherChar == null) {
                        Connection con = DatabaseConnection.getConnection();
                        PreparedStatement ps = con.prepareStatement("SELECT name FROM characters WHERE id = ?");
                        ps.setInt(1, otherCid);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            otherName = rs.getString("name");
                        }
                        rs.close();
                        ps.close();
                    } else {
                        otherName = otherChar.getName();
                    }
                    if (otherName != null) {
                        buddylist.put(new BuddylistEntry(otherName, otherCid, channel, true));
                        c.getSession().write(MaplePacketCreator.updateBuddylist(buddylist.getBuddies()));
                        notifyRemoteChannel(c, channel, otherCid, BuddyOperation.ADDED);
                    }
                } catch (RemoteException e) {
                    System.err.println("REMOTE THROW" + e);
                } catch (SQLException e) {
                    System.err.println("SQL THROW" + e);
                }
            }
            nextPendingRequest(c);
        } else if (mode == 3) { // delete
            final int otherCid = slea.readInt();
            if (buddylist.containsVisible(otherCid)) {
                try {
                    notifyRemoteChannel(c, worldInterface.find(otherCid), otherCid, BuddyOperation.DELETED);
                } catch (RemoteException e) {
                    System.err.println("REMOTE THROW" + e);
                }
            }
            buddylist.remove(otherCid);
            c.getSession().write(MaplePacketCreator.updateBuddylist(c.getPlayer().getBuddylist().getBuddies()));
            nextPendingRequest(c);
        }
    }

    public static final void addBuddy(String addName, String groupName, MapleClient c) {
        final BuddyList buddylist = c.getPlayer().getBuddylist();
        final BuddylistEntry ble = buddylist.get(addName);
        final WorldChannelInterface worldInterface = c.getChannelServer().getWorldInterface();

        if (addName.length() > 13 || groupName.length() > 16) {
            return;
        }
        if (ble != null && !ble.isVisible()) {
            c.getSession().write(MaplePacketCreator.buddylistMessage((byte) 13));
        } else if (buddylist.isFull()) {
            c.getSession().write(MaplePacketCreator.buddylistMessage((byte) 11));
        } else {
            try {
                CharacterIdNameBuddyCapacity charWithId = null;
                int channel;
                final MapleCharacter otherChar = c.getChannelServer().getPlayerStorage().getCharacterByName(addName);
                if (otherChar != null) {
                    channel = c.getChannel();
                    charWithId = new CharacterIdNameBuddyCapacity(otherChar.getId(), otherChar.getName(), otherChar.getGMLevel());
                    if (otherChar.isChunin() && !c.getPlayer().canFuck(otherChar)) {
                        c.getSession().write(MaplePacketCreator.buddylistMessage((byte) 15));
                        return;
                    }
                } else {
                    channel = worldInterface.find(addName);
                    charWithId = getCharacterIdAndNameFromDatabase(addName);
                    if (charWithId.gmlvl() > 1 && c.getGMLevel() < charWithId.gmlvl()) {
                        c.getSession().write(MaplePacketCreator.buddylistMessage((byte) 15));
                        return;
                    }
                }

                if (charWithId != null) {
                    BuddyAddResult buddyAddResult = null;
                    if (channel != -1) {
                        final ChannelWorldInterface channelInterface = worldInterface.getChannelInterface(channel);
                        buddyAddResult = channelInterface.requestBuddyAdd(addName, c.getChannel(), c.getPlayer().getId(), c.getPlayer().getName());
                    } else {
                        Connection con = DatabaseConnection.getConnection();
                        PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as buddyCount FROM buddies WHERE characterid = ? AND pending = 0");
                        ps.setInt(1, charWithId.getId());
                        ResultSet rs = ps.executeQuery();

                        if (!rs.next()) {
                            ps.close();
                            rs.close();
                            throw new RuntimeException("Result set expected");
                        } else {
                            int count = rs.getInt("buddyCount");
                            if (count >= 200) {
                                buddyAddResult = BuddyAddResult.BUDDYLIST_FULL;
                            }
                        }
                        rs.close();
                        ps.close();

                        ps = con.prepareStatement("SELECT pending FROM buddies WHERE characterid = ? AND buddyid = ?");
                        ps.setInt(1, charWithId.getId());
                        ps.setInt(2, c.getPlayer().getId());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            buddyAddResult = BuddyAddResult.ALREADY_ON_LIST;
                        }
                        rs.close();
                        ps.close();
                    }
                    if (buddyAddResult == BuddyAddResult.BUDDYLIST_FULL) {
                        c.getSession().write(MaplePacketCreator.buddylistMessage((byte) 12));
                    } else {
                        int displayChannel = -1;
                        int otherCid = charWithId.getId();
                        if (buddyAddResult == BuddyAddResult.ALREADY_ON_LIST && channel != -1) {
                            displayChannel = channel;
                            notifyRemoteChannel(c, channel, otherCid, BuddyOperation.ADDED);
                        } else if (buddyAddResult != BuddyAddResult.ALREADY_ON_LIST && channel == -1) {
                            Connection con = DatabaseConnection.getConnection();
                            PreparedStatement ps = con.prepareStatement("INSERT INTO buddies (`characterid`, `buddyid`, `groupname`, `pending`) VALUES (?, ?, ?, 1)");
                            ps.setInt(1, charWithId.getId());
                            ps.setInt(2, c.getPlayer().getId());
                            ps.setString(3, groupName);
                            ps.executeUpdate();
                            ps.close();
                        }
                        buddylist.put(new BuddylistEntry(charWithId.getName(), groupName, otherCid, displayChannel, true));
                        c.getSession().write(MaplePacketCreator.updateBuddylist(buddylist.getBuddies()));
                    }
                } else {
                    c.getSession().write(MaplePacketCreator.buddylistMessage((byte) 15));
                }
            } catch (RemoteException e) {
                System.err.println("REMOTE THROW" + e);
            } catch (SQLException e) {
                System.err.println("SQL THROW" + e);
            }
        }
    }

    private static final void notifyRemoteChannel(final MapleClient c, final int remoteChannel, final int otherCid, final BuddyOperation operation) throws RemoteException {
        final WorldChannelInterface worldInterface = c.getChannelServer().getWorldInterface();
        final MapleCharacter player = c.getPlayer();

        if (remoteChannel != -1) {
            final ChannelWorldInterface channelInterface = worldInterface.getChannelInterface(remoteChannel);
            channelInterface.buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation);
        }
    }
}
