/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
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

/*
 * MapleShop.java
 *
 * Created on 28. November 2007, 17:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.odinms.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Inventory.MaplePet;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.server.constants.InventoryConstants;
import net.sf.odinms.tools.MaplePacketCreator;


/**
 *
 * @author Matze
 */
public class MapleShop {
    private static final Set<Integer> rechargeableItems = new LinkedHashSet<Integer>();
    private int id;
    private int npcId;
    private List<MapleShopItem> items;

    static {
	rechargeableItems.add(2070000);
	rechargeableItems.add(2070001);
	rechargeableItems.add(2070002);
	rechargeableItems.add(2070003);
	rechargeableItems.add(2070004);
	rechargeableItems.add(2070005);
	rechargeableItems.add(2070006);
	rechargeableItems.add(2070007);
	rechargeableItems.add(2070008);
	rechargeableItems.add(2070009);
	rechargeableItems.add(2070010);
	rechargeableItems.add(2070011);
	rechargeableItems.add(2070012);
	rechargeableItems.add(2070013);
//	rechargeableItems.add(2070014); // Doesn't Exist [Devil Rain]
//	rechargeableItems.add(2070015); // Beginner Star
	rechargeableItems.add(2070016);
//	rechargeableItems.add(2070017); // Doesn't Exist
	rechargeableItems.add(2070018); // Balanced Fury
	rechargeableItems.add(2070019); // Magic Throwing Star

	rechargeableItems.add(2330000);
	rechargeableItems.add(2330001);
	rechargeableItems.add(2330002);
	rechargeableItems.add(2330003);
	rechargeableItems.add(2330004);
	rechargeableItems.add(2330005);
//	rechargeableItems.add(2330006); // Beginner Bullet
	rechargeableItems.add(2330007);

	rechargeableItems.add(2331000); // Capsules
	rechargeableItems.add(2332000); // Capsules
    }

    /** Creates a new instance of MapleShop */
    private MapleShop(int id, int npcId) {
	this.id = id;
	this.npcId = npcId;
	items = new LinkedList<MapleShopItem>();
    }

    public void addItem(MapleShopItem item) {
	items.add(item);
    }

    public void sendShop(MapleClient c) {
	c.getPlayer().setShop(this);
	c.getSession().write(MaplePacketCreator.getNPCShop(c, getNpcId(), items));
    }

