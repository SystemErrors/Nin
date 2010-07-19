package net.sf.odinms.scripting;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Inventory.MaplePet;
import net.sf.odinms.client.NinjaMS.Rebirths;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.guild.MapleGuild;
import net.sf.odinms.scripting.event.EventInstanceManager;
import net.sf.odinms.scripting.event.EventManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.constants.InventoryConstants;
import net.sf.odinms.server.constants.Modes;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleReactor;
import net.sf.odinms.server.maps.SavedLocationType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Packets.PetPacket;

public class AbstractPlayerInteraction {

    private MapleClient c;

    public AbstractPlayerInteraction(MapleClient c) {
        this.c = c;
    }

    public final MapleClient getClient() {
        return c;
    }

    public final MapleCharacter getPlayer() {
        return c.getPlayer();
    }

    public final MapleCharacter p() {
        return c.getPlayer();
    }

    public final EventManager getEventManager(final String event) {
        return c.getChannelServer().getEventSM().getEventManager(event);
    }

    public final EventInstanceManager getEventInstance() {
        return c.getPlayer().getEventInstance();
    }

    public final void playPortalSE() {
	c.getSession().write(MaplePacketCreator.showOwnBuffEffect(0, 7));
    }

    public final void warp(int map) {
        MapleMap target = getWarpMap(map);
        c.getPlayer().changeMap(target, target.getPortal(0));
    }

    public final void warp(String map) {
        MapleMap target = getWarpMap(Integer.parseInt(map));
        c.getPlayer().changeMap(target, target.getPortal(0));
    }

    public final void warp(long map) {
        MapleMap target = getWarpMap((int) map);
        c.getPlayer().changeMap(target, target.getPortal(0));
    }

    public final void warp(int map, int portal) {
        MapleMap target = getWarpMap(map);
        c.getPlayer().changeMap(target, target.getPortal(portal));
    }

    public final void warp(int map, String portal) {
        MapleMap target = getWarpMap(map);
        c.getPlayer().changeMap(target, target.getPortal(portal));
    }

    private final MapleMap getWarpMap(int map) {
        MapleMap target;
        if (getPlayer().getEventInstance() == null) {
            target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(map);
        } else {
            target = getPlayer().getEventInstance().getMapInstance(map);
        }
        return target;
    }

    public final void spawnNpc(final int npcId) {
        c.getPlayer().getMap().spawnNpc(npcId, c.getPlayer().getPosition());
    }

    public final void spawnNpc(final int npcId, final int x, final int y) {
        c.getPlayer().getMap().spawnNpc(npcId, new Point(x, y));
    }

    public final void spawnNpc(final int npcId, final Point pos) {
        c.getPlayer().getMap().spawnNpc(npcId, pos);
    }

    public final void removeNpc(final int mapid, final int npcId) {
        c.getChannelServer().getMapFactory().getMap(mapid).removeNpc(npcId);
    }

