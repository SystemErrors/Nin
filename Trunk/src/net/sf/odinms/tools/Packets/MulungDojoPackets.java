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

import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Admin
 */
public class MulungDojoPackets {

    public static MaplePacket Mulung_DojoUp() {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
	mplew.write(0x0A);
	mplew.writeShort(1207); // ???
	mplew.writeMapleAsciiString("pt=5599;min=4;belt=3;tuto=1"); // todo

	return mplew.getPacket();
    }

    public static MaplePacket Mulung_DojoUp2() {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
	mplew.write(7);

	return mplew.getPacket();
    }

    public static MaplePacket Mulung_Pts(int recv, int total) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
	mplew.write(9);
	mplew.writeMapleAsciiString("You have received " + recv + " training points, for the accumulated total of " + total + " training points.");

	return mplew.getPacket();
    }

    public static MaplePacket MulungEnergy(int energy) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	mplew.writeShort(SendPacketOpcode.ENERGY.getValue());
	mplew.writeMapleAsciiString("energy");
	mplew.writeMapleAsciiString(String.valueOf(energy));
	return mplew.getPacket();
    }

}
