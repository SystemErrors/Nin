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

public enum MapleQuestActionType {

    UNDEFINED(-1), exp(0), item(1), nextQuest(2), money(3), QUEST(4), skill(5), pop(6), buffItemID(7), infoNumber(8);
    final byte type;

    private MapleQuestActionType(int type) {
	this.type = (byte) type;
    }

    public byte getType() {
	return type;
    }

    public static MapleQuestActionType getByType(byte type) {
	for (MapleQuestActionType l : MapleQuestActionType.values()) {
	    if (l.getType() == type) {
		return l;
	    }
	}
	return null;
    }

    public static MapleQuestActionType getByWZName(String name) {
	try {
	    return valueOf(name);
	} catch (IllegalArgumentException ex) {
	    return UNDEFINED;
	}
    }
}
