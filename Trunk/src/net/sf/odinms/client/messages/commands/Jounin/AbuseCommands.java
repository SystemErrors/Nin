/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.Jounin;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.NinjaMS.Processors.SmegaProcessor;
import net.sf.odinms.client.messages.GMCommand;
import net.sf.odinms.client.messages.GMCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.server.constants.Items.MegaPhoneType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Admin
 */
public class AbuseCommands implements GMCommand {

    public GMCommandDefinition[] getDefinition() {
        return new GMCommandDefinition[]{
                    new GMCommandDefinition("kill", "ign", "kills the person la"),
                    new GMCommandDefinition("torture", "<ign> <reason>", "world tour command. Dont abuse this bishes"),
                    new GMCommandDefinition("addtao", "ign amount", "adds amount of tao. negative value possible. Dont abuse"),
                    new GMCommandDefinition("mc", "ign text", "mind control"),
                    new GMCommandDefinition("gmwannabe", "ign text", "white chat mind control"),
                    new GMCommandDefinition("bp", "number", "set your boss points"),
                    new GMCommandDefinition("jqp", "number", "set your jq points"),};
    }

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        MapleCharacter player = c.getPlayer();
        if (splitted[0].equalsIgnoreCase("kill")) {
            if (splitted.length != 2) {
                mc.dropMessage("read commands la nub");
            } else {
                MapleCharacter noob = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if (noob == null) {
                    mc.dropMessage("the noob is not online/ is in different channel / does not exist la");
                } else {
                    if (player.canFuck(noob)) {
                        noob.kill();
                        mc.dropMessage("[Anbu]MURDERER LA EUU. EUU HAVE MURDERED DA JEWZ");
                    } else {
                        player.kill();
                        mc.dropMessage("[Anbu] You dream too much");
                    }
                }
            }
        } else if (splitted[0].equalsIgnoreCase("torture")) {
            if (splitted.length < 3) {
                mc.dropMessage("Fail Jounin is Fail. Read !commands la nub. It is : !commands <ign> <reason>");
                return;
            }
            WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
            if (loc != null) {
                MapleCharacter noob = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                if (noob != null) {
                    if (player.canFuck(noob)) {
                        noob.torture(StringUtil.joinStringFrom(splitted, 2));
                    } else {
                        c.getPlayer().torture("trying to torture " + noob.getName());
                    }
                } else {
                    mc.dropMessage(splitted[1] + " does not exist.");
                }
            } else {
                mc.dropMessage(splitted[1] + " does not exist.");
            }
        } else if (splitted[0].equalsIgnoreCase("addtao")) {
            if (splitted.length != 3) {
                mc.dropMessage("Correct Syntax : !addtao IGN amount");
            } else {
                MapleCharacter noob = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if (noob != null) {
                    int amount;
                    try {
                        amount = Integer.parseInt(splitted[2]);
                    } catch (NumberFormatException numberFormatException) {
                        mc.dropMessage("Drunk asshole. read the syntax. !addtao ign number");
                        return;
                    }
                    noob.gainItem(4032016, amount);
                    noob.dropMessage("[Anbu]" + player.getName() + " has given you " + amount + " taos");
                    player.dropMessage("You have given "+ noob.getName() +" "+ amount + " taos");
                } else {
                    mc.dropMessage("player not in your channel");
                }
            }
        } else if (splitted[0].equalsIgnoreCase("mc")) {
            MapleCharacter noobs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (player.canFuck(noobs)) {
                player.getMap().broadcastMessage(MaplePacketCreator.getChatText(player.getId(), "Hey everybody. Fuck me. You all fucking suck. BAN ME GM. BAHAHAHA.", player.isJounin(), 1));
            } else {
                String text = StringUtil.joinStringFrom(splitted, 2);
                noobs.getMap().broadcastMessage(MaplePacketCreator.getChatText(noobs.getId(), text, noobs.isJounin(), 0));
            }
        } else if (splitted[0].equalsIgnoreCase("gmwannabe")) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (player.canFuck(victim)) {
                String text = StringUtil.joinStringFrom(splitted, 2);
                player.getMap().broadcastMessage(MaplePacketCreator.getChatText(player.getId(), text, true, 0));
            } else {
                player.getMap().broadcastMessage(MaplePacketCreator.getChatText(player.getId(), "Hey everybody. Fuck me. You all fucking suck. BAN ME GM. BAHAHAHA.", true, 0));
            }
        } else if (splitted[0].equalsIgnoreCase("bp")) {
            int bossPoints = 0;
            try {
                bossPoints = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException w) {
                mc.dropMessage("[Anbu]Drunk bastard learn to type a number rite!");
            }
            player.setBossPoints(bossPoints);
            mc.dropMessage("[Anbu]Your boss pq points set to : " + bossPoints);
        } else if (splitted[0].equalsIgnoreCase("jqp")){
            int jqPoints = 0;
            try {
                jqPoints = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException w) {
                mc.dropMessage("[Anbu]Drunk bastard learn to type a number rite!");
            }
            player.setJqpoints(jqPoints);
             mc.dropMessage("[Anbu]Your JQ points set to : " + jqPoints);
        }
    }
}
