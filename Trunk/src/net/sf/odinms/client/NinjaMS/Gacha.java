/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.server.constants.Items;

/**
 *
 * @author Admin
 */
public class Gacha {

    private static int[] items = {1302107,// - Black Crystal Blade
        1382016, // - Pyogo Mushroom
        1382045, // - Elemental Staff 1
        1382046, // - Elemental Staff 2
        1382047, // - Elemental Staff 3
        1382048, // - Elemental Staff 4
        1382049, // - Elemental Staff 5
        1382050, // - Elemental Staff 6
        1382051, // - Elemental Staff 7
        1382052, // - Elemental Staff 8
        1382060, // - Crimson Arcanon
        1442068, // - Crimson Arcglaive
        1452060, // - Crimson Arclancer
        1372035, // - Elemental Wand 1
        1372036, // - Elemental Wand 2
        1372037, // - Elemental Wand 3
        1372038, // - Elemental Wand 4
        1372039, // - Elemental Wand 5
        1372040, // - Elemental Wand 6
        1372041, // - Elemental Wand 7
        1372042, // - Elemental Wand 8
        1302081, // - Timeless Executioners
        1312037, // - Timeless Bardiche
        1322060, // - Timeless Allargando
        1402046, // - Timeless Nibleheim
        1412033, // - Timeless Tabarzin
        1422037, // - Timeless Bellocce
        1442063, // - Timeless Diesra
        1482023, // - Timeless Equinox
        1332073, // - Timeless Pescas
        1332074, // - Timeless Killic
        1372044, // - Timeless Enreal Tear
        1382057, // - Timeless Aeas Hand
        1432047, // - Timeless Alchupiz
        1462050, // - Timeless Black Beauty
        1472068, // - Timeless Lampion
        1492023, // - Timeless Blindness
        1322063, // - Duck tube
        1322064, // - Duck tube
        1402048, // - Raven's Wing
        1402049, // - Night Raven's Wing
        1402050, // - Dawn Raven's Wing
        1402051, // - Dusk Raven's Wing
        1422030, // - Pink Seal Cushion
        1422031, // - Blue Seal Cushion
        1442065, // - Tsunami Wave
        1442066, // - Bullseye Board
        1412040, // - Redner
        1432056, // - Stormshear
        1472072, // - Raven's Claw
        1472073, // - Night Raven's Claw
        1472074, // - Dawn Raven's Claw
        1472075, // - Dusk Raven's Claw
        1462052, // - Raven's Eye
        1462053, // - Night Raven's Eye
        1462054, // - Dawn Raven's Eye
        1462055, // - Dusk Raven's Eye
        1332077, // - Raven's Beak
        1332078, // - Night Raven's Beak
        1332079, // - Dawn Raven's Beak
        1332080, // - Dusk Raven's Beak
        1422030, // - Pink Seal Cushion - (no description)
        1422031, //- Blue Seal Cushion - (no description)
        1432039, // - Fishing Pole - (no description)
        1432046, // - Maplemas Tree - (no description)
        1302073, // - Singapore Flag (Beginner) - (no description)
        1302074, // - Malaysia Flag (Beginner) - (no description)
        1302080, // - Maplemas Lights - (no description)
        1302088, // - Stirge-on-a-String - (no description)
        1302089, // - Stirge-on-a-Rope - (no description)
        1302090, // - Stirge-o-Whip - (no description)
        1302091, // - Stirge Grappler - (no description)
        1302092, //- Swooping Stirge - (no description)
        1302093, // - Frantic Strige - (no description)
        1302094 // - Angry Stirge - (no description)
    };

