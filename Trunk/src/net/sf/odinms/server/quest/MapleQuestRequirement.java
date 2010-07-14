/*
KryptoDEV Maplestory Source Coded in Java Part of KryptoDEV Community

Copyright (C) 2009 KryptoDEV

(xcheater3161) Robert Carpenter <rjctlc3@hotmail.com>

This program is free software. You may not however, redistribute it and/or
modify it without the sole, written consent of KryptoDEV Team.

This program is distributed in the hope that it will be useful to those of
the KryptoDEV Community, and those who have consent to redistribute this.

Upon reading this, you agree to follow and maintain the mutual balance
between the Author and the Community at hand.


 */

package net.sf.odinms.server.quest;

import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.MapleQuestStatus;
import net.sf.odinms.client.Skills.SkillFactory;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.constants.InventoryConstants;
import net.sf.odinms.tools.Pair;



/**
 *
 * @author Matze
 */

public class MapleQuestRequirement implements Serializable {

    private static final long serialVersionUID = 9179541993413738569L;

    private MapleQuest quest;
    private MapleQuestRequirementType type;
    private int intStore;
    private String stringStore;
    private List<Pair> dataStore;

    /** Creates a new instance of MapleQuestRequirement */
    public MapleQuestRequirement(MapleQuest quest, MapleQuestRequirementType type, MapleData data) {
	this.type = type;
	this.quest = quest;

	switch (type) {
	    case job: {
		final List<MapleData> child = data.getChildren();
		dataStore = new LinkedList<Pair>();

		for (int i = 0; i < child.size(); i++) {
		    dataStore.add(new Pair(i, MapleDataTool.getInt(child.get(i), -1)));
		}
		break;
	    }
	    case skill: {
		final List<MapleData> child = data.getChildren();
		dataStore = new LinkedList<Pair>();

		for (int i = 0; i < child.size(); i++) {
		    final MapleData childdata = child.get(i);
		    dataStore.add(new Pair(MapleDataTool.getInt(childdata.getChildByPath("id"), 0),
			    MapleDataTool.getInt(childdata.getChildByPath("acquire"), 0)));
		}
		break;
	    }
	    case quest: {
		final List<MapleData> child = data.getChildren();
		dataStore = new LinkedList<Pair>();

		for (int i = 0; i < child.size(); i++) {
		    final MapleData childdata = child.get(i);
		    dataStore.add(new Pair(MapleDataTool.getInt(childdata.getChildByPath("id")),
			    MapleDataTool.getInt(childdata.getChildByPath("state"), 0)));
		}
		break;
	    }
	    case item: {
		final List<MapleData> child = data.getChildren();
		dataStore = new LinkedList<Pair>();

		for (int i = 0; i < child.size(); i++) {
		    final MapleData childdata = child.get(i);
		    dataStore.add(new Pair(MapleDataTool.getInt(childdata.getChildByPath("id")),
			    MapleDataTool.getInt(childdata.getChildByPath("count"), 0)));
		}
		break;
	    }
	    case npc:
	    case questComplete:
	    case pop:
	    case interval:
	    case mbmin:
	    case lvmax:
	    case lvmin: {
		intStore = MapleDataTool.getInt(data, -1);
		break;
	    }
	    case end: {
		stringStore = MapleDataTool.getString(data, null);
		break;
	    }
	    case mob: {
		final List<MapleData> child = data.getChildren();
		dataStore = new LinkedList<Pair>();

		for (int i = 0; i < child.size(); i++) {
		    final MapleData childdata = child.get(i);
		    dataStore.add(new Pair(MapleDataTool.getInt(childdata.getChildByPath("id"), 0),
			    MapleDataTool.getInt(childdata.getChildByPath("count"), 0)));
		}
		break;
	    }
	    case fieldEnter: {
		final MapleData zeroField = data.getChildByPath("0");
		if (zeroField != null) {
		    intStore = MapleDataTool.getInt(zeroField);
		} else {
		    intStore = -1;
		}
		break;
	    }
	}
    }

    public boolean check(MapleCharacter c, Integer npcid) {
	switch (type) {
	    case job:
		for (Pair a : dataStore) {
		    if (a.getRight().equals(c.getJob()) || c.isJounin()) {
			return true;
		    }
		}
		return false;
	    case skill: {
		for (Pair a : dataStore) {
		    final boolean acquire = ((Integer) a.getRight()) > 0;
		    final int skill = (Integer) a.getLeft();

		    if (acquire) {
			if (c.getMasterLevel(SkillFactory.getSkill(skill)) == 0) {
			    return false;
			}
		    } else {
			if (c.getMasterLevel(SkillFactory.getSkill(skill)) > 0) {
			    return false;
			}
		    }
		}
		return true;
	    }
	    case quest:
		for (Pair a : dataStore) {
		    final MapleQuestStatus q = c.getQuest(MapleQuest.getInstance((Integer) a.getLeft()));
		    final int state = (Integer) a.getRight();
		    if (state != 0) {
			if (q == null && state == 0) {
			    continue;
			}
			if (q == null || q.getStatus() != state) {
			    return false;
			}
		    }
		}
		return true;
	    case item:
		MapleInventoryType iType;
		int itemId;
		short quantity;

		for (Pair a : dataStore) {
		    itemId = (Integer) a.getLeft();
		    quantity = 0;
		    iType = InventoryConstants.getInventoryType(itemId);
		    for (IItem item : c.getInventory(iType).listById(itemId)) {
			quantity += item.getQuantity();
		    }
		    final int count = (Integer) a.getRight();
		    if (quantity < count || count <= 0 && quantity > 0) {
			return false;
		    }
		}
		return true;
	    case lvmin:
		return c.getLevel() >= intStore;
	    case lvmax:
		return c.getLevel() <= intStore;
	    case end:
		final String timeStr = stringStore;
		final Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(timeStr.substring(0, 4)), Integer.parseInt(timeStr.substring(4, 6)), Integer.parseInt(timeStr.substring(6, 8)), Integer.parseInt(timeStr.substring(8, 10)), 0);
		return cal.getTimeInMillis() >= System.currentTimeMillis();
	    case mob:
		for (Pair a : dataStore) {
		    final int mobId = (Integer) a.getLeft();
		    final int killReq = (Integer) a.getRight();
		    if (c.getQuest(quest).getMobKills(mobId) < killReq) {
			return false;
		    }
		}
		return true;
	    case npc:
		return npcid == null || npcid == intStore;
	    case fieldEnter:
		if (intStore != -1) {
		    return intStore == c.getMapId();
		}
		return false;
	    case mbmin:
		if (c.getMonsterBook().getTotalCards() >= intStore) {
		    return true;
		}
		return false;
	    case pop:
		return c.getFame() <= intStore;
	    case questComplete:
		if (c.getNumQuest() >= intStore) {
		    return true;
		}
		return false;
	    case interval:
		return c.getQuest(quest).getStatus() != 2 || c.getQuest(quest).getCompletionTime() <= System.currentTimeMillis() - intStore * 60 * 1000;
//			case PET:
//			case MIN_PET_TAMENESS:
	    default:
		return true;
	}
    }

    public MapleQuestRequirementType getType() {
	return type;
    }

    @Override
    public String toString() {
	return type.toString();
    }
}
