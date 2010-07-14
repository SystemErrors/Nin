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
package net.sf.odinms.client.Skills;

import java.util.ArrayList;
import java.util.List;

import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.client.Buffs.MapleStatEffect;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.server.life.Element;

public class Skill implements ISkill {

    private int id;
    private final List<MapleStatEffect> effects = new ArrayList<MapleStatEffect>();
    private Element element;
    private byte level;
    private int animationTime, requiredSkill;
    private boolean action;
    private boolean invisible;
    private boolean chargeskill;

    private Skill(final int id) {
	super();
	this.id = id;
    }

    @Override
    public int getId() {
	return id;
    }

    public static final Skill loadFromData(final int id, final MapleData data) {
	Skill ret = new Skill(id);

	boolean isBuff = false;
	final int skillType = MapleDataTool.getInt("skillType", data, -1);
	final String elem = MapleDataTool.getString("elemAttr", data, null);
	if (elem != null) {
	    ret.element = Element.getFromChar(elem.charAt(0));
	} else {
	    ret.element = Element.NEUTRAL;
	}
	ret.invisible = MapleDataTool.getInt("invisible", data, 0) > 0;

	// unfortunatly this is only set for a few skills so we have to do some more to figure out if it's a buff �.o
	final MapleData effect = data.getChildByPath("effect");
	if (skillType != -1) {
	    if (skillType == 2) {
		isBuff = true;
	    }
	} else {
	    final MapleData action_ = data.getChildByPath("action");
	    final MapleData hit = data.getChildByPath("hit");
	    final MapleData ball = data.getChildByPath("ball");

	    boolean action = false;
	    if (action_ == null) {
		if (data.getChildByPath("prepare/action") != null) {
		    action = true;
		} else {
		    switch (id) {
			case 5201001:
			case 5221009:
			    action = true;
			    break;
		    }
		}
	    } else {
		action = true;
	    }
	    ret.action = action;
	    isBuff = effect != null && hit == null && ball == null;
	    isBuff |= action_ != null && MapleDataTool.getString("0", action_, "").equals("alert2");
	    switch (id) {
		case 2301002: // heal is alert2 but not overtime...
		case 2111003: // poison mist
		case 12111005: // Flame Gear
		case 2111002: // explosion
		case 4211001: // chakra
		case 2121001: // Big bang
		case 2221001: // Big bang
		case 2321001: // Big bang
		    isBuff = false;
		    break;
		case 1004: // monster riding
		case 5101004: // hide is a buff -.- atleast for us o.o"
		case 1111002: // combo
		case 4211003: // pickpocket
		case 4111001: // mesoup
		case 15111002: // Super Transformation
		case 5111005: // Transformation
		case 5121003: // Super Transformation
		case 13111005: // Alabtross
		case 21000000: // Aran Combo
		case 21101003: // Body Pressure
		case 5211001: // Pirate octopus summon
		case 5211002:
		case 5220002: // wrath of the octopi
		    isBuff = true;
		    break;
	    }
	}
	ret.chargeskill = data.getChildByPath("keydown") != null;

	for (final MapleData level : data.getChildByPath("level")) {
	    ret.effects.add(MapleStatEffect.loadSkillEffectFromData(level, id, isBuff));
	}
	final MapleData reqDataRoot = data.getChildByPath("req");
	if (reqDataRoot != null) {
	    for (final MapleData reqData : reqDataRoot.getChildren()) {
		ret.requiredSkill = Integer.parseInt(reqData.getName());
		ret.level = (byte) MapleDataTool.getInt(reqData, 1);
	    }
	}
	ret.animationTime = 0;
	if (effect != null) {
	    for (final MapleData effectEntry : effect) {
		ret.animationTime += MapleDataTool.getIntConvert("delay", effectEntry, 0);
	    }
	}
	return ret;
    }

    @Override
    public MapleStatEffect getEffect(final int level) {
	return effects.get(level - 1);
    }

    @Override
    public MapleStatEffect getEffect(final MapleCharacter chr, final int level) {
	return effects.get(level - 1);
    }

    @Override
    public boolean getAction() {
	return action;
    }

    @Override
    public boolean isChargeSkill() {
	return chargeskill;
    }

    @Override
    public boolean isInvisible() {
	return invisible;
    }

    @Override
    public boolean hasRequiredSkill() {
	return level > 0;
    }

    @Override
    public int getRequiredSkillLevel() {
	return level;
    }

    @Override
    public int getRequiredSkillId() {
	return requiredSkill;
    }

    @Override
    public byte getMaxLevel() {
	return (byte) effects.size();
    }

    @Override
    public boolean canBeLearnedBy(int job) {
	int jid = job;
	int skillForJob = id / 10000;

	if (job < 1000) {
	    if (jid / 100 != skillForJob / 100 && skillForJob / 100 != 0) { // wrong job
		return false;
	    }
	} else {
	    if (jid / 1000 != skillForJob / 1000 && skillForJob / 1000 != 0) { // wrong job
		return false;
	    }
	}
	if ((skillForJob / 10) % 10 > (jid / 10) % 10) { // wrong 2nd job
	    return false;
	}
	if (skillForJob % 10 > jid % 10) { // wrong 3rd/4th job
	    return false;
	}
	return true;
    }

    @Override
    public boolean isFourthJob() {
	return ((id / 10000) % 10) == 2;
    }

    @Override
    public Element getElement() {
	return element;
    }

    @Override
    public int getAnimationTime() {
	return animationTime;
    }

    @Override
    public boolean isBeginnerSkill() {
	String idString = String.valueOf(id);
	if (idString.length() == 4 || idString.length() == 1) {
	    return true;
	}
	return false;
    }

    /**
     * To check the beginner and nobless skills
     * @return
     */
    public boolean isDefaultSkill() {
        int[] premax = {8, 1000, 1001, 1002, 1003, 1004, 1005, 10001000, 10001001, 10001002, 10001003, 10001004, 10001005, 10001006};
        for (int a = 0; a < premax.length; a++) {
            if (premax[a] == id) {
                return true;
            }
        }
        return false;
    }
}
