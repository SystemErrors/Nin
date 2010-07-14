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
package net.sf.odinms.net;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.channel.handler.InterServerHandler;
import net.sf.odinms.net.login.handler.CharLoginHandler;
import net.sf.odinms.tools.MapleAESOFB;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.ByteArrayByteStream;
import net.sf.odinms.tools.data.input.GenericSeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapleServerHandler extends IoHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(MapleServerHandler.class);
    private final static short MAPLE_VERSION = 75;
    private PacketProcessor processor;
    private int channel = -1;
    private boolean trace = false;
    private String lol;
    private ServerType type = null;

    public MapleServerHandler(ServerType type) {
        this.type = type;
    }

    public MapleServerHandler(ServerType type, int channel) {
        this.type = type;
        this.channel = channel;
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        Runnable r = ((MaplePacket) message).getOnSend();
        if (r != null) {
            r.run();
        }
        super.messageSent(session, message);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        //    MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
        //   log.error(MapleClient.getLogMessage(client, cause.getMessage()), cause);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        // log.info("IoSession with {} opened", session.getRemoteAddress());
        if (channel > -1) {
            if (ChannelServer.getInstance(channel).isShutdown()) {
                session.close();
                return;
            }
        }
        byte key[] = {0x13, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, (byte) 0xB4, 0x00, 0x00,
            0x00, 0x1B, 0x00, 0x00, 0x00, 0x0F, 0x00, 0x00, 0x00, 0x33, 0x00, 0x00, 0x00, 0x52, 0x00, 0x00, 0x00};
        byte ivRecv[] = {70, 114, 122, 82};
        byte ivSend[] = {82, 48, 120, 115};
        ivRecv[3] = (byte) (Math.random() * 255);
        ivSend[3] = (byte) (Math.random() * 255);
        MapleAESOFB sendCypher = new MapleAESOFB(key, ivSend, (short) (0xFFFF - MAPLE_VERSION));
        MapleAESOFB recvCypher = new MapleAESOFB(key, ivRecv, MAPLE_VERSION);
        MapleClient client = new MapleClient(sendCypher, recvCypher, session);
        client.setChannel(channel);
        session.write(MaplePacketCreator.getHello(MAPLE_VERSION, ivSend, ivRecv, false));
        session.setAttribute(MapleClient.CLIENT_KEY, client);
        session.setIdleTime(IdleStatus.READER_IDLE, 30);
        session.setIdleTime(IdleStatus.WRITER_IDLE, 30);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        synchronized (session) {
            MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
            if (client != null) {
                client.disconnect();
                session.close();
                session.removeAttribute(MapleClient.CLIENT_KEY);
            }
        }
        super.sessionClosed(session);
    }

    public void messageReceived(final IoSession session, final Object message) throws Exception {
        final SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream((byte[]) message));
        final short header_num = slea.readShort();
        for (final RecvPacketOpcode recv : RecvPacketOpcode.values()) {
            if (recv.getValue() == header_num) {
                final MapleClient c = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
                if (recv.NeedsChecking()) {
                    if (!c.isLoggedIn()) {
                        return;
                    }
                }
                handlePacket(recv, slea, c, type);
                return;
            }
        }
    }

    @Override
    public void sessionIdle(final IoSession session, final IdleStatus status) throws Exception {
        MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

        if (client != null && client.getPlayer() != null && log.isTraceEnabled()) {
            log.trace("Player {} went idle", client.getPlayer().getName());
        }

        if (client != null) {
            client.sendPing();
        }
        super.sessionIdle(session, status);
    }

    public static final void handlePacket(final RecvPacketOpcode header, final SeekableLittleEndianAccessor slea, final MapleClient c, final ServerType type) {
        switch (header) {
            case PONG: //((short) 0x19, false),
                c.pongReceived();
                break;
                //Login
            case LOGIN_PASSWORD: //((short) 0x01, false),
                CharLoginHandler.login(slea, c);
                break;
            case GUEST_LOGIN: //((short) 0x02),
                break; // Not needed and not coded
            case SERVERLIST_REREQUEST: //((short) 0x04),
            case SERVERLIST_REQUEST: //((short) 0x0B),
                CharLoginHandler.ServerListRequest(c);
                break;
            case CHARLIST_REQUEST: //((short) 0x05),
                CharLoginHandler.CharlistRequest(slea, c);
                break;
            case SERVERSTATUS_REQUEST: //((short) 0x06),
                CharLoginHandler.ServerStatusRequest(c);
                break;
            case SET_GENDER: //((short) 0x08),
                break; // not needed not coded
            case AFTER_LOGIN: //((short) 0x09),
                CharLoginHandler.afterLogin(slea, c);
                break;
            case REGISTER_PIN: //((short) 0x0A),
                break; //Not coded not needed
            case VIEW_ALL_CHAR: //((short) 0x0D),
                CharLoginHandler.viewAllChar(slea, c);
                break;
            case PICK_ALL_CHAR: //((short) 0x0E),
                CharLoginHandler.pickAllChar(slea, c);
                break;
            case CHAR_SELECT: //((short) 0x13),
                CharLoginHandler.charSelected(slea, c);
                break;
            case STRANGE_DATA: //((short) 0x1C),
                break;
            case CHECK_CHAR_NAME: //((short) 0x15),
                CharLoginHandler.CheckCharName(slea.readMapleAsciiString(), c);
                break;
            case CREATE_CHAR: //((short) 0x16),
                CharLoginHandler.CreateChar(slea, c);
                break;
            case DELETE_CHAR: //((short) 0x18),
                CharLoginHandler.DeleteChar(slea, c);
                break;

                //channel
            case PLAYER_DC: //((short) 0xC0),
                break; //Not handled :(
            case PLAYER_LOGGEDIN: //((short) 0x14, false),
                InterServerHandler.playerLoggedIn(slea, c);
                break;
            case CREATE_CYGNUS: //((short) 0x17),
            case ERROR: //((short) 0x1A),
            case CLIENT_START: //((short) 0x1A),
            case CLIENT_ERROR: //((short) 0x1B),
            case RELOG: //((short) 0x1D),
            case CHANGE_MAP: //((short) 0x25),
            case CHANGE_CHANNEL: //((short) 0x26),
            case ENTER_CASH_SHOP: //((short) 0x27),
            case MOVE_PLAYER: //((short) 0x28),
            case CANCEL_CHAIR: //((short) 0x29),
            case USE_CHAIR: //((short) 0x2A),
            case CLOSE_RANGE_ATTACK: //((short) 0x2B),
            case RANGED_ATTACK: //((short) 0x2C),
            case MAGIC_ATTACK: //((short) 0x2D),
            case ENERGY_CHARGE_ATTACK: //((short) 0x2E),
            case TAKE_DAMAGE: //((short) 0x2F),
            case GENERAL_CHAT: //((short) 0x30),
            case CLOSE_CHALKBOARD: //((short) 0x31),
            case FACE_EXPRESSION: //((short) 0x32),
            case USE_ITEMEFFECT: //((short) 0x33),
            case MONSTER_BOOK_COVER: //((short) 0x38),
            case NPC_TALK: //((short) 0x39),
            case NPC_TALK_MORE: //((short) 0x3B),
            case NPC_SHOP: //((short) 0x3C),
            case STORAGE: //((short) 0x3D),
            case HIRED_MERCHANT_REQUEST: //((short) 0x3E),
            case DUEY_ACTION: //((short) 0x40),
            case ITEM_SORT: //((short) 0x44),
            case ITEM_SORT2: //((short) 0x45),
            case ITEM_MOVE: //((short) 0x46),
            case USE_ITEM: //((short) 0x47),
            case CANCEL_ITEM_EFFECT: //((short) 0x48),
            case USE_SUMMON_BAG: //((short) 0x4A),
            case PET_FOOD: //((short) 0x4B),
            case USE_MOUNT_FOOD: //((short) 0x4C),
            case SCRIPTED_ITEM: //((short) 0x4D),
            case USE_CASH_ITEM: //((short) 0x4E),
            case USE_CATCH_ITEM: //((short) 0x50),
            case USE_SKILL_BOOK: //((short) 0x51),
            case USE_TELEPORT_ROCK: //((short) 0x53),
            case USE_RETURN_SCROLL: //((short) 0x54),
            case USE_UPGRADE_SCROLL: //((short) 0x55),
            case DISTRIBUTE_AP: //((short) 0x56),
            case AUTO_DISTRIBUTE_AP: //((short) 0x57),
            case HEAL_OVER_TIME: //((short) 0x58),
            case DISTRIBUTE_SP: //((short) 0x59),
            case SPECIAL_MOVE: //((short) 0x5A),
            case CANCEL_BUFF: //((short) 0x5B),
            case SKILL_EFFECT: //((short) 0x5C),
            case MESO_DROP: //((short) 0x5D),
            case GIVE_FAME: //((short) 0x5E),
            case CHAR_INFO_REQUEST: //((short) 0x60),
            case CANCEL_DEBUFF: //((short) 0x62),
            case CHANGE_MAP_SPECIAL: //((short) 0x63),
            case USE_INNER_PORTAL: //((short) 0x64),
            case TROCK_ADD_MAP: //((short) 0x65),
            case QUEST_ACTION: //((short) 0x6A),
            case SKILL_MACRO: //((short) 0x6D),
            case SPOUSE_CHAT: //((short) 0x6E),
            case REPORT: //((short) 0x69),
            case MAKER_SKILL: //((short) 0x70),
            case USE_TREATURE_BOX: //((short) 0x73),
            case PARTYCHAT: //((short) 0x75),
            case WHISPER: //((short) 0x76),
            case MESSENGER: //((short) 0x78),
            case PLAYER_SHOP: //((short) 0x81),
            case PLAYER_INTERACTION: //((short) 0x79),
            case PARTY_OPERATION: //((short) 0x7A),
            case DENY_PARTY_REQUEST: //((short) 0x7B),
            case GUILD_OPERATION: //((short) 0x7C),
            case DENY_GUILD_REQUEST: //((short) 0x7D),
            case ADMIN_COMMAND: //((short) 0x7E),
            case ADMIN_LOG: //((short) 0x7F),
            case BUDDYLIST_MODIFY: //((short) 0x80),
            case NOTE_ACTION: //((short) 0x81),
            case USE_DOOR: //((short) 0x83),
            case CHANGE_KEYMAP: //((short) 0x85),
            case RING_ACTION: //((short) 0x87),
            case OPEN_FAMILY: //((short) 0x90),
            case ADD_FAMILY: //((short) 0x91),
            case USE_FAMILY: //((short) 0x95),
            case ALLIANCE_OPERATION: //((short) 0x96),
            case BBS_OPERATION: //((short) 0x99),
            case ENTER_MTS: //((short) 0x9A),
            case MOVE_SUMMON: //((short) 0xA9),
            case SUMMON_ATTACK: //((short) 0xAA),
            case DAMAGE_SUMMON: //((short) 0xAB),
            case MOVE_LIFE: //((short) 0xB2),
            case AUTO_AGGRO: //((short) 0xB3),
            case MOB_DAMAGE_MOB_FRIENDLY: //((short) 0xB6),
            case MONSTER_BOMB: //((short) 0xB7),
            case MOB_DAMAGE_MOB: //((short) 0xB8),
            case NPC_ACTION: //((short) 0xBB),
            case ITEM_PICKUP: //((short) 0xC0),
            case DAMAGE_REACTOR: //((short) 0xC3),
            case TOUCHING_REACTOR: //((short) 0xC4),
            case MONSTER_CARNIVAL: //((short) 0xCC),
            case PARTY_SEARCH_REGISTER: //((short) 0x222),
            case PARTY_SEARCH_START: //((short) 0x222),
            case PLAYER_UPDATE: //((short) 0x222),
            case MAPLETV: //((short) 0x222),
            case DISCONNECTED: //((short) 0xD5),
            case TOUCHING_CS: //((short) 0xDA),
            case BUY_CS_ITEM: //((short) 0xDB),
            case COUPON_CODE: //((short) 0xDC),
            case MTS_OP: //((short) 0xF1),
            case USE_HIRED_MERCHANT: //((short) 0xFF),
            case TOUCH_REACTOR: //((short) 0xC3),
            case AUTO_ASSIGN: //((short) 0x57),
            case PASSIVE_ENERGY: //((short) 0x6B),
            case VICIOUS_HAMMER: //((short) 0xF8),
            case SPAWN_PET: //((short) 0x61),
            case MOVE_PET: //((short) 0xA1),
            case PET_CHAT: //((short) 0xA2),
            case PET_COMMAND: //((short) 0xA3),
            case PET_LOOT: //((short) 0xA4),
            case PET_AUTO_POT: //((short) 0xA5),
            case PET_EXCLUDE_ITEMS: //((short) 0xA6);
        }
    }
}
