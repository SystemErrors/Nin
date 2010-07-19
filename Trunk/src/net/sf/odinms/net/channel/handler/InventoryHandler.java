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
import java.util.Collections;
import java.util.Iterator;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class InventoryHandler {

    public static final void itemSort2(SeekableLittleEndianAccessor slea, MapleClient c){
        slea.readInt(); // timestamp
        byte mode = slea.readByte();
        if (mode < 0 || mode > 5) {
            return;
        }
        MapleInventory Inv = c.getPlayer().getInventory(MapleInventoryType.getByType(mode));
        ArrayList<Item> itemarray = new ArrayList<Item>();
        for (Iterator<IItem> it = Inv.iterator(); it.hasNext();) {
            Item item = (Item) it.next();
            itemarray.add((Item) (item.copy()));
        }
        Collections.sort(itemarray);
        for (IItem item : itemarray) {
            MapleInventoryManipulator.removeById(c, MapleInventoryType.getByType(mode), item.getItemId(), item.getQuantity(), false, false);
        }
        for (Item i : itemarray) {
            MapleInventoryManipulator.addFromDrop(c, i, false);
        }
        c.getSession().write(MaplePacketCreator.finishedSort2(mode));
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static final void itemSort(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.readInt(); // timestamp
        byte mode = slea.readByte();
        boolean sorted = false;
        MapleInventoryType pInvType = MapleInventoryType.getByType(mode);
        MapleInventory pInv = c.getPlayer().getInventory(pInvType);
        while (!sorted) {
            byte freeSlot = (byte)pInv.getNextFreeSlot();
            if (freeSlot != -1) {
                byte itemSlot = -1;
                for (int i = freeSlot + 1; i <= 100; i++) {
                    if (pInv.getItem((byte) i) != null) {
                        itemSlot = (byte) i;
                        break;
                    }
                }
                if (itemSlot <= 100 && itemSlot > 0) {
                    MapleInventoryManipulator.move(c, pInvType, itemSlot, freeSlot);
                } else {
                    sorted = true;
                }
            }
        }
        c.getSession().write(MaplePacketCreator.finishedSort(mode));
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static void itemMove(SeekableLittleEndianAccessor slea, MapleClient c){
        slea.skip(4);
	final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
	final byte src = (byte) slea.readShort();
	final byte dst = (byte) slea.readShort();
	final short quantity = slea.readShort();
	if (src < 0 && dst > 0) {
	    MapleInventoryManipulator.unequip(c, src, dst);
	} else if (dst < 0) {
	    MapleInventoryManipulator.equip(c, src, dst);
	} else if (dst == 0) {
	    MapleInventoryManipulator.drop(c, type, src, quantity);
	} else {
	    MapleInventoryManipulator.move(c, type, src, dst);
	}
    }
}
