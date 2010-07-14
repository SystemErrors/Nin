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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.server;

import java.util.LinkedList;
import java.util.List;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.ItemFlag;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.server.constants.GameConstants;
import net.sf.odinms.server.constants.InventoryConstants;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Matze
 */
public class MapleTrade {

    private MapleTrade partner = null;
    private final List<IItem> items = new LinkedList<IItem>();
    private List<IItem> exchangeItems;
    private int meso = 0, exchangeMeso = 0;
    private boolean locked = false;
    private final MapleCharacter chr;
    private final byte tradingslot;

    public MapleTrade(final byte tradingslot, final MapleCharacter chr) {
	this.tradingslot = tradingslot;
	this.chr = chr;
    }

    public final void CompleteTrade() {
	for (final IItem item : exchangeItems) {
	    byte flag = item.getFlag();

	    if (ItemFlag.KARMA_EQ.check(flag)) {
		item.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
	    } else if (ItemFlag.KARMA_USE.check(flag)) {
		item.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
	    }
	    MapleInventoryManipulator.addFromDrop(chr.getClient(), item, false);
	}
	if (exchangeMeso > 0) {
	    chr.gainMeso(exchangeMeso - GameConstants.getTaxAmount(exchangeMeso), false, true, false);
	}
	exchangeMeso = 0;
	if (exchangeItems != null) { // just to be on the safe side...
	    exchangeItems.clear();
	}
	chr.getClient().getSession().write(MaplePacketCreator.TradeMessage(tradingslot, (byte) 0x06));
    }

    public final void cancel() {
	for (final IItem item : items) {
	    MapleInventoryManipulator.addFromDrop(chr.getClient(), item, false);
	}
	if (meso > 0) {
	    chr.gainMeso(meso, false, true, false);
	}
	meso = 0;
	if (items != null) { // just to be on the safe side...
	    items.clear();
	}
	chr.getClient().getSession().write(MaplePacketCreator.getTradeCancel(tradingslot));
    }

    public final boolean isLocked() {
	return locked;
    }

    public final void setMeso(final int meso) {
	if (locked || partner == null || meso <= 0 || this.meso + meso <= 0) {
	    return;
	}
	if (chr.getMeso() >= meso) {
	    chr.gainMeso(-meso, false, true, false);
	    this.meso += meso;
	    chr.getClient().getSession().write(MaplePacketCreator.getTradeMesoSet((byte) 0, this.meso));
	    if (partner != null) {
		partner.getChr().getClient().getSession().write(MaplePacketCreator.getTradeMesoSet((byte) 1, this.meso));
	    }
	}
    }

    public final void addItem(final IItem item) {
	if (locked || partner == null) {
	    return;
	}
	items.add(item);
	chr.getClient().getSession().write(MaplePacketCreator.getTradeItemAdd((byte) 0, item));
	if (partner != null) {
	    partner.getChr().getClient().getSession().write(MaplePacketCreator.getTradeItemAdd((byte) 1, item));
	}
    }

    public final void chat(final String message) {
	chr.getClient().getSession().write(MaplePacketCreator.getPlayerShopChat(chr, message, true));
	if (partner != null) {
	    partner.getChr().getClient().getSession().write(MaplePacketCreator.getPlayerShopChat(chr, message, false));
	}
    }

    public final MapleTrade getPartner() {
	return partner;
    }

    public final void setPartner(final MapleTrade partner) {
	if (locked) {
	    return;
	}
	this.partner = partner;
    }

    public final MapleCharacter getChr() {
	return chr;
    }

    private final boolean check() {
	if (chr.getMeso() + exchangeMeso < 0) {
	    return false;
	}
	byte eq = 0, use = 0, setup = 0, etc = 0;
	for (final IItem item : exchangeItems) {
	    switch (InventoryConstants.getInventoryType(item.getItemId())) {
		case EQUIP:
		    eq++;
		    break;
		case USE:
		    use++;
		    break;
		case SETUP:
		    setup++;
		    break;
		case ETC:
		    etc++;
		    break;
		case CASH: // Not allowed, probably hacking
		    return false;
	    }
	}
	if (chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() <= eq
		|| chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() <= use
		|| chr.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() <= setup
		|| chr.getInventory(MapleInventoryType.ETC).getNumFreeSlot() <= etc) {
	    return false;
	}
	return true;
    }

