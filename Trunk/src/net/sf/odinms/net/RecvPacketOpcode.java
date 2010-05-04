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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public enum RecvPacketOpcode implements WritableIntValueHolder {
	// GENERIC
	PONG,
	// LOGIN
	AFTER_LOGIN,
	SERVERLIST_REQUEST,
	SERVERLIST_REREQUEST,
	CHARLIST_REQUEST,
	CHAR_SELECT,
	CHECK_CHAR_NAME,
	CREATE_CHAR,
	DELETE_CHAR,
	LOGIN_PASSWORD,
	RELOG,
	SERVERSTATUS_REQUEST,
	VIEW_ALL_CHAR,
	PICK_ALL_CHAR,
	// CHANNEL
	CHANGE_CHANNEL,
	CHAR_INFO_REQUEST,
	CLOSE_RANGE_ATTACK,
	RANGED_ATTACK,
	MAGIC_ATTACK,
	FACE_EXPRESSION,
	HEAL_OVER_TIME,
	ITEM_SORT,
	ITEM_MOVE,
	ITEM_PICKUP,
	CHANGE_MAP,
	MESO_DROP,
	MOVE_LIFE,
	MOVE_PLAYER,
	NPC_SHOP,
	NPC_TALK,
	NPC_TALK_MORE,
	PLAYER_LOGGEDIN,
	QUEST_ACTION,
	TAKE_DAMAGE,
	USE_CASH_ITEM,
	USE_ITEM,
	USE_RETURN_SCROLL,
	USE_UPGRADE_SCROLL,
	USE_SUMMON_BAG,
	GENERAL_CHAT,
	WHISPER,
	SPECIAL_MOVE,
	USE_INNER_PORTAL,
	CANCEL_BUFF,
	PLAYER_INTERACTION,
	CANCEL_ITEM_EFFECT,
	DISTRIBUTE_AP,
	DISTRIBUTE_SP,
	CHANGE_KEYMAP,
	CHANGE_MAP_SPECIAL,
	STORAGE,
	STRANGE_DATA,
	GIVE_FAME,
	PARTY_OPERATION,
	DENY_PARTY_REQUEST, //probably something else too..
	PARTYCHAT,
	USE_DOOR,
	ENTER_MTS,
	ENTER_CASH_SHOP,
	DAMAGE_SUMMON,
	MOVE_SUMMON,
	SUMMON_ATTACK,
	BUDDYLIST_MODIFY,
	USE_ITEMEFFECT,
	USE_CHAIR,
	SKILL_EFFECT,
	CANCEL_CHAIR,
	DAMAGE_REACTOR,
	GUILD_OPERATION,
	DENY_GUILD_REQUEST,
	BBS_OPERATION,
	MESSENGER,
	NPC_ACTION,
	TOUCHING_CS,
	BUY_CS_ITEM,
	COUPON_CODE,
    MTS_OP,
	SPAWN_PET,
	MOVE_PET,
	PET_CHAT,
	PET_COMMAND,
	PET_FOOD,
	PET_LOOT,
	AUTO_AGGRO,
	MONSTER_BOMB,
	CANCEL_DEBUFF,
	USE_SKILL_BOOK,
	SKILL_MACRO,
	NOTE_ACTION,
	MAPLETV,
	PLAYER_UPDATE,
    TROCK_ADD_MAP,// = 0x5E
	USE_CATCH_ITEM,
	CLOSE_CHALKBOARD,
	DUEY_ACTION,
    MONSTER_CARNIVAL,
    USE_HIRED_MERCHANT,
    REGISTER_PIN,
    PASSIVE_ENERGY,
    CREATE_CYGNUS,
    TOUCH_REACTOR,
    SCRIPTED_ITEM,
    MONSTER_BOOK_COVER,
    AUTO_ASSIGN,
    MAKER_SKILL,
    VICIOUS_HAMMER,
    ENERGY_CHARGE_ATTACK,
    MOB_DAMAGE_MOB,
    MOB_DAMAGE_MOB_FRIENDLY,
    USE_MOUNT_FOOD,
    SPOUSE_CHAT,
    PET_AUTO_POT,
    REPORT,
    ADMIN_COMMAND,
    HIRED_MERCHANT_REQUEST;

    private int code = -2;

    public void setValue(int code) {
        this.code = code;
    }

    @Override
    public int getValue() {
        return code;
    }

    public static Properties getDefaultProperties() throws FileNotFoundException, IOException {
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(System.getProperty("net.sf.odinms.recvops"));
        props.load(fis);
        fis.close();
        return props;
    }

    static {
        try {
            ExternalCodeTableGetter.populateValues(getDefaultProperties(), values());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load recvops", e);
        }
    }
}
