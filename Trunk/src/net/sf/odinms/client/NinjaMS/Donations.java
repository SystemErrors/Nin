/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS;

import net.sf.odinms.client.Buffs.MapleStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.constants.Items;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Angy
 */
public class Donations {
    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */

    public static boolean Gacha(MapleCharacter pl) {
        MapleClient c = pl.getClient();
        int i = 0;
        int type = (int) Math.floor(Math.random() * 20 + 1);
        int chance = (int) Math.floor(Math.random() * 10 + 1);
        int chance1 = (int) Math.floor(Math.random() * 10 + 1);
        int chance2 = (int) Math.floor(Math.random() * 10 + 1);
        switch (type) {
            case 1:
            case 2:
            case 3:
                int mesolar = type;
                mesolar += chance;
                mesolar += chance1;
                mesolar += chance2;
                mesolar *= 20;
                if (mesolar < 250) {
                    mesolar += 250;
                }
                pl.gainItem(Items.currencyType.Sight, mesolar);
                pl.dropMessage("You have gained Tao Of Sights");
                return true;
            case 4:
            case 5:
                pl.gainItem(Items.GachaType.special, 1);
                pl.showMessage("You have gained a special gacha ticket");
                return true;
            case 6:
                pl.gainItem(Items.GachaType.special, 1);
                pl.showMessage("You have gained a special gacha ticket");
                return true;
            case 7:
                int[] scrolls = {2040603, 2044503, 2041024, 2041025, 2044703, 2044603, 2043303, 2040807, 2040806, 2040006, 2040007, 2043103, 2043203, 2043003, 2040506, 2044403, 2040903, 2040709, 2040710, 2040711, 2044303, 2043803, 2040403, 2044103, 2044203, 2044003, 2043703, 2041200, 2049100, 2049000, 2049001, 2049002, 2049003};
                i = (int) Math.floor(Math.random() * scrolls.length);
                MapleInventoryManipulator.addById(c, scrolls[i], (short) 5);
                pl.showMessage("You have gained a scroll (Gm scroll / Chaos scroll / Clean slate scroll)");
                return true;
            case 8:
                int[] rareness = {1302081, 1312037, 1322060, 1402046, 1412033, 1422037, 1442063, 1482023, 1372035, 1372036, 1372037, 1372038, 1372039, 1372040, 1372041, 1372042, 1382045, 1382046, 1382047, 1382048, 1382049, 1382050, 1382051, 1382052, 1382060, 1442068, 1452060};
                pl.showMessage("You have gained a super Rare Weapon");
                i = (int) Math.floor(Math.random() * rareness.length);
                MapleInventoryManipulator.addById(c, rareness[i], (short) 1);
                int fucks = chance + chance1 + chance2;
                pl.showMessage("You have gained " + fucks + " attack pots of each kind");
                MapleInventoryManipulator.addById(c, 2022245, (short) fucks);
                MapleInventoryManipulator.addById(c, 2022179, (short) fucks);
                MapleInventoryManipulator.addById(c, 2022282, (short) fucks);
                return true;
            case 9:
            case 10:
            case 11:
            case 12:
                Donations.gainSummonBag(pl);
                return true;
            case 13:
                pl.addFame(100);
                pl.updateSingleStat(MapleStat.FAME, pl.getFame());
                pl.showMessage("You have gained 100 Fame");
                return true;
            case 14:
                pl.setJqpoints((short)(pl.getJqpoints() + 1));
                pl.showMessage("you have gained a JQ point");
                return true;
            case 15:
                int p = chance > 5 ? 1 : 2;
                pl.setJqpoints((short)(pl.getJqpoints() + p));
                pl.showMessage("you have gained " + p + " JQ Points");
                return true;
            case 16:
                pl.showMessage("You have gained 50 Rebirths");
                for (i = 0; i < 50; i++) {
                    Rebirths.giveRebirth(pl, 50);
                }
                c.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[The Elite NinjaGang] Congratulations " + pl.getName() + " On getting 50 rebirths from Donator Gacha"));
                return true;
            case 17:
            case 18:
            case 19:
                int[] chairs1 = {3010000, 3010001, 3010002, 3010003, 3010004, 3010005};
                int[] chairs2 = {3010006, 3010007, 3010008, 3010009, 3010010, 3010011};
                int[] chairs3 = {3010012, 3010013, 3010014, 3010015, 3010016, 3010017};
                int[] chairs4 = {3010018, 3010019, 3010022, 3010023, 3010024, 3010025};
                int[] chairs5 = {3010026, 3010028, 3010040, 3010041, 3010045, 3010046};
                int[] chairs6 = {3010000, 3010047, 3010057, 3010058, 3010072, 3011000};
                int z;
                switch (chance2) {
                    case 1:
                    case 2:
                        for (z = 0; z < 6; z++) {
                            pl.gainItem(chairs1[i], 1);
                        }
                        break;
                    case 3:
                    case 4:
                        for (z = 0; z < 6; z++) {
                            pl.gainItem(chairs2[i], 1);
                        }
                        break;
                    case 5:
                    case 6:
                        for (z = 0; z < 6; z++) {
                            pl.gainItem(chairs3[i], 1);
                        }
                        break;
                    case 7:
                        for (z = 0; z < 6; z++) {
                            pl.gainItem(chairs4[i], 1);
                        }
                        break;
                    case 8:
                        for (z = 0; z < 6; z++) {
                            pl.gainItem(chairs5[i], 1);
                        }
                        break;
                    default:
                        for (z = 0; z < 6; z++) {
                            pl.gainItem(chairs6[i], 1);
                        }
                }
                pl.showMessage("You have Gained Chairs");
                return true;
            case 20:
                pl.showMessage("You have gained 25 Rebirths");
                for (i = 0; i < 25; i++) {
                    Rebirths.giveRebirth(pl);
                }
                c.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[The Elite NinjaGang] Congratulations " + pl.getName() + " On getting 25 rebirths from Donator Gacha"));
                return true;
            default:
                pl.modifyCSPoints(1, 15000);
                pl.showMessage("You have gained 15000 NX");
                break;
        }
        return true;
    }

