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

package net.sf.odinms.tools.Packets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.sf.odinms.client.Inventory.IEquip;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Inventory.MaplePet;
import net.sf.odinms.client.Inventory.MapleRing;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleQuestStatus;
import net.sf.odinms.client.Skills.ISkill;
import net.sf.odinms.client.Skills.SkillEntry;
import net.sf.odinms.net.world.PlayerCoolDownValueHolder;
import net.sf.odinms.server.constants.InventoryConstants;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.tools.KoreanDateUtil;
import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.tools.data.output.LittleEndianWriter;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Admin
 */
public class PacketHelper {

        private final static long FT_UT_OFFSET = 116444592000000000L;
        private static final byte unk1[] = new byte[]{(byte) 0x00, (byte) 0x40, (byte) 0xE0, (byte) 0xFD, (byte) 0x3B, (byte) 0x37, (byte) 0x4F, (byte) 0x01};

    private final static long NO_EXPIRATION = 150842304000000000L;


    public static final long getKoreanTimestamp(final long realTimestamp) {
	long time = (realTimestamp / 1000 / 60); // convert to minutes
	return ((time * 600000000) + FT_UT_OFFSET);
    }

    public static final long getTime(final long realTimestamp) {
	long time = (realTimestamp / 1000); // convert to seconds
	return ((time * 10000000) + FT_UT_OFFSET);
    }
    public static int getQuestTimestamp(long realTimestamp) {
        return (int) (((int) (realTimestamp / 1000 / 60)) * 0.1396987) + 27111908;
    }

     public static void addQuestInfo(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
	final List<MapleQuestStatus> started = chr.getStartedQuests();
	mplew.writeShort(started.size());

	for (final MapleQuestStatus q : started) {
	    mplew.writeShort(q.getQuest().getId());
	    mplew.writeMapleAsciiString("");
	}
	final List<MapleQuestStatus> completed = chr.getCompletedQuests();
	int time;
	mplew.writeShort(completed.size());

	for (final MapleQuestStatus q : completed) {
	    mplew.writeShort(q.getQuest().getId());
	    time = KoreanDateUtil.getQuestTimestamp(q.getCompletionTime());
	    mplew.writeInt(time); // maybe start time? no effect.
	    mplew.writeInt(time); // completion time
	}
    }

    public static final void addSkillInfo(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
	final Map<ISkill, SkillEntry> skills = chr.getSkills();
	mplew.writeShort(skills.size());
	for (final Entry<ISkill, SkillEntry> skill : skills.entrySet()) {
	    mplew.writeInt(skill.getKey().getId());
	    mplew.writeInt(skill.getValue().skillevel);

	    if (skill.getKey().isFourthJob()) {
		mplew.writeInt(skill.getValue().masterlevel);
	    }
	}
    }

    public static final void addCoolDownInfo(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
	mplew.writeShort(chr.getAllCoolDowns().size());
	for (final PlayerCoolDownValueHolder cooling : chr.getAllCoolDowns()) {
	    mplew.writeInt(cooling.skillId);
	    mplew.writeShort((int) (cooling.length + cooling.startTime - System.currentTimeMillis()) / 1000);
	}
    }

   public static final void addMonsterBookInfo(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
	mplew.writeInt(chr.getMonsterBookCover());
	mplew.write(0);
	chr.getMonsterBook().addCardPacket(mplew);
    }

    public static final void addRingInfo(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
	List<MapleRing> rings = new ArrayList<MapleRing>();
	MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
	for (final IItem item : iv.list()) {
	    if (((IEquip) item).getRingId() > -1) {
		rings.add(MapleRing.loadFromDb(((IEquip) item).getRingId()));
	    }
	}
	iv = chr.getInventory(MapleInventoryType.EQUIP);
	for (final IItem item : iv.list()) {
	    if (((IEquip) item).getRingId() > -1) {
		rings.add(MapleRing.loadFromDb(((IEquip) item).getRingId()));
	    }
	}
	Collections.sort(rings);
	boolean FR_last = false;
	for (final MapleRing ring : rings) {
	    if ((ring.getItemId() >= 1112800 && ring.getItemId() <= 1112803 || ring.getItemId() <= 1112806 || ring.getItemId() <= 1112807 || ring.getItemId() <= 1112809) && rings.indexOf(ring) == 0) {
		mplew.writeShort(0);
	    }
	    mplew.writeShort(0);
	    mplew.writeShort(1);
	    mplew.writeInt(ring.getPartnerChrId());
	    mplew.writeAsciiString(StringUtil.getRightPaddedStr(ring.getPartnerName(), '\0', 13));
	    mplew.writeInt(ring.getRingId());
	    mplew.writeInt(0);
	    mplew.writeInt(ring.getPartnerRingId());
	    if (ring.getItemId() >= 1112800 && ring.getItemId() <= 1112803 || ring.getItemId() <= 1112806 || ring.getItemId() <= 1112807 || ring.getItemId() <= 1112809) {
		FR_last = true;
		mplew.writeInt(0);
		mplew.writeInt(ring.getItemId());
		mplew.writeShort(0);
	    } else {
		if (rings.size() > 1) {
		    mplew.writeShort(0);
		}
		FR_last = false;
	    }
	}
	if (!FR_last) {
	    mplew.writeLong(0);
	}
    }

