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

public enum RecvPacketOpcode {

    PONG((short) 0x19, false),
    // Login
    LOGIN_PASSWORD((short) 0x01, false),
    GUEST_LOGIN((short) 0x02),
    SERVERLIST_REREQUEST((short) 0x04),
    CHARLIST_REQUEST((short) 0x05),
    SERVERSTATUS_REQUEST((short) 0x06),
    SET_GENDER((short) 0x08),
    AFTER_LOGIN((short) 0x09),
    REGISTER_PIN((short) 0x0A),
    SERVERLIST_REQUEST((short) 0x0B),
    VIEW_ALL_CHAR((short) 0x0D),
    PICK_ALL_CHAR((short) 0x0E),
    CHAR_SELECT((short) 0x13),
    STRANGE_DATA((short) 0x1C),
    CHECK_CHAR_NAME((short) 0x15),
    CREATE_CHAR((short) 0x16),
    DELETE_CHAR((short) 0x18),
    //channel
    PLAYER_DC((short) 0xC0),
    PLAYER_LOGGEDIN((short) 0x14, false),
    CREATE_CYGNUS((short) 0x17),
    ERROR((short) 0x1A),
    CLIENT_START((short) 0x1A),
    CLIENT_ERROR((short) 0x1B),
    RELOG((short) 0x1D),
    CHANGE_MAP((short) 0x25),
    CHANGE_CHANNEL((short) 0x26),
    ENTER_CASH_SHOP((short) 0x27),
    MOVE_PLAYER((short) 0x28),
    CANCEL_CHAIR((short) 0x29),
    USE_CHAIR((short) 0x2A),
    CLOSE_RANGE_ATTACK((short) 0x2B),
    RANGED_ATTACK((short) 0x2C),
    MAGIC_ATTACK((short) 0x2D),
    ENERGY_CHARGE_ATTACK((short) 0x2E),
    TAKE_DAMAGE((short) 0x2F),
    GENERAL_CHAT((short) 0x30),
    CLOSE_CHALKBOARD((short) 0x31),
    FACE_EXPRESSION((short) 0x32),
    USE_ITEMEFFECT((short) 0x33),
    MONSTER_BOOK_COVER((short) 0x38),
    NPC_TALK((short) 0x39),
    NPC_TALK_MORE((short) 0x3B),
    NPC_SHOP((short) 0x3C),
    STORAGE((short) 0x3D),
    HIRED_MERCHANT_REQUEST((short) 0x3E),
    DUEY_ACTION((short) 0x40),
    ITEM_SORT((short) 0x44),
    ITEM_SORT2((short) 0x45),
    ITEM_MOVE((short) 0x46),
    USE_ITEM((short) 0x47),
    CANCEL_ITEM_EFFECT((short) 0x48),
    USE_SUMMON_BAG((short) 0x4A),
    PET_FOOD((short) 0x4B),
    USE_MOUNT_FOOD((short) 0x4C),
    SCRIPTED_ITEM((short) 0x4D),
    USE_CASH_ITEM((short) 0x4E),
    USE_CATCH_ITEM((short) 0x50),
    USE_SKILL_BOOK((short) 0x51),
    USE_TELEPORT_ROCK((short) 0x53),
    USE_RETURN_SCROLL((short) 0x54),
    USE_UPGRADE_SCROLL((short) 0x55),
    DISTRIBUTE_AP((short) 0x56),
    AUTO_DISTRIBUTE_AP((short) 0x57),
    HEAL_OVER_TIME((short) 0x58),
    DISTRIBUTE_SP((short) 0x59),
    SPECIAL_MOVE((short) 0x5A),
    CANCEL_BUFF((short) 0x5B),
    SKILL_EFFECT((short) 0x5C),
    MESO_DROP((short) 0x5D),
    GIVE_FAME((short) 0x5E),
    CHAR_INFO_REQUEST((short) 0x60),
    CANCEL_DEBUFF((short) 0x62),
    CHANGE_MAP_SPECIAL((short) 0x63),
    USE_INNER_PORTAL((short) 0x64),
    TROCK_ADD_MAP((short) 0x65),
    QUEST_ACTION((short) 0x6A),
    SKILL_MACRO((short) 0x6D),
    SPOUSE_CHAT((short) 0x6E),
    REPORT((short) 0x69),
    MAKER_SKILL((short) 0x70),
    USE_TREATURE_BOX((short) 0x73),
    PARTYCHAT((short) 0x75),
    WHISPER((short) 0x76),
    MESSENGER((short) 0x78),
    PLAYER_SHOP((short) 0x81),
    PLAYER_INTERACTION((short) 0x79),
    PARTY_OPERATION((short) 0x7A),
    DENY_PARTY_REQUEST((short) 0x7B),
    GUILD_OPERATION((short) 0x7C),
    DENY_GUILD_REQUEST((short) 0x7D),
    ADMIN_COMMAND((short) 0x7E),
    ADMIN_LOG((short) 0x7F),
    BUDDYLIST_MODIFY((short) 0x80),
    NOTE_ACTION((short) 0x81),
    USE_DOOR((short) 0x83),
    CHANGE_KEYMAP((short) 0x85),
    RING_ACTION((short) 0x87),
    OPEN_FAMILY((short) 0x90),
    ADD_FAMILY((short) 0x91),
    USE_FAMILY((short) 0x95),
    ALLIANCE_OPERATION((short) 0x96),
    BBS_OPERATION((short) 0x99),
    ENTER_MTS((short) 0x9A),
    MOVE_SUMMON((short) 0xA9),
    SUMMON_ATTACK((short) 0xAA),
    DAMAGE_SUMMON((short) 0xAB),
    MOVE_LIFE((short) 0xB2),
    AUTO_AGGRO((short) 0xB3),
    MOB_DAMAGE_MOB_FRIENDLY((short) 0xB6),
    MONSTER_BOMB((short) 0xB7),
    MOB_DAMAGE_MOB((short) 0xB8),
    NPC_ACTION((short) 0xBB),
    ITEM_PICKUP((short) 0xC0),
    DAMAGE_REACTOR((short) 0xC3),
    TOUCHING_REACTOR((short) 0xC4),
    MONSTER_CARNIVAL((short) 0xCC),
    PARTY_SEARCH_REGISTER((short) 0x222),
    PARTY_SEARCH_START((short) 0x222),
    PLAYER_UPDATE((short) 0x222),
    MAPLETV((short) 0x222),
    DISCONNECTED((short) 0xD5),
    TOUCHING_CS((short) 0xDA),
    BUY_CS_ITEM((short) 0xDB),
    COUPON_CODE((short) 0xDC),
    MTS_OP((short) 0xF1),
    USE_HIRED_MERCHANT((short) 0xFF),
    TOUCH_REACTOR((short) 0xC3),
    AUTO_ASSIGN((short) 0x57),
    PASSIVE_ENERGY((short) 0x6B),
    VICIOUS_HAMMER((short) 0xF8),
    SPAWN_PET((short) 0x61),
    MOVE_PET((short) 0xA1),
    PET_CHAT((short) 0xA2),
    PET_COMMAND((short) 0xA3),
    PET_LOOT((short) 0xA4),
    PET_AUTO_POT((short) 0xA5),
    PET_EXCLUDE_ITEMS((short) 0xA6);
    private short code;
    private boolean CheckState;

    private RecvPacketOpcode(final short header) {
        this.code = header;
        this.CheckState = true;
    }

    private RecvPacketOpcode(final short header, final boolean CheckState) {
        this.code = header;
        this.CheckState = CheckState;
    }

    public final int getValue() {
        return code;
    }

    public final boolean NeedsChecking() {
        return CheckState;
    }
}
