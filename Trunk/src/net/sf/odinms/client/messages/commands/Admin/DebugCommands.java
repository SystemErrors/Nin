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
package net.sf.odinms.client.messages.commands.Admin;

import java.awt.Point;
import java.rmi.RemoteException;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.client.messages.AdminCommand;
import net.sf.odinms.client.messages.AdminCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.scripting.portal.PortalScriptManager;
import net.sf.odinms.scripting.reactor.ReactorScriptManager;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.life.MapleMonsterInformationProvider;
import net.sf.odinms.server.maps.MapleDoor;
import net.sf.odinms.server.maps.MapleMap;

import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

public class DebugCommands implements AdminCommand {

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        MapleCharacter player = c.getPlayer();
        if (splitted[0].equals("nearestportal")) {
            final MaplePortal portal = player.getMap().findClosestSpawnpoint(player.getPosition());
            mc.dropMessage(portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());
        } else if (splitted[0].equals("spawndebug")) {
            c.getPlayer().getMap().spawnDebug(mc);
        } else if (splitted[0].equals("door")) {
            Point doorPos = new Point(player.getPosition());
            doorPos.y -= 270;
            MapleDoor door = new MapleDoor(c.getPlayer(), doorPos);
            door.getTarget().addMapObject(door);
            // c.getSession().write(MaplePacketCreator.spawnDoor(/*c.getPlayer().getId()*/ 0x1E47, door.getPosition(),
            // false));
			/* c.getSession().write(MaplePacketCreator.saveSpawnPosition(door.getPosition())); */
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(HexTool.getByteArrayFromHexString("B9 00 00 47 1E 00 00 0A 04 76 FF"));
            c.getSession().write(mplew.getPacket());
            mplew = new MaplePacketLittleEndianWriter();
            mplew.write(HexTool.getByteArrayFromHexString("36 00 00 EF 1C 0D 4C 3E 1D 0D 0A 04 76 FF"));
            c.getSession().write(mplew.getPacket());
            c.getSession().write(MaplePacketCreator.enableActions());
            door = new MapleDoor(door);
            door.getTown().addMapObject(door);
        } else if (splitted[0].equals("timerdebug")) {
            TimerManager.getInstance().dropDebugInfo(mc);
        } else if (splitted[0].equals("threads")) {
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            String filter = "";
            if (splitted.length > 1) {
                filter = splitted[1];
            }
            for (int i = 0; i < threads.length; i++) {
                String tstring = threads[i].toString();
                if (tstring.toLowerCase().indexOf(filter.toLowerCase()) > -1) {
                    mc.dropMessage(i + ": " + tstring);
                }
            }
        } else if (splitted[0].equals("showtrace")) {
            if (splitted.length < 2) {
                mc.dropMessage("Syntax : /showtrace <threadid>");
            }
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            Thread t = threads[Integer.parseInt(splitted[1])];
            mc.dropMessage(t.toString() + ":");
            for (StackTraceElement elem : t.getStackTrace()) {
                mc.dropMessage(elem.toString());
            }
        } else if (splitted[0].equals("fakerelog")) {
            c.getSession().write(MaplePacketCreator.getCharInfo(player));
            player.getMap().removePlayer(player);
            player.getMap().addPlayer(player);
        } else if (splitted[0].equals("toggleoffense")) {
            try {
                CheatingOffense co = CheatingOffense.valueOf(splitted[1]);
                co.setEnabled(!co.isEnabled());
            } catch (IllegalArgumentException iae) {
                mc.dropMessage("Offense " + splitted[1] + " not found");
            }
        } else if (splitted[0].equals("tdrops")) {
            player.getMap().toggleDrops();
        } else if (splitted[0].equals("reload")) {
            if (splitted[1].equalsIgnoreCase("portals")) {
                PortalScriptManager.getInstance().clearScripts();
                mc.dropMessage("Portal Scripts Cleared");
            } else if (splitted[1].equalsIgnoreCase("map")) {
                int mapid = 0;
                try {
                    mapid = Integer.parseInt(splitted[2]);
                } catch (NumberFormatException numberFormatException) {
                }
                for (ChannelServer cs : ChannelServer.getAllInstances()) {
                    MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);
                    map.deleteAndReloadMap();
                }
            } else if (splitted[1].equalsIgnoreCase("timer")) {
                try {
                    TimerManager.getInstance().stop();
                } catch (Exception e) {
                    mc.dropMessage("Error : " + e);
                    e.printStackTrace();
                } finally {
                    try {
                        TimerManager tMan = TimerManager.getInstance();
                        tMan.start();
                        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                            cserv.restarttimers();
                        }
                        mc.dropMessage("Success");
                    } catch (Exception e) {
                        mc.dropMessage("Error : " + e);
                        e.printStackTrace();
                    }
                }
            } else if (splitted[1].equalsIgnoreCase("reactors")) {
                c.getPlayer().getMap().resetReactors();
            } else if (splitted[1].equalsIgnoreCase("events")) {
                for (ChannelServer cs : ChannelServer.getAllInstances()) {
                    cs.reloadEvents();
                }
            } else if (splitted[1].equalsIgnoreCase("guilds")) {
                try {
                    c.getPlayer().dropMessage(6, "Attempting to reload all guilds... this may take a while...");
                    c.getChannelServer().getWorldInterface().clearGuilds();
                    c.getPlayer().dropMessage(6, "Completed.");
                } catch (RemoteException re) {
                    c.getPlayer().dropMessage(6, "RemoteException occurred while attempting to reload guilds.");
                    System.err.println("RemoteException occurred while attempting to reload guilds." + re);
                }
            } else if (splitted[1].equalsIgnoreCase("cache")) {
                //     MapleMonsterInformationProvider.getInstance().clearMesos();
                MapleMonsterInformationProvider.getInstance().clearDrops();
                ReactorScriptManager.getInstance().clearDrops();
                PortalScriptManager.getInstance().clearScripts();
                MapleShopFactory.getInstance().clear();
            } else if (splitted[1].equalsIgnoreCase("drops")){
                MapleMonsterInformationProvider.getInstance().clearDrops();
            }
        }
    }

    @Override
    public AdminCommandDefinition[] getDefinition() {
        return new AdminCommandDefinition[]{
                    new AdminCommandDefinition("nearestportal", "", ""),
                    new AdminCommandDefinition("spawndebug", "", ""),
                    new AdminCommandDefinition("timerdebug", "", ""),
                    new AdminCommandDefinition("threads", "", ""),
                    new AdminCommandDefinition("showtrace", "", ""),
                    new AdminCommandDefinition("toggleoffense", "", ""),
                    new AdminCommandDefinition("fakerelog", "", ""),
                    new AdminCommandDefinition("tdrops", "", ""),
                    new AdminCommandDefinition("reload", "", ""),};
    }
}
