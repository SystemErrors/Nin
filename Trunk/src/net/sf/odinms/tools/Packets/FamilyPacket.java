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
package net.sf.odinms.tools.Packets;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

public class FamilyPacket {

    public static MaplePacket getFamilyData() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOAD_FAMILY);
        mplew.writeInt(11); // Number of events

        mplew.write(0);
        mplew.writeInt(300); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("Transfer to Family Member");
        mplew.writeMapleAsciiString("[Target] Myself\n[Effect] Will be transfered directly to the Map where the family member is located in.");

        mplew.write(1);
        mplew.writeInt(500); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("Summon family member");
        mplew.writeMapleAsciiString("[Target] 1 Family member\n[Effect] Summons one of the family member to the map you are located in.");

        mplew.write(2);
        mplew.writeInt(700); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("1.5 X Drop Rate for Me(15min)");
        mplew.writeMapleAsciiString("[Target] Myself\n[Duration] 15 min\n[Effect]  Drop rate will be #cincreased by 50%#.\nThe effect will be disregarded if overlapped with other drop rate event.");

        mplew.write(3);
        mplew.writeInt(800); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("1.5 X EXP for me(15min)");
        mplew.writeMapleAsciiString("[Target] Myself\n[Duration] 15min\n[Effect] EXP gained from monsters  will be #cincreased by 50%.#\nThe effect will be disregarded if overlapped with other EXP event.");

        mplew.write(4);
        mplew.writeInt(1000); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("Unity of Family(30min)");
        mplew.writeMapleAsciiString("[Condition] 6 juniors online from pedigree\n[Duration] 30min\n[Effect] Drop Rate and EXP gained will be #cincreased by 100%#.\nThe effect will be disregarded if overlapped with other Drop Rate and EXP event.");

        mplew.write(2);
        mplew.writeInt(1200); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("2 X Drop Rate for Me(15min)");
        mplew.writeMapleAsciiString("[Target] Myself\n[Duration] 15min\n[Effect]  Drop rate will be #cincreased by 100%.# \nThe effect will be disregarded if overlapped with other Drop Rate event.");

        mplew.write(3);
        mplew.writeInt(1500); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("2 X EXP event for Me(15min)");
        mplew.writeMapleAsciiString("[Target] Myself\n[Duration] 15min\n[Effect] EXP gained from monsters  will be #cincreased by 100%.#\nThe effect will be disregarded if overlapped with other EXP event.");

        mplew.write(2);
        mplew.writeInt(2000); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("2 X Drop Rate for Me(30min)");
        mplew.writeMapleAsciiString("[Target] Myself\n[Duration] 30min\n[Effect]  drop rate will be #cincreased by 100%.# \nThe effect will be disregarded if overlapped with other Drop Rate event");

        mplew.write(3);
        mplew.writeInt(2500); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("2 X EXP event for Me(30min)");
        mplew.writeMapleAsciiString("[Target] Myself\n[Duration] 30min\n[Effect] EXP gained from monsters  will be #cincreased by 100%.#\nThe effect will be disregarded if overlapped with other EXP event.");

        mplew.write(2);
        mplew.writeInt(4000); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("2 X Drop Rate for Party(30min)");
        mplew.writeMapleAsciiString("[Target] My Party\n[Duration] 30min\n[Effect]  drop rate will be #cincreased by 100%.# \nThe effect will be disregarded if overlapped with other Drop Rate event.");

        mplew.write(3);
        mplew.writeInt(5000); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("2 X EXP event for Party(30min)");
        mplew.writeMapleAsciiString("[Target] My Party\n[Duration] 30min\n[Effect] EXP gained from monsters  will be #cincreased by 100%.#\nThe effect will be disregarded if overlapped with other EXP event.");

        return mplew.getPacket();
    }

    public static MaplePacket sendFamilyMessage() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);
        mplew.writeShort(SendPacketOpcode.FAMILY_MESSAGE);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket getFamilyInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.OPEN_FAMILY); // who cares what header is
       /*mplew.writeInt(chr.getFamily().getReputation()); // cur rep left
        mplew.writeInt(chr.getFamily().getTotalReputation()); // tot rep left
        mplew.writeInt(chr.getFamily().getTodaysRep()); // todays rep
        mplew.writeShort(chr.getFamily().getJuniors()); // juniors added
        mplew.writeShort(chr.getFamily().getTotalJuniors()); // juniors allowed
        mplew.writeShort(0);
        mplew.writeInt(chr.getFamilyId()); // id?
        mplew.writeMapleAsciiString(chr.getFamily().getFamilyName());
        mplew.writeInt(0);
        mplew.writeShort(0);*/
        //TODO : Do this
        return mplew.getPacket();
    }

    public static final MaplePacket useRep(int mode, int type, int erate, int drate, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.USE_FAMILY_REP);
        mplew.write(mode);
        mplew.writeInt(type);
        if (mode < 4) {
            mplew.writeInt(erate);
            mplew.writeInt(drate);
        }
        mplew.write(0);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    //20 00
    //00 00 00 00
    //00 00 00 00 00 00 00 00
    //80 01
    //00 00 28 00
    //8C 93 3E 00
    //40 0D
    //03 00 14 00
    //8C 93 3E 00
    //40 0D 03 00 00 00 00 00 02
    public static final MaplePacket giveBuff() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF);
        mplew.writeInt(0);
        mplew.writeLong(0);

        return null;
    }

    public static final MaplePacket sendFamilyInvite(int playerId, String inviter) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.FAMILY_INVITE);
        mplew.writeInt(playerId);
        mplew.writeMapleAsciiString(inviter);
        return mplew.getPacket();
    }
}
