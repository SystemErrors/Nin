/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.players;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.NinjaMS.Rebirths;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.PlayerCommand;
import net.sf.odinms.client.messages.PlayerCommandDefinition;
import net.sf.odinms.server.constants.Items;

/**
 *
 * @author Admin
 */
public class GamePlayCommands implements PlayerCommand {

    public void execute(final MapleClient c, final MessageCallback mc, String[] splitted) throws Exception {
        MapleCharacter player = c.getPlayer();
        int fee = ((player.getTaoOfSight() / 200) + 1);
        if (splitted[0].equalsIgnoreCase("rebirth")) {
            if(player.getReborns() < 50){
                Rebirths.RebornTimer(player, true);
            } else if(player.haveItem(Items.currencyType.Sight, fee)){
                player.gainItem(Items.currencyType.Sight,  -(fee));
                Rebirths.RebornTimer(player, true);
            } else {
                mc.dropMessage("Seems like you do not have enough Chakra. You need " + fee + " Tao Of Sight");
            }
        } else if (splitted[0].equalsIgnoreCase("rebirthjob")){            
            if(player.haveItem(Items.currencyType.Sight, (fee+ 3))){
                player.gainItem(Items.currencyType.Sight,  -(fee + 3));
                Rebirths.RebornTimer(player, false);
            } else {
                mc.dropMessage("Seems like you do not have enough Chakra. You need " + fee + " Tao Of Sight");
            }
        } else if (splitted[0].equalsIgnoreCase("buynx")) {
            if (player.getTaoOfSight() >= 1) {
                player.addCSPoints(1, 5000);
                player.gainItem(4032016, -1);
                mc.dropMessage("You have gained 5000 NX and lost 1 tao of sight");
            } else {
                mc.dropMessage("Hello poor Fag :)");
            }
        }
    }

    public PlayerCommandDefinition[] getDefinition() {
        return new PlayerCommandDefinition[]{
                    new PlayerCommandDefinition("rebirth", "", "rebirth command"),
                    new PlayerCommandDefinition("rebirthjob", "", "rebirth command with out changing Job"),
                    new PlayerCommandDefinition("buynx", "", "buys 5k nx for 1  tao of sight"),
                    
        };
    }
}
