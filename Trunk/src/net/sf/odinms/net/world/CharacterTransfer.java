/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.net.world;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedHashMap;
import java.util.Map;
import net.sf.odinms.client.BuddylistEntry;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Inventory.MapleMount;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.tools.Pair;

/**
 *
 * @author Admin
 */
public class CharacterTransfer implements Externalizable {

    public String name, legend, previousnames, createdate, accountname, footnote;
    public short level, dpoints, damount;
    public byte guildrank, allianceRank, mutality, clonelimit, rasengan, msi,
            prefixshit, autoap, GMSMode, smega, textColour, mission, gmLevel,
            lastjq, village, ninjatensu, channel, gender;
    public int characterid, fame, str, dex, int_, luk, hp, mp, maxhp, maxmp,
            exp, remainingAp, meso,
            skinColor, job, hair, face, mapid, initialSpawnPoint, world,
            accountid, reborns, mobkilled, bosskilled, guildid, lmpoints,
            pvpdeaths, pvpkills, rank, rankmove, jobrank, jobrankmove, taocheck,
            lastDojoStage, dojoPoints, taorank, clantaorank, bossPoints,
            kpqpoints, expBoost, mesoBoost, dropBoost, bdropBoost, partyid,
            paypalNX, maplePoints, cardNX, jqfinished, jqpoints, messengerid,
            messengerposition, mbookcover, mount_itemid, mBookCover,
            mount_Fatigue, mount_level, mount_exp;
    public final Map<Integer, Pair<String, Boolean>> buddies = new LinkedHashMap<Integer, Pair<String, Boolean>>();
    public Object monsterbook, inventorys, skillmacro, keymap, savedlocation, famedcharacters,
            storage, rocks, wishlist, autobuffs;
    public long TransferTime;

    public CharacterTransfer() {
    }

