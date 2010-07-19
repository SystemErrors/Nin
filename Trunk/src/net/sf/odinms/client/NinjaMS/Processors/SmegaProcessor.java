/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.Processors;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.NinjaMS.IRCStuff.MainIRC;
import net.sf.odinms.client.Status;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.Pair;

/**
 *
 * @author Admin
 */
public class SmegaProcessor {

    public static boolean processMegaphone(final MapleClient c, final String msg) {
        String message = msg.toLowerCase();
        if (msg.length() > 100) {
            c.showMessage("[Anbu] toobad so sad. not over 100 characters. :P");
            c.getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        StringBuilder sb = new StringBuilder();
        if (isIllegalWords(c.getPlayer(), message)) {
            sb.append("<Retard> [I Suck Dick]");
            sb.append(c.getPlayer().getName());
            sb.append(" : I'm a cock Sucker. Ban me GM Bahahaha!");
        } else {
            if (c.getPlayer().getPrefixShit() >= 2) {
                sb.append("[");
                sb.append(c.getPlayer().getLegend());
                sb.append("]");
            }
            if (c.getPlayer().getPrefixShit() == 1 || c.getPlayer().getPrefixShit() == 2) {
                sb.append("<");
                sb.append(Status.getName(c.getPlayer().getGMLevel()));
                sb.append(">");
            }
            sb.append(c.getPlayer().getName());
            sb.append(" : ");
            sb.append(msg);
        }
       c.getChannelServer().getPlayerStorage().broadcastSmegaPacket(MaplePacketCreator.serverNotice(2, sb.toString()));
       MainIRC.getInstance().sendIrcMessage("#Mega : " + sb.toString());
       return true;        
    }

    public static boolean processSmega(final MapleClient c, final String msg, final boolean ear) {
        String message = msg.toLowerCase();
        if (msg.length() > 100) {
            c.showMessage("[Anbu] toobad so sad. not over 100 characters. :P");
            c.getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        StringBuilder sb = new StringBuilder();
        if (isIllegalWords(c.getPlayer(), message)) {
            sb.append("<Retard> [I Suck Dick]");
            sb.append(c.getPlayer().getName());
            sb.append(" : I'm a cock Sucker. Ban me GM Bahahaha!");
        } else {
            if (c.getPlayer().getPrefixShit() >= 2) {
                sb.append("[");
                sb.append(c.getPlayer().getLegend());
                sb.append("]");
            }
            if (c.getPlayer().getPrefixShit() == 1 || c.getPlayer().getPrefixShit() == 2) {
                sb.append("<");
                sb.append(Status.getName(c.getPlayer().getGMLevel()));
                sb.append(">");
            }
            sb.append(c.getPlayer().getName());
            sb.append(" : ");
            sb.append(msg);
        }
        try {
            c.getChannelServer().getWorldInterface().broadcastSmega(MaplePacketCreator.serverNotice(3, c.getChannel(), sb.toString(), ear).getBytes());
            MainIRC.getInstance().sendIrcMessage("#Smega : " + sb.toString());
            return true;
        } catch (RemoteException e) {
            System.out.println("RemoteException occured, super megaphone");
            return false;
        }
    }

    public static boolean processISmega(final MapleClient c, final IItem item, final String msg, final boolean ear) {
        String message = msg.toLowerCase();
        if (msg.length() > 100) {
            c.showMessage("[Anbu] toobad so sad. not over 100 characters. :P");
            c.getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        StringBuilder sb = new StringBuilder();
        if (isIllegalWords(c.getPlayer(), message)) {
            sb.append("<Retard>");
            sb.append(c.getPlayer().getName());
            sb.append(" : I'm a cock Sucker. Ban me GM Bahahaha!");
        } else {
            if (c.getPlayer().getPrefixShit() >= 2) {
                sb.append("[");
                sb.append(c.getPlayer().getLegend());
                sb.append("]");
            }
            if (c.getPlayer().getPrefixShit() == 1 || c.getPlayer().getPrefixShit() == 2) {
                sb.append("<");
                sb.append(Status.getName(c.getPlayer().getGMLevel()));
                sb.append(">");
            }
            sb.append(c.getPlayer().getName());
            sb.append(" : ");
            sb.append(msg);
        }
        try {
            c.getChannelServer().getWorldInterface().broadcastSmega(MaplePacketCreator.itemMegaphone(msg, ear, c.getChannel(), item).getBytes());
            StringBuilder sbb = new StringBuilder();
            sbb.append("#ISmega : ");
            if (item != null) {
                for (Pair<Integer, String> data : MapleItemInformationProvider.getInstance().getAllItems()) {
                    if(data.getLeft() == item.getItemId()){
                        sbb.append(" [");
                        sbb.append(data.getRight());
                        sbb.append("] ");
                    }
                }
            }
            sbb.append(sb.toString());
            MainIRC.getInstance().sendIrcMessage(sbb.toString());
            return true;
        } catch (RemoteException e) {
            System.out.println("RemoteException occured, Item megaphone");
            return false;
        }
    }

    public static boolean processASmega(final MapleClient c, final int itemId, final String line, final boolean ear) {
        if (line.length() > 55) {
            c.showMessage(" The message cannot be over 55 Characters in length");
            c.getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        String text = line;
        if (isIllegalWords(c.getPlayer(), line.toLowerCase())) {
            text = " I'm a DickSucker. BAN ME GM!!!!!!";
        }
        try {
            c.getChannelServer().getWorldInterface().broadcastASmega(MaplePacketCreator.getAvatarMega(c.getPlayer(), c.getChannel(), itemId, text, ear).getBytes());
            MainIRC.getInstance().sendIrcMessage("#Asmega ~ " + c.getPlayer().getName() + " : " + text);
            return true;
        } catch (RemoteException e) {
            System.out.println("RemoteException occured, avatar megaphone");
            return false;

        }
    }

    public static boolean processTripleMegaPhone(final MapleClient c, final List<String> message, final byte numlines, final boolean ear) {
        String tag = "";
        String legend = "";
        final List<String> messages = new LinkedList<String>();
        String msg = "-";
        for (byte i = 0; i < numlines; i++) {
            if (c.getPlayer().getPrefixShit() >= 2) {
                legend = c.getPlayer().getLegend();
                msg = "[" + legend + "]" + message.get(i);
            }
            if (c.getPlayer().getPrefixShit() == 1 || c.getPlayer().getPrefixShit() == 2) {
                tag = Status.getName(c.getPlayer().getGMLevel());
                msg = "<" + tag + ">" + message.get(i);
            }
            if (isIllegalWords(c.getPlayer(), msg)) {
                msg = "<Retard> [I Suck Dick]" + c.getPlayer().getName() + " : I'm a Cock Sucker. Ban me GM bahahaha. I tried to advertise";
            }
            messages.add(msg);
        }

        try {
            c.getChannelServer().getWorldInterface().broadcastSmega(MaplePacketCreator.tripleSmega(messages, ear, c.getChannel()).getBytes());
            for (int i = 1; i < messages.size(); i++) {
                if (messages.get(i) != null) {
                    MainIRC.getInstance().sendIrcMessage(messages.get(i));
                }
            }
            return true;
        } catch (RemoteException e) {
            System.out.println("RemoteException occured, triple megaphone");
            return false;
        }
    }

    public static final boolean isIllegalWords(MapleCharacter player, String text) {
        String origitext = text;
        text = text.replaceAll(" ", "").toLowerCase();
        // server sucks
        String[] bannedcomments = {"isserversu", "ninjamssu", "ninjastorysu", "isserverisgay", "fuckthisserv", "ispssu"};
        for (int i = 0; i < bannedcomments.length; i++) {
            if (text.contains(bannedcomments[i])) {
                if (player.getReborns() < 10) {
                    AutobanManager.getInstance().autoban(player.getClient(), "Trying to comment on the server sucking (Text: " + origitext + ")");
                }
                return true;
            }
        }
        // advertisements of maplestory servers...
        String[] advertisingarray = {".com", ".net", ".info", ".org", ".tk", ".weebly", ".freewebs", ".co.cc"};
        String[] forgivencontents = {"story.org", "sydneyms", "ninjams", "wagga", ".org/vote", "google", "tinypic", "mapletip"};
        for (int i = 0; i < advertisingarray.length; i++) {
            String[] subarray = {"story", "ms"};
            for (int p = 0; p < subarray.length; p++) {
                if (text.toLowerCase().contains(subarray[p] + advertisingarray[i])) {
                    boolean banhammer = true;
                    for (int z = 0; z < forgivencontents.length; z++) {
                        if (text.contains(forgivencontents[z])) {
                            banhammer = false;
                            return false;
                        }
                    }
                    if (banhammer) {
                        if (player.getReborns() < 10) {
                            AutobanManager.getInstance().autoban(player.getClient(), "Advertisement of other server (Text: " + origitext + ")");
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }



}
