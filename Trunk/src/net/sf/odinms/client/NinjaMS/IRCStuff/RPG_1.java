/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.IRCStuff;

import java.io.IOException;
import net.sf.odinms.server.TimerManager;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

/**
 *
 * @author Owner
 */
public class RPG_1 extends PircBot {
    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */

    private String channel = "#ninjams";
    private static RPG_1 instance = new RPG_1();

    public static RPG_1 getInstance() {
        return instance;
    }

    public RPG_1() {
        try {
            this.setLogin("Ninja");
            this.setName("Hokage");
            this.setAutoNickChange(true);
            this.connect("irc.vbirc.com");
            this.identify("{Janet143<3}");
            this.joinChannel(channel);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (IrcException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisconnect() {
        //reconnect
        this.reconnectIRC();
    }

    public void reconnectIRC() {
        if (!isConnected()) {
            try {
                reconnect();
            } catch (Exception e) {
                e.printStackTrace();
                TimerManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        reconnectIRC();
                    }
                }, 5000);
            }
        }
    }

    @Override
    public void onKick(String channel, String kickerNick, String kickerLogin,
            String kickerHostname, String recipientNick, String reason) {
        //Auto Rejoin if Kicked
        if (recipientNick.equalsIgnoreCase(getNick())) {
            joinChannel(channel);
        }
    }
}
