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
import java.util.List;
import net.sf.odinms.client.Buffs.MapleStatEffect;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.Skills.ISkill;
import net.sf.odinms.client.Skills.SkillFactory;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.constants.Skills;
import net.sf.odinms.tools.AttackPair;

/**
 *
 * @author Admin
 */
public class AttackInfo {

    public int skill, charge, lastAttackTickCount;
    public List<AttackPair> allDamage;
    public Point position;
    public byte hits, targets, tbyte, display, animation, speed, csstar, AOE, slot;

    public final MapleStatEffect getAttackEffect(final MapleCharacter chr, int skillLevel, final ISkill skill_) {
	if (Skills.isMulungSkill(skill)) {
	    skillLevel = 1;
	} else if (skillLevel == 0) {
	    return null;
	}
	if (Skills.isLinkedAranSkill(skill)) {
	    final ISkill skillLink = SkillFactory.getSkill(skill);
	    if (display > 80) {
		if (!skillLink.getAction()) {
		    AutobanManager.getInstance().autoban(chr.getClient(), "No delay hack, SkillID : " + skill);
		    return null;
		}
	    }
	    return skillLink.getEffect(chr, skillLevel);
	}
	if (display > 80) {
	    if (!skill_.getAction()) {
		AutobanManager.getInstance().autoban(chr.getClient(), "No delay hack, SkillID : " + skill);
		return null;
	    }
	}
	return skill_.getEffect(chr, skillLevel);
    }
}