    public void buy(MapleClient c, int itemId, short quantity) {
	if (quantity <= 0) {
	    AutobanManager.getInstance().addPoints(c, 1000, 0, "Buying " + quantity + " " + itemId);
	    return;
	}
	MapleShopItem item = findById(itemId);
	if (item != null && item.getPrice() > 0) {
	    if (c.getPlayer().getMeso() >= item.getPrice() * quantity) {
		if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
		    if (InventoryConstants.isPet(itemId)) {
			MapleInventoryManipulator.addById(c, itemId, quantity, null, MaplePet.createPet(itemId));
		    } else {
			MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

			if (InventoryConstants.isRechargable(itemId)){
			    quantity = ii.getSlotMax(item.getItemId());
			    c.getPlayer().gainMeso(-(item.getPrice()), false);
			    MapleInventoryManipulator.addById(c, itemId, quantity);
			} else {
			    c.getPlayer().gainMeso(-(item.getPrice() * quantity), false);
			    MapleInventoryManipulator.addById(c, itemId, quantity);
			}
		    }
		} else {
		    c.getPlayer().dropMessage(1, "Your Inventory is full");
		}
		c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 0));
	    }
	}
    }

    public void sell(MapleClient c, MapleInventoryType type, byte slot, short quantity) {
	if (quantity == 0xFFFF || quantity == 0) {
	    quantity = 1;
	}
	IItem item = c.getPlayer().getInventory(type).getItem(slot);

	if (InventoryConstants.isThrowingStar(item.getItemId()) || InventoryConstants.isBullet(item.getItemId())) {
	    quantity = item.getQuantity();
	}
	if (quantity < 0) {
	    AutobanManager.getInstance().addPoints(c, 1000,	0, "Selling " + quantity + " " + item.getItemId() + " (" + type.name() + "/" + slot + ")");
	    return;
	}
	short iQuant = item.getQuantity();
	if (iQuant == 0xFFFF) {
	    iQuant = 1;
	}
	final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
	if (quantity <= iQuant && iQuant > 0) {
	    MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
	    double price;
	    if (InventoryConstants.isThrowingStar(item.getItemId()) || InventoryConstants.isBullet(item.getItemId())) {
		price = ii.getWholePrice(item.getItemId()) / (double) ii.getSlotMax(item.getItemId());
	    } else {
		price = ii.getPrice(item.getItemId());
	    }
	    final int recvMesos = (int) Math.max(Math.ceil(price * quantity), 0);
	    if (price != -1 && recvMesos > 0) {
		c.getPlayer().gainMeso(recvMesos, false);
	    }
	    c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 0x8));
	}
    }

    public void recharge(final MapleClient c, final byte slot) {
	final IItem item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);

	if (item == null || (!InventoryConstants.isThrowingStar(item.getItemId()) && !InventoryConstants.isBullet(item.getItemId()))) {
	    return;
	}
	final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
	short slotMax = ii.getSlotMax(item.getItemId());	
	slotMax += 200;
	if (item.getQuantity() < slotMax) {
	    final int price = (int) Math.round(ii.getPrice(item.getItemId()) * (slotMax - item.getQuantity()));
	    if (c.getPlayer().getMeso() >= price) {
		item.setQuantity(slotMax);
		c.getSession().write(MaplePacketCreator.updateInventorySlot(MapleInventoryType.USE, (Item) item, false));
		c.getPlayer().gainMeso(-price, false, true, false);
		c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 0x8));
	    }
	}
    }

    protected MapleShopItem findById(int itemId) {
	for (MapleShopItem item : items) {
	    if (item.getItemId() == itemId)
		return item;
	}
	return null;
    }

    public static MapleShop createFromDB(int id, boolean isShopId) {
	MapleShop ret = null;
	int shopId;

	try {
	    Connection con = DatabaseConnection.getConnection();
	    PreparedStatement ps = con.prepareStatement(isShopId ? "SELECT * FROM shops WHERE shopid = ?" : "SELECT * FROM shops WHERE npcid = ?");

	    ps.setInt(1, id);
	    ResultSet rs = ps.executeQuery();
	    if (rs.next()) {
		shopId = rs.getInt("shopid");
		ret = new MapleShop(shopId, rs.getInt("npcid"));
		rs.close();
		ps.close();
	    } else {
		rs.close();
		ps.close();
		return null;
	    }
	    ps = con.prepareStatement("SELECT * FROM shopitems WHERE shopid = ? ORDER BY position ASC");
	    ps.setInt(1, shopId);
	    rs = ps.executeQuery();
	    List<Integer> recharges = new ArrayList<Integer>(rechargeableItems);
	    while (rs.next()) {
		if (InventoryConstants.isThrowingStar(rs.getInt("itemid")) || InventoryConstants.isBullet(rs.getInt("itemid"))) {
		    MapleShopItem starItem = new MapleShopItem((short) 1, rs.getInt("itemid"), rs.getInt("price"));
		    ret.addItem(starItem);
		    if (rechargeableItems.contains(starItem.getItemId())) {
			recharges.remove(Integer.valueOf(starItem.getItemId()));
		    }
		} else {
		    ret.addItem(new MapleShopItem((short) 1000, rs.getInt("itemid"), rs.getInt("price")));
		}
	    }
	    for (Integer recharge : recharges) {
		ret.addItem(new MapleShopItem((short) 1000, recharge.intValue(), 0));
	    }
	    rs.close();
	    ps.close();
	} catch (SQLException e) {
	    System.err.println("Could not load shop" + e);
	}
	return ret;
    }

    public int getNpcId() {
	return npcId;
    }

    public int getId() {
	return id;
    }
}