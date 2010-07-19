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

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import net.sf.odinms.client.Buffs.CancelCooldownAction;
import net.sf.odinms.client.Buffs.MapleStatEffect;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleKeyBinding;
import net.sf.odinms.client.PlayerStats;
import net.sf.odinms.client.Skills.ISkill;
import net.sf.odinms.client.Skills.SkillFactory;
import net.sf.odinms.client.Skills.SkillMacro;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.constants.GameConstants;
import net.sf.odinms.server.constants.InventoryConstants;
import net.sf.odinms.server.constants.Skills;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.FieldLimitType;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.movement.AbsoluteLifeMovement;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Packets.MTSCSPacket;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class PlayerHandler {

    public static final void ChangeMapSpecial(final String portal_name, final MapleClient c, final MapleCharacter chr) {
        final MaplePortal portal = chr.getMap().getPortal(portal_name);
//	slea.skip(2);

        if (portal != null) {
            portal.enterPortal(c);
        }
    }

    public static final void ChangeMap(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (slea.available() != 0) {
            final byte type = slea.readByte(); // 1 = from dying 2 = regular portals
            final int targetid = slea.readInt(); // FF FF FF FF
            final MaplePortal portal = chr.getMap().getPortal(slea.readMapleAsciiString());
            slea.skip(1);
            final boolean wheel = slea.readByte() > 0;

            if (targetid != -1 && !chr.isAlive()) {
                if (chr.getEventInstance() != null) {
                    chr.getEventInstance().revivePlayer(chr);
                }
                chr.setStance(0);

                if (!wheel) {
                    chr.getStat().setHp(50);

                    final MapleMap to = chr.getMap().getReturnMap();
                    chr.changeMap(to, to.getPortal(0));
                } else {
                    if (chr.haveItem(5510000, 1, false, true)) { // Wheel of Fortune
                        chr.getStat().setHp((chr.getStat().getMaxHp() / 100) * 40);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5510000, 1, true, false);

                        final MapleMap to = chr.getMap();
                        chr.changeMap(to, to.getPortal(0));
                    } else {
                        c.disconnect(true, false);
                    }
                }
            } else if (targetid != -1 && chr.isJounin()) {
                final MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                chr.changeMap(to, to.getPortal(0));

            } else if (targetid != -1 && !chr.isJounin()) {
                final int divi = chr.getMapId() / 100;
                if (divi == 9130401) { // Only allow warp if player is already in Intro map, or else = hack

                    if (targetid == 130000000 || targetid / 100 == 9130401) { // Cygnus introduction
                        final MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                        chr.changeMap(to, to.getPortal(0));
                    }
                } else if (divi == 9140900) { // Aran Introduction
                    if (targetid == 914090011 || targetid == 914090012 || targetid == 914090013 || targetid == 140090000) {
                        final MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                        chr.changeMap(to, to.getPortal(0));
                    }
                } else if (divi / 10 == 1020) { // Adventurer movie clip Intro
                    if (targetid == 1020000) {
                        final MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                        chr.changeMap(to, to.getPortal(0));
                    }
                }
            } else {
                if (portal != null) {
                    portal.enterPortal(c);
                } else {
                    c.getSession().write(MaplePacketCreator.enableActions());
                }
            }
        }
    }

    public static final void InnerPortal(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final MaplePortal portal = c.getPlayer().getMap().getPortal(slea.readMapleAsciiString());
        final int toX = slea.readShort();
        final int toY = slea.readShort();
//	slea.readShort(); // Original X pos
//	slea.readShort(); // Original Y pos

        if (portal == null) {
            c.disconnect(true, false);
            return;
        } else if (portal.getPosition().distanceSq(chr.getPosition()) > 22500) {
            chr.getCheatTracker().registerOffense(CheatingOffense.USING_FARAWAY_PORTAL);
        }
        chr.getMap().movePlayer(chr, new Point(toX, toY));
    }

    public static void MovePlayer(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        /*slea.readByte();
        slea.readInt();
        slea.readInt();*/
        slea.skip(9);

        final List<LifeMovementFragment> res = MovementParse.parseMovement(slea);

        if (res != null) { // TODO more validation of input data
            if (slea.available() != 18) {
                System.out.println("slea.available != 18 (movement parsing error)");
                return;
            }
            final MapleMap map = c.getPlayer().getMap();

            if (chr.isHidden()) {
                chr.setLastRes(res);
            } else {
                speedCheck(res, c);
                map.broadcastMessage(chr, MaplePacketCreator.movePlayer(chr.getId(), res), false);
            }
            MovementParse.updatePosition(res, chr, 0);
            map.movePlayer(chr, chr.getPosition());

            /*	    int count = c.getPlayer().getFallCounter();
            if (map.getFootholds().findBelow(c.getPlayer().getPosition()) == null) {
            if (count > 3) {
            c.getPlayer().changeMap(map, map.getPortal(0));
            } else {
            c.getPlayer().setFallCounter(++count);
            }
            } else if (count > 0) {
            c.getPlayer().setFallCounter(0);
            }*/
        }

    }

    private static final void speedCheck(final List<LifeMovementFragment> res, final MapleClient c) {
        double speedMod, playerSpeedMod = c.getPlayer().getStat().getSpeedMod() + 0.005;
        for (LifeMovementFragment lmf : res) {
            if (lmf.getClass() == AbsoluteLifeMovement.class) {
                final AbsoluteLifeMovement alm = (AbsoluteLifeMovement) lmf;
                speedMod = Math.abs(alm.getPixelsPerSecond().x) / 125.0;
                if (speedMod > playerSpeedMod) {
                    if (alm.getUnk() != 0) {
                        if (speedMod > playerSpeedMod) {
                            c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.FAST_MOVE);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static final void ChangeEmotion(final int emote, final MapleCharacter chr) {
        if (emote > 7) {
            final int emoteid = 5159992 + emote;
            final MapleInventoryType type = InventoryConstants.getInventoryType(emoteid);
            if (chr.getInventory(type).findById(emoteid) == null) {
                chr.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(emoteid));
                return;
            }
        }
        if (emote > 0) {
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.facialExpression(chr, emote), false);
        }
    }

    public static final void UseItemEffect(final int itemId, final MapleClient c, final MapleCharacter chr) {
        final IItem toUse = chr.getInventory(MapleInventoryType.CASH).findById(itemId);
        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        chr.setItemEffect(itemId);
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.itemEffect(chr.getId(), itemId), false);
    }

    public static final void CancelItemEffect(final int id, final MapleCharacter chr) {
        chr.cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(-id), false, -1);
    }

    public static final void ChangeMonsterBookCover(final int bookid, final MapleClient c, final MapleCharacter chr) {
        if (bookid == 0 || InventoryConstants.isMonsterCard(bookid)) {
            chr.setMonsterBookCover(bookid);
            chr.getMonsterBook().updateCard(c, bookid);
        }
    }

    public static final void Heal(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
        slea.skip(4);
        final int healHP = slea.readShort();
        final int healMP = slea.readShort();
        final PlayerStats stats = chr.getStat();

        if (stats.getHp() <= 0) {
            return;
        }
        if (healHP != 0) {
            if (healHP > stats.getHealHP()) {
                chr.getCheatTracker().registerOffense(CheatingOffense.REGEN_HIGH_HP, String.valueOf(healHP));
            }
            chr.addHP(healHP);
        }
        if (healMP != 0) {
            if (healMP > stats.getHealMP()) {
                chr.getCheatTracker().registerOffense(CheatingOffense.REGEN_HIGH_MP, String.valueOf(healMP));
            }
            chr.addMP(healMP);
        }
    }

    public static final void SpecialMove(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (!chr.isAlive()) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        slea.skip(4); // Old X and Y
        final int skillid = slea.readInt();
        final int skillLevel = slea.readByte();
        final ISkill skill = SkillFactory.getSkill(skillid);

        if (chr.getSkillLevel(skill) == 0 || chr.getSkillLevel(skill) != skillLevel) {
            if (!Skills.isMulungSkill(skillid)) {
                c.getSession().close();
                return;
            }
            if (chr.getMapId() / 10000 != 92502) {
                AutobanManager.getInstance().autoban(c, "Using Mu Lung dojo skill out of dojo maps.");
            } else {
                chr.mulung_EnergyModify(false);
            }
        }
        final MapleStatEffect effect = skill.getEffect(chr.getSkillLevel(skill));

        if (effect.getCooldown() > 0) {
            if (chr.skillisCooling(skillid)) {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            if (skillid != 5221006) { // Battleship
                c.getSession().write(MaplePacketCreator.skillCooldown(skillid, effect.getCooldown()));
                ScheduledFuture<?> timer = TimerManager.getInstance().schedule(new CancelCooldownAction(chr, skillid), effect.getCooldown() * 1000);
                chr.addCooldown(skillid, System.currentTimeMillis(), effect.getCooldown() * 1000, timer);
            }
        }

        switch (skillid) {
            case 1121001:
            case 1221001:
            case 1321001:
            case 9001020: // GM magnet
                final byte number_of_mobs = slea.readByte();
                slea.skip(3);
                for (int i = 0; i < number_of_mobs; i++) {
                    int mobId = slea.readInt();

                    final MapleMonster mob = chr.getMap().getMonsterByOid(mobId);
                    if (mob != null) {
//			chr.getMap().broadcastMessage(chr, MaplePacketCreator.showMagnet(mobId, slea.readByte()), chr.getPosition());
                        mob.switchController(chr, mob.isControllerHasAggro());
                    }
                }
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.showBuffeffect(chr.getId(), skillid, 1, slea.readByte(), false), chr.getPosition());
                c.getSession().write(MaplePacketCreator.enableActions());
                break;
            default:
                Point pos = null;
                if (slea.available() == 5) {
                    pos = slea.readPos();
                }
                if (skill.getId() == 2311002) { // Mystic Door
                    if (chr.canDoor()) {
                        if (!FieldLimitType.MysticDoor.check(chr.getMap().getFieldLimit())) {
                            effect.applyTo(c.getPlayer(), pos);
                        } else {
                            c.getSession().write(MaplePacketCreator.enableActions());
                        }
                    } else {
                        chr.dropMessage(5, "Please wait 5 seconds before casting Mystic Door again.");
                        c.getSession().write(MaplePacketCreator.enableActions());
                    }
                } else {
                    effect.applyTo(c.getPlayer(), pos);
                }
                break;
        }
    }

    public static final void CancelBuffHandler(final int sourceid, final MapleCharacter chr) {
        final ISkill skill = SkillFactory.getSkill(sourceid);

        if (skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(0);
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.skillCancel(chr, sourceid), false);
        } else {
            chr.cancelEffect(SkillFactory.getSkill(sourceid).getEffect(1), false, -1);
        }
    }

    public static final void SkillEffect(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
        final int skillId = slea.readInt();
        final byte level = slea.readByte();
        final byte flags = slea.readByte();
        final byte speed = slea.readByte();
        //final byte unk = slea.readByte(); // Added on v.82

        final ISkill skill = SkillFactory.getSkill(skillId);
        final int skilllevel_serv = chr.getSkillLevel(skill);

        if (skilllevel_serv > 0 && skilllevel_serv == level && skill.isChargeSkill() && level > 0) {
            chr.setKeyDownSkill_Time(System.currentTimeMillis());
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.skillEffect(chr, skillId, level, flags, speed), false);
        }
    }

    public static final void DropMeso(final int meso, final MapleCharacter chr) {
        if (!chr.isAlive() || (meso < 10 || meso > 50000) || (meso > chr.getMeso())) {
            chr.getClient().getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (chr.getCheatTracker().isSpam(200, 14)) {
            chr.getClient().getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        chr.gainMeso(-meso, false, true);
        chr.getMap().spawnMesoDrop(meso, chr.getPosition(), chr, chr, true, (byte) 0);
    }

    public static final void CharInfoRequest(final int objectid, final MapleClient c, final MapleCharacter chr) {
        final MapleCharacter player = (MapleCharacter) c.getPlayer().getMap().getMapObject(objectid);

        if (player != null) {
            if (!player.isJounin() || (c.getPlayer().isJounin() && player.isJounin())) {
                c.getSession().write(MaplePacketCreator.charInfo(player));
            } else {
                c.getSession().write(MaplePacketCreator.enableActions());
            }
        }
    }

    public static void cancelDebuff(MapleClient c) {
        // Nothing for now. 
    }

    public static final void TrockAddMap(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final byte addrem = slea.readByte();
        final byte vip = slea.readByte();

        if (addrem == 0) {
            chr.deleteFromRocks(slea.readInt());
        } else if (addrem == 1) {
            if (chr.getMap().getForcedReturnId() == 999999999) {
                chr.addRockMap();
            }
        }
        c.getSession().write(MTSCSPacket.getTrockRefresh(chr, vip == 1, addrem == 3));
    }

    public static void report(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.readByte();
        String victim = slea.readMapleAsciiString();
        slea.readByte();
        String description = slea.readMapleAsciiString();
        if (c.getPlayer().getCheatTracker().isSpam(60000, 8)) {
            c.getSession().write(MaplePacketCreator.reportResponse((byte) 2));
            try {
                c.getChannelServer().getWorldInterface().broadcastGMMessage(MaplePacketCreator.serverNotice(6, victim + " was reported for: " + description).getBytes());
            } catch (RemoteException ex) {
                System.err.println("Error in reporthandler : " + ex);
            }
        } else {
            c.showMessage("Not that often. Wait for 1 minute to report again");
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }
    

    public static final void ChangeSkillMacro(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
	final int num = slea.readByte();
	String name;
	int shout, skill1, skill2, skill3;
	SkillMacro macro;

	for (int i = 0; i < num; i++) {
	    name = slea.readMapleAsciiString();
	    shout = slea.readByte();
	    skill1 = slea.readInt();
	    skill2 = slea.readInt();
	    skill3 = slea.readInt();

	    macro = new SkillMacro(skill1, skill2, skill3, name, shout, i);
	    chr.updateMacros(i, macro);
	}
    }

    public static final void ChangeKeymap(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
	if (slea.available() != 8) { // else = pet auto pot
	    slea.skip(4);
	    final int numChanges = slea.readInt();

	    int key, type, action;
	    MapleKeyBinding newbinding;

	    for (int i = 0; i < numChanges; i++) {
		key = slea.readInt();
		type = slea.readByte();
		action = slea.readInt();
		newbinding = new MapleKeyBinding(type, action);
		chr.changeKeybinding(key, newbinding);
	    }
	}
    }
}
