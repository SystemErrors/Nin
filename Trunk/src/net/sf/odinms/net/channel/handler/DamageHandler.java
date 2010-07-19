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

import net.sf.odinms.client.Buffs.CancelCooldownAction;
import net.sf.odinms.client.Buffs.MapleBuffStat;
import net.sf.odinms.client.Buffs.MapleStatEffect;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.PlayerStats;
import net.sf.odinms.client.Skills.ISkill;
import net.sf.odinms.client.Skills.SkillFactory;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.constants.Skills;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MobAttackInfo;
import net.sf.odinms.server.life.MobAttackInfoFactory;
import net.sf.odinms.server.life.MobSkill;
import net.sf.odinms.server.life.MobSkillFactory;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Packets.MobPacket;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class DamageHandler {
 public static final void closeRangeAttack(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
	if (!chr.isAlive()) {
	    chr.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
	    return;
	}
	final AttackInfo attack = DamageParse.parseDmgM(slea);

	double maxdamage = chr.getStat().getCurrentMaxBaseDamage();
	int attackCount = 1, skillLevel = 0;
	MapleStatEffect effect = null;
	ISkill skill = null;

	if (attack.skill != 0) {
	    skill = SkillFactory.getSkill(Skills.getLinkedAranSkill(attack.skill));
	    skillLevel = chr.getSkillLevel(skill);
	    if (skillLevel == 0) {
		c.getSession().close();
		return;
	    }
	    effect = attack.getAttackEffect(chr, skillLevel, skill);
	    maxdamage *= effect.getDamage() / 100.0;
	    attackCount = effect.getAttackCount();

	    if (effect.getCooldown() > 0) {
		if (chr.skillisCooling(attack.skill)) {
		    c.getSession().write(MaplePacketCreator.enableActions());
		    return;
		}
		c.getSession().write(MaplePacketCreator.skillCooldown(attack.skill, effect.getCooldown()));
		chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown() * 1000, TimerManager.getInstance().schedule(new CancelCooldownAction(chr, attack.skill), effect.getCooldown() * 1000));
	    }
	}

	// handle combo orbconsume
	int numFinisherOrbs = 0;
	final Integer comboBuff = chr.getBuffedValue(MapleBuffStat.COMBO);

	if (isFinisher(attack.skill)) { // finisher
	    if (comboBuff != null) {
		numFinisherOrbs = comboBuff.intValue() - 1;
	    }
	    chr.handleOrbconsume();

	} else if (attack.targets > 0 && comboBuff != null) {
	    // handle combo orbgain
	    switch (chr.getJob()) {
		case 111:
		case 112:
		case 1110:
		case 1111:
		    if (attack.skill != 1111008) { // shout should not give orbs
			chr.handleOrbgain();
		    }
		    break;
	    }
	}
	switch (chr.getJob()) {
	    case 511:
	    case 512: {
		chr.handleEnergyCharge(5110001, attack.targets);
		break;
	    }
	    case 1510:
	    case 1511:
	    case 1512: {
		chr.handleEnergyCharge(15100004, attack.targets);
		break;
	    }
	}
	// handle sacrifice hp loss
	if (attack.targets > 0 && attack.skill == 1211002) { // handle charged blow
	    final int advcharge_level = chr.getSkillLevel(SkillFactory.getSkill(1220010));
	    if (advcharge_level > 0) {
		if (!SkillFactory.getSkill(1220010).getEffect(advcharge_level).makeChanceResult()) {
		    chr.cancelEffectFromBuffStat(MapleBuffStat.WK_CHARGE);
		}
	    } else {
		chr.cancelEffectFromBuffStat(MapleBuffStat.WK_CHARGE);
	    }
	}

	if (numFinisherOrbs > 0) {
	    maxdamage *= numFinisherOrbs;
	} else if (comboBuff != null) {
	    ISkill combo;
	    if (c.getPlayer().getJob() == 1110 || c.getPlayer().getJob() == 1111) {
		combo = SkillFactory.getSkill(11111001);
	    } else {
		combo = SkillFactory.getSkill(1111002);
	    }
	    maxdamage *= 1.0 + (combo.getEffect(c.getPlayer().getSkillLevel(combo)).getDamage() / 100.0 - 1.0) * (comboBuff.intValue() - 1);
	}

	if (isFinisher(attack.skill)) {
	    if (numFinisherOrbs == 0) {
		return;
	    }
	    maxdamage = 199999; // FIXME reenable damage calculation for finishers
	}
	DamageParse.applyAttack(attack, skill, c.getPlayer(), attackCount, maxdamage, effect, AttackType.NON_RANGED);

	chr.getMap().broadcastMessage(chr, MaplePacketCreator.closeRangeAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.animation, attack.speed, attack.allDamage), chr.getPosition());
    }

    public static final void rangedAttack(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
	if (!chr.isAlive()) {
	    chr.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
	    return;
	}
	final AttackInfo attack = DamageParse.parseDmgR(slea);

	int bulletCount = 1, skillLevel = 0;
	MapleStatEffect effect = null;
	ISkill skill = null;

	if (attack.skill != 0) {
	    skill = SkillFactory.getSkill(attack.skill);
	    skillLevel = chr.getSkillLevel(skill);
	    if (skillLevel == 0) {
		c.getSession().close();
		return;
	    }
	    effect = attack.getAttackEffect(chr, skillLevel, skill);

	    switch (attack.skill) {
		case 21110004: // Ranged but uses attackcount instead
		case 14101006: // Vampure
		    bulletCount = effect.getAttackCount();
		    break;
		default:
		    bulletCount = effect.getBulletCount();
		    break;
	    }
	    if (effect.getCooldown() > 0) {
		if (chr.skillisCooling(attack.skill)) {
		    c.getSession().write(MaplePacketCreator.enableActions());
		    return;
		}
		c.getSession().write(MaplePacketCreator.skillCooldown(attack.skill, effect.getCooldown()));
		chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown() * 1000, TimerManager.getInstance().schedule(new CancelCooldownAction(chr, attack.skill), effect.getCooldown() * 1000));
	    }
	}
	final Integer ShadowPartner = chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER);
	if (ShadowPartner != null) {
	    bulletCount *= 2;
	}
	int projectile = 0, visProjectile = 0;
	if (attack.AOE != 0 && chr.getBuffedValue(MapleBuffStat.SOULARROW) == null && attack.skill != 4111004) {
	    projectile = chr.getInventory(MapleInventoryType.USE).getItem(attack.slot).getItemId();

	    if (attack.csstar > 0) {
		visProjectile = chr.getInventory(MapleInventoryType.CASH).getItem(attack.csstar).getItemId();
	    } else {
		visProjectile = projectile;
	    }
	    // Handle bulletcount
	    if (chr.getBuffedValue(MapleBuffStat.SPIRIT_CLAW) == null) {
		int bulletConsume = bulletCount;
		if (effect != null && effect.getBulletConsume() != 0) {
		    bulletConsume = effect.getBulletConsume() * (ShadowPartner != null ? 2 : 1);
		}
		MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, projectile, bulletConsume, false, true);
	    }
	}

	double basedamage;
	int projectileWatk = 0;
	if (projectile != 0) {
	    projectileWatk = MapleItemInformationProvider.getInstance().getWatkForProjectile(projectile);
	}
	final PlayerStats statst = chr.getStat();
	switch (attack.skill) {
	    case 4001344: // Lucky Seven
	    case 4121007: // Triple Throw
	    case 14001004: // Lucky seven
	    case 14111005: // Triple Throw
		basedamage = (float) ((float) ((statst.getTotalLuk() * 5.0f) * (statst.getTotalWatk() + projectileWatk)) / 100);
		break;
	    case 4111004: // Shadow Meso
//		basedamage = ((effect.getMoneyCon() * 10) / 100) * effect.getProb(); // Not sure
		basedamage = 13000;
		break;
	    default:
		if (projectileWatk != 0) {
		    basedamage = statst.calculateMaxBaseDamage(statst.getTotalWatk() + projectileWatk);
		} else {
		    basedamage = statst.getCurrentMaxBaseDamage();
		}
		switch (attack.skill) {
		    case 3101005: // arrowbomb is hardcore like that
			basedamage *= effect.getX() / 100.0;
			break;
		}
		break;
	}
	if (effect != null) {
	    basedamage *= effect.getDamage() / 100.0;

	    int money = effect.getMoneyCon();
	    if (money != 0) {
		if (money > chr.getMeso()) {
		    money = chr.getMeso();
		}
		chr.gainMeso(-money, false);
	    }
	}
	DamageParse.applyAttack(attack, skill, chr, bulletCount, basedamage, effect, ShadowPartner != null ? AttackType.RANGED_WITH_SHADOWPARTNER : AttackType.RANGED);

	chr.getMap().broadcastMessage(chr, MaplePacketCreator.rangedAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.animation, attack.speed, visProjectile, attack.allDamage, attack.position), chr.getPosition());
    }

    public static final void magicDamage(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
	if (!chr.isAlive()) {
	    chr.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
	    return;
	}
	final AttackInfo attack = DamageParse.parseDmgMa(slea);
	final ISkill skill = SkillFactory.getSkill(attack.skill);
	final int skillLevel = chr.getSkillLevel(skill);
	if (skillLevel == 0) {
	    c.getSession().close();
	    return;
	}
	final MapleStatEffect effect = attack.getAttackEffect(chr, skillLevel, skill);

	if (effect.getCooldown() > 0) {
	    if (chr.skillisCooling(attack.skill)) {
		c.getSession().write(MaplePacketCreator.enableActions());
		return;
	    }
	    c.getSession().write(MaplePacketCreator.skillCooldown(attack.skill, effect.getCooldown()));
	    chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown() * 1000, TimerManager.getInstance().schedule(new CancelCooldownAction(chr, attack.skill), effect.getCooldown() * 1000));
	}
	DamageParse.applyAttackMagic(attack, skill, c.getPlayer(), effect);

	chr.getMap().broadcastMessage(chr, MaplePacketCreator.magicAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.animation, attack.speed, attack.allDamage, attack.charge), chr.getPosition());
    }

     private static final boolean isFinisher(final int skillid) {
	switch (skillid) {
	    case 1111003:
	    case 1111004:
	    case 1111005:
	    case 1111006:
	    case 11111002:
	    case 11111003:
		return true;
	}
	return false;
    }

     public static final void TakeDamage(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
	slea.skip(4); // Ticks
	final byte type = slea.readByte();
	slea.skip(1); // Element - 0x00 = elementless, 0x01 = ice, 0x02 = fire, 0x03 = lightning
	int damage = slea.readInt();

	int oid = 0;
	int monsteridfrom = 0;
	int reflect = 0;
	byte direction = 0;
	int pos_x = 0;
	int pos_y = 0;
	int fake = 0;
	int mpattack = 0;
	boolean is_pg = false;
	boolean isDeadlyAttack = false;
	MapleMonster attacker = null;
	final PlayerStats stats = chr.getStat();

	if (type != -2) { // Not map damage
	    monsteridfrom = slea.readInt();
	    oid = slea.readInt();
	    attacker = (MapleMonster) chr.getMap().getMonsterByOid(oid);
	    direction = slea.readByte();

	    if (attacker == null) {
		return;
	    }
	    if (type != -1) { // Bump damage
		final MobAttackInfo attackInfo = MobAttackInfoFactory.getInstance().getMobAttackInfo(attacker, type);
		if (attackInfo.isDeadlyAttack()) {
		    isDeadlyAttack = true;
		    mpattack = stats.getMp() - 1;
		} else {
		    mpattack += attackInfo.getMpBurn();
		}
		final MobSkill skill = MobSkillFactory.getMobSkill(attackInfo.getDiseaseSkill(), attackInfo.getDiseaseLevel());
		if (skill != null && (damage == -1 || damage > 0)) {
		    skill.applyEffect(chr, attacker, false);
		}
		attacker.setMp(attacker.getMp() - attackInfo.getMpCon());
	    }
	}

	if (damage == -1) {
	    fake = 4020002 + ((chr.getJob() / 10 - 40) * 100000);
	} else if (damage < -1 || damage > 60000) {
	    AutobanManager.getInstance().addPoints(c, 1000, 60000, "Taking abnormal amounts of damge from " + monsteridfrom + ": " + damage);
	    return;
	}
	chr.getCheatTracker().checkTakeDamage(damage);

	if (damage > 0) {
	    chr.getCheatTracker().setAttacksWithoutHit(false);

	    if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
		chr.cancelMorphs();
	    }

	    if (type == -1) {
		if (chr.getBuffedValue(MapleBuffStat.POWERGUARD) != null) {
		    attacker = (MapleMonster) chr.getMap().getMapObject(oid);
		    if (attacker != null) {
			int bouncedamage = (int) (damage * (chr.getBuffedValue(MapleBuffStat.POWERGUARD).doubleValue() / 100));
			bouncedamage = Math.min(bouncedamage, attacker.getMobMaxHp() / 10);
			attacker.damage(chr, bouncedamage, true);
			damage -= bouncedamage;
			chr.getMap().broadcastMessage(chr, MobPacket.damageMonster(oid, bouncedamage), chr.getPosition());
			is_pg = true;
		    }
		}
	    } else if (type != -2) {
		switch (chr.getJob()) {
		    case 112: {
			final ISkill skill = SkillFactory.getSkill(1120004);
			if (chr.getSkillLevel(skill) > 0) {
			    damage = (int) ((skill.getEffect(chr.getSkillLevel(skill)).getX() / 1000.0) * damage);
			}
			break;
		    }
		    case 122: {
			final ISkill skill = SkillFactory.getSkill(1220005);
			if (chr.getSkillLevel(skill) > 0) {
			    damage = (int) ((skill.getEffect(chr.getSkillLevel(skill)).getX() / 1000.0) * damage);
			}
			break;
		    }
		    case 132: {
			final ISkill skill = SkillFactory.getSkill(1320005);
			if (chr.getSkillLevel(skill) > 0) {
			    damage = (int) ((skill.getEffect(chr.getSkillLevel(skill)).getX() / 1000.0) * damage);
			}
			break;
		    }
		}
	    }

	    if (chr.getBuffedValue(MapleBuffStat.MAGIC_GUARD) != null) {
		int hploss = 0, mploss = 0;
		if (isDeadlyAttack) {
		    if (stats.getHp() > 1) {
			hploss = stats.getHp() - 1;
		    }
		    if (stats.getMp() > 1) {
			mploss = stats.getMp() - 1;
		    }
		    chr.addMPHP(-hploss, -mploss);
		} else if (mpattack > 0) {
		    chr.addMPHP(-damage, -mpattack);
		} else {
		    mploss = (int) (damage * (chr.getBuffedValue(MapleBuffStat.MAGIC_GUARD).doubleValue() / 100.0));
		    hploss = damage - mploss;

		    if (mploss > stats.getMp()) {
			hploss += mploss - stats.getMp();
			mploss = stats.getMp();
		    }
		    chr.addMPHP(-hploss, -mploss);
		}

	    } else if (chr.getBuffedValue(MapleBuffStat.MESOGUARD) != null) {
		damage = (damage % 2 == 0) ? damage / 2 : (damage / 2) + 1;

		final int mesoloss = (int) (damage * (chr.getBuffedValue(MapleBuffStat.MESOGUARD).doubleValue() / 100.0));
		if (chr.getMeso() < mesoloss) {
		    chr.gainMeso(-chr.getMeso(), false);
		    chr.cancelBuffStats(MapleBuffStat.MESOGUARD);
		} else {
		    chr.gainMeso(-mesoloss, false);
		}
		if (isDeadlyAttack && stats.getMp() > 1) {
		    mpattack = stats.getMp() - 1;
		}
		chr.addMPHP(-damage, -mpattack);
	    } else {
		if (isDeadlyAttack) {
		    chr.addMPHP(stats.getHp() > 1 ? -(stats.getHp() - 1) : 0, stats.getMp() > 1 ? -(stats.getMp() - 1) : 0);
		} else {
		    chr.addMPHP(-damage, -mpattack);
		}
	    }
	}
	if (!chr.isHidden()) {
	    chr.getMap().broadcastMessage(chr, MaplePacketCreator.damagePlayer(type, monsteridfrom, chr.getId(), damage, fake, direction, reflect, is_pg, oid, pos_x, pos_y), false);
	}
    }
}
