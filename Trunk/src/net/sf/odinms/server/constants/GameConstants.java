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
}
