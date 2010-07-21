/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.server.constants;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.net.login.LoginInformationProvider;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.maps.MapleMapObjectType;

/**
 *
 * @author Owner
 */
public class GameConstants {

    public static final List<MapleMapObjectType> rangedMapobjectTypes = Arrays.asList(
            MapleMapObjectType.ITEM,
            MapleMapObjectType.MONSTER,
            MapleMapObjectType.DOOR,
            MapleMapObjectType.REACTOR,
            MapleMapObjectType.SUMMON,
            MapleMapObjectType.NPC,
            MapleMapObjectType.MIST);

    public static final int maxViewRangeSq() {
        return 800000; // 800 * 800
    }

    public static final boolean isJobFamily(final int baseJob, final int currentJob) {
        return currentJob >= baseJob
                && currentJob / 100 == baseJob / 100;
    }

    public static final boolean isKOC(final int job) {
        return job >= 1000 && job < 2000;
    }

    public static final boolean isAran(final int job) {
        return job >= 2000 && job <= 2112;
    }

    public static final boolean isAdventurer(final int job) {
        return job >= 0 && job < 1000;
    }

    public static final String getJobName(int id) {
        String name = "Beginner";
        switch (id) {
            case 0:
                name = "Beginner";
                break;
            case 200:
                name = "Magician";
                break;
            case 300:
                name = "Bowman";
                break;
            case 400:
                name = "Thief";
                break;
            case 500:
                name = "Pirate";
                break;
            case 100:
                name = "Swordman";
                break;
            case 110:
                name = "Fighter";
                break;
            case 120:
                name = "Page";
                break;
            case 130:
                name = "Spearman";
                break;
            case 210:

                name = "F/P Wizard";
                break;
            case 220:
                name = "I/L Wizard";
                break;
            case 230:
                name = "Cleric";
                break;
            case 310:
                name = "Hunter";
                break;
            case 320:
                name = "Crossbowman";
                break;
            case 410:
                name = "Assassin";
                break;
            case 420:
                name = "Bandit";
                break;
            case 510:
                name = "Brawler";
                break;
            case 520:
                name = "Gunslinger";
                break;
            case 111:
                name = "Crusader";
                break;
            case 121:
                name = "White Knight";
                break;
            case 131:
                name = "Dragon Knight";
                break;
            case 211:
                name = "F/P Mage";
                break;
            case 221:
                name = "I/L Mage";
                break;
            case 231:
                name = "Priest";
                break;
            case 311:
                name = "Ranger";
                break;
            case 321:
                name = "Sniper";
                break;
            case 411:
                name = "Hermit";
                break;
            case 421:
                name = "Chief Bandit";
                break;
            case 511:
                name = "Marauder";
                break;
            case 521:
                name = "Outlaw";
                break;
            case 112:
                name = "Hero";
                break;
            case 122:
                name = "Paladin";
                break;
            case 132:
                name = "Dark Knight";
                break;
            case 212:
                name = "F/P Arch Mage";
                break;
            case 222:
                name = "I/L Arch Mage";
                break;
            case 232:
                name = "Bishop";
                break;
            case 312:
                name = "Bowmaster";
                break;
            case 322:
                name = "Marksman";
                break;
            case 412:
                name = "Night Lord";
                break;
            case 422:
                name = "Shadower";
                break;
            case 512:
                name = "Buccaneer";
                break;
            case 522:
                name = "Corsair";
                break;
            case 900:
                name = "GM";
                break;
            case 910:
                name = "Epic GM";
                break;
            case 1000:
                name = "Noblesse";
                break;
            case 1100:
            case 1110:
            case 1111:
                name = "Dawn Warrior";
                break;
            case 1200:
            case 1210:
            case 1211:
                name = "Blaze Wizard";
                break;
            case 1300:
            case 1310:
            case 1311:
                name = "Wind Archer";
                break;
            case 1400:
            case 1410:
            case 1411:
                name = "Night Walker";
                break;
            case 1500:
            case 1510:
            case 1511:
                name = "Thunder Breaker";
                break;
            default:
                name = "Noobass";
                break;
        }
        return name;
    }

