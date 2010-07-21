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
import java.util.Collection;
import net.sf.odinms.client.Clones;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MapleMessenger;
import net.sf.odinms.net.world.MapleMessengerCharacter;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
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
            if (txtcolor > 0 && !player.isGenin()) {
                player.setTextColour((byte) 0);
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
                    pkt = MaplePacketCreator.itemMegaphone(player.getName() + " : " + text, true, show, null);
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

    public static final void Whisper_Find(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final byte mode = slea.readByte();

        switch (mode) {
            case 5: { // Find
                final String recipient = slea.readMapleAsciiString();
                MapleCharacter player = c.getChannelServer().getPlayerStorage().getCharacterByName(recipient);
                if (player != null) {
                    if (c.getPlayer().canFuck(player)) {
                        c.getSession().write(MaplePacketCreator.getFindReplyWithMap(player.getName(), player.getMap().getId()));
                   } else {
                        c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                    }
                } else { // Not found
                    final Collection<ChannelServer> cservs = ChannelServer.getAllInstances();
                    for (ChannelServer cserv : cservs) {
                        player = cserv.getPlayerStorage().getCharacterByName(recipient);
                        if (player != null) {
                            if (c.getPlayer().canFuck(player)) {
                                c.getSession().write(MaplePacketCreator.getFindReply(player.getName(), (byte) player.getClient().getChannel()));
                            } else {
                                c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                            }
                            return;
                        }
                    }
                    try {
                        if (c.getChannelServer().getWorldInterface().isCharacterInCS(recipient)) {
                            c.getSession().write(MaplePacketCreator.getFindReplyWithCS(player.getName()));
                        } else {
                            c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                        }
                    } catch (RemoteException e) {
                        c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                        c.getChannelServer().reconnectWorld();
                    }
                }
                break;
            }
            case 6: { // Whisper
                final String recipient = slea.readMapleAsciiString();
                final String text = slea.readMapleAsciiString();
                whisper(recipient, text, c);
                break;
            }
            case 0x44: {
                String person = slea.readMapleAsciiString();
                MapleCharacter who = c.getChannelServer().getPlayerStorage().getCharacterByName(person);
                if (who != null) {
                    if (!c.getPlayer().canFuck(who)) {
                    } else if (who.inCS()) {
                        c.getSession().write(MaplePacketCreator.getFindBuddyReplyInCS(who.getName()));
                    } else {
                        c.getSession().write(MaplePacketCreator.getFindBuddyReplyInChannel(who.getName(), who.getMapId()));
                    }
                } else { // not found in channel
                    final Collection<ChannelServer> cservs = ChannelServer.getAllInstances();
                    for (ChannelServer cserv : cservs) {
                        who = cserv.getPlayerStorage().getCharacterByName(person);
                        if (who != null) {
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

    public static final void whisper(String recipient, String text, MapleClient c) {
        if (c.getPlayer().getMute() <= 2) {

            MapleCharacter player = c.getChannelServer().getPlayerStorage().getCharacterByName(recipient);
            if (player != null) {
                player.getClient().getSession().write(MaplePacketCreator.getWhisper(c.getPlayer().getName(), c.getChannel(), text));
                if (c.getPlayer().canFuck(player)) {
                    c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                } else {
                    c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 1));
                }
            } else { // Not found
                final Collection<ChannelServer> cservs = ChannelServer.getAllInstances();
                for (ChannelServer cserv : cservs) {
                    player = cserv.getPlayerStorage().getCharacterByName(recipient);
                    if (player != null) {
                        break;
                    }
                }
                if (player != null) {
                    try {
                        ChannelServer.getInstance(c.getChannel()).getWorldInterface().whisper(c.getPlayer().getName(), player.getName(), c.getChannel(), text);
                        if (!c.getPlayer().isChunin() && player.isChunin()) {
                            c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                        } else {
                            c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 1));
                        }
                    } catch (RemoteException re) {
                        c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                        c.getChannelServer().reconnectWorld();
                    }
                } else {
                    c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                }
            }
        } else {
            c.showMessage(1, "You have been muted permanently. You will be unmuted next server check :].");
        }
    }    

    public static final void Others(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
	final int type = slea.readByte();
	final byte numRecipients = slea.readByte();
	int recipients[] = new int[numRecipients];

	for (byte i = 0; i < numRecipients; i++) {
	    recipients[i] = slea.readInt();
	}
	final String chattext = slea.readMapleAsciiString();

	try {
	    switch (type) {
		case 0:
		    c.getChannelServer().getWorldInterface().buddyChat(recipients, chr.getId(), chr.getName(), chattext);
		    break;
		case 1:
		    c.getChannelServer().getWorldInterface().partyChat(chr.getParty().getId(), chattext, chr.getName());
		    break;
		case 2:
		    c.getChannelServer().getWorldInterface().guildChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
		    break;
		case 3:
		    c.getChannelServer().getWorldInterface().allianceChat(chr.getGuildId(), chr.getName(),chr.getId(), chattext);
		    break;
	    }
	} catch (RemoteException e) {
	    c.getChannelServer().reconnectWorld();
	}
    }
public static final void Messenger(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        String input;
        final WorldChannelInterface wci = ChannelServer.getInstance(c.getChannel()).getWorldInterface();
        MapleMessenger messenger = c.getPlayer().getMessenger();

        switch (slea.readByte()) {
            case 0x00: // open
                if (messenger == null) {
                    int messengerid = slea.readInt();
                    if (messengerid == 0) { // create
                        try {
                            final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
                            messenger = wci.createMessenger(messengerplayer);
                            c.getPlayer().setMessenger(messenger);
                            c.getPlayer().setMessengerPosition(0);
                        } catch (RemoteException e) {
                            c.getChannelServer().reconnectWorld();
                        }
                    } else { // join
                        try {
                            messenger = wci.getMessenger(messengerid);
                            final int position = messenger.getLowestPosition();
                            final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer(), position);
                            if (messenger != null) {
                                if (messenger.getMembers().size() < 3) {
                                    c.getPlayer().setMessenger(messenger);
                                    c.getPlayer().setMessengerPosition(position);
                                    wci.joinMessenger(messenger.getId(), messengerplayer, c.getPlayer().getName(), messengerplayer.getChannel());
                                }
                            }
                        } catch (RemoteException e) {
                            c.getChannelServer().reconnectWorld();
                        }
                    }
                }
                break;
            case 0x02: // exit
                if (messenger != null) {
                    final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
                    try {
                        wci.leaveMessenger(messenger.getId(), messengerplayer);
                    } catch (RemoteException e) {
                        c.getChannelServer().reconnectWorld();
                    }
                    c.getPlayer().setMessenger(null);
                    c.getPlayer().setMessengerPosition(4);
                }
                break;
            case 0x03: // invite
                if (messenger.getMembers().size() < 3) {
                    input = slea.readMapleAsciiString();
                    final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(input);

                    if (target != null) {
                        if (target.getMessenger() == null) {
                            target.getClient().getSession().write(MaplePacketCreator.messengerInvite(c.getPlayer().getName(), messenger.getId()));

                            if (!target.isChunin()) {
                                c.getSession().write(MaplePacketCreator.messengerNote(input, 4, 1));
                            } else {
				c.getSession().write(MaplePacketCreator.messengerNote(input, 4, 0));
			    }
                        } else {
                            c.getSession().write(MaplePacketCreator.messengerChat(c.getPlayer().getName() + " : " + input + " is already using Maple Messenger"));
                        }
                    } else {
                        try {
                            if (wci.isConnected(input)) {
                                wci.messengerInvite(c.getPlayer().getName(), messenger.getId(), input, c.getChannel());
                            } else {
                                c.getSession().write(MaplePacketCreator.messengerNote(input, 4, 0));
                            }
                        } catch (RemoteException e) {
                            c.getChannelServer().reconnectWorld();
                        }
                    }
                }
                break;
            case 0x05: // decline
                final String targeted = slea.readMapleAsciiString();
                final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(targeted);
                if (target != null) { // This channel
                    if (target.getMessenger() != null) {
                        target.getClient().getSession().write(MaplePacketCreator.messengerNote(c.getPlayer().getName(), 5, 0));
                    }
                } else { // Other channel
                    try {
                        if (!c.getPlayer().isChunin()) {
                            wci.declineChat(targeted, c.getPlayer().getName());
                        }
                    } catch (RemoteException e) {
                        c.getChannelServer().reconnectWorld();
                    }
                }
                break;
            case 0x06: // message
                if (messenger != null) {
                    final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
                    input = slea.readMapleAsciiString();
                    try {
                        wci.messengerChat(messenger.getId(), input, messengerplayer.getName());
                    } catch (RemoteException e) {
                        c.getChannelServer().reconnectWorld();
                    }
                }
                break;
        }
    }
}
