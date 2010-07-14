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

import java.rmi.RemoteException;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.AdminCommand;
import net.sf.odinms.client.messages.AdminCommandDefinition;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.scripting.portal.PortalScriptManager;
import net.sf.odinms.scripting.reactor.ReactorScriptManager;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.server.life.MapleMonsterInformationProvider;

public class ReloadingCommands implements AdminCommand {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReloadingCommands.class);

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        ChannelServer cserv = c.getChannelServer();
         if (splitted[0].equals("clearPortalScripts")) {
            PortalScriptManager.getInstance().clearScripts();
        } else if (splitted[0].equals("cleardrops")) {
            MapleMonsterInformationProvider.getInstance().clearDrops();
        } else if (splitted[0].equals("clearReactorDrops")) {
            ReactorScriptManager.getInstance().clearDrops();
        } else if (splitted[0].equals("clearshops")) {
            MapleShopFactory.getInstance().clear();
        } else if (splitted[0].equals("clearevents")) {
            for (ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
        } else if (splitted[0].equals("reloadallcommands")) {
            CommandProcessor.getInstance().reloadAllCommands();
        } else if (splitted[0].equals("reloadgmCommands")) {
            CommandProcessor.getInstance().reloadGMCommands();
        } else if (splitted[0].equals("reloadinterncommands")) {
            CommandProcessor.getInstance().reloadInternCommands();
        } else if (splitted[0].equals("reloadadmincommands")) {
            CommandProcessor.getInstance().reloadAdminCommands();
        } else if (splitted[0].equals("reloadplayercommands")) {
            CommandProcessor.getInstance().reloadPlayerCommands();
        } else if (splitted[0].equals("reloaddonatorcommands")) {
            CommandProcessor.getInstance().reloadDonatorCommands();
        }
    }

    @Override
    public AdminCommandDefinition[] getDefinition() {
        return new AdminCommandDefinition[]{
                    new AdminCommandDefinition("clearguilds", "", ""),
                    new AdminCommandDefinition("clearPortalScripts", "", ""),
                    new AdminCommandDefinition("cleardrops", "", ""),
                    new AdminCommandDefinition("clearReactorDrops", "", ""),
                    new AdminCommandDefinition("clearshops", "", ""),
                    new AdminCommandDefinition("clearevents", "", ""),
                    new AdminCommandDefinition("reloadallcommands", "", ""),};
    }
}
