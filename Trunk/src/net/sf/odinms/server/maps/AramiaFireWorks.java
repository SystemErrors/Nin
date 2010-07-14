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

package net.sf.odinms.server.maps;

import java.awt.Point;
import java.rmi.RemoteException;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Admin
 */
public class AramiaFireWorks {

    private short kegs = 0;
    private static final AramiaFireWorks instance = new AramiaFireWorks();

    private static final int[] arrayMob = {9500168, 9500169, 9500170, 9500171, 9500173,
    9500174, 9500175, 9500176, 9500170, 9500171, 9500172, 9500173, 9500174, 9500175,
    9400569};
    private static final int[] arrayX = {2100, 2605, 1800, 2600, 3120, 2700, 2320, 2062,
    2800, 3100, 2300, 2840, 2700, 2320, 1950};
    private static final int[] arrayY = {574, 364, 574, 316, 574, 574, 403, 364, 574, 574,
    403, 574, 574, 403, 574};

    public static final AramiaFireWorks getInstance() {
	return instance;
    }

    public final void giveKegs(final MapleCharacter c, final int kegs) {
	this.kegs += kegs;

	if (this.kegs >= 2000) {
	    this.kegs = 0;
	    broadcastEvent(c);
	}
    }

    public final short getKegsPercentage() {
	return (short) ((kegs / 2000) * 10000);
    }

    private final void broadcastEvent(final MapleCharacter c) {
	try {
	    c.getClient().getChannelServer().getWorldInterface().broadcastMessage(MaplePacketCreator.serverNotice(5, "<Channel " + c.getClient().getChannel() + "> Aramia from Henesys park will shoot up the firecrackers soon!").getBytes());
	} catch (RemoteException e) {
	    c.getClient().getChannelServer().reconnectWorld();
	}
	// Henesys Park
	TimerManager.getInstance().schedule(new Runnable() {

	    @Override
	    public final void run() {
		startEvent(c.getClient().getChannelServer().getMapFactory().getMap(100000200));
	    }
	}, 10000);
    }

    private final void startEvent(final MapleMap map) {
	map.startMapEffect("Who's going crazy with the fireworks?", 5121010);

	TimerManager.getInstance().schedule(new Runnable() {

	    @Override
	    public final void run() {
		spawnMonster(map);
	    }
	}, 5000);
    }

    private final void spawnMonster(final MapleMap map) {
	Point pos;

	for (int i = 0; i < arrayMob.length; i++) {
	    pos = new Point(arrayX[i], arrayY[i]);
	    map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(arrayMob[i]), pos);
	}
    }
}