    public static void addInventoryInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
	mplew.writeInt(chr.getMeso()); // mesos
	mplew.write(chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit()); // equip slots
	mplew.write(chr.getInventory(MapleInventoryType.USE).getSlotLimit()); // use slots
	mplew.write(chr.getInventory(MapleInventoryType.SETUP).getSlotLimit()); // set-up slots
	mplew.write(chr.getInventory(MapleInventoryType.ETC).getSlotLimit()); // etc slots
	mplew.write(chr.getInventory(MapleInventoryType.CASH).getSlotLimit()); // cash slots

	mplew.write(unk1);

	MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
	Collection<IItem> equippedC = iv.list();
	List<Item> equipped = new ArrayList<Item>(equippedC.size());

	for (IItem item : equippedC) {
	    equipped.add((Item) item);
	}
	Collections.sort(equipped);
	for (Item item : equipped) {
	    if (item.getPosition() < 0 && item.getPosition() > -100) {
		addItemInfo(mplew, item, false, false);
	    }
	}
	mplew.write(0); // start of equipped nx
	for (Item item : equipped) {
	    if (item.getPosition() < -100) {
		addItemInfo(mplew, item, false, false);
	    }
	}

	mplew.write(0); // start of equip inventory
	iv = chr.getInventory(MapleInventoryType.EQUIP);
	for (IItem item : iv.list()) {
	    addItemInfo(mplew, item, false, false);
	}
	mplew.write(0); // start of use inventory
	iv = chr.getInventory(MapleInventoryType.USE);
	for (IItem item : iv.list()) {
	    addItemInfo(mplew, item, false, false);
	}
	mplew.write(0); // start of set-up inventory
	iv = chr.getInventory(MapleInventoryType.SETUP);
	for (IItem item : iv.list()) {
	    addItemInfo(mplew, item, false, false);
	}
	mplew.write(0); // start of etc inventory
	iv = chr.getInventory(MapleInventoryType.ETC);
	for (IItem item : iv.list()) {
	    addItemInfo(mplew, item, false, false);
	}
	mplew.write(0); // start of cash inventory
	iv = chr.getInventory(MapleInventoryType.CASH);
	for (IItem item : iv.list()) {
	    addItemInfo(mplew, item, false, false);
	}
	mplew.write(0);
    }

    public static final void addCharStats(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
	mplew.writeInt(chr.getId()); // character id
	mplew.writeAsciiString(chr.getName(), 13);
	mplew.write(chr.getGender()); // gender (0 = male, 1 = female)
	mplew.write(chr.getSkinColor()); // skin color
	mplew.writeInt(chr.getFace()); // face
	mplew.writeInt(chr.getHair()); // hair
	mplew.writeZeroBytes(24);
	mplew.write(chr.getLevel()); // level
	mplew.writeShort(chr.getJob()); // job
	chr.getStat().connectData(mplew);
	mplew.writeShort(chr.getRemainingAp()); // remaining ap
	mplew.writeShort(chr.getRemainingSp()); // remaining sp
	mplew.writeInt(chr.getExp()); // exp
	mplew.writeShort(chr.getFame()); // fame
	mplew.writeInt(0); // Gachapon exp
	mplew.writeInt(chr.getMapId()); // current map id
	mplew.write(chr.getInitialSpawnpoint()); // spawnpoint
        // We had a write int 0 here. :p 
    }

    public static final void addCharLook(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr, final boolean mega) {
	mplew.write(chr.getGender());
	mplew.write(chr.getSkinColor());
	mplew.writeInt(chr.getFace());
	mplew.write(mega ? 0 : 1);
	mplew.writeInt(chr.getHair());

	final Map<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
	final Map<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
	MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);

	for (final IItem item : equip.list()) {
	    byte pos = (byte) (item.getPosition() * -1);
	    if (pos < 100 && myEquip.get(pos) == null) {
		myEquip.put(pos, item.getItemId());
	    } else if (pos > 100 && pos != 111) {
		pos -= 100;
		if (myEquip.get(pos) != null) {
		    maskedEquip.put(pos, myEquip.get(pos));
		}
		myEquip.put(pos, item.getItemId());
	    } else if (myEquip.get(pos) != null) {
		maskedEquip.put(pos, item.getItemId());
	    }
	}
	for (final Entry<Byte, Integer> entry : myEquip.entrySet()) {
	    mplew.write(entry.getKey());
	    mplew.writeInt(entry.getValue());
	}
	mplew.write(0xFF); // end of visible itens
	// masked itens
	for (final Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
	    mplew.write(entry.getKey());
	    mplew.writeInt(entry.getValue());
	}
	mplew.write(0xFF); // ending markers

	final IItem cWeapon = equip.getItem((byte) -111);
	mplew.writeInt(cWeapon != null ? cWeapon.getItemId() : 0);
	mplew.writeInt(0);
	mplew.writeLong(0);
    }

    public static final void addExpirationTime(final MaplePacketLittleEndianWriter mplew, final long time) {
	mplew.writeShort(1408); // 80 05
	if (time != -1) {
	    mplew.writeInt(KoreanDateUtil.getItemTimestamp(time));
	    mplew.write(1);
	} else {
	    mplew.writeInt(400967355);
	    mplew.write(2);
	}
    }

    public static final void addItemInfo(final MaplePacketLittleEndianWriter mplew, final IItem item, final boolean zeroPosition, final boolean leaveOut) {
	short pos = item.getPosition();
	if (zeroPosition) {
	    if (!leaveOut) {
		mplew.write(0);
	    }
	} else {
	    if (pos <= -1) {
		pos *= -1;
		if (pos > 100) {
		    pos -= 100;
		}
	    }
	    mplew.write(pos);
	}
	mplew.write(item.getPet() != null ? 3 : item.getType());
	mplew.writeInt(item.getItemId());

	if (item.getPet() != null) { // Pet
	    final MaplePet pet = item.getPet();

	    mplew.write(1);
	    mplew.writeInt(pet.getUniqueId());
	    mplew.writeZeroBytes(5);
	    addExpirationTime(mplew, item.getExpiration()); // 80 F9 58 8D 3B C7 24 Time where it expired?

	    mplew.writeAsciiString(pet.getName(), 13);
	    mplew.write(pet.getLevel());
	    mplew.writeShort(pet.getCloseness());
/*	    mplew.writeShort(pet.getFullness());  // What I've got from MSEA, still buggy though.
	    addExpirationTime(mplew, item.getExpiration());
	    mplew.writeZeroBytes(10);*/

	    mplew.write(pet.getFullness());
	    mplew.writeLong(getTime(System.currentTimeMillis()));
	    mplew.writeInt(1);
	    mplew.writeInt(0);
	} else {
	    mplew.writeShort(0);
	    addExpirationTime(mplew, item.getExpiration());

	    if (item.getType() == 1) {
		final IEquip equip = (IEquip) item;
		mplew.write(equip.getUpgradeSlots());
		mplew.write(equip.getLevel());
		mplew.writeShort(equip.getStr());
		mplew.writeShort(equip.getDex());
		mplew.writeShort(equip.getInt());
		mplew.writeShort(equip.getLuk());
		mplew.writeShort(equip.getHp());
		mplew.writeShort(equip.getMp());
		mplew.writeShort(equip.getWatk());
		mplew.writeShort(equip.getMatk());
		mplew.writeShort(equip.getWdef());
		mplew.writeShort(equip.getMdef());
		mplew.writeShort(equip.getAcc());
		mplew.writeShort(equip.getAvoid());
		mplew.writeShort(equip.getHands());
		mplew.writeShort(equip.getSpeed());
		mplew.writeShort(equip.getJump());
		mplew.writeMapleAsciiString(equip.getOwner());
		mplew.writeShort(equip.getFlag());
		mplew.write(0);
		mplew.write(equip.getItemLevel()); // Item level
		mplew.writeShort(0);
		mplew.writeShort(equip.getItemEXP()); // Item Exp
		mplew.writeInt(equip.getViciousHammer());
		mplew.writeLong(-1);
		mplew.write(unk1);
		mplew.writeInt(-1);
	    } else {
		mplew.writeShort(item.getQuantity());
		mplew.writeMapleAsciiString(item.getOwner());
		mplew.writeShort(item.getFlag());

		if (InventoryConstants.isThrowingStar(item.getItemId()) || InventoryConstants.isBullet(item.getItemId())) {
		    mplew.writeInt(2);
		    mplew.writeShort(0x54);
		    mplew.write(0);
		    mplew.write(0x34);
		}
	    }
	}
    }

    public static final void serializeMovementList(final LittleEndianWriter lew, final List<LifeMovementFragment> moves) {
	lew.write(moves.size());
	for (LifeMovementFragment move : moves) {
	    move.serialize(lew);
	}
    }

     public static final void addRocksInfo(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
	mplew.writeInt(999999999); // Teleport maps (TODO)
	mplew.writeInt(999999999);
	mplew.writeInt(999999999);
	mplew.writeInt(999999999);
	mplew.writeInt(999999999);

	final int[] map = chr.getRocks();
	for (int i = 0; i < 10; i++) { // VIP teleport map
	    mplew.writeInt(map[i]);
	}
    }
}
