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

import java.util.List;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.net.channel.GuildRankingInfo;
import net.sf.odinms.net.world.guild.MapleGuild;
import net.sf.odinms.net.world.guild.MapleGuildCharacter;
import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Admin
 */
public class GuildPacket {

    public static MaplePacket showGuildInfo(MapleCharacter c) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(0x1A); //signature for showing guild info

	if (c == null) { //show empty guild (used for leaving, expelled)
	    mplew.write(0);
	    return mplew.getPacket();
	}
	MapleGuildCharacter initiator = c.getMGC();
	MapleGuild g = c.getClient().getChannelServer().getGuild(initiator);
	if (g == null) { //failed to read from DB - don't show a guild
	    mplew.write(0);
	    return mplew.getPacket();
	} else {
	    //MapleGuild holds the absolute correct value of guild rank after it is initiated
	    MapleGuildCharacter mgc = g.getMGC(c.getId());
	    c.setGuildRank(mgc.getGuildRank());
	}
	mplew.write(1); //bInGuild
	mplew.writeInt(c.getGuildId()); //not entirely sure about this one
	mplew.writeMapleAsciiString(g.getName());
	for (int i = 1; i <= 5; i++) {
	    mplew.writeMapleAsciiString(g.getRankTitle(i));
	}
	g.addMemberData(mplew);

	mplew.writeInt(g.getCapacity());
	mplew.writeShort(g.getLogoBG());
	mplew.write(g.getLogoBGColor());
	mplew.writeShort(g.getLogo());
	mplew.write(g.getLogoColor());
	mplew.writeMapleAsciiString(g.getNotice());
	mplew.writeInt(g.getGP());
	mplew.writeInt(0);

	return mplew.getPacket();
    }

    public static MaplePacket guildMemberOnline(int gid, int cid, boolean bOnline) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(0x3d);
	mplew.writeInt(gid);
	mplew.writeInt(cid);
	mplew.write(bOnline ? 1 : 0);

	return mplew.getPacket();
    }

    public static MaplePacket guildInvite(int gid, String charName) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(0x05);
	mplew.writeInt(gid);
	mplew.writeMapleAsciiString(charName);

	return mplew.getPacket();
    }

    public static MaplePacket denyGuildInvitation(String charname) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(0x37);
	mplew.writeMapleAsciiString(charname);

	return mplew.getPacket();
    }

    public static MaplePacket genericGuildMessage(byte code) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(code);

	return mplew.getPacket();
    }

    public static MaplePacket newGuildMember(MapleGuildCharacter mgc) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(0x27);
	mplew.writeInt(mgc.getGuildId());
	mplew.writeInt(mgc.getId());
	mplew.writeAsciiString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 13));
	mplew.writeInt(mgc.getJobId());
	mplew.writeInt(mgc.getReborns());
	mplew.writeInt(mgc.getGuildRank()); //should be always 5 but whatevs
	mplew.writeInt(mgc.isOnline() ? 1 : 0); //should always be 1 too
	mplew.writeInt(1); //? could be guild signature, but doesn't seem to matter
	mplew.writeInt(3);

	return mplew.getPacket();
    }

    //someone leaving, mode == 0x2c for leaving, 0x2f for expelled
    public static MaplePacket memberLeft(MapleGuildCharacter mgc, boolean bExpelled) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(bExpelled ? 0x2f : 0x2c);

	mplew.writeInt(mgc.getGuildId());
	mplew.writeInt(mgc.getId());
	mplew.writeMapleAsciiString(mgc.getName());

	return mplew.getPacket();
    }

    public static MaplePacket changeRank(MapleGuildCharacter mgc) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(0x40);
	mplew.writeInt(mgc.getGuildId());
	mplew.writeInt(mgc.getId());
	mplew.write(mgc.getGuildRank());

	return mplew.getPacket();
    }

    public static MaplePacket guildNotice(int gid, String notice) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(0x44);
	mplew.writeInt(gid);
	mplew.writeMapleAsciiString(notice);

	return mplew.getPacket();
    }

    public static MaplePacket guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(0x3C);
	mplew.writeInt(mgc.getGuildId());
	mplew.writeInt(mgc.getId());
	mplew.writeInt(mgc.getReborns());
	mplew.writeInt(mgc.getJobId());

	return mplew.getPacket();
    }

    public static MaplePacket rankTitleChange(int gid, String[] ranks) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(0x3e);
	mplew.writeInt(gid);

	for (String r : ranks) {
	    mplew.writeMapleAsciiString(r);
	}
	return mplew.getPacket();
    }

    public static MaplePacket guildDisband(int gid) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(0x32);
	mplew.writeInt(gid);
	mplew.write(1);

	return mplew.getPacket();
    }

    public static MaplePacket guildEmblemChange(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(0x42);
	mplew.writeInt(gid);
	mplew.writeShort(bg);
	mplew.write(bgcolor);
	mplew.writeShort(logo);
	mplew.write(logocolor);

	return mplew.getPacket();
    }

    public static MaplePacket guildCapacityChange(int gid, int capacity) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(0x3a);
	mplew.writeInt(gid);
	mplew.write(capacity);

	return mplew.getPacket();
    }

     public static MaplePacket showGuildRanks(int npcid, List<GuildRankingInfo> all) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(0x49);
	mplew.writeInt(npcid);
	mplew.writeInt(all.size());

	for (GuildRankingInfo info : all) {
	    mplew.writeMapleAsciiString(info.getName());
	    mplew.writeInt(info.getGP());
	    mplew.writeInt(info.getLogo());
	    mplew.writeInt(info.getLogoBg());
	    mplew.writeInt(info.getLogoBgColor());
	    mplew.writeInt(info.getLogoColor());
	}

	return mplew.getPacket();
    }

    public static MaplePacket updateGP(int gid, int GP) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
	mplew.write(0x48);
	mplew.writeInt(gid);
	mplew.writeInt(GP);

	return mplew.getPacket();
    }
}
