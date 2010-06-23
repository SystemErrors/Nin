/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.net.channel.OliveroMatic;

import java.util.List;
import net.sf.odinms.client.NinjaMS.BashBot;
import net.sf.odinms.client.NinjaMS.IRCStuff.MainIRC;
import net.sf.odinms.client.NinjaMS.MLIABot;
import net.sf.odinms.client.NinjaMS.MapleFML;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.MaplePacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Owner
 */
public class AutomagicShit {

    private static AutomagicShit instance;
    private static final Logger log = LoggerFactory.getLogger(AutomagicShit.class);

    public static AutomagicShit getInstance() {
        if (instance == null) {
            instance = new AutomagicShit();
        }
        return instance;
    }

    public void start() {
      //  startFML();
        startVoteMsg();
        RankingWorker.getInstance().start();
    }

    public void startFML() {
        broadcastFMLMessage();
        scheduleFML();
    }

    public void startVoteMsg() {
        broadcastVoteMsg();
        scheduleVoteMsg();
    }

    public void scheduleFML() {
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                startFML();
            }
        }, 10 * 60 * 1000); // 10 min once
    }

    public void scheduleVoteMsg() {
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                startVoteMsg();
            }
        }, 15 * 60 * 1000); // 15 min once
    }

    public void broadcastFMLMessage() {
        int random = (int) Math.floor(Math.random() * 3);
        if (random == 3) {
            sendBashQuote();
        } else if (random == 1) {
            // sendMLIA();
        } else {
            sendFML();
        }
    }

    public void sendBashQuote() {
        MainIRC.getInstance().sendMessage("Sunny", "#BASH : 0");
        for (ChannelServer lol : ChannelServer.getAllInstances()) {
            MainIRC.getInstance().sendMessage("Sunny", "#BASH : 1 - "+ lol.getChannel());
            List<String> gay = BashBot.getQuotes();
            MainIRC.getInstance().sendMessage("Sunny", "#BASH : 2- Gay");
            lol.broadcastPacket(MaplePacketCreator.sendYellowTip("---------------------------------------Random bash.org Quote---------------------------------------"));
            for (String i : gay) {
                lol.broadcastPacket(MaplePacketCreator.sendYellowTip(i));
            }
            lol.broadcastPacket(MaplePacketCreator.sendYellowTip("---------------------------------------Random bash.org Quote---------------------------------------"));
            if (lol.getChannel() == 2) {
                for (String i : gay) {
                    MainIRC.getInstance().sendIrcMessage("#BASH : " + i);
                }
            }
        }


    }

    public void sendMLIA() {
        for (ChannelServer lol : ChannelServer.getAllInstances()) {
            lol.broadcastPacket(MaplePacketCreator.sendYellowTip(MLIABot.findMLIA()));
        }
        MainIRC.getInstance().sendIrcMessage("#MLIA : " + MLIABot.findMLIA());
    }

    public void sendFML() {
        for (ChannelServer lol : ChannelServer.getAllInstances()) {
            lol.broadcastPacket(MaplePacketCreator.sendYellowTip(MapleFML.getFML()));
        }
        int lol = (int) (Math.random() * 3);
    /*    if (lol < 1) {
            MainIRC.getInstance().sendGlobalMessage("#FML : " + MapleFML.getFML());
        } else {*/
            MainIRC.getInstance().sendMessage("#ninjashelp","#FML : " + MapleFML.getFML());
        //}
    }

    public void broadcastVoteMsg() {
        for (ChannelServer lol : ChannelServer.getAllInstances()) {
            lol.broadcastPacket(MaplePacketCreator.serverNotice(6, "[Vote Reminder] Please do not forget to vote for us every 12 hrs. There are rewards for voting which you can claim from Charles at henesys. http://ninjams,org/vote"));
        }
    }
}

