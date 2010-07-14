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

import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Admin
 */
public class BBSPacket {
public static MaplePacket BBSThreadList(ResultSet rs, int start) throws SQLException {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.BBS_OPERATION.getValue());
	mplew.write(6);

	int threadCount = rs.getRow();

	if (!rs.last()) {
	    mplew.write(0);
	} else if (rs.getInt("localthreadid") == 0) { //has a notice
	    mplew.write(1);
	    addThread(mplew, rs);
	    threadCount--; //one thread didn't count (because it's a notice)
	} else {
	    mplew.write(0);
	}
	if (!rs.absolute(start + 1)) { //seek to the thread before where we start
	    rs.first(); //uh, we're trying to start at a place past possible
	    start = 0;
	}
	mplew.writeInt(threadCount);
	mplew.writeInt(Math.min(10, threadCount - start));

	for (int i = 0; i < Math.min(10, threadCount - start); i++) {
	    addThread(mplew, rs);
	    rs.next();
	}
	return mplew.getPacket();
    }

    private static void addThread(MaplePacketLittleEndianWriter mplew, ResultSet rs) throws SQLException {
	mplew.writeInt(rs.getInt("localthreadid"));
	mplew.writeInt(rs.getInt("postercid"));
	mplew.writeMapleAsciiString(rs.getString("name"));
	mplew.writeLong(PacketHelper.getKoreanTimestamp(rs.getLong("timestamp")));
	mplew.writeInt(rs.getInt("icon"));
	mplew.writeInt(rs.getInt("replycount"));
    }

    public static MaplePacket showThread(int localthreadid, ResultSet threadRS, ResultSet repliesRS) throws SQLException, RuntimeException {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.BBS_OPERATION.getValue());
	mplew.write(7);

	mplew.writeInt(localthreadid);
	mplew.writeInt(threadRS.getInt("postercid"));
	mplew.writeLong(PacketHelper.getKoreanTimestamp(threadRS.getLong("timestamp")));
	mplew.writeMapleAsciiString(threadRS.getString("name"));
	mplew.writeMapleAsciiString(threadRS.getString("startpost"));
	mplew.writeInt(threadRS.getInt("icon"));

	if (repliesRS != null) {
	    int replyCount = threadRS.getInt("replycount");
	    mplew.writeInt(replyCount);

	    int i;
	    for (i = 0; i < replyCount && repliesRS.next(); i++) {
		mplew.writeInt(repliesRS.getInt("replyid"));
		mplew.writeInt(repliesRS.getInt("postercid"));
		mplew.writeLong(PacketHelper.getKoreanTimestamp(repliesRS.getLong("timestamp")));
		mplew.writeMapleAsciiString(repliesRS.getString("content"));
	    }
	    if (i != replyCount || repliesRS.next()) {
		//in the unlikely event that we lost count of replyid
		throw new RuntimeException(String.valueOf(threadRS.getInt("threadid")));
		//we need to fix the database and stop the packet sending
		//or else it'll probably error 38 whoever tries to read it
		//there is ONE case not checked, and that's when the thread
		//has a replycount of 0 and there is one or more replies to the
		//thread in bbs_replies
	    }
	} else {
	    mplew.writeInt(0); //0 replies
	}
	return mplew.getPacket();
    }

}