    public final static void completeTrade(final MapleCharacter c) {
	final MapleTrade local = c.getTrade();
	final MapleTrade partner = local.getPartner();

	if (partner == null || local.locked) {
	    return;
	}
	local.locked = true; // Locking the trade
	partner.getChr().getClient().getSession().write(MaplePacketCreator.getTradeConfirmation());

	partner.exchangeItems = local.items; // Copy this to partner's trade since it's alreadt accepted
	partner.exchangeMeso = local.meso; // Copy this to partner's trade since it's alreadt accepted

	if (partner.isLocked()) { // Both locked
	    if (!local.check() || !partner.check()) { // Check for full inventories
		// NOTE : IF accepted = other party but inventory is full, the item is lost.
		partner.cancel();
		local.cancel();

		c.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "There is not enough inventory space to complete the trade."));
		partner.getChr().getClient().getSession().write(MaplePacketCreator.serverNotice(5, "There is not enough inventory space to complete the trade."));
	    } else {
		local.CompleteTrade();
		partner.CompleteTrade();
	    }
	    partner.getChr().setTrade(null);
	    c.setTrade(null);
	}
    }

    public static final void cancelTrade(final MapleTrade Localtrade) {
	Localtrade.cancel();

	final MapleTrade partner = Localtrade.getPartner();
	if (partner != null) {
	    partner.cancel();
	    partner.getChr().setTrade(null);
	}
	Localtrade.chr.setTrade(null);
    }

    public static final void startTrade(final MapleCharacter c) {
	if (c.getTrade() == null) {
	    c.setTrade(new MapleTrade((byte) 0, c));
	    c.getClient().getSession().write(MaplePacketCreator.getTradeStart(c.getClient(), c.getTrade(), (byte) 0));
	} else {
	    c.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "You are already in a trade"));
	}
    }

    public static final void inviteTrade(final MapleCharacter c1, final MapleCharacter c2) {
	if (c2.getTrade() == null) {
	    c2.setTrade(new MapleTrade((byte) 1, c2));
	    c2.getTrade().setPartner(c1.getTrade());
	    c1.getTrade().setPartner(c2.getTrade());
	    c2.getClient().getSession().write(MaplePacketCreator.getTradeInvite(c1));
	} else {
	    c1.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "The other player is already trading with someone else."));
	    cancelTrade(c1.getTrade());
	}
    }

    public static final void visitTrade(final MapleCharacter c1, final MapleCharacter c2) {
	if (c1.getTrade() != null && c1.getTrade().getPartner() == c2.getTrade() && c2.getTrade() != null && c2.getTrade().getPartner() == c1.getTrade()) {
	    // We don't need to check for map here as the user is found via MapleMap.getCharacterById_InMap()
	    c2.getClient().getSession().write(MaplePacketCreator.getTradePartnerAdd(c1));
	    c1.getClient().getSession().write(MaplePacketCreator.getTradeStart(c1.getClient(), c1.getTrade(), (byte) 1));
	} else {
	    c1.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "The other player has already closed the trade"));
	}
    }

    public static final void declineTrade(final MapleCharacter c) {
	final MapleTrade trade = c.getTrade();
	if (trade != null) {
	    if (trade.getPartner() != null) {
		MapleCharacter other = trade.getPartner().getChr();
		other.getTrade().cancel();
		other.setTrade(null);
		other.getClient().getSession().write(MaplePacketCreator.serverNotice(5, c.getName() + " has declined your trade request"));
	    }
	    trade.cancel();
	    c.setTrade(null);
	}
    }
}
