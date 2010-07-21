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
GNU Affero General Public Licese for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.odinms.tools.Packets;

import java.sql.SQLException;
import java.sql.ResultSet;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.server.CashItemInfo;
import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

public class MTSCSPacket {

    public static MaplePacket warpCS(MapleClient c) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPEN);
        final MapleCharacter chr = c.getPlayer();
        mplew.writeLong(-1);
        PacketHelper.addCharStats(mplew, chr);
        mplew.write(chr.getBuddylist().getCapacity());
        mplew.write(0);
        PacketHelper.addInventoryInfo(mplew, chr);
        PacketHelper.addSkillInfo(mplew, chr);
        PacketHelper.addCoolDownInfo(mplew, chr);
        PacketHelper.addQuestInfo(mplew, chr);
        PacketHelper.addRingInfo(mplew, chr);
        PacketHelper.addRocksInfo(mplew, chr);
//	PacketHelper.addMonsterBookInfo(mplew, c.getPlayer());
//	mplew.writeInt(0); // PQ rank
        mplew.writeZeroBytes(11);
        mplew.write(HexTool.getByteArrayFromHexString("38 00 01 9F 98 00 00 04 00 00 00 02 9F 98 00 00 04 00 00 00 03 9F 98 00 00 04 00 00 00 04 9F 98 00 00 04 00 00 00 05 9F 98 00 00 04 00 00 00 06 9F 98 00 00 04 00 00 00 07 9F 98 00 00 04 00 00 00 08 9F 98 00 00 04 00 00 00 09 9F 98 00 00 04 00 00 00 0A 9F 98 00 00 04 00 00 00 0B 9F 98 00 00 04 00 00 00 0C 9F 98 00 00 04 00 00 00 0D 9F 98 00 00 04 00 00 00 0E 9F 98 00 00 04 00 00 00 0F 9F 98 00 00 04 00 00 00 6F 24 9A 00 00 04 00 00 00 70 24 9A 00 00 04 00 00 00 71 24 9A 00 00 04 00 00 00 72 24 9A 00 00 04 00 00 00 73 24 9A 00 00 08 00 00 FF 74 24 9A 00 00 08 00 00 FF 78 24 9A 00 00 04 00 00 00 79 24 9A 00 00 04 00 00 00 53 2F 31 01 10 00 00 00 0D 54 2F 31 01 00 04 00 00 00 9B 3A 34 01 00 04 00 00 00 9C 3A 34 01 00 04 00 00 00 11 C2 35 01 14 08 00 00 0C 92 09 00 00 01 42 C2 35 01 10 00 00 00 0B 44 C2 35 01 00 04 00 00 00 45 C2 35 01 00 0C 00 00 00 03 46 C2 35 01 00 0C 00 00 00 03 69 48 37 01 14 08 00 00 0C 92 09 00 00 01 8A 48 37 01 00 04 00 00 00 D3 CE 38 01 14 08 00 00 0C 04 06 00 00 01 07 CF 38 01 00 04 00 00 00 AD 55 3A 01 00 04 00 00 00 AE 55 3A 01 00 04 00 00 00 AF 55 3A 01 00 04 00 00 00 98 62 3D 01 06 08 00 00 0C 00 4E 0C 00 00 01 1B 63 3D 01 10 08 00 00 0B 02 1E 63 3D 01 00 04 00 00 00 1F 63 3D 01 00 04 00 00 00 20 63 3D 01 00 04 00 00 00 FF F5 41 01 14 08 00 00 0C 92 09 00 00 01 3A F6 41 01 00 04 00 00 00 3B F6 41 01 00 0C 00 00 00 03 AE C3 C9 01 00 04 00 00 00 47 77 FC 02 00 08 00 00 FF 48 77 FC 02 00 08 00 00 FF 3C FE FD 02 00 0C 00 00 00 FF 40 FE FD 02 00 04 00 00 00 E7 91 02 03 00 0C 00 00 00 03 4E 87 93 03 00 04 00 00 01 DB 1E 2C 04 00 04 00 00 00 DC 1E 2C 04 00 04 00 00 00 00 73 00 65 00 63 00 2E 00 00 00 0D 00 DF 01 0E 04 0E 00 0E 00 DE 01 0E 04 5C 00 00 00 48 00 50 00 20 00 2D 00 32 00 34 00 2C 00 20 00 4D 00 50 00 20 00 2D 00 32 00 34 00 3B 00 20 00 49 00 6D 00 70 00 72 00 6F 00 76 00 65 00 73 00 20 00 67 00 75 00 6E 00 20 00 73 00 70 00 65 00 65 00 64 00 20 00 66 00 6F 00 72 00 20 00 36 00 30 00 20 00 73 00 65 00 63 00 2E 00 01 00 00 00 00 00 00 00 E6 91 02 03 01 00 00 00 00 00 00 00 FB E8 3E 01 01 00 00 00 00 00 00 00 4F 2F 31 01 01 00 00 00 00 00 00 00 75 24 9A 00 01 00 00 00 00 00 00 00 76 24 9A 00 01 00 00 00 01 00 00 00 E6 91 02 03 01 00 00 00 01 00 00 00 FB E8 3E 01 01 00 00 00 01 00 00 00 4F 2F 31 01 01 00 00 00 01 00 00 00 75 24 9A 00 01 00 00 00 01 00 00 00 76 24 9A 00 02 00 00 00 00 00 00 00 E6 91 02 03 02 00 00 00 00 00 00 00 FB E8 3E 01 02 00 00 00 00 00 00 00 4F 2F 31 01 02 00 00 00 00 00 00 00 75 24 9A 00 02 00 00 00 00 00 00 00 76 24 9A 00 02 00 00 00 01 00 00 00 E6 91 02 03 02 00 00 00 01 00 00 00 FB E8 3E 01 02 00 00 00 01 00 00 00 4F 2F 31 01 02 00 00 00 01 00 00 00 75 24 9A 00 02 00 00 00 01 00 00 00 76 24 9A 00 03 00 00 00 00 00 00 00 E6 91 02 03 03 00 00 00 00 00 00 00 FB E8 3E 01 03 00 00 00 00 00 00 00 4F 2F 31 01 03 00 00 00 00 00 00 00 75 24 9A 00 03 00 00 00 00 00 00 00 76 24 9A 00 03 00 00 00 01 00 00 00 E6 91 02 03 03 00 00 00 01 00 00 00 FB E8 3E 01 03 00 00 00 01 00 00 00 4F 2F 31 01 03 00 00 00 01 00 00 00 75 24 9A 00 03 00 00 00 01 00 00 00 76 24 9A 00 04 00 00 00 00 00 00 00 E6 91 02 03 04 00 00 00 00 00 00 00 FB E8 3E 01 04 00 00 00 00 00 00 00 4F 2F 31 01 04 00 00 00 00 00 00 00 75 24 9A 00 04 00 00 00 00 00 00 00 76 24 9A 00 04 00 00 00 01 00 00 00 E6 91 02 03 04 00 00 00 01 00 00 00 FB E8 3E 01 04 00 00 00 01 00 00 00 4F 2F 31 01 04 00 00 00 01 00 00 00 75 24 9A 00 04 00 00 00 01 00 00 00 76 24 9A 00 05 00 00 00 00 00 00 00 E6 91 02 03 05 00 00 00 00 00 00 00 FB E8 3E 01 05 00 00 00 00 00 00 00 4F 2F 31 01 05 00 00 00 00 00 00 00 75 24 9A 00 05 00 00 00 00 00 00 00 76 24 9A 00 05 00 00 00 01 00 00 00 E6 91 02 03 05 00 00 00 01 00 00 00 FB E8 3E 01 05 00 00 00 01 00 00 00 4F 2F 31 01 05 00 00 00 01 00 00 00 75 24 9A 00 05 00 00 00 01 00 00 00 76 24 9A 00 06 00 00 00 00 00 00 00 E6 91 02 03 06 00 00 00 00 00 00 00 FB E8 3E 01 06 00 00 00 00 00 00 00 4F 2F 31 01 06 00 00 00 00 00 00 00 75 24 9A 00 06 00 00 00 00 00 00 00 76 24 9A 00 06 00 00 00 01 00 00 00 E6 91 02 03 06 00 00 00 01 00 00 00 FB E8 3E 01 06 00 00 00 01 00 00 00 4F 2F 31 01 06 00 00 00 01 00 00 00 75 24 9A 00 06 00 00 00 01 00 00 00 76 24 9A 00 07 00 00 00 00 00 00 00 E6 91 02 03 07 00 00 00 00 00 00 00 FB E8 3E 01 07 00 00 00 00 00 00 00 4F 2F 31 01 07 00 00 00 00 00 00 00 75 24 9A 00 07 00 00 00 00 00 00 00 76 24 9A 00 07 00 00 00 01 00 00 00 E6 91 02 03 07 00 00 00 01 00 00 00 FB E8 3E 01 07 00 00 00 01 00 00 00 4F 2F 31 01 07 00 00 00 01 00 00 00 75 24 9A 00 07 00 00 00 01 00 00 00 76 24 9A 00 08 00 00 00 00 00 00 00 E6 91 02 03 08 00 00 00 00 00 00 00 FB E8 3E 01 08 00 00 00 00 00 00 00 4F 2F 31 01 08 00 00 00 00 00 00 00 75 24 9A 00 08 00 00 00 00 00 00 00 76 24 9A 00 08 00 00 00 01 00 00 00 E6 91 02 03 08 00 00 00 01 00 00 00 FB E8 3E 01 08 00 00 00 01 00 00 00 4F 2F 31 01 08 00 00 00 01 00 00 00 75 24 9A 00 08 00 00 00 01 00 00 00 76 24 9A 00 00 00 00 00 00 00 00 41 00 00 00"));
        return mplew.getPacket();
    }

    public static MaplePacket useCharm(byte charmsleft, byte daysleft) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT);
        mplew.write(6);
        mplew.write(1);
        mplew.write(charmsleft);
        mplew.write(daysleft);
        return mplew.getPacket();
    }

    public static MaplePacket itemExpired(int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 1E 00 02 83 C9 51 00

        // 21 00 08 02
        // 50 62 25 00
        // 50 62 25 00
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO);
        mplew.write(2);
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }

    public static MaplePacket ViciousHammer(boolean start, int hammered) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.VICIOUS_HAMMER);
        if (start) {
            mplew.write(49);
            mplew.writeInt(0);
            mplew.writeInt(hammered);
        } else {
            mplew.write(53);
            mplew.writeInt(0);
        }

        return mplew.getPacket();
    }

    public static MaplePacket changePetName(MapleCharacter chr, String newname, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PET_NAMECHANGE);
        mplew.writeInt(chr.getId());
        mplew.write(slot);
        mplew.writeMapleAsciiString(newname);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket showNotes(ResultSet notes, int count) throws SQLException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_NOTES);
        mplew.write(3);
        mplew.write(count);
        for (int i = 0; i < count; i++) {
            mplew.writeInt(notes.getInt("id"));
            mplew.writeMapleAsciiString(notes.getString("from"));
            mplew.writeMapleAsciiString(notes.getString("message"));
            mplew.writeLong(PacketHelper.getKoreanTimestamp(notes.getLong("timestamp")));
            mplew.write(0);
            notes.next();
        }

        return mplew.getPacket();
    }

    public static MaplePacket useChalkboard(final int charid, final String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CHALKBOARD);

        mplew.writeInt(charid);
        if (msg == null) {
            mplew.write(0);
        } else {
            mplew.write(1);
            mplew.writeMapleAsciiString(msg);
        }

        return mplew.getPacket();
    }

    public static MaplePacket getTrockRefresh(MapleCharacter chr, boolean vip, boolean delete) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TROCK_LOCATIONS);
        mplew.write(delete ? 2 : 3);
        mplew.write(vip ? 1 : 0);
        int[] map = chr.getRocks();
        for (int i = 0; i < 10; i++) {
            mplew.writeInt(map[i]);
        }
        return mplew.getPacket();
    }

    public static MaplePacket sendWishList(MapleCharacter chr, boolean update) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION);
        mplew.write(update ? 0x45 : 0x40);
        int[] list = chr.getWishlist();
        for (int i = 0; i < 10; i++) {
            mplew.writeInt(list[i] != -1 ? list[i] : 0);
        }
        return mplew.getPacket();
    }

    public static MaplePacket showNXMapleTokens(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_UPDATE);
        mplew.writeInt(chr.getCSPoints(1)); // Paypal/PayByCash NX
        mplew.writeInt(chr.getCSPoints(2)); // Maple Points
        mplew.writeInt(chr.getCSPoints(4)); // Game Card NX
        return mplew.getPacket();
    }

    public static MaplePacket showBoughtCSItem(MapleClient mapleclient, CashItemInfo cashiteminfo) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION);
        mplew.write(74);
        mplew.write(HexTool.getByteArrayFromHexString("FA 96 C1 00"));
        mplew.writeInt(0);
        mplew.writeInt(mapleclient.getAccID());
        mplew.writeInt(0);
        mplew.writeInt(cashiteminfo.getId());
        mplew.write(HexTool.getByteArrayFromHexString("15 2D 31 01"));
        mplew.writeShort(cashiteminfo.getCount());
        mplew.write(HexTool.getByteArrayFromHexString("00 00 50 4C 40 00 B4 F9 78"));
        mplew.writeInt(0);
        mplew.write(HexTool.getByteArrayFromHexString("C0 1E CC C5 A4 73 CA 01"));
        mplew.writeLong(0);
        return mplew.getPacket();
    }

    public static MaplePacket showBoughtCSQuestItem(short position, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION);
        mplew.writeInt(382);
        mplew.write(0);
        mplew.writeShort(1);
        mplew.write(position);
        mplew.write(0);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static MaplePacket wrongCouponCode() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION);
        mplew.write(0x40);
        mplew.write(0x87);

        return mplew.getPacket();
    }

    public static MaplePacket showCouponRedeemedItem(int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION);
        mplew.writeShort(0x3A);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeShort(1);
        mplew.writeShort(0x1A);
        mplew.writeInt(itemid);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static final MaplePacket enableUse0() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(HexTool.getByteArrayFromHexString("12 00 00 00 00 00 00"));
        return mplew.getPacket();
    }

    public static final MaplePacket enableUse1() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION);
        mplew.write(0x40); //v75
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static final MaplePacket enableUse2() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION);
        mplew.writeShort(0x3e); //v75
        mplew.write(0);
        mplew.writeShort(4);
        mplew.writeShort(3);
        return mplew.getPacket();
    }

    public static final MaplePacket enableUse3() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION);
        mplew.write(0x42); //v75
        mplew.write(new byte[40]);
        return mplew.getPacket();
    }

    public static final MaplePacket enableCSUse4() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 02 01 00 00 00 00 00 00 00 00 00 00 00 00
        mplew.write(HexTool.getByteArrayFromHexString("9F 00 00 00 00 00 00 00 00 00 00 00 00 00"));
        return mplew.getPacket();
    }

}
