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

package net.sf.odinms.net.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.world.CharacterTransfer;
import net.sf.odinms.net.world.remote.CheaterData;
import net.sf.odinms.server.TimerManager;

public class PlayerStorage {

    private final Lock mutex = new ReentrantLock();
    private final Lock mutex2 = new ReentrantLock();
    private final Map<String, MapleCharacter> nameToChar = new HashMap<String, MapleCharacter>();
    private final Map<Integer, MapleCharacter> idToChar = new HashMap<Integer, MapleCharacter>();
    private final Map<Integer, CharacterTransfer> PendingCharacter = new HashMap<Integer, CharacterTransfer>();

    public PlayerStorage() {
	// Prune once every 15 minutes
	TimerManager.getInstance().schedule(new PersistingTask(), 900000);
    }

    public final void registerPlayer(final MapleCharacter chr) {
	mutex.lock();
	try {
	    nameToChar.put(chr.getName().toLowerCase(), chr);
	    idToChar.put(chr.getId(), chr);
	} finally {
	    mutex.unlock();
	}
    }

    public final void registerPendingPlayer(final CharacterTransfer chr, final int playerid) {
	mutex2.lock();
	try {
	    PendingCharacter.put(playerid, chr);//new Pair(System.currentTimeMillis(), chr));
	} finally {
	    mutex2.unlock();
	}
    }

    public final void deregisterPlayer(final MapleCharacter chr) {
	mutex.lock();
	try {
	    nameToChar.remove(chr.getName().toLowerCase());
	    idToChar.remove(chr.getId());
	} finally {
	    mutex.unlock();
	}
    }

    public final void deregisterPendingPlayer(final int charid) {
	mutex2.lock();
	try {
	    PendingCharacter.remove(charid);
	} finally {
	    mutex2.unlock();
	}
    }

    public final CharacterTransfer getPendingCharacter(final int charid) {
	final CharacterTransfer toreturn = PendingCharacter.get(charid);//.right;
	if (toreturn != null) {
	    deregisterPendingPlayer(charid);
	}
	return toreturn;
    }

    public final MapleCharacter getCharacterByName(final String name) {
	return nameToChar.get(name.toLowerCase());
    }

    /*
    public MapleCharacter getPendingCharacter(int id) {
    for (MapleCharacter chr : pendingCharacter) {
    if (chr.getId() == id) {
    return chr;
    }
    }
    return null;
    }*/
    public final MapleCharacter getCharacterById(final int id) {
	return idToChar.get(id);
    }

    public final int getConnectedClients() {
	return idToChar.size();
    }

    public final List<CheaterData> getCheaters() {
	final List<CheaterData> cheaters = new ArrayList<CheaterData>();

	mutex.lock();
	try {
	    final Iterator<MapleCharacter> itr = nameToChar.values().iterator();
	    MapleCharacter chr;
	    while (itr.hasNext()) {
		chr = itr.next();

		if (chr.getCheatTracker().getPoints() > 0) {
		    cheaters.add(new CheaterData(chr.getCheatTracker().getPoints(), MapleCharacterUtil.makeMapleReadable(chr.getName()) + " (" + chr.getCheatTracker().getPoints() + ") " + chr.getCheatTracker().getSummary()));
		}
	    }
	} finally {
	    mutex.unlock();
	}
	return cheaters;
    }

    public final void disconnectAll() {
	mutex.lock();
	try {
	    final Iterator<MapleCharacter> itr = nameToChar.values().iterator();
	    MapleCharacter chr;
	    while (itr.hasNext()) {
		chr = itr.next();

		if (!chr.isJounin()) {
		    chr.getClient().disconnect(false, false);
		    chr.getClient().getSession().close();
		    itr.remove();
		}
	    }
	} finally {
	    mutex.unlock();
	}
    }

    public final String getOnlinePlayers(final boolean byGM) {
	final StringBuilder sb = new StringBuilder();

	if (byGM) {
	    mutex.lock();
	    try {
		final Iterator<MapleCharacter> itr = nameToChar.values().iterator();
		while (itr.hasNext()) {
		    sb.append(MapleCharacterUtil.makeMapleReadable(itr.next().getName()));
		    sb.append(", ");
		}
	    } finally {
		mutex.unlock();
	    }
	} else {
	    mutex.lock();
	    try {
		final Iterator<MapleCharacter> itr = nameToChar.values().iterator();
		MapleCharacter chr;
		while (itr.hasNext()) {
		    chr = itr.next();

		    if (!chr.isJounin()) {
			sb.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
			sb.append(", ");
		    }
		}
	    } finally {
		mutex.unlock();
	    }
	}
	return sb.toString();
    }

    public final void broadcastPacket(final MaplePacket data) {
	mutex.lock();
	try {
	    final Iterator<MapleCharacter> itr = nameToChar.values().iterator();
	    while (itr.hasNext()) {
		itr.next().getClient().getSession().write(data);
	    }
	} finally {
	    mutex.unlock();
	}
    }

    public final void broadcastSmegaPacket(final MaplePacket data) {
	mutex.lock();
	try {
	    final Iterator<MapleCharacter> itr = nameToChar.values().iterator();
	    MapleCharacter chr;
	    while (itr.hasNext()) {
		chr = itr.next();
		if (chr.getClient().isLoggedIn()
                        && !chr.isPsmega()
                        && !chr.isfake
                        && !chr.inCS()) {
		    chr.getClient().getSession().write(data);
		}
	    }
	} finally {
	    mutex.unlock();
	}
    }

    public final void broadcastASmegaPacket(final MaplePacket data) {
	mutex.lock();
	try {
	    final Iterator<MapleCharacter> itr = nameToChar.values().iterator();
	    MapleCharacter chr;
	    while (itr.hasNext()) {
		chr = itr.next();
		if (chr.getClient().isLoggedIn()
                        && !chr.isAsmega()
                        && !chr.isfake
                        && !chr.inCS()) {
		    chr.getClient().getSession().write(data);
		}
	    }
	} finally {
	    mutex.unlock();
	}
    }

    public final void broadcastGMPacket(final MaplePacket data) {
	mutex.lock();
	try {
	    final Iterator<MapleCharacter> itr = nameToChar.values().iterator();
	    MapleCharacter chr;
	    while (itr.hasNext()) {
		chr = itr.next();
		if (chr.getClient().isLoggedIn() && chr.isJounin()) {
		    chr.getClient().getSession().write(data);
		}
	    }
	} finally {
	    mutex.unlock();
	}
    }

    public final void broadcastStaffPacket(final MaplePacket data) {
	mutex.lock();
	try {
	    final Iterator<MapleCharacter> itr = nameToChar.values().iterator();
	    MapleCharacter chr;
	    while (itr.hasNext()) {
		chr = itr.next();
		if (chr.getClient().isLoggedIn() && chr.isChunin()) {
		    chr.getClient().getSession().write(data);
		}
	    }
	} finally {
	    mutex.unlock();
	}
    }



    public class PersistingTask implements Runnable {

	@Override
	public void run() {
	    mutex2.lock();
	    try {
		final long currenttime = System.currentTimeMillis();
		final Iterator<Map.Entry<Integer, CharacterTransfer>> itr = PendingCharacter.entrySet().iterator();

		while (itr.hasNext()) {
		    if (currenttime - itr.next().getValue().TransferTime > 40000) { // 40 sec
			itr.remove();
		    }
		}
		TimerManager.getInstance().schedule(new PersistingTask(), 900000);
	    } finally {
		mutex2.unlock();
	    }
	}
    }
}

