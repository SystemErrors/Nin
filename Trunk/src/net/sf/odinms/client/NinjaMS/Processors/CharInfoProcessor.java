/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.Processors;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.odinms.client.Status;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Village;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.channel.OliveroMatic.RBRankingInfo;
import net.sf.odinms.net.channel.OliveroMatic.RankingWorker;
import net.sf.odinms.net.channel.OliveroMatic.TaoRankingInfo;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.server.constants.GameConstants;
import net.sf.odinms.server.constants.Rates;

/**
 *
 * @author Owner
 */
public class CharInfoProcessor {

    public static List<String> getNinjaGlare(MapleCharacter other) {
        List<String> sb = new LinkedList<String>();
        if (other != null) {
            sb.add(" Name of the Player : " + other.getName());
            sb.add(" CharacterId :" + other.getId());
            sb.add(" Created on : " + other.getCreateDate());
            if (!other.isJounin()) {
                sb.add("Rebirth Ranking: ");
                sb.add(" Overall Rank : #" + other.getRank());
                sb.add(" Rebirths : " + other.getReborns());
                sb.add("Wealth Ranking: #");
                sb.add(" Overall Rank : #" + other.getTaorank());
                sb.add(" Tao Amount : " + other.getTaoOfSight());
            }
            sb.add("MapleStory Job: " + GameConstants.getJobName(other.getJob()));
            sb.add(" Village: " + Village.getName(other.getVillage()));
            if (other.getPreviousNames() != null) {
                if (other.getPreviousNames().length() < 3) {
                    sb.add(other.getName() + " has also been known as..." + other.getPreviousNames());
                }
            }
            sb.add(" Str : " + other.getStat().getStr()
                    + " Dex : " + other.getStat().getDex()
                    + " Int : " + other.getStat().getInt()
                    + " Luk : " + other.getStat().getLuk()
                    + " Remaining AP : "
                    + other.getRemainingAp() + " StorageAp : " + other.getStorageAp());
            sb.add(" TStr : " + other.getStat().getTotalStr()
                    + " TDex : " + other.getStat().getTotalDex()
                    + " TInt : " + other.getStat().getTotalInt()
                    + " TLuk : " + other.getStat().getTotalLuk()
                    + " WA : " + other.getStat().getTotalWatk());
            sb.add(" MobKilled : " + other.getMobKilled()
                    + " BossKilled : " + other.getBossKilled()
                    + " level : " + other.getLevel()
                    + " Mesos : " + other.getMeso()
                    + " ShurikenItems : " + other.getMaxStatItems());
            sb.add(" PvP Kills : " + other.getPvpKills()
                    + " PvP Deaths : " + other.getPvpDeaths()
                    + " NinjaTensu : " + other.getNinjaTensu()
                    + " DojoPoints : " + other.getDojoPoints()
                    + " BQPoints : " + other.getBossPoints()
                    + " JQFinished : " + other.getJqFinished()
                    + " JQPoints : " + other.getJqPoints());
            sb.add("Rasengan Quest Level : " + other.getRasengan()
                    + "Exp/meso/drop/bossdrop boost :" + other.getExpBoost()
                    + " / " + other.getMesoBoost()
                    + " / " + other.getDropBoost()
                    + " / " + other.getBossDropBoost()
                    + " Total Exp/meso/drop/bossdrop rates : "
                    + +Rates.getExpRate(other)
                    + " / " + Rates.getMesoRate(other)
                    + " / " + Rates.getDropRate(other)
                    + " / " + Rates.getBossDropRate(other));
            sb.add(" This Player is : " + Status.getName(other.getGMLevel())
                    + " Player's Legend : " + other.getLegend() + " Player's GMS mode : " + other.getGMSMode());
            if (other.getDAmount() > 0) {
                sb.add("Donated Amount : " + other.getDAmount() + " Dpoints : " + other.getDPoints());
            }

        }
        return sb;
    }

