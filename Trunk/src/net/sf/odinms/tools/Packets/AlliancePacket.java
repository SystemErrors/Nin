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
import net.sf.odinms.net.world.guild.MapleAlliance;
import net.sf.odinms.net.world.guild.MapleGuild;
import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Admin
 */
public class AlliancePacket {
 public static MaplePacket showAllianceInfo(MapleCharacter chr) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
	mplew.write(0x0C);
	MapleAlliance alliance = chr.getGuild().getAlliance(chr.getClient());
	if (alliance == null) { //show empty alliance (used for leaving, expelled)
	    mplew.write(0);
	    return mplew.getPacket();
	}
	mplew.write(1); //Only happens if you are in an alliance
	mplew.writeInt(alliance.getId());
	mplew.writeMapleAsciiString(alliance.getName()); // alliance name
	for (int i = 0; i < 5; i++) {
	    mplew.writeMapleAsciiString(alliance.getTitles()[i]);
	}
	mplew.write(alliance.getAmountOfGuilds());//ammount of guilds joined
	for (int z = 0; z < 5; z++) {
	    if (alliance.getGuilds().get(z) != null) {
		mplew.writeInt(alliance.getGuilds().get(z).getId());
	    }
	}
	mplew.writeInt(3);//3..
	mplew.writeMapleAsciiString(alliance.getNotice());
	return mplew.getPacket();
    }

    public static MaplePacket createAlliance(String name) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
	mplew.write(0x0F);
	return mplew.getPacket();
    }

    public static MaplePacket showAllianceMembers(MapleCharacter chr) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
	mplew.write(0x0D);
	MapleAlliance az = chr.getGuild().getAlliance(chr.getClient());
	int e = 0;
	for (int u = 0; u < 5; u++) {
	    if (az.getGuilds().get(u) != null) {
		e++;
	    }
	}
	mplew.writeInt(e);//ammount of guilds joined
	chr.setGuildRank(chr.getGuild().getMGC(chr.getId()).getGuildRank());
	for (int i = 0; i < 5; i++) {
	    MapleGuild g = az.getGuilds().get(i);
	    if (g != null) {
		mplew.writeInt(g.getId());
		mplew.writeMapleAsciiString(g.getName());
		for (int a = 1; a <= 5; a++) {
		    mplew.writeMapleAsciiString(g.getRankTitle(a));
		}
		g.addMemberData(mplew);

		mplew.writeInt(g.getCapacity());
		mplew.writeShort(g.getLogoBG());
		mplew.write(g.getLogoBGColor());
		mplew.writeShort(g.getLogo());
		mplew.write(g.getLogoColor());
		mplew.writeMapleAsciiString(g.getNotice());
		mplew.writeInt(g.getGP());
		mplew.write(HexTool.getByteArrayFromHexString("0F 03 00 00"));
	    }
	}
	return mplew.getPacket();
    }

}
