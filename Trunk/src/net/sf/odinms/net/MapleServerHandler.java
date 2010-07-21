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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.CashShop.handler.CashShopOperation;
import net.sf.odinms.net.channel.handler.AdminCommandHandler;
import net.sf.odinms.net.channel.handler.AllianceHandler;
import net.sf.odinms.net.channel.handler.BBSHandler;
import net.sf.odinms.net.channel.handler.BuddyListHandler;
import net.sf.odinms.net.channel.handler.ChairHandler;
import net.sf.odinms.net.channel.handler.ChatHandler;
import net.sf.odinms.net.channel.handler.DamageHandler;
import net.sf.odinms.net.channel.handler.DueyHandler;
import net.sf.odinms.net.channel.handler.FamilyHandler;
import net.sf.odinms.net.channel.handler.GuildHandler;
import net.sf.odinms.net.channel.handler.HiredMerchantHandler;
import net.sf.odinms.net.channel.handler.InterServerHandler;
import net.sf.odinms.net.channel.handler.InventoryHandler;
import net.sf.odinms.net.channel.handler.ItemMakerHandler;
import net.sf.odinms.net.channel.handler.MobHandler;
import net.sf.odinms.net.channel.handler.MonsterCarnivalHandler;
import net.sf.odinms.net.channel.handler.NPCHandler;
import net.sf.odinms.net.channel.handler.NotProcessedPacketsHandler;
import net.sf.odinms.net.channel.handler.PartyHandler;
import net.sf.odinms.net.channel.handler.PetHandler;
import net.sf.odinms.net.channel.handler.PlayerHandler;
import net.sf.odinms.net.channel.handler.PlayerInteractionHandler;
import net.sf.odinms.net.channel.handler.PlayersHandler;
import net.sf.odinms.net.channel.handler.StatsHandler;
import net.sf.odinms.net.channel.handler.SummonHandler;
import net.sf.odinms.net.channel.handler.UseItemsHandler;
import net.sf.odinms.net.login.handler.CharLoginHandler;
import net.sf.odinms.tools.MapleAESOFB;
import net.sf.odinms.tools.Packets.LoginPacket;
import net.sf.odinms.tools.Packets.MTSCSPacket;
import net.sf.odinms.tools.Pair;
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
    private int channel = -1;
    private ServerType type = null;
    private final List<String> BlockedIP = new ArrayList();
    private final Map<String, Pair<Long, Byte>> tracker = new ConcurrentHashMap<String, Pair<Long, Byte>>();

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
        final String address = session.getRemoteAddress().toString().split(":")[0];

        if (BlockedIP.contains(address)) {
            session.close();
            return;
        }
        final Pair<Long, Byte> track = tracker.get(address);

        byte count;
        if (track == null) {
            count = 1;
        } else {
            count = track.right;

            final long difference = System.currentTimeMillis() - track.left;
            if (difference < 2000) { // Less than 2 sec
                count++;
            } else if (difference > 20000) { // Over 20 sec
                count = 1;
            }
            if (count >= 10) {
                BlockedIP.add(address);
                tracker.remove(address); // Cleanup
                session.close();
                return;
            }
        }
        tracker.put(address, new Pair(System.currentTimeMillis(), count));
        // End of IP checking.

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
        session.write(LoginPacket.getHello(MAPLE_VERSION, ivSend, ivRecv));
        session.setAttribute(MapleClient.CLIENT_KEY, client);
        session.setIdleTime(IdleStatus.READER_IDLE, 30);
        session.setIdleTime(IdleStatus.WRITER_IDLE, 30);


    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        synchronized (session) {
            MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
            if (client != null) {
                client.disconnect(true, false);
                session.close();
                session.removeAttribute(MapleClient.CLIENT_KEY);
            }
        }
        super.sessionClosed(session);
    }

    @Override
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
                NotProcessedPacketsHandler.strangeData(c);
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
                NotProcessedPacketsHandler.player_DC(c);
                break;
            case PLAYER_LOGGEDIN: //((short) 0x14, false),
                final int playerid = slea.readInt();
                if (type == ServerType.CHANNEL) {
                    InterServerHandler.Loggedin(playerid, c);
                } else {
                    CashShopOperation.EnterCS(playerid, c);
                }
                break;
            case CREATE_CYGNUS: //((short) 0x17),
                NotProcessedPacketsHandler.createCygnus(c);
                break;
            case CHANGE_MAP:
                if (type == ServerType.CHANNEL) {
                    PlayerHandler.ChangeMap(slea, c, c.getPlayer());
                } else {
                    CashShopOperation.LeaveCS(slea, c, c.getPlayer());
                }
                break;
            case CHANGE_CHANNEL: //((short) 0x26),
                InterServerHandler.ChangeChannel((byte) (slea.readByte() + 1), c, c.getPlayer());
                break;
            case ENTER_CASH_SHOP: //((short) 0x27),
                InterServerHandler.EnterCS(slea, c, c.getPlayer());
                break;
            case MOVE_PLAYER: //((short) 0x28),
                PlayerHandler.MovePlayer(slea, c, c.getPlayer());
                break;
            case CANCEL_CHAIR: //((short) 0x29),
                ChairHandler.CancelChair(slea.readShort(), c, c.getPlayer());
                break;
            case USE_CHAIR: //((short) 0x2A),
                ChairHandler.UseChair(slea.readInt(), c, c.getPlayer());
                break;
            case CLOSE_RANGE_ATTACK: //((short) 0x2B),
                DamageHandler.closeRangeAttack(slea, c, c.getPlayer());
                break;
            case RANGED_ATTACK: //((short) 0x2C),
                DamageHandler.rangedAttack(slea, c, c.getPlayer());
                break;
            case MAGIC_ATTACK: //((short) 0x2D),
                DamageHandler.magicDamage(slea, c, c.getPlayer());
                break;
            case ENERGY_CHARGE_ATTACK: //((short) 0x2E), //PASSIVE_ENERGY
                NotProcessedPacketsHandler.EnergyChargeAttack();
                break;
            case TAKE_DAMAGE: //((short) 0x2F),
                DamageHandler.TakeDamage(slea, c, c.getPlayer());
                break;
            case GENERAL_CHAT: //((short) 0x30),
                ChatHandler.generalChat(slea, c, c.getPlayer());
                break;
            case CLOSE_CHALKBOARD: //((short) 0x31),
                c.getPlayer().setChalkboard(null);
                break;
            case FACE_EXPRESSION: //((short) 0x32),
                PlayerHandler.ChangeEmotion(slea.readInt(), c.getPlayer());
                break;
            case USE_ITEMEFFECT: //((short) 0x33),
                PlayerHandler.UseItemEffect(slea.readInt(), c, c.getPlayer());
                break;
            case MONSTER_BOOK_COVER: //((short) 0x38),
                PlayerHandler.ChangeMonsterBookCover(slea.readInt(), c, c.getPlayer());
                break;
            case NPC_TALK: //((short) 0x39),
                NPCHandler.NPCTalk(slea, c, c.getPlayer());
                break;
            case NPC_TALK_MORE: //((short) 0x3B),
                NPCHandler.NPCMoreTalk(slea, c);
                break;
            case NPC_SHOP: //((short) 0x3C),
                NPCHandler.NPCShop(slea, c, c.getPlayer());
                break;
            case STORAGE: //((short) 0x3D),
                NPCHandler.Storage(slea, c, c.getPlayer());
                break;
            case HIRED_MERCHANT_REQUEST: //((short) 0x3E),
                HiredMerchantHandler.UseHiredMerchant(slea, c);
                break;
            case DUEY_ACTION: //((short) 0x40),
                DueyHandler.DueyOperation(slea, c);
                break;
            case ITEM_SORT: //((short) 0x44),
                InventoryHandler.itemSort(slea, c);
                break;
            case ITEM_SORT2: //((short) 0x45),
                InventoryHandler.itemSort2(slea, c);
                break;
            case ITEM_MOVE: //((short) 0x46),
                InventoryHandler.itemMove(slea, c);
                break;
            case USE_ITEM: //((short) 0x47),
                UseItemsHandler.UseItem(slea, c, c.getPlayer());
                break;
            case CANCEL_ITEM_EFFECT: //((short) 0x48),
                PlayerHandler.CancelItemEffect(slea.readInt(), c.getPlayer());
                break;
            case USE_SUMMON_BAG: //((short) 0x4A),
                UseItemsHandler.UseSummonBag(slea, c, c.getPlayer());
                break;
            case PET_FOOD: //((short) 0x4B),
                PetHandler.PetFood(slea, c, c.getPlayer());
                break;
            case USE_MOUNT_FOOD: //((short) 0x4C),
                UseItemsHandler.UseMountFood(slea, c, c.getPlayer());
                break;
            case SCRIPTED_ITEM: //((short) 0x4D),
                UseItemsHandler.UseScriptedNPCItem(slea, c, c.getPlayer());
                break;
            case USE_CASH_ITEM: //((short) 0x4E),
                UseItemsHandler.UseCashItem(slea, c);
                break;
            case USE_CATCH_ITEM: //((short) 0x50),
                UseItemsHandler.UseCatchItem(slea, c, c.getPlayer());
                break;
            case USE_SKILL_BOOK: //((short) 0x51),
                UseItemsHandler.UseSkillBook(slea, c, c.getPlayer());
                break;
            case USE_TELEPORT_ROCK: //((short) 0x53),
                NotProcessedPacketsHandler.useTelePortRock();
                break;
            case USE_RETURN_SCROLL: //((short) 0x54),
                UseItemsHandler.UseReturnScroll(slea, c, c.getPlayer());
                break;
            case USE_UPGRADE_SCROLL: //((short) 0x55),
                UseItemsHandler.UseUpgradeScroll(slea, c, c.getPlayer());
                break;
            case DISTRIBUTE_AP: //((short) 0x56),
                StatsHandler.DistributeAP(slea, c, c.getPlayer());
                break;
            case AUTO_DISTRIBUTE_AP: //((short) 0x57),
                StatsHandler.AutoAssignAP(slea, c, c.getPlayer());
                break;
            case HEAL_OVER_TIME: //((short) 0x58),
                PlayerHandler.Heal(slea, c.getPlayer());
                break;
            case DISTRIBUTE_SP: //((short) 0x59),
                StatsHandler.DistributeSP(slea.readInt(), c, c.getPlayer());
                break;
            case SPECIAL_MOVE: //((short) 0x5A),
                PlayerHandler.SpecialMove(slea, c, c.getPlayer());
                break;
            case CANCEL_BUFF: //((short) 0x5B),
                PlayerHandler.CancelBuffHandler(slea.readInt(), c.getPlayer());
                break;
            case SKILL_EFFECT: //((short) 0x5C),
                PlayerHandler.SkillEffect(slea, c.getPlayer());
                break;
            case MESO_DROP: //((short) 0x5D),
                slea.skip(4);
                PlayerHandler.DropMeso(slea.readInt(), c.getPlayer());
                break;
            case GIVE_FAME: //((short) 0x5E),
                PlayersHandler.GiveFame(slea, c, c.getPlayer());
                break;
            case CHAR_INFO_REQUEST: //((short) 0x60),
                slea.skip(4);
                PlayerHandler.CharInfoRequest(slea.readInt(), c, c.getPlayer());
                break;
            case CANCEL_DEBUFF:
                PlayerHandler.cancelDebuff(c);
                break;
            case CHANGE_MAP_SPECIAL:
                slea.skip(1);
                PlayerHandler.ChangeMapSpecial(slea.readMapleAsciiString(), c, c.getPlayer());
                break;
            case USE_INNER_PORTAL:
                slea.skip(1);
                PlayerHandler.InnerPortal(slea, c, c.getPlayer());
                break;
            case TROCK_ADD_MAP: //((short) 0x65),
                PlayerHandler.TrockAddMap(slea, c, c.getPlayer());
                break;
            case REPORT: //((short) 0x69),
                PlayerHandler.report(slea, c);
                break;
            case QUEST_ACTION: //((short) 0x6A),
                NPCHandler.QuestAction(slea, c, c.getPlayer());
                break;
            case SKILL_MACRO: //((short) 0x6D),
                PlayerHandler.ChangeSkillMacro(slea, c.getPlayer());
                break;
            case SPOUSE_CHAT: //((short) 0x6E),
                NotProcessedPacketsHandler.spouseChat();
                break;
            case USE_REWARD_ITEM:
                UseItemsHandler.UseRewardItem(slea, c, c.getPlayer());
                break;
            case MAKER_SKILL: //((short) 0x70),
                ItemMakerHandler.ItemMaker(slea, c);
                break;
            case USE_TREATURE_BOX: //((short) 0x73),
                UseItemsHandler.UseTreasureChest(slea, c, c.getPlayer());
                break;
            case PARTYCHAT: //((short) 0x75),
                ChatHandler.Others(slea, c, c.getPlayer());
                break;
            case WHISPER: //((short) 0x76),
                ChatHandler.Whisper_Find(slea, c);
                break;
            case MESSENGER: //((short) 0x78),
                ChatHandler.Messenger(slea, c);
                break;
            case PLAYER_INTERACTION: //((short) 0x79),
                PlayerInteractionHandler.PlayerInteraction(slea, c, c.getPlayer());
                break;
            case PARTY_OPERATION: //((short) 0x7A),
                PartyHandler.PartyOperatopn(slea, c);
                break;
            case DENY_PARTY_REQUEST: //((short) 0x7B),
                PartyHandler.DenyPartyRequest(slea, c);
                break;
            case GUILD_OPERATION: //((short) 0x7C),
                GuildHandler.GuildOperation(slea, c);
                break;
            case DENY_GUILD_REQUEST: //((short) 0x7D),
                slea.skip(1);
                GuildHandler.DenyGuildRequest(slea.readMapleAsciiString(), c);
                break;
            case ADMIN_COMMAND: //((short) 0x7E),
                AdminCommandHandler.handleCommand(slea, c);
                break;
            case ADMIN_LOG: //((short) 0x7F),
                NotProcessedPacketsHandler.adminLog();
                break;
            case BUDDYLIST_MODIFY: //((short) 0x80),
                BuddyListHandler.BuddyOperation(slea, c);
                break;
            case NOTE_ACTION: //((short) 0x81),
                PlayersHandler.Note(slea, c.getPlayer());
                break;
            case USE_DOOR: //((short) 0x83),
                PlayersHandler.UseDoor(slea, c.getPlayer());
                break;
            case CHANGE_KEYMAP: //((short) 0x85),
                PlayerHandler.ChangeKeymap(slea, c.getPlayer());
                break;
            case RING_ACTION: //((short) 0x87),
                PlayerHandler.RingAction(slea, c);
                break;
            case OPEN_FAMILY: //((short) 0x90),
                NotProcessedPacketsHandler.openFamily();
                break;
            case ADD_FAMILY: //((short) 0x91),
                FamilyHandler.addFamily(slea, c);
                break;
            case USE_FAMILY: //((short) 0x95),
                FamilyHandler.use_Family(slea, c);
                break;
            case ALLIANCE_OPERATION: //((short) 0x96),
                AllianceHandler.AllianceOperatopn(slea, c);
                break;
            case BBS_OPERATION: //((short) 0x99),
                BBSHandler.BBSOperatopn(slea, c);
                break;
            case ENTER_MTS: //((short) 0x9A),
                InterServerHandler.EnterMTS(c);
                break;
            case PET_TALK:// = 0x9B
                NotProcessedPacketsHandler.pet_Talk();
                break;
            case MOVE_SUMMON: //((short) 0xA9),
                SummonHandler.MoveSummon(slea, c.getPlayer());
                break;
            case SUMMON_ATTACK://((short) 0xAA),
                SummonHandler.SummonAttack(slea, c, c.getPlayer());
                break;
            case DAMAGE_SUMMON: //((short) 0xAB),
                slea.skip(4);
                SummonHandler.DamageSummon(slea, c.getPlayer());
                break;
            case MOVE_LIFE:
                MobHandler.MoveMonster(slea, c, c.getPlayer());
                break;
            case AUTO_AGGRO: //((short) 0xB3),
                MobHandler.AutoAggro(slea.readInt(), c.getPlayer());
                break;
            case MOB_DAMAGE_MOB_FRIENDLY: //((short) 0xB6),
                MobHandler.FriendlyDamage(slea, c.getPlayer());
                break;
            case MONSTER_BOMB: //((short) 0xB7),
                MobHandler.MonsterBomb(slea.readInt(), c.getPlayer());
                break;
            case MOB_DAMAGE_MOB: //((short) 0xB8),
                MobHandler.mobDamageMob(slea, c);
                break;
            case NPC_ACTION: //((short) 0xBB),
                NPCHandler.NPCAnimation(slea, c);
                break;
            case ITEM_PICKUP: //((short) 0xC0),
                InventoryHandler.Pickup_Player(slea, c, c.getPlayer());
                break;
            case DAMAGE_REACTOR: //((short) 0xC3),
                PlayersHandler.HitReactor(slea, c);
                break;
            case TOUCHING_REACTOR: //((short) 0xC4),
                PlayersHandler.touchReactor(slea, c);
                break;
            case MONSTER_CARNIVAL: //((short) 0xD0),
                MonsterCarnivalHandler.MonsterCarnival(slea, c);
                break;
            case PARTY_SEARCH_REGISTER: //((short) 0xD2),
                PartyHandler.partySearchRegister(slea, c);
                break;
            case PARTY_SEARCH_START: //((short) 0xD4),
                PartyHandler.partySearchStart(slea, c);
                break;
            case PLAYER_UPDATE: //((short) 0xD5),
            case MAPLETV: //((short) 0x222),
                break;
            case TOUCHING_CS: //((short) 0xDA),
                c.getSession().write(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
                break;
            case BUY_CS_ITEM: //((short) 0xDB),
                CashShopOperation.buyCashItem(slea, c);
                break;
            case COUPON_CODE: //((short) 0xDC),
                NotProcessedPacketsHandler.couponCode();
                break;
            case MTS_OP: //((short) 0xF1),
                NotProcessedPacketsHandler.mtsOp();
                break;
            case USE_HIRED_MERCHANT: //((short) 0xFF),
                HiredMerchantHandler.UseHiredMerchant(slea, c);
                break;
            case TOUCH_REACTOR: //((short) 0xC3),
                PlayersHandler.touchReactor(slea, c);
                break;
            case AUTO_ASSIGN: //((short) 0x57),
                StatsHandler.AutoAssignAP(slea, c, c.getPlayer());
                break;
            case SPAWN_PET: //((short) 0x61),
                PetHandler.SpawnPet(slea, c, c.getPlayer());
                break;
            case MOVE_PET: //((short) 0xA1),
                PetHandler.MovePet(slea, c.getPlayer());
                break;
            case PET_CHAT: //((short) 0xA2),
                PetHandler.PetChat(slea.readInt(), slea.readShort(), slea.readMapleAsciiString(), c.getPlayer());
                break;
            case PET_COMMAND: //((short) 0xA3),
                PetHandler.PetCommand(slea, c, c.getPlayer());
                break;
            case PET_LOOT: //((short) 0xA4),
                InventoryHandler.Pickup_Pet(slea, c, c.getPlayer());
                break;
            case PET_AUTO_POT: //((short) 0xA5),
                PetHandler.Pet_AutoPotion(slea, c, c.getPlayer());
                break;
            case PET_EXCLUDE_ITEMS: //((short) 0xA6);
                PetHandler.itemExclude(slea, c, c.getPlayer());
                break;
            case VICIOUS_HAMMER: //((short) 0xF8),
        }
    }
}