    public static void getNinjaTop10(MessageCallback mc, String name) {
        mc.dropMessage("--------------------------------------Searching Rank for '" + name + "'--------------------------------------");
        RBRankingInfo rank = RankingWorker.getInstance().getRBRanksByName().get(name);
        if (rank != null) {
            int tocheck = rank.getRank();
            for (int i = tocheck; i < (tocheck + 20); i++) {
                rank = RankingWorker.getInstance().getRBRanks().get(i);
                if (rank == null) {
                    mc.dropMessage("The Ranking for this person is not available. The Rank you entered is invalid");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append(" Rank #" + rank.getRank());
                sb.append(" Name : " + rank.getName());
                sb.append(" Rb : " + rank.getRB());
                sb.append(" Clan : " + rank.getClan());
                sb.append(" MSI : " + rank.getMSI());
                mc.dropMessage(sb.toString());
                mc.dropMessage("--Rankings Last Updated: " + RankingWorker.getInstance().getLastRBRankingUpdateTime() + " (Updates Every 30 Minutes)--");
            }
        } else {
            mc.dropMessage("The Ranking for this person is not available.");
        }
        return;
    }

    public static void getNinjaTop10(MessageCallback mc, int tocheck) {
        mc.dropMessage("--------------------------------------Searching Ranks " + tocheck + " to " + (tocheck + 19) + "--------------------------------------");
        for (int i = tocheck; i < (tocheck + 20); i++) {
            RBRankingInfo rank = RankingWorker.getInstance().getRBRanks().get(i);
            if (rank == null) {
                mc.dropMessage("The Ranking for this person is not available. The Rank you entered is invalid");
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(" Rank #" + rank.getRank());
            sb.append(" Name : " + rank.getName());
            sb.append(" Rb : " + rank.getRB());
            sb.append(" Clan : " + rank.getClan());
            sb.append(" MSI : " + rank.getMSI());
            mc.dropMessage(sb.toString());
        }
        mc.dropMessage("--Rankings Last Updated: " + RankingWorker.getInstance().getLastRBRankingUpdateTime() + " (Updates Every 30 Minutes)--");
        mc.dropMessage("For individual rankings, type @ninjatop10 <player>.");
        mc.dropMessage("For other rankings (shows 20), type @ninjatop10 <number> (i.e. @ninjatop10 23 shows 23-42nd)");
        mc.dropMessage("As a default, just type @ninjatop10 for top 20.");
    }

    public static void getTaoTop10(MessageCallback mc, String name) {
        mc.dropMessage("--------------------------------------Searching Tao Rank for '" + name + "'--------------------------------------");
        TaoRankingInfo rank = RankingWorker.getInstance().getTaoRanksByName().get(name);
        if (rank != null) {
            int tocheck = rank.getRank();
            for (int i = tocheck; i < (tocheck + 20); i++) {
                rank = RankingWorker.getInstance().getTaoRanks().get(i);
                if (rank == null) {
                    mc.dropMessage("The Tao Ranking for this person is not available. The Rank you entered is invalid");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append(" Tao Rank #" + rank.getRank());
                sb.append(" Name : " + rank.getName());
                sb.append(" Taos : " + rank.getTaoCheck());
                sb.append(" Clan : " + rank.getClan());
                sb.append(" RB : " + rank.getRB());
                mc.dropMessage(sb.toString());
                mc.dropMessage("--Rankings Last Updated: " + RankingWorker.getInstance().getLastTaoRankingUpdateTime() + " (Updates Every 30 Minutes)--");
            }
        } else {
            mc.dropMessage("The Ranking for this person is not available.");
        }
        return;
    }

    public static void getTaoTop10(MessageCallback mc, int tocheck) {
        mc.dropMessage("--------------------------------------Searching Tao Ranks " + tocheck + " to " + (tocheck + 19) + "--------------------------------------");
        for (int i = tocheck; i < (tocheck + 20); i++) {
            TaoRankingInfo rank = RankingWorker.getInstance().getTaoRanks().get(i);
            if (rank == null) {
                mc.dropMessage("The Ranking for this person is not available. The Rank you entered is invalid");
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(" Tao Rank #" + rank.getRank());
            sb.append(" Name : " + rank.getName());
            sb.append(" Taos : " + rank.getTaoCheck());
            sb.append(" Clan : " + rank.getClan());
            sb.append(" RB : " + rank.getRB());
            mc.dropMessage(sb.toString());
        }
        mc.dropMessage("--Rankings Last Updated: " + RankingWorker.getInstance().getLastTaoRankingUpdateTime() + " (Updates Every 30 Minutes)--");
        mc.dropMessage("For individual rankings, type @ninjatop10 <player>.");
        mc.dropMessage("For other rankings (shows 20), type @taotop10 <number> (i.e. @taotop10 23 shows 23-42nd)");
        mc.dropMessage("As a default, just type @ninjatop10 for top 20.");
    }

    public static void getNinjaGlare(MessageCallback mc, MapleCharacter other) {
        if (other != null) {
            mc.dropMessage("--------------- Player Name : " + other.getName()
                    + " (CharacterId :" + other.getId() + ")"
                    + " Created on : " + other.getCreateDate());
            if (!other.isJounin()) {
                mc.dropMessage("Rebirth Ranking: "
                        + "Overall Rank : #" + other.getRank()
                        + GameConstants.getCardinal(other.getRank())
                        + " Rebirths : " + other.getReborns());
                mc.dropMessage("Wealth Ranking: #"
                        + "Overall Rank : #" + other.getTaorank()
                        + GameConstants.getCardinal(other.getTaorank())
                        + " Tao Amount : " + other.getTaoOfSight());
            }
            mc.dropMessage("MapleStory Job: "
                    + GameConstants.getJobName(other.getJob()));
            mc.dropMessage(other.getPreviousNames().length() < 3 ? "This player has no alias." : other.getName()
                    + " has also been known as..." + other.getPreviousNames());
            mc.dropMessage(" Str : " + other.getStat().getStr()
                    + " Dex : " + other.getStat().getDex()
                    + " Int : " + other.getStat().getInt()
                    + " Luk : " + other.getStat().getLuk()
                    + " Remaining AP : "
                    + other.getRemainingAp() + " StorageAp : " + other.getStorageAp());
            mc.dropMessage(" TStr : " + other.getStat().getTotalStr()
                    + "; TDex : " + other.getStat().getTotalDex()
                    + "; TInt : " + other.getStat().getTotalInt()
                    + "; TLuk : " + other.getStat().getTotalLuk()
                    + "; WA : " + other.getStat().getTotalWatk());
            mc.dropMessage(" MobKilled : " + other.getMobKilled()
                    + " BossKilled : " + other.getBossKilled()
                    + " level : " + other.getLevel()
                    + " Mesos : " + other.getMeso()
                    + " ShurikenItems : " + other.getMaxStatItems());
            mc.dropMessage(" PvP Kills : " + other.getPvpKills()
                    + " PvP Deaths : " + other.getPvpDeaths()
                    + " NinjaTensu : " + other.getNinjaTensu()
                    + " DojoPoints : " + other.getDojoPoints()
                    + " BQPoints :" + other.getBossPoints()
                    + " JQFinished : " + other.getJqFinished()
                    + " JQPoints : " + other.getJqPoints());
            mc.dropMessage("Rasengan Quest Level : " + other.getRasengan()
                    + "Exp/meso/drop/bossdrop boost :" + other.getExpBoost()
                    + " / " + other.getMesoBoost()
                    + " / " + other.getDropBoost()
                    + " / " + other.getBossDropBoost()
                    + " Total Exp/meso/drop/bossdrop rates : "
                    + +Rates.getExpRate(other)
                    + " / " + Rates.getMesoRate(other)
                    + " / " + Rates.getDropRate(other)
                    + " / " + Rates.getBossDropRate(other));
            mc.dropMessage(" This Player is : " + Status.getName(other.getGMLevel())
                    + " Player's Legend : " + other.getLegend() + " Player's GMS mode : " + other.getGMSMode());
            if (other.getDAmount() > 0) {
                mc.dropMessage("Donated Amount : " + other.getDAmount() + " Dpoints : " + other.getDPoints());
            }
        }
    }

    public static void loadAccountDetails(MapleClient c, String IGN) { // !getcharinfo command credits to Oliver the Slut
        c.showMessage(6, "Processing...");
        Connection con = DatabaseConnection.getConnection();
        try {
            int str = 0, dex = 0, int_ = 0, luk = 0, ap = 0, store = 0,
                    damount = 0, dpoint = 0, level = 0, rasengan = 0, tensu = 0;
            String previousnames = "", macs = "", accountcreatedate = "", accountname = "", gm = "";
            int checkmacs = 0;
            c.showMessage(5, "[Step 1/3] Loading Character Information...");
            String sql = "SELECT c.`str`, c.`dex`, c.`luk`, c.`int`,"
                    + " c.`ap`, c.`storageap`, c.`rasengan`, c.`previousnames`,"
                    + " a.`macs`, a.`name`, a.createdat,"
                    + " a.`gm`, a.`damount`, a.`dpoints`,"
                    + " a.`ninjatensu` FROM characters AS c"
                    + " LEFT JOIN accounts AS a ON"
                    + " c.accountid = a.id WHERE c.name = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, IGN);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                str = rs.getInt("str");
                dex = rs.getInt("dex");
                luk = rs.getInt("luk");
                int_ = rs.getInt("int");
                ap = rs.getInt("ap");
                store = rs.getInt("storageap");
                rasengan = rs.getByte("rasengan");
                previousnames = rs.getString("previousnames");
                if (previousnames == null) {
                    previousnames = "N/A";
                }
                macs = rs.getString("macs");
                accountname = rs.getString("name");
                accountcreatedate = rs.getString("createdat");
                gm = Status.getName(rs.getByte("gm"));
                damount = rs.getInt("damount");
                dpoint = rs.getInt("dpoints");
                tensu = rs.getInt("ninjatensu");
            }
            rs.close();
            ps.close();
            c.showMessage(5, "[Step 2/3] Loading mac Information...");
            ps = con.prepareStatement("SELECT COUNT(*) FROM accounts WHERE macs = ? LIMIT 10");
            ps.setString(1, macs);
            rs = ps.executeQuery();
            while (rs.next()) {
                checkmacs = rs.getInt(1);
            }
            rs.close();
            ps.close();
            c.showMessage(5, "[Step 3/3] Printing Results...");
            c.showMessage(6, "Information of: " + IGN + ":" + "(Acc Creation Date: " + accountcreatedate + ")");

            c.showMessage(6, previousnames.length() < 4 ? "This player has no alias." : IGN + " has also been known as..." + previousnames);
            c.showMessage(6, "Level : " + level + "; Str : " + str + "; Dex : " + dex + "; Int : " + int_ + "; Luk : " + luk);
            c.showMessage(6, "RemainingAp : " + ap + " StorageAp : " + store + "; rasengan : " + rasengan);
            c.showMessage(6, "Account Name: " + accountname + "; Mac Address(es): " + macs + "; Account Created: " + accountcreatedate);
            RBRankingInfo rbrank = RankingWorker.getInstance().getRBRanksByName().get(IGN);
            TaoRankingInfo taorank = RankingWorker.getInstance().getTaoRanksByName().get(IGN);
            if (rbrank != null) {
                c.showMessage(6, "Rebirth Ranking: "
                        + "Overall Rank : #" + rbrank.getRank()
                        + GameConstants.getCardinal(rbrank.getRank())
                        + " Rebirths : " + rbrank.getRB() + " MSI : " + rbrank.getMSI());
            }
            if (taorank != null) {
                c.showMessage(6, "Wealth Ranking: #"
                        + "Overall Rank : #" + taorank.getRank()
                        + GameConstants.getCardinal(taorank.getRank())
                        + " Tao Amount : " + taorank.getTaoCheck());
            }
            c.showMessage(6, "dPoint : " + dpoint + "; damount : " + damount + "; Tensu : " + tensu + "; GM status : " + gm);
            if (checkmacs > 0 && !macs.equalsIgnoreCase("")) {
                ps = con.prepareStatement("SELECT name FROM characters WHERE accountid in (SELECT id FROM accounts WHERE macs = ?) LIMIT 20");
                ps.setString(1, macs);
                rs = ps.executeQuery();
                while (rs.next()) {
                    c.showMessage(5, "This player has a MAC same to another user(s). The IGN is: " + rs.getString(1));
                }
                rs.close();
                ps.close();
            }
            c.showMessage(5, "-----------------------------Command Complete------------------------------");
        } catch (SQLException e) {
            c.showMessage(5, "Error: " + e);
        }
        // search similarities
    }

    public static void seeReason(MapleClient c, String name) {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        int accountid = 0;
        try {
            ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
            }
            accountid = rs.getInt("accountid");
            ps.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e);
            c.showMessage(5, "[SQL Checker] Character not found");
        }
        String banString;
        try {
            ps = con.prepareStatement("SELECT banreason FROM accounts WHERE id = " + accountid);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
            }
            banString = rs.getString("banreason");
            c.showMessage(5, "[SQL Checker] " + banString);
            ps.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e);
            c.showMessage(5, "[SQL Checker] Character not found");
        }
    }

    public static String getNPCNinjaGlare(MapleClient c, String name) {
        try {
            WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(name);
            MapleCharacter player = c.getPlayer();
            if (loc != null) {
                MapleCharacter noob = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(name);
                if (noob != null) {
                    if (player.canFuck(noob)) {
                        return getNPCNinjaGlare(noob);
                    } else {
                        return "The ninja " + name + " is too 1337 for you";
                    }
                } else {
                    return getCharInfoOffline(c, name);
                }
            } else {
                return getCharInfoOffline(c, name);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(CharInfoProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "UNKNOWN ERROR";
    }

    public static String getNPCNinjaGlare(MapleCharacter other) {
        StringBuilder sb = new StringBuilder();
        if (other != null) {
            sb.append(" #d#e [General information] #n\r\n");
            sb.append("\r\n#b Name of the Player : #r" + other.getName());
            sb.append("\r\n #bCreated on : #r" + other.getCreateDate());
            sb.append("\r\n #bCharacterId : #r" + other.getId());
            sb.append("\r\n#b Ninja Rank : #r" + Status.getName(other.getGMLevel()));
            sb.append("\r\n #bVillage: #r" + Village.getName(other.getVillage()));

            sb.append("\r\n\r\n#e#d[STATS]#n\r\n #bWA : #r" + other.getStat().getTotalWatk());
            sb.append("\r\n #bStr : #r" + other.getStat().getStr()
                    + "; #bDex : #r" + other.getStat().getDex()
                    + "; #bInt : #r" + other.getStat().getInt()
                    + "; #bLuk : #r" + other.getStat().getLuk());
            sb.append("\r\n #bTStr : #r" + other.getStat().getTotalStr()
                    + "; #bTDex : #r" + other.getStat().getTotalDex()
                    + "; #bTInt : #r" + other.getStat().getTotalInt()
                    + "; #bTLuk : #r" + other.getStat().getTotalLuk());
            sb.append("\r\n #bRemaining AP : " + other.getRemainingAp()
                    + "; #bStorageAp : #r" + other.getStorageAp());

            sb.append("#b" + getMissionStatus(other.getMission()) + "\r\n");
            if (!other.isJounin()) {
                sb.append("\r\n\r\n#d#e Rebirth Ranking : #n\r\n");
                sb.append(" #bOverall Rank :#r #" + other.getRank());
                sb.append(" #dRebirths : #r" + other.getReborns());
                sb.append("\r\n\r\n #d#eWealth Ranking : #n\r\n");
                sb.append(" #bOverall Rank :#r #" + other.getTaorank());
                sb.append(" #dTao Amount : #r" + other.getTaoOfSight());
            }
            sb.append("\r\n #bMobKilled : #r" + other.getMobKilled()
                    + "; #bBossKilled : #r" + other.getBossKilled());
            sb.append("\r\n #bPvP Kills : #r" + other.getPvpKills()
                    + "; #bPvP Deaths : #r" + other.getPvpDeaths());
            sb.append("\r\n#b Fame : #r" + other.getFame());
            sb.append("\r\n #blevel : #r" + other.getLevel()
                    + "; #bMesos : #r" + other.getMeso());
            sb.append("\r\n #bMapleStory Job: #r" + GameConstants.getJobName(other.getJob()));
            sb.append("\r\n#b Player's Legend : #r" + other.getLegend());
            sb.append("\r\n #bShurikenItems : #r" + other.getMaxStatItems());

            if (other.getPreviousNames() != null) {
                if (other.getPreviousNames().length() > 4) {
                    sb.append("\r\n #d" + other.getName() + " has also been known as..." + other.getPreviousNames());
                }
            }

            sb.append("\r\n #bNinjaTensu : #r" + other.getNinjaTensu()
                    + "\r\n #bDojoPoints : #r" + other.getDojoPoints()
                    + "\r\n #bBQPoints : #r" + other.getBossPoints()
                    + "\r\n #bJQFinished : #r" + other.getJqFinished()
                    + "; #bJQPoints : #r" + other.getJqPoints());
            sb.append("\r\n\r\n#d#e[Rates]#n");
            sb.append("\r\n #bExprate : #r" + other.getExpBoost()
                    + "#b ; Mesorate : #r" + other.getMesoBoost()
                    + "#b\r\n Droprate : #r" + other.getDropBoost()
                    + "#b ; Boss Droprate : #r" + other.getBossDropBoost()
                    + "#b\r\n #b Total Exprate : #r" + Rates.getExpRate(other)
                    + "#b ; Total Mesorate : #r" + Rates.getMesoRate(other)
                    + "#b\r\n Total Droprate : #r" + Rates.getDropRate(other)
                    + "#b ; Total BossDroprate : #r" + Rates.getBossDropRate(other));
            if (other.getGMSMode() == 0) {
                sb.append("\r\n\r\n #b#eThis player is not in GMS mode#n");
            } else {
                sb.append("\r\n\r\n #b#ePlayer's GMS mode : #r" + other.getGMSMode() + "#n");
            }
            sb.append("\r\n\r\n #bRasengan Quest Level : #r" + other.getRasengan());
            if (other.getDAmount() > 0) {
                sb.append("\r\n#b Donated Amount : #r" + other.getDAmount() + "#b Dpoints : #r" + other.getDPoints());
            }
        }
        return sb.toString();
    }

    public static String getMissionStatus(int mis) {
        String s = "\r\n";
        if (mis <= 0) {
            s = " This player is Yet to start any mission";
        } else if (mis < 5) {
            s = " This player has completed " + mis + " Rank D missions";
        } else if (mis == 5) {
            s = " This player has completed all Rank D missions";
        } else if (mis < 10) {
            s = " This player has completed " + (mis - 5) + " Rank C missions";
        } else if (mis == 10) {
            s = " This player has completed all Rank C missions";
        } else if (mis < 15) {
            s = " This player has completed " + (mis - 10) + " Rank B missions";
        } else if (mis == 15) {
            s = " This player has completed all Rank B missions";
        } else if (mis < 20) {
            s = " This player has completed " + (mis - 15) + " Rank A missions";
        } else if (mis == 20) {
            s = " This player has completed all Rank A missions";
        } else if (mis < 25) {
            s = " This player has completed " + (mis - 20) + " Rank S missions";
        } else if (mis == 25) {
            s = " This player has completed All Rank S missions";
        }
        return s;
    }

    public static String getCharInfoOffline(MapleClient c, String name) {
        MapleCharacter player = c.getPlayer();
        StringBuilder sb = new StringBuilder();
        Connection con = DatabaseConnection.getConnection();
        try {
            byte village = 0, gml = 0;
            int str = 0, dex = 0, int_ = 0, luk = 0, ap = 0, store = 0, 
                    damount = 0, dpoint = 0, level = 0, rasengan = 0, tensu = 0,
                    id = 0, accid = 0, mission = 0, bqpoints = 0,
                    dojopoints = 0, jqfinished = 0, jqpoints = 0, rank = 0,
                    rb = 0, taorank = 0, taocheck = 0, msi = 0, mobkilled = 0,
                    bosskilled = 0, pvpkills = 0, pvpdeaths = 0, fame = 0,
                    meso = 0, job = 0, exprate = 0, mesorate = 0, droprate = 0,
                    bossrate = 0;
            String previousnames = "", macs = "", accountcreatedate = "",
                    accountname = "", gm = "", legend = "", maccheck = "";
            int checkmacs = 0;
            String sql = "SELECT c.`str`, c.`dex`, c.`luk`, c.`int`,"
                    + " c.`ap`, c.`storageap`, c.`rasengan`, c.`previousnames`,"
                    + " c.`mission`, c.`id`, c.`accountid`, c.`legend`, c.`pvpkills`,"
                    + " c.`pvpdeaths`, c.`fame`, c.`dojopoints`, c.`bqpoints`, c.`meso`,"
                    + " c.`job`, c.`exprate`, c.`mesorate`, c.`droprate`, c.`bossrate`,"
                    + " c.bosskilled, c.mobkilled, "
                    + " a.`macs`, a.`name`, a.createdat,"
                    + " a.`gm`, a.`damount`, a.`dpoints`,"
                    + " a.`ninjatensu` FROM characters AS c"
                    + " LEFT JOIN accounts AS a ON"
                    + " c.accountid = a.id WHERE c.name = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                mobkilled = rs.getInt("mobkilled");
                bosskilled = rs.getInt("bosskilled");
                dojopoints = rs.getInt("dojopoints");
                legend = rs.getString("legend");
                str = rs.getInt("str");
                dex = rs.getInt("dex");
                luk = rs.getInt("luk");
                int_ = rs.getInt("int");
                ap = rs.getInt("ap");
                store = rs.getInt("storageap");
                rasengan = rs.getByte("rasengan");
                previousnames = rs.getString("previousnames");
                if (previousnames == null) {
                    previousnames = "N/A";
                }
                macs = rs.getString("macs");
                accountname = rs.getString("name");
                accountcreatedate = rs.getString("createdat");
                gml = rs.getByte("gm");
                gm = Status.getName(gml);
                damount = rs.getInt("damount");
                dpoint = rs.getInt("dpoints");
                tensu = rs.getInt("ninjatensu");
                mission = rs.getInt("mission");
                id = rs.getInt("id");
                accid = rs.getInt("accountid");
                exprate = rs.getInt("exprate");
                mesorate = rs.getInt("mesorate");
                droprate = rs.getInt("droprate");
                bossrate = rs.getInt("bossrate");
            }
            rs.close();
            ps.close();
            if (player.getGMLevel() < gml) {
                sb.append("The ninja you want to spy is too 1337 for you");
                return sb.toString();
            }
            if (player.isChunin()) {
                ps = con.prepareStatement("SELECT COUNT(*) FROM accounts WHERE macs = ? LIMIT 10");
                ps.setString(1, macs);
                rs = ps.executeQuery();
                while (rs.next()) {
                    checkmacs = rs.getInt(1);
                }
                rs.close();
                ps.close();
            }
            RBRankingInfo rbrank = RankingWorker.getInstance().getRBRanksByName().get(name);
            TaoRankingInfo taorankk = RankingWorker.getInstance().getTaoRanksByName().get(name);
            if (rbrank != null) {
                rank = rbrank.getRank();
                rb = rbrank.getRB();
                msi = rbrank.getMSI();
            }
            if (taorankk != null) {
                taorank = taorankk.getRank();
                taocheck = taorankk.getTaoCheck();
            }
            if (checkmacs > 0 && !macs.equalsIgnoreCase("") && player.isChunin()) {
                ps = con.prepareStatement("SELECT name FROM characters WHERE accountid in (SELECT id FROM accounts WHERE macs = ?) LIMIT 20");
                ps.setString(1, macs);
                rs = ps.executeQuery();
                maccheck += "This player has a MAC same to another user(s). The IGNs are : ";
                while (rs.next()) {
                    maccheck += rs.getString(1);
                    maccheck += ", ";
                }
                rs.close();
                ps.close();
            }


            sb.append(" #d#e [General information] #n\r\n");
            sb.append("\r\n#b Name of the Player : #r" + name);
            if (gml > 1) {
                sb.append("\r\n #bAccount Name : #r" + accountname);
            }
            sb.append("\r\n #bBecame Ninja on : #r" + accountcreatedate);
            sb.append("\r\n #bCharacter Id : #r" + id);
            if (gml > 1) {
                sb.append("\r\n #bAccount Id : #r" + accid);
            }
            sb.append("\r\n #bNinja Rank : #r" + Status.getName(gml));
            sb.append("\r\n #bVillage: #r" + Village.getName(village));
            sb.append("\r\n\r\n#e#d[STATS]#n");
            sb.append("\r\n #bStr : #r" + str
                    + "; #bDex : #r" + dex
                    + "; #bInt : #r" + int_
                    + "; #bLuk : #r" + luk);
            sb.append("\r\n #bRemaining AP : " + ap
                    + "; #bStorageAp : #r" + store);
            sb.append("#b" + getMissionStatus(mission) + "\r\n");
            if (gml < 3) {
                sb.append("\r\n\r\n#d#e Rebirth Ranking : #n\r\n");
                sb.append(" #bOverall Rank :#r #" + rank);
                sb.append(" #dRebirths : #r" + rb);
                sb.append("\r\n\r\n #d#eWealth Ranking : #n\r\n");
                sb.append(" #bOverall Rank :#r #" + taorank);
                sb.append(" #dTao Amount : #r" + taocheck);
            }
            sb.append("\r\n #bMobKilled : #r" + mobkilled
                    + "; #bBossKilled : #r" + bosskilled);
            sb.append("\r\n #bPvP Kills : #r" + pvpkills
                    + "; #bPvP Deaths : #r" + pvpdeaths);
            sb.append("\r\n#b Fame : #r" + fame);
            sb.append("\r\n #blevel : #r" + level
                    + "; #bMesos : #r" + meso);
            sb.append("\r\n #bMapleStory Job: #r" + GameConstants.getJobName(job));
            sb.append("\r\n#b Player's Legend : #r" + legend);
            sb.append("\r\n #bShurikenItems : #r" + msi);

            if (previousnames != null) {
                if (previousnames.length() > 4) {
                    sb.append("\r\n #d" + name + " has also been known as..." + previousnames);
                }
            }

            sb.append("\r\n #bNinjaTensu : #r" + tensu
                    + "\r\n #bDojoPoints : #r" + dojopoints
                    + "\r\n #bBQPoints : #r" + bqpoints
                    + "\r\n #bJQFinished : #r" + jqfinished
                    + "; #bJQPoints : #r" + jqpoints);
            sb.append("\r\n\r\n#d#e[Rates]#n");
            sb.append("\r\n #bExprate : #r" + exprate
                    + "#b ; Mesorate : #r" + mesorate
                    + "#b\r\n Droprate : #r" + droprate
                    + "#b ; Boss Droprate : #r" + bossrate);
            sb.append("\r\n\r\n #bRasengan Quest Level : #r" + rasengan);
            sb.append("\r\n#b Donated Amount : #r" + damount + "#b Dpoints : #r" + dpoint);
            sb.append("\r\n\r\n" + maccheck);
        } catch (SQLException e) {
            c.showMessage(5, "Error: " + e);
        }
        // search similarities
        return sb.toString();
    }

    public static List<String> getCharInfoOffline(String name) {
        List<String> sb = new LinkedList<String>();
        Connection con = DatabaseConnection.getConnection();
        try {
            byte  village = 0, gml = 0;
            int str = 0, dex = 0, int_ = 0, luk = 0, ap = 0, store = 0,
                    damount = 0, dpoint = 0, level = 0, rasengan = 0, tensu = 0,
                    id = 0, accid = 0, mission = 0, bqpoints = 0,
                    dojopoints = 0, jqfinished = 0, jqpoints = 0, rank = 0,
                    rb = 0, taorank = 0, taocheck = 0, msi = 0, mobkilled = 0,
                    bosskilled = 0, pvpkills = 0, pvpdeaths = 0, fame = 0,
                    meso = 0, job = 0, exprate = 0, mesorate = 0, droprate = 0,
                    bossrate = 0;
            String previousnames = "", macs = "", accountcreatedate = "",
                    accountname = "", gm = "", legend = "", maccheck = "";
            int checkmacs = 0;
            String sql = "SELECT c.`str`, c.`dex`, c.`luk`, c.`int`,"
                    + " c.`ap`, c.`storageap`, c.`rasengan`, c.`previousnames`,"
                    + " c.`mission`, c.`id`, c.`accountid`, c.`legend`, c.`pvpkills`,"
                    + " c.`pvpdeaths`, c.`fame`, c.`dojopoints`, c.`bqpoints`, c.`meso`,"
                    + " c.`job`, c.`exprate`, c.`mesorate`, c.`droprate`, c.`bossrate`,"
                    + " c.bosskilled, c.mobkilled, "
                    + " a.`macs`, a.`name`, a.createdat,"
                    + " a.`gm`, a.`damount`, a.`dpoints`,"
                    + " a.`ninjatensu` FROM characters AS c"
                    + " LEFT JOIN accounts AS a ON"
                    + " c.accountid = a.id WHERE c.name = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                mobkilled = rs.getInt("mobkilled");
                bosskilled = rs.getInt("bosskilled");
                dojopoints = rs.getInt("dojopoints");
                legend = rs.getString("legend");
                str = rs.getInt("str");
                dex = rs.getInt("dex");
                luk = rs.getInt("luk");
                int_ = rs.getInt("int");
                ap = rs.getInt("ap");
                store = rs.getInt("storageap");
                rasengan = rs.getByte("rasengan");
                previousnames = rs.getString("previousnames");
                if (previousnames == null) {
                    previousnames = "N/A";
                }
                macs = rs.getString("macs");
                accountname = rs.getString("name");
                accountcreatedate = rs.getString("createdat");
                gml = rs.getByte("gm");
                gm = Status.getName(gml);
                damount = rs.getInt("damount");
                dpoint = rs.getInt("dpoints");
                tensu = rs.getInt("ninjatensu");
                mission = rs.getInt("mission");
                id = rs.getInt("id");
                accid = rs.getInt("accountid");
                exprate = rs.getInt("exprate");
                mesorate = rs.getInt("mesorate");
                droprate = rs.getInt("droprate");
                bossrate = rs.getInt("bossrate");
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT COUNT(*) FROM accounts WHERE macs = ? LIMIT 10");
            ps.setString(1, macs);
            rs = ps.executeQuery();
            while (rs.next()) {
                checkmacs = rs.getInt(1);
            }
            rs.close();
            ps.close();

            RBRankingInfo rbrank = RankingWorker.getInstance().getRBRanksByName().get(name);
            TaoRankingInfo taorankk = RankingWorker.getInstance().getTaoRanksByName().get(name);
            if (rbrank != null) {
                rank = rbrank.getRank();
                rb = rbrank.getRB();
                msi = rbrank.getMSI();
            }
            if (taorankk != null) {
                taorank = taorankk.getRank();
                taocheck = taorankk.getTaoCheck();
            }
            if (checkmacs > 0 && !macs.equalsIgnoreCase("")) {
                ps = con.prepareStatement("SELECT name FROM characters WHERE accountid in (SELECT id FROM accounts WHERE macs = ?) LIMIT 20");
                ps.setString(1, macs);
                rs = ps.executeQuery();
                maccheck += "This player has a MAC same to another user(s). The IGNs are : ";
                while (rs.next()) {
                    maccheck += rs.getString(1);
                    maccheck += ", ";
                }
                rs.close();
                ps.close();
            }


            sb.add(" #d#e [General information] #n\r\n");
            sb.add("\r\n#b Name of the Player : #r" + name);
            if (gml > 1) {
                sb.add("\r\n #bAccount Name : #r" + accountname);
            }
            sb.add("\r\n #bBecame Ninja on : #r" + accountcreatedate);
            sb.add("\r\n #bCharacter Id : #r" + id);
            if (gml > 1) {
                sb.add("\r\n #bAccount Id : #r" + accid);
            }
            sb.add("\r\n #bNinja Rank : #r" + Status.getName(gml));
            sb.add("\r\n #bVillage: #r" + Village.getName(village));
            sb.add("\r\n\r\n#e#d[STATS]#n");
            sb.add("\r\n #bStr : #r" + str
                    + "; #bDex : #r" + dex
                    + "; #bInt : #r" + int_
                    + "; #bLuk : #r" + luk);
            sb.add("\r\n #bRemaining AP : " + ap
                    + "; #bStorageAp : #r" + store);
            sb.add("#b" + getMissionStatus(mission) + "\r\n");
            if (gml < 3) {
                sb.add("\r\n\r\n#d#e Rebirth Ranking : #n\r\n");
                sb.add(" #bOverall Rank :#r #" + rank);
                sb.add(" #dRebirths : #r" + rb);
                sb.add("\r\n\r\n #d#eWealth Ranking : #n\r\n");
                sb.add(" #bOverall Rank :#r #" + taorank);
                sb.add(" #dTao Amount : #r" + taocheck);
            }
            sb.add("\r\n #bMobKilled : #r" + mobkilled
                    + "; #bBossKilled : #r" + bosskilled);
            sb.add("\r\n #bPvP Kills : #r" + pvpkills
                    + "; #bPvP Deaths : #r" + pvpdeaths);
            sb.add("\r\n#b Fame : #r" + fame);
            sb.add("\r\n #blevel : #r" + level
                    + "; #bMesos : #r" + meso);
            sb.add("\r\n #bMapleStory Job: #r" + GameConstants.getJobName(job));
            sb.add("\r\n#b Player's Legend : #r" + legend);
            sb.add("\r\n #bShurikenItems : #r" + msi);

            if (previousnames != null) {
                if (previousnames.length() > 4) {
                    sb.add("\r\n #d" + name + " has also been known as..." + previousnames);
                }
            }

            sb.add("\r\n #bNinjaTensu : #r" + tensu
                    + "\r\n #bDojoPoints : #r" + dojopoints
                    + "\r\n #bBQPoints : #r" + bqpoints
                    + "\r\n #bJQFinished : #r" + jqfinished
                    + "; #bJQPoints : #r" + jqpoints);
            sb.add("\r\n\r\n#d#e[Rates]#n");
            sb.add("\r\n #bExprate : #r" + exprate
                    + "#b ; Mesorate : #r" + mesorate
                    + "#b\r\n Droprate : #r" + droprate
                    + "#b ; Boss Droprate : #r" + bossrate);
            sb.add("\r\n\r\n #bRasengan Quest Level : #r" + rasengan);
            sb.add("\r\n#b Donated Amount : #r" + damount + "#b Dpoints : #r" + dpoint);
            sb.add("\r\n\r\n" + maccheck);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // search similarities
        return sb;
    }
}
