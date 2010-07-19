/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
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
package net.sf.odinms.net.channel.handler;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import net.sf.odinms.client.ExpTable;
import net.sf.odinms.client.Inventory.Equip;
import net.sf.odinms.client.Inventory.IEquip;
import net.sf.odinms.client.Inventory.IEquip.ScrollResult;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.ItemFlag;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Inventory.MapleMount;
import net.sf.odinms.client.Inventory.MaplePet;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.NinjaMS.Processors.SmegaProcessor;
import net.sf.odinms.client.Skills.SkillFactory;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.server.StructRewardItem;
import net.sf.odinms.server.constants.InventoryConstants;
import net.sf.odinms.server.constants.SpecialStuff;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.FieldLimitType;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Packets.MTSCSPacket;
import net.sf.odinms.tools.Packets.PetPacket;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.Randomizer;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class UseItemsHandler {

    public static final void UseRewardItem(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final IItem toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId) {
            if (chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1
                    && chr.getInventory(MapleInventoryType.USE).getNextFreeSlot() > -1
                    && chr.getInventory(MapleInventoryType.SETUP).getNextFreeSlot() > -1
                    && chr.getInventory(MapleInventoryType.ETC).getNextFreeSlot() > -1) {
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final Pair<Integer, List<StructRewardItem>> rewards = ii.getRewardItem(itemId);

                if (rewards != null) {
                    for (StructRewardItem reward : rewards.getRight()) {
                        if (Randomizer.nextInt(rewards.getLeft()) < reward.prob) { // Total prob
                            if (InventoryConstants.getInventoryType(reward.itemid) == MapleInventoryType.EQUIP) {
                                final IItem item = ii.getEquipById(reward.itemid);
                                if (reward.period != -1) {
                                    item.setExpiration(System.currentTimeMillis() + (reward.period * 60 * 60 * 10));
                                }
                                MapleInventoryManipulator.addbyItem(c, item);
                            } else {
                                MapleInventoryManipulator.addById(c, reward.itemid, reward.quantity);
                            }
                            MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemId, 1, false, false);

                            c.getSession().write(MaplePacketCreator.showRewardItemAnimation(reward.itemid, reward.effect));
                            chr.getMap().broadcastMessage(chr, MaplePacketCreator.showRewardItemAnimation(reward.itemid, reward.effect, chr.getId()), false);
                            break;
                        }
                    }
                }
            } else {
                chr.dropMessage(6, "Insufficient inventory slot.");
            }
        }
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static final void UseItem(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (!chr.isAlive()) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        slea.skip(4);
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit())) {
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
            }
        } else {
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }

    public static final void UseReturnScroll(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (!chr.isAlive()) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        slea.skip(4);
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit())) {
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyReturnScroll(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
            } else {
                c.getSession().write(MaplePacketCreator.enableActions());
            }
        } else {
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }

    public static final void UseUpgradeScroll(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        slea.skip(4);
        final byte slot = (byte) slea.readShort();
        final byte dst = (byte) slea.readShort();
        final byte ws = (byte) slea.readShort();
        boolean whiteScroll = false; // white scroll being used?
        boolean legendarySpirit = false; // legendary spirit skill
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if ((ws & 2) == 2) {
            whiteScroll = true;
        }
        IEquip toScroll;
        if (dst < 0) {
            toScroll = (IEquip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        } else { // legendary spirit
            legendarySpirit = true;
            toScroll = (IEquip) chr.getInventory(MapleInventoryType.EQUIP).getItem(dst);
        }
        final byte oldLevel = toScroll.getLevel();
        final byte oldFlag = toScroll.getFlag();

        if (!InventoryConstants.isSpecialScroll(toScroll.getItemId()) && !InventoryConstants.isCleanSlate(toScroll.getItemId())) {
            if (toScroll.getUpgradeSlots() < 1) {
                c.getSession().write(MaplePacketCreator.getInventoryFull());
                return;
            }
        }
        IItem scroll = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        IItem wscroll = null;

        // Anti cheat and validation
        List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
        if (scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
            c.getSession().write(MaplePacketCreator.getInventoryFull());
            return;
        }
        if (whiteScroll) {
            wscroll = chr.getInventory(MapleInventoryType.USE).findById(2340000);
            if (wscroll == null || wscroll.getItemId() != 2340000) {
                whiteScroll = false;
            }
        }
        if (!InventoryConstants.isChaosScroll(scroll.getItemId()) && !InventoryConstants.isCleanSlate(scroll.getItemId())) {
            if (!ii.canScroll(scroll.getItemId(), toScroll.getItemId())) {
                return;
            }
        }
        if (scroll.getQuantity() <= 0) {
            return;
        } 
        // Scroll Success/ Failure/ Curse
        final IEquip scrolled = (IEquip) ii.scrollEquipWithId(toScroll, scroll.getItemId(), whiteScroll, chr.isJounin());
        ScrollResult scrollSuccess;
        if (scrolled == null) {
            scrollSuccess = IEquip.ScrollResult.CURSE;
        } else if (scrolled.getLevel() > oldLevel) {
            scrollSuccess = IEquip.ScrollResult.SUCCESS;
        } else if ((InventoryConstants.isCleanSlate(scroll.getItemId()) && scrolled.getLevel() == oldLevel + 1)) {
            scrollSuccess = IEquip.ScrollResult.SUCCESS;
        } else if ((InventoryConstants.isSpecialScroll(scroll.getItemId()) && scrolled.getFlag() > oldFlag)) {
            scrollSuccess = IEquip.ScrollResult.SUCCESS;
        } else {
            scrollSuccess = IEquip.ScrollResult.FAIL;
        }

        // Update
        chr.getInventory(MapleInventoryType.USE).removeItem(scroll.getPosition(), (short) 1, false);
        if (whiteScroll) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, wscroll.getPosition(), (short) 1, false, false);
        }

        if (scrollSuccess == IEquip.ScrollResult.CURSE) {
            c.getSession().write(MaplePacketCreator.scrolledItem(scroll, toScroll, true));
            if (dst < 0) {
                chr.getInventory(MapleInventoryType.EQUIPPED).removeItem(toScroll.getPosition());
            } else {
                chr.getInventory(MapleInventoryType.EQUIP).removeItem(toScroll.getPosition());
            }
        } else {
            c.getSession().write(MaplePacketCreator.scrolledItem(scroll, scrolled, false));
        }

        chr.getMap().broadcastMessage(MaplePacketCreator.getScrollEffect(c.getPlayer().getId(), scrollSuccess, legendarySpirit));

        // equipped item was scrolled and changed
        if (dst < 0 && (scrollSuccess == IEquip.ScrollResult.SUCCESS || scrollSuccess == IEquip.ScrollResult.CURSE)) {
            chr.equipChanged();
        }
    }

    public static final void UseSkillBook(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        /*    slea.skip(4);
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
        return;
        }
        final Map<String, Integer> skilldata = MapleItemInformationProvider.getInstance().getSkillStats(toUse.getItemId());
        if (skilldata == null) { // Hacking or used an unknown item
        return;
        }
        boolean canuse = false, success = false;
        int skill = 0, maxlevel = 0;

        final int SuccessRate = skilldata.get("success");
        final int ReqSkillLevel = skilldata.get("reqSkillLevel");
        final int MasterLevel = skilldata.get("masterLevel");

        byte i = 0;
        Integer CurrentLoopedSkillId;
        for (;;) {
        CurrentLoopedSkillId = skilldata.get("skillid" + i);
        i++;
        if (CurrentLoopedSkillId == null) {
        break; // End of data
        }
        if (Math.floor(CurrentLoopedSkillId / 10000) == chr.getJob()) {
        final ISkill CurrSkillData = SkillFactory.getSkill(CurrentLoopedSkillId);
        if (chr.getSkillLevel(CurrSkillData) >= ReqSkillLevel && chr.getMasterLevel(CurrSkillData) < MasterLevel) {
        canuse = true;
        if (Randomizer.nextInt(99) <= SuccessRate && SuccessRate != 0) {
        success = true;
        final ISkill skill2 = CurrSkillData;
        chr.changeSkillLevel(skill2, chr.getSkillLevel(skill2), (byte) MasterLevel);
        } else {
        success = false;
        }
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        break;
        } else { // Failed to meet skill requirements
        canuse = false;
        }
        }
        }
        c.getSession().write(MaplePacketCreator.useSkillBook(chr, skill, maxlevel, canuse, success));
         */
        c.showMessage("All your skills are already maxxed. You don't need Skill books. Retard!");
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static final void UseCatchItem(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        slea.skip(4);
        final byte slot = (byte) slea.readShort();
        final int itemid = slea.readInt();
        final MapleMonster mob = chr.getMap().getMonsterByOid(slea.readInt());
        final IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mob != null) {
            switch (itemid) {
                case 2270002: { // Characteristic Stone
                    final MapleMap map = chr.getMap();

                    if (mob.getHp() <= mob.getMobMaxHp() / 2) {
                        map.broadcastMessage(MaplePacketCreator.catchMonster(mob.getId(), itemid, (byte) 1));
                        map.killMonster(mob, chr, true, false, (byte) 0);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, false, false);
                    } else {
                        map.broadcastMessage(MaplePacketCreator.catchMonster(mob.getId(), itemid, (byte) 0));
                        chr.dropMessage(5, "The monster has too much physical strength, so you cannot catch it.");
                    }
                    break;
                }
                case 2270000: { // Pheromone Perfume
                    if (mob.getId() != 9300101) {
                        break;
                    }
                    final MapleMap map = c.getPlayer().getMap();

                    map.broadcastMessage(MaplePacketCreator.catchMonster(mob.getId(), itemid, (byte) 1));
                    map.killMonster(mob, chr, true, false, (byte) 0);
                    MapleInventoryManipulator.addById(c, 1902000, (short) 1, null);
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, false, false);
                    break;
                }
                case 2270003: { // Cliff's Magic Cane
                    if (mob.getId() != 9500320) {
                        break;
                    }
                    final MapleMap map = c.getPlayer().getMap();

                    if (mob.getHp() <= mob.getMobMaxHp() / 2) {
                        map.broadcastMessage(MaplePacketCreator.catchMonster(mob.getId(), itemid, (byte) 1));
                        map.killMonster(mob, chr, true, false, (byte) 0);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, false, false);
                    } else {
                        map.broadcastMessage(MaplePacketCreator.catchMonster(mob.getId(), itemid, (byte) 0));
                        chr.dropMessage(5, "The monster has too much physical strength, so you cannot catch it.");
                    }
                    break;
                }
            }
        }
        c.getSession().write(MaplePacketCreator.enableActions());
        //   c.getPlayer().setAPQScore(c.getPlayer().getAPQScore() + 1);
        // c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.updateAriantPQRanking(c.getPlayer().getName(), c.getPlayer().getAPQScore(), false));
    }

    public static final void UseMountFood(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        slea.skip(4);
        final byte slot = (byte) slea.readShort();
        final int itemid = slea.readInt(); //2260000 usually
        final IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        final MapleMount mount = chr.getMount();

        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mount != null) {
            final int fatigue = mount.getFatigue();

            boolean levelup = false;
            mount.setFatigue(-30);

            if (fatigue > 0) {
                mount.increaseExp();
                final int level = mount.getLevel();
                if (mount.getExp() >= ExpTable.getMountExpNeededForLevel(level + 1) && level < 31) {
                    mount.setLevel(level + 1);
                    levelup = true;
                }
            }
            chr.getMap().broadcastMessage(MaplePacketCreator.updateMount(chr, levelup));
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static final void UseScriptedNPCItem(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        slea.skip(4);
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId) {
            switch (toUse.getItemId()) {
                case 2430007: // Blank Compass
                {
                    final MapleInventory inventory = chr.getInventory(MapleInventoryType.SETUP);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);

                    if (inventory.countById(3994102) >= 20 // Compass Letter "North"
                            && inventory.countById(3994103) >= 20 // Compass Letter "South"
                            && inventory.countById(3994104) >= 20 // Compass Letter "East"
                            && inventory.countById(3994105) >= 20) { // Compass Letter "West"
                        MapleInventoryManipulator.addById(c, 2430008, (short) 1); // Gold Compass
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994102, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994103, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994104, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994105, 20, false, false);
                    } else {
                        MapleInventoryManipulator.addById(c, 2430007, (short) 1); // Blank Compass
                    }
                    NPCScriptManager.getInstance().start(c, 2084001);
                    break;
                }
                /*
                case 2430008: // Gold Compass
                {
                chr.saveLocation(SavedLocationType.RICHIE);
                MapleMap map;
                boolean warped = false;

                for (int i = 390001000; i <= 390001004; i++) {
                map = c.getChannelServer().getMapFactory().getMap(i);

                if (map.getCharactersSize() == 0) {
                chr.changeMap(map, map.getPortal(0));
                warped = true;
                break;
                }
                }
                if (warped) { // Removal of gold compass
                MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, 2430008, 1, false, false);
                } else { // Or mabe some other message.
                c.getPlayer().dropMessage(5, "All maps are currently in use, please try again later.");
                }
                break;
                }*/
            }
        }
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static final void UseSummonBag(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr.getCheatTracker().isSpam(15000, 11)) {
            chr.dropMessage(5, "Not so often Sweetie :p ");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (!chr.isAlive()) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        slea.skip(4);
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId) {

            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);

            if (c.getPlayer().isJounin() || !FieldLimitType.SummoningBag.check(chr.getMap().getFieldLimit())) {
                final List<Pair<Integer, Integer>> toSpawn = MapleItemInformationProvider.getInstance().getSummonMobs(itemId);

                if (toSpawn == null) {
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
                MapleMonster ht;
                int type = 0;

                for (int i = 0; i < toSpawn.size(); i++) {
                    if (Randomizer.nextInt(99) <= toSpawn.get(i).getRight()) {
                        ht = MapleLifeFactory.getMonster(toSpawn.get(i).getLeft());
                        chr.getMap().spawnMonster_sSack(ht, chr.getPosition(), type);
                    }
                }
            }
        }
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static final void UseTreasureChest(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        /*        final short slot = slea.readShort();
        final int itemid = slea.readInt();

        final IItem toUse = chr.getInventory(MapleInventoryType.ETC).getItem((byte) slot);
        if (toUse == null || toUse.getQuantity() <= 0 || toUse.getItemId() != itemid) {
        c.getSession().write(MaplePacketCreator.enableActions());
        return;
        }
        int reward;
        int keyIDforRemoval = 0;

        switch (toUse.getItemId()) {
        case 4280000: // Gold box
        reward = RandomRewards.getInstance().getGoldBoxReward();
        keyIDforRemoval = 5490000;
        break;
        case 4280001: // Silver box
        reward = RandomRewards.getInstance().getSilverBoxReward();
        keyIDforRemoval = 5490001;
        break;
        default: // Up to no good
        c.getSession().close();
        return;
        }

        // Get the quantity
        int amount = 1;
        switch (reward) {
        case 2000004:
        amount = 200; // Elixir
        break;
        case 2000005:
        amount = 100; // Power Elixir
        break;
        }
        if (chr.getInventory(MapleInventoryType.CASH).countById(keyIDforRemoval) > 0) {
        final IItem item = MapleInventoryManipulator.addbyId_Gachapon(c, reward, (short) amount);

        if (item == null) {
        chr.dropMessage(5, "Please check your item inventory and see if you have a Master Key, or if the inventory is full.");
        c.getSession().write(MaplePacketCreator.enableActions());
        return;
        }
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, (byte) slot, (short) 1, true);
        MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, keyIDforRemoval, 1, true, false);
        c.getSession().write(MaplePacketCreator.getShowItemGain(reward, (short) amount, true));

        if (InventoryConstants.gachaponRareItem(item.getItemId()) > 0) {
        try {
        c.getChannelServer().getWorldInterface().broadcastMessage(MaplePacketCreator.getGachaponMega(c.getPlayer().getName(), " : Lucky winner of Gachapon!", item, (byte) 2).getBytes());
        } catch (RemoteException e) {
        c.getChannelServer().reconnectWorld();
        }
        }
        } else {
        chr.dropMessage(5, "Please check your item inventory and see if you have a Master Key, or if the inventory is full.");
        c.getSession().write(MaplePacketCreator.enableActions());
        }
         * */
        chr.dropMessage(5, "This feature is not coded Yet!");
        c.getSession().write(MaplePacketCreator.enableActions());

    }

    public static final void UseCashItem(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();

        final IItem toUse = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(slot);
        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }

        boolean used = false;

        switch (itemId) {//	    
            case 2320000: // The Teleport Rock
            case 5041000: // VIP Teleport Rock
            case 5040000: // The Teleport Rock
            case 5040001: { // Teleport Coke
                if (c.getPlayer().getCheatTracker().isSpam(10000, 12)){
                        c.getPlayer().dropMessage(5, "You cannot use Teleport Rocks that often. Wait 10 more seconds.");
                        return;
                }
                if (slea.readByte() == 0) { // Rocktype
                    final int mapid = slea.readInt();
                    final MapleMap target = c.getChannelServer().getMapFactory().getMap(mapid);
                    if (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())
                            && !SpecialStuff.getInstance().canWarpTo(c.getPlayer(), mapid)) { //Makes sure this map doesn't have a forced return map
                        c.getPlayer().changeMap(target, target.getPortal(0));
                        used = true;
                    }
                } else {
                    final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                    if (victim != null && !victim.isJounin()) {
                        if (!FieldLimitType.VipRock.check(c.getChannelServer().getMapFactory().getMap(victim.getMapId()).getFieldLimit())
                                && !SpecialStuff.getInstance().canWarpTo(c.getPlayer(), victim.getMapId())) {
                            if (itemId == 5041000 || (victim.getMapId() / 100000000) == (c.getPlayer().getMapId() / 100000000)) { // Viprock or same continent
                                c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getPosition()));
                                used = true;
                            }
                        }
                    }
                }
                break;
            }
            case 5050000:  // AP Reset               
            case 5050001: // SP Reset (1st job)
            case 5050002: // SP Reset (2nd job)
            case 5050003: // SP Reset (3rd job)
            case 5050004: { // SP Reset (4th job)
                c.showMessage("How did you get this item?");
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            case 5060000: { // Item Tag
                final IItem item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readByte());
                if (item != null && item.getOwner().equals("")) {
                    item.setOwner(c.getPlayer().getName());
                    used = true;
                }
                break;
            }
            case 5520000: { // Karma
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final IItem item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());

                if (item != null) {
                    if (MapleItemInformationProvider.getInstance().isKarmaEnabled(item.getItemId())) {
                        byte flag = item.getFlag();
                        if (type == MapleInventoryType.EQUIP) {
                            flag |= ItemFlag.KARMA_EQ.getValue();
                        } else {
                            flag |= ItemFlag.KARMA_USE.getValue();
                        }
                        item.setFlag(flag);

                        c.getSession().write(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                        used = true;
                    }
                }
                break;
            }
            case 5570000: { // Vicious Hammer
                final byte invType = (byte) slea.readInt(); // Inventory type, Hammered eq is always EQ.
                final Equip item = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                // another int here, D3 49 DC 00
                if (item != null) {
                    if (item.getViciousHammer() <= 2) {
                        item.setViciousHammer((byte) (item.getViciousHammer() + 1));
                        item.setUpgradeSlots((byte) (item.getUpgradeSlots() + 1));

                        c.getSession().write(MaplePacketCreator.updateSpecialItemUse(item, invType));
                        c.getSession().write(MTSCSPacket.ViciousHammer(true, (byte) item.getViciousHammer()));
                        c.getSession().write(MTSCSPacket.ViciousHammer(false, (byte) 0));
                        used = true;
                    }
                }
                break;
            }
            case 5060001: { // Sealing Lock
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final IItem item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    byte flag = item.getFlag();
                    flag |= ItemFlag.LOCK.getValue();
                    item.setFlag(flag);

                    c.getSession().write(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    used = true;
                }
                break;
            }
            case 5061000: { // Sealing Lock 7 days
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final IItem item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    byte flag = item.getFlag();
                    flag |= ItemFlag.LOCK.getValue();
                    item.setFlag(flag);
                    item.setExpiration(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));

                    c.getSession().write(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    used = true;
                }
                break;
            }
            case 5061001: { // Sealing Lock 30 days
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final IItem item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    byte flag = item.getFlag();
                    flag |= ItemFlag.LOCK.getValue();
                    item.setFlag(flag);

                    item.setExpiration(System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000));

                    c.getSession().write(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    used = true;
                }
                break;
            }
            case 5061002: { // Sealing Lock 90 days
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final IItem item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    byte flag = item.getFlag();
                    flag |= ItemFlag.LOCK.getValue();
                    item.setFlag(flag);

                    item.setExpiration(System.currentTimeMillis() + (90 * 24 * 60 * 60 * 1000));

                    c.getSession().write(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    used = true;
                }
                break;
            }
            case 5071000: { // Megaphone
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    if (c.getPlayer().getMute() > 0) {
                        c.getPlayer().dropMessage(5, "The usage of Megapone is currently disabled for you. You have been muted");
                    } else if (c.getPlayer().getCheatTracker().isSpam(10000, 1)){
                        c.getPlayer().dropMessage(5, "You cannot send Smega that often. Wait 10 more seconds.");
                    } else {
                           String message = slea.readMapleAsciiString();
                            if (message.length() > 100) {
                                break;
                            }                           
                        used = SmegaProcessor.processMegaphone(c, message);
                    }
                    break;
                }
            }
            case 5077000: { // 3 line Megaphone
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    if (c.getPlayer().getMute() > 0) {
                        c.getPlayer().dropMessage(5, "The usage of Megapone is currently disabled for you. You have been muted");
                    } else if (c.getPlayer().getCheatTracker().isSpam(10000, 6)){
                        c.getPlayer().dropMessage(5, "You cannot send Triple Smega that often. Wait 10 more seconds.");
                    } else {
                        final byte numLines = slea.readByte();
                        if (numLines > 3) {
                            return;
                        }
                        final List<String> messages = new LinkedList<String>();
                        String message;
                        for (int i = 0; i < numLines; i++) {
                            message = slea.readMapleAsciiString();
                            if (message.length() > 65) {
                                break;
                            }
                            messages.add(c.getPlayer().getName() + " : " + message);
                        }
                        final boolean ear = slea.readByte() > 0;
                        used = SmegaProcessor.processTripleMegaPhone(c, messages, numLines, ear);
                    }
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megapone is currently disabled.");
                }
                break;
            }
            case 5073000: { // Heart Megaphone
                //TODO : Heart MegaPhone
                break;
            }
            case 5074000: { // Skull Megaphone
                //TODO: Skull MegaPhone
                break;
            }
            case 5072000: { // Super Megaphone
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    if (c.getPlayer().getMute() > 0) {
                        c.getPlayer().dropMessage(5, "The usage of Megapone is currently disabled for you. You have been muted");
                    } else if (c.getPlayer().getCheatTracker().isSpam(10000, 1)){
                        c.getPlayer().dropMessage(5, "You cannot send Smega that often. Wait 10 more seconds.");
                    } else {
                        final String message = slea.readMapleAsciiString();
                        final boolean ears = slea.readByte() != 0;
                        used = SmegaProcessor.processSmega(c, message, ears);
                    }
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megapone is currently disabled.");
                }
                break;
            }
            case 5076000: { // Item Megaphone
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    if (c.getPlayer().getMute() > 0) {
                        c.getPlayer().dropMessage(5, "The usage of Megapone is currently disabled for you. You have been muted");
                    } else if (c.getPlayer().getCheatTracker().isSpam(10000, 1)){
                        c.getPlayer().dropMessage(5, "You cannot send Smega that often. Wait 10 more seconds.");
                    } else {
                        final String message = slea.readMapleAsciiString();
                        final boolean ears = slea.readByte() != 0;
                        IItem item = null;
                        if (slea.readByte() == 1) { //item
                            byte invType = (byte) slea.readInt();
                            byte pos = (byte) slea.readInt();
                            item = c.getPlayer().getInventory(MapleInventoryType.getByType(invType)).getItem(pos);
                        }
                        used = SmegaProcessor.processISmega(c, item, message, ears);
                    }
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megapone is currently disabled.");
                }
                break;
            }
            case 5075000: // MapleTV Messenger
            case 5075001: // MapleTV Star Messenger
            case 5075002: { // MapleTV Heart Messenger
/*		int tvType = itemId % 10;
                boolean megassenger = false;
                boolean ear = false;
                MapleCharacter victim = null;

                if (tvType != 1) { // 1 is the odd one out since it doesnt allow 2 players.
                if (tvType >= 3) {
                megassenger = true;
                if (tvType == 3) {
                slea.readByte();
                }
                ear = 1 == slea.readByte();
                } else if (tvType != 2) {
                slea.readByte();
                }
                if (tvType != 4) {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                }
                }
                List<String> messages = new LinkedList<String>();
                StringBuilder builder = new StringBuilder();
                String message;
                for (int i = 0; i < 5; i++) {
                message = slea.readMapleAsciiString();
                if (megassenger) {
                builder.append(" ");
                builder.append(message);
                }
                messages.add(message);
                }
                slea.readInt(); // some random shit
                if (megassenger) {
                try {
                c.getChannelServer().getWorldInterface().broadcastMessage(MaplePacketCreator.serverNotice(3, c.getChannel(), c.getPlayer().getName() + " : " + builder.toString(), ear).getBytes());
                } catch (RemoteException e) {
                System.out.println("RemoteException occured, TV megaphone");
                }
                }
                if (!MapleTVEffect.isActive()) {
                new MapleTVEffect(c.getPlayer(), victim, messages, tvType);
                used = true;
                } else {
                c.getPlayer().dropMessage(5, "MapleTV is already in use!");
                }*/
                break;
            }
            case 5090100: // Wedding Invitation Card
            case 5090000: { // Note
                final String sendTo = slea.readMapleAsciiString();
                final String msg = slea.readMapleAsciiString();
                c.getPlayer().sendNote(sendTo, msg);
                used = true;
                break;
            }
            case 5100000: { // Congratulatory Song
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.musicChange("Jukebox/Congratulation"));
                used = true;
                break;
            }
            case 5170000: { // Pet name change
                if (c.getPlayer().getPet(0) == null) {
                    break;
                }
                String nName = slea.readMapleAsciiString();
                if (MapleCharacterUtil.canChangePetName(nName)) {
                    c.getPlayer().getPet(0).setName(nName);
                    c.getSession().write(PetPacket.updatePet(c.getPlayer().getPet(0), true));
                    c.getSession().write(MaplePacketCreator.enableActions());
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MTSCSPacket.changePetName(c.getPlayer(), nName, 1), true);
                    used = true;
                }
                break;
            }
            case 5200000: { // Bronze Sack of Mesos
                c.getPlayer().gainMeso(1000000, true, false, true);
                c.getSession().write(MaplePacketCreator.enableActions());
                used = true;
                break;
            }
            case 5200001: { // Silver Sack of Mesos
                c.getPlayer().gainMeso(130000, true, false, true);
                c.getSession().write(MaplePacketCreator.enableActions());
                used = true;
                break;
            }
            case 5200002: { // Gold Sack of Mesos
                c.getPlayer().gainMeso(350000, true, false, true);
                c.getSession().write(MaplePacketCreator.enableActions());
                used = true;
                break;
            }
            case 5240000:
            case 5240001:
            case 5240002:
            case 5240003:
            case 5240004:
            case 5240005:
            case 5240006:
            case 5240007:
            case 5240008:
            case 5240009:
            case 5240010:
            case 5240011:
            case 5240012:
            case 5240013:
            case 5240014:
            case 5240015:
            case 5240016:
            case 5240017:
            case 5240018:
            case 5240019:
            case 5240020:
            case 5240021:
            case 5240022:
            case 5240023:
            case 5240024: { // Pet food
                MaplePet pet = c.getPlayer().getPet(0);

                if (pet == null) {
                    break;
                }
                if (!pet.canConsume(itemId)) {
                    pet = c.getPlayer().getPet(1);
                    if (pet != null) {
                        if (!pet.canConsume(itemId)) {
                            pet = c.getPlayer().getPet(2);
                            if (pet != null) {
                                if (!pet.canConsume(itemId)) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
                final byte petindex = c.getPlayer().getPetIndex(pet);
                pet.setFullness(100);
                if (pet.getCloseness() < 30000) {
                    if (pet.getCloseness() + 100 > 30000) {
                        pet.setCloseness(30000);
                    } else {
                        pet.setCloseness(pet.getCloseness() + 100);
                    }
                    if (pet.getCloseness() >= ExpTable.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                        pet.setLevel(pet.getLevel() + 1);
                        c.getSession().write(PetPacket.showOwnPetLevelUp(c.getPlayer().getPetIndex(pet)));
                        c.getPlayer().getMap().broadcastMessage(PetPacket.showPetLevelUp(c.getPlayer(), petindex));
                    }
                }
                c.getSession().write(PetPacket.updatePet(pet, true));
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), PetPacket.commandResponse(c.getPlayer().getId(), (byte) 1, petindex, true, true), true);
                used = true;
                break;
            }
//            case 5280001: // Gas Skill
            case 5281000: { // Passed gas
/*		Rectangle bounds = new Rectangle((int) c.getPlayer().getPosition().getX(), (int) c.getPlayer().getPosition().getY(), 1, 1);
                MapleStatEffect mse = new MapleStatEffect();
                mse.setSourceId(2111003);
                MapleMist mist = new MapleMist(bounds, c.getPlayer(), mse);
                c.getPlayer().getMap().spawnMist(mist, 10000, false, true);
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), "Oh no, I farted!", false, 1));
                c.getSession().write(MaplePacketCreator.enableActions());
                used = true;*/
                break;
            }
            case 5370000: { // Chalkboard
                c.getPlayer().setChalkboard(slea.readMapleAsciiString());
                break;
            }
            case 5370001: { // BlackBoard
                if (c.getPlayer().getMapId() / 1000000 == 910) {
                    c.getPlayer().setChalkboard(slea.readMapleAsciiString());
                }
                break;
            }
            case 5390000: // Diablo Messenger
            case 5390001: // Cloud 9 Messenger
            case 5390002: // Loveholic Messenger
            case 5390003: // New Year Megassenger 1
            case 5390004: // New Year Megassenger 2
            case 5390005: // Cute Tiger Messenger
            case 5390006: { // Tiger Roar's Messenger
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    if (c.getPlayer().getMute() > 0) {
                        c.getPlayer().dropMessage(5, "The usage of Megapone is currently disabled for you. You have been muted");
                    } else if (c.getPlayer().getCheatTracker().isSpam(30000, 5)){
                        c.getPlayer().dropMessage(5, "You cannot send Avatar Smega that often. Wait 30 more seconds.");
                    } else {
                        final String message = slea.readMapleAsciiString();
                        final boolean ears = slea.readByte() != 0;
                        used = SmegaProcessor.processASmega(c, itemId, message, ears);
                    }
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megapone is currently disabled.");
                }
                break;
            }
            case 5450000: { // Mu Mu the Travelling Merchant
                MapleShopFactory.getInstance().getShop(61).sendShop(c);
                used = true;
                break;
            }
            default:
                if (itemId / 10000 == 512) {
                    final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    final String msg = ii.getMsg(itemId).replaceFirst("%s", c.getPlayer().getName()).replaceFirst("%s", slea.readMapleAsciiString());
                    c.getPlayer().getMap().startMapEffect(msg, itemId);

                    final int buff = ii.getStateChangeItem(itemId);
                    if (buff != 0) {
                        for (MapleCharacter mChar : c.getPlayer().getMap().getCharacters()) {
                            ii.getItemEffect(buff).applyTo(mChar);
                        }
                    }
                    used = true;
                } else {
                    System.out.println("Unhandled CS item : " + itemId);
                    System.out.println(slea.toString());
                }
                break;
        }
        if (used) {
            MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, itemId, 1, true, false);
        } else {
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }
}
