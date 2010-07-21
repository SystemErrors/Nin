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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.Randomizer;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class AdminCommandHandler {

    public static void handleCommand(SeekableLittleEndianAccessor slea, MapleClient c) {
        /**
         * CreateItem = 0x00,
         * DestroyFirstItem = 0x01,
         * GiveExp = 0x02,
         * Ban = 0x03,
         * Block = 0x04,
         * VarSetGet = 0x09,
         * Hide = 0x10,
         * ShowMessageMap = 0x11,
         * Send = 0x12,
         * Summon = 0x17,
         * Snow = 0x1c,
         * Warn = 0x1d,
         * Log = 0x1e,
         * SetObjState = 0x22
         */
        if (!c.isJounin()) {
            return;
        }
        final byte mode = slea.readByte();
        String victim;
        MapleCharacter target, chr = c.getPlayer();
        switch (mode) {
            case 0x00: {// Level1~Level8 & Package1~Package2
                final List<Pair<Integer, Integer>> toSpawn = MapleItemInformationProvider.getInstance().getSummonMobs(slea.readInt());

		if (toSpawn == null) {
		    c.getSession().write(MaplePacketCreator.enableActions());
		    return;
		}
		MapleMonster ht;
		int typez = 0;

		for (int i = 0; i < toSpawn.size(); i++) {
		    if (Randomizer.nextInt(99) <= toSpawn.get(i).getRight()) {
			ht = MapleLifeFactory.getMonster(toSpawn.get(i).getLeft());
			chr.getMap().spawnMonster_sSack(ht, chr.getPosition(), typez);
		    }
		}
                c.getSession().write(MaplePacketCreator.enableActions());
                break;
            }
            case 0x01: { // /d (inv)
                final byte type = slea.readByte();
                MapleInventory in = c.getPlayer().getInventory(MapleInventoryType.getByType(type));
                for (byte i = 0; i < in.getSlotLimit(); i++) {
                    if (in.getItem(i) != null) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.getByType(type), i, in.getItem(i).getQuantity(), false);
                    }
                    return;
                }
                break;
            }
            case 0x02: // Exp
                c.getPlayer().setExp(slea.readInt());
                break;
            case 0x03: // Ban
                victim = slea.readMapleAsciiString();
                String reason = c.getPlayer().getName() + " used /ban to ban";
                target = c.getChannelServer().getPlayerStorage().getCharacterByName(victim);
                if (target != null) {
                    String readableTargetName = MapleCharacterUtil.makeMapleReadable(target.getName());
                    String ip = target.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                    reason += readableTargetName + " (IP: " + ip + ")";
                    target.ban(reason);
                    c.getSession().write(MaplePacketCreator.sendGMOperation(4, 0));
                } else if (MapleCharacter.ban(victim, reason, false)) {
                    c.getSession().write(MaplePacketCreator.sendGMOperation(4, 0));
                } else {
                    c.getSession().write(MaplePacketCreator.sendGMOperation(6, 1));
                }
                break;
            case 0x04: // Block
                victim = slea.readMapleAsciiString();
                slea.readByte(); //type
                int duration = slea.readInt();
                String description = slea.readMapleAsciiString();
                reason = c.getPlayer().getName() + " used /ban to ban";
                target = c.getChannelServer().getPlayerStorage().getCharacterByName(victim);
                if (target != null) {
                    String readableTargetName = MapleCharacterUtil.makeMapleReadable(target.getName());
                    String ip = target.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                    reason += readableTargetName + " (IP: " + ip + ")";
                    if (duration == -1) {
                        target.ban(description + " " + reason);
                    } else {
                        //target.tempban(reason, duration, type);
                    }
                    c.getSession().write(MaplePacketCreator.sendGMOperation(4, 0));
                } else if (MapleCharacter.ban(victim, reason, false)) {
                    c.getSession().write(MaplePacketCreator.sendGMOperation(4, 0));
                } else {
                    c.getSession().write(MaplePacketCreator.sendGMOperation(6, 1));
                }
                break;
            case 0x11: // /h, information by vana
                StringBuilder sb = new StringBuilder("USERS ON THIS MAP: ");
                for (MapleCharacter mc : c.getPlayer().getMap().getCharacters()) {
                    sb.append(mc.getName());
                    sb.append(" ");
                }
                c.getPlayer().dropMessage(sb.toString());
                break;
            case 0x12: // Send
                victim = slea.readMapleAsciiString();
                int mapId = slea.readInt();
                c.getChannelServer().getPlayerStorage().getCharacterByName(victim).changeMap(c.getChannelServer().getMapFactory().getMap(mapId));
                break;
            case 0x15: // Kill
                int mobToKill = slea.readInt();
                int amount = slea.readInt();
                List<MapleMapObject> monsterx = c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
                for (int x = 0; x < amount; x++) {
                    MapleMonster monster = (MapleMonster) monsterx.get(x);
                    if (monster.getId() == mobToKill) {
                        c.getPlayer().getMap().killMonster(monster, chr, true, false, (byte)0);
                        
                    }
                }
                break;
            case 0x16: // Questreset
                // lol?
                break;
            case 0x17: // Summon
                int mobId = slea.readInt();
                int quantity = slea.readInt();
                for (int i = 0; i < quantity; i++) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), c.getPlayer().getPosition());
                }
                break;
            case 0x18: // Maple & Mobhp
                int mobHp = slea.readInt();
                c.getPlayer().dropMessage("Monsters HP");
                List<MapleMapObject> monsters = c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
                for (MapleMapObject mobs : monsters) {
                    MapleMonster monster = (MapleMonster) mobs;
                    if (monster.getId() == mobHp) {
                        c.getPlayer().dropMessage(monster.getName() + ": " + monster.getHp());
                    }
                }
                break;
            case 0x1D: // Warn
                victim = slea.readMapleAsciiString();
                String message = slea.readMapleAsciiString();
                target = c.getChannelServer().getPlayerStorage().getCharacterByName(victim);
                if (target != null) {
                    target.getClient().getSession().write(MaplePacketCreator.serverNotice(1, message));
                    c.getSession().write(MaplePacketCreator.sendGMOperation(29, (byte) 1));
                } else {
                    c.getSession().write(MaplePacketCreator.sendGMOperation(29, (byte) 0));
                }
                break;
            default:
                System.out.println("New GM packet encountered (MODE : " + mode + ": " + slea.toString());
                break;
        }
    }
}
