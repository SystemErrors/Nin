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

/**
 *
 * @author Matze
 */

public enum MapleQuestRequirementType {

    UNDEFINED(-1), job(0), item(1), quest(2), lvmin(3), lvmax(4), end(5), mob(6), npc(7), fieldEnter(8), interval(9), startscript(10), endscript(10), pet(11), pettamenessmin(12), mbmin(13), questComplete(14), pop(15), skill(16);

    public MapleQuestRequirementType getITEM() {
	return item;
    }
    final byte type;

    private MapleQuestRequirementType(int type) {
	this.type = (byte) type;
    }

    public byte getType() {
	return type;
    }

    public static MapleQuestRequirementType getByType(byte type) {
	for (MapleQuestRequirementType l : MapleQuestRequirementType.values()) {
	    if (l.getType() == type) {
		return l;
	    }
	}
	return null;
    }

    public static MapleQuestRequirementType getByWZName(String name) {
	try {
	    return valueOf(name);
	} catch (IllegalArgumentException ex) {
	    return UNDEFINED;
	}
    }
}
