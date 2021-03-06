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

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.world.guild.MapleAlliance;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class AllianceHandler {
public static final void AllianceOperatopn(final SeekableLittleEndianAccessor slea, final MapleClient c) {
	final byte mode = slea.readByte();

	final MapleAlliance alliance = new MapleAlliance(c, c.getChannelServer().getGuildSummary(c.getPlayer().getGuildId()).getAllianceId());

	switch (mode) {
	    case 0x01: // show info?
		//c.getSession().write(MaplePacketCreator.showAllianceInfo(c.getPlayer()));
		//c.getSession().write(MaplePacketCreator.showAllianceMembers(c.getPlayer()));
		break;
	    case 0x08: // change titles
		String[] ranks = new String[5];
		for (int i = 0; i < 5; i++) {
		    ranks[i] = slea.readMapleAsciiString();
		}
		alliance.setTitles(ranks);
		break;
	    case 0x0A: // change notice
		String notice = slea.readMapleAsciiString(); // new notice (100 is de max)
		alliance.setNotice(notice);
		break;
	    default:
		System.out.println("Unknown Alliance operation:\r\n" + slea.toString());
		break;
	}
    }
}
