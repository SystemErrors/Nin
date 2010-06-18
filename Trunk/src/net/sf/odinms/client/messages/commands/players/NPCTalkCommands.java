package net.sf.odinms.client.messages.commands.players;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.PlayerCommand;
import net.sf.odinms.client.messages.PlayerCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.scripting.npc.NPCScriptManager;

public class NPCTalkCommands implements PlayerCommand {

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        splitted[0] = splitted[0].toLowerCase();
        if (splitted[0].equalsIgnoreCase("ninjaglare")) {
            NPCScriptManager.getInstance().start(c, 1103005);
        } else if (splitted[0].equalsIgnoreCase("scroll")) {
            NPCScriptManager.getInstance().start(c, 2022002);
        } else if (splitted[0].equalsIgnoreCase("shop")) {
            NPCScriptManager.getInstance().start(c, 1032002);
        } else if (splitted[0].equalsIgnoreCase("guide")) {
            NPCScriptManager.getInstance().start(c, 9001000);
        } else if (splitted[0].equalsIgnoreCase("dispose")) {
            NPCScriptManager.getInstance().dispose(c);
            mc.dropMessage("You should now be able to talk to the NPCs");
        } else {
            mc.dropMessage(splitted[0] + " is not a valid command.");
        }
    }

    @Override
    public PlayerCommandDefinition[] getDefinition() {
        return new PlayerCommandDefinition[]{
                    new PlayerCommandDefinition("ninjaglare", "<ign>", "Shows Stats of the person"),
                    new PlayerCommandDefinition("scroll", "", "opens Scroll shop of NinjaMS"),
                    new PlayerCommandDefinition("shop", "", "Opens the All in one shop NPC"),
                    new PlayerCommandDefinition("guide", "", "opens guide NPC"),
                    new PlayerCommandDefinition("dispose", "", "Use this if the NPCs wont talk to you"),

        };
    }
}
