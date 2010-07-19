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

import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.scripting.npc.NPCConversationManager;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleShop;
import net.sf.odinms.server.MapleStorage;
import net.sf.odinms.server.constants.GameConstants;
import net.sf.odinms.server.constants.InventoryConstants;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.quest.MapleQuest;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Admin
 */
public class NPCHandler {
 public static final void NPCAnimation(final SeekableLittleEndianAccessor slea, final MapleClient c) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	final int length = (int) slea.available();

	if (length == 6) { // NPC Talk
	    mplew.writeShort(SendPacketOpcode.NPC_ACTION.getValue());
	    mplew.writeInt(slea.readInt());
	    mplew.writeShort(slea.readShort());
	    c.getSession().write(mplew.getPacket());
	} else if (length > 6) { // NPC Move
	    final byte[] bytes = slea.read(length - 9);
	    mplew.writeShort(SendPacketOpcode.NPC_ACTION.getValue());
	    mplew.write(bytes);
	    c.getSession().write(mplew.getPacket());
	}
    }

    public static final void NPCShop(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
	final byte bmode = slea.readByte();

	switch (bmode) {
	    case 0: {
		final MapleShop shop = chr.getShop();
		if (shop == null) {
		    return;
		}
		slea.skip(2);
		final int itemId = slea.readInt();
		final short quantity = slea.readShort();
		shop.buy(c, itemId, quantity);
		break;
	    }
	    case 1: {
		final MapleShop shop = chr.getShop();
		if (shop == null) {
		    return;
		}
		final byte slot = (byte) slea.readShort();
		final int itemId = slea.readInt();
		final short quantity = slea.readShort();
		shop.sell(c, InventoryConstants.getInventoryType(itemId), slot, quantity);
		break;
	    }
	    case 2: {
		final MapleShop shop = chr.getShop();
		if (shop == null) {
		    return;
		}
		final byte slot = (byte) slea.readShort();
		shop.recharge(c, slot);
		break;
	    }
	    default:
		chr.setConversation(0);
		break;
	}
    }

    public static final void NPCTalk(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
	final MapleNPC npc = (MapleNPC) chr.getMap().getNPCByOid(slea.readInt());

	if (npc == null || chr.getConversation() != 0) {
	    return;
	}

	if (npc.hasShop()) {
	    chr.setConversation(1);
	    npc.sendShop(c);
	} else {
	    NPCScriptManager.getInstance().start(c, npc.getId());
	}
    }

    public static final void QuestAction(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
	final byte action = slea.readByte();
	final int quest = slea.readShort2();

	switch (action) {
	    case 0: { // Restore lost item
		slea.skip(4);
		final int itemid = slea.readInt();
		MapleQuest.getInstance(quest).RestoreLostItem(chr, itemid);
		break;
	    }
	    case 1: { // Start Quest
		final int npc = slea.readInt();
		slea.skip(4);
		MapleQuest.getInstance(quest).start(chr, npc);
		break;
	    }
	    case 2: { // Complete Quest
		final int npc = slea.readInt();
		slea.skip(4);

		if (slea.available() >= 4) {
		    MapleQuest.getInstance(quest).complete(chr, npc, slea.readInt());
		} else {
		    MapleQuest.getInstance(quest).complete(chr, npc);
		}
		// c.getSession().write(MaplePacketCreator.completeQuest(c.getPlayer(), quest));
		//c.getSession().write(MaplePacketCreator.updateQuestInfo(c.getPlayer(), quest, npc, (byte)14));
		// 6 = start quest
		// 7 = unknown error
		// 8 = equip is full
		// 9 = not enough mesos
		// 11 = due to the equipment currently being worn wtf o.o
		// 12 = you may not posess more than one of this item
		break;
	    }
	    case 3: { // Forefit Quest
		MapleQuest.getInstance(quest).forfeit(chr);
		break;
	    }
	    case 4: { // Scripted Start Quest
		final int npc = slea.readInt();
		slea.skip(4);
		NPCScriptManager.getInstance().startQuest(c, npc, quest);
		break;
	    }
	    case 5: { // Scripted End Quest
		final int npc = slea.readInt();
		slea.skip(4);
		NPCScriptManager.getInstance().endQuest(c, npc, quest, false);
		c.getSession().write(MaplePacketCreator.showSpecialEffect(9)); // Quest completion
		chr.getMap().broadcastMessage(chr, MaplePacketCreator.showSpecialEffect(chr.getId(), 9), false);
		break;
	    }
	}
    }

    public static final void Storage(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
	final byte mode = slea.readByte();
	final MapleStorage storage = chr.getStorage();

	switch (mode) {
	    case 4: { // Take Out
		final byte type = slea.readByte();
		final byte slot = storage.getSlot(MapleInventoryType.getByType(type), slea.readByte());
		final IItem item = storage.takeOut(slot);

		if (item != null) {
		    if (MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
			MapleInventoryManipulator.addFromDrop(c, item, false);
		    } else {
			storage.store(item);
			chr.dropMessage(1, "Your inventory is full");
		    }
		    storage.sendTakenOut(c, InventoryConstants.getInventoryType(item.getItemId()));
		} else {
		    AutobanManager.getInstance().autoban(c, "Trying to take out item from storage which does not exist.");
		    return;
		}
		break;
	    }
	    case 5: { // Store
		final byte slot = (byte) slea.readShort();
		final int itemId = slea.readInt();
		short quantity = slea.readShort();
		if (quantity < 1) {
		    AutobanManager.getInstance().autoban(c, "Trying to store " + quantity + " of " + itemId);
		    return;
		}
		if (storage.isFull()) {
		    c.getSession().write(MaplePacketCreator.getStorageFull());
		    return;
		}

		if (chr.getMeso() < 100) {
		    chr.dropMessage(1, "You don't have enough mesos to store the item");
		} else {
		    MapleInventoryType type = InventoryConstants.getInventoryType(itemId);
		    IItem item = chr.getInventory(type).getItem(slot).copy();

		    if (InventoryConstants.isPet(item.getItemId())) {
			c.getSession().write(MaplePacketCreator.enableActions());
			return;
		    }
		    if (item.getItemId() == itemId && (item.getQuantity() >= quantity || InventoryConstants.isThrowingStar(itemId) || InventoryConstants.isBullet(itemId))) {
			if (InventoryConstants.isThrowingStar(itemId) || InventoryConstants.isBullet(itemId)) {
			    quantity = item.getQuantity();
			}
			chr.gainMeso(-100, false, true, false);
			MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
			item.setQuantity(quantity);
			storage.store(item);
		    } else {
			AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to store non-matching itemid (" + itemId + "/" + item.getItemId() + ") or quantity not in posession (" + quantity + "/" + item.getQuantity() + ")");
			return;
		    }
		}
		storage.sendStored(c, InventoryConstants.getInventoryType(itemId));
		break;
	    }
	    case 7: {
		int meso = slea.readInt();
		final int storageMesos = storage.getMeso();
		final int playerMesos = chr.getMeso();

		if ((meso > 0 && storageMesos >= meso) || (meso < 0 && playerMesos >= -meso)) {
		    if (meso < 0 && (storageMesos - meso) < 0) { // storing with overflow
			meso = -(Integer.MAX_VALUE - storageMesos);
			if ((-meso) > playerMesos) { // should never happen just a failsafe
			    return;
			}
		    } else if (meso > 0 && (playerMesos + meso) < 0) { // taking out with overflow
			meso = (Integer.MAX_VALUE - playerMesos);
			if ((meso) > storageMesos) { // should never happen just a failsafe
			    return;
			}
		    }
		    storage.setMeso(storageMesos - meso);
		    chr.gainMeso(meso, false, true, false);
		} else {
		    AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to store or take out unavailable amount of mesos (" + meso + "/" + storage.getMeso() + "/" + c.getPlayer().getMeso() + ")");
		    return;
		}
		storage.sendMeso(c);
		break;
	    }
	    case 8: {
		storage.close();
		chr.setConversation(0);
		break;
	    }
	    default:
		System.out.println("Unhandled Storage mode : " + mode);
		break;
	}
    }

    public static final void NPCMoreTalk(final SeekableLittleEndianAccessor slea, final MapleClient c) {
	final byte lastMsg = slea.readByte(); // 00 (last msg type I think)
	final byte action = slea.readByte(); // 00 = end chat, 01 == follow

	final NPCConversationManager cm = NPCScriptManager.getInstance().getCM(c);

	if (cm == null || c.getPlayer().getConversation() == 0) {
	    return;
	}
	if (lastMsg == 2) {
	    if (action != 0) {
		cm.setGetText(slea.readMapleAsciiString());
		if (cm.getType() == 0) {
		    NPCScriptManager.getInstance().startQuest(c, action, lastMsg, -1);
		} else if (cm.getType() == 1) {
		    NPCScriptManager.getInstance().endQuest(c, action, lastMsg, -1);
		} else {
		    NPCScriptManager.getInstance().action(c, action, lastMsg, -1);
		}
	    } else {
		cm.dispose();
	    }
	} else {
	    int selection = -1;
	    if (slea.available() >= 4) {
		selection = slea.readInt();
	    } else if (slea.available() > 0) {
		selection = slea.readByte();
	    }
	    if (action != -1) {
		if (cm.getType() == 0) {
		    NPCScriptManager.getInstance().startQuest(c, action, lastMsg, selection);
		} else if (cm.getType() == 1) {
		    NPCScriptManager.getInstance().endQuest(c, action, lastMsg, selection);
		} else {
		    NPCScriptManager.getInstance().action(c, action, lastMsg, selection);
		}
	    } else {
		cm.dispose();
	    }
	}
    }
    /*    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
    final NPC npc = c.getPlayer().getNpc();
    if (npc == null) {
    return;
    }

    final byte type = slea.readByte();
    if (type != npc.getSentDialogue()) {
    return;
    }
    final byte what = slea.readByte();

    switch (type) {
    case 0x00: // NPCDialogs::normal
    switch (what) {
    case 0:
    //			npc->proceedBack();
    break;
    case 1:
    //			npc->proceedNext();
    break;
    default:
    //			npc->end();
    break;
    }
    break;
    case 0x01: // NPCDialogs::yesNo
    case 0x0c: // NPCDialogs::acceptDecline
    switch (what) {
    case 0:
    //			npc->proceedSelection(0);
    break;
    case 1:
    //			npc->proceedSelection(1);
    break;
    default:
    //			npc->end();
    break;
    }
    break;
    case 0x02: // NPCDialogs::getText
    if (what != 0) {
    //		    npc->proceedText(packet.getString());
    } else {
    //		    npc->end();
    }
    break;
    case 0x03: // NPCDialogs::getNumber
    if (what == 1) {
    //		    npc->proceedNumber(packet.get<int32_t>());
    } else {
    //		    npc->end();
    }
    break;
    case 0x04: // NPCDialogs::simple
    if (what == 0) {
    //		    npc->end();
    } else {
    //		    npc->proceedSelection(packet.get<uint8_t>());
    }
    break;
    case 0x07: // NPCDialogs::style
    if (what == 1) {
    //		    npc->proceedSelection(packet.get<uint8_t>());
    } else {
    //		    npc->end();
    }
    break;
    default:
    //		npc->end();
    break;
    }
    }*/
}