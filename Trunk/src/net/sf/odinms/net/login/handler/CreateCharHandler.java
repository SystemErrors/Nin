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
package net.sf.odinms.net.login.handler;

import net.sf.odinms.client.Enums.MapleSkinColor;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.constants.Items;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class CreateCharHandler extends AbstractMaplePacketHandler {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CreateCharHandler.class);

    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        String name = slea.readMapleAsciiString();
        int face = slea.readInt();
        int hair = slea.readInt();
        int hairColor = slea.readInt();
        int skinColor = slea.readInt();
        int top = slea.readInt();
        int bottom = slea.readInt();
        int shoes = slea.readInt();
        int weapon = slea.readInt();
        int gender = slea.readByte();

        boolean charok = true;
        if (gender == 0) {
            if (face != 20000 && face != 20001 && face != 20002) {
                charok = false;
            }
            if (hair != 30000 && hair != 30020 && hair != 30030) {
                charok = false;
            }
            if (top != 1040002 && top != 1040006 && top != 1040010) {
                charok = false;
            }
            if (bottom != 1060006 && bottom != 1060002) {
                charok = false;
            }
        } else if (gender == 1) {
            if (face != 21000 && face != 21001 && face != 21002) {
                charok = false;
            }
            if (hair != 31000 && hair != 31040 && hair != 31050) {
                charok = false;
            }
            if (top != 1041002 && top != 1041006 && top != 1041010 && top != 1041011) { // Credits Traitor for adding the Cygnus armours
                charok = false;
            }
            if (bottom != 1061002 && bottom != 1061008) {
                charok = false;
            }
        } else {
            charok = false;
        }
        if (skinColor < 0 || skinColor > 3) {
            charok = false;
        }
        if (weapon != 1302000 && weapon != 1322005 && weapon != 1312004) {
            charok = false;
        }
        if (shoes != 1072001 && shoes != 1072005 && shoes != 1072037 && shoes != 1072038) {
            charok = false;
        }
        if (hairColor != 0 && hairColor != 2 && hairColor != 3 && hairColor != 7) {
            charok = false;
        }
        MapleCharacter newchar = MapleCharacter.getDefault(c);  // I have no idea why this moved down. but it looks better. only assign stuff is charok.
        if (charok) {
            newchar.setWorld(c.getWorld());
            newchar.setFace(face);
            newchar.setHair(hair + hairColor);
            newchar.setName(name);
            newchar.setSkinColor(MapleSkinColor.getById(skinColor));
            MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
            IItem eq_top = MapleItemInformationProvider.getInstance().getEquipById(top);
            eq_top.setPosition((byte) -5);
            equip.addFromDB(eq_top);
            IItem eq_bottom = MapleItemInformationProvider.getInstance().getEquipById(bottom);
            eq_bottom.setPosition((byte) -6);
            equip.addFromDB(eq_bottom);
            IItem eq_shoes = MapleItemInformationProvider.getInstance().getEquipById(shoes);
            eq_shoes.setPosition((byte) -7);
            equip.addFromDB(eq_shoes);
            IItem eq_weapon = MapleItemInformationProvider.getInstance().getEquipById(weapon);
            eq_weapon.setPosition((byte) -11);
            equip.addFromDB(eq_weapon);
      /*      IItem noob_cap = MapleItemInformationProvider.getInstance().getEquipById(1002419);
            noob_cap.setPosition((byte) -1);
            equip.addFromDB(noob_cap);
            IItem noob_overall = MapleItemInformationProvider.getInstance().getEquipById(1052170);
            noob_overall.setPosition((byte) -105);
            equip.addFromDB(noob_overall);
            IItem pWeap = MapleItemInformationProvider.getInstance().getEquipById(1702187);
            pWeap.setPosition((byte) -111);
            equip.addFromDB(pWeap);*/
            MapleInventory etc = newchar.getInventory(MapleInventoryType.ETC);
            etc.addItem(new Item(Items.currencyType.Sight, (byte) 0, (short) 10));
            MapleInventory use = newchar.getInventory(MapleInventoryType.USE);
            int[] useitems = {2000005, 2050004, 2022094, 2022179, 2040807, 2060000, 2061000, 2070005, 2330000};
            int[] useitemsquantity = {500, 100, 50, 1, 7, 1000, 1000, 1000, 1000};
            for (int i = 0; i < useitems.length; i++) {
                Item item = new Item(useitems[i], (byte) (i + 1), (short) useitemsquantity[i]);
                if (useitems.length - 4 >= i + 1) {
                }
                use.addItem(item);
            }
            MapleInventory cash = newchar.getInventory(MapleInventoryType.CASH);
            int[] cashitems = {5072000, 5076000};
            int[] cashitemsquantity = {10, 5};
            for (int i = 0; i < cashitems.length; i++) {
                Item item = new Item(cashitems[i], (byte) (i + 1), (short) cashitemsquantity[i]);
                cash.addItem(item);
            }
            MapleInventory equipp = newchar.getInventory(MapleInventoryType.EQUIP);
            int[] equipitems = {1372005, 1082149, 1302007, 1332063, 1432000, 1442000, 1452002, 1462047, 1472000, 1492000, 1482000};
            for (int i = 0; i < equipitems.length; i++) {
                IItem thing = MapleItemInformationProvider.getInstance().getEquipById(equipitems[i]);
                thing.setPosition((byte) (i + 1));
                equipp.addFromDB(thing);
            }
        }
        if (charok && MapleCharacterUtil.canCreateChar(name)) {
            newchar.saveNewToDB();
            c.getSession().write(MaplePacketCreator.addNewCharEntry(newchar, charok));
        } else {
            log.warn(MapleClient.getLogMessage(c, "Trying to create a character with a name: {}", name));
        }
    }
}
