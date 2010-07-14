/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.Processors;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.net.channel.ChannelServer;

/**
 *
 * @author Owner
 */
public class ConnectedProcessor {

    public static String getConnected() {
        String connect = "";
        try {
            Map<Integer, Integer> connected = ChannelServer.getInstance(1).getWorldInterface().getConnected();
            StringBuilder conStr = new StringBuilder("Connected Clients: ");
            boolean first = true;
            for (int i : connected.keySet()) {
                if (!first) {
                    conStr.append(", ");
                } else {
                    first = false;
                }
                if (i == 0) {
                    conStr.append("Total: ");
                    conStr.append(connected.get(i));
                } else {
                    conStr.append("Ch");
                    conStr.append(i);
                    conStr.append(": ");
                    conStr.append(connected.get(i));
                }
            }
            connect = conStr.toString();
        } catch (RemoteException e) {
            //  c.getChannelServer().reconnectWorld();
            connect = "ERROR.";
        }
        return connect;
    }

    public static String getOnline(int channel) {
        StringBuilder sb = new StringBuilder("Characters connected to channel ");
        sb.append(channel);
        sb.append(":");
        sb.append(ChannelServer.getInstance(channel).getPlayerStorage().getOnlinePlayers(true));
        return sb.toString();
    }

    public static String getOnline() {
        StringBuilder sb = new StringBuilder();
        for (int channel = 0; channel < ChannelServer.getAllInstances().size(); channel++) {
            sb.append("Characters in channel ");
            sb.append(channel);
            sb.append(":");
            sb.append(ChannelServer.getInstance(channel).getPlayerStorage().getOnlinePlayers(true));
            sb.append("\r\n");
        }
        return sb.toString();
    }
}
