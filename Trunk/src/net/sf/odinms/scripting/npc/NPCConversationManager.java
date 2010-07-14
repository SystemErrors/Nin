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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.scripting.npc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.odinms.client.Enums.MapleJob;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;

import net.sf.odinms.client.Skills.SkillFactory;
import net.sf.odinms.scripting.AbstractPlayerInteraction;
import net.sf.odinms.scripting.event.EventManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.client.Buffs.MapleStat;
import net.sf.odinms.client.NinjaMS.Rebirths;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.channel.MapleGuildRanking;
import net.sf.odinms.server.MapleSquad;
import net.sf.odinms.server.constants.GameConstants;
import net.sf.odinms.server.constants.InventoryConstants;
import net.sf.odinms.server.maps.AramiaFireWorks;
import net.sf.odinms.server.maps.Event_DojoAgent;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.quest.MapleQuest;
import net.sf.odinms.tools.Packets.GuildPacket;

/**
 *
 * @author Matze
 */
public class NPCConversationManager extends AbstractPlayerInteraction {

    private MapleClient c;
    private int npc, questid;
    private String getText;

    private byte type; // -1 = NPC, 0 = start quest, 1 = end quest
public boolean pendingDisposal = false;

public NPCConversationManager(MapleClient c, int npc, int questid, byte type) {
	super(c);
	this.c = c;
	this.npc = npc;
	this.questid = questid;
	this.type = type;
    }

    public void dispose() {
        NPCScriptManager.getInstance().dispose(this);
    }

    public void sendNext(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 01"));
    }

    public void sendPrev(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 00"));
    }

    public void sendNextPrev(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 01"));
    }

    public void sendOk(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00"));
    }

    public void voteMSG() {
        sendOk("Don't forget to vote for us #b http://ninjams.org/vote");
        dispose();
    }

