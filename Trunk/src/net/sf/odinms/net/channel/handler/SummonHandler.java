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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sf.odinms.client.Buffs.MapleBuffStat;
import net.sf.odinms.client.Buffs.MapleStatEffect;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Skills.ISkill;
import net.sf.odinms.client.Skills.SkillFactory;
import net.sf.odinms.client.SummonSkillEntry;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.client.status.MonsterStatusEffect;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.SummonAttackEntry;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.maps.MapleSummon;
import net.sf.odinms.server.maps.SummonMovementType;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class SummonHandler {

    public static final void MoveSummon(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
	final int oid = slea.readInt();
	final Point startPos = slea.readPos();
	final List<LifeMovementFragment> res = MovementParse.parseMovement(slea);

	for (MapleSummon sum : chr.getSummons().values()) {
	    if (sum.getObjectId() == oid && sum.getMovementType() != SummonMovementType.STATIONARY) {
		MovementParse.updatePosition(res, sum, 0);
		chr.getMap().broadcastMessage(chr, MaplePacketCreator.moveSummon(chr.getId(), oid, startPos, res), sum.getPosition());
		break;
	    }
	}
    }

    public static final void DamageSummon(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
	final int unkByte = slea.readByte();
	final int damage = slea.readInt();
	final int monsterIdFrom = slea.readInt();
	//       slea.readByte(); // stance

	final Iterator<MapleSummon> iter = chr.getSummons().values().iterator();
	MapleSummon summon;

	while (iter.hasNext()) {
	    summon = iter.next();
	    if (summon.isPuppet() && summon.getOwnerId() == chr.getId()) { //We can only have one puppet(AFAIK O.O) so this check is safe.
		summon.addHP((short) -damage);
		if (summon.getHP() <= 0) {
		    chr.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
		}
		chr.getMap().broadcastMessage(chr, MaplePacketCreator.damageSummon(chr.getId(), summon.getSkill(), damage, unkByte, monsterIdFrom), summon.getPosition());
		break;
	    }
	}
    }

    public static final void SummonAttack(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
	if (!chr.isAlive()) {
	    chr.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
	    return;
	}
	final MapleMap map = chr.getMap();
	final MapleMapObject obj = map.getMapObject(slea.readInt());
	if (obj == null || !obj.getType().equals(MapleMapObjectType.SUMMON)) {
	    return;
	}
	final MapleSummon summon = (MapleSummon) obj;
	if (summon.getOwnerId() != chr.getId()) {
	    return;
	}
	final SummonSkillEntry sse = SkillFactory.getSummonData(summon.getSkill());
	if (sse == null) {
	    return;
	}
	summon.CheckSummonAttackFrequency(chr, slea.readInt());

	final byte animation = slea.readByte();

	final byte numAttacked = slea.readByte();
	if (numAttacked > sse.mobCount) {
	    AutobanManager.getInstance().autoban(c, "Attacking more monster that summon can do (Skillid : "+summon.getSkill()+" Count : " + numAttacked + ", allowed : " + sse.mobCount + ")");
	    return;
	}
	final List<SummonAttackEntry> allDamage = new ArrayList<SummonAttackEntry>();
	chr.getCheatTracker().checkSummonAttack();

	for (int i = 0; i < numAttacked; i++) {
	    final MapleMonster mob = map.getMonsterByOid(slea.readInt());

	    if (mob == null) {
		continue;
	    }
	    if (chr.getPosition().distanceSq(mob.getPosition()) > 250000.0) {
		chr.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER_SUMMON);
	    }
	    slea.skip(14); // who knows
	    final int damage = slea.readInt();
	    allDamage.add(new SummonAttackEntry(mob, damage));
	}
	map.broadcastMessage(chr, MaplePacketCreator.summonAttack(summon.getOwnerId(), summon.getSkill(), animation, allDamage), summon.getPosition());

	final ISkill summonSkill = SkillFactory.getSkill(summon.getSkill());
	final MapleStatEffect summonEffect = summonSkill.getEffect(summon.getSkillLevel());

	for (SummonAttackEntry attackEntry : allDamage) {
	    final int toDamage = attackEntry.getDamage();
	    final MapleMonster mob = attackEntry.getMonster();

	    if (toDamage > 0 && summonEffect.getMonsterStati().size() > 0) {
		if (summonEffect.makeChanceResult()) {
		    mob.applyStatus(chr, new MonsterStatusEffect(summonEffect.getMonsterStati(), summonSkill, null, false), summonEffect.isPoison(), 4000, false);
		}
	    }
	    if (chr.isJounin() || toDamage < 100000 || chr.getReborns() > 100) {
		mob.damage(chr, toDamage, true);
		chr.checkMonsterAggro(mob);
	    } else {
		AutobanManager.getInstance().autoban(c, "High Summon Damage (" + toDamage + " to " + attackEntry.getMonster().getId() + ")");
		// TODO : Check player's stat for damage checking.
	    }
	}
    }
}
