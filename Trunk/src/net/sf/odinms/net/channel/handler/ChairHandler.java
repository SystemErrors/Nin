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

import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Admin
 */
public class ChairHandler {
public static final void UseChair(final int itemId, final MapleClient c, final MapleCharacter chr) {
	final IItem toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);
	if (toUse == null || toUse.getItemId() != itemId) {
	    chr.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(itemId));
	    return;
	}
	if (itemId == 3011000) {
	    for (IItem item : c.getPlayer().getInventory(MapleInventoryType.CASH).list()) {
		//TODO add fishing. 
                if (item.getItemId() == 5340000) {
		//    chr.startFishingTask(false);
		    break;
		} else if (item.getItemId() == 5340001) {
		    chr.startFishingTask(true);
		    break;
		}
	    }
	}
	chr.setChair(itemId);
	chr.getMap().broadcastMessage(chr, MaplePacketCreator.showChair(chr.getId(), itemId), false);
	c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static final void CancelChair(final short id, final MapleClient c, final MapleCharacter chr) {
	if (id == -1) { // Cancel Chair
	    if (chr.getChair() == 3011000) {
		chr.cancelFishingTask();
	    }
	    chr.setChair(0);
	    c.getSession().write(MaplePacketCreator.cancelChair(-1));
	    chr.getMap().broadcastMessage(chr, MaplePacketCreator.showChair(chr.getId(), 0), false);
	} else { // Use In-Map Chair
	    chr.setChair(id);
	    c.getSession().write(MaplePacketCreator.cancelChair(id));
	}
    }
}