    public void sendYesNo(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 1, text, ""));
    }

    public void sendAcceptDecline(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0x0C, text, ""));
    }

    public void sendSimple(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 4, text, ""));
    }

    public void sendStyle(String text, int styles[]) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalkStyle(npc, text, styles));
    }

    public void sendGetNumber(String text, int def, int min, int max) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalkNum(npc, text, def, min, max));
    }

    public void sendGetText(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalkText(npc, text));
    }

    public void setGetText(String text) {
        this.getText = text;
    }

    public int getNumber() {
        int fuck = 0;
        try {
            fuck = Integer.parseInt(getText);
        } catch (NumberFormatException numberFormatException) {
            fuck = 0;
        }
        return fuck;
    }

    public String getText() {
        return this.getText;
    }

    public void openShop(int id) {
        MapleShopFactory.getInstance().getShop(id).sendShop(getClient());
    }

    public void changeJob(MapleJob job) {
        getPlayer().changeJob(job);
    }

    public void changeJobById(int fuck) {
        getPlayer().changeJobById(fuck);
    }

    public int getJob() {
        return getPlayer().getJob();
    }   

    public void completeQuest(int id) {
        MapleQuest.getInstance(id).complete(getPlayer(), npc);
    }

    public void forfeitQuest(int id) {
        MapleQuest.getInstance(id).forfeit(getPlayer());
    }

    /**
     * use getPlayer().getMeso() instead
     * @return
     */
    @Deprecated
    public int getMeso() {
        return getPlayer().getMeso();
    }

    public void gainMeso(int gain) {
        getPlayer().gainMeso(gain, true, false, true);
    }

    public void gainExp(int gain) {
        getPlayer().gainExp(gain, true, true);
    }

    public int getNpc() {
        return npc;
    }

    /**
     * use getPlayer().getLevel() instead
     * @return
     */
    @Deprecated
    public int getLevel() {
        return getPlayer().getLevel();
    }

    public void unequipEverything() {
        MapleInventory equipped = getPlayer().getInventory(MapleInventoryType.EQUIPPED);
        MapleInventory equip = getPlayer().getInventory(MapleInventoryType.EQUIP);
        List<Short> ids = new LinkedList<Short>();
        for (IItem item : equipped.list()) {
            ids.add(item.getPosition());
        }
        for (short id : ids) {
            MapleInventoryManipulator.unequip(getC(), id, equip.getNextFreeSlot());
        }
    }

    public void teachSkill(int id, byte level, byte masterlevel) {
        getPlayer().changeSkillLevel(SkillFactory.getSkill(id), level, masterlevel);
    }

    /**
     * Use getPlayer() instead (for consistency with MapleClient)
     * @return
     */
    @Deprecated
    public MapleCharacter getChar() {
        return getPlayer();
    }

    public MapleClient getC() {
        return getClient();
    }

    public void rechargeStars() {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        IItem stars = getPlayer().getInventory(MapleInventoryType.USE).getItem((byte) 1);
        if (InventoryConstants.isThrowingStar(stars.getItemId()) || InventoryConstants.isBullet(stars.getItemId())) {
            stars.setQuantity(ii.getSlotMax(stars.getItemId()));
            getC().getSession().write(MaplePacketCreator.updateInventorySlot(MapleInventoryType.USE, (Item) stars));
        }
    }

    public EventManager getEventManager(String event) {
        return getClient().getChannelServer().getEventSM().getEventManager(event);
    }

    public void showEffect(String effect) {
        getPlayer().getMap().broadcastMessage(MaplePacketCreator.showEffect(effect));
    }

    public void playSound(String sound) {
        getClient().getPlayer().getMap().broadcastMessage(MaplePacketCreator.playSound(sound));
    }

    @Override
    public String toString() {
        return "Conversation with NPC: " + npc;
    }

    public void updateBuddyCapacity(int capacity) {
        getPlayer().setBuddyCapacity(capacity);
    }

    public int getBuddyCapacity() {
        return getPlayer().getBuddyCapacity();
    }

    public void setHair(int hair) {
        c.getPlayer().setHair(hair);
        c.getPlayer().updateSingleStat(MapleStat.HAIR, hair);
        c.getPlayer().equipChanged();
    }

    public void setFace(int face) {
        c.getPlayer().setFace(face);
        c.getPlayer().updateSingleStat(MapleStat.FACE, face);
        c.getPlayer().equipChanged();
    }

    public void setSkin(int color) {
        c.getPlayer().setSkinColor(color);
        c.getPlayer().updateSingleStat(MapleStat.SKIN, color);
    }

    public MapleSquad createMapleSquad(String type) {
        MapleSquad squad = new MapleSquad(c.getChannel(), getPlayer());
        if (c.getChannelServer().addMapleSquad(squad, type)){
        return squad;
        }
        return null;
    }

    public MapleCharacter getSquadMember(String type, int index) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        MapleCharacter ret = null;
        if (squad != null) {
            ret = squad.getMembers().get(index);
        }
        return ret;
    }

    public int getSquadState(String type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            return squad.getStatus();
        } else {
            return 0;
        }
    }

    public void setSquadState(String type, int state) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.setStatus(state);
        }
    }

    public boolean checkSquadLeader(String type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            if (squad.getLeader().getId() == getPlayer().getId()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void removeMapleSquad(String type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            if (squad.getLeader().getId() == getPlayer().getId()) {
                squad.clear();
                c.getChannelServer().removeMapleSquad(type);
            }
        }
    }

    public int numSquadMembers(String type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        int ret = 0;
        if (squad != null) {
            ret = squad.getSquadSize();
        }
        return ret;
    }

    public boolean isSquadMember(String type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        boolean ret = false;
        if (squad.containsMember(getPlayer())) {
            ret = true;
        }
        return ret;
    }

    public void addSquadMember(String type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.addMember(getPlayer());
        }
    }

    public void removeSquadMember(String type, MapleCharacter chr, boolean ban) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.banMember(chr, ban);
        }
    }

    public void removeSquadMember(String type, int index, boolean ban) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            MapleCharacter chrs = squad.getMembers().get(index);
            squad.banMember(chrs, ban);
        }
    }

    public boolean canAddSquadMember(String type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            if (squad.isBanned(getPlayer())) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void warpSquadMembers(String type, int mapId) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapId);
        if (squad != null) {
            if (checkSquadLeader(type)) {
                for (MapleCharacter chaar : squad.getMembers()) {
                    chaar.changeMap(map, map.getPortal(0));
                }
            }
        }
    }

    public void resetReactors(int mapid) {
        MapleMap shit = c.getChannelServer().getMapFactory().getMap(mapid);
        if (shit != null) {
            shit.resetReactors();
        }
    }

    public void killMobsInMap(int mapid) {
        MapleMap shit = c.getChannelServer().getMapFactory().getMap(mapid);
        if (shit != null) {
            shit.killAllMonsters(false);
        }
    }

    public void resetReactors() {
        c.getPlayer().getMap().resetReactors();
    }

     public void displayGuildRanks() {
	c.getSession().write(GuildPacket.showGuildRanks(npc, MapleGuildRanking.getInstance().getRank()));
    }

    public void claimVoteRewards() {
        if (getPlayer().getCheatTracker().spam(600000, 7)) {
            c.showMessage(5, "You are trying too often So The system did not check if you have any rewards left. try after 15 minutes");
            return;
        } else {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * FROM voterewards WHERE name = ?");
                ps.setString(1, c.getAccountName());
                ResultSet rs = ps.executeQuery();
                int i = 0;
                if (!rs.next()) {
                    c.showMessage(5, "You dont seem to have any claim left");
                } else {
                    do {
                        getPlayer().addNinjaTensu();
                        c.showMessage(5, "You have gained 1 NinjaTensuu");
                    } while (rs.next());
                    PreparedStatement pse = con.prepareStatement("DELETE FROM voterewards WHERE name = ?");
                    pse.setString(1, c.getAccountName());
                    pse.executeUpdate();
                    pse.close();
                }
                rs.close();
                ps.close();
                getPlayer().saveToDB(false, false);
            } catch (SQLException ex) {
                Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void gainAP(int fuck) {
        getPlayer().gainAp(fuck);
    }

    public void gainNX(int fuck) {
        getPlayer().addCSPoints(2, fuck);
    }

    public void jqComplete() {
        int[] jqmap = {105040311, 105040313, 105040316, 103000902, 103000905, 103000909, 101000101, 101000104, 109040004, 280020001};
        int i = 0;
        boolean fuck = false;
        for (i = 0; i < jqmap.length; i++) {
            if (getPlayer().getMapId() == jqmap[i]) {
                fuck = true;
            }
        }
        if (fuck && (getChannel() == 3)) {
            getPlayer().giveJQReward();
            getPlayer().finishAlert();
        }
    }

    private int getChannel() {
        return c.getChannel();
    }

    public void startJQ(int fuck) {
        getPlayer().startJq(fuck);
        getPlayer().startAlert(fuck);
    }

    public int getLastJQ() {
        return getPlayer().getLastJQ();
    }

    public void jqBonus() {
        int jqp = getPlayer().getJqpoints();
        if (jqp >= 1) {
            getPlayer().bonusReward();
        } else {
            dropMessage("you don't have any JQ point. you can go fap now");
        }
    }

    public void jqRBBonus() {
        int jqp = getPlayer().getJqpoints();
        if (jqp >= 1) {
            getPlayer().setJqpoints((short) (jqp - 1));
            for (int i = 0; i < 5; i++) {
                Rebirths.giveRebirth(getPlayer());
            }
            dropMessage("you have gained 5 RB");
        } else {
            dropMessage("you don't have any Jq points. you can go fap now");
        }
    }

    public boolean checkSpace(int itemid, int fuck) {
        return getPlayer().checkSpace(itemid, fuck);
    }

    public boolean checkSpace(int itemid) {
        return checkSpace(itemid, 1);
    }

   public void createAlliance(String name) {
	c.getPlayer().getGuild().createAlliance(c, name);
    }

    public boolean hasAlliance() {
	return c.getPlayer().getGuild().getAlliance(c) != null;
    }

    public void sendAllianceInvite(String charname) {
	MapleCharacter z = c.getChannelServer().getPlayerStorage().getCharacterByName(charname);
	if (z != null) {
	    if (z.getGuild().getLeader(z.getClient()) == z) {
//                z.dropMessage(getPlayer().getName() + " invites your guild to join his alliance");
//               z.dropMessage("If you want to accept that offer type @accept, else type @decline");
//               z.setAllianceInvited(getPlayer().getGuild().getAlliance(getPlayer().getClient()));
		c.getPlayer().getAlliance().addGuild(c, c.getPlayer().getGuildId());
	    } else {
		getPlayer().dropMessage(0, "That character is not the leader of the guild");
	    }
	} else {
	    getPlayer().dropMessage(0, "That character is offline");
	}
    }

    public void sendServerNotice(int type, String message) {
        for (ChannelServer vvvv : ChannelServer.getAllInstances()) {
            vvvv.broadcastPacket(MaplePacketCreator.serverNotice(type, message));
        }
    }

    public int partyMembersInMap() {
        int inMap = 0;
        for (MapleCharacter char2 : getPlayer().getMap().getCharacters()) {
            if (char2.getParty() == getPlayer().getParty()) {
                inMap++;
            }
        }
        return inMap;
    }

   public final int getDojoPoints() {
	return c.getPlayer().getDojoPoints();
    }

    public final int getDojoRecord() {
	return c.getPlayer().getDojoStage();
    }

    public void setDojoRecord(final boolean reset) {
	c.getPlayer().setDojoRecord(reset);
    }

    public boolean start_DojoAgent(final boolean dojo, final boolean party) {
	if (dojo) {
	    return Event_DojoAgent.warpStartDojo(c.getPlayer(), party);
	}
	return Event_DojoAgent.warpStartAgent(c.getPlayer(), party);
    }

    public final short getKegs() {
	return AramiaFireWorks.getInstance().getKegsPercentage();
    }

    public void giveKegs(final int kegs) {
	AramiaFireWorks.getInstance().giveKegs(c.getPlayer(), kegs);
    }

    public void addAutobuff(int id) {
        p().addAutobuff(id);
    }

    public void removeAutobuff(String id) {
        p().removeAutobuff(Integer.parseInt(id));
    }

    public void removeAutobuff(int id) {
        p().removeAutobuff((int) id);
    }

    public void removeAutobuff(long id) {
        p().removeAutobuff((int) id);
    }

    public String showAutobuffs() {
        String ret = "";
        for (int i : p().getAutobuffs()) {
            ret += "\r\n\r\n#L" + ((int) i) + "##s" + i + "# #e#b" + getSkillName(i) + "#k#n";
            ret += "\r\n#eJob of Origin#n: " + getJobName(i / 10000) + "#l";
        }
        return ret;
    }

    public String getSkillName(int i) {
        return SkillFactory.getSkillName(i);
    }

    public String getJobName(int id) {
        return GameConstants.getJobName(id);
    }

    public int[] getAutobuffArray() {
        int[] array = new int[p().getAutobuffs().size()];
        int z = 0;
        for (int i : p().getAutobuffs()) {
            array[z] = i;
            z++;
        }
        return array;
    }

    public int getItemType(int itemId) {
        int cat = 0;
        if (itemId >= 1010000 && itemId < 1040000) {
            cat = 1; //"Accessory";
        } else if (itemId >= 1000000 && itemId < 1010000) {
            cat = 2; // "Cap";
        } else if (itemId >= 1102000 && itemId < 1103000) {
            cat = 3; // "Cape";
        } else if (itemId >= 1040000 && itemId < 1050000) {
            cat = 4; //"Coat";
        } else if (itemId >= 1080000 && itemId < 1090000) {
            cat = 5;// "Glove";
        } else if (itemId >= 1050000 && itemId < 1060000) {
            cat = 6; //"Longcoat";
        } else if (itemId >= 1060000 && itemId < 1070000) {
            cat = 7; //"Pants";
        } else if (itemId >= 1802000 && itemId < 1810000) {
            cat = 8; //"PetEquip";
        } else if (itemId >= 1070000 && itemId < 1080000) {
            cat = 9; //"Shoes";
        } else if (itemId >= 1900000 && itemId < 2000000) {
            cat = 10; //"Taming";
        }
        return cat;

    }

    public boolean isBlockedItem(int itemid) {
        int[] blockedItems = {1812006,// Magic scales
            1002140, // - Wizet Invincible Hat
            1042003,// - Wizet Plain Suit
            1062007,// - Wizet Plain Suit Pants
            1322013,// - Wizet Secret Agent Suitcase
            1002959 //- Junior GM Cap
        };
        for (int i = 0; i < blockedItems.length; i++) {
            if (blockedItems[1] == itemid) {
                return true;
            }
        }
        return false;
    }

    public boolean isCashItem(int itemId) {
        if (MapleItemInformationProvider.getInstance().isCashEquip(itemId)) {
            return true;
        }
        return false;
    }

    public boolean nonExistantItem(int itemId) {
        return MapleItemInformationProvider.getInstance().getSlotMax(itemId) < 1;
    }

    public void needGMSModeMsg(int lol) {
        sendOk("You need to be in GMS mode " + lol + ". Talk to NPC duru in henesys to go into GMS mode");
        dispose();
    }

    public int getAccountId() {
        return getPlayer().getAccountID();
    }

    public void missionComplete() {
        getPlayer().missionComplete();
    }

    public void gainFame(int famechange) {
        getPlayer().addFame(famechange);
        getPlayer().updateSingleStat(MapleStat.FAME, getPlayer().getFame());
    }

    public void bowTie() {
        if (!checkSpace(1122001)){
            dropMessage("You do not have enough space in your inventory");
            return;
        }
        int[] item = {1122001, 1122002, 1122003, 1122004, 1122005, 1122006};
        short[] stat = {69, 1337, 3337, 7337, 13337, 31337};
        for (byte i = 1; i <= item.length; i++) {
            if(haveItem(item[i-1], 1)){
                int jqp = getPlayer().getJqpoints();
                byte lol = (byte) (i * 3);
                if(jqp >= lol){
                    getPlayer().setJqpoints((short)(jqp - lol));
                    gainStatItem(item[i], stat[i], lol , lol);
                    break;
                }
            }
        }
    }
}
