/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.Jounin;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.NinjaMS.Processors.DonationProcessor;
import net.sf.odinms.client.messages.GMCommand;
import net.sf.odinms.client.messages.GMCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;

/**
 *
 * @author Admin
 */
public class DonationCommands implements GMCommand {

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        String cmd = splitted[0];
        MapleCharacter pl = c.getPlayer();
        if (cmd.equalsIgnoreCase("donated")) {
            if (splitted.length != 4) {
                pl.dropMessage("syntax : !donated <ign> <amount> <paypal e-mail>");
            }
            short damount = 0;
            try {
                damount = Short.parseShort(splitted[2]);
            } catch (NumberFormatException nfe) {
                pl.dropMessage(nfe.toString());
                return;
            }
            DonationProcessor.doDonation(c, splitted[1], damount, cmd);
        } else if (cmd.equalsIgnoreCase("setdamount")) {
            if (splitted.length != 3) {
                mc.dropMessage("[Anbu]Drunk?? read the commands. Syntax : !setdamount ign amount");
                return;
            }
            String name = splitted[1];
            short amt = 0;
            try {
                amt = Short.parseShort(splitted[2]);
            } catch (NumberFormatException nfe) {
                pl.dropMessage(nfe.toString());
                return;
            }
            DonationProcessor.setDAmount(c, name, amt);
        } else if (cmd.equalsIgnoreCase("setdpoint")) {
            if (splitted.length != 3) {
                mc.dropMessage("[Anbu]Drunk?? read the commands. Syntax : !setdpoint ign amount");
                return;
            }
            String name = splitted[1];
            short amt = 0;
            try {
                amt = Short.parseShort(splitted[2]);
            } catch (NumberFormatException nfe) {
                pl.dropMessage(nfe.toString());
                return;
            }
            DonationProcessor.setDPoints(c, name, amt);
        }
    }

    @Override
    public GMCommandDefinition[] getDefinition() {
        return new GMCommandDefinition[]{
                    new GMCommandDefinition("donated", "<ign> <amount> <paypal e-mail>", "Gives Donation rewards"),
                    new GMCommandDefinition("setdamount", "<ign> <amount>", "sets Donation amount"),
                    new GMCommandDefinition("setdpoint", "<ign> <amount>", "sets Donation points"),};
    }
}
