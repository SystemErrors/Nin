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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Inventory.MaplePet;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.constants.InventoryConstants;
import net.sf.odinms.server.maps.MapleMapItem;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class InventoryHandler {

    public static final void itemSort2(SeekableLittleEndianAccessor slea, MapleClient c) {
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
            byte freeSlot = (byte) pInv.getNextFreeSlot();
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

    public static void itemMove(SeekableLittleEndianAccessor slea, MapleClient c) {
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

    public static final void Pickup_Player(final SeekableLittleEndianAccessor slea, MapleClient c, final MapleCharacter chr) {
        slea.skip(5); // [4] Seems to be tickcount, [1] always 0
        final Point Client_Reportedpos = slea.readPos();
        final MapleMapObject ob = chr.getMap().getMapObject(slea.readInt());

        if (ob == null || ob.getType() != MapleMapObjectType.ITEM) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        final MapleMapItem mapitem = (MapleMapItem) ob;

        if (mapitem.isPickedUp()) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (mapitem.getOwner() != chr.getId() && chr.getMap().getEverlast()) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        final double Distance = Client_Reportedpos.distanceSq(mapitem.getPosition());
        if (Distance > 2500) {
            chr.getCheatTracker().registerOffense(CheatingOffense.ITEMVAC_CLIENT, String.valueOf(Distance));
        } else if (chr.getPosition().distanceSq(mapitem.getPosition()) > 90000.0) {
            chr.getCheatTracker().registerOffense(CheatingOffense.ITEMVAC_SERVER);
        }
        if (mapitem.getMeso() > 0) {
            if (chr.getParty() != null && mapitem.getOwner() == chr.getId()) {
                final List<MapleCharacter> toGive = new LinkedList<MapleCharacter>();

                for (final MapleCharacter m : c.getChannelServer().getPartyMembers(chr.getParty())) { // TODO, store info in MaplePartyCharacter instead
                    if (m != null) {
                        if (m.getMapId() == chr.getMapId()) {
                            toGive.add(m);
                        }
                    }
                }
                for (final MapleCharacter m : toGive) {
                    m.gainMeso(mapitem.getMeso() / toGive.size(), true, true);
                }
            } else {
                chr.gainMeso(mapitem.getMeso(), true, true);
            }
            removeItem(chr, mapitem, ob);
        } else {
            if (useItem(c, mapitem.getItemId())) {
                removeItem(c.getPlayer(), mapitem, ob);
            } else {
                if (MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true)) {
                    removeItem(chr, mapitem, ob);
                }
            }
        }
    }

    public static final void Pickup_Pet(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final MaplePet pet = chr.getPet(chr.getPetIndex(slea.readInt()));
        slea.skip(9); // [4] Zero, [4] Seems to be tickcount, [1] Always zero
        final Point Client_Reportedpos = slea.readPos();
        final MapleMapObject ob = chr.getMap().getMapObject(slea.readInt());
        if (ob == null || pet == null || ob.getType() != MapleMapObjectType.ITEM) {
            return;
        }
        final MapleMapItem mapitem = (MapleMapItem) ob;

        if (mapitem.isPickedUp()) {
            c.getSession().write(MaplePacketCreator.getInventoryFull());
            return;
        }
        if (mapitem.getOwner() != chr.getId() || mapitem.isPlayerDrop()) {
            return;
        }
        final double Distance = Client_Reportedpos.distanceSq(mapitem.getPosition());
        if (Distance > 2500) {
            chr.getCheatTracker().registerOffense(CheatingOffense.PET_ITEMVAC_CLIENT, String.valueOf(Distance));
        } else if (pet.getPos().distanceSq(mapitem.getPosition()) > 90000.0) {
            chr.getCheatTracker().registerOffense(CheatingOffense.PET_ITEMVAC_SERVER);
        }
        if (mapitem.getMeso() > 0) {
            if (chr.getInventory(MapleInventoryType.EQUIPPED).findById(1812000) == null) {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            if (chr.getParty() != null && mapitem.getOwner() == chr.getId()) {
                final List<MapleCharacter> toGive = new LinkedList<MapleCharacter>();

                for (final MapleCharacter m : c.getChannelServer().getPartyMembers(chr.getParty())) { // TODO, store info in MaplePartyCharacter instead
                    if (m != null) {
                        if (m.getMapId() == chr.getMapId()) {
                            toGive.add(m);
                        }
                    }
                }
                for (final MapleCharacter m : toGive) {
                    m.gainMeso(mapitem.getMeso() / toGive.size(), true, true);
                }
            } else {
                chr.gainMeso(mapitem.getMeso(), true, true);
            }
            removeItem(chr, mapitem, ob);
        } else {
            if (useItem(c, mapitem.getItemId())) {
                removeItem(chr, mapitem, ob);
            } else {
                if (MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true)) {
                    removeItem(chr, mapitem, ob);
                }
            }
        }
    }

    private static final boolean useItem(final MapleClient c, final int id) {
        if (InventoryConstants.isUse(id)) { // TO prevent caching of everything, waste of mem
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final byte consumeval = ii.isConsumeOnPickup(id);
            if (consumeval > 0) {
                if (consumeval == 2) {
                    if (c.getPlayer().getParty() != null) {
                        for (final MaplePartyCharacter pc : c.getPlayer().getParty().getMembers()) {
                            final MapleCharacter chr = c.getPlayer().getMap().getCharacterById_InMap(pc.getId());
                            if (chr != null) {
                                ii.getItemEffect(id).applyTo(chr);
                            }
                        }
                    } else {
                        ii.getItemEffect(id).applyTo(c.getPlayer());
                    }
                } else {
                    ii.getItemEffect(id).applyTo(c.getPlayer());
                }
                c.getSession().write(MaplePacketCreator.getShowItemGain(id, (byte) 1));
                return true;
            }
        }
        return false;
    }

    private static final void removeItem(final MapleCharacter chr, final MapleMapItem mapitem, final MapleMapObject ob) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 2, chr.getId()), mapitem.getPosition());
        chr.getMap().removeMapObject(ob);
    }
}