    private static int[] itemsWithScrolls = {1302107, 2043003, // - Black Crystal Blade
        1382016, 2043803,// - Pyogo Mushroom
        1382045, 2043803,// - Elemental Staff 1
        1382046, 2043803,// - Elemental Staff 2
        1382047, 2043803,// - Elemental Staff 3
        1382048, 2043803,// - Elemental Staff 4
        1382049, 2043803,// - Elemental Staff 5
        1382050, 2043803,// - Elemental Staff 6
        1382051, 2043803,// - Elemental Staff 7
        1382052, 2043803,// - Elemental Staff 8
        1382060, 2043803,// - Crimson Arcanon
        1442068, 2044403,// - Crimson Arcglaive
        1452060, 2044503,// - Crimson Arclancer
        1372035, 2043703,// - Elemental Wand 1
        1372036, 2043703,// - Elemental Wand 2
        1372037, 2043703,// - Elemental Wand 3
        1372038, 2043703,// - Elemental Wand 4
        1372039, 2043703,// - Elemental Wand 5
        1372040, 2043703,// - Elemental Wand 6
        1372041, 2043703,// - Elemental Wand 7
        1372042, 2043703,// - Elemental Wand 8
        1302081, 2043003,// - Timeless Executioners
        1312037, 2043103,// - Timeless Bardiche
        1322060, 2043203,// - Timeless Allargando
        1402046, 2044003,// - Timeless Nibleheim
        1412033, 2044103,// - Timeless Tabarzin
        1422037, 2044203,// - Timeless Bellocce
        1442063, 2044403,// - Timeless Diesra
        1482023, 2044803,// - Timeless Equinox (10%)
        1332073, 2043303,// - Timeless Pescas
        1332074, 2043303,// - Timeless Killic
        1372044, 2043703,// - Timeless Enreal Tear
        1382057, 2043803,// - Timeless Aeas Hand
        1432047, 2044303,// - Timeless Alchupiz
        1462050, 2044603,// - Timeless Black Beauty
        1472068, 2044703,// - Timeless Lampion
        1492023, 2044902,// - Timeless Blindness (10%)
        1322063, 2043203,// - Duck tube
        1322064, 2043203,// - Duck tube
        1402048, 2044003,// - Raven's Wing
        1402049, 2044003,// - Night Raven's Wing
        1402050, 2044003,// - Dawn Raven's Wing
        1402051, 2044003,// - Dusk Raven's Wing
        1422030, 2044203,// - Pink Seal Cushion
        1422031, 2044203,// - Blue Seal Cushion
        1442065, 2044403,// - Tsunami Wave
        1442066, 2044403,// - Bullseye Board
        1412040, 2044103,// - Redner
        1432056, 2044303,// - Stormshear
        1472072, 2044703,// - Raven's Claw
        1472073, 2044703,// - Night Raven's Claw
        1472074, 2044703,// - Dawn Raven's Claw
        1472075, 2044703,// - Dusk Raven's Claw
        1462052, 2044603,// - Raven's Eye
        1462053, 2044603,// - Night Raven's Eye
        1462054, 2044603,// - Dawn Raven's Eye
        1462055, 2044603,// - Dusk Raven's Eye
        1332077, 2043303,// - Raven's Beak
        1332078, 2043303,// - Night Raven's Beak
        1332079, 2043303,// - Dawn Raven's Beak
        1332080, 2043303,// - Dusk Raven's Beak
        1432039, 2044303,// - Fishing Pole - (no description)
        1432046, 2044303,// - Maplemas Tree - (no description)
        1302073, 2043003,// - Singapore Flag (Beginner) - (no description)
        1302074, 2043003,// - Malaysia Flag (Beginner) - (no description)
        1302080, 2043003,// - Maplemas Lights - (no description)
        1302088, 2043003,// - Stirge-on-a-String - (no description)
        1302089, 2043003,// - Stirge-on-a-Rope - (no description)
        1302090, 2043003,// - Stirge-o-Whip - (no description)
        1302091, 2043003,// - Stirge Grappler - (no description)
        1302092, 2043003,//- Swooping Stirge - (no description)
        1302093, 2043003,// - Frantic Strige - (no description)
        1302094, 2043003// - Angry Stirge - (no description)
    };

    public static int itemGacha(MapleCharacter player) {
        if (!player.haveItem(Items.GachaType.regular, 1)) {
            player.dropMessage("You do not have a Gacha ticket");
            return 0;
        } else if (!player.checkSpace(items[0])) {
            player.dropMessage("You do not have inventory space");
            return 9;
        }
        player.gainItem(Items.GachaType.regular, -1);
        int chance = (int) Math.floor(items.length * Math.random());
        player.gainItem(items[chance], 1);
        player.dropMessage("You have gained an Item");
        return items[chance];
    }
    private static int[] scrolls = {
        2340000, // - White scroll
        2049100, // - Chaos Scroll 60%
        2049000,// - Clean Slate Scroll 1%
        2049001,// - Clean Slate Scroll 3%
        2049002,// - Clean Slate Scroll 5%
        2049003,// - Clean Slate Scroll 20%
        // GM scrolls
        2044503, // bow
        2044703, // claw
        2044603, // xbow
        2043303, // dagger
        2044303, // spear
        2044403, // polearm
        2043803, // staff
        2043703, // wand
        2043003, // One handed sword
        2044003, // two handed sword
        2043203, // one handed bw
        2044203, // two handed bw
        2043103, // one handed axe
        2044103, // two handed axe
        2040506, // overall dex
        2040710, // shoes jump
        2040303, // earring int
        2040807, // Gloves Attack
    };