    public static void gainSummonBag(MapleCharacter pl) {
        int[] bags = {2100000,// - Black Sack - If you think your level's too low, don't bother opening it.
            2100001,// - Monster Sack 1 - Summons weak monsters of level 10 and under
            2100002,// - Monster Sack 2 - Summons weak monsters between levels 10 and 20
            2100003,// - Monster Sack 3 - Summons mid-lower-leveled monsters between levels 20 and 30
            2100004,// - Monster Sack 4 - Summons mid-level monsters between levels 30 and 40
            2100005,// - Monster Sack 5 - Summons mid-leveled monsters between levels 40 and 50
            2100006,// - Monster Sack 6 - Summons high-leveled monsters between levels 50 and 60
            2100007,// - Monster Sack 7 - Summons high-leveled monsters between levels 60 and 70
            2100008,// - Summoning the Boss - To the old, the pregnant, and the low-leveled : don't even bother...
            2100009,// - Summoning New-Type Balrog - The moment you summon it...you're dead already
            2100010,// - Summoning "Dances with Balrog's Clone" - Summons Dances with Balrog's Clone
            2100011,// - Summoning Grendel the Really Old's Clone - Summons Grendel the Really Old's Clone
            2100012,// - Summoning Athena Pierce's Clone - Summons Athena Pierce's Clone
            2100013,// - Summoning Dark Lord's Clone - Summons Dark Lord's Clone
            2100014,// - Brand New Monster Galore - Bam!
            2100015,// - Summoning Bag of Birds - A bag, which summons blue and pink birds living in Eos Tower
            2100016,// - Different Sack - A sack that summons monsters.
            2100017,// - Alien Sack - A sack full of aliens
            2100018,// - Toy Robot Sack - A sack full of toy robots.
            2100019,// - Toy Trojan Sack - A sack full of toy trojans.
            2100028,// - Summoning Three-Tail Fox - A peculiar summon sack that summons Three-Tail Fox
            2100029,// - Summoning Ghosts - A peculiar summon sack that summons ghosts. No way to tell which one, though...
            2100030,// - Summoning Goblins - A peculiar summon sack that summons goblins. No way to tell which one, though...
            2100033,// - Monster Sack 8 - Summons high-leveled monsters between levels 70 and 80
            2100034,// - Monster Sack 9 - Summons high-leveled monsters between levels 80 and 90
            2100035,// - Monster Sack 10 - Summons high-leveled monsters between levels 90 and 100
            2100036,// - Monster Sack 11 - Summons high-leveled monsters between levels 100 and 110
            2100037,// - Summon Master Monsters 1 - Summon the Event-only Mano & Stumpy.
            2100038,// - Summon Master Monsters 2 - Summon the Event-only Faust, King Clang, Timer, and Dyle.
            2100039,// - Summon Master Monsters 3 - Summon the Event-only Nine-Tailed Fox, Tae Roon, and King Sage Cat.
            2100040,// - Summon Master Monsters 4 - Summon the Event-only Elliza, and Snowman.
            2100060,// - Weird Sack - Summon Halloween Monster!
            2100061,// - Strange Sack - Summon Halloween Monster!!
            2100062,// - Interesting Sack - Summon Halloween Monster!!!
            2100066,// - Summon Slime - A summoning sack to summon 10 Slimes.
            2101000,// - Summoning Mushmom - Summons a Mushmom
            2101001,// - Summoning Crimson Balrog - Summons a Crimson Balrog
            2101002,// - Summoning Werewolf - Summons a Werewolf
            2101003,// - Summoning Yeti & Pepe - Summons one set of Yeti & Pepe
            2101004,// - Summoning Superslime - Summons a Superslime
            2101005,// - Summoning Tauromacis - Summons a Tauromacis
            2101006,// - Summoning Taurospear - Summons a Taurospear
            2101007,// - Summoning Lycanthrope - Summons a Lycanthrope.
            2101008,// - Summoning Dark Yeti & Pepe - Summons a set of Dark Yeti & Pepe.
            2101013,// - Summon Showa Boss - Summons Showa Boss.
            2101014,// - Summon Monsters - Summon particular monsters.
            2101016,// - Summon Toy Robot - Summons toy robots.
            2101020,// - Halloween Monster Sack - Summon Halloween monsters.
            2101021,// - Monster Sack (Jr. Mimick) - Summons 1 Jr. Mimick
            2101025,// - Monster Sack (Slime Red) - Summons 5 Slime Red.
            2101026,// - Monster Sack (Mushmom Blue) - Summons 1 Mushmom Blue.
            2101039,// - Monster Summoning Sack - Monster Summoning Sack
            2101050,// - GM event Sack1 - Summons some monsters. Slime Storm.
            2101051,// - GM event Sack2 - Summons some monsters. Mushroom Boom
            2101052,// - GM event Sack3 - Summons some monsters. Pigs in a Blanket
            2101053,// - GM event Sack4 - Summons some monsters. Eye See You
            2101054,// - GM event Sack5 - Summons some monsters. Alien Armada
            2101055,// - GM event Sack6 - Summons some monsters. Toying Around
            2101056,// - GM event Sack7 - Summons some monsters. Crimson Crash
            2101057,// - Amoria Penalty Monster Sack8 - Summons some monsters.
            2101058,// - Amoria Penalty Monster Sack8 - Summons some monsters.
            2101060,// - Monster Sack (SG CBD) - Summons 9 SG Exclusive monsters.
            2101061,// - Monster Sack (SG Ghost ship) - Summons 4 SG Exclusive monsters.
            2101137,// - Masteria Summoning Bag-Jungle Jam - A mysterious black sack that calls forth monsters from the Krakian Jungle.
            2101138,// - Masteria Summoning Bag-Corrupted Army - A mysterious black sack that calls forth monsters from Crimsonwood Mountain.
            2101139,// - Masteria Summoning Bag-Bosses - A mysterious black sack that calls forth powerful monsters from Masteria. Not for the faint o,//f heart!  Bigfoot will stomp nearly anyone!
            2102000,// - Monster Attack Lvl 1 - Summons 3 Slimes, Pigs, Orange Mushrooms, Bubblings, Octopuses, Green Mushrooms, and Horny Mushroo,//ms each.
            2102001,// - Monster Attack Lvl 2 - Summons 3 Drumming Bunnies, Ligators, Ratz, Star Pixie's, Jr. Wraith's, and Jr. Pepe's each.
            2102002,// - Monster Attack Lvl 3 - Summons 3 Panda Teddy's, King Bloctopuses, Lorangs, Zombie Lupins, Hellies, and Tweeters each.
            2102003,// - Monster Attack Lvl 4 - Summons 3 Toy Trojans, King Block Golems, Wraiths, Cheif Grey's, and Mixed Golems each.
            2102004,// - Monster Attack Lvl 5 - Summons 3 Mushmoms, Red Drakes, Ice Drakes, Master Soul Teddy's, and Dark Yeti's each.
            2102005,// - Monster Attack Lvl 6 - Summons 3 Taurospears, King Blue Goblins, Luinels, Werewolves, and Yeti & Pepes each.
            2102006,// - Monster Attack Lvl 7 - Summons 3 Lycanthropes, Death Teddy's, Gigantic Spirit Vikings, and G. Phantom Watches each.
            2102007,// - Monster Attack Lvl 8 - Summons 5 Bains, 2 Jr. Balrogs, and 1 Crimson Balrog.
            2102008,// - Monster Attack Package 1 - Summons one of each monsters featured in Monster Attack Level 1 ~ Level 3.
            2102009,// - Monster Attack Package 2 - Summons one of each monsters featured in Monster Attack Level 4 ~ Level 7.
        };
        int lol = (int) (Math.random() * bags.length);
        pl.gainItem(bags[lol], 20);
        pl.dropMessage("You have gained 20 Summon bags");
    }
}
