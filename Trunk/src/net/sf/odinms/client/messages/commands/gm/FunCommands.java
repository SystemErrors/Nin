/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.gm;

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
public class FunCommands implements GMCommand {

    public GMCommandDefinition[] getDefinition() {
        return new GMCommandDefinition[]{
                    new GMCommandDefinition("haxrb", "amount", "set your RB"),
                    new GMCommandDefinition("smega", "msg", "send smega with your name . "),
                    new GMCommandDefinition("strip", "ign", "strips the person"),
                    new GMCommandDefinition("text", "[normal/white/hide/buddy/party/guild/alliance/pink/blue/smega/ismega/yellow/spouse]", "text colour")
        };
    }

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        MapleCharacter player = c.getPlayer();
        if (splitted[0].equalsIgnoreCase("haxrb")) {
            if (splitted.length < 2) {
                mc.dropMessage("What a nub. Read !commands lar idiot. Syntax : !haxrb <amount>");
            }
            short fuck;
            try {
                fuck = Short.parseShort(splitted[1]);
            } catch (NumberFormatException nigger) {
                mc.dropMessage("only 32767 or below. Now eff off");
                return;
            }
            c.getPlayer().setReborns(fuck);
            mc.dropMessage("Your rebirths now is : " + fuck);
        } else if (splitted[0].equalsIgnoreCase("smega")) {
            String fuck = StringUtil.joinStringFrom(splitted, 1);
            SmegaProcessor.smegaProcessor(MegaPhoneType.ITEMMEGAPHONE, c, fuck, null, true);
        } else if (splitted[0].equalsIgnoreCase("strip")) {
            if (splitted.length < 2) {
                player.unequipEverything();
            } else {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter noob = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    if (noob != null) {
                        if (c.getPlayer().getGMLevel() >= noob.getGMLevel() || noob.isAdmin()) {
                            noob.Strip(player);
                        } else {
                            c.getPlayer().torture("trying to torture " + noob.getName());
                        }
                    } else {
                        mc.dropMessage(splitted[1] + " does not exist.");
                    }
                } else {
                    mc.dropMessage(splitted[1] + " does not exist.");
                }
            }
        } else if (splitted[0].equalsIgnoreCase("text")){
            byte[] txts = {0,1,2,3,4,5,6,8,9,10,11,12,13};
            byte sel = -1;
            String[] lol = {"normal","white","hide","buddy","party","guild","alliance","pink","blue","smega","ismega","yellow","spouse"};
            for(byte i = 0; i < txts.length; i++){
                if(splitted[1].equalsIgnoreCase(lol[i])){
                    sel = i;
                    break;
                }
            }
            if(sel == -1){
                mc.dropMessage(" please read the commands for syntax. ");
                return;
            }
            player.setTextColour(txts[sel]);
            mc.dropMessage("your text colour set to  " + lol[sel]);
        }
    }
}
