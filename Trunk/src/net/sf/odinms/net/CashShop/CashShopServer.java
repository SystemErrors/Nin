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
package net.sf.odinms.net.CashShop;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import net.sf.odinms.client.Skills.SkillFactory;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.CashShop.remote.CashShopWorldInterface;
import net.sf.odinms.net.MapleServerHandler;
import net.sf.odinms.net.ServerConstants;
import net.sf.odinms.net.ServerType;
import net.sf.odinms.net.mina.MapleCodecFactory;
import net.sf.odinms.net.world.remote.CashShopInterface;
import net.sf.odinms.net.world.remote.WorldRegistry;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.TimerManager;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;



public class CashShopServer {

    private String ip;
    private final int PORT = 6767;
    private InetSocketAddress InetSocketadd;
    private IoAcceptor acceptor;
    private CashShopWorldInterface lwi;
    private CashShopInterface wli;
    private Properties initialProp, csProp;
    private Boolean worldReady = Boolean.TRUE;
    private WorldRegistry worldRegistry = null;
    private PlayerStorage_CS players;
    private static final CashShopServer instance = new CashShopServer();

    public static final CashShopServer getInstance() {
	return instance;
    }

    public final void reconnectWorld() {
	// check if the connection is really gone
	try {
	    wli.isAvailable();
	} catch (RemoteException ex) {
	    synchronized (worldReady) {
		worldReady = Boolean.FALSE;
	    }
	    synchronized (lwi) {
		synchronized (worldReady) {
		    if (worldReady) {
			return;
		    }
		}
		System.out.println("Reconnecting to world server");
		synchronized (wli) {
		    // completely re-establish the rmi connection
		    try {
			FileReader fileReader = new FileReader(System.getProperty("net.sf.odinms.cs.config"));
			initialProp.load(fileReader);
			fileReader.close();
			ip = initialProp.getProperty("net.sf.odinms.world.host");
			Registry registry = LocateRegistry.getRegistry(ip, Registry.REGISTRY_PORT, new SslRMIClientSocketFactory());
			ip += ":" + PORT;
			worldRegistry = (WorldRegistry) registry.lookup("WorldRegistry");
			lwi = new CashShopWorldInterfaceImpl();
			wli = worldRegistry.registerCSServer(ServerConstants.CashShop_Key, ip, lwi);
			Properties dbProp = new Properties();
			fileReader = new FileReader("db.properties");
			dbProp.load(fileReader);
			DatabaseConnection.setProps(dbProp);
			DatabaseConnection.getConnection();

			dbProp.clear();
			fileReader.close();
		    } catch (Exception e) {
			System.err.println("Reconnecting failed" + e);
		    }
		    worldReady = Boolean.TRUE;
		}
	    }
	    synchronized (worldReady) {
		worldReady.notifyAll();
	    }
	}

    }

    public final void run_startup_configurations() {
	try {
	    FileReader fileReader = new FileReader(System.getProperty("net.sf.odinms.cs"));
	    csProp = new Properties();
	    csProp.load(fileReader);
	    fileReader.close();
	    ip = csProp.getProperty("net.sf.odinms.world.host") + ":" + PORT;

	    initialProp = new Properties();
	    final FileReader channelConfig = new FileReader(System.getProperty("net.sf.odinms.channel.config"));
	    initialProp.load(channelConfig);
	    channelConfig.close();
	    final Registry registry = LocateRegistry.getRegistry(initialProp.getProperty("net.sf.odinms.world.host"), Registry.REGISTRY_PORT, new SslRMIClientSocketFactory());
	    worldRegistry = (WorldRegistry) registry.lookup("WorldRegistry");

	    lwi = new CashShopWorldInterfaceImpl(this);
	    wli = worldRegistry.registerCSServer(ServerConstants.CashShop_Key, ip, lwi);
	    Properties dbProp = new Properties();
	    fileReader = new FileReader("db.properties");
	    dbProp.load(fileReader);
	    DatabaseConnection.setProps(dbProp);
	    DatabaseConnection.getConnection();

	    dbProp.clear();
	    fileReader.close();

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException("Could not connect to world server.", e);
	}

	ByteBuffer.setUseDirectBuffers(false);
	ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

	acceptor = new SocketAcceptor();

	final SocketAcceptorConfig cfg = new SocketAcceptorConfig();
	cfg.getSessionConfig().setTcpNoDelay(true);
	cfg.setDisconnectOnUnbind(true);
	cfg.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));

	TimerManager.getInstance().start();
	SkillFactory.getSkill(99999999); // Load
	MapleItemInformationProvider.getInstance();
	players = new PlayerStorage_CS();

	try {
	    InetSocketadd = new InetSocketAddress(PORT);
	    acceptor.bind(InetSocketadd, new MapleServerHandler(ServerType.CASHSHOP), cfg);
	    System.out.println("Listening on port " + PORT + ".");
	} catch (final IOException e) {
	    System.err.println("Binding to port " + PORT + " failed" + e);
	}
	Runtime.getRuntime().addShutdownHook(new Thread(new ShutDownListener()));
    }

    public static void startCashShop_main() {
	try {
	    CashShopServer.instance.run_startup_configurations();
	} catch (final Exception ex) {
	    System.err.println("Error initializing Cash Shop server" + ex);
	}
    }

    public final String getIP() {
	return ip;
    }

    public final PlayerStorage_CS getPlayerStorage() {
	return players;
    }

    public final CashShopInterface getCSInterface() {
	synchronized (worldReady) {
	    while (!worldReady) {
		try {
		    worldReady.wait();
		} catch (final InterruptedException e) {
		}
	    }
	}
	return wli;
    }

    public final void shutdown2() {
	System.out.println("Shutting down...");
	try {
	    worldRegistry.deregisterCSServer();
	} catch (final RemoteException e) {
	    // doesn't matter we're shutting down anyway
	}
	System.exit(0);
    }

    public final void shutdown() {
	System.out.println("Saving all connected clients...");

	players.disconnectAll();

	acceptor.unbindAll();

	System.exit(0);
    }

    private final class ShutDownListener implements Runnable {

	@Override
	public void run() {
	    System.out.println("Saving all connected clients...");

	    players.disconnectAll();

	    acceptor.unbindAll();

//	    shutdown2();
	}
    }
}
