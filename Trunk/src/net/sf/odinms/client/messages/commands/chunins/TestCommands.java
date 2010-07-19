/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.chunins;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.InternCommand;
import net.sf.odinms.client.messages.InternCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;

/**
 *
 * @author Owner
 */
public class TestCommands implements InternCommand {

    public InternCommandDefinition[] getDefinition() {
        return new InternCommandDefinition[]{
                   new InternCommandDefinition("reloadevents", "", "Reload Events. Use only if many players are stuck"),
                   new InternCommandDefinition("rebootcc", "channel number", "restarts channel. Test command dont use and fuck up the server thanks.")
        };
    }

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        if(splitted[0].equalsIgnoreCase("reloadevents")) {
            c.getChannelServer().reloadEvents();
        }
    }
}
