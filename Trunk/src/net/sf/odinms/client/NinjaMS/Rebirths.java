/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS;

import java.util.ArrayList;
import java.util.List;
import net.sf.odinms.client.Enums.MapleJob;
import net.sf.odinms.client.Enums.MapleStat;
import net.sf.odinms.client.Enums.Village;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.NinjaMS.Processors.NoticeProcessor;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;

/**
 *
 * @author Admin
 */
public class Rebirths {

    public static void doReborn(MapleCharacter pl, boolean changejob) {
        if (pl.getMapId() < 4) {
            pl.dropMessage("You cannot rebirth from training camp");
            return;
        } else if (pl.getVillage().equals(Village.UNDECIDED)) {
            pl.dropMessage("You need to be in a Village to rebirth.");
            return;
        } else if (pl.getLevel() < pl.getMaxLevel()) {
            pl.dropMessage("You need atleast " + pl.getMaxLevel() + " to rebirth");
            return;
        } else if (!reqMissionComplete(pl)) {
            pl.dropMessage("You cannot rebirth with out completing your mission");
            return;
        }
        missionReminder(pl);
        pl.addReborn();
        pl.setLevel(1);
        pl.setExp(1);
        pl.setHp(1);
        pl.setMp(1);
        if (changejob) {
            if (pl.getPath() != 2) {
                pl.changeJob(MapleJob.BEGINNER);
            } else {
                pl.changeJob(MapleJob.NOBLESSE);
            }
        }
        pl.dispel();
        List<Pair<MapleStat, Integer>> statup = new ArrayList<Pair<MapleStat, Integer>>(8);
        statup.add(new Pair<MapleStat, Integer>(MapleStat.EXP, 0));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.LEVEL, 1));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.HP, 1));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MP, 1));
        pl.getClient().getSession().write(MaplePacketCreator.updatePlayerStats(statup));
        int reborn = pl.getReborns();
        String name = pl.getName();
        if ((reborn % 25) == 0) {
            NoticeProcessor.sendBlueNotice("[Notice] Congratulations " + name + " on " + reborn + " rebirths!");
        }
        pl.guildUpdate();
        if (reborn > 1000 && !pl.haveItem(1142069, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(pl.getClient(), 1142069, name, (short) 1337, (short) 50, (short) 50);
            pl.dropMessage("[The Elite ninja Gang] You have gained a Captain Knight Medal for reaching 1000 Rebirths");
        } else if (reborn >= 500 && !pl.haveItem(1142068, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(pl.getClient(), 1142068, name, (short) 500, (short) 25, (short) 25);
            pl.dropMessage("[The Elite ninja Gang] You have gained an Advanced Knight Medal for reaching 500 Rebirths");
        } else if (reborn >= 100 && !pl.haveItem(1142067, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(pl.getClient(), 1142067, name, (short) 250, (short) 10, (short) 10);
            pl.dropMessage("[The Elite ninja Gang] You have gained an Official Knight Medal for reaching 100 Rebirths");
        } else if (reborn >= 10 && !pl.haveItem(1142066, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(pl.getClient(), 1142066, name, (short) 50, (short) 5, (short) 5);
            pl.dropMessage("[The Elite ninja Gang] You have gained an Training Knight Medal for reaching 10 Rebirths");
        }
        if ((reborn % 25) == 0) {
            pl.goHome();
        }
    }

    public static void RebornTimer(final MapleCharacter pl, final boolean changejob) {
        if (pl.isRebirthing()) {
            return;
        }
        if (pl.getVillage().equals(Village.UNDECIDED)) {
            pl.dropMessage("You need to be in a Village to rebirth.");
            return;
        }
        if (pl.getLevel() < pl.getMaxLevel() && !pl.isAdmin()) {
            pl.dropMessage("You need atleast " + pl.getMaxLevel() + " to rebirth");
            return;
        }
        pl.setIsRebirthing(true);
        pl.dropMessage("You are now in Queue. Please Wait 5 seconds");
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                doReborn(pl, changejob);
                pl.dropMessage("Please Wait another 10 seconds");
            }
        }, 5 * 1000);

        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                pl.setIsRebirthing(false);
                pl.dropMessage("You can continue your Training now. Have Fun :)");
            }
        }, 10 * 1000);
    }

    public static void giveRebirth(MapleCharacter pl) {
        while (pl.getLevel() < pl.getMaxLevel()) {
            pl.levelUp();
        }
        doReborn(pl, false);
        pl.dropMessage("You have gained a Rebirth");
    }

    public static void giveRebirth(MapleCharacter pl, int amount) {
        for (int i = 0; i < amount; i++) {
            while (pl.getLevel() < pl.getMaxLevel()) {
                pl.levelUp();
            }
            doReborn(pl, false);
        }
        pl.dropMessage("You have gained " + amount + " Rebirths");
    }

    private static boolean reqMissionComplete(MapleCharacter pl) {
        int reborns = pl.getReborns();
        if (reborns < 250) {
            return true;
        } else if (reborns < 500) {
            return pl.getMission() >= 5;
        } else if (reborns < 750) {
            return pl.getMission() >= 10;
        } else if (reborns < 1000) {
            return pl.getMission() >= 15;
        } else {
            return pl.getMission() >= 20;
        }
    }

    private static void missionReminder(MapleCharacter pl) {
        int reborns = pl.getReborns();
        if ((reborns < 250) && (reborns % 10 == 0) && pl.getMission() < 5) {
            pl.dropMessage("You should start doing mission D quests available from Chunji in your Town.");
        } else if ((reborns < 500) && (reborns % 10 == 0) && pl.getMission() < 10) {
            pl.dropMessage("You should start doing mission C quests available from a NPC deep in Ellin forest where the fairies live.");
        } else if ((reborns < 750) && (reborns % 10 == 0) && pl.getMission() < 10) {
            pl.dropMessage("You should start doing mission B quests available from a NPC who keeps the records.");
        } else if ((reborns < 1000) && (reborns % 10 == 0) && pl.getMission() < 10) {
            pl.dropMessage("You should start doing mission A quests available from a NPC who is located in the dangerous land of the scorpions.");
        }
    }
}
