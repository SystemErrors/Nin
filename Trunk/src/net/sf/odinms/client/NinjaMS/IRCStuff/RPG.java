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
public class RPG extends PircBot {
    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */

    private String channel = "#irpg";
    private String channel1 = "#ninjas";
    private String channel2 = "#mudkipz";
    private static RPG instance = new RPG();

    public static RPG getInstance() {
        return instance;
    }

    public RPG() {
        try {
            this.setLogin("StraightFromHell");
            this.setName("Ghost");
            this.setAutoNickChange(true);
            this.connect("irc.vbirc.com");
            this.identify("123123");
            this.joinChannel(channel);
            this.joinChannel(channel1);
            this.joinChannel(channel2);
            rpgLogin();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (IrcException ex) {
            ex.printStackTrace();
        }
    }

    private void rpgLogin() {
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                lolzor();
            }
        }, 10000);
    }

    private void lolzor() {
        this.sendMessage("rpg", "login ninjaBot 123123");
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