    public static final boolean isIllegialWords(MapleCharacter player, String text) {
        if (player.getReborns() > 3) {
            return false;
        }
        String origitext = text;
        text = text.replaceAll(" ", "").toLowerCase();
        // server sucks
        String[] bannedcomments = {"isserversux", "isserversuc", "isserversuk", "ninjamssux", "nmssux", "ninjamssuc", "nmssuc", "ninjamssuk", "nmssuk"};
        for (int i = 0; i < bannedcomments.length; i++) {
            if (text.contains(bannedcomments[i])) {
                AutobanManager.getInstance().autoban(player.getClient(), "Trying to comment on the server sucking (Text: " + origitext + ")");
                return true;
            }
        }
        // advertisements of maplestory servers...
        String[] advertisingarray = {".com", ".net", ".info", ".org", ".tk", ".weebly", ".freewebs"};
        String[] forgivencontents = {"story.org", "sydneyms", "pokemonms", "farmerstory", ".org/vote"};
        for (int i = 0; i < advertisingarray.length; i++) {
            String[] subarray = {"story", "ms"};
            for (int p = 0; p < subarray.length; p++) {
                if (text.toLowerCase().contains(subarray[p] + advertisingarray[i])) {
                    boolean banhammer = true;
                    for (int z = 0; z < forgivencontents.length; z++) {
                        if (text.contains(forgivencontents[z])) {
                            return false;
                        }
                    }
                    if (banhammer) {
                        AutobanManager.getInstance().autoban(player.getClient(), "Advertisement of other server (Text: " + origitext + ")");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static final boolean isBlockedLegend(String newname) {
        if (LoginInformationProvider.getInstance().isForbiddenName(newname)) {
            return true;
        }
        return false;
    }

    public static final boolean isBlockedGuild(String newname) {
        if (LoginInformationProvider.getInstance().isForbiddenName(newname)) {
            return true;
        }
        return false;
    }

    public static final boolean isBlockedName(String newname) {
        if (LoginInformationProvider.getInstance().isForbiddenName(newname)) {
            return true;
        }
        return false;
    }

    public static final String getDateTime(long time) {
        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat sd = new SimpleDateFormat("MMMMMMMMMMM dd, yyyy 'at' hh:mm:ss a");
        return sd.format(date);
    }

    public static final String getCardinal(int number) {
        while (number > 100) {
            number -= 100;
        }
        if ((number - 1) % 10 == 0 && number != 11) {
            return "st";
        }
        if ((number - 2) % 10 == 0 && number != 12) {
            return "nd";
        }
        if ((number - 3) % 10 == 0 && number != 13) {
            return "rd";
        }
        return "th";
    }

    public static final int getTaxAmount(final int meso) {
        if (meso >= 100000000) {
            return (int) Math.round(0.06 * meso);
        } else if (meso >= 25000000) {
            return (int) Math.round(0.05 * meso);
        } else if (meso >= 10000000) {
            return (int) Math.round(0.04 * meso);
        } else if (meso >= 5000000) {
            return (int) Math.round(0.03 * meso);
        } else if (meso >= 1000000) {
            return (int) Math.round(0.018 * meso);
        } else if (meso >= 100000) {
            return (int) Math.round(0.008 * meso);
        }
        return 0;
    }

    public static final int EntrustedStoreTax(final int meso) {
        if (meso >= 100000000) {
            return (int) Math.round(0.03 * meso);
        } else if (meso >= 25000000) {
            return (int) Math.round(0.025 * meso);
        } else if (meso >= 10000000) {
            return (int) Math.round(0.02 * meso);
        } else if (meso >= 5000000) {
            return (int) Math.round(0.015 * meso);
        } else if (meso >= 1000000) {
            return (int) Math.round(0.009 * meso);
        } else if (meso >= 100000) {
            return (int) Math.round(0.004 * meso);
        }
        return 0;
    }

    public static final boolean isMonsterCard(final int id) {
	return id / 10000 == 238;
    }

    public static final boolean isSpecialCard(final int id) {
	return id / 100 >= 2388;
    }

    public static final int getCardShortId(final int id) {
	return id % 10000;
    }

    public static final int getBookLevel(final int level) {
	return (int) ((5 * level) * (level + 1));
    }

    public final static int[] goldrewards = {
        1402037, 1, // Rigbol Sword
        2290096, 1, // Maple Warrior 20
        2290049, 1, // Genesis 30
        2290041, 1, // Meteo 30
        2290047, 1, // Blizzard 30
        2290095, 1, // Smoke 30
        2290017, 1, // Enrage 30
        2290075, 1, // Snipe 30
        2290085, 1, // Triple Throw 30
        2290116, 1, // Areal Strike
        1302059, 3, // Dragon Carabella
        2049100, 1, // Chaos Scroll
        2340000, 1, // White Scroll
        1092049, 1, // Dragon Kanjar
        1102041, 1, // Pink Cape
        1432018, 3, // Sky Ski
        1022047, 3, // Owl Mask
        3010051, 1, // Chair
        3010020, 1, // Portable meal table
        2040914, 1, // Shield for Weapon Atk

        1432011, 3, // Fair Frozen
        1442020, 3, // HellSlayer
        1382035, 3, // Blue Marine
        1372010, 3, // Dimon Wand
        1332027, 3, // Varkit
        1302056, 3, // Sparta
        1402005, 3, // Bezerker
        1472053, 3, // Red Craven
        1462018, 3, // Casa Crow
        1452017, 3, // Metus
        1422013, 3, // Lemonite
        1322029, 3, // Ruin Hammer
        1412010, 3, // Colonian Axe

        1472051, 1, // Green Dragon Sleeve
        1482013, 1, // Emperor's Claw
        1492013, 1, // Dragon fire Revlover

        1382050, 1, // Blue Dragon Staff
        1382045, 1, // Fire Staff, Level 105
        1382047, 1, // Ice Staff, Level 105
        1382048, 1, // Thunder Staff
        1382046, 1, // Poison Staff

        1332032, 4, // Christmas Tree
        1482025, 3, // Flowery Tube

        4001011, 4, // Lupin Eraser
        4001010, 4, // Mushmom Eraser
        4001009, 4, // Stump Eraser

        2030008, 5, // Bottle, return scroll
        1442012, 4, // Sky Snowboard
        1442018, 3, // Frozen Tuna
        2040900, 4, // Shield for DEF
        2000005, 10, // Power Elixir
        2000004, 10, // Elixir
        4280000, 4}; // Gold Box
    public final static int[] silverrewards = {
        1002452, 3, // Starry Bandana
        1002455, 3, // Starry Bandana
        2290084, 1, // Triple Throw 20
        2290048, 1, // Genesis 20
        2290040, 1, // Meteo 20
        2290046, 1, // Blizzard 20
        2290074, 1, // Sniping 20
        2290064, 1, // Concentration 20
        2290094, 1, // Smoke 20
        2290022, 1, // Berserk 20
        2290056, 1, // Bow Expert 30
        2290066, 1, // xBow Expert 30
        2290020, 1, // Sanc 20
        1102082, 1, // Black Raggdey Cape
        1302049, 1, // Glowing Whip
        2340000, 1, // White Scroll
        1102041, 1, // Pink Cape
        1452019, 2, // White Nisrock
        4001116, 3, // Hexagon Pend
        4001012, 3, // Wraith Eraser
        1022060, 2, // Foxy Racoon Eye

        1432011, 3, // Fair Frozen
        1442020, 3, // HellSlayer
        1382035, 3, // Blue Marine
        1372010, 3, // Dimon Wand
        1332027, 3, // Varkit
        1302056, 3, // Sparta
        1402005, 3, // Bezerker
        1472053, 3, // Red Craven
        1462018, 3, // Casa Crow
        1452017, 3, // Metus
        1422013, 3, // Lemonite
        1322029, 3, // Ruin Hammer
        1412010, 3, // Colonian Axe

        1002587, 3, // Black Wisconsin
        1402044, 1, // Pumpkin lantern
        2101013, 4, // Summoning Showa boss
        1442046, 1, // Super Snowboard
        1422031, 1, // Blue Seal Cushion
        1332054, 3, // Lonzege Dagger
        1012056, 3, // Dog Nose
        1022047, 3, // Owl Mask
        3012002, 1, // Bathtub
        1442012, 3, // Sky snowboard
        1442018, 3, // Frozen Tuna
        1432010, 3, // Omega Spear
        1432036, 1, // Fishing Pole
        2000005, 10, // Power Elixir
        2000004, 10, // Elixir
        4280001, 4}; // Silver Box
    public static final int[] fishingReward = {
        0, 80, // Meso
        1, 60, // EXP
        2022179, 1, // Onyx Apple
        1302021, 5, // Pico Pico Hammer
        1072238, 1, // Voilet Snowshoe
        1072239, 1, // Yellow Snowshoe
        2049100, 1, // Chaos Scroll
        1302000, 3, // Sword
        1442011, 1, // Surfboard
        4000517, 8, // Golden Fish
        4000518, 25, // Golden Fish Egg
        4031627, 2, // White Bait (3cm)
        4031628, 1, // Sailfish (120cm)
        4031630, 1, // Carp (30cm)
        4031631, 1, // Salmon(150cm)
        4031632, 1, // Shovel
        4031633, 2, // Whitebait (3.6cm)
        4031634, 1, // Whitebait (5cm)
        4031635, 1, // Whitebait (6.5cm)
        4031636, 1, // Whitebait (10cm)
        4031637, 2, // Carp (53cm)
        4031638, 2, // Carp (60cm)
        4031639, 1, // Carp (100cm)
        4031640, 1, // Carp (113cm)
        4031641, 2, // Sailfish (128cm)
        4031642, 2, // Sailfish (131cm)
        4031643, 1, // Sailfish (140cm)
        4031644, 1, // Sailfish (148cm)
        4031645, 2, // Salmon (166cm)
        4031646, 2, // Salmon (183cm)
        4031647, 1, // Salmon (227cm)
        4031648, 1, // Salmon (288cm)
        4031629, 1 // Pot
    };
}
