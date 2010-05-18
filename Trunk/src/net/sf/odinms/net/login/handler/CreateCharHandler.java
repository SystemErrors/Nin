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
            int[] etcitems = {Items.currencyType.Sight,
                4006000, // Magic rock
                4006001, // Summon rock
                4290001}; // Bummer Effect
            int[] etcitemsquantity = {25, // Tao
                100, // Magic rock
                100, // Summon rock
                1}; // Bummer Effect
            for (int i = 0; i < etcitems.length; i++) {
                Item item = new Item(etcitems[i], (byte) (i + 1), (short) etcitemsquantity[i]);
                if (etcitems.length - 4 >= i + 1) {
                }
                etc.addItem(item);
            }

            MapleInventory use = newchar.getInventory(MapleInventoryType.USE);
            int[] useitems = {2022015, //mushroom miso ramen
                2022011, // Triangular sushi
                2022121, // Gelt Chocolate
                2050004, // All cure potion
                2070007, // Hwabi throwing stars
                2061004, //diamond arrow for xbow
                2060004, // diamond arrow for bow
                2330001, // split bullet
                2040807}; // GFA GM
            int[] useitemsquantity = {500, // mmr
                250, // triangular Sushi
                50, // Gelt Chocolate
                200, // All cure potion
                1000, // Hwabi throwing Star
                1000, //diamond arrow for xbow
                1000, //diamond arrow for bow
                1000, // split bullet
                7}; // GFA GM
            for (int i = 0; i < useitems.length; i++) {
                Item item = new Item(useitems[i], (byte) (i + 1), (short) useitemsquantity[i]);
                if (useitems.length - 4 >= i + 1) {
                }
                use.addItem(item);
            }

            MapleInventory setup = newchar.getInventory(MapleInventoryType.SETUP);
            setup.addItem(new Item(3010000, (byte) 1, (short) 1));

            MapleInventory cash = newchar.getInventory(MapleInventoryType.CASH);
            int[] cashitems = {5072000, // Super mega
                5076000, // item mega
                5390000, // diablo
                5390001, // Cloud 9
                5390002, //Loveholic
                5121000}; // Fighting spirit (weather)
            int[] cashitemsquantity = {10, // smega
                5, // ismega
                1, // diablo
                1, // Cloud 9
                1, // Loveholic
                5}; // Fighting spirit (weather)
            for (int i = 0; i < cashitems.length; i++) {
                Item item = new Item(cashitems[i], (byte) (i + 1), (short) cashitemsquantity[i]);
                cash.addItem(item);
            }

            MapleInventory equipp = newchar.getInventory(MapleInventoryType.EQUIP);
            if (gender == 0) { // Men
                int[] equipitems = {1002240, // Hajimaki(hat)
                    1050115, // sea hermit robe
                    1082149, // Brown Work glove
                    1702031, // Liu Bei sword
                    1082077, // White bandage
                    1022023, // Crested Eye Patch
                    1010002, // Ninja Mask for Men
                    1071008, // Kimono sandals
                    1332066, // - Razor (DAGGER)
                    1472063, // - Magical Mitten (CLAW)
                    1492000, // - Pistol (GUN)
                    1482000, // - Steel Knuckler (KNUCKLER)
                    1432009, // - Bamboo Spear (SPEAR)
                    1442011, // - Surfboard (POLEARM)
                    1402044, // - Pumpkin Lantern (SWORD)
                    1382015, // - Poison Mushroom (WAND
                    1102061, // - Oxygen Tank (CAPE)
                };
                for (int i = 0; i < equipitems.length; i++) {
                    IItem thing = MapleItemInformationProvider.getInstance().getEquipById(equipitems[i]);
                    thing.setPosition((byte) (i + 1));
                    equipp.addFromDB(thing);
                }
            } else {
                int[] equipitems = {1002240, // - Hajimaki (HAT)
                    1051126, // - Red Chinese Dress
                    1082149, // Brown Work Gloves
                    1022023, // - Crested Eye Patch (EYE ACCESSORY)
                    1011000, // - Ninja Mask for Women (ACCESSORY)
                    1071008, // - Kimono Sandals (SHOES)
                    1082077, // - White Bandage (GLOVES)
                    1702031, // - Liu Bei Sword (WEAPON)
                    1332066, // - Razor (DAGGER)
                    1472063, // - Magical Mitten (CLAW)
                    1492000, // - Pistol (GUN)
                    1482000, // - Steel Knuckler (KNUCKLER)
                    1432009, // - Bamboo Spear (SPEAR)
                    1442011, // - Surfboard (POLEARM)
                    1402044, // - Pumpkin Lantern (SWORD)
                    1382015, // - Poison Mushroom (WAND
                    1102061, // - Oxygen Tank (CAPE)
                };
                for (int i = 0; i < equipitems.length; i++) {
                    IItem thing = MapleItemInformationProvider.getInstance().getEquipById(equipitems[i]);
                    thing.setPosition((byte) (i + 1));
                    equipp.addFromDB(thing);
                }
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