    public static int scrollGacha(MapleCharacter player) {
        if (!player.haveItem(Items.GachaType.regular, 1)) {
            player.dropMessage("You do not have a Gacha ticket");
            return 0;
        } else if (!player.checkSpace(scrolls[0])) {
            player.dropMessage("You do not have inventory space");
            return 9;
        }
        player.gainItem(Items.GachaType.regular, -1);
        int chance = (int) Math.floor(scrolls.length * Math.random());
        player.gainItem(scrolls[chance], 1);
        player.dropMessage("You have gained a Special Scroll");
        return scrolls[chance];
    }

    public static int ChairGacha(MapleCharacter player) {
        if (!player.haveItem(Items.GachaType.regular, 1)) {
            player.dropMessage("You do not have a Gacha ticket");
            return 0;
        } else if (!player.checkSpace(3010000)) {
            player.dropMessage("You do not have inventory space");
            return 9;
        }
        player.gainItem(Items.GachaType.regular, -1);
        int[] chairs = {3010000, 3010001, 3010002, 3010003, 3010004, 3010005, 3010006, 3010007, 3010008, 3010009, 3010010, 3010011,
            3010012, 3010013, 3010014, 3010015, 3010016, 3010017, 3010018, 3010019, 3010022, 3010023, 3010024, 3010025, 3010026,
            3010028, 3010040, 3010041, 3010045, 3010046, 3010047, 3010057, 3010058, 3010072, 3011000};
        int chance = (int) Math.floor(chairs.length * Math.random());
        player.gainItem(chairs[chance], 1);
        player.dropMessage("You have gained a non-expiring chair");
        return chairs[chance];
    }

    public static int mountGacha(MapleCharacter player) {
         if (!player.haveItem(Items.GachaType.regular, 1)) {
            player.dropMessage("You do not have a Gacha ticket");
            return 0;
        } else if (!player.checkSpace(1902002)) {
            player.dropMessage("You do not have inventory space");
            return 9;
        }
        player.gainItem(Items.GachaType.regular, -1);
        int[] mounts = {1902000, 1902001, 1902002, 1912000, 1902008, 1902009, 1902011, 1902012, 1912003, 1912004, 1912007, 1912008, 1902005, 1902006, 1912005};
        int chance = (int) Math.floor(mounts.length * Math.random());
        player.gainItem(mounts[chance], 1);
        player.dropMessage("You have gained a non-expiring Mount");
        return mounts[chance];
    }

    public static int expGacha(MapleCharacter player) {
        if (!player.haveItem(Items.GachaType.regular, 1)) {
            player.dropMessage("You do not have a Gacha ticket");
            return 0;
        }
        player.gainItem(Items.GachaType.regular, -1);
        int chance1 = (int) Math.floor(10 * Math.random());
        int lol;
        if (chance1 < 3) {
            Rebirths.giveRebirth(player, (chance1 + 1));
            return 1;
        } else if (chance1 < 5) {
            while (player.getLevel() < player.getMaxLevel()) {
                player.levelUp();
            }
            player.dropMessage("You have been leveled to max level");
            return 2;
        } else {
            for (int i = 0; i
                    < (chance1 * 10); i++) {
                if (player.getLevel() < 255) {
                    player.levelUp();
                }
            }
            player.dropMessage("You have gained " + (chance1 * 10) + " level");
            return 3;
        }
    }

    public static String specialGacha(MapleCharacter pl) {
        if (!pl.haveItem(Items.GachaType.special, 1)) {
            pl.dropMessage("You do not have a Special Gacha ticket");
            return "You do not have a Special Gacha ticket";
        }
        pl.gainItem(Items.GachaType.special, -1);


        return "Error";
    }
}