    public final void forceStartReactor(final int mapid, final int id) {
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);
        MapleReactor react;
        for (final MapleMapObject remo : map.getAllReactor()) {
            react = (MapleReactor) remo;
            if (react.getReactorId() == id) {
                react.forceStartReactor(c);
                break;
            }
        }
    }

    public final void destroyReactor(final int mapid, final int id) {
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);
        MapleReactor react;

        for (final MapleMapObject remo : map.getAllReactor()) {
            react = (MapleReactor) remo;
            if (react.getReactorId() == id) {
                react.hitReactor(c);
                break;
            }
        }
    }

    public final void hitReactor(final int mapid, final int id) {
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);
        MapleReactor react;

        for (final MapleMapObject remo : map.getAllReactor()) {
            react = (MapleReactor) remo;
            if (react.getReactorId() == id) {
                react.hitReactor(c);
                break;
            }
        }
    }

    public final MapleMap getMap(int map) {
        return getWarpMap(map);
    }

    public final boolean haveItem(int itemid) {
        return haveItem(itemid, 1);
    }

    public final boolean haveItem(int itemid, int quantity) {
        return haveItem(itemid, quantity, false, true);
    }

    public final boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
        return c.getPlayer().haveItem(itemid, quantity, checkEquipped, greaterOrEquals);
    }

    public String getName() {
        return getPlayer().getName();
    }

    /**
     * Gives item with the specified id or takes it if the quantity is negative. Note that this does NOT take items from the equipped inventory.
     * @param id
     * @param quantity
     */
    public final void gainItem(int id, short quantity) {
        if (quantity >= 0) {
            MapleInventoryManipulator.addById(c, id, quantity);
        } else {
            MapleInventoryManipulator.removeById(c, InventoryConstants.getInventoryType(id), id, -quantity, true, false);
        }
        c.getSession().write(MaplePacketCreator.getShowItemGain(id, quantity, true));
    }

    public final void gainExpiringItem(int itemid, int minutes) {
        getPlayer().gainExpiringItem(itemid, minutes);
    }

    public final void gainTaggedItem(int id, int quantity) {
        if (quantity >= 0) {
            StringBuilder logInfo = new StringBuilder(c.getPlayer().getName());
            logInfo.append(" received ");
            logInfo.append(quantity);
            logInfo.append(" from a scripted PlayerInteraction (");
            logInfo.append(this.toString());
            logInfo.append(")");
            MapleInventoryManipulator.addById(c, id, (short) quantity, c.getPlayer().getName());
        } else {
            MapleInventoryManipulator.removeById(c, InventoryConstants.getInventoryType(id), id, -quantity, true, false);
        }
        c.getSession().write(MaplePacketCreator.getShowItemGain(id, (short) quantity, true));
    }

    public final void gainStatItem(int id, short stat, short wa, short ma) {
        MapleInventoryManipulator.addStatItemById(c, id, c.getPlayer().getName(), stat, wa, ma);
        c.getSession().write(MaplePacketCreator.getShowItemGain(id, (short) 1, true));
        dropMessage("You have gained a stat Item. Itemid : " + id + " Stats : " + stat + "WA : " + wa + " MA : " + ma);
    }

    public final void changeMusic(String songName) {
        getPlayer().getMap().broadcastMessage(MaplePacketCreator.musicChange(songName));
    }

    // default playerMessage and mapMessage to use type 5
    public final void playerMessage(String message) {
        playerMessage(5, message);
    }

    public final void mapMessage(String message) {
        mapMessage(5, message);
    }

    public final void guildMessage(String message) {
        guildMessage(5, message);
    }

    public final void playerMessage(int type, String message) {
        c.getSession().write(MaplePacketCreator.serverNotice(type, message));
    }

    public final void mapMessage(int type, String message) {
        getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(type, message));
    }

    public final void guildMessage(int type, String message) {
        MapleGuild guild = getGuild();
        if (guild != null) {
            guild.guildMessage(MaplePacketCreator.serverNotice(type, message));
            //guild.broadcast(MaplePacketCreator.serverNotice(type, message));
        }
    }

    public final MapleGuild getGuild() {
        try {
            return c.getChannelServer().getWorldInterface().getGuild(getPlayer().getGuildId(), null);
        } catch (RemoteException ex) {
            Logger.getLogger(AbstractPlayerInteraction.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public final MapleParty getParty() {
        return (c.getPlayer().getParty());
    }

    public List<MapleCharacter> getPartyMembers() {
        if (getPlayer().getParty() == null) {
            return null;
        }
        List<MapleCharacter> chars = new LinkedList<MapleCharacter>();
        for (ChannelServer channel : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : channel.getPartyMembers(getPlayer().getParty())) {
                if (chr != null) {
                    chars.add(chr);
                }
            }
        }
        return chars;
    }

    public final boolean isLeader() {
        return (getParty().getLeader().equals(new MaplePartyCharacter(c.getPlayer())));
    }

    public final boolean isPartyLeader() {
        return (getParty().getLeader().equals(new MaplePartyCharacter(c.getPlayer())));
    }

    public final int itemQuantity(int itemid) {
        MapleInventoryType type = InventoryConstants.getInventoryType(itemid);
        MapleInventory iv = getPlayer().getInventory(type);
        int possesed = iv.countById(itemid);
        return possesed;
    }
    //PQ methods: give items/exp to all party members

    public final void givePartyItems(int id, short quantity, List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            MapleClient cl = chr.getClient();
            if (quantity >= 0) {
                StringBuilder logInfo = new StringBuilder(cl.getPlayer().getName());
                logInfo.append(" received ");
                logInfo.append(quantity);
                logInfo.append(" from event ");
                logInfo.append(chr.getEventInstance().getName());
                MapleInventoryManipulator.addById(cl, id, quantity, logInfo.toString());
            } else {
                MapleInventoryManipulator.removeById(cl, InventoryConstants.getInventoryType(id), id, -quantity, true, false);
            }
            cl.getSession().write(MaplePacketCreator.getShowItemGain(id, quantity, true));
        }
    }

    //PQ gain EXP: Multiplied by channel rate here to allow global values to be input direct into NPCs
    public final void givePartyExp(int amount, List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            chr.gainExp(amount * c.getChannelServer().getExpRate(), true, true);
        }
    }

    // Ninja HAX
    public final void levelPartyToMax(List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            while (chr.getLevel() < chr.getMaxLevel()) {
                chr.levelUp();
            }
            Rebirths.doReborn(chr, false);
            chr.dropMessage("You have been leveled up to your max level and Rebirthed into the Same Job");
        }
    }

    public final boolean checkPartyGMSMode(int mode, List<MaplePartyCharacter> party) {
        boolean x = true;
        for (MaplePartyCharacter chr : party) {
            MapleCharacter other = ChannelServer.getInstance(c.getChannel()).getPlayerStorage().getCharacterByName(chr.getName());
            if (other.getGMSMode() != mode && !other.isJounin()) {
                x = false;
            }
        }
        return x;
    }

    public String membersNotQualified(int mode, List<MaplePartyCharacter> party) {
        String mnq = "";
        for (MaplePartyCharacter chr : party) {
            MapleCharacter other = ChannelServer.getInstance(c.getChannel()).getPlayerStorage().getCharacterByName(chr.getName());
            if (other.getGMSMode() != mode && !other.isJounin()) {
                mnq += " || " + chr.getName() + " || ";
            }
        }
        return mnq;
    }

    //remove all items of type from party
    //combination of haveItem and gainItem
    public final void removeFromParty(int id, List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            MapleClient cl = chr.getClient();
            MapleInventoryType type = InventoryConstants.getInventoryType(id);
            MapleInventory iv = cl.getPlayer().getInventory(type);
            int possesed = iv.countById(id);
            if (possesed > 0) {
                MapleInventoryManipulator.removeById(c, InventoryConstants.getInventoryType(id), id, possesed, true, false);
                cl.getSession().write(MaplePacketCreator.getShowItemGain(id, (short) -possesed, true));
            }
        }
    }

    public final void removeFromPty(int id, List<MaplePartyCharacter> party) {
        for (MaplePartyCharacter chr : party) {
            MapleCharacter other = ChannelServer.getInstance(c.getChannel()).getPlayerStorage().getCharacterByName(chr.getName());
            if (other.haveItem(id, 1)) {
                int lol = other.getItemQuantity(id, false);
                other.gainItem(id, -lol);
            }
        }
    }
    //remove all items of type from character
    //combination of haveItem and gainItem

    public final void removeAll(int id) {
        removeAll(id, c);
    }

    //remove all items of type from character
    //combination of haveItem and gainItem
    public final void removeAll(int id, MapleClient cl) {
        MapleInventoryType type = InventoryConstants.getInventoryType(id);
        MapleInventory iv = cl.getPlayer().getInventory(type);
        int possessed = iv.countById(id);
        if (possessed > 0) {
            MapleInventoryManipulator.removeById(cl, InventoryConstants.getInventoryType(id), id, possessed, true, false);
            cl.getSession().write(MaplePacketCreator.getShowItemGain(id, (short) -possessed, true));
        }
    }

    public final void gainCloseness(int closeness, int index) {
        MaplePet pet = getPlayer().getPet(index);
        if (pet != null) {
            pet.setCloseness(pet.getCloseness() + closeness);
            getClient().getSession().write(PetPacket.updatePet(pet, true));
        }
    }

    public final void gainClosenessAll(int closeness) {
        for (MaplePet pet : getPlayer().getPets()) {
            if (pet != null) {
                pet.setCloseness(pet.getCloseness() + closeness);
                getClient().getSession().write(PetPacket.updatePet(pet, true));
            }
        }
    }

    public final boolean canHold(int itemid) {
        MapleInventoryType type = InventoryConstants.getInventoryType(itemid);
        MapleInventory iv = c.getPlayer().getInventory(type);
        return iv.getNextFreeSlot() > -1;
    }

    public final int getMapId() {
        return c.getPlayer().getMap().getId();
    }

    public final int getPlayerCount(int mapid) {
        return c.getChannelServer().getMapFactory().getMap(mapid).getCharacters().size();
    }

    public final int getCurrentPartyId(int mapid) {
        return getMap(mapid).getCurrentPartyId();
    }

    public final void levelUp() {
        c.getPlayer().levelUp();
    }

    public final void environmentChange(String env, int mode) {
        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.environmentChange(env, mode));
    }

    public final void warpParty(int mapId) {
        MapleMap target = getMap(mapId);
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
            }
        }
    }

    public final void setModeOn(int x) {
        switch (x) {
            case 1:
                Modes.getInstance(getPlayer()).setKyubi();
                break;
            case 2:
                Modes.getInstance(getPlayer()).setSage();
                break;
            case 3:
                Modes.getInstance(getPlayer()).setHachibi();
                break;
            case 4:
                Modes.getInstance(getPlayer()).setShakaku();
                break;
            default:
                Modes.getInstance(getPlayer()).setAllModeOn();
                break;
        }
    }

    public final void showInstruction(String msg, int width, int height) {
        c.getSession().write(MaplePacketCreator.sendHint(msg, width, height));
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public final boolean isRestingSpot(int id) {
        // Resting rooms :
        // 925020600 ~ 925020609
        // 925021200 ~ 925021209
        // 925021800 ~ 925021809
        // 925022400 ~ 925022409
        // 925023000 ~ 925023009
        // 925023600 ~ 925023609
        int shortid = id / 100;

        switch (shortid) {
            case 9250206:
            case 9250212:
            case 9250218:
            case 9250224:
            case 9250230:
            case 9250236:
                return true;
        }
        return false;
    }

    public final int getPlayerQuantity(int mid) {
        return getPlayer().getClient().getChannelServer().getMapFactory().getMap(mid).getCharacters().size();
    }

    public final void clearDojoMap(int mid) {
        MapleMap map = getPlayer().getClient().getChannelServer().getMapFactory().getMap(mid);

        map.killAllMonsters(false);
        map.resetReactors();
        map.clearDrops();
    }

    public final void dropMessage(String message) {
        dropMessage(6, message);
    }

    public final void dropMessage(int type, String message) {
        getPlayer().dropMessage(type, message);
    }

    public final void giveRebirth(int amount) {
        Rebirths.giveRebirth(p(), amount);
    }

    public final void giveRebirth() {
        Rebirths.giveRebirth(p());
    }

    public final void saveLocation(final String loc) {
        c.getPlayer().saveLocation(SavedLocationType.fromString(loc));
    }

    public final void clearSavedLocation(final String loc) {
        c.getPlayer().clearSavedLocation(SavedLocationType.fromString(loc));
    }
}
