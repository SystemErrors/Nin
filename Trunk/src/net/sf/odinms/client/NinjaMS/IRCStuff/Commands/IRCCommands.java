/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.IRCStuff.Commands;

import java.util.List;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.NinjaMS.IRCStuff.MainIRC;
import net.sf.odinms.client.NinjaMS.Processors.CharInfoProcessor;
import net.sf.odinms.client.NinjaMS.Processors.ConnectedProcessor;
import net.sf.odinms.client.NinjaMS.Processors.SearchProcessor;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Owner
 */
class IRCCommands {

    static void execute(String sender, String[] splitted, String channel) {
        String command = splitted[0];
        if (command.equalsIgnoreCase("commands")) {
            ircMsg(sender, "You should bang a wall and get Raped by AJ");
        } else if (command.equalsIgnoreCase("connected")) {
            ircMsg(channel, ConnectedProcessor.getConnected());
        } else if (command.equalsIgnoreCase("online")) {
            if (splitted.length != 2) {
                ircMsg(channel, sender + " is a retard. Syntax : !online <channel number>");
            } else {
                int chan = Integer.parseInt(splitted[1]);
                ircMsg(channel, ConnectedProcessor.getOnline(chan));
            }
        } else if (command.equalsIgnoreCase("ninjaglare")) {
            MapleCharacter other = null;
            try {
                WorldLocation loc = ChannelServer.getInstance(1).getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    other = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                } else {
                    ircMsg(sender, splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                ircMsg(sender, splitted[1] + "' does not exist, is CCing, or is offline.");
            }
            for (String lol : CharInfoProcessor.getNinjaGlare(other)) {
                ircMsg(sender, lol);
            }
        } else if (command.equalsIgnoreCase("playercommands")) {
            ircMsg(sender, "Under Construction");
        } else if (command.equalsIgnoreCase("itemid")) {
            List<String> ids = SearchProcessor.getItemId(StringUtil.joinStringFrom(splitted, 1));
            if (ids != null && ids.size() > 0) {
                for (String id : ids) {
                    ircMsg(sender, id);
                }
            } else {
                ircMsg(sender, "No such item");
            }
        } else if (command.equalsIgnoreCase("mapid")) {
            List<String> ids = SearchProcessor.getMapId(StringUtil.joinStringFrom(splitted, 1));
            if (ids != null && ids.size() > 0) {
                for (String id : ids) {
                    ircMsg(sender, id);
                }
            } else {
                ircMsg(sender, "No such Map");
            }
        } else if (command.equalsIgnoreCase("mobid")) {
            List<String> ids = SearchProcessor.getMobId(StringUtil.joinStringFrom(splitted, 1));
            if (ids != null && ids.size() > 0) {
                for (String id : ids) {
                    ircMsg(sender, id);
                }
            } else {
                ircMsg(sender, "No such Monster");
            }
        } else if (command.equalsIgnoreCase("npcid")) {
            List<String> ids = SearchProcessor.getNPCId(StringUtil.joinStringFrom(splitted, 1));
            if (ids != null && ids.size() > 0) {
                for (String id : ids) {
                    ircMsg(sender, id);
                }
            } else {
                ircMsg(sender, "No such NPC");
            }
        } else if (command.equalsIgnoreCase("whodrops")) {
            int itemid = Integer.parseInt(splitted[1]);
            ircMsg(channel, "You searched itemid : " +itemid +" on our whodrops database");
            List<String> ret = SearchProcessor.whoDrops(itemid);
            if (ret != null && ret.size() > 0) {
                for (String id : ret) {
                    ircMsg(sender, id);
                }
            } else {
                ircMsg(" No mob drops the specified item or you entered wrong itemid");
            }
        } else if (command.equalsIgnoreCase("roulette")) {
            double lol = Math.random() * 100;
            if (lol > 50) {
                ircMsg(channel, " You seem to be lucky today. :p");
            } else {
                kick(channel, sender, "You are such a unlucky fag");
            }
        } 
    }

    private static void ircMsg(String message) {
        MainIRC.getInstance().sendIrcMessage(message);
    }

    private static void ircMsg(String target, String message) {
        MainIRC.getInstance().sendMessage(target, message);
    }

    private static void kick(String channel, String sender, String reason) {
        MainIRC.getInstance().kick(channel, sender, reason);
    }
}
