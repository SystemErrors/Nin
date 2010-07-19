/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Publicgene License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.odinms.client;

import net.sf.odinms.client.Skills.ISkill;
import net.sf.odinms.client.Buffs.MapleDisease;
import net.sf.odinms.client.Buffs.MapleStat;
import net.sf.odinms.client.Buffs.MapleBuffStat;
import net.sf.odinms.client.Buffs.MapleBuffStatValueHolder;
import net.sf.odinms.client.Buffs.MapleCoolDownValueHolder;
import net.sf.odinms.client.Buffs.DiseaseValueHolder;
import net.sf.odinms.client.Skills.SkillFactory;
import net.sf.odinms.client.Skills.SkillMacro;
import net.sf.odinms.client.Skills.SkillEntry;
import net.sf.odinms.client.Inventory.*;
import net.sf.odinms.net.world.CharacterTransfer;
import net.sf.odinms.server.constants.*;
import java.awt.Point;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Deque;
import net.sf.odinms.client.Enums.*;
import net.sf.odinms.client.Inventory.MapleRing;
import net.sf.odinms.client.NinjaMS.Donations;
import net.sf.odinms.client.NinjaMS.Rebirths;

import net.sf.odinms.client.anticheat.CheatTracker;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.database.DatabaseException;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;

import net.sf.odinms.scripting.event.EventInstanceManager;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleShop;
import net.sf.odinms.client.Buffs.MapleStatEffect;
import net.sf.odinms.client.NinjaMS.NinjaMath;
import net.sf.odinms.net.CashShop.CashShopServer;
import net.sf.odinms.net.channel.handler.InterServerHandler;
import net.sf.odinms.net.world.MapleMessenger;
import net.sf.odinms.net.world.MapleMessengerCharacter;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.PartyOperation;
import net.sf.odinms.net.world.PlayerBuffValueHolder;
import net.sf.odinms.net.world.PlayerCoolDownValueHolder;
import net.sf.odinms.net.world.PlayerDiseaseValueHolder;
import net.sf.odinms.server.MapleStorage;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.AbstractAnimatedMapleMapObject;
import net.sf.odinms.server.maps.MapleDoor;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapFactory;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.maps.MapleSummon;
import net.sf.odinms.server.maps.SavedLocationType;
import net.sf.odinms.server.quest.MapleQuest;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.net.world.guild.*;
import net.sf.odinms.net.world.remote.WorldChannelInterface;

import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.MapleCarnivalChallenge;
import net.sf.odinms.server.MapleCarnivalParty;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MiniGame;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MobSkill;
import net.sf.odinms.server.life.MobSkillFactory;
import net.sf.odinms.server.maps.MapleMapEffect;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.server.shops.HiredMerchant;
import net.sf.odinms.server.shops.IMaplePlayerShop;
import net.sf.odinms.tools.Packets.MTSCSPacket;
import net.sf.odinms.tools.Packets.MobPacket;
import net.sf.odinms.tools.Packets.MonsterCarnivalPacket;
import net.sf.odinms.tools.Packets.PetPacket;
import net.sf.odinms.tools.Randomizer;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

public class MapleCharacter extends AbstractAnimatedMapleMapObject implements InventoryContainer, Serializable {

    private String name, chalktext;
    private long lastCombo, lastfametime, keydown_skill;
    private byte lastDojoStage, gmLevel, gender; // Make this a quest record, TODO : Transfer it somehow with the current data
    private short level, dojoenergy, combo, availableCP, totalCP, jqPoints;
    private int world, accountid, id,
            meso, exp, job, rank, rankMove, jobRank, jobRankMove,
            hair, face, skinColor, remainingAp, remainingSp,
            fame, mapid, initialSpawnPoint, bookCover, dojoPoints,
            guildid, fallcounter,
            messengerposition, chair, itemEffect, deaths;
    private boolean canDoor, Berserk, hidden;
    private int[] wishlist, savedLocations, rocks;
    private transient AtomicInteger inst;
    private transient List<LifeMovementFragment> lastres;
    private List<Integer> lastmonthfameids;
    private List<MapleDoor> doors;
    private transient Set<MapleMonster> controlled;
    private transient Set<MapleMapObject> visibleMapObjects;
    private Map<MapleQuest, MapleQuestStatus> quests;
    private Map<Integer, String> questinfo;
    private Map<ISkill, SkillEntry> skills = new LinkedHashMap<ISkill, SkillEntry>();
    private transient Map<MapleBuffStat, MapleBuffStatValueHolder> effects = new LinkedHashMap<MapleBuffStat, MapleBuffStatValueHolder>(50);
    private transient Map<Integer, MapleSummon> summons;
    private transient Map<Integer, MapleCoolDownValueHolder> coolDowns = new LinkedHashMap<Integer, MapleCoolDownValueHolder>(50);
    private transient Map<MapleDisease, DiseaseValueHolder> diseases;
    private MapleAlliance alliance;
    private transient Deque<MapleCarnivalChallenge> pendingCarnivalRequests;
    private transient MapleCarnivalParty carnivalParty;
    private BuddyList buddylist;
    private MonsterBook monsterbook;
    private transient CheatTracker anticheat;
    private MapleClient client;
    private PlayerStats stats;
    //  private PlayerRandomStream CRand;
    private transient MapleMap map;
    private transient MapleShop shop;
    private MapleStorage storage;
    private transient MapleTrade trade;
    private MapleMount mount;
    private MapleMessenger messenger;
    private IMaplePlayerShop playerShop;
    private MapleParty party;
    private MapleGuildCharacter mgc;
    private transient EventInstanceManager eventInstance;
    private MapleInventory[] inventory;
    private SkillMacro[] skillMacros = new SkillMacro[5];
    private MapleKeyLayout keylayout;
    private transient ScheduledFuture<?> fullnessSchedule, fullnessSchedule_1, fullnessSchedule_2, hpDecreaseTask;
    private transient ScheduledFuture<?> beholderHealingSchedule, beholderBuffSchedule, BerserkSchedule;
    private transient ScheduledFuture<?> dragonBloodSchedule;
    private transient ScheduledFuture<?> mapTimeLimitTask, fishing;
    private int storageAp;
    private String createdate;
    private MaplePet pet = null;
    private List<MaplePet> pets;
    private boolean inflicted, incs, leet = false;
    private int cardNX, maplePoints, paypalNX;
    private int markedMonster = 0, npcId = -1, battleshipHp = 0, energybar;
    private Byte hammerSlot = null, rasengan, textColour = 0, mutality,
            guildrank, allianceRank, clonelimit, ninjatensu, village;
    private int reborn, mobkilled, bosskilled,
            rankmove, jobrank, jobrankmove, taorank, clantaorank;
    /**
     * Title shit contols the display of legend and title.
     * 0 = show none
     * 1 = show title only
     * 2 = show both.
     * 3 = show legend only
     */
    private byte prefixshit, smega;
    private String legend;
    // fakes!- Oliver
    private List<Clones> fakes = new ArrayList<Clones>();
    public boolean isfake = false, rebirthing;
    //maxtstat counter
    private byte maxstatitem = 0;
    //donator
    private short dpoints, damount;
    //JQ
    private String jqStart;
    private int jqFinished;
    private byte lastjq;
    private String lastJQFinish = "In 1947";
    // PVP
    private int pvpkills, pvpdeaths;
    private boolean cannotdrop = false;
    private byte autoap;
    // Rate boost
    private int expBoost, mesoBoost, dropBoost, bdropBoost;
    private boolean noHide;
    // Checks
    private String previousnames, footnote;
    private int taocheck;
    private boolean keymapchange, macrochange;
    //GMS Mode
    private byte GMSMode = 0, mission;
    private Map<Integer, String> entered = new LinkedHashMap<Integer, String>();
    //autobuff - oliver
    private List<Integer> autobuffs = new LinkedList<Integer>(); // 12
    private boolean autobuffchange = false;
    //points
    private int bossPoints, kpqpoints, lmpoints;
    //MiniGames
    private int omokwins, omokties, omoklosses, matchcardwins, matchcardties,
            matchcardlosses;
    private MiniGame miniGame;
    //HiredMerchant
    private HiredMerchant hiredMerchant = null;
    private boolean hasMerchant = false;
    private int slots = 0;
    //  Ring - Oliver
    private List<MapleRing> crushRings = new LinkedList<MapleRing>(),
            friendshipRings = new LinkedList<MapleRing>(),
            marriageRings = new LinkedList<MapleRing>();
    //item filter
    private ArrayList<Integer> excluded = new ArrayList<Integer>();

    private MapleCharacter(final boolean ChannelServer) {
        setStance(0);
        inventory = new MapleInventory[MapleInventoryType.values().length];
        for (MapleInventoryType type : MapleInventoryType.values()) {
            inventory[type.ordinal()] = new MapleInventory(type);
        }
        for (int i = 0; i < SavedLocationType.values().length; i++) {
            savedLocations[i] = -1;
        }
        setPosition(new Point(0, 0));
        if (ChannelServer) {
            lastCombo = 0;
            dojoenergy = 0;
            combo = 0;
            keydown_skill = 0;
            messengerposition = 4;
            canDoor = true;
            Berserk = false;
            smega = 0;
            wishlist = new int[10];
            rocks = new int[10];
            inst = new AtomicInteger();
            inst.set(0); // 1 = NPC/ Quest, 2 = Duey, 3 = Hired Merch store, 4 = Storage
            keylayout = new MapleKeyLayout();
            doors = new ArrayList<MapleDoor>();
            diseases = new LinkedHashMap<MapleDisease, DiseaseValueHolder>(5);
            controlled = new LinkedHashSet<MapleMonster>();
            summons = new LinkedHashMap<Integer, MapleSummon>();
            visibleMapObjects = new LinkedHashSet<MapleMapObject>();
            //       pendingCarnivalRequests = new LinkedList<MapleCarnivalChallenge>();
            savedLocations = new int[SavedLocationType.values().length];
            for (int i = 0; i < SavedLocationType.values().length; i++) {
                savedLocations[i] = -1;
            }
            questinfo = new LinkedHashMap<Integer, String>();
            anticheat = new CheatTracker(this);
            pets = new ArrayList<MaplePet>();
        }
    }

    public MapleCharacter getThis() {
        return this;
    }

    public static void doLoginMapCheck(MapleCharacter ret) {
        MapleMapFactory mapFactory = ChannelServer.getInstance(ret.client.getChannel()).getMapFactory();
        ret.map = mapFactory.getMap(ret.mapid);
        if (ret.map == null) { //char is on a map that doesn't exist warp it to henesys
            ret.map = mapFactory.getMap(100000000);
        }
        if (ret.inJail()) {
            ret.scheduleUnJail();
        }
        MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
        if (portal == null) {
            portal = ret.map.getPortal(0); // char is on a spawnpoint that doesn't exist - select the first spawnpoint instead
            ret.initialSpawnPoint = 0;
        }
        ret.setPosition(portal.getPosition());
    }

    private void setClient(MapleClient c) {
        client = c;
    }

    private void setMGC(MapleGuildCharacter mgc) {
        this.mgc = mgc;
    }

    public final static MapleCharacter ReconstructChr(final CharacterTransfer ct, final MapleClient c, final boolean isChannel) {
        final MapleCharacter ret = new MapleCharacter(true);
        ret.client = c;
        if (!isChannel) {
            ret.client.setChannel(ct.channel);
        }
        ret.id = ct.characterid;
        ret.accountid = ct.accountid;
        ret.name = ct.name;
        ret.level = ct.level;
        ret.fame = ct.fame;

        ret.stats = new PlayerStats(ret);
        ret.stats.setStr(ct.str);
        ret.stats.setDex(ct.dex);
        ret.stats.setInt(ct.int_);
        ret.stats.setLuk(ct.luk);
        ret.stats.setMaxHp(ct.maxhp);
        ret.stats.setMaxMp(ct.maxmp);
        ret.stats.setHp(ct.hp);
        ret.stats.setMp(ct.mp);

        ret.exp = ct.exp;
        ret.remainingAp = ct.remainingAp;
        ret.meso = ct.meso;
        ret.gmLevel = ct.gmLevel;


        ret.skinColor = ct.skinColor;
        ret.gender = ct.gender;
        ret.job = ct.job;
        ret.hair = ct.hair;
        ret.face = ct.face;

        ret.mapid = ct.mapid;
        ret.initialSpawnPoint = ct.initialSpawnPoint;
        ret.world = ct.world;

        ret.rank = ct.rank;
        ret.rankMove = ct.rankmove;
        ret.jobRank = ct.jobrank;
        ret.jobRankMove = ct.jobrankmove;

        ret.bookCover = ct.mBookCover;
        ret.dojoPoints = ct.dojoPoints;
        ret.lastDojoStage = ct.lastDojoStage;

        ret.guildid = ct.guildid;
        ret.guildrank = ct.guildrank;
        ret.allianceRank = ct.allianceRank;
        if (ret.guildid > 0) {
            ret.mgc = new MapleGuildCharacter(ret);
        }

        ret.reborn = ct.reborns;
        ret.mobkilled = ct.mobkilled;
        ret.bosskilled = ct.bosskilled;

        ret.mutality = ct.mutality;
        ret.clonelimit = ct.clonelimit;
        ret.rasengan = ct.rasengan;
        ret.legend = ct.legend;
        ret.maxstatitem = ct.msi;
        ret.lmpoints = ct.lmpoints;
        ret.pvpdeaths = ct.pvpdeaths;
        ret.pvpkills = ct.pvpkills;
        ret.prefixshit = ct.prefixshit;
        ret.autoap = ct.autoap;
        ret.buddylist = new BuddyList(200);

        ret.createdate = ct.createdate;
        ret.previousnames = ct.previousnames;
        ret.taocheck = ct.taocheck;
        ret.GMSMode = ct.GMSMode;

        ret.taorank = ct.taorank;
        ret.clantaorank = ct.clantaorank;
        ret.smega = ct.smega;

        ret.bossPoints = ct.bossPoints;
        ret.kpqpoints = ct.kpqpoints;

        ret.expBoost = ct.expBoost;
        ret.mesoBoost = ct.mesoBoost;
        ret.dropBoost = ct.dropBoost;
        ret.bdropBoost = ct.bdropBoost;

        ret.textColour = ct.textColour;
        ret.mission = ct.mission;

        ret.paypalNX = ct.paypalNX;
        ret.maplePoints = ct.maplePoints;
        ret.cardNX = ct.cardNX;
        ret.ninjatensu = ct.ninjatensu;
        ret.dpoints = ct.dpoints;
        ret.damount = ct.damount;
        ret.jqPoints = ct.jqpoints;
        ret.jqFinished = ct.jqfinished;
        ret.lastjq = ct.lastjq;
        ret.footnote = ct.footnote;
        ret.village = ct.village;
        ret.autobuffs = (List<Integer>) ct.autobuffs;

        ret.monsterbook = (MonsterBook) ct.monsterbook;
        ret.inventory = (MapleInventory[]) ct.inventorys;
        ret.skillMacros = (SkillMacro[]) ct.skillmacro;
        ret.keylayout = (MapleKeyLayout) ct.keymap;
        ret.savedLocations = (int[]) ct.savedlocation;
        ret.wishlist = (int[]) ct.wishlist;
        ret.rocks = (int[]) ct.rocks;

        ret.storage = (MapleStorage) ct.storage;
        c.setAccountName(ct.accountname);

        ret.lastfametime = ct.lastfametime;
        ret.lastmonthfameids = (List<Integer>) ct.famedcharacters;

        ret.buddylist.loadFromTransfer((List<BuddyData>) ct.buddies);


        ret.mount = new MapleMount(ret, ct.mount_itemid, 1004, ct.mount_Fatigue, ct.mount_level, ct.mount_exp);
        if (isChannel) {
            final MapleMapFactory mapFactory = ChannelServer.getInstance(c.getChannel()).getMapFactory();
            ret.map = mapFactory.getMap(ret.mapid);
            if (ret.map == null) { //char is on a map that doesn't exist warp it to henesys
                ret.map = mapFactory.getMap(100000000);
            } else {
                if (ret.map.getForcedReturnId() != 999999999) {
                    ret.map = ret.map.getForcedReturnMap();
                }
            }
            MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
            if (portal == null) {
                portal = ret.map.getPortal(0); // char is on a spawnpoint that doesn't exist - select the first spawnpoint instead
                ret.initialSpawnPoint = 0;
            }
            ret.setPosition(portal.getPosition());

            int partyid = ct.partyid;
            if (partyid >= 0) {
                try {
                    MapleParty party = c.getChannelServer().getWorldInterface().getParty(partyid);
                    if (party != null && party.getMemberById(ret.id) != null) {
                        ret.party = party;
                    }
                } catch (RemoteException e) {
                    c.getChannelServer().reconnectWorld();
                }
            }

            final int messengerid = ct.messengerid;
            final int position = ct.messengerposition;
            if (messengerid > 0 && position < 4 && position > -1) {
                try {
                    WorldChannelInterface wci = ChannelServer.getInstance(c.getChannel()).getWorldInterface();
                    MapleMessenger messenger = wci.getMessenger(messengerid);
                    if (messenger != null) {
                        ret.messenger = messenger;
                        ret.messengerposition = position;
                    }
                } catch (RemoteException e) {
                    c.getChannelServer().reconnectWorld();
                }
            }
        } else {
            int partyid = ct.partyid;
            if (partyid >= 0) {
                try {
                    MapleParty party = CashShopServer.getInstance().getCSInterface().getParty(partyid);
                    if (party != null && party.getMemberById(ret.id) != null) {
                        ret.party = party;
                    }
                } catch (RemoteException e) {
                    c.getChannelServer().reconnectWorld();
                }
            }

            ret.messenger = null;
            ret.messengerposition = ct.messengerposition;
        }

        ret.stats.recalcLocalStats();
        ret.silentEnforceMaxHpMp();

        return ret;
    }

    public static MapleCharacter loadCharFromDB(int charid, MapleClient client, boolean channelserver) {
        final MapleCharacter ret = new MapleCharacter(channelserver);
        ret.client = client;
        ret.id = charid;

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("Loading the Char Failed (char not found)");
            }
            ret.name = rs.getString("name");
            ret.level = rs.getShort("level");
            if (ret.level >= 255) {
                ret.level = 255;
            }
            if (ret.level < 1) {
                ret.level = 1;
            }
            ret.fame = rs.getInt("fame");
            ret.stats.str = rs.getInt("str");
            ret.stats.dex = rs.getInt("dex");
            ret.stats.int_ = rs.getInt("int");
            ret.stats.luk = rs.getInt("luk");
            ret.exp = rs.getInt("exp");
            ret.stats.hp = rs.getInt("hp");
            ret.stats.mp = rs.getInt("mp");
            ret.stats.maxhp = rs.getInt("maxhp");
            ret.stats.maxmp = rs.getInt("maxmp");
            ret.storageAp = rs.getInt("storageap");
            ret.remainingAp = rs.getInt("ap");
            ret.meso = rs.getInt("meso");
            ret.skinColor = rs.getInt("skincolor");
            ret.job = rs.getInt("job");
            ret.hair = rs.getInt("hair");
            ret.face = rs.getInt("face");
            ret.accountid = rs.getInt("accountid");
            ret.mapid = rs.getInt("map");
            ret.initialSpawnPoint = rs.getInt("spawnpoint");
            ret.world = rs.getInt("world");
            ret.reborn = rs.getInt("reborns");
            ret.mobkilled = rs.getInt("mobkilled");
            ret.bosskilled = rs.getInt("bosskilled");
            ret.mutality = rs.getByte("mutality");
            ret.guildid = rs.getInt("guildid");
            ret.guildrank = rs.getByte("guildrank");
            ret.allianceRank = rs.getByte("allianceRank");
            ret.clonelimit = rs.getByte("clonelimit");
            ret.rasengan = rs.getByte("rasengan");
            ret.legend = rs.getString("legend");
            ret.maxstatitem = rs.getByte("msi");
            ret.lmpoints = rs.getInt("lmpoints");
            ret.pvpdeaths = rs.getInt("pvpdeaths");
            ret.pvpkills = rs.getInt("pvpkills");
            ret.prefixshit = rs.getByte("prefixshit");
            ret.autoap = rs.getByte("autoap");
            ret.jobrank = rs.getInt("jobrank");
            ret.jobrankmove = rs.getInt("jobrankmove");
            ret.rank = rs.getInt("rank");
            ret.rankmove = rs.getInt("rankmove");
            ret.buddylist = new BuddyList(255);
            if (ret.guildid > 0) {
                ret.mgc = new MapleGuildCharacter(ret);
            }
            if (channelserver) {
                ret.createdate = rs.getString("createdate");
                ret.previousnames = rs.getString("previousnames");
                if (ret.previousnames == null || ret.previousnames.length() < 2) {
                    ret.previousnames = "||";
                }
                ret.taocheck = rs.getInt("taocheck");
                ret.GMSMode = rs.getByte("gmsmode");
                ret.lastDojoStage = rs.getByte("lastdojostage");
                ret.dojoPoints = rs.getInt("dojopoints");
                ret.taorank = rs.getInt("taorank");
                ret.clantaorank = rs.getInt("clantaorank");
                ret.smega = rs.getByte("smega");
                ret.bossPoints = rs.getInt("bqpoints");
                ret.kpqpoints = rs.getInt("kpqpoints");
                ret.expBoost = rs.getInt("exprate");
                ret.mesoBoost = rs.getInt("mesorate");
                ret.dropBoost = rs.getInt("droprate");
                ret.bdropBoost = rs.getInt("bossrate");
                ret.textColour = rs.getByte("textcolour");
                ret.mission = rs.getByte("mission");
                doLoginMapCheck(ret);
                int partyid = rs.getInt("party");
                if (partyid >= 0) {
                    MapleParty party = null;
                    try {
                        party = client.getChannelServer().getWorldInterface().getParty(partyid);
                    } catch (RemoteException remoteException) {
                    }
                    if (party != null && party.getMemberById(ret.id) != null) {
                        ret.party = party;
                    }
                }
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, ret.accountid);
            rs = ps.executeQuery();
            byte gmm = 0;
            if (rs.next()) {
                ret.getClient().setAccountName(rs.getString("name"));
                ret.paypalNX = rs.getInt("paypalNX");
                ret.maplePoints = rs.getInt("maplePoints");
                ret.cardNX = rs.getInt("cardNX");
                ret.ninjatensu = rs.getByte("ninjatensu");
                ret.dpoints = rs.getShort("dpoints");
                ret.damount = rs.getShort("damount");
                ret.jqPoints = rs.getShort("jqpoints");
                ret.jqFinished = rs.getInt("jqfinished");
                ret.lastjq = rs.getByte("lastjq");
                ret.footnote = rs.getString("footnote");
                ret.village = rs.getByte("village");
                ret.gmLevel = rs.getByte("gm");
            }
            rs.close();
            ps.close();
            if ((ret.job == 900 || ret.job == 910) && gmm < 3) {
                ret.job = 0;
            }


            if (channelserver) {
                if (ret.isGenin()) {
                    ps = con.prepareStatement("SELECT skillid FROM autobuffs WHERE characterid = ?");
                    ps.setInt(1, charid);
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        ret.autobuffs.add(rs.getInt("skillid"));
                    }
                    rs.close();
                    ps.close();
                }


                ps = con.prepareStatement("SELECT * FROM inventoryitems LEFT JOIN inventoryequipment USING (inventoryitemid) WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();

                long expiration;
                MapleInventoryType type;
                Item item;
                MaplePet pet;

                while (rs.next()) {
                    type = MapleInventoryType.getByType(rs.getByte("inventorytype"));
                    expiration = rs.getLong("expiredate");

                    if (type.equals(MapleInventoryType.EQUIP) || type.equals(MapleInventoryType.EQUIPPED)) {
                        final Equip equip = new Equip(rs.getInt("itemid"), rs.getByte("position"), rs.getInt("ringid"), rs.getByte("flag"));
                        equip.setOwner(rs.getString("owner"));
                        equip.setQuantity(rs.getShort("quantity"));
                        equip.setAcc(rs.getShort("acc"));
                        equip.setAvoid(rs.getShort("avoid"));
                        equip.setDex(rs.getShort("dex"));
                        equip.setHands(rs.getShort("hands"));
                        equip.setHp(rs.getShort("hp"));
                        equip.setInt(rs.getShort("int"));
                        equip.setJump(rs.getShort("jump"));
                        equip.setLuk(rs.getShort("luk"));
                        equip.setMatk(rs.getShort("matk"));
                        equip.setMdef(rs.getShort("mdef"));
                        equip.setMp(rs.getShort("mp"));
                        equip.setSpeed(rs.getShort("speed"));
                        equip.setStr(rs.getShort("str"));
                        equip.setWatk(rs.getShort("watk"));
                        equip.setWdef(rs.getShort("wdef"));
                        equip.setItemLevel(rs.getByte("itemLevel"));
                        equip.setItemEXP(rs.getShort("itemEXP"));
                        equip.setViciousHammer(rs.getByte("ViciousHammer"));
                        equip.setUpgradeSlots(rs.getByte("upgradeslots"));
                        equip.setLevel(rs.getByte("level"));
                        equip.setExpiration(expiration);
                        equip.setGMLog(rs.getString("GM_Log"));
                        ret.getInventory(type).addFromDB(equip);
                    } else {
                        item = new Item(rs.getInt("itemid"), rs.getByte("position"), rs.getShort("quantity"), rs.getByte("flag"));
                        item.setOwner(rs.getString("owner"));
                        item.setExpiration(expiration);
                        item.setGMLog(rs.getString("GM_Log"));
                        ret.getInventory(type).addFromDB(item);

                        if (rs.getInt("petid") > -1) {
                            pet = MaplePet.loadFromDb(item.getItemId(), rs.getInt("petid"), item.getPosition());
                            ret.pets.add(pet);
                            item.setPet(pet);
                        }
                    }
                }
                rs.close();
                ps.close();
                if (channelserver) {
                    ret.maxSkills(false);
                    ps = con.prepareStatement("SELECT `key`,`type`,`action` FROM keymap WHERE characterid = ? AND kb = ?");
                    ps.setInt(1, charid);
                    byte kb = (byte) ((ret.GMSMode > 0) ? 1 : (ret.job > 910) ? 2 : 3);
                    ps.setByte(2, kb);
                    rs = ps.executeQuery();
                    final Map<Integer, MapleKeyBinding> keyb = ret.keylayout.Layout();
                    while (rs.next()) {
                        keyb.put(Integer.valueOf(rs.getInt("key")), new MapleKeyBinding(rs.getInt("type"), rs.getInt("action")));
                    }
                    rs.close();
                    ps.close();


                    ps = con.prepareStatement("SELECT * FROM skillmacros WHERE characterid = ? AND kb = ?");
                    ps.setInt(1, charid);
                    ps.setByte(2, kb);
                    rs = ps.executeQuery();
                    int position;
                    while (rs.next()) {
                        position = rs.getInt("position");
                        SkillMacro macro = new SkillMacro(rs.getInt("skill1"), rs.getInt("skill2"), rs.getInt("skill3"), rs.getString("name"), rs.getInt("shout"), position);
                        ret.skillMacros[position] = macro;
                    }
                    rs.close();
                    ps.close();



                    ps = con.prepareStatement("SELECT `locationtype`,`map` FROM savedlocations WHERE characterid = ?");
                    ps.setInt(1, charid);
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        ret.savedLocations[SavedLocationType.valueOf(rs.getString("locationtype")).ordinal()] = rs.getInt("map");
                    }
                    rs.close();
                    ps.close();


                    ps = con.prepareStatement("SELECT `characterid_to`,`when` FROM famelog WHERE characterid = ? AND DATEDIFF(NOW(),`when`) < 30");
                    ps.setInt(1, charid);
                    rs = ps.executeQuery();
                    ret.lastfametime = 0;
                    ret.lastmonthfameids = new ArrayList<Integer>(31);
                    while (rs.next()) {
                        ret.lastfametime = Math.max(ret.lastfametime, rs.getTimestamp("when").getTime());
                        ret.lastmonthfameids.add(Integer.valueOf(rs.getInt("characterid_to")));
                    }
                    rs.close();
                    ps.close();

                    ret.buddylist.loadFromDb(charid);
                    ret.storage = MapleStorage.loadStorage(ret.accountid);
                }
                ps = con.prepareStatement("SELECT sn FROM wishlist WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                int i = 0;
                while (rs.next()) {
                    ret.wishlist[i] = rs.getInt("sn");
                    i++;
                }
                while (i < 10) {
                    ret.wishlist[i] = 0;
                    i++;
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT mapid FROM trocklocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                int r = 0;
                while (rs.next()) {
                    ret.rocks[r] = rs.getInt("mapid");
                    r++;
                }
                while (r < 10) {
                    ret.rocks[r] = 999999999;
                    r++;
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT * FROM mountdata WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new RuntimeException("No mount data found on SQL column");
                }
                final IItem mount = ret.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -22);
                ret.mount = new MapleMount(ret, mount != null ? mount.getItemId() : 0, 1004, rs.getInt("Fatigue"), rs.getInt("Level"), rs.getInt("Exp"));
                ps.close();
                rs.close();

                ret.stats.recalcLocalStats();
                ret.silentEnforceMaxHpMp();
                ret.resetBattleshipHp();
            } else {
                ps = con.prepareStatement("SELECT * FROM inventoryitems LEFT JOIN inventoryequipment USING (inventoryitemid) WHERE characterid = ? AND inventorytype = -1");
                ps.setInt(1, charid);
                rs = ps.executeQuery();

                MapleInventoryType type;
                while (rs.next()) {
                    type = MapleInventoryType.getByType(rs.getByte("inventorytype"));
                    if (type.equals(MapleInventoryType.EQUIP) || type.equals(MapleInventoryType.EQUIPPED)) {
                        final Equip equip = new Equip(rs.getInt("itemid"), rs.getByte("position"), rs.getInt("ringid"), rs.getByte("flag"));
                        ret.getInventory(type).addFromDB(equip);
                    }
                }
                rs.close();
                ps.close();
            }
        } catch (SQLException ess) {
            ess.printStackTrace();
            System.out.println("Failed to load character..");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (pse != null) {
                    pse.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignore) {
            }
        }
        return ret;
    }

    public static MapleCharacter getDefault(MapleClient client, int chrid) {
        MapleCharacter ret = getDefault(client);
        ret.id = chrid;
        return ret;
    }

    public static MapleCharacter getDefault(MapleClient client) {
        MapleCharacter ret = new MapleCharacter(false);
        ret.client = client;
        ret.stats.hp = 50;
        ret.stats.mp = 50;
        ret.map = null;
        ret.stats.str = 69;
        ret.stats.dex = 69;
        ret.stats.luk = 69;
        ret.stats.int_ = 69;
        ret.exp = 0;
        ret.job = 0;
        ret.meso = 2000000000;
        ret.level = 30;
        ret.accountid = client.getAccID();
        ret.buddylist = new BuddyList(200);
        ret.cardNX = 0;
        ret.maplePoints = 0;
        ret.paypalNX = 0;
        ret.incs = false;
        ret.clonelimit = (byte) 0;
        ret.rasengan = 0;
        ret.legend = "nub";
        ret.pvpdeaths = 0;
        ret.pvpkills = 0;
        ret.storageAp = 69;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, ret.accountid);
            ResultSet rs;
            rs = ps.executeQuery();
            if (rs.next()) {
                ret.getClient().setAccountName(rs.getString("name"));
                ret.paypalNX = rs.getInt("paypalNX");
                ret.maplePoints = rs.getInt("maplePoints");
                ret.cardNX = rs.getInt("cardNX");
                ret.gmLevel = rs.getByte("gm");
                ret.ninjatensu = rs.getByte("ninjatensu");
                ret.dpoints = rs.getShort("dpoints");
                ret.damount = rs.getShort("damount");
                ret.jqPoints = rs.getShort("jqpoints");
                ret.jqFinished = rs.getInt("jqfinished");
                ret.footnote = rs.getString("footnote");
                ret.village = rs.getByte("village");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error loading account details from DB" + e);
        }
        ret.stats.recalcLocalStats();
        return ret;
    }

    public static void saveNewToDB(final MapleCharacter chr) {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = null;
        PreparedStatement pse = null;
        try {
            // clients should not be able to log back before their old state is saved (see MapleClient#getLoginState) so we are save to switch to a very low isolation level here
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            // connections are thread local now, no need to
            // synchronize anymore =)
            con.setAutoCommit(false);
            sql = "INSERT INTO characters " + "(`hair`, `face`, `accountid`, `name`, `defaultaccid`)" + //6
                    "VALUES (?, ?, ?, ?, ?);";
            ps = con.prepareStatement(sql); //6
            ps.setInt(1, chr.hair);
            ps.setInt(2, chr.face);
            ps.setInt(3, chr.accountid);
            ps.setString(4, chr.name);
            ps.setInt(5, chr.accountid);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                chr.id = rs.getInt(1);
            } else {
                rs.close();
                ps.close();
                throw new DatabaseException("Inserting char failed.");
            }
            rs.close();
            ps.close();


            ps = con.prepareStatement("INSERT INTO mountdata (characterid, `Level`, `Exp`, `Fatigue`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            ps.setInt(2, 1);
            ps.setInt(3, 0);
            ps.setInt(4, 0);
            ps.execute();
            ps.close();

            ps = con.prepareStatement("INSERT INTO inventoryitems (characterid, itemid, inventorytype, position, quantity, owner, GM_Log, petid, expiredate, flag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", DatabaseConnection.RETURN_GENERATED_KEYS);
            pse = con.prepareStatement("INSERT INTO inventoryequipment VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            IEquip equip;

            for (final MapleInventory iv : chr.inventory) {
                ps.setInt(3, iv.getType().getType());
                for (final IItem item : iv.list()) {
                    ps.setInt(1, chr.id);
                    ps.setInt(2, item.getItemId());
                    ps.setInt(4, item.getPosition());
                    ps.setInt(5, item.getQuantity());
                    ps.setString(6, item.getOwner());
                    ps.setString(7, item.getGMLog());
                    ps.setInt(8, -1); // Pet cant be loaded on logins + new char doesn't have.
                    ps.setLong(9, item.getExpiration());
                    ps.setByte(10, item.getFlag());
                    ps.executeUpdate();

                    rs = ps.getGeneratedKeys();
                    int itemid;
                    if (rs.next()) {
                        itemid = rs.getInt(1);
                    } else {
                        throw new DatabaseException("Inserting char failed.");
                    }
                    rs.close();

                    if (iv.getType().equals(MapleInventoryType.EQUIP) || iv.getType().equals(MapleInventoryType.EQUIPPED)) {
                        pse.setInt(1, itemid);
                        equip = (IEquip) item;
                        pse.setInt(2, equip.getUpgradeSlots());
                        pse.setInt(3, equip.getLevel());
                        pse.setInt(4, equip.getStr());
                        pse.setInt(5, equip.getDex());
                        pse.setInt(6, equip.getInt());
                        pse.setInt(7, equip.getLuk());
                        pse.setInt(8, equip.getHp());
                        pse.setInt(9, equip.getMp());
                        pse.setInt(10, equip.getWatk());
                        pse.setInt(11, equip.getMatk());
                        pse.setInt(12, equip.getWdef());
                        pse.setInt(13, equip.getMdef());
                        pse.setInt(14, equip.getAcc());
                        pse.setInt(15, equip.getAvoid());
                        pse.setInt(16, equip.getHands());
                        pse.setInt(17, equip.getSpeed());
                        pse.setInt(18, equip.getJump());
                        pse.setInt(19, equip.getRingId());
                        pse.setInt(20, equip.getViciousHammer());
                        pse.setInt(21, 0);
                        pse.setInt(22, 0);
                        pse.executeUpdate();
                    }
                }
            }
            ps.close();
            pse.close();

            final int[] array1 = {2, 3, 4, 5, 6, 7, 16, 17, 18, 19, 23, 25, 26, 27, 31, 34, 37, 38, 41, 44, 45, 46, 50, 57, 59, 60, 61, 62, 63, 64, 65, 8, 9, 24, 30};
            final int[] array2 = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 4, 5, 6, 6, 6, 6, 6, 6, 6, 4, 4, 4, 4};
            final int[] array3 = {10, 12, 13, 18, 6, 11, 8, 5, 0, 4, 1, 19, 14, 15, 3, 17, 9, 20, 22, 50, 51, 52, 7, 53, 100, 101, 102, 103, 104, 105, 106, 16, 23, 24, 2};

            ps = con.prepareStatement("INSERT INTO keymap (characterid, `key`, `type`, `action`, `kb`) VALUES (?, ?, ?, ?, ?)");
            for (byte z = 1; z < 4; z++) {
                for (byte i = 0; i < array1.length; i++) {
                    ps.setInt(1, chr.id);
                    ps.setInt(2, array1[i]);
                    ps.setInt(3, array2[i]);
                    ps.setInt(4, array3[i]);
                    ps.setByte(5, z);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            ps.close();

            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[charsave] Error saving character data");
            try {
                con.rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
                System.err.println("[charsave] Error Rolling Back");
            }
        } finally {
            try {
                if (pse != null) {
                    pse.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                con.setAutoCommit(true);
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (SQLException e) {
                System.err.println("[charsave] Error going back to autocommit mode" + e);
            }
        }
    }

    public void saveToDB(boolean dc, boolean fromcs) {
        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        String sql = null;
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);
            sql = "UPDATE characters SET" + " `level` = ?, `fame` = ?, `str` = ?, `dex` = ?, `luk` = ?," + //5
                    " `int` = ?, `exp` = ?, `hp` = ?, `mp` = ?, `storageap` = ?," + //10
                    " `ap` = ?, `skincolor` = ?, `job` = ?, `hair` = ?," + //14
                    " `face` = ?, `map` = ?, `meso` = ?, `spawnpoint` = ?, `party` = ?," + //19
                    " `reborns` = ?, `mobkilled` = ?, `bosskilled` = ?, `mutality` =?, `clonelimit` = ?," +//24
                    " `legend` = ?, `msi` = ?, `name` = ?, `pvpdeaths` = ?," + //28
                    " `pvpkills` = ?, `prefixshit` = ?, `autoap` = ?, `lmpoints` = ?, `taocheck` = ?, `gmsmode` = ?," + //34
                    " `lastdojostage` = ? , `dojopoints` = ?, `smega` = ?, `bqpoints` = ?," + //38
                    " `kpqpoints` = ?, `textcolour` = ?, `mission` = ?," +//41
                    " `exprate` = ?, `mesorate` = ?, `droprate` = ?, `bossrate` = ?," + // 45
                    " `maxhp` = ?, `maxmp` = ?" + //47
                    //      " `matchcardwins` = ?, `matchcardlosses` = ?, `matchcardties` = ?, `omokwins` = ?, `omoklosses` = ?, `omokties` = ?" + //46
                    " WHERE id = ?"; //48
         /*   sql = "INSERT INTO characters VALUES (`level`, `fame`, `str`, `dex`, `luk`," + //5
            " `int`, `exp`, `hp`, `mp`, `storageap`," + //10
            " `ap`, `skincolor`, `job`, `hair`," + //14
            " `face`, `map`, `meso`, `spawnpoint`, `party`," + //19
            " `reborns`, `mobkilled`, `bosskilled`, `mutality` =?, `clonelimit`," +//24
            " `legend`, `msi`, `name`, `pvpdeaths`," + //28
            " `pvpkills`, `prefixshit`, `autoap`, `shuriken`, `taocheck`, `gmsmode`," + //34
            " `lastdojostage` = ? , `dojopoints`, `smega`, `bqpoints`," + //38
            " `clan`, `kpqpoints`, `textcolour`)"*/
            ps = con.prepareStatement(sql);
            ps.setInt(1, level);
            ps.setInt(2, fame);
            ps.setInt(3, stats.str);
            ps.setInt(4, stats.dex);
            ps.setInt(5, stats.luk);
            ps.setInt(6, stats.int_);
            ps.setInt(7, exp); // exp
            ps.setInt(8, stats.hp);
            ps.setInt(9, stats.mp);
            ps.setInt(10, storageAp);
            ps.setInt(11, remainingAp);
            ps.setInt(12, skinColor);
            ps.setInt(13, job);
            ps.setInt(14, hair);
            ps.setInt(15, face);
            ps.setInt(16, map.getId()); // map
            ps.setInt(17, meso); // meso
            ps.setInt(18, initialSpawnPoint);
            if (party != null) {
                ps.setInt(19, party.getId());
            } else {
                ps.setInt(19, -1);
            }
            ps.setInt(20, reborn);
            ps.setInt(21, mobkilled);
            ps.setInt(22, bosskilled);
            ps.setByte(23, mutality);
            ps.setByte(24, clonelimit);
            ps.setString(25, legend);
            ps.setByte(26, maxstatitem);
            ps.setString(27, name);
            ps.setInt(28, pvpdeaths);
            ps.setInt(29, pvpkills);
            ps.setByte(30, prefixshit);
            ps.setByte(31, autoap);
            ps.setInt(32, lmpoints);
            int itemcount = getTaoOfSight();
            int difference = itemcount - taocheck;
            if ((difference > 100 && reborn < 5) || (difference > 1000 && reborn < 25) || (difference > 10000 && reborn < 100)) {
                if (isJounin()) {
                    client.getChannelServer().broadcastStaffPacket(MaplePacketCreator.serverNotice(1, "[Report] " + name + " has received " + difference + " more chickens more than he originated since his last save."));
                }
            }
            ps.setInt(33, itemcount);
            ps.setByte(34, GMSMode);
            ps.setInt(35, lastDojoStage);
            ps.setInt(36, dojoPoints);
            ps.setByte(37, smega);
            ps.setInt(38, bossPoints);
            ps.setInt(39, kpqpoints);
            ps.setByte(40, textColour);
            ps.setByte(41, mission);
            ps.setInt(42, expBoost);
            ps.setInt(43, mesoBoost);
            ps.setInt(44, dropBoost);
            ps.setInt(45, bdropBoost);
            ps.setInt(46, stats.maxhp);
            ps.setInt(47, stats.maxmp);
            ps.setInt(48, id);
            int updateRows = ps.executeUpdate();
            if (updateRows < 1) {
                throw new DatabaseException("Character not in database (" + id + ")");
            }
            for (final MaplePet petz : pets) {
                if (petz.getSummoned()) {
                    petz.saveToDb(); // Only save those summoned :P
                }
            }

            deleteWhereCharacterId(con, "DELETE FROM inventoryitems WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO inventoryitems (characterid, itemid, inventorytype, position, quantity, owner, GM_Log, petid, expiredate, flag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", DatabaseConnection.RETURN_GENERATED_KEYS);
            pse = con.prepareStatement("INSERT INTO inventoryequipment VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            for (final MapleInventory iv : inventory) {
                ps.setInt(3, iv.getType().getType());
                for (final IItem item : iv.list()) {
                    ps.setInt(1, id);
                    ps.setInt(2, item.getItemId());
                    ps.setInt(4, item.getPosition());
                    ps.setInt(5, item.getQuantity());
                    ps.setString(6, item.getOwner());
                    ps.setString(7, item.getGMLog());
                    ps.setInt(8, item.getPet() != null ? item.getPet().getUniqueId() : -1);
                    ps.setLong(9, item.getExpiration());
                    ps.setByte(10, item.getFlag());
                    ps.executeUpdate();

                    rs = ps.getGeneratedKeys();
                    int itemid;
                    if (rs.next()) {
                        itemid = rs.getInt(1);
                    } else {
                        throw new DatabaseException("Inserting char failed.");
                    }

                    if (iv.getType().equals(MapleInventoryType.EQUIP) || iv.getType().equals(MapleInventoryType.EQUIPPED)) {
                        pse.setInt(1, itemid);
                        IEquip equip = (IEquip) item;
                        pse.setInt(2, equip.getUpgradeSlots());
                        pse.setInt(3, equip.getLevel());
                        pse.setInt(4, equip.getStr());
                        pse.setInt(5, equip.getDex());
                        pse.setInt(6, equip.getInt());
                        pse.setInt(7, equip.getLuk());
                        pse.setInt(8, equip.getHp());
                        pse.setInt(9, equip.getMp());
                        pse.setInt(10, equip.getWatk());
                        pse.setInt(11, equip.getMatk());
                        pse.setInt(12, equip.getWdef());
                        pse.setInt(13, equip.getMdef());
                        pse.setInt(14, equip.getAcc());
                        pse.setInt(15, equip.getAvoid());
                        pse.setInt(16, equip.getHands());
                        pse.setInt(17, equip.getSpeed());
                        pse.setInt(18, equip.getJump());
                        pse.setInt(19, equip.getRingId());
                        pse.setInt(20, equip.getViciousHammer());
                        pse.setInt(21, equip.getItemLevel());
                        pse.setInt(22, equip.getItemEXP());
                        pse.executeUpdate();
                    }
                }
            }
            ps.close();
            pse.close();

            if (dc && getAllCoolDowns().size() > 0) {
                for (final PlayerCoolDownValueHolder cooling : getAllCoolDowns()) {
                    ps = con.prepareStatement("INSERT INTO skills_cooldowns (charid, SkillID, StartTime, length) VALUES (?, ?, ?, ?)");
                    ps.setInt(1, getId());
                    ps.setInt(2, cooling.skillId);
                    ps.setLong(3, cooling.startTime);
                    ps.setLong(4, cooling.length);
                    ps.addBatch();
                }
                ps.executeBatch();
                ps.close();
            }



            byte kb = (byte) ((this.GMSMode > 0) ? 1 : (this.job > 910) ? 2 : 3);
            keylayout.saveKeys(id, kb);


            deleteWhereCharacterId(con, "DELETE FROM skillmacros WHERE characterid = ?");
            for (int i = 0; i < 5; i++) {
                SkillMacro macro = skillMacros[i];
                if (macro != null) {
                    ps = con.prepareStatement("INSERT INTO skillmacros" + " (characterid, skill1, skill2, skill3, name, shout, position, kb) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                    ps.setInt(1, id);
                    ps.setInt(2, macro.getSkill1());
                    ps.setInt(3, macro.getSkill2());
                    ps.setInt(4, macro.getSkill3());
                    ps.setString(5, macro.getName());
                    ps.setInt(6, macro.getShout());
                    ps.setInt(7, i);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            ps.close();


            deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO savedlocations (characterid, `locationtype`, `map`) VALUES (?, ?, ?)");
            ps.setInt(1, id);
            for (final SavedLocationType savedLocationType : SavedLocationType.values()) {
                if (savedLocations[savedLocationType.ordinal()] != -1) {
                    ps.setString(2, savedLocationType.name());
                    ps.setInt(3, savedLocations[savedLocationType.ordinal()]);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            ps.close();

            mount.saveMount(id);

            deleteWhereCharacterId(con, "DELETE FROM buddies WHERE characterid = ? AND pending = 0");
            ps = con.prepareStatement("INSERT INTO buddies (characterid, `buddyid`, `group`, `pending`) VALUES (?, ?, ?, 0)");
            ps.setInt(1, id);
            for (BuddylistEntry entry : buddylist.getBuddies()) {
                if (entry.isVisible()) {
                    ps.setInt(2, entry.getCharacterId());
                    ps.setString(3, entry.getGroup());
                    ps.addBatch();
                }
            }
            ps.executeBatch();

            if (autobuffchange) {
                deleteWhereCharacterId(con, "DELETE FROM autobuffs WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO autobuffs (characterid, skillid) VALUES (?, ?)");
                ps.setInt(1, id);
                for (int buff : autobuffs) {
                    ps.setInt(2, buff);
                    ps.addBatch();
                }
                ps.executeBatch();
                ps.close();
            }

            ps = con.prepareStatement("UPDATE accounts SET `cardNX` = ?, `maplePoints` = ?, `paypalNX` = ?, `village` = ?, `ninjatensu` = ?, `dpoints` = ?, `damount` = ?, `jqfinished` = ?, `jqpoints` = ?, `lastjq` = ? WHERE id = ?");
            ps.setInt(1, cardNX);
            ps.setInt(2, maplePoints);
            ps.setInt(3, paypalNX);
            ps.setInt(4, village);
            ps.setInt(5, ninjatensu);
            ps.setShort(6, dpoints);
            ps.setShort(7, damount);
            ps.setInt(8, jqFinished);
            ps.setShort(9, jqPoints);
            ps.setInt(10, lastjq);
            ps.setInt(11, client.getAccID());
            ps.executeUpdate();

            if (storage != null) {
                storage.saveToDB();
            }

            deleteWhereCharacterId(con, "DELETE FROM wishlist WHERE characterid = ?");
            for (int i = 0; i < getWishlistSize(); i++) {
                ps = con.prepareStatement("INSERT INTO wishlist(characterid, sn) VALUES(?, ?) ");
                ps.setInt(1, getId());
                ps.setInt(2, wishlist[i]);
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();

            con.commit();
        } catch (Exception e) {
            System.err.println("[charsave] Error saving character data" + e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.err.println("[charsave] Error Rolling back" + e1);
            }
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                con.setAutoCommit(true);
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (SQLException e) {
                System.err.println("[charsave] Error going back to autocommit mode" + e);
            }
        }
    }

    private void deleteWhereCharacterId(Connection con, String sql) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }

    public final void QuestInfoPacket(final MaplePacketLittleEndianWriter mplew) {
        mplew.writeShort(questinfo.size());
        for (final Entry<Integer, String> q : questinfo.entrySet()) {
            mplew.writeShort(q.getKey());
            mplew.writeMapleAsciiString(q.getValue() == null ? "" : q.getValue());
        }
    }

    public final void updateInfoQuest(final int questid, final String data) {
        questinfo.put(questid, data);
        client.getSession().write(MaplePacketCreator.updateInfoQuest(questid, data));
    }

    public final String getInfoQuest(final int questid) {
        if (questinfo.containsKey(id)) {
            return questinfo.get(questid);
        }
        return "";
    }

    public final List<MapleQuestStatus> getStartedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<MapleQuestStatus>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 1 && !q.isCustomQuest()) {
                ret.add(q);
            }
        }
        return Collections.unmodifiableList(ret);
    }

    public final List<MapleQuestStatus> getCompletedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<MapleQuestStatus>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 2 && !q.isCustomQuest()) {
                ret.add(q);
            }
        }
        return Collections.unmodifiableList(ret);
    }

    public final int getNumQuest() {
        int i = 0;
        for (final MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 2 && !q.isCustomQuest()) {
                i++;
            }
        }
        return i;
    }

    public final byte getQuestStatus(final int quest) {
        for (final MapleQuestStatus q : quests.values()) {
            if (q.getQuest().getId() == quest) {
                return q.getStatus();
            }
        }
        return 0;
    }

    public final MapleQuestStatus getQuest(final MapleQuest quest) {
        if (!quests.containsKey(quest)) {
            return new MapleQuestStatus(quest, (byte) 0);
        }
        return quests.get(quest);
    }

    public final MapleQuestStatus getQuestNAdd(final MapleQuest quest) {
        if (!quests.containsKey(quest)) {
            final MapleQuestStatus status = new MapleQuestStatus(quest, (byte) 0);
            quests.put(quest, status);
            return status;
        }
        return quests.get(quest);
    }

    public final void updateQuest(final MapleQuestStatus quest) {
        quests.put(quest.getQuest(), quest);
        if (!quest.isCustomQuest()) {

            if (quest.getStatus() == 1) {
                client.getSession().write(MaplePacketCreator.startQuest(this, (short) quest.getQuest().getId()));
                client.getSession().write(MaplePacketCreator.updateQuestInfo(this, (short) quest.getQuest().getId(), quest.getNpc(), (byte) 8));
            } else if (quest.getStatus() == 2) {
                client.getSession().write(MaplePacketCreator.completeQuest(this, (short) quest.getQuest().getId()));
            } else if (quest.getStatus() == 0) {
                client.getSession().write(MaplePacketCreator.forfeitQuest(this, (short) quest.getQuest().getId()));
            }
        }
    }

    public final Map<Integer, String> getInfoQuest_Map() {
        return questinfo;
    }

    public final Map<MapleQuest, MapleQuestStatus> getQuest_Map() {
        return quests;
    }

    public static int getIdByName(String name) {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("SELECT id FROM characters WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            int id = rs.getInt("id");
            rs.close();
            ps.close();
            return id;
        } catch (SQLException e) {
            System.err.println("Error getting id by name from DB" + e);
        }
        return -1;
    }

    public boolean isActiveBuffedValue(int skillid) {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skillid) {
                return true;
            }
        }
        return false;
    }

    public Integer getBuffedValue(MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : Integer.valueOf(mbsvh.value);
    }

    public final Integer getBuffedSkill_X(final MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : mbsvh.effect.getX();
    }

    public final Integer getBuffedSkill_Y(final MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : mbsvh.effect.getY();
    }

    public final MapleStatEffect getBuffedSkillEffect(final MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : mbsvh.effect;
    }

    public boolean isBuffFrom(MapleBuffStat stat, ISkill skill) {
        final MapleBuffStatValueHolder mbsvh = effects.get(stat);
        if (mbsvh == null) {
            return false;
        }
        return mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skill.getId();
    }

    public int getBuffSource(MapleBuffStat stat) {
        final MapleBuffStatValueHolder mbsvh = effects.get(stat);
        return mbsvh == null ? -1 : mbsvh.effect.getSourceId();
    }

    public void setBuffedValue(MapleBuffStat effect, int value) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return;
        }
        mbsvh.value = value;
    }

    public Long getBuffedStarttime(MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : Long.valueOf(mbsvh.startTime);
    }

    public MapleStatEffect getStatForBuff(MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : mbsvh.effect;
    }

    private void prepareDragonBlood(final MapleStatEffect bloodEffect) {
        if (dragonBloodSchedule != null) {
            dragonBloodSchedule.cancel(false);
        }
        dragonBloodSchedule = TimerManager.getInstance().register(new Runnable() {

            public void run() {
                addHP(-bloodEffect.getX());
                getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(bloodEffect.getSourceId(), 5));
                getMap().broadcastMessage(MapleCharacter.this, MaplePacketCreator.showBuffeffect(getId(), bloodEffect.getSourceId(), 5), false);
            }
        }, 4000, 4000);
    }

    public void startFullnessSchedule(final int decrease, final MaplePet pet, int petSlot) {
        ScheduledFuture<?> schedule = TimerManager.getInstance().register(new Runnable() {

            @Override
            public void run() {
                int newFullness = pet.getFullness() - decrease;
                if (newFullness <= 5) {
                    pet.setFullness(15);
                    unequipPet(pet, true, true);
                } else {
                    pet.setFullness(newFullness);
                    client.getSession().write(PetPacket.updatePet(pet, true));
                }
            }
        }, 60000, 60000);
        switch (petSlot) {
            case 0:
                fullnessSchedule = schedule;
                break;
            case 1:
                fullnessSchedule_1 = schedule;
                break;
            case 2:
                fullnessSchedule_2 = schedule;
                break;
        }
    }

    public void cancelFullnessSchedule(int petSlot) {
        switch (petSlot) {
            case 0:
                if (fullnessSchedule != null) {
                    fullnessSchedule.cancel(false);
                }
                break;
            case 1:
                if (fullnessSchedule_1 != null) {
                    fullnessSchedule_1.cancel(false);
                }
                break;
            case 2:
                if (fullnessSchedule_2 != null) {
                    fullnessSchedule_2.cancel(false);
                }
                break;
        }
    }

    public void startMapTimeLimitTask(int time, final MapleMap to) {
        client.getSession().write(MaplePacketCreator.getClock(time));

        time *= 1000;
        mapTimeLimitTask = TimerManager.getInstance().register(new Runnable() {

            @Override
            public void run() {
                changeMap(to, to.getPortal(0));
            }
        }, time, time);
    }

    public void startFishingTask(final boolean VIP) {
        final int time = VIP ? 30000 : 60000;
        cancelFishingTask();

        fishing = TimerManager.getInstance().register(new Runnable() {

            @Override
            public void run() {
                if (!haveItem(2300000, 1, false, true)) {
                    cancelFishingTask();
                    return;
                }
                MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, 2300000, 1, false, false);

                final int randval = RandomRewards.getInstance().getFishingReward();

                switch (randval) {
                    case 0: // Meso
                        final int money = Randomizer.rand(10, 50000);
                        gainMeso(money, true);
                        //client.getSession().write(UIPacket.fishingUpdate((byte) 1, money));
                        break;
                    case 1: // EXP
                        final int experi = Randomizer.nextInt(ExpTable.getExpNeededForLevel(level) / 200);
                        gainExp(experi, true, false, true);
                        //client.getSession().write(UIPacket.fishingUpdate((byte) 2, experi));
                        break;
                    default:
                        MapleInventoryManipulator.addById(client, randval, (short) 1);
                        //client.getSession().write(UIPacket.fishingUpdate((byte) 0, randval));
                        break;
                }
                //map.broadcastMessage(UIPacket.fishingCaught(id));
            }
        }, time, time);
    }

    public void cancelMapTimeLimitTask() {
        if (mapTimeLimitTask != null) {
            mapTimeLimitTask.cancel(false);
        }
    }

    public void cancelFishingTask() {
        if (fishing != null) {
            fishing.cancel(false);
        }
    }

    public void registerEffect(MapleStatEffect effect, long starttime, ScheduledFuture<?> schedule) {
        if (effect.isHide()) {
            this.hidden = true;
            getClient().getSession().write(MaplePacketCreator.sendGMOperation(16, 1));
            getMap().broadcastMessage(this, MaplePacketCreator.removePlayerFromMap(getId()), false);
        } else if (effect.isDragonBlood()) {
            prepareDragonBlood(effect);
        } else if (effect.isBerserk()) {
            checkBerserk();
        } else if (effect.isBeholder()) {
            prepareBeholderEffect();
        }

        for (Pair<MapleBuffStat, Integer> statup : effect.getStatups()) {
            effects.put(statup.getLeft(), new MapleBuffStatValueHolder(effect, starttime, schedule, statup.getRight()));
        }
        stats.recalcLocalStats();
    }

    public List<MapleBuffStat> getBuffStats(final MapleStatEffect effect, final long startTime) {
        final List<MapleBuffStat> bstats = new ArrayList<MapleBuffStat>();

        for (Entry<MapleBuffStat, MapleBuffStatValueHolder> stateffect : effects.entrySet()) {
            final MapleBuffStatValueHolder mbsvh = stateffect.getValue();
            if (mbsvh.effect.sameSource(effect) && (startTime == -1 || startTime == mbsvh.startTime)) {
                bstats.add(stateffect.getKey());
            }
        }
        return bstats;
    }

    private void deregisterBuffStats(List<MapleBuffStat> stats) {
        List<MapleBuffStatValueHolder> effectsToCancel = new ArrayList<MapleBuffStatValueHolder>(stats.size());
        for (MapleBuffStat stat : stats) {
            final MapleBuffStatValueHolder mbsvh = effects.get(stat);
            if (mbsvh != null) {
                effects.remove(stat);
                boolean addMbsvh = true;
                for (MapleBuffStatValueHolder contained : effectsToCancel) {
                    if (mbsvh.startTime == contained.startTime && contained.effect == mbsvh.effect) {
                        addMbsvh = false;
                    }
                }
                if (addMbsvh) {
                    effectsToCancel.add(mbsvh);
                }
                if (stat == MapleBuffStat.SUMMON || stat == MapleBuffStat.PUPPET) {
                    final int summonId = mbsvh.effect.getSourceId();
                    final MapleSummon summon = summons.get(summonId);
                    if (summon != null) {
                        map.broadcastMessage(MaplePacketCreator.removeSummon(summon, true));
                        map.removeMapObject(summon);
                        removeVisibleMapObject(summon);
                        summons.remove(summonId);
                    }
                    if (summon.getSkill() == 1321007) {
                        if (beholderHealingSchedule != null) {
                            beholderHealingSchedule.cancel(false);
                            beholderHealingSchedule = null;
                        }
                        if (beholderBuffSchedule != null) {
                            beholderBuffSchedule.cancel(false);
                            beholderBuffSchedule = null;
                        }
                    }
                } else if (stat == MapleBuffStat.DRAGONBLOOD) {
                    if (dragonBloodSchedule != null) {
                        dragonBloodSchedule.cancel(false);
                        dragonBloodSchedule = null;
                    }
                }
            }
        }
        for (MapleBuffStatValueHolder cancelEffectCancelTasks : effectsToCancel) {
            if (getBuffStats(cancelEffectCancelTasks.effect, cancelEffectCancelTasks.startTime).size() == 0) {
                if (cancelEffectCancelTasks.schedule != null) {
                    cancelEffectCancelTasks.schedule.cancel(false);
                }
            }
        }
    }

    /**
     * cancel effect
     * @param effect
     * @param overwrite when overwrite is set no data is sent and all the Buffstats in the StatEffect are deregistered
     * @param startTime
     */
    public void cancelEffect(MapleStatEffect effect, boolean overwrite, long startTime) {
        List<MapleBuffStat> buffstats;
        if (!overwrite) {
            buffstats = getBuffStats(effect, startTime);
        } else {
            List<Pair<MapleBuffStat, Integer>> statups = effect.getStatups();
            buffstats = new ArrayList<MapleBuffStat>(statups.size());
            for (Pair<MapleBuffStat, Integer> statup : statups) {
                buffstats.add(statup.getLeft());
            }
        }
        deregisterBuffStats(buffstats);
        if (effect.isMagicDoor()) {
            // remove for all on maps
            if (!getDoors().isEmpty()) {
                final MapleDoor door = getDoors().iterator().next();
                for (final MapleCharacter chr : door.getTarget().getCharacters()) {
                    door.sendDestroyData(chr.getClient());
                }
                for (final MapleCharacter chr : door.getTown().getCharacters()) {
                    door.sendDestroyData(chr.getClient());
                }
                for (final MapleDoor destroyDoor : getDoors()) {
                    door.getTarget().removeMapObject(destroyDoor);
                    door.getTown().removeMapObject(destroyDoor);
                }
                clearDoors();
                silentPartyUpdate();
            }
        } else if (effect.isMonsterRiding()) {
//	    if (effect.getSourceId() != 5221006) {
//		getMount().cancelSchedule();
//	    }
        } else if (effect.isAranCombo()) {
            combo = 0;
        }
        // check if we are still logged in o.o
        if (!overwrite) {
            cancelPlayerBuffs(buffstats);
            if (effect.isHide() && (MapleCharacter) map.getMapObject(getObjectId()) != null) {
                this.hidden = false;
                map.broadcastMessage(this, MaplePacketCreator.spawnPlayerMapobject(this), false);

                for (final MaplePet pett : pets) {
                    if (pett.getSummoned()) {
                        map.broadcastMessage(this, PetPacket.showPet(this, pett, false, false), false);
                    }
                }
            }
        }
    }

    public void cancelBuffStats(MapleBuffStat... stat) {
        List<MapleBuffStat> buffStatList = Arrays.asList(stat);
        deregisterBuffStats(buffStatList);
        cancelPlayerBuffs(buffStatList);
    }

    public void cancelEffectFromBuffStat(MapleBuffStat stat) {
        cancelEffect(effects.get(stat).effect, false, -1);
    }

    private void cancelPlayerBuffs(List<MapleBuffStat> buffstats) {
        if (getClient().getChannelServer().getPlayerStorage().getCharacterById(getId()) != null) {
            stats.recalcLocalStats();
            enforceMaxHpMp();

            getClient().getSession().write(MaplePacketCreator.cancelBuff(buffstats));
            getMap().broadcastMessage(this, MaplePacketCreator.cancelForeignBuff(getId(), buffstats), false);
        }

    }

    public void dispel() {
        if (isJounin() || isHidden()) {
            return;
        }
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isSkill()) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
            }

        }
    }

    public void cancelAllBuffs_() {
        effects.clear();
    }

    public void cancelAllBuffs() {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
        }

        cancelBuffStats(MapleBuffStat.SUMMON);
    }

    public void cancelMorphs() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(effects.values());

        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            switch (mbsvh.effect.getSourceId()) {
                case 5111005:
                case 5121003:
                case 15111002:
                case 13111005:
                    return; // Since we can't have more than 1, save up on loops
                default:
                    if (mbsvh.effect.isMorph()) {
                        cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                        continue;
                    }
            }
        }
    }

    public int getMorphState() {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isMorph()) {
                return mbsvh.effect.getSourceId();
            }
        }
        return -1;
    }

    public void silentGiveBuffs(List<PlayerBuffValueHolder> buffs) {
        for (PlayerBuffValueHolder mbsvh : buffs) {
            mbsvh.effect.silentApplyBuff(this, mbsvh.startTime);
        }

    }

    public List<PlayerBuffValueHolder> getAllBuffs() {
        List<PlayerBuffValueHolder> ret = new ArrayList<PlayerBuffValueHolder>();
        for (MapleBuffStatValueHolder mbsvh : effects.values()) {
            ret.add(new PlayerBuffValueHolder(mbsvh.startTime, mbsvh.effect));
        }

        return ret;
    }

    public List<PlayerBuffValueHolder> getAllFakeBuffs() {
        List<PlayerBuffValueHolder> ret = new ArrayList<PlayerBuffValueHolder>();
        for (MapleBuffStatValueHolder mbsvh : effects.values()) {
            if (mbsvh.effect.isMonsterRiding() || mbsvh.effect.isMorph()) {
                ret.add(new PlayerBuffValueHolder(mbsvh.startTime, mbsvh.effect));
            }

        }
        return ret;
    }

    public void cancelMagicDoor() {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isMagicDoor()) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
            }

        }
    }

    public void setEnergyBar(int set) {
        energybar = set;
    }

    public final void handleEnergyCharge(final int skillid, final byte targets) {
        final ISkill echskill = SkillFactory.getSkill(skillid);
        final byte skilllevel = getSkillLevel(echskill);
        if (skilllevel > 0) {
            if (targets > 0) {
                if (getBuffedValue(MapleBuffStat.ENERGY_CHARGE) == null) {
                    echskill.getEffect(skilllevel).applyEnergyBuff(this, true); // Infinity time
                } else {
                    Integer energyLevel = getBuffedValue(MapleBuffStat.ENERGY_CHARGE);

                    if (energyLevel < 10000) {
                        energyLevel += (100 * targets);
                        setBuffedValue(MapleBuffStat.ENERGY_CHARGE, energyLevel);
                        client.getSession().write(MaplePacketCreator.giveEnergyChargeTest(energyLevel));

                        if (energyLevel >= 10000) {
                            energyLevel = 10001;
                        }
                    } else if (energyLevel == 10001) {
                        echskill.getEffect(skilllevel).applyEnergyBuff(this, false); // One with time
                        energyLevel = 10002;
                    }
                }
            }
        }
    }

    public final void handleOrbgain() {
        int orbcount = getBuffedValue(MapleBuffStat.COMBO);
        MapleStatEffect ceffect = SkillFactory.getSkill(1120003).getEffect(30);
        if (orbcount < ceffect.getX() + 1) {
            int neworbcount = orbcount + 1;
            if (ceffect.makeChanceResult()) {
                if (neworbcount < ceffect.getX() + 1) {
                    neworbcount++;
                }
            }
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO, neworbcount));
            setBuffedValue(MapleBuffStat.COMBO, neworbcount);
            int duration = ceffect.getDuration();
            duration += (int) ((getBuffedStarttime(MapleBuffStat.COMBO) - System.currentTimeMillis()));

            client.getSession().write(MaplePacketCreator.giveBuff(1111002, duration, stat, ceffect));
            map.broadcastMessage(this, MaplePacketCreator.giveForeignBuff(getId(), stat, ceffect), false);
        }
    }

    public void handleOrbconsume() {
        MapleStatEffect ceffect = SkillFactory.getSkill(1111002).getEffect(30);
        List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO, 1));
        setBuffedValue(MapleBuffStat.COMBO, 1);
        int duration = ceffect.getDuration();
        duration += (int) ((getBuffedStarttime(MapleBuffStat.COMBO) - System.currentTimeMillis()));
        client.getSession().write(MaplePacketCreator.giveBuff(1111002, duration, stat, ceffect));
        map.broadcastMessage(this, MaplePacketCreator.giveForeignBuff(getId(), stat, ceffect), false);
    }

    public void checkBerserk() {
        if (BerserkSchedule != null) {
            BerserkSchedule.cancel(false);
            BerserkSchedule = null;
        }

        ISkill BerserkX = SkillFactory.getSkill(1320006);
        final int skilllevel = getSkillLevel(BerserkX);
        if (skilllevel >= 1) {
            MapleStatEffect ampStat = BerserkX.getEffect(skilllevel);

            if (stats.getHp() * 100 / stats.getMaxHp() > ampStat.getX()) {
                Berserk = false;
            } else {
                Berserk = true;
            }

            client.getSession().write(MaplePacketCreator.showOwnBerserk(skilllevel, Berserk));
            map.broadcastMessage(this, MaplePacketCreator.showBerserk(getId(), skilllevel, Berserk), false);

            BerserkSchedule = TimerManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    checkBerserk();
                }
            }, 10000);
        }
    }

    private void prepareBeholderEffect() {
        if (beholderHealingSchedule != null) {
            beholderHealingSchedule.cancel(false);
        }
        if (beholderBuffSchedule != null) {
            beholderBuffSchedule.cancel(false);
        }
        ISkill bHealing = SkillFactory.getSkill(1320008);
        int bHealingLvl = getSkillLevel(bHealing);
        if (bHealingLvl > 0) {
            final MapleStatEffect healEffect = bHealing.getEffect(bHealingLvl);
            int healInterval = healEffect.getX() * 1000;
            beholderHealingSchedule = TimerManager.getInstance().register(new Runnable() {

                @Override
                public void run() {
                    addHP(healEffect.getHp());
                    client.getSession().write(MaplePacketCreator.showOwnBuffEffect(1321007, 2));
                    map.broadcastMessage(MapleCharacter.this, MaplePacketCreator.summonSkill(getId(), 1321007, 5), true);
                    map.broadcastMessage(MapleCharacter.this, MaplePacketCreator.showBuffeffect(getId(), 1321007, 2), false);
                }
            }, healInterval, healInterval);
        }
        ISkill bBuff = SkillFactory.getSkill(1320009);
        int bBuffLvl = getSkillLevel(bBuff);
        if (bBuffLvl > 0) {
            final MapleStatEffect buffEffect = bBuff.getEffect(bBuffLvl);
            int buffInterval = buffEffect.getX() * 1000;
            beholderBuffSchedule = TimerManager.getInstance().register(new Runnable() {

                @Override
                public void run() {
                    buffEffect.applyTo(MapleCharacter.this);
                    client.getSession().write(MaplePacketCreator.showOwnBuffEffect(1321007, 2));
                    map.broadcastMessage(MapleCharacter.this, MaplePacketCreator.summonSkill(getId(), 1321007, (int) (Math.random() * 3) + 6), true);
                    map.broadcastMessage(MapleCharacter.this, MaplePacketCreator.showBuffeffect(getId(), 1321007, 2), false);
                }
            }, buffInterval, buffInterval);
        }
    }

    private void silentEnforceMaxHpMp() {
        stats.setMp(stats.getMp());
        stats.setHp(stats.getHp(), true);
    }

    private void enforceMaxHpMp() {
        List<Pair<MapleStat, Integer>> statups = new ArrayList<Pair<MapleStat, Integer>>(2);
        if (stats.getMp() > stats.getCurrentMaxMp()) {
            stats.setMp(stats.getMp());
            statups.add(new Pair<MapleStat, Integer>(MapleStat.MP, Integer.valueOf(stats.getMp())));
        }
        if (stats.getHp() > stats.getCurrentMaxHp()) {
            stats.setHp(stats.getHp());
            statups.add(new Pair<MapleStat, Integer>(MapleStat.HP, Integer.valueOf(stats.getHp())));
        }
        if (statups.size() > 0) {
            client.getSession().write(MaplePacketCreator.updatePlayerStats(statups));
        }
    }

    public MapleMap getMap() {
        return map;
    }

    public void setMap(MapleMap newmap) {
        this.map = newmap;
    }

    public int getMapId() {
        if (map != null) {
            return map.getId();
        }
        return mapid;
    }

    public int getInitialSpawnpoint() {
        return initialSpawnPoint;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreateDate() {
        return createdate;
    }

    public String getPreviousNames() {
        return previousnames;
    }

    public void addPreviousNames(String namez) {
        previousnames += " || ";
        previousnames += namez;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        try {
            String sql = "UPDATE `characters` SET `previousnames` = ? WHERE `id` = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, previousnames);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public short getLevel() {
        return level;
    }

    public int getFame() {
        return fame;
    }

    public MapleClient getClient() {
        return client;
    }

    public int getExp() {
        return exp;
    }

    public int getRemainingAp() {
        return remainingAp;
    }

    public int getRemainingSp() {
        return 32767;
    }

    public boolean isHidden() {
        return hidden;
    }

    public int getSkinColor() {
        return skinColor;
    }

    public int getJob() {
        return job;
    }

    public int getGender() {
        return 2;
    }

    public int getHair() {
        return hair;
    }

    public int getFace() {
        return face;
    }

    public void setJob(int job) {
        this.job = job;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHair(int hair) {
        this.hair = hair;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public void setRemainingAp(int remainingAp) {
        this.remainingAp = remainingAp;
    }

    public void setSkinColor(int skinColor) {
        this.skinColor = skinColor;
    }

    public CheatTracker getCheatTracker() {
        return anticheat;
    }

    public BuddyList getBuddylist() {
        return buddylist;
    }

    public void addFame(int famechange) {
        this.fame += famechange;
        if (fame > 1337 && !haveItem(1142003, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(client, 1142003, name, (short) 1337, (short) 50, (short) 50);
            dropMessage("[The Elite ninja Gang] You have gained a Celebrity for reaching 1337 Fame");
        } else if (fame > 13337 && !haveItem(1142006, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(client, 1142006, name, (short) 13337, (short) 100, (short) 100);
            dropMessage("[The Elite ninja Gang] You have gained a MapleIdol for reaching 13337 Fame");
        }
    }

    public void gainFame() {
        this.fame++;
    }

    public void setFame(int fuck) {
        this.fame += fuck;
    }

    public void changeMapBanish(final int mapid, final String portal, final String msg) {
        dropMessage(5, msg);
        final MapleMap mapp = client.getChannelServer().getMapFactory().getMap(mapid);
        changeMap(mapp, mapp.getPortal(portal));
    }

    public void changeMap(final MapleMap to, final Point pos) {
        MaplePacket warpPacket = MaplePacketCreator.getWarpToMap(to, 0x80, this);
        changeMapInternal(to, pos, warpPacket);
    }

    public void changeMap(final MapleMap to, final MaplePortal pto) {
        MaplePacket warpPacket = MaplePacketCreator.getWarpToMap(to, pto.getId(), this);
        changeMapInternal(to, pto.getPosition(), warpPacket);
    }

    private void changeMapInternal(final MapleMap to, final Point pos, MaplePacket warpPacket) {
        if (NPCScriptManager.getInstance().getCM(client) != null) {
            NPCScriptManager.getInstance().dispose(client);
        }
        if (eventInstance != null) {
            eventInstance.changedMap(this, to.getId());
        }
        removeClones();
        if (isChunin() && !noHide) {
            this.hide();
        }
        if (isHidden() && !noHide) {
            getClient().getSession().write(MaplePacketCreator.sendGMOperation(16, 1));
        }
        client.getSession().write(warpPacket);
        map.removePlayer(this);
        if (client.getChannelServer().getPlayerStorage().getCharacterById(getId()) != null) {
            map = to;
            setPosition(pos);
            to.addPlayer(this);
            stats.relocHeal();
        }
    }

    public void leaveMap() {
        controlled.clear();
        visibleMapObjects.clear();
        if (chair != 0) {
            cancelFishingTask();
            chair = 0;
        }
        if (hpDecreaseTask != null) {
            hpDecreaseTask.cancel(false);
        }
        cancelMapTimeLimitTask();
    }

    public void changeJob(MapleJob newJob) {
        this.job = newJob.getId();
        updateSingleStat(MapleStat.JOB, job);
        getMap().broadcastMessage(this, MaplePacketCreator.showJobChange(getId()), false);
        this.maxSkills(false);
        silentPartyUpdate();
        guildUpdate();
        if (newJob.isA(MapleJob.NOBLESSE) && !haveItem(1142065, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(client, 1142065, name, (short) 69, (short) 1, (short) 1);
            showMessage(1, "Congratulations on acheiving noblesse Medal!");
        }
    }

    public void gainAp(int ap) {
        if (this.remainingAp + ap > 32767) {
            dropMessage("You tried to get more than 32767 AP so you got scammed");
            ap = 32767 - this.remainingAp;
        }
        this.remainingAp += ap;
        updateSingleStat(MapleStat.AVAILABLEAP, this.remainingAp);
    }

    public void changeSkillLevel(ISkill skill, byte newLevel, byte newMasterlevel) {
        skills.put(skill, new SkillEntry(newLevel, newMasterlevel));
        this.getClient().getSession().write(MaplePacketCreator.updateSkill(skill.getId(), newLevel, newMasterlevel));
    }

    public void updatePartyMemberHP() {
        if (party != null) {
            int channel = client.getChannel();
            for (MaplePartyCharacter partychar : party.getMembers()) {
                if (partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                    MapleCharacter other = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(partychar.getName());
                    if (other != null) {
                        other.getClient().getSession().write(
                                MaplePacketCreator.updatePartyMemberHP(getId(), stats.hp, stats.getCurrentMaxHp()));
                    }

                }
            }
        }
    }

    public void receivePartyMemberHP() {
        if (party != null) {
            int channel = client.getChannel();
            for (MaplePartyCharacter partychar : party.getMembers()) {
                if (partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                    MapleCharacter other = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(partychar.getName());
                    if (other != null) {
                        getClient().getSession().write(
                                MaplePacketCreator.updatePartyMemberHP(other.getId(), other.stats.getHp(), other.stats.getCurrentMaxHp()));
                    }

                }
            }
        }
    }

    /**
     * Convenience function which adds the supplied parameter to the current hp then directly does a updateSingleStat.
     *
     * @see MapleCharacter#setHp(int)
     * @param delta
     */
    public void addHP(int delta) {
        if (stats.setHp(stats.getHp() + delta)) {
            updateSingleStat(MapleStat.HP, stats.getHp());
        }
    }

    /**
     * Convenience function which adds the supplied parameter to the current mp then directly does a updateSingleStat.
     *
     * @see MapleCharacter#setMp(int)
     * @param delta
     */
    public void addMP(int delta) {
        if (stats.setMp(stats.getMp() + delta)) {
            updateSingleStat(MapleStat.MP, stats.getMp());
        }
    }

    public void addMPHP(int hpDiff, int mpDiff) {
        List<Pair<MapleStat, Integer>> statups = new ArrayList<Pair<MapleStat, Integer>>();

        if (stats.setHp(stats.getHp() + hpDiff)) {
            statups.add(new Pair<MapleStat, Integer>(MapleStat.HP, Integer.valueOf(stats.getHp())));
        }
        if (stats.setMp(stats.getMp() + mpDiff)) {
            statups.add(new Pair<MapleStat, Integer>(MapleStat.MP, Integer.valueOf(stats.getMp())));
        }
        if (statups.size() > 0) {
            client.getSession().write(MaplePacketCreator.updatePlayerStats(statups));
        }
    }

    /**
     * Updates a single stat of this MapleCharacter for the client. This method only creates and sends an update packet,
     * it does not update the stat stored in this MapleCharacter instance.
     *
     * @param stat
     * @param newval
     * @param itemReaction
     */
    public void updateSingleStat(MapleStat stat, int newval, boolean itemReaction) {
        Pair<MapleStat, Integer> statpair = new Pair<MapleStat, Integer>(stat, newval);
        MaplePacket updatePacket = MaplePacketCreator.updatePlayerStats(Collections.singletonList(statpair), itemReaction);
        client.getSession().write(updatePacket);
    }

    public void updateSingleStat(MapleStat stat, int newval) {
        updateSingleStat(stat, newval, false);
    }

    public void gainExp(int gain, boolean show, boolean inChat, boolean white, short partymembers) {
        if (rebirthing) {
            return;
        }
        int maxlevel = 255;
        if (level == maxlevel) {
            setExp(0);
            updateSingleStat(MapleStat.EXP, 0);
            return;
        }
        if (getExp() < 0) {
            client.showMessage(5, "You had negative EXP and now its Fixed by Janet the Pervie ninja.");
            levelUp();
            updateSingleStat(MapleStat.LEVEL, level);
            setExp(0);
            updateSingleStat(MapleStat.EXP, 0);
        }
        if (level < maxlevel) {
            if ((long) this.exp + (long) gain > (long) Integer.MAX_VALUE) {
                int gainFirst = ExpTable.getExpNeededForLevel(level) - this.exp;
                gain -= gainFirst + 1;
                this.gainExp(gainFirst + 1, false, inChat, white, partymembers);
            }
            updateSingleStat(MapleStat.EXP, this.exp + gain);
        } else {
            return;
        }
        if (show && gain != 0) {
            client.getSession().write(MaplePacketCreator.getShowExpGain(gain, inChat, white, partymembers));
        }

        if (exp >= ExpTable.getExpNeededForLevel(level) && level < maxlevel) {
            while (level < maxlevel && exp >= ExpTable.getExpNeededForLevel(level)) {
                levelUp();
            }
        }
    }

    public int getMaxLevel() {
        int reborns = this.getReborns();
        if (reborns < 10) {
            return 200;
        } else if (reborns < 50) {
            return 205;
        } else if (reborns < 100) {
            return 210;
        } else if (reborns < 200) {
            return 215;
        } else if (reborns < 400) {
            return 220;
        } else if (reborns < 800) {
            return 225;
        } else if (reborns < 1200) {
            return 230;
        } else if (reborns < 1600) {
            return 235;
        } else if (reborns < 2000) {
            return 240;
        } else if (reborns < 2500) {
            return 250;
        } else {
            return 255;
        }

    }

    public void silentPartyUpdate() {
        if (party != null) {
            try {
                client.getChannelServer().getWorldInterface().updateParty(party.getId(), PartyOperation.SILENT_UPDATE, new MaplePartyCharacter(this));
            } catch (RemoteException e) {
                System.err.println("REMOTE THROW, silentPartyUpdate" + e);
                client.getChannelServer().reconnectWorld();
            }
        }
    }

    public void gainExp(int gain, boolean show, boolean inChat) {
        gainExp(gain, show, inChat, true);
    }

    public void gainExp(int gain, boolean show, boolean inChat, boolean white) {
        gainExp(gain, show, inChat, white, (short) 0);
    }

    public MapleInventory getInventory(
            MapleInventoryType type) {
        return inventory[type.ordinal()];
    }

    public final MapleInventory[] getInventorys() {
        return inventory;
    }

    public MapleShop getShop() {
        return shop;
    }

    public void setShop(MapleShop shop) {
        this.shop = shop;
    }

    public int getMeso() {
        return meso;
    }

    public final int[] getSavedLocations() {
        return savedLocations;
    }

    public int getSavedLocation(SavedLocationType type) {
        return savedLocations[type.ordinal()];
    }

    public void saveLocation(SavedLocationType type) {
        savedLocations[type.ordinal()] = getMapId();
    }

    public void clearSavedLocation(SavedLocationType type) {
        savedLocations[type.ordinal()] = -1;
    }

    public void gainMeso(int gain, boolean show) {
        gainMeso(gain, show, false, false);
    }

    public void setMeso(int amt) {
        meso = amt;
    }

    public void gainMeso(int gain, boolean show, boolean enableActions) {
        gainMeso(gain, show, enableActions, false);
    }

    public void gainMeso(int gain, boolean show, boolean enableActions, boolean inChat) {
        if (meso + gain < 0) {
            client.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int newVal = meso + gain;
        updateSingleStat(MapleStat.MESO, newVal, enableActions);
        if (show) {
            client.getSession().write(MaplePacketCreator.getShowMesoGain(gain, inChat));
        }
    }

    public void controlMonster(MapleMonster monster, boolean aggro) {
        monster.setController(this);
        controlled.add(monster);
        client.getSession().write(MobPacket.controlMonster(monster, false, aggro));
    }

    public void stopControllingMonster(MapleMonster monster) {
        controlled.remove(monster);
    }

    public void checkMonsterAggro(MapleMonster monster) {
        if (monster.getController() == this) {
            monster.setControllerHasAggro(true);
        } else {
            monster.switchController(this, true);
        }
    }

    public Collection<MapleMonster> getControlledMonsters() {
        return Collections.unmodifiableCollection(controlled);
    }

    @Override
    public String toString() {
        return "Character: " + this.name;
    }

    public int getAccountID() {
        return accountid;
    }

    public final PlayerStats getStat() {
        return stats;
    }

    public void playerDead() {
        deaths++;
        if (getEventInstance() != null) {
            getEventInstance().playerKilled(this);
        }
        dispelSkill(0);
        if (getBuffedValue(MapleBuffStat.MORPH) != null) {
            cancelEffectFromBuffStat(MapleBuffStat.MORPH);
        } else if (getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
            cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
        }

        if (reborn > 50) {
            //lol
            //TODO deathpenalty
        }
        this.updateSingleStat(MapleStat.EXP, this.exp);
    }

    public void mobKilled() {
        this.mobkilled++;
        if (mobkilled > 250000 && !haveItem(1142004, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(client, 1142004, name, (short) 1337, (short) 25, (short) 25);
            dropMessage("[The Elite ninja Gang] You have gained a veteran hunter medal for killing 250000 monsters");
        }
    }

    public void bossKilled() {
        this.bosskilled++;
        if (bosskilled > 500000 && !haveItem(1142005, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(client, 1142005, name, (short) 13337, (short) 50, (short) 50);
            dropMessage("[The Elite ninja Gang] You have gained a Legendary hunter for killing 500000 boss monsters");
        }
    }

    public int getMobKilled() {
        return this.mobkilled;
    }

    public int getBossKilled() {
        return this.bosskilled;
    }

    public Map<ISkill, SkillEntry> getSkills() {
        return Collections.unmodifiableMap(skills);
    }

    public byte getSkillLevel(ISkill skill) {
        SkillEntry ret = skills.get(skill);
        if (ret == null) {
            return 0;
        }
        return ret.skillevel;
    }

    public int getMasterLevel(ISkill skill) {
        SkillEntry ret = skills.get(skill);
        if (ret == null) {
            return 0;
        }
        return ret.masterlevel;
    }

    public void levelUp() {
        if (this.level >= 255) {
            this.level = 255;
            exp = 0;
            return;
        }
        if (level < 0) {
            this.level = 1;
            exp = 0;
            return;
        }
        if (this.level < this.getMaxLevel()) {
            byte gain = getApPerLevel();
            stats.doAutoAp(gain);
        } else {
            if (level % 5 == 0) {
                dropMessage("You are over the required Level for your Rebirth. So you will not gain Any AP");
            }
        }
        int maxhp = stats.getMaxHp();
        int maxmp = stats.getMaxMp();
        if (job == 0 || job == 1000 || job == 2000) { // Beginner
            maxhp += Randomizer.rand(12, 16);
            maxmp += Randomizer.rand(10, 12);
        } else if (job >= 100 && job <= 132) { // Warrior
            final ISkill improvingMaxHP = SkillFactory.getSkill(1000001);
            final int slevel = getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(24, 28);
            maxmp += Randomizer.rand(4, 6);
        } else if (job >= 200 && job <= 232) { // Magician
            final ISkill improvingMaxMP = SkillFactory.getSkill(2000001);
            final int slevel = getSkillLevel(improvingMaxMP);
            if (slevel > 0) {
                maxmp += improvingMaxMP.getEffect(slevel).getX() * 2;
            }
            maxhp += Randomizer.rand(10, 14);
            maxmp += Randomizer.rand(22, 24);
        } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 422) || (job >= 1300 && job <= 1311) || (job >= 1400 && job <= 1411)) { // Bowman, Thief, Wind Breaker and Night Walker
            maxhp += Randomizer.rand(20, 24);
            maxmp += Randomizer.rand(14, 16);
        } else if (job >= 500 && job <= 522) { // Pirate
            final ISkill improvingMaxHP = SkillFactory.getSkill(5100000);
            final int slevel = getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(22, 26);
            maxmp += Randomizer.rand(18, 22);
        } else if (job >= 1100 && job <= 1111) { // Soul Master
            final ISkill improvingMaxHP = SkillFactory.getSkill(11000000);
            final int slevel = getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(24, 28);
            maxmp += Randomizer.rand(4, 6);
        } else if (job >= 1200 && job <= 1211) { // Flame Wizard
            final ISkill improvingMaxMP = SkillFactory.getSkill(12000000);
            final int slevel = getSkillLevel(improvingMaxMP);
            if (slevel > 0) {
                maxmp += improvingMaxMP.getEffect(slevel).getX() * 2;
            }
            maxhp += Randomizer.rand(10, 14);
            maxmp += Randomizer.rand(22, 24);
        } else if (job >= 1500 && job <= 1512) { // Pirate
            final ISkill improvingMaxHP = SkillFactory.getSkill(15100000);
            final int slevel = getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(22, 26);
            maxmp += Randomizer.rand(18, 22);
        } else if (job >= 2100 && job <= 2112) { // Aran
            maxhp += Randomizer.rand(50, 52);
            maxmp += Randomizer.rand(4, 6);
        } else { // GameMaster
            maxhp += Randomizer.rand(50, 100);
            maxmp += Randomizer.rand(50, 100);
        }
        maxmp += stats.getTotalInt() / 10;
        exp -= ExpTable.getExpNeededForLevel(level);
        level += 1;
        maxhp = Math.min(30000, maxhp);
        maxmp = Math.min(30000, maxmp);
        final List<Pair<MapleStat, Integer>> statup = new ArrayList<Pair<MapleStat, Integer>>(8);
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MAXHP, maxhp));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MAXMP, maxmp));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.HP, maxhp));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MP, maxmp));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.EXP, exp));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.LEVEL, (int) level));
        if (job != 0 && job != 1000 && job != 2000) { // Not Beginner, Nobless and Legend
            remainingSp += 3;
            statup.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLESP, Integer.valueOf(remainingSp)));
        } else {
            if (level <= 10) {
                stats.setStr(stats.getStr() + remainingAp);
                remainingAp = 0;
                statup.add(new Pair<MapleStat, Integer>(MapleStat.STR, stats.getStr()));
            }
        }
        statup.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, remainingAp));

        stats.setMaxHp(maxhp);
        stats.setMaxMp(maxmp);
        stats.setHp(maxhp);
        stats.setMp(maxmp);
        client.getSession().write(MaplePacketCreator.updatePlayerStats(statup));
        map.broadcastMessage(this, MaplePacketCreator.showForeignEffect(getId(), 0), false);
        stats.recalcLocalStats();
        silentPartyUpdate();
        guildUpdate();
    }

    public void changeKeybinding(int key, MapleKeyBinding keybinding) {
        if (keybinding.getType() != 0) {
            keylayout.Layout().put(Integer.valueOf(key), keybinding);
        } else {
            keylayout.Layout().remove(Integer.valueOf(key));
        }
    }

    public final MapleKeyLayout getKeyLayout() {
        return this.keylayout;
    }

    public void sendMacros() {
        for (int i = 0; i < 5; i++) {
            if (skillMacros[i] != null) {
                client.getSession().write(MaplePacketCreator.getMacros(skillMacros));
                break;
            }
        }
    }

    public SkillMacro[] getSkillMacros() {
        return skillMacros;
    }

    public void updateMacros(int position, SkillMacro updateMacro) {
        skillMacros[position] = updateMacro;
        macrochange = true;
    }

    public void tempban(String reason, Calendar duration, int greason) {
        if (lastmonthfameids == null) {
            throw new RuntimeException("Trying to ban a non-loaded character (testhack)");
        }

        tempban(reason, duration, greason, client.getAccID());
        client.getSession().close();
    }

    public static boolean tempban(String reason, Calendar duration, int greason, int accountid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET tempban = ?, banreason = ?, greason = ? WHERE id = ?");
            Timestamp TS = new Timestamp(duration.getTimeInMillis());
            ps.setTimestamp(1, TS);
            ps.setString(2, reason);
            ps.setInt(3, greason);
            ps.setInt(4, accountid);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException ex) {
            System.err.println("Error while Tempbanning..." + ex);
        }

        return false;
    }

    public void ban(String reason) {
        if (lastmonthfameids == null) {
            throw new RuntimeException("Trying to ban a non-loaded character (testhack)");
        }
        try {
            getClient().banMacs();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE id = ?");
            ps.setInt(1, 1);
            ps.setString(2, reason);
            ps.setInt(3, accountid);
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
            ps.setString(1, client.getSessionIPAddress());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("Error in Banning" + ex);
        }

        client.getSession().close();
    }

    public static boolean ban(String id, String reason, boolean accountId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;

            if (id.matches("/[0-9]{1,3}\\..*")) {
                ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, id);
                ps.executeUpdate();
                ps.close();
                return true;
            }

            if (accountId) {
                ps = con.prepareStatement("SELECT id FROM accounts WHERE name = ?");
            } else {
                ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            }

            boolean ret = false;
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PreparedStatement psb = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE id = ?");
                psb.setString(1, reason);
                psb.setInt(2, rs.getInt(1));
                psb.executeUpdate();
                psb.close();
                ret =
                        true;
            }

            rs.close();
            ps.close();
            return ret;
        } catch (SQLException ex) {
            System.err.println("Error while banning" + ex);
        }

        return false;
    }

    /**
     * Oid of players is always = the cid
     */
    @Override
    public int getObjectId() {
        return getId();
    }

    /**
     * Throws unsupported operation exception, oid of players is read only
     */
    @Override
    public void setObjectId(int id) {
        throw new UnsupportedOperationException();
    }

    public MapleStorage getStorage() {
        return storage;
    }

    public void addVisibleMapObject(MapleMapObject mo) {
        visibleMapObjects.add(mo);
    }

    public void removeVisibleMapObject(MapleMapObject mo) {
        visibleMapObjects.remove(mo);
    }

    public boolean isMapObjectVisible(MapleMapObject mo) {
        return visibleMapObjects.contains(mo);
    }

    public Collection<MapleMapObject> getVisibleMapObjects() {
        return Collections.unmodifiableCollection(visibleMapObjects);
    }

    public boolean isAlive() {
        return stats.hp > 0;
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.getSession().write(MaplePacketCreator.removePlayerFromMap(this.getObjectId()));
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        if (!isHidden()) {
            client.getSession().write(MaplePacketCreator.spawnPlayerMapobject(this));

            for (final MaplePet peet : pets) {
                if (peet.getSummoned()) {
                    client.getSession().write(PetPacket.showPet(this, peet, false, false));
                }
            }
        }
    }

    public void equipChanged() {
        getMap().broadcastMessage(this, MaplePacketCreator.updateCharLook(this), false);
        stats.recalcLocalStats();
        enforceMaxHpMp();
        if (getClient().getPlayer().getMessenger() != null) {
            WorldChannelInterface wci = ChannelServer.getInstance(getClient().getChannel()).getWorldInterface();
            try {
                wci.updateMessenger(getClient().getPlayer().getMessenger().getId(), getClient().getPlayer().getName(), getClient().getChannel());
            } catch (RemoteException e) {
                getClient().getChannelServer().reconnectWorld();
            }

        }
    }

    public final MaplePet getPet(final int index) {
        byte count = 0;
        for (final MaplePet petz : pets) {
            if (petz.getSummoned()) {
                if (count == index) {
                    return petz;
                }
                count++;
            }
        }
        return null;
    }

    public void addPet(final MaplePet pet) {
        pets.remove(pet);
        pets.add(pet);
        // So that the pet will be at the last
        // Pet index logic :(
    }

    public void removePet(MaplePet pet, boolean shiftLeft) {
        pet.setSummoned(false);
        /*	int slot = -1;
        for (int i = 0; i < 3; i++) {
        if (pets[i] != null) {
        if (pets[i].getUniqueId() == pet.getUniqueId()) {
        pets[i] = null;
        slot = i;
        break;
        }
        }
        }
        if (shiftLeft) {
        if (slot > -1) {
        for (int i = slot; i < 3; i++) {
        if (i != 2) {
        pets[i] = pets[i + 1];
        } else {
        pets[i] = null;
        }
        }
        }
        }*/
    }

    public final byte getPetIndex(final MaplePet petz) {
        byte count = 0;
        for (final MaplePet petzz : pets) {
            if (petzz.getSummoned()) {
                if (petzz == petz) {
                    return count;
                }
                count++;
            }
        }
        return -1;
    }

    public final byte getPetIndex(final int petId) {
        byte count = 0;
        for (final MaplePet pett : pets) {
            if (pett.getSummoned()) {
                if (pett.getUniqueId() == petId) {
                    return count;
                }
                count++;
            }
        }
        return -1;
    }

    public final List<MaplePet> getPets() {
        return pets;
    }

    public final void unequipAllPets() {
        for (final MaplePet petz : pets) {
            if (petz != null) {
                unequipPet(petz, true, false);
            }
        }
    }

    public void unequipPet(MaplePet pet, boolean shiftLeft, boolean hunger) {
        cancelFullnessSchedule(getPetIndex(pet));
        pet.saveToDb();
        map.broadcastMessage(this, PetPacket.showPet(this, pet, true, hunger), true);
        //   List<Pair<MapleStat, Integer>> statss = new ArrayList<Pair<MapleStat, Integer>>();
        // statss.add(new Pair<MapleStat, Integer>(MapleStat.PET, Integer.valueOf(0)));
        client.getSession().write(PetPacket.petStatUpdate(this));
        client.getSession().write(MaplePacketCreator.enableActions());
        removePet(pet, shiftLeft);
    }

    public final long getLastFameTime() {
        return lastfametime;
    }

    public final List<Integer> getFamedCharacters() {
        return lastmonthfameids;
    }

    public FameStatus canGiveFame(MapleCharacter from) {
        if (lastfametime >= System.currentTimeMillis() - 60 * 60 * 24 * 1000) {
            return FameStatus.NOT_TODAY;
        } else if (lastmonthfameids.contains(Integer.valueOf(from.getId()))) {
            return FameStatus.NOT_THIS_MONTH;
        }
        return FameStatus.OK;
    }

    public void hasGivenFame(MapleCharacter to) {
        lastfametime = System.currentTimeMillis();
        lastmonthfameids.add(Integer.valueOf(to.getId()));
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO famelog (characterid, characterid_to) VALUES (?, ?)");
            ps.setInt(1, getId());
            ps.setInt(2, to.getId());
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.err.println("ERROR writing famelog for char " + getName() + " to " + to.getName() + e);
        }
    }

    public MapleParty getParty() {
        return party;
    }

    public int getPartyId() {
        return (party != null ? party.getId() : -1);
    }

    public int getWorld() {
        return world;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    public void setParty(MapleParty party) {
        this.party = party;
    }

    public MapleTrade getTrade() {
        return trade;
    }

    public void setTrade(MapleTrade trade) {
        this.trade = trade;
    }

    public EventInstanceManager getEventInstance() {
        return eventInstance;
    }

    public void setEventInstance(EventInstanceManager eventInstance) {
        this.eventInstance = eventInstance;
    }

    public void addDoor(MapleDoor door) {
        doors.add(door);
    }

    public void clearDoors() {
        doors.clear();
    }

    public List<MapleDoor> getDoors() {
        return new ArrayList<MapleDoor>(doors);
    }

    public boolean canDoor() {
        return canDoor;
    }

    public void disableDoor() {
        canDoor = false;
        TimerManager tMan = TimerManager.getInstance();
        tMan.schedule(new Runnable() {

            @Override
            public void run() {
                canDoor = true;
            }
        }, 5000);
    }

    public Map<Integer, MapleSummon> getSummons() {
        return summons;
    }

    public int getChair() {
        return chair;
    }

    public int getItemEffect() {
        return itemEffect;
    }

    public void setChair(int chair) {
        this.chair = chair;
    }

    public void setItemEffect(int itemEffect) {
        this.itemEffect = itemEffect;
    }

    @Override
    public Collection<MapleInventory> allInventories() {
        return Arrays.asList(inventory);
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.PLAYER;
    }

    public MapleGuild getGuild() {
        try {
            return getClient().getChannelServer().getWorldInterface().getGuild(getGuildId(), null);
        } catch (RemoteException ex) {
            client.getChannelServer().reconnectWorld();
        }
        return null;
    }

    public int getGuildId() {
        return guildid;
    }

    public byte getGuildRank() {
        return guildrank;
    }

    public void setGuildId(int _id) {
        if (isfake) {
            return;
        }
        guildid = _id;
        if (guildid > 0) {
            if (mgc == null) {
                mgc = new MapleGuildCharacter(this);
            } else {
                mgc.setGuildId(guildid);
            }

        } else {
            mgc = null;
        }
    }

    public void setAllianceRank(int rank) {
        allianceRank = (byte) rank;
    }

    public byte getAllianceRank() {
        return this.allianceRank;
    }

    public MapleAlliance getAlliance() {
        return alliance;
    }

    public void setGuildRank(int _rank) {
        guildrank = (byte) _rank;
        if (mgc != null) {
            mgc.setGuildRank(_rank);
        }
    }

    public MapleGuildCharacter getMGC() {
        return mgc;
    }

    public void guildUpdate() {
        if (guildid <= 0) {
            return;
        }
        mgc.setReborns((short) level);
        mgc.setJobId(job);
        try {
            client.getChannelServer().getWorldInterface().memberLevelJobUpdate(mgc);
        } catch (RemoteException re) {
            System.err.println("RemoteExcept while trying to update level/job in guild." + re);
        }
    }

    public void saveGuildStatus() {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE characters SET guildid = ?, guildrank = ? WHERE id = ?");
            ps.setInt(1, guildid);
            ps.setInt(2, guildrank);
            ps.setInt(3, id);
            ps.execute();
            ps.close();
        } catch (SQLException se) {
            System.err.println("SQL error: " + se.getLocalizedMessage() + se);
        }
    }

    /**
     * Allows you to change someone's NXCash, Maple Points, and Gift Tokens!
     *
     * Created by Acrylic/Penguins
     *
     * @param type: 0 = NX, 1 = MP, 2 = GT
     * @param quantity: how much to modify it by. Negatives subtract points, Positives add points.
     */
    public void addCSPoints(int type, int quantity) {
        modifyCSPoints(type, quantity);
    }

    public void modifyCSPoints(int type, int quantity) {
        if (type == 0) {
            this.cardNX += quantity;
        } else if (type == 1) {
            this.maplePoints += quantity;
        } else if (type == 2) {
            this.paypalNX += quantity;
        }
    }

    public int getCSPoints(int type) {
        if (type == 0) {
            return this.cardNX;
        } else if (type == 1) {
            return this.maplePoints;
        } else if (type == 2) {
            return this.paypalNX;
        } else {
            return 0;
        }
    }

    public boolean haveItem(int itemid, int quantity) {
        return haveItem(itemid, quantity, false, true);
    }

    public boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
        MapleInventoryType type = InventoryConstants.getInventoryType(itemid);
        MapleInventory iv = inventory[type.ordinal()];
        int possesed = iv.countById(itemid);
        if (checkEquipped) {
            possesed += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        if (greaterOrEquals) {
            return possesed >= quantity;
        } else {
            return possesed == quantity;
        }
    }

    public void setLeetness(boolean b) {
        this.leet = b;
    }

    public boolean getLeetness() {
        return leet;
    }

    public void goHome() {
        if (getMute() == 1) {
            unMute();
        }
        if (this.getEventInstance() != null) {
            this.getEventInstance().playerDisconnected(this);
        }
        changeMap(Village.getHomeTown(village), 0);
    }

    public byte getMaxStatItems() {
        return maxstatitem;
    }

    public void setMaxStatItem(byte fuck) {
        this.maxstatitem = fuck;
    }

    public void addMaxStatItem() {
        this.maxstatitem++;
    }

    public void wipeStats() {
        stats.setAllStats(4);
    }

    public boolean hasAllStatMax() {
        if (stats.getStr() != 32767) {
            dropMessage("Little birdie told me that you don't have 32767 Str.");
            return false;
        } else if (stats.getDex() != 32767) {
            dropMessage("Little birdie told me that you don't have 32767 Dex.");
            return false;
        } else if (stats.getInt() != 32767) {
            dropMessage("Little birdie told me that you don't have 32767 Int.");
            return false;
        } else if (stats.getLuk() != 32767) {
            dropMessage("Little birdie told me that you don't have 32767 Luk.");
            return false;
        }
        return true;
    }

    public boolean checkSpace(int fuck) {
        return checkSpace(fuck, 1);
    }

    public boolean checkSpace(int itemid, int quantity) {
        return MapleInventoryManipulator.checkSpace(getClient(), itemid, quantity, "");
    }

    public enum FameStatus {

        OK, NOT_TODAY, NOT_THIS_MONTH
    }

    public int getBuddyCapacity() {
        return buddylist.getCapacity();
    }

    public void setBuddyCapacity(int capacity) {
        buddylist.setCapacity(capacity);
        client.getSession().write(MaplePacketCreator.updateBuddyCapacity(capacity));
    }

    public MapleMessenger getMessenger() {
        return messenger;
    }

    public void setMessenger(MapleMessenger messenger) {
        this.messenger = messenger;
    }

    public void checkMessenger() {
        if (messenger != null && messengerposition < 4 && messengerposition > -1) {
            try {
                WorldChannelInterface wci = ChannelServer.getInstance(client.getChannel()).getWorldInterface();
                MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(client.getPlayer(), messengerposition);
                wci.silentJoinMessenger(messenger.getId(), messengerplayer, messengerposition);
                wci.updateMessenger(getClient().getPlayer().getMessenger().getId(), getClient().getPlayer().getName(), getClient().getChannel());
            } catch (RemoteException e) {
                client.getChannelServer().reconnectWorld();
            }
        }
    }

    public int getMessengerPosition() {
        return messengerposition;
    }

    public void setMessengerPosition(int position) {
        this.messengerposition = position;
    }

    public int hasEXPCard() {
        return 1;
    }

    public boolean getNXCodeValid(String code, boolean validcode) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT `valid` FROM nxcode WHERE code = ?");
        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            validcode = rs.getInt("valid") != 0;
        }

        rs.close();
        ps.close();
        return validcode;
    }

    public int getNXCodeType(String code) throws SQLException {
        int type = -1;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT `type` FROM nxcode WHERE code = ?");
        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            type = rs.getInt("type");
        }
        rs.close();
        ps.close();
        return type;
    }

    public int getNXCodeItem(String code) throws SQLException {
        int item = -1;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT `item` FROM nxcode WHERE code = ?");
        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            item = rs.getInt("item");
        }
        rs.close();
        ps.close();
        return item;
    }

    public void setNXCodeUsed(String code) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("UPDATE nxcode SET `valid` = 0 WHERE code = ?");
        ps.setString(1, code);
        ps.executeUpdate();
        ps.close();
        ps = con.prepareStatement("UPDATE nxcode SET `user` = ? WHERE code = ?");
        ps.setString(1, this.getName());
        ps.setString(2, code);
        ps.executeUpdate();
        ps.close();
    }

    public void setInCS(boolean yesno) {
        this.incs = yesno;
    }

    public boolean inCS() {
        return this.incs;
    }

    public void addCooldown(int skillId, long startTime, long length, ScheduledFuture<?> timer) {
        coolDowns.put(Integer.valueOf(skillId), new MapleCoolDownValueHolder(skillId, startTime, length, timer));
    }

    public void removeCooldown(int skillId) {
        if (coolDowns.containsKey(Integer.valueOf(skillId))) {
            coolDowns.remove(Integer.valueOf(skillId));
        }
    }

    public boolean skillisCooling(int skillId) {
        return coolDowns.containsKey(Integer.valueOf(skillId));
    }

    public void giveCoolDowns(final int skillid, long starttime, long length) {
        int time = (int) ((length + starttime) - System.currentTimeMillis());
        ScheduledFuture<?> timer = TimerManager.getInstance().schedule(new CancelCooldownAction(this, skillid), time);
        addCooldown(skillid, System.currentTimeMillis(), time, timer);
    }

    public void giveCoolDowns(final List<PlayerCoolDownValueHolder> cooldowns) {
        int time;
        if (cooldowns != null) {
            for (PlayerCoolDownValueHolder cooldown : cooldowns) {
                time = (int) ((cooldown.length + cooldown.startTime) - System.currentTimeMillis());
                ScheduledFuture<?> timer = TimerManager.getInstance().schedule(new CancelCooldownAction(this, cooldown.skillId), time);
                addCooldown(cooldown.skillId, System.currentTimeMillis(), time, timer);
            }
        } else {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT SkillID,StartTime,length FROM skills_cooldowns WHERE charid = ?");
                ps.setInt(1, getId());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    if (rs.getLong("length") + rs.getLong("StartTime") - System.currentTimeMillis() <= 0) {
                        continue;
                    }
                    giveCoolDowns(rs.getInt("SkillID"), rs.getLong("StartTime"), rs.getLong("length"));
                }
                deleteWhereCharacterId(con, "DELETE FROM skills_cooldowns WHERE charid = ?");

            } catch (SQLException e) {
                System.err.println("Error while retriving cooldown from SQL storage");
            }
        }
    }

    public List<PlayerCoolDownValueHolder> getAllCoolDowns() {
        List<PlayerCoolDownValueHolder> ret = new ArrayList<PlayerCoolDownValueHolder>();
        for (MapleCoolDownValueHolder mcdvh : coolDowns.values()) {
            ret.add(new PlayerCoolDownValueHolder(mcdvh.skillId, mcdvh.startTime, mcdvh.length));
        }
        return ret;
    }

    public static class CancelCooldownAction implements Runnable {

        private int skillId;
        private WeakReference<MapleCharacter> target;

        public CancelCooldownAction(MapleCharacter target, int skillId) {
            this.target = new WeakReference<MapleCharacter>(target);
            this.skillId = skillId;
        }

        @Override
        public void run() {
            MapleCharacter realTarget = target.get();
            if (realTarget != null) {
                realTarget.removeCooldown(skillId);
                realTarget.getClient().getSession().write(MaplePacketCreator.skillCooldown(skillId, 0));
            }
        }
    }

    public final List<PlayerDiseaseValueHolder> getAllDiseases() {
        final List<PlayerDiseaseValueHolder> ret = new ArrayList<PlayerDiseaseValueHolder>(5);
        DiseaseValueHolder vh;
        for (Entry<MapleDisease, DiseaseValueHolder> disease : diseases.entrySet()) {
            vh = disease.getValue();
            ret.add(new PlayerDiseaseValueHolder(disease.getKey(), vh.startTime, vh.length));
        }
        return ret;
    }

    public final boolean hasDisease(final MapleDisease dis) {
        for (final MapleDisease disease : diseases.keySet()) {
            if (disease == dis) {
                return true;
            }
        }
        return false;
    }

    public void removeDiseases() {
        diseases.clear();
    }

    public void giveDebuff(final MapleDisease disease, MobSkill skill) {
        final List<Pair<MapleDisease, Integer>> debuff = Collections.singletonList(new Pair<MapleDisease, Integer>(disease, Integer.valueOf(skill.getX())));

        if (!hasDisease(disease) && diseases.size() < 2) {
            if (!(disease == MapleDisease.SEDUCE || disease == MapleDisease.STUN)) {
                if (isActiveBuffedValue(2321005)) {
                    return;
                }
            }
            TimerManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    dispelDebuff(disease);
                }
            }, skill.getDuration());

            diseases.put(disease, new DiseaseValueHolder(System.currentTimeMillis(), skill.getDuration()));
            client.getSession().write(MaplePacketCreator.giveDebuff(debuff, skill));
            map.broadcastMessage(this, MaplePacketCreator.giveForeignDebuff(id, debuff, skill), false);
        }
    }

    public final void giveSilentDebuff(final List<PlayerDiseaseValueHolder> ld) {
        if (ld != null) {
            for (final PlayerDiseaseValueHolder disease : ld) {
                TimerManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        dispelDebuff(disease.disease);
                    }
                }, (disease.length + disease.startTime) - System.currentTimeMillis());

                diseases.put(disease.disease, new DiseaseValueHolder(disease.startTime, disease.length));
            }
        }
    }

    public void dispelSkill(int skillid) {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(effects.values());

        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (skillid == 0) {
                if (mbsvh.effect.isSkill() && (mbsvh.effect.getSourceId() == 1004 || mbsvh.effect.getSourceId() == 1321007 || mbsvh.effect.getSourceId() == 2121005 || mbsvh.effect.getSourceId() == 2221005 || mbsvh.effect.getSourceId() == 2311006 || mbsvh.effect.getSourceId() == 2321003 || mbsvh.effect.getSourceId() == 3111002 || mbsvh.effect.getSourceId() == 3111005 || mbsvh.effect.getSourceId() == 3211002 || mbsvh.effect.getSourceId() == 3211005 || mbsvh.effect.getSourceId() == 4111002)) {
                    cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                    break;
                }
            } else {
                if (mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skillid) {
                    cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                    break;
                }
            }
        }
    }

    public void dispelDebuff(MapleDisease debuff) {
        if (hasDisease(debuff)) {
            long mask = debuff.getValue();
            client.getSession().write(MaplePacketCreator.cancelDebuff(mask));
            map.broadcastMessage(this, MaplePacketCreator.cancelForeignDebuff(id, mask), false);

            diseases.remove(debuff);
        }
    }

    public void dispelDebuffs() {
        dispelDebuff(MapleDisease.CURSE);
        dispelDebuff(MapleDisease.DARKNESS);
        dispelDebuff(MapleDisease.POISON);
        dispelDebuff(MapleDisease.SEAL);
        dispelDebuff(MapleDisease.WEAKEN);
    }

    public void dispelAllDebuffs() {
        diseases.clear();
    }

    public void setLevel(final short level) {
        this.level = (short) (level - 1);
    }

//public boolean canWear(IEquip equip) {
//	if (equip.)
//}
    public void sendNote(String to, String msg) {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        final String sql = "INSERT INTO notes (`to`, `from`, `message`, `timestamp`) VALUES (?, ?, ?, ?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, to);
            ps.setString(2, this.getName());
            ps.setString(3, msg);
            ps.setLong(4, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error Sending Note : " + ex);
        } finally{
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error Sending Note : " + e);
                }
            }
        }
    }

    public void showNote() {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM notes WHERE `to`=?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, getName());
            ResultSet rs = ps.executeQuery();
            rs.last();
            int count = rs.getRow();
            rs.first();
            client.getSession().write(MTSCSPacket.showNotes(rs, count));
            ps.close();
        } catch (SQLException e) {
            System.err.println("Unable to show note" + e);
        }
    }

    public void deleteNote(int id) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM notes WHERE `id`=?");
            ps.setInt(1, id);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Unable to delete note" + e);
        }
    }

    public int getCashSlots() {
        return 96;
    }

    public int getEquipSlots() {
        return 96;
    }

    public int getEtcSlots() {
        return 96;
    }

    public int getSetupSlots() {
        return 96;
    }

    public int getUseSlots() {
        return 96;
    }

    public int getMarkedMonster() {
        return markedMonster;
    }

    public void setMarkedMonster(int markedMonster) {
        this.markedMonster = markedMonster;
    }

    public Byte getHammerSlot() {
        return hammerSlot;
    }

    public void setHammerSlot(Byte hammerSlot) {
        this.hammerSlot = hammerSlot;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public void resetBattleshipHp() {
        ISkill skill = SkillFactory.getSkill(Skills.Corsair.Battleship);
        this.battleshipHp = (4000 * getSkillLevel(skill)) + ((getLevel() - 120) * 2000);
    }

    public int getBattleshipHp() {
        return battleshipHp;
    }

    public void setBattleshipHp(int battleshipHp) {
        this.battleshipHp = battleshipHp;
    }

    public void decreaseBattleshipHp(int decrease) {
        this.battleshipHp -= decrease;
        if (battleshipHp <= 0) {
            this.battleshipHp = 0;
            ISkill battleship = SkillFactory.getSkill(Skills.Corsair.Battleship);
            int cooldown = battleship.getEffect(getSkillLevel(battleship)).getCooldown();
            getClient().getSession().write(MaplePacketCreator.skillCooldown(Skills.Corsair.Battleship, cooldown));
            ScheduledFuture<?> timer = TimerManager.getInstance().schedule(new CancelCooldownAction(this, Skills.Corsair.Battleship), cooldown * 1000);
            addCooldown(Skills.Corsair.Battleship, System.currentTimeMillis(), cooldown * 1000, timer);
            cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
            resetBattleshipHp();
        }
    }

    private void maxAdventurerSkills(boolean shit) {
        maxHeroSkills(shit);
        maxPaladinSkills(shit);
        maxDarkKnightSkills(shit);
        maxFPArchMageSkills(shit);
        maxILArchMageSkills(shit);
        maxBishopSkills(shit);
        maxBowMasterSkills(shit);
        maxCrossBowMasterSkills(shit);
        maxNightLordSkills(shit);
        maxShadowerSkills(shit);
        maxBuccaneerSkills(shit);
        maxCorsairSkills(shit);
    }

    public void maxKOCSkills(boolean shit) {
        maxDawnWarrior3Skills(shit);
        maxBlazeWizard3Skills(shit);
        maxWindArcher3Skills(shit);
        maxNightWalker3Skills(shit);
        maxThunderBreaker3Skills(shit);
    }

    public void maxSkills(boolean shit) {
        if (GMSMode > 0 && !isJounin()) {
            maxJobSkills(shit);
            return;
        }
        if (job < 911 || isJounin()) {
            maxAdventurerSkills(shit);
        }
        if (job > 910 || isJounin()) {
            maxKOCSkills(shit);
        }
        if (this.hasRasengan()) {
            maxGMSkills(shit);
        }
        if (isJounin()) {
            maxSuperGMSkills(shit);
        }
    }

    public void maxSingleSkill(boolean shit, int skillid) {
        ISkill ski = SkillFactory.getSkill(skillid);
        byte maxi = ski.getMaxLevel();
        if (shit) {
            changeSkillLevel(ski, maxi, maxi);
        } else {
            skills.put(ski, new SkillEntry(maxi, maxi));
        }
    }

    public void maxBeginnerSkills(boolean shit) {
        for (int ski : Skills.BeginnerSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxSwordsManSkills(boolean shit) {
        maxBeginnerSkills(shit);
        for (int ski : Skills.SwordsmanSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxFighterSkills(boolean shit) {
        maxSwordsManSkills(shit);
        for (int ski : Skills.FighterSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxCrusaderSkills(boolean shit) {
        maxFighterSkills(shit);
        for (int ski : Skills.CrusaderSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxHeroSkills(boolean shit) {
        maxCrusaderSkills(shit);
        for (int ski : Skills.HeroSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxPageSkills(boolean shit) {
        maxSwordsManSkills(shit);
        for (int ski : Skills.PageSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxWhiteKnightSkills(boolean shit) {
        maxPageSkills(shit);
        for (int ski : Skills.WhiteKnightSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxPaladinSkills(boolean shit) {
        maxWhiteKnightSkills(shit);
        for (int ski : Skills.PaladinSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxSpearManSkills(boolean shit) {
        maxSwordsManSkills(shit);
        for (int ski : Skills.SpearManSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxDragonKnightSkills(boolean shit) {
        maxSpearManSkills(shit);
        for (int ski : Skills.DragonKnightSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxDarkKnightSkills(boolean shit) {
        maxDragonKnightSkills(shit);
        for (int ski : Skills.DarkKnightSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxMagicianSkills(boolean shit) {
        maxBeginnerSkills(shit);
        for (int ski : Skills.MagicianSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxFPMagicianSkills(boolean shit) {
        maxMagicianSkills(shit);
        for (int ski : Skills.FPMagicianSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxFPMageSkills(boolean shit) {
        maxFPMagicianSkills(shit);
        for (int ski : Skills.FPMageSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxFPArchMageSkills(boolean shit) {
        maxFPMageSkills(shit);
        for (int ski : Skills.FPArchMageSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxILMagicianSkills(boolean shit) {
        maxMagicianSkills(shit);
        for (int ski : Skills.ILMagicianSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxILMageSkills(boolean shit) {
        maxILMagicianSkills(shit);
        for (int ski : Skills.ILMageSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxILArchMageSkills(boolean shit) {
        maxILMageSkills(shit);
        for (int ski : Skills.ILArchMageSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxClericSkills(boolean shit) {
        maxMagicianSkills(shit);
        for (int ski : Skills.ClericSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxPriestSkills(boolean shit) {
        maxClericSkills(shit);
        for (int ski : Skills.Priestskills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBishopSkills(boolean shit) {
        maxPriestSkills(shit);
        for (int ski : Skills.BishopSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxArcherSkills(boolean shit) {
        maxBeginnerSkills(shit);
        for (int ski : Skills.ArcherSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBowManSkills(boolean shit) {
        maxArcherSkills(shit);
        for (int ski : Skills.HunterSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxRangerSkills(boolean shit) {
        maxBowManSkills(shit);
        for (int ski : Skills.RangerSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBowMasterSkills(boolean shit) {
        maxRangerSkills(shit);
        for (int ski : Skills.BowmasterSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxCrossBowManSkills(boolean shit) {
        maxArcherSkills(shit);
        for (int ski : Skills.CrossBowMankills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxSniperSkills(boolean shit) {
        maxCrossBowManSkills(shit);
        for (int ski : Skills.SniperSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxCrossBowMasterSkills(boolean shit) {
        maxSniperSkills(shit);
        for (int ski : Skills.MarksmanSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxRogueSkills(boolean shit) {
        maxBeginnerSkills(shit);
        for (int ski : Skills.RogueSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxAssasinSkills(boolean shit) {
        maxRogueSkills(shit);
        for (int ski : Skills.AssassinSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxHermitSkills(boolean shit) {
        maxAssasinSkills(shit);
        for (int ski : Skills.HermitSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxNightLordSkills(boolean shit) {
        maxHermitSkills(shit);
        for (int ski : Skills.NightLordSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBanditSkills(boolean shit) {
        maxRogueSkills(shit);
        for (int ski : Skills.BanditSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxChiefBanditSkills(boolean shit) {
        maxBanditSkills(shit);
        for (int ski : Skills.ChiefBanditSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxShadowerSkills(boolean shit) {
        maxChiefBanditSkills(shit);
        for (int ski : Skills.ShadowerSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxPirateSkills(boolean shit) {
        maxBeginnerSkills(shit);
        for (int ski : Skills.PirateSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBrawlerSkills(boolean shit) {
        maxPirateSkills(shit);
        for (int ski : Skills.BrawlerSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxMarauderSkills(boolean shit) {
        maxBrawlerSkills(shit);
        for (int ski : Skills.MarauderSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBuccaneerSkills(boolean shit) {
        maxMarauderSkills(shit);
        for (int ski : Skills.BuccaneerSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxGunSlingerSkills(boolean shit) {
        maxPirateSkills(shit);
        for (int ski : Skills.GunslingerSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxOutLawSkills(boolean shit) {
        maxGunSlingerSkills(shit);
        for (int ski : Skills.OutlawSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxCorsairSkills(boolean shit) {
        maxOutLawSkills(shit);
        for (int ski : Skills.CorsairSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxNoblesseSkills(boolean shit) {
        for (int ski : Skills.NoblesseSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxDawnWarrior1Skills(boolean shit) {
        maxNoblesseSkills(shit);
        for (int ski : Skills.DawnWarrior1Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxDawnWarrior2Skills(boolean shit) {
        maxDawnWarrior1Skills(shit);
        for (int ski : Skills.DawnWarrior2Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxDawnWarrior3Skills(boolean shit) {
        maxDawnWarrior2Skills(shit);
        for (int ski : Skills.DawnWarrior3skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBlazeWizard1Skills(boolean shit) {
        maxNoblesseSkills(shit);
        for (int ski : Skills.BlazeWizard1Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBlazeWizard2Skills(boolean shit) {
        maxBlazeWizard1Skills(shit);
        for (int ski : Skills.BlazeWizard2Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBlazeWizard3Skills(boolean shit) {
        maxBlazeWizard2Skills(shit);
        for (int ski : Skills.BlazeWizard3Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxWindArcher1Skills(boolean shit) {
        maxNoblesseSkills(shit);
        for (int ski : Skills.WindArcher1Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxWindArcher2Skills(boolean shit) {
        maxWindArcher1Skills(shit);
        for (int ski : Skills.WindArcher2Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxWindArcher3Skills(boolean shit) {
        maxWindArcher2Skills(shit);
        for (int ski : Skills.WindArcher3Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxNightWalker1Skills(boolean shit) {
        maxNoblesseSkills(shit);
        for (int ski : Skills.NightWalker1Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxNightWalker2Skills(boolean shit) {
        maxNightWalker1Skills(shit);
        for (int ski : Skills.NightWalker2Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxNightWalker3Skills(boolean shit) {
        maxNightWalker2Skills(shit);
        for (int ski : Skills.NightWalker3Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxThunderBreaker1Skills(boolean shit) {
        maxNoblesseSkills(shit);
        for (int ski : Skills.ThunderBreaker1Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxThunderBreaker2Skills(boolean shit) {
        maxThunderBreaker1Skills(shit);
        for (int ski : Skills.ThunderBreaker2Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxThunderBreaker3Skills(boolean shit) {
        maxThunderBreaker2Skills(shit);
        for (int ski : Skills.ThunderBreaker2Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxGMSkills(boolean shit) {
        for (int ski : Skills.GMSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxSuperGMSkills(boolean shit) {
        maxGMSkills(shit);
        for (int ski : Skills.SuperGMSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void clearSkills() {
        this.skills.clear();
    }

    public void wipeKB() {
        this.keylayout.Layout().clear();
        skillMacros = new SkillMacro[5];
    }

    public void changeMap(int mapid, int portal) {
        MapleMap to = client.getChannelServer().getMapFactory().getMap(mapid);
        MaplePortal pto = to.getPortal(portal);
        MaplePacket warpPacket = MaplePacketCreator.getWarpToMap(to, pto.getId(), this);
        changeMapInternal(to, pto.getPosition(), warpPacket);
    }

    public void changeMap(MapleMap to) {
        MaplePortal pto = to.getPortal(0);
        MaplePacket warpPacket = MaplePacketCreator.getWarpToMap(to, pto.getId(), this);
        changeMapInternal(to, pto.getPosition(), warpPacket);
    }

    public void unJail() {
        changeMap(100000000, 0);
        dropMessage("Be a good girl now or you might be banned ;P");
        unMute();

    }

    public void dropMessage(String msg) {
        dropMessage(5, msg);
    }

    public void dropMessage(int type, String message) {
        getClient().getSession().write(MaplePacketCreator.serverNotice(type, message));
    }

    public void showMessage(String msg) {
        showMessage(5, msg);
    }

    public void showMessage(int type, String message) {
        getClient().getSession().write(MaplePacketCreator.serverNotice(type, message));
    }

    /*
     * GM Status
     */
    public boolean canFuck(MapleCharacter noob) {
        if (noob.getGMLevel() <= 1) {
            return true;
        } else if (getGMLevel() >= noob.getGMLevel()) {
            return true;
        }
        return false;
    }

    public byte getGMLevel() {
        return gmLevel;
    }

    public boolean hasGmLevel(int level) {
        return gmLevel >= level;
    }

    public boolean isAdmin() {
        return getAccountID() == 1 || gmLevel > 4;
    }

    public boolean isHokage() {
        return isAdmin();
    }

    public boolean isSannin() {
        return isHokage() || gmLevel > 3;
    }

    public boolean isJounin() {
        return isSannin() || gmLevel > 2;
    }

    public boolean isChunin() {
        return isJounin() || gmLevel > 1;
    }

    public boolean isGenin() {
        return isChunin() || gmLevel > 0;
    }

    public void setGM(byte x) {
        this.gmLevel = x;
    }

    public boolean inJail() {
        return getMapId() == 200090300;
    }

    public void jail() {
        mute((byte) 1);
        changeMap(200090300, 0);
        dropMessage("fuck you asshole. Rot in jail. Auto unjail in 15 minutes. If you relog counter will reset :)");
        scheduleUnJail();

    }

    public void scheduleUnJail() {
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                unJail();
            }
        }, 15 * 60 * 1000); // 15 minute
    }

    public void mute(byte fuck) {
        this.mutality = fuck;
    }

    public void unMute() {
        this.mutality = 0;
    }

    public byte getMute() {
        return this.mutality;
    }

    public void changeJobById(int fuck) {
        skills.clear();
        this.changeJob(MapleJob.getById(fuck));
    }

    public void kill() {
        stats.setHp(0);
        stats.setMp(0);
        updateSingleStat(MapleStat.HP, 0);
        updateSingleStat(MapleStat.MP, 0);
    }

    public void heal() {
        int thehp = stats.getHp(); //  getnow
        stats.setHp(stats.getMaxHp());
        stats.setMp(stats.getMaxMp());
        updateSingleStat(MapleStat.HP, stats.getHp());
        updateSingleStat(MapleStat.MP, stats.getMp());
        if (thehp == 0) {
            setStance(4);
            getMap().broadcastMessage(this, MaplePacketCreator.removePlayerFromMap(this.getObjectId()), false);
            getMap().broadcastMessage(this, MaplePacketCreator.spawnPlayerMapobject(this), false);
            if (getChalkboard() != null) {
                this.getMap().broadcastMessage(this, MTSCSPacket.useChalkboard(this.id, this.chalktext), false);
            }
        }
    }

    public void setExp(int newExp) {
        exp = newExp;
        this.updateSingleStat(MapleStat.EXP, newExp);
    }

    public boolean isRebirthing() {
        return rebirthing;
    }

    public void setIsRebirthing(boolean b) {
        this.rebirthing = b;
    }

    public void setReborns(int fuck) {
        this.reborn = fuck;
    }

    public void addReborn() {
        this.reborn++;
    }

    public int getReborns() {
        return reborn;
    }

    public void torture() {
        if (!isAdmin()) {
            client.getSession().write(MaplePacketCreator.serverNotice(1, "You have lost your E-Penis"));
            final int originalmap = getMapId();
            TimerManager.getInstance().schedule(new Runnable() {

                public void run() {
                    for (int i = 0; i < 50; i++) {
                        int[] mapids = {0, 2, 100000000, 101000000, 102000000, 103000000, 104000000, 106000000, 107000000, 200000000, 220000000, 230000000, 240000000, 250000000, 600000000, 800000000, 910000001, 910000007, 910000015, 910000021, 280030000, 240060200, originalmap};
                        for (int mapid1 : mapids) {
                            changeMap(mapid1, 0);
                        }

                    }
                }
            }, 1000);
        }
    }

    public void torture(String reason) {
        String[] reasonslol = {"a Jinchuriki", "a legendary Sannin", "Oruchimaru", "a Shinigami", "a Hollow", "a Hokage", "a Raikage", "ChuckNorris"};
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.broadcastPacket(MaplePacketCreator.serverNotice(6, "[Akatsuki] " + name + " has been butt-raped by " + reasonslol[NinjaMath.random(reasonslol.length)] + " and is now taking a ride on a big black ahem! Reason : " + reason));
        }
        torture();
    }

    public void hide() {
        if (noHide) {
            // do nothing
        } else {
            // SkillFactory.getSkill(9101004).getEffect(1).applyTo(this);
            getClient().getSession().write(MaplePacketCreator.sendGMOperation(16, 1));
        }
    }

    public void deHide() {
        if (this.getBuffedValue(MapleBuffStat.GM_HIDE) != null) {
            this.cancelBuffStats(MapleBuffStat.GM_HIDE);
        }
    }

    public void setChalkboard(String text) {
        this.chalktext = text;
        if (chalktext == null) {
            getMap().broadcastMessage(MTSCSPacket.useChalkboard(this.id, this.chalktext));
        } else {
            getMap().broadcastMessage(MTSCSPacket.useChalkboard(this.id, this.chalktext));
        }

    }

    public String getChalkboard() {
        return chalktext;
    }

    public List<LifeMovementFragment> getLastRes() {
	return lastres;
    }

    public void setLastRes(List<LifeMovementFragment> lastres) {
	this.lastres = lastres;
    }
    
    public void setID(int i) {
        this.id = i;
    }

    public void removeClones() {
        if (hasClones()) {
            for (Clones clone : getClones()) {
                clone.getClone().getMap().removePlayer(clone.getClone());
            }
            fakes.clear();
        }
    }

    public boolean hasClones() {
        for (Clones clone : fakes) {
            if (clone != null) {
                return true;
            }
        }
        return false;
    }

    public List<Clones> getClones() {
        return fakes;
    }

    public void addClone(Clones f) {
        this.fakes.add(f);
    }

    public boolean canHasClone() {
        return clonelimit > 0;
    }

    public byte getCloneLimit() {
        if (isHokage() && clonelimit < 69) {
            this.clonelimit = (byte) 69;
        } else if (isJounin() && clonelimit < 25) {
            this.clonelimit = (byte) 25;
        } else if (isGenin() && clonelimit < 15) {
            this.clonelimit = (byte) 15;
        }
        return clonelimit;
    }

    public void setCloneLimit(byte fuck) {
        this.clonelimit = fuck;
    }

    public boolean hasRasengan() {
        return rasengan == 69;
    }

    public byte getRasengan() {
        return rasengan;
    }

    public void setRasengan(byte b) {
        rasengan = b;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE `characters` SET `rasengan` = ? where `id` =?");
            ps.setByte(1, this.rasengan);
            ps.setInt(2, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException sQLException) {
            sQLException.printStackTrace();
        }
        dropMessage("Your Rasengan Quest level is now " + b);
    }

    public void sendGMMsg(String msg) {
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.broadcastStaffPacket(MaplePacketCreator.serverNotice(5, msg));
        }
    }

    public void unstuck() {
        sendGMMsg("Unstucking " + getName());
        try {
            getMap().broadcastMessage(MaplePacketCreator.removePlayerFromMap(id));
            try {
                map.removePlayer(this);
                sendGMMsg("tried to remove player from map");
            } catch (Exception e) {
                getMap().broadcastMessage(MaplePacketCreator.removePlayerFromMap(id));
                sendGMMsg("Tried to broadcast remove player packet as removing player failed");
            }
            try {
                if (getEventInstance() != null) {
                    getEventInstance().unregisterPlayer(this);
                    sendGMMsg("Event stuck tried to fix by unregistering the character in the event");
                }
            } catch (Exception e) {
                System.err.println("Unstuck ERROR : " + e);
            }
            try {
                if (NPCScriptManager.getInstance().getCM(client) != null) {
                    NPCScriptManager.getInstance().dispose(client);
                }
            } catch (Exception e) {
                System.err.println("Unstuck ERROR : " + e);
            }
            try {
                if (client.getChannelServer().getPlayerStorage().getCharacterById(this.id) != null) { // check, null pointer no thanks
                    client.getChannelServer().removePlayer(this); // this is all you need.
                    sendGMMsg("player was in the player storage so tried to remove him from that");
                }
            } catch (Exception e) {
                System.err.println("Unstuck ERROR : " + e);
            }
            try {
                client.disconnect(true, false);
                sendGMMsg("Tried to disconnect his client");
            } catch (Exception e) {
                System.err.println("Unstuck ERROR : " + e);
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = ? WHERE id = ?");
                ps.setInt(1, 0);
                ps.setInt(2, accountid);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException ignored) {
            }
        } catch (Exception e) {
            System.err.println("Unstuck ERROR : " + e);
        }

    }

    public byte getNinjaTensu() {
        return this.ninjatensu;
    }

    public void setNinjaTensu(byte fuck) {
        this.ninjatensu = fuck;
    }

    public void addNinjaTensu() {
        this.ninjatensu++;
    }

    public void reduceNinjaTensu() {
        this.ninjatensu--;
    }

    public boolean legendBlocked(String legend) {
        return GameConstants.isBlockedName(legend) && !isChunin() || legend.length() > 15;
    }

    public void setLegend(String legend) {
        if (legendBlocked(legend)) {
            dropMessage("This cannot be used as a legend in NinjaMS (" + legend + ")");
            return;
        }
        this.legend = legend;
        dropMessage("Your legend set to" + legend);
    }

    public String getLegend() {
        return this.legend;
    }

    public void expirationTask() {
        long expiration;
        long currenttime = System.currentTimeMillis();
        List<IItem> toberemove = new ArrayList<IItem>(); // This is here to prevent deadlock.
        for (MapleInventory inv : inventory) {
            for (IItem item : inv.list()) {
                expiration = item.getExpiration();
                if (currenttime > expiration && expiration != -1) {
                    client.getSession().write(MTSCSPacket.itemExpired(item.getItemId()));
                    toberemove.add(item);
                }
            }
            for (IItem item : toberemove) {
                MapleInventoryManipulator.removeFromSlot(client, inv.getType(), item.getPosition(), item.getQuantity(), true);
            }
            toberemove.clear();
        }
    }

    public MaplePet getPet() {
        return pet;
    }

    public void setPet(MaplePet pet) {
        this.pet = pet;
    }

//Donation stuff
    public short getDPoints() {
        return this.dpoints;
    }

    public short getDAmount() {
        return this.damount;
    }

    public void modifyDPoints(short fff) {
        dpoints += fff;
        saveToDB(false, false);
        if (fff < 0) {
            dropMessage("You have lost " + (fff * -1) + " donator Points.");
        } else {
            dropMessage("You have Gained " + fff + "donator Points");
        }
        dropMessage("You have " + dpoints + " donator points");
    }

    public void modifyDAmount(short fff) {
        damount += fff;
        saveToDB(false, false);
        if (fff < 0) {
            dropMessage("[TheBass] $ " + (fff * -1) + " has been reduced from your donated amount.");
        } else {
            dropMessage("[TheBass] $ " + fff + " has been increased in your donated amount.");
        }
        dropMessage("[TheBass] So far you have donated " + damount + " USD to NinjaMS.");
    }

    public void finishAlert() {
        StringBuilder sb = new StringBuilder();
        String readableTargetName = MapleCharacterUtil.makeMapleReadable(getName());
        sb.append(getName());
        sb.append(" (").append(readableTargetName).append(")");
        sb.append(" has just finished the JQ: ");
        sb.append(getMapId());
        sb.append(". Last Jq Finished at : ").append(lastJQFinish);
        lastJQFinish = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(System.currentTimeMillis()));
        sb.append(" Started at System time : ").append(jqStart).append(". finished at : ").append(lastJQFinish).append(". ");
        try {
            getClient().getChannelServer().broadcastStaffPacket(MaplePacketCreator.serverNotice(5, sb.toString()));
        } catch (Exception ignored) {
        }
    }

    public void giveJQReward() {
        if (jqStart == null || client.getChannel() != 3) {
            dropMessage("You did not start the JQ properly. either you warped in or CCed . so no rewards for you");
            return;
        }
        int type = (int) Math.floor(Math.random() * 3200 + 1);
        int[] jqmap = {105040311, 105040313, 105040316, 103000902, 103000905, 103000909, 101000101, 101000104, 109040004, 280020001};
        for (byte i = 1; i <= jqmap.length; i++) {
            if (this.getMapId() == jqmap[i]) {
                jqFinished++;
                if (i == 9) {
                    jqPoints++;
                    lastjq = 0;
                } else {
                    lastjq = i;
                }
                dropMessage("Congratulations on successfully finishing the JQ #" + (i + 1));
            } else {
                dropMessage("You cheat");
            }
        }
        int chance = (int) (Math.random() * 1000);
        if (chance < 10) {
            giveSpecialScroll(1);
        } else if (chance < 50) {
            Rebirths.giveRebirth(this);
            dropMessage("you have gained a rebirth");
        } else if (chance < 75) {
            gainItem(Items.GachaType.regular, 1);
            dropMessage("you have gained a Gacha ticket");
        } else if (chance < 100) {
            levelUp();
            levelUp();
            levelUp();
        } else if (chance < 110) {
            addNinjaTensu();
            dropMessage("you have gained NinjaTensu");
        } else if (chance < 200) {
            addCSPoints(1, chance * 5);
            dropMessage("You have gained " + (chance * 5) + " NX");
        }
        changeMap(100000000);
    }

    public void giveSpecialScroll(int amt) {
        int[] scrolls = {2040603, 2044503, 2041024, 2041025, 2044703, 2044603, 2043303, 2040807, 2040806, 2040006, 2040007, 2043103, 2043203, 2043003, 2040506, 2044403, 2040903, 2040709, 2040710, 2040711, 2044303, 2043803, 2040403, 2044103, 2044203, 2044003, 2043703, 2041200, 2049100, 2049000, 2049001, 2049002, 2049003};
        int i = (int) (Math.random() * scrolls.length);
        if (checkSpace(2040603, amt)) {
            gainItem(scrolls[i], amt);
            dropMessage("You have gained " + amt + " of some special scroll");
        } else {
            dropMessage("You did not gain a special scroll because your inventory was full");
        }
    }

    public String jqStartTime() {
        return this.jqStart;
    }

    public final int getJqFinished() {
        return jqFinished;
    }

    public void setJqFinished(final int jqFinished) {
        this.jqFinished = jqFinished;
    }

    public final short getJqPoints() {
        return jqPoints;
    }

    public void setJqPoints(final short jqpoints) {
        this.jqPoints = jqpoints;
    }

    public final byte getLastJQ() {
        if (lastjq > 9) {
            lastjq = 0;
        }
        return lastjq;
    }

    public void setLastJQ(final byte x) {
        this.lastjq = x;
    }

    public void startJq(final int wtf) {
        int[] target = {105040310, 105040312, 105040314, 103000900, 103000903, 103000906, 101000100, 101000102, 109040001, 280020000};
        changeMap(target[wtf - 1]);
        dropMessage("Warping to map : " + target[wtf - 1] + " to start JQ");
        dropMessage("Please Vote for us at http://ninjams.org/vote. and DON'T hack :)");
        noHide = true;
        cancelAllBuffs();
    }

    public void startAlert(final int jq) {
        StringBuilder sb = new StringBuilder();
        String readableTargetName = MapleCharacterUtil.makeMapleReadable(getName());
        sb.append(getName());
        sb.append(" (").append(readableTargetName).append(")");
        sb.append(" has just started the JQ: ");
        sb.append(jq);
        sb.append(".");
        jqStart = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(System.currentTimeMillis()));
        sb.append(" Started at System time : ").append(jqStart).append(" . ");
        try {
            getClient().getChannelServer().broadcastStaffPacket(MaplePacketCreator.serverNotice(5, sb.toString()));
        } catch (Exception ignored) {
        }
    }

    public void bonusReward() {
        if (jqPoints < 1) {
            dropMessage("you do not even have one JQ point. idiot");
            return;
        }
        jqPoints--;
        int type = (int) (Math.random() * 100);
        if (type < 5) {
            if (checkSpace(1812006)) {
                gainExpiringItem(1812006, (3 * 24 * 60));
            } else {
                dropMessage("You did not have enough space. Instead off scamming your JQpoints, I'll simply kill you and put a death penalty on you");
                jqPoints++;
            }
        } else if (type < 10) {
            jqPoints++;
            jqPoints++;
            dropMessage("You have gained 1 JQ point");
        } else if (type < 35) {
            gainItem(Items.GachaType.special, 2);
        } else if (type < 50) {
            for (int i = 0; i < 10; i++) {
                Rebirths.giveRebirth(this);
            }
            dropMessage("You have gained 10 rebirths");
        } else if (type < 75) {
            giveSpecialScroll(5);
        } else if (type < 90) {
            gainItem(Items.currencyType.Sight, 100);
        } else {
            dropMessage("you have been scammed");
        }
    }

    public void donatorGacha() {
        if (dpoints < 1) {
            dropMessage("You dont have any donator Point");
            return;
        }
        if (Donations.Gacha(this)) {
            modifyDPoints((short) -1);
        }
    }

    public void maxAllStats() {
        stats.setAllStats(32767);
    }

    public void changeMap(int fuck) {
        changeMap(fuck, 0);
    }

    public void gainExpiringItem(int itemid, int minutes) {
        long expiry = minutes * 60 * 1000;
        MapleInventoryManipulator.addById(client, itemid, (short) 1, "", null, expiry);
        client.getSession().write(MaplePacketCreator.getShowItemGain(itemid, (short) 1, true));
    }

    public void gainStatItem(int id, short stat, short wa, short ma) {
        MapleInventoryManipulator.addStatItemById(client, id, name, stat, wa, ma);
        // client.getSession().write(MaplePacketCreator.getShowItemGain(id, (short) 1, true));
        dropMessage("You have gained a stat Item. Itemid : " + id + " Stats : " + stat + "WA : " + wa + " MA : " + ma);
    }

    public void gainItem(int id, int quantity) {
        if (quantity >= 0) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            IItem item = ii.getEquipById(id);
            MapleInventoryType type = InventoryConstants.getInventoryType(id);
            if (!MapleInventoryManipulator.checkSpace(client, id, quantity, "")) {
                client.getSession().write(MaplePacketCreator.serverNotice(1, "Your inventory is full."));
                return;
            }
            if (type.equals(MapleInventoryType.EQUIP) && !InventoryConstants.isThrowingStar(item.getItemId()) && !InventoryConstants.isBullet(item.getItemId())) {
                MapleInventoryManipulator.addFromDrop(client, item, true);
            } else {
                MapleInventoryManipulator.addById(client, id, (short) quantity, "");
            }
        } else {
            MapleInventoryManipulator.removeById(client, InventoryConstants.getInventoryType(id), id, -quantity, true, false);
        }
        client.getSession().write(MaplePacketCreator.getShowItemGain(id, (short) quantity, true));
    }

    public int getPvpKills() {
        return this.pvpkills;
    }

    public int getPvpDeaths() {
        return this.pvpdeaths;
    }

    public void gainPvpKill() {
        this.pvpkills++;
    }

    public void gainPvpDeath() {
        this.pvpdeaths++;
    }

    public void setPvpDeaths(int amount) {
        this.pvpdeaths = amount;
    }

    public void setPvpKills(int amount) {
        this.pvpkills = amount;
    }

// Fun stuff from Oliver For the GMs
    public void registerGayBombs() {
        inflicted = true;
        doGayBombs();

    }

    public void inflict() {
        inflicted = true;
    }

    public boolean isInflicted() {
        return inflicted;
    }

    private void doGayBombs() {
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (inflicted) {
                    heal();
                    getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300166), getPosition());
                    doGayBombs();

                }
            }
        }, 500);
    }

    public void registerJump() {
        inflicted = true;
        removeChair();
//dispelSeduce(); // LOL
        dispelDebuffs();
        giveDebuff(MapleDisease.getType(128), MobSkillFactory.getMobSkill(128, 6));
        doJump();
    }

    public void removeChair() {
        setChair(0);
        getClient().getSession().write(MaplePacketCreator.cancelChair());
        getMap().broadcastMessage(this, MaplePacketCreator.showChair(id, 0), false);
    }

    private void doJump() {
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (inflicted) {
                    removeChair();
                    //dispelSeduce(); // LOL
                    dispelDebuffs();
                    heal();
                    giveDebuff(MapleDisease.getType(128), MobSkillFactory.getMobSkill(128, 6));
                    doJump();
                }
            }
        }, 2000);
    }

    public void registerDance() {
        inflicted = true;
        doDance(1);
    }

    private void doDance(final int dir) {
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (inflicted) {
                    removeChair();
                    dispelDebuffs();
                    heal();
                    giveDebuff(MapleDisease.getType(128), MobSkillFactory.getMobSkill(128, dir));
                    if (dir == 1) {
                        doDance(2); // loop
                    } else {
                        doDance(1);
                    }
                }
            }
        }, 1500);
    }

    public void deInflict() {
        inflicted = false;
    }

    public boolean unequipEverything() {
        MapleInventory equipped = this.getInventory(MapleInventoryType.EQUIPPED);
        List<Short> position = new ArrayList<Short>();
        for (IItem item : equipped.list()) {
            position.add(item.getPosition());
        }
        for (short pos : position) {
            if (getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1) {
                MapleInventoryManipulator.unequip(client, pos, getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
            } else {
                showMessage(1, "You do not have enough space to strip all your clothes.");
                return false;
            }

        }
        client.showMessage(5, "[System] All items have been successfully unequiped!");
        return true;
    }

    public boolean Strip(MapleCharacter boss) {
        MapleInventory equipped = this.getInventory(MapleInventoryType.EQUIPPED);
        List<Short> position = new ArrayList<Short>();
        for (IItem item : equipped.list()) {
            position.add(item.getPosition());
        }
        for (short pos : position) {
            if (getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1) {
                MapleInventoryManipulator.unequip(client, pos, getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
            } else {
                boss.showMessage(1, "the noob do not have enough space to strip");
                return false;
            }

        }
        boss.showMessage(5, "[Hokage] The noob has been stripped!");
        return true;
    }

// limitations
    public void setUndroppable() {
        cannotdrop = true;
    }

    public void setDroppable() {
        cannotdrop = false;
    }

    public boolean cannotDrop() {
        return cannotdrop;
    }

// Smega Stuff
    public void setPrefixShit(int lol) {
        this.prefixshit = (byte) lol;
    }

    public byte getPrefixShit() {
        return prefixshit;
    }

    // Ap storage
    public void setStorageAp(int fuck) {
        storageAp = fuck;
    }

    public void addStorageAp(int fuck) {
        storageAp += fuck;
    }

    public void reduceStorageAp(int fuck) {
        storageAp -= fuck;
    }

    public int getStorageAp() {
        return storageAp;
    }

    // ap per level
    public byte getApPerLevel() {
        if (reborn > 2000) {
            return 1;
        } else if (reborn > 1000) {
            return 2;
        } else if (reborn > 500) {
            return 3;
        } else if (reborn > 250) {
            return 4;
        } else {
            return 5;
        }
    }

    // AutoAp Shit
    public byte getAutoAp() {
        return autoap;
    }

    public void setAutoAp(byte type) {
        autoap = type;
    }

    public boolean isMarried() {
        return false;
    }

    public boolean getNoHide() {
        return noHide;
    }

    public void setNoHide(boolean setTo) {
        noHide = setTo;
    }

    // skills
    public boolean canUseSkill(int skillid) {
        if (skillid < 1000000) {
            return true;
        }
        if (isJounin()) {
            return true;
        }
        byte x = 0;
        if (this.getBuffedValue(MapleBuffStat.MORPH) != null) {
            if (!SkillFactory.getSkill(skillid).canBeLearnedBy(this.getBuffSource(MapleBuffStat.MORPH) / 10000) && SkillFactory.getSkill(this.getBuffSource(MapleBuffStat.MORPH)) != null) {
                cancelAllBuffs();
                changeSkillLevel(SkillFactory.getSkill(skillid), x, x); // false :D!
                return false;
            }
        }
        if (!SpecialStuff.getInstance().isSkillBlocked(mapid)) {
            return true;
        }
        if (skillid < 10000000 && job < 911) {
            return true;
        }
        if (skillid >= 10000000 && job > 910) {
            changeSkillLevel(SkillFactory.getSkill(skillid), x, x); // false :D!
        }
        return false;
    }

    // Rates Stuff
    public int getExpBoost() {
        return (expBoost);
    }

    public int getMesoBoost() {
        return (mesoBoost);
    }

    public int getDropBoost() {
        return (dropBoost);
    }

    public int getBossDropBoost() {
        return (bdropBoost);
    }

    public void addExpBoost() {
        expBoost++;
    }

    public void addMesoBoost() {
        mesoBoost++;
    }

    public void addDropBoost() {
        dropBoost++;
    }

    public void addBossDropBoost() {
        bdropBoost++;
    }

    /**
     * Smega limitations
     * @return
     */
    public boolean isAsmega() {
        return smega > 1;
    }

    public void setAllMega() {
        smega = 0;
    }

    public void setAsmega() {
        smega = 1;
    }

    public boolean isPsmega() {
        return smega >= 2;
    }

    public void setPsmega() {
        smega = 2;
    }

    // Tao management
    public int itemCount(int itemid) {
        MapleInventoryType type = InventoryConstants.getInventoryType(itemid);
        MapleInventory iv = inventory[type.ordinal()];
        int possesed = iv.countById(itemid);
        if (storage != null) {
            for (IItem noob : storage.getItems()) {
                if (noob.getItemId() == itemid) {
                    possesed += noob.getQuantity();
                }
            }
        }
        return possesed;
    }

    public int getItemQuantity(int itemid, boolean checkEquipped) {
        int possesed = inventory[InventoryConstants.getInventoryType(itemid).ordinal()].countById(itemid);
        if (checkEquipped) {
            possesed += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        return possesed;
    }

    public int getTaoOfSight() {
        return itemCount(Items.currencyType.Sight);
    }

    public boolean haveSight(int amount) {
        return haveItem(Items.currencyType.Sight, amount);
    }

    public int getTaoOfHarmony() {
        return itemCount(Items.currencyType.Harmony);
    }

    public int getTaoOfShadow() {
        return itemCount(Items.currencyType.Shadow);
    }

    public final void changeChannel(final byte ch) {
        InterServerHandler.ChangeChannel(ch, client, this);
    }

    /**
     * GMS Mode my version. Oliver's Idea
     */
    public final void cancelGMSMode() {
        GMSMode = 0;
        autoap = 0;
        skills.clear();
        maxSkills(true);
        changeChannel((byte) client.getChannel());
        dropMessage("You are no longer in GMS mode. You have " + storageAp + " AP in Storage");
    }

    public void setGMSMode(byte type) {
        if (isHokage()) {
            GMSMode = type;
            return;
        }
        if (reborn < 5) {
            dropMessage("You need to be above 5 RB to be able to turn on GMS mode");
            return;
        }
        if (!unequipEverything()) {
            dropMessage("You don't seem to have enough inventory Slots");
            return;
        }
        if (!stats.addAllApToStorage(type)) {
            return;
        }
        autoap = 5;
        GMSMode = type;
        exp = 0;
        skills.clear();
        this.maxSkills(false);
        changeChannel((byte) client.getChannel());
        dropMessage("[The Elite ninja Gang] You are now in GMS mode. You will not gain Exp by killing mobs. You will how ever gain stats and elevel ups from PQs.");
        dropMessage("[The Elite ninja Gang] You cannot stalk skills from other Jobs when you are in GMS mode. Your skills will be wiped when you change jobs");
        dropMessage("[The Elite Ninja Gang] Your level will not change when you level in GMS mode. Your AP will automagically be added to your storage Ap.");
    }

    public byte getGMSMode() {
        return GMSMode;
    }

    public void maxJobSkills(boolean shit) {
        switch (job) {
            case 112:
                maxHeroSkills(shit);
                break;
            case 122:
                maxPaladinSkills(shit);
                break;
            case 132:
                maxDarkKnightSkills(shit);
                break;
            case 212:
                maxFPArchMageSkills(shit);
                break;
            case 222:
                maxILArchMageSkills(shit);
                break;
            case 232:
                maxBishopSkills(shit);
                break;
            case 312:
                maxBowMasterSkills(shit);
                break;
            case 322:
                maxCrossBowMasterSkills(shit);
                break;
            case 412:
                maxNightLordSkills(shit);
                break;
            case 422:
                maxShadowerSkills(shit);
                break;
            case 512:
                maxBuccaneerSkills(shit);
                break;
            case 522:
                maxCorsairSkills(shit);
                break;
            case 1111:
                maxDawnWarrior3Skills(shit);
                break;
            case 1211:
                maxBlazeWizard3Skills(shit);
                break;
            case 1311:
                maxWindArcher3Skills(shit);
                break;
            case 1411:
                maxNightWalker3Skills(shit);
                break;
            case 1511:
                maxThunderBreaker3Skills(shit);
                break;
        }
    }

    public void wipeSkills() {
        skills.clear();
    }

    // .....dojo!
    public void mulung_EnergyModify(boolean inc) {
        if (inc) {
            if (dojoenergy + 100 > 10000) {
                dojoenergy = 10000;
            } else {
                dojoenergy += 100;
            }
        } else {
            dojoenergy = 0;
        }
        client.getSession().write(MaplePacketCreator.MulungEnergy(dojoenergy));
    }

    public void writeMulungEnergy() {
        client.getSession().write(MaplePacketCreator.MulungEnergy(dojoenergy));
    }

    public final short getCombo() {
        return combo;
    }

    public void setCombo(final short combo) {
        this.combo = combo;
    }

    public final long getLastCombo() {
        return lastCombo;
    }

    public void setLastCombo(final long combo) {
        this.lastCombo = combo;
    }

    public final long getKeyDownSkill_Time() {
        return keydown_skill;
    }

    public void setKeyDownSkill_Time(final long keydown_skill) {
        this.keydown_skill = keydown_skill;
    }

    public final int getDojoPoints() {
        return dojoPoints;
    }

    public final byte getDojoStage() {
        return lastDojoStage;
    }

    public void setDojoPoints(final int x) {
        this.dojoPoints = x;
    }

    public void setDojoRecord(final boolean reset) {
        if (reset) {
            dojoPoints = 0;
            lastDojoStage = 0;
        } else {
            lastDojoStage++;
        }
    }

    public void resetEnteredScript() {
        if (entered.containsKey(map.getId())) {
            entered.remove(map.getId());
        }
    }

    public void resetEnteredScript(int mapId) {
        if (entered.containsKey(mapId)) {
            entered.remove(mapId);
        }
    }

    public void resetEnteredScript(String script) {
        for (int mapId : entered.keySet()) {
            if (entered.get(mapId).equals(script)) {
                entered.remove(mapId);
            }
        }
    }

    public void enteredScript(String script, int mapid) {
        if (!entered.containsKey(mapid)) {
            entered.put(mapid, script);
        }
    }

    public boolean hasEntered(String script) {
        for (int mapId : entered.keySet()) {
            if (entered.get(mapId).equals(script)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEntered(String script, int mapId) {
        if (entered.containsKey(mapId)) {
            if (entered.get(mapId).equals(script)) {
                return true;
            }
        }
        return false;
    }

    public int addDojoPointsByMap() {
        int pts = 0;
        int oldpts = dojoPoints;
        pts = 2 + (int) Math.floor(((getMap().getId() - 1) / 100 % 100) / 6);
        if (party != null) {
            pts--;
        }
        if (dojoPoints < 170000) {
            this.dojoPoints += pts;
            checkForBelts(oldpts);
        }
        return pts;
    }

    private void checkForBelts(int oldpts) {
        int[] beltpoints = {1000, 8000, 25000, 60000, 120000};
        int[] belts = {1132000, 1132001, 1132002, 1132003, 1132004};
        int[] stat = {250, 500, 1337, 3337, 13337};
        for (int i = 0; i < beltpoints.length; i++) {
            if (beltpoints[i] < oldpts && dojoPoints >= beltpoints[i] && !haveItem(belts[i], 1, true, true)) {
                MapleInventoryManipulator.addStatItemById(client, belts[i], name, (short) stat[i], (short) 1, (short) 1);
                showMessage(1, "Congratulations on acheiving a belt!");
            }
        }
    }

    public void startMapEffect(String msg, int itemId) {
        startMapEffect(msg, itemId, 30000);
    }

    public void startMapEffect(String msg, int itemId, int duration) {
        final MapleMapEffect mapEffect = new MapleMapEffect(msg, itemId);
        getClient().getSession().write(mapEffect.makeStartData());
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                getClient().getSession().write(mapEffect.makeDestroyData());
            }
        }, duration);
    }

    public void startCygnusIntro() {
        client.getSession().write(MaplePacketCreator.CygnusIntroDisableUI(true));
        client.getSession().write(MaplePacketCreator.CygnusIntroLock(true));
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                client.getSession().write(MaplePacketCreator.CygnusIntroDisableUI(false));
                client.getSession().write(MaplePacketCreator.CygnusIntroLock(false));
                changeMap(913040006);
            }
        }, 54 * 1000); // 52 second = intro with "Please Help, Be my Knight..." 40 sec = without
    }

    public int getJobRank() {
        return jobrank;
    }

    public int getJobRankMove() {
        return jobrankmove;
    }

    public int getRank() {
        return rank;
    }

    public int getRankMove() {
        return rankmove;
    }

    public int getClantaorank() {
        return clantaorank;
    }

    public int getTaorank() {
        return taorank;
    }

    // autobuffs
    public void rebuff() {
        if (GMSMode > 0 && !isJounin()) {
            dropMessage(5, "[Meow]You could have got rebuff, but you are in GMS mode. So bleh ");
            return;
        }

        if (autobuffs != null) {
            for (int i : autobuffs) {
                SkillFactory.getSkill(i).getEffect(SkillFactory.getSkill(i).getMaxLevel()).applyTo(this);
            }
        } else {
            dropMessage("Looks like you have no buffs stored for Autobuff. Talk to Donation box NPC in Henesys");
        }
    }

    public List<Integer> getAutobuffs() {
        return autobuffs;
    }

    public void addAutobuff(int buff) {
        autobuffs.add(buff);
        autobuffchange = true;
    }

    public void removeAutobuff(int id) {
        autobuffs.remove(id);
        autobuffchange = true;
    }

    // Boss Quest
    public int getBossPoints() {
        return bossPoints;
    }

    public void setBossPoints(int bossPoints) {
        this.bossPoints = bossPoints;
    }

    // MiniGames
    public MiniGame getMiniGame() {
        return miniGame;
    }

    public int getMiniGamePoints(String type, boolean omok) {
        if (omok) {
            if (type.equals("wins")) {
                return omokwins;
            } else if (type.equals("losses")) {
                return omoklosses;
            } else {
                return omokties;
            }
        } else {
            if (type.equals("wins")) {
                return matchcardwins;
            } else if (type.equals("losses")) {
                return matchcardlosses;
            } else {
                return matchcardties;
            }
        }
    }

    public void setMiniGame(MiniGame miniGame) {
        this.miniGame = miniGame;
    }

    public void setMiniGamePoints(boolean win, boolean loss, boolean omok) {
        if (omok) {
            if (win) {
                this.omokwins++;
            } else if (loss) {
                this.omoklosses++;
            } else {
                this.omokties++;
            }
        } else {
            if (win) {
                this.matchcardwins++;
            } else if (loss) {
                this.matchcardwins++;
            } else {
                this.matchcardties++;
            }
        }
    }

    public HiredMerchant getHiredMerchant() {
        return hiredMerchant;
    }

    public void setHasMerchant(boolean set) {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE characters SET HasMerchant = ? WHERE id = ?");
            ps.setInt(1, set ? 1 : 0);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            return;
        }
        hasMerchant = set;
    }

    public void setHiredMerchant(HiredMerchant merchant) {
        this.hiredMerchant = merchant;
    }

    public boolean hasMerchant() {
        return hasMerchant;
    }

    public int getSlot() {
        return slots;
    }

    public void setSlot(int slots) {
        this.slots = slots;
    }

    // Rings
    // fff
    public int getEquippedRing(int type) {
        for (IItem item : getInventory(MapleInventoryType.EQUIPPED)) {
            IEquip equip = (IEquip) item;
            if (equip.getRingId() > 0) {
                int itemId = equip.getItemId();
                if ((itemId >= 1112001 && itemId <= 1112007 || itemId == 1112012) && type == 0) {
                    return equip.getRingId();
                }
                if (itemId >= 1112800 && itemId <= 1112802 && type == 1) {
                    return equip.getRingId();
                }
                if ((itemId >= 1112803 && itemId <= 1112807 || itemId == 1112809) && type == 2) {
                    return equip.getRingId();
                }
            }
        }
        return 0;
    }

    public boolean isRingEquipped(int ringId) {
        for (IItem item : getInventory(MapleInventoryType.EQUIPPED)) {
            IEquip equip = (IEquip) item;
            if (equip.getRingId() == ringId) {
                return equip.getPosition() <= (byte) -1;
            }
        }
        return false;
    }

    public List<MapleRing> getCrushRings() {
        Collections.sort(crushRings);
        return crushRings;
    }

    public List<MapleRing> getFriendshipRings() {
        Collections.sort(friendshipRings);
        return friendshipRings;
    }

    public List<MapleRing> getMarriageRings() {
        Collections.sort(marriageRings);
        return marriageRings;
    }

    public void addRingToCache(int ringId) {
        MapleRing ring = MapleRing.loadFromDb(ringId);
        if (ring.getItemId() >= 1112001 && ring.getItemId() <= 1112007 || ring.getItemId() == 1112012) {
            if (ring != null) {
                crushRings.add(ring);
            }
        } else if (ring.getItemId() >= 1112800 && ring.getItemId() <= 1112802) {
            if (ring != null) {
                friendshipRings.add(ring);
            }
        } else if (ring.getItemId() >= 1112803 && ring.getItemId() <= 1112807 || ring.getItemId() == 1112809) {
            if (ring != null) {
                marriageRings.add(ring);
            }
        }
    }

    public void relog() {
        client.getSession().write(MaplePacketCreator.getCharInfo(this));
        getMap().removePlayer(this);
        getMap().addPlayer(this);
    }

    public final byte getVillage() {
        return village;
    }

    public void setVillage(final byte lol) {
        this.village = lol;
    }

    public final byte getTextColour() {
        return textColour;
    }

    public void setTextColour(final byte textColour) {
        this.textColour = textColour;
    }

    public String getFootnote() {
        return footnote;
    }

    public void addFootnote(String note) {
        StringBuilder sb = new StringBuilder();
        if (footnote != null) {
            sb.append(this.footnote);
        }
        sb.append(note);
        sb.append(" || ");
        footnote = sb.toString();
    }

    public byte getMission() {
        return mission;
    }

    public void missionComplete() {
        this.mission++;
    }

    // Item Filter
    public ArrayList<Integer> getExcluded() {
        return excluded;
    }

    public void addExcluded(int x) {
        excluded.add(x);
    }

    // KPQ Points
    public int getKpqpoints() {
        return kpqpoints;
    }

    public void gainKpqpoints() {
        kpqpoints++;
    }

    public void setKpqpoints(int kpqpoints) {
        this.kpqpoints = kpqpoints;
    }

    public int getLmpoints() {
        return lmpoints;
    }

    public void setLmpoints(int lmpoints) {
        this.lmpoints = lmpoints;
    }

    public void addLmpoints() {
        lmpoints++;
    }

    public void startHurtHp() {
        hpDecreaseTask = TimerManager.getInstance().register(new Runnable() {

            @Override
            public void run() {
                if (map.getHPDec() < 1 || !isAlive()) {
                    return;
                } else if (getInventory(MapleInventoryType.EQUIPPED).findById(map.getHPDecProtect()) == null) {
                    addHP(-map.getHPDec());
                }
            }
        }, 10000);
    }

    public void setMonsterBookCover(int bookCover) {
        this.bookCover = bookCover;
    }

    public int getMonsterBookCover() {
        return bookCover;
    }

    public MonsterBook getMonsterBook() {
        return monsterbook;
    }

    public IMaplePlayerShop getPlayerShop() {
        return playerShop;
    }

    public void setPlayerShop(IMaplePlayerShop playerShop) {
        this.playerShop = playerShop;
    }

    public int getConversation() {
        return inst.get();
    }

    public void setConversation(int inst) {
        this.inst.set(inst);
    }

    public MapleCarnivalParty getCarnivalParty() {
        return carnivalParty;
    }

    public void setCarnivalParty(MapleCarnivalParty party) {
        carnivalParty = party;
    }

    public void addCP(int ammount) {
        totalCP += ammount;
        availableCP += ammount;
    }

    public void useCP(int ammount) {
        availableCP -= ammount;
    }

    public int getAvailableCP() {
        return availableCP;
    }

    public int getTotalCP() {
        return totalCP;
    }

    public void resetCP() {
        totalCP = 0;
        availableCP = 0;
    }

    public void addCarnivalRequest(MapleCarnivalChallenge request) {
        pendingCarnivalRequests.add(request);
    }

    public final MapleCarnivalChallenge getNextCarnivalRequest() {
        return pendingCarnivalRequests.pollLast();
    }

    public void clearCarnivalRequests() {
        pendingCarnivalRequests = new LinkedList<MapleCarnivalChallenge>();
    }

    public void startMonsterCarnival(final int enemyavailable, final int enemytotal) {
        client.getSession().write(MonsterCarnivalPacket.startMonsterCarnival(this, enemyavailable, enemytotal));
    }

    public void CPUpdate(final boolean party, final int available, final int total, final int team) {
        client.getSession().write(MonsterCarnivalPacket.CPUpdate(party, available, total, team));
    }

    public void playerDiedCPQ(final String name, final int lostCP, final int team) {
        client.getSession().write(MonsterCarnivalPacket.playerDiedMessage(name, lostCP, team));
    }

    public int[] getRocks() {
        return rocks;
    }

    public int getRockSize() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            if (rocks[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteFromRocks(int map) {
        for (int i = 0; i < 10; i++) {
            if (rocks[i] == map) {
                rocks[i] = -1;
                break;
            }
        }
    }

    public void addRockMap() {
        if (getRockSize() >= 10) {
            return;
        }
        rocks[getRockSize()] = getMapId();
    }

    public boolean isRockMap(int id) {
        for (int i = 0; i < 10; i++) {
            if (rocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    public int[] getWishlist() {
        return wishlist;
    }

    public void clearWishlist() {
        for (int i = 0; i < 10; i++) {
            wishlist[i] = 0;
        }
    }

    public int getWishlistSize() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            if (wishlist[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    public MapleMount getMount() {
        return mount;
    }

    public int getTaoCheck() {
        return this.taocheck;
    }

    public byte getSmegaStatus() {
        return this.smega;
    }
}
