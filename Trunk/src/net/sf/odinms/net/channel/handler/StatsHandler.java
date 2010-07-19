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

import java.util.ArrayList;
import java.util.List;
import net.sf.odinms.client.Buffs.MapleStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.PlayerStats;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class StatsHandler {
    
 public static final void DistributeAP(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
	final List<Pair<MapleStat, Integer>> statupdate = new ArrayList<Pair<MapleStat, Integer>>(2);
	c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true));
	slea.skip(4);

	final PlayerStats stat = chr.getStat();

	if (chr.getRemainingAp() > 0) {
	    switch (slea.readInt()) {
		case 64: // Str
		    if (stat.getStr() >= 32767) {
			return;
		    }
		    stat.setStr(stat.getStr() + 1);
		    statupdate.add(new Pair<MapleStat, Integer>(MapleStat.STR, stat.getStr()));
		    break;
		case 128: // Dex
		    if (stat.getDex() >= 32767) {
			return;
		    }
		    stat.setDex(stat.getDex() + 1);
		    statupdate.add(new Pair<MapleStat, Integer>(MapleStat.DEX, stat.getDex()));
		    break;
		case 256: // Int
		    if (stat.getInt() >= 32767) {
			return;
		    }
		    stat.setInt(stat.getInt() + 1);
		    statupdate.add(new Pair<MapleStat, Integer>(MapleStat.INT, stat.getInt()));
		    break;
		case 512: // Luk
		    if (stat.getLuk() >= 32767) {
			return;
		    }
		    stat.setLuk(stat.getLuk() + 1);
		    statupdate.add(new Pair<MapleStat, Integer>(MapleStat.LUK, stat.getLuk()));
		    break;
		case 2048: // HP		    
		case 8192: // MP
                    c.showMessage("You cannot add AP into HP or MP in NinjaMS");
		default:
		    c.getSession().write(MaplePacketCreator.updatePlayerStats(MaplePacketCreator.EMPTY_STATUPDATE, true));
		    return;
	    }
	    chr.setRemainingAp(chr.getRemainingAp() - 1);
	    statupdate.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, chr.getRemainingAp()));
	    c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true));
	}
    }

    public static final void DistributeSP(final int skillid, final MapleClient c, final MapleCharacter chr) {
	c.showMessage("All your skills are maxxed by default. If your skills are not maxxed, please try to relog.");
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static final void AutoAssignAP(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        c.showMessage("Use @AutoAP instead.");
        c.getSession().write(MaplePacketCreator.enableActions());
    }
}