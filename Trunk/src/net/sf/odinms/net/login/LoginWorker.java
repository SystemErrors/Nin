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
package net.sf.odinms.net.login;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.FileoutputUtil;
import net.sf.odinms.tools.Packets.LoginPacket;
import net.sf.odinms.tools.Pair;


public class LoginWorker {

    private static Runnable persister;
    private static final List<Pair<Integer, String>> IPLog = new LinkedList<Pair<Integer, String>>();
    private static long lastUpdate = 0;
    private static final Lock mutex = new ReentrantLock();

    protected LoginWorker() {
	persister = new PersistingTask();
	TimerManager.getInstance().register(persister, 1800000); // 30 min once
    }

    private static class PersistingTask implements Runnable {

	@Override
	public void run() {
	    final StringBuilder sb = new StringBuilder();

	    mutex.lock();
	    try {
		final String time = FileoutputUtil.CurrentReadable_Time();

		for (Pair<Integer, String> logentry : IPLog) {
		    sb.append("ACCID : ");
		    sb.append(logentry.getLeft());
		    sb.append(", IP : ");
		    sb.append(logentry.getRight());
		    sb.append(", TIME : ");
		    sb.append(time);
		    sb.append("\n");
		}
		IPLog.clear();
	    } finally {
		mutex.unlock();
	    }
	    FileoutputUtil.log(FileoutputUtil.IP_Log, sb.toString());
	}
    }

    public static void registerClient(final MapleClient c) {
	if (c.finishLogin() == 0) {
	    c.getSession().write(LoginPacket.getAuthSuccessRequest(c));
	    c.setIdleTask(TimerManager.getInstance().schedule(new Runnable() {

		public void run() {
		    c.getSession().close();
		}
	    }, 10 * 60 * 10000));
	} else {
	    c.getSession().write(LoginPacket.getLoginFailed(7));
	    return;
	}
	final LoginServer LS = LoginServer.getInstance();

	if (System.currentTimeMillis() - lastUpdate > 300000) { // Update once every 5 minutes
            try {
                // Update once every 5 minutes
                lastUpdate = System.currentTimeMillis();
                final Map<Integer, Integer> load = LS.getWorldInterface().getChannelLoad();
                if (load == null) {
                    // In an unfortunate event that client logged in before load
                    lastUpdate = 0;
                    c.getSession().write(LoginPacket.getLoginFailed(7));
                    return;
                }
                final double loadFactor = 1200 / ((double) LS.getUserLimit() / load.size());
                for (Entry<Integer, Integer> entry : load.entrySet()) {
                    load.put(entry.getKey(), Math.min(1200, (int) (entry.getValue() * loadFactor)));
                }
                LS.setLoad(load);
            } catch (RemoteException ex) {
                System.err.println("Login Worker Error : " + ex);
            }
	    
	}

	c.getSession().write(LoginPacket.getServerList(6, LS.getServerName(), LS.getLoad()));
	c.getSession().write(LoginPacket.getEndOfServerList());

	mutex.lock();
	try {
	    IPLog.add(new Pair<Integer, String>(c.getAccID(), c.getSession().getRemoteAddress().toString()));
	} finally {
	    mutex.unlock();
	}
    }
}
