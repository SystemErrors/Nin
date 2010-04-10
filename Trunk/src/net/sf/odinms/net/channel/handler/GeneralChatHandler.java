/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
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
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class GeneralChatHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        String text = slea.readMapleAsciiString();
        int show = slea.readByte();
        MapleCharacter player = c.getPlayer();
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
                    player.getMap().broadcastMessage(MaplePacketCreator.getChatText(player.getId(), text, player.isJounin() && c.getChannelServer().allowGmWhiteText(), show));
                    if (player.isJounin()) {
                        for (Clones clone : c.getPlayer().getClones()) {
                            player.getMap().broadcastMessage(MaplePacketCreator.getChatText(clone.getClone().getId(), text, c.getChannelServer().allowGmWhiteText() && player.isJounin(), 1));
                        }
                    }
                    break;
            }
            // }
        }
    }

    private void processPermaSay(MapleClient c, String text) {
        MapleCharacter player = c.getPlayer();
        if (player.isHokage()) {
            try {
                String msg = "[Hokage] ";
                msg += text;
                MaplePacket packet = MaplePacketCreator.sendYellowTip(msg);
                ChannelServer.getInstance(c.getChannel()).getWorldInterface().broadcastMessage(c.getPlayer().getName(), packet.getBytes());
            } catch (RemoteException ex) {
                Logger.getLogger(GeneralChatHandler.class.getName()).log(Level.SEVERE, null, ex);
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
                ChannelServer.getInstance(c.getChannel()).getWorldInterface().broadcastMessage(c.getPlayer().getName(), packet.getBytes());
            } catch (RemoteException e) {
                c.getChannelServer().reconnectWorld();
            }
        }
    }

    private void processTextColour(MapleClient c, String text, int show) {
        MapleCharacter player = c.getPlayer();
        MaplePacket pkt = null;
        switch (player.getTextColour()) {            
            case 1:
                pkt = MaplePacketCreator.serverNotice(2, player.getName() + " : " + text);
                break;
            case 2: // buddy chat
                pkt = MaplePacketCreator.multiChat(player.getName(), text, 0);
                break;
            case 3: // party chat
                pkt = MaplePacketCreator.multiChat(player.getName(), text, 1);
                break;
            case 4:
                pkt = MaplePacketCreator.multiChat(player.getName(), text, 2);
                break;
            case 5:
                pkt = MaplePacketCreator.multiChat(player.getName(), text, 3);
                break;
            case 6:
                pkt = MaplePacketCreator.serverNotice(1, player.getName() + " : " + text);
                break;
            case 7:
                pkt = MaplePacketCreator.serverNotice(5, player.getName() + " : " + text);
                break;
            case 8:
                pkt = MaplePacketCreator.serverNotice(6, player.getName() + " : " + text);
                break;
            case 9:
                pkt = MaplePacketCreator.serverNotice(3, 69, player.getName() + " : " + text);
                break;
            case 10:
                pkt = MaplePacketCreator.serverNotice(8, 69, player.getName() + " : " + text);
                break;
            case 11:
                pkt = MaplePacketCreator.sendYellowTip(player.getName() + " : " + text);
                break;
            case 12:
                pkt = MaplePacketCreator.spouseChat(player.getName(), text, 1);
                break;
            case 13:
                pkt = MaplePacketCreator.getChatText(player.getId(), text, true, show);
                break;
            default:
                pkt = MaplePacketCreator.getChatText(player.getId(), text, false, show);
        }
        player.getMap().broadcastMessage(pkt);
        if (player.isJounin()) {
            for (Clones clone : c.getPlayer().getClones()) {
                player.getMap().broadcastMessage(MaplePacketCreator.getChatText(clone.getClone().getId(), text, c.getChannelServer().allowGmWhiteText() && player.isJounin(), 1));
            }
        }
    }
}
