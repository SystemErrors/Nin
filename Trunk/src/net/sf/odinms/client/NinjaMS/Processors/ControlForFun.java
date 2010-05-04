/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.NinjaMS.Processors;

import java.util.ArrayList;
import java.util.List;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.server.MapleInventoryManipulator;

/**
 *
 * @author Admin
 */
public class ControlForFun {
     public boolean unequipEverything(MapleCharacter pl) {
        MapleInventory equipped = pl.getInventory(MapleInventoryType.EQUIPPED);
        List<Byte> position = new ArrayList<Byte>();
        for (IItem item : equipped.list()) {
            position.add(item.getPosition());
        }
        for (byte pos : position) {
            if (pl.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1) {
                MapleInventoryManipulator.unequip(pl.getClient(), pos, pl.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
            } else {
                pl.showMessage(1, "You do not have enough space to strip all your clothes.");
                return false;
            }
        }
        pl.showMessage(5, "[System] All items have been successfully unequiped!");
        return true;
    }

    public boolean Strip(MapleClient c, MapleCharacter boss) {
        MapleInventory equipped = boss.getInventory(MapleInventoryType.EQUIPPED);
        List<Byte> position = new ArrayList<Byte>();
        for (IItem item : equipped.list()) {
            position.add(item.getPosition());
        }
        for (byte pos : position) {
            if (boss.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1) {
                MapleInventoryManipulator.unequip(boss.getClient(), pos, boss.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
            } else {
                c.showMessage(1, "the noob do not have enough space to strip");
                return false;
            }
        }
        c.showMessage(5, "[Hokage] The noob has been stripped!");
        return true;
    }
}