    public CharacterTransfer(final MapleCharacter chr) {
        this.characterid = chr.getId();
        this.accountid = chr.getAccountID();
        this.channel = (byte) chr.getClient().getChannel();
        this.name = chr.getName();
        this.level = chr.getLevel();
        this.fame = chr.getFame();
        this.str = chr.getStat().getStr();
        this.dex = chr.getStat().getDex();
        this.int_ = chr.getStat().getInt();
        this.luk = chr.getStat().getLuk();
        this.hp = chr.getStat().getHp();
        this.mp = chr.getStat().getMp();
        this.maxhp = chr.getStat().getMaxHp();
        this.maxmp = chr.getStat().getMaxMp();
        this.exp = chr.getExp();
        this.remainingAp = chr.getRemainingAp();
        this.meso = chr.getMeso();
        this.skinColor = chr.getSkinColor();
        this.job = chr.getJob();
        this.hair = chr.getHair();
        this.face = chr.getFace();
        this.mapid = chr.getMapId();
        this.initialSpawnPoint = chr.getInitialSpawnpoint();
        this.world = chr.getWorld();
        this.reborns = chr.getReborns();
        this.mobkilled = chr.getMobKilled();
        this.bosskilled = chr.getBossKilled();
        this.mutality = chr.getMute();
        this.guildid = chr.getGuildId();
        this.guildrank = chr.getGuildRank();
        this.allianceRank = chr.getAllianceRank();
        this.clonelimit = chr.getCloneLimit();
        this.rasengan = chr.getRasengan();
        this.legend = chr.getLegend();
        this.msi = chr.getMaxStatItems();
        this.lmpoints = chr.getLmpoints();
        this.pvpdeaths = chr.getPvpDeaths();
        this.pvpkills = chr.getPvpKills();
        this.prefixshit = chr.getPrefixShit();
        this.autoap = chr.getAutoAp();
        this.jobrank = chr.getJobRank();
        this.jobrankmove = chr.getJobRankMove();
        this.rank = chr.getRank();
        this.rankmove = chr.getRankMove();
        for (final BuddylistEntry qs : chr.getBuddylist().getBuddies()) {
            this.buddies.put(qs.getCharacterId(), new Pair<String, Boolean>(qs.getName(), qs.isVisible()));
        }
        this.createdate = chr.getCreateDate();
        this.previousnames = chr.getPreviousNames();
        this.taocheck = chr.getTaoCheck();
        this.GMSMode = chr.getGMSMode();
        this.lastDojoStage = chr.getDojoStage();
        this.dojoPoints = chr.getDojoPoints();
        this.taorank = chr.getTaorank();
        this.clantaorank = chr.getClantaorank();
        this.smega = chr.getSmegaStatus();
        this.bossPoints = chr.getBossPoints();
        this.kpqpoints = chr.getKpqpoints();
        this.expBoost = chr.getExpBoost();
        this.mesoBoost = chr.getMesoBoost();
        this.dropBoost = chr.getDropBoost();
        this.bdropBoost = chr.getBossDropBoost();
        //this.clan = Clans.getById(rs.getInt("clan"));
        this.textColour = chr.getTextColour();
        this.mission = chr.getMission();
        this.partyid = chr.getPartyId();
        this.accountname = chr.getClient().getAccountName();
        this.paypalNX = chr.getCSPoints(2);
        this.maplePoints = chr.getCSPoints(1);
        this.cardNX = chr.getCSPoints(0);
        this.ninjatensu = chr.getNinjaTensu();
        this.dpoints = chr.getDPoints();
        this.damount = chr.getDAmount();
        this.jqpoints = chr.getJqpoints();
        this.jqfinished = chr.getJqFinished();
        this.lastjq = chr.getLastJQ();
        this.footnote = chr.getFootnote();
        this.village = chr.getVillage();
        this.gmLevel = chr.getGMLevel();
        this.autobuffs = chr.getAutobuffs();
        for (final BuddylistEntry qs : chr.getBuddylist().getBuddies()) {
            this.buddies.put(qs.getCharacterId(), new Pair<String, Boolean>(qs.getName(), qs.isVisible()));
        }
        if (chr.getMessenger() != null) {
            this.messengerid = chr.getMessenger().getId();
            this.messengerposition = chr.getMessengerPosition();
        } else {
            messengerid = 0;
            messengerposition = 4;
        }
        this.mBookCover = chr.getMonsterBookCover();
        this.monsterbook = chr.getMonsterBook();
        this.inventorys = chr.getInventorys();
        this.keymap = chr.getKeyLayout();
        this.skillmacro = chr.getSkillMacros();
        this.savedlocation = chr.getSavedLocations();
        this.famedcharacters = chr.getFamedCharacters();
        this.storage = chr.getStorage();
        this.wishlist = chr.getWishlist();
        this.rocks = chr.getRocks();
        this.wishlist = chr.getWishlist();
        final MapleMount mount = chr.getMount();
        this.mount_itemid = mount.getItemId();
        this.mount_Fatigue = mount.getFatigue();
        this.mount_level = mount.getLevel();
        this.mount_exp = mount.getExp();
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.characterid = in.readInt();
        this.accountid = in.readInt();
        this.channel = in.readByte();
        this.name = (String) in.readObject();
        this.level = in.readShort();
        this.fame = in.readShort();
        this.str = in.readInt();
        this.dex = in.readInt();
        this.int_ = in.readInt();
        this.luk = in.readInt();
        this.hp = in.readInt();
        this.mp = in.readInt();
        this.maxhp = in.readInt();
        this.maxmp = in.readInt();
        this.exp = in.readInt();
        this.remainingAp = in.readInt();
        this.meso = in.readInt();
        this.skinColor = in.readInt();
        this.job = in.readInt();
        this.hair = in.readInt();
        this.face = in.readInt();
        this.mapid = in.readInt();
        this.initialSpawnPoint = in.readInt();
        this.world = in.readInt();
        this.reborns = in.readInt();
        this.mobkilled = in.readInt();
        this.bosskilled = in.readInt();
        this.mutality = in.readByte();
        this.guildid = in.readInt();
        this.guildrank = in.readByte();
        this.allianceRank = in.readByte();
        this.clonelimit = in.readByte();
        this.rasengan = in.readByte();
        this.legend = (String) in.readObject();
        this.msi = in.readByte();
        this.lmpoints = in.readInt();
        this.pvpdeaths = in.readInt();
        this.pvpkills = in.readInt();
        this.prefixshit = in.readByte();
        this.autoap = in.readByte();
        this.jobrank = in.readInt();
        this.jobrankmove = in.readInt();
        this.rank = in.readInt();
        this.rankmove = in.readInt();
        final short addedbuddysize = in.readShort();
        int buddyid;
        String buddyname;
        boolean visible;
        for (int i = 0; i < addedbuddysize; i++) {
            buddyid = in.readInt();
            buddyname = (String) in.readObject();
            visible = in.readBoolean();
            buddies.put(buddyid, new Pair(buddyname, visible));
        }
        this.createdate = (String) in.readObject();
        this.previousnames = (String) in.readObject();
        this.taocheck = in.readInt();
        this.GMSMode = in.readByte();
        this.lastDojoStage = in.readInt();
        this.dojoPoints = in.readInt();
        this.taorank = in.readInt();
        this.clantaorank = in.readInt();
        this.smega = in.readByte();
        this.bossPoints = in.readInt();
        this.kpqpoints = in.readInt();
        this.expBoost = in.readInt();
        this.mesoBoost = in.readInt();
        this.dropBoost = in.readInt();
        this.bdropBoost = in.readInt();
        //this.clan = Clans.getById(rs.getInt("clan"));
        this.textColour = in.readByte();
        this.mission = in.readByte();
        this.partyid = in.readInt();
        this.accountname = (String) in.readObject();
        this.paypalNX = in.readInt();
        this.maplePoints = in.readInt();
        this.cardNX = in.readInt();
        this.ninjatensu = in.readByte();
        this.dpoints = in.readShort();
        this.damount = in.readShort();
        this.jqpoints = in.readInt();
        this.jqfinished = in.readInt();
        this.lastjq = in.readByte();
        this.footnote = (String) in.readObject();
        this.village = in.readByte();
        this.gmLevel = in.readByte();
        this.messengerid = in.readInt();
        this.messengerposition = in.readInt();
        this.mount_itemid = in.readInt();
        this.mount_Fatigue = in.readInt();
        this.mount_level = in.readInt();
        this.mount_exp = in.readInt();
        this.mBookCover = in.readInt();

        this.monsterbook = in.readObject();
        this.autobuffs = in.readObject();
        this.inventorys = in.readObject();
        this.keymap = in.readObject();
        this.skillmacro = in.readObject();
        this.savedlocation = in.readObject();
        this.famedcharacters = in.readObject();
        this.storage = in.readObject();
        this.wishlist = in.readObject();
        this.rocks = in.readObject();
        TransferTime = System.currentTimeMillis();
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeInt(this.characterid);
        out.writeInt(this.accountid);
        out.writeByte(this.channel);
        out.writeObject(this.name);
        out.writeShort(this.level);
        out.writeShort(this.fame);
        out.writeInt(this.str);
        out.writeInt(this.dex);
        out.writeInt(this.int_);
        out.writeInt(this.luk);
        out.writeInt(this.hp);
        out.writeInt(this.mp);
        out.writeInt(this.maxhp);
        out.writeInt(this.maxmp);
        out.writeInt(this.exp);
        out.writeInt(this.remainingAp);
        out.writeInt(this.meso);
        out.writeInt(this.skinColor);
        out.writeInt(this.job);
        out.writeInt(this.hair);
        out.writeInt(this.face);
        out.writeInt(this.mapid);
        out.writeInt(this.initialSpawnPoint);
        out.writeInt(this.world);
        out.writeInt(this.reborns);
        out.writeInt(this.mobkilled);
        out.writeInt(this.bosskilled);
        out.writeByte(this.mutality);
        out.writeInt(this.guildid);
        out.writeByte(this.guildrank);
        out.writeByte(this.allianceRank);
        out.writeByte(this.clonelimit);
        out.writeByte(this.rasengan);
        out.writeObject(this.legend);
        out.writeByte(this.msi);
        out.writeInt(this.lmpoints);
        out.writeInt(this.pvpdeaths);
        out.writeInt(this.pvpkills);
        out.writeByte(this.prefixshit);
        out.writeByte(this.autoap);
        out.writeInt(this.jobrank);
        out.writeInt(this.jobrankmove);
        out.writeInt(this.rank);
        out.writeInt(this.rankmove);
        out.writeObject(this.createdate);
        out.writeObject(this.previousnames);
        out.writeInt(this.taocheck);
        out.writeByte(this.GMSMode);
        out.writeInt(this.lastDojoStage);
        out.writeInt(this.dojoPoints);
        out.writeInt(this.taorank);
        out.writeInt(this.clantaorank);
        out.writeByte(this.smega);
        out.writeInt(this.bossPoints);
        out.writeInt(this.kpqpoints);
        out.writeInt(this.expBoost);
        out.writeInt(this.mesoBoost);
        out.writeInt(this.dropBoost);
        out.writeInt(this.bdropBoost);
//this.clan = Clans.getById(rs.getInt("clan"));
        out.writeByte(this.textColour);
        out.writeByte(this.mission);
        out.writeInt(this.partyid);
        out.writeObject(this.accountname);
        out.writeInt(this.paypalNX);
        out.writeInt(this.maplePoints);
        out.writeInt(this.cardNX);
        out.writeByte(this.ninjatensu);
        out.writeShort(this.dpoints);
        out.writeShort(this.damount);
        out.writeInt(this.jqpoints);
        out.writeInt(this.jqfinished);
        out.writeByte(this.lastjq);
        out.writeObject(this.footnote);
        out.writeByte(this.village);
        out.writeByte(this.gmLevel);
        out.writeInt(this.messengerid);
        out.writeInt(this.messengerposition);
        out.writeInt(this.mount_itemid);
        out.writeInt(this.mount_Fatigue);
        out.writeInt(this.mount_level);
        out.writeInt(this.mount_exp);
        out.writeInt(this.mBookCover);
        out.writeObject(this.monsterbook);
        out.writeObject(this.autobuffs);
        out.writeObject(this.inventorys);
        out.writeObject(this.keymap);
        out.writeObject(this.skillmacro);
        out.writeObject(this.savedlocation);
        out.writeObject(this.famedcharacters);
        out.writeObject(this.storage);
        out.writeObject(this.wishlist);
        out.writeObject(this.rocks);

        out.writeShort(this.buddies.size());
        for (final Map.Entry<Integer, Pair<String, Boolean>> qs : this.buddies.entrySet()) {
            out.writeInt(qs.getKey());
            out.writeObject(qs.getValue().left);
            out.writeBoolean(qs.getValue().right);
        }
    }
}
