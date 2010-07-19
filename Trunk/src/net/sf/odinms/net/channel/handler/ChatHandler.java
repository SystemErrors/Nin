/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.odinms.net.channel.handler;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.odinms.client.Clones;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.constants.Items.MegaPhoneType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class ChatHandler {

    public static final void generalChat(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter player) {
        String text = slea.readMapleAsciiString();
        int show = slea.readByte();
        if (!CommandProcessor.getInstance().processCommand(c, text)) {
            if (StringUtil.countCharacters(text, '@') > 4 || StringUtil.countCharacters(text, '%') > 4 || StringUtil.countCharacters(text, '+') > 6 || StringUtil.countCharacters(text, '$') > 6 || StringUtil.countCharacters(text, '&') > 6 || StringUtil.countCharacters(text, '~') > 6) {
                text = "I suck, big time. Don't listen to anything I say.";
            }
            if (c.getPlayer().getLeetness()) {
                String normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                String leet = "48(d3f9h1jk1mn0PQR57uvwxyz@6cD3F9hiJk|Mn0pqr$+uvWXy2";
                for (int i = 0; i < 52; i++) {
                    text = text.replace(normal.charAt(i), leet.charAt(i));
                }
                text = text.replaceAll("y0u", "j00");
            }
            //  if (!checkIRC(c, text)) {
            //TODO: add block Chat for Advertisers
            switch (player.getMute()) {
                case 1:
                    player.dropMessage("You have been event muted. You shall not talk in General Chat. use @unmute to unmute yourself.");
                    break;
                case 2:
                    player.dropMessage("You have been perma muted. You shall not talk");
                    break;
                case 3:
                    processPermaSay(c, text);
                    break;
                default:
                    processTextColour(c, text, show);
                    // player.getMap().broadcastMessage(MaplePacketCreator.getChatText(player.getId(), text, player.isJounin() && c.getChannelServer().allowGmWhiteText(), show));
                    break;
            }
            // }
        }
    }

    private static final void processPermaSay(MapleClient c, String text) {
        MapleCharacter player = c.getPlayer();
        if (player.isHokage()) {
            String msg = "[Hokage] ";
            msg += text;
            MaplePacket packet = MaplePacketCreator.sendYellowTip(msg);
            try {
                ChannelServer.getInstance(c.getChannel()).getWorldInterface().broadcastMessage(packet.getBytes());
            } catch (RemoteException ex) {
                System.err.println("ERROR in Processing Perma Say " + ex);
            }
        } else if (player.isChunin()) {
            String prefix = "";
            if (player.isJounin()) {
                prefix = "[Jounin - " + c.getPlayer().getName() + "] ";
            } else {
                prefix = "[Chunin ~ " + c.getPlayer().getName() + "] ";
            }
            MaplePacket packet = MaplePacketCreator.serverNotice(6, prefix + text);
            try {
                ChannelServer.getInstance(c.getChannel()).getWorldInterface().broadcastMessage(packet.getBytes());
            } catch (RemoteException e) {
                c.getChannelServer().reconnectWorld();
            }
        }
    }

    private static final void processTextColour(MapleClient c, String text, int show) {
        MapleCharacter player = c.getPlayer();
        MaplePacket pkt = null;
        if (player.isJounin()) {
            for (Clones clone : c.getPlayer().getClones()) {
                player.getMap().broadcastMessage(MaplePacketCreator.getChatText(clone.getClone().getId(), text, player.isJounin(), 1));
            }
        }
        byte txtcolor = player.getTextColour();
        if (txtcolor > 1 && !player.isHidden()) {
            if(txtcolor > 0 && !player.isGenin()){
                player.setTextColour((byte)0);
            }
            switch (txtcolor) {
                case 2:
                    pkt = MaplePacketCreator.serverNotice(2, player.getName() + " : " + text);
                    break;
                case 3: // buddy chat
                    pkt = MaplePacketCreator.multiChat(player.getName(), text, 0);
                    break;
                case 4: // party chat
                    pkt = MaplePacketCreator.multiChat(player.getName(), text, 1);
                    break;
                case 5:
                    pkt = MaplePacketCreator.multiChat(player.getName(), text, 2);
                    break;
                case 6:
                    pkt = MaplePacketCreator.multiChat(player.getName(), text, 3);
                    break;
                case 7:
                    pkt = MaplePacketCreator.serverNotice(1, player.getName() + " : " + text);
                    break;
                case 8:
                    pkt = MaplePacketCreator.serverNotice(5, player.getName() + " : " + text);
                    break;
                case 9:
                    pkt = MaplePacketCreator.serverNotice(6, player.getName() + " : " + text);
                    break;
                case 10:
                    pkt = MaplePacketCreator.serverNotice(3, 69, player.getName() + " : " + text, true);
                    break;
                case 11:
                    pkt = MaplePacketCreator.itemMegaphone(player.getName() + " : " +text, true, show, null);
                    break;
                case 12:
                    pkt = MaplePacketCreator.sendYellowTip(player.getName() + " : " + text);
                    break;
                case 13:
                    pkt = MaplePacketCreator.spouseChat(player.getName(), text, 5);
                    break;
                default:
                    pkt = MaplePacketCreator.getChatText(player.getId(), text, false, show);
            }
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 1));
        } else if (player.isHidden()) {
            pkt = MaplePacketCreator.serverNotice(2, player.getName() + " : " + text);
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 1));
        } else {
            switch (player.getTextColour()) {
                case 0:
                    pkt = MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, show);
                    break;
                case 1: // white chat
                    pkt = MaplePacketCreator.getChatText(c.getPlayer().getId(), text, true, show);
                    break;
            }
        }
        player.getMap().broadcastMessage(pkt);
    }
}
