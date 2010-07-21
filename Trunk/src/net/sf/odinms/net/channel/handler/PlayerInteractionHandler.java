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

import java.util.Arrays;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.ItemFlag;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleMiniGame;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.constants.InventoryConstants;
import net.sf.odinms.server.maps.FieldLimitType;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.shops.HiredMerchant;
import net.sf.odinms.server.shops.IMaplePlayerShop;
import net.sf.odinms.server.shops.MaplePlayerShop;
import net.sf.odinms.server.shops.MaplePlayerShopItem;
import net.sf.odinms.tools.Packets.PlayerShopPacket;
import net.sf.odinms.tools.Packets.ShopAndGamePackets;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.constants.SpecialStuff;
import net.sf.odinms.server.shops.AbstractPlayerStore;
import net.sf.odinms.tools.MaplePacketCreator;

public class PlayerInteractionHandler {

    private static final byte CREATE = 0x00,
            INVITE = 0x02,
            DECLINE = 0x03,
            VISIT = 0x04,
            CHAT = 0x06,
            EXIT = (0xA),
            OPEN = (0xB),
            SET_ITEMS = (0xE),
            SET_MESO = (0xF),
            CONFIRM = (0x10),
            ADD_ITEM = (0x14),
            BUY = (0x15),
            REMOVE_ITEM = (0x19),
            BAN_PLAYER = (0x1A),
            PUT_ITEM = (0x1F),
            MERCHANT_BUY = (0x20),
            TAKE_ITEM_BACK = (0x24),
            MAINTENANCE_OFF = (0x25),
            MERCHANT_ORGANISE = (0x26),
            CLOSE_MERCHANT = (0x27),
            REQUEST_TIE = (44),
            ANSWER_TIE = (45),
            GIVE_UP = (46),
            EXIT_AFTER_GAME = (0x32),
            CANCEL_EXIT = (0x33),
            READY = (0x34),
            UN_READY = (0x35),
            START = (0x37),
            SKIP = 0x39,
            MOVE_OMOK = 0x3A,
            SELECT_CARD = 62;

    public static final void PlayerInteraction(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final byte action = slea.readByte();
        switch (action) { // Mode
            case CREATE: {
                byte createType = slea.readByte();
                if (createType == 3) {// trade
                    MapleTrade.startTrade(chr);
                } else if (createType == 1) { // omok mini game
                    if (chr.getChalkboard() != null || FieldLimitType.Minigames.check(chr.getMap().getFieldLimit())) {
                        return;
                    }
                    String desc = slea.readMapleAsciiString();
                    slea.readByte(); // 20 6E 4E
                    int type = slea.readByte(); // 20 6E 4E
                    MapleMiniGame game = new MapleMiniGame(chr, desc);
                    chr.setMapleMiniGame(game);
                    game.setPieceType(type);
                    game.setGameType("omok");
                    chr.getMap().addMapObject(game);
                    chr.getMap().broadcastMessage(ShopAndGamePackets.addOmokBox(chr, 1, 0));
                    game.sendOmok(c, type);
                } else if (createType == 2) { // matchcard
                    if (chr.getChalkboard() != null) {
                        return;
                    }
                    String desc = slea.readMapleAsciiString();
                    slea.readByte(); // 20 6E 4E
                    int type = slea.readByte(); // 20 6E 4E
                    MapleMiniGame game = new MapleMiniGame(chr, desc);
                    game.setPieceType(type);
                    if (type == 0) {
                        game.setMatchesToWin(6);
                    } else if (type == 1) {
                        game.setMatchesToWin(10);
                    } else if (type == 2) {
                        game.setMatchesToWin(15);
                    }
                    game.setGameType("matchcard");
                    chr.setMapleMiniGame(game);
                    chr.getMap().addMapObject(game);
                    chr.getMap().broadcastMessage(ShopAndGamePackets.addMatchCardBox(chr, 1, 0));
                    game.sendMatchCard(c, type);
                } else if (createType == 4 || createType == 5) {
                    if (chr.getMap().getMapObjectsInRange(chr.getPosition(), 19500, Arrays.asList(MapleMapObjectType.SHOP, MapleMapObjectType.HIRED_MERCHANT)).size() != 0) {
                        chr.dropMessage(1, "You may not establish a store here.");
                        return;
                    }
                    final String desc = slea.readMapleAsciiString();
                    slea.skip(3);
                    final int itemId = slea.readInt();

                    if (createType == 4) {
                        chr.setPlayerShop(new MaplePlayerShop(chr, itemId, desc));
                        c.getSession().write(PlayerShopPacket.getPlayerStore(chr, true));
                    } else {
                        final HiredMerchant merch = new HiredMerchant(chr, itemId, desc);
                        chr.setPlayerShop(merch);
                        c.getSession().write(PlayerShopPacket.getHiredMerch(chr, merch, true));
                    }
                }
                break;
            }
            case INVITE: {
                MapleTrade.inviteTrade(chr, chr.getMap().getCharacterById_InMap(slea.readInt()));
                break;
            }
            case DECLINE: {
                MapleTrade.declineTrade(chr);
            }
            case VISIT: {
                if (chr.getTrade() != null && chr.getTrade().getPartner() != null) {
                    MapleTrade.visitTrade(chr, chr.getTrade().getPartner().getChr());
                } else {
                    final MapleMapObject ob = chr.getMap().getMapObject(slea.readInt());

                    if (ob instanceof IMaplePlayerShop && chr.getPlayerShop() == null) {
                        final IMaplePlayerShop ips = (IMaplePlayerShop) ob;

                        if (ob instanceof HiredMerchant) {
                            final HiredMerchant merchant = (HiredMerchant) ips;
                            if (merchant.isOwner(chr)) {
                                merchant.setOpen(false);
                                merchant.broadcastToVisitors(PlayerShopPacket.shopErrorMessage(0x0D, 1));
                                merchant.removeAllVisitors((byte) 16, (byte) 0);
                                chr.setPlayerShop(ips);
                                c.getSession().write(PlayerShopPacket.getHiredMerch(chr, merchant, false));
                            } else {
                                if (!merchant.isOpen()) {
                                    chr.dropMessage(1, "This shop is in maintenance, please come by later.");
                                } else {
                                    if (ips.getFreeSlot() == -1) {
                                        chr.dropMessage(1, "This shop has reached it's maximum capacity, please come by later.");
                                    } else {
                                        chr.setPlayerShop(ips);
                                        merchant.addVisitor(chr);
                                        c.getSession().write(PlayerShopPacket.getHiredMerch(chr, merchant, false));
                                    }
                                }
                            }
                        } else if (ips.getShopType() == 2) {
                            if (((MaplePlayerShop) ips).isBanned(chr.getName())) {
                                chr.dropMessage(1, "You have been banned from this store.");
                                return;
                            }
                        } else {
                            if (ips.getFreeSlot() == -1) {
                                chr.dropMessage(1, "This shop has reached it's maximum capacity, please come by later.");
                            } else {
                                chr.setPlayerShop(ips);
                                ips.addVisitor(chr);
                                c.getSession().write(PlayerShopPacket.getPlayerStore(chr, false));
                            }
                        }
                    } else if (ob instanceof MapleMiniGame) {
                        MapleMiniGame game = (MapleMiniGame) ob;
                        if (game.hasFreeSlot() && !game.isVisitor(c.getPlayer())) {
                            game.addVisitor(c.getPlayer());
                            c.getPlayer().setMapleMiniGame(game);
                            if (game.getGameType().equals("omok")) {
                                game.sendOmok(c, game.getPieceType());
                            } else if (game.getGameType().equals("matchcard")) {
                                game.sendMatchCard(c, game.getPieceType());
                            }
                        } else {
                            c.getPlayer().getClient().getSession().write(ShopAndGamePackets.getMiniGameFull());
                        }
                    }
                }
                break;
            }
            case CHAT: { // chat lol
                HiredMerchant merchant = chr.getHiredMerchant();
                if (chr.getTrade() != null) {
                    chr.getTrade().chat(slea.readMapleAsciiString());
                } else if (chr.getPlayerShop() != null) { //mini game
                    final IMaplePlayerShop ips = chr.getPlayerShop();
                    final String message = slea.readMapleAsciiString();
                    ips.broadcastToVisitors(PlayerShopPacket.shopChat(chr.getName() + " : " + message, ips.isOwner(chr) ? 0 : ips.getVisitorSlot(chr)));
                } else if (chr.getMapleMiniGame() != null) {
                    MapleMiniGame game = chr.getMapleMiniGame();
                    if (game != null) {
                        game.chat(c, slea.readMapleAsciiString());
                    }
                }
                break;
            }
            case EXIT: {
                if (chr.getTrade() != null) {
                    MapleTrade.cancelTrade(chr.getTrade());
                } else if (chr.getPlayerShop() != null) {
                    final IMaplePlayerShop ips = chr.getPlayerShop();
		    if (ips == null) {
			return;
		    }
		    if (ips.isOwner(chr)) {
			if (ips.getShopType() == 2) {
			    boolean save = false;
			    for (MaplePlayerShopItem items : ips.getItems()) {
				if (items.bundles > 0) {
				    if (MapleInventoryManipulator.addFromDrop(c, items.item, false)) {
					items.bundles = 0;
				    } else {
					save = true;
					break;
				    }
				}
			    }
			    ips.removeAllVisitors(3, 1);
			    ips.closeShop(save, true);
			}
		    } else {
			ips.removeVisitor(chr);
		    }
		    chr.setPlayerShop(null);
                } else if (chr.getMapleMiniGame() != null) {
                    MapleMiniGame game = c.getPlayer().getMapleMiniGame();
                    c.getPlayer().setMapleMiniGame(null);
                    if (game.isOwner(c.getPlayer())) {
                        c.getPlayer().getMap().broadcastMessage(PlayerShopPacket.removeCharBox(c.getPlayer()));
                        game.broadcastToVisitor(ShopAndGamePackets.getMiniGameClose((byte) 0));
                    } else {
                        game.removeVisitor(c.getPlayer());
                    }
                }
                break;
            }
            case OPEN: {
                if (chr.getMap().allowPersonalShop()) {
		    final IMaplePlayerShop shop = chr.getPlayerShop();
		    if (shop != null && shop.isOwner(chr)) {
			chr.getMap().addMapObject((AbstractPlayerStore) shop);
			if (shop.getShopType() == 1) {
			    final HiredMerchant merchant = (HiredMerchant) shop;
			    merchant.setStoreid(c.getChannelServer().addMerchant(merchant));
			    merchant.setOpen(true);
			    chr.getMap().broadcastMessage(PlayerShopPacket.spawnHiredMerchant(merchant));
			    chr.setPlayerShop(null);

			} else if (shop.getShopType() == 2) {
			    chr.getMap().broadcastMessage(PlayerShopPacket.sendPlayerShopBox(chr));
			}
			slea.readByte();
		    }
		} else {
		    c.getSession().close();
		}
		break;
	    }
            case READY: {
                MapleMiniGame game = chr.getMapleMiniGame();
                game.broadcast(ShopAndGamePackets.getMiniGameReady(game));
            }
            case UN_READY: {
                MapleMiniGame game = chr.getMapleMiniGame();
                game.broadcast(ShopAndGamePackets.getMiniGameUnReady(game));
            }
            case START: {
                MapleMiniGame game = chr.getMapleMiniGame();
                if (game.getGameType().equals("omok")) {
                    game.broadcast(ShopAndGamePackets.getMiniGameStart(game, game.getLoser()));
                    chr.getMap().broadcastMessage(ShopAndGamePackets.addOmokBox(game.getOwner(), 2, 1));
                }
                if (game.getGameType().equals("matchcard")) {
                    game.shuffleList();
                    game.broadcast(ShopAndGamePackets.getMatchCardStart(game, game.getLoser()));
                    chr.getMap().broadcastMessage(ShopAndGamePackets.addMatchCardBox(game.getOwner(), 2, 1));
                }
            }
            case GIVE_UP: {
                MapleMiniGame game = chr.getMapleMiniGame();
                if (game.getGameType().equals("omok")) {
                    if (game.isOwner(chr)) {
                        game.broadcast(ShopAndGamePackets.getMiniGameOwnerForfeit(game));
                    } else {
                        game.broadcast(ShopAndGamePackets.getMiniGameVisitorForfeit(game));
                    }
                }
                if (game.getGameType().equals("matchcard")) {
                    if (game.isOwner(chr)) {
                        game.broadcast(ShopAndGamePackets.getMatchCardVisitorWin(game));
                    } else {
                        game.broadcast(ShopAndGamePackets.getMatchCardOwnerWin(game));
                    }
                }
            }
            case REQUEST_TIE: {
                MapleMiniGame game = chr.getMapleMiniGame();
                if (game.isOwner(chr)) {
                    game.broadcastToVisitor(ShopAndGamePackets.getMiniGameRequestTie(game));
                } else {
                    game.getOwner().getClient().getSession().write(ShopAndGamePackets.getMiniGameRequestTie(game));
                }
            }
            case ANSWER_TIE: {
                MapleMiniGame game = chr.getMapleMiniGame();
                slea.readByte();
                if (game.getGameType().equals("omok")) {
                    game.broadcast(ShopAndGamePackets.getMiniGameTie(game));
                }
                if (game.getGameType().equals("matchcard")) {
                    game.broadcast(ShopAndGamePackets.getMatchCardTie(game));
                }
            }
            case SKIP: {
                MapleMiniGame game = chr.getMapleMiniGame();
                if (game.isOwner(chr)) {
                    game.broadcast(ShopAndGamePackets.getMiniGameSkipOwner(game));
                } else {
                    game.broadcast(ShopAndGamePackets.getMiniGameSkipVisitor(game));
                }
            }
            case MOVE_OMOK: {
                int x = slea.readInt(); // x point
                int y = slea.readInt(); // y point
                int type = slea.readByte(); // piece ( 1 or 2; Owner has one piece, visitor has another, it switches every game.)
                chr.getMapleMiniGame().setPiece(x, y, type, chr);
            }
            case SELECT_CARD: {
                int turn = slea.readByte(); // 1st turn = 1; 2nd turn = 0
                int slot = slea.readByte(); // slot
                MapleMiniGame game = chr.getMapleMiniGame();
                int firstslot = game.getFirstSlot();
                if (turn == 1) {
                    game.setFirstSlot(slot);
                    if (game.isOwner(chr)) {
                        game.broadcastToVisitor(ShopAndGamePackets.getMatchCardSelect(game, turn, slot, firstslot, turn));
                    } else {
                        game.getOwner().getClient().getSession().write(ShopAndGamePackets.getMatchCardSelect(game, turn, slot, firstslot, turn));
                    }
                } else if ((game.getCardId(firstslot + 1)) == (game.getCardId(slot + 1))) {
                    if (game.isOwner(chr)) {
                        game.broadcast(ShopAndGamePackets.getMatchCardSelect(game, turn, slot, firstslot, 2));
                        game.setOwnerPoints();
                    } else {
                        game.broadcast(ShopAndGamePackets.getMatchCardSelect(game, turn, slot, firstslot, 3));
                        game.setVisitorPoints();
                    }
                } else if (game.isOwner(chr)) {
                    game.broadcast(ShopAndGamePackets.getMatchCardSelect(game, turn, slot, firstslot, 0));
                } else {
                    game.broadcast(ShopAndGamePackets.getMatchCardSelect(game, turn, slot, firstslot, 1));
                }
            }
            case SET_MESO: {
                final MapleTrade trade = chr.getTrade();
                final int meso = slea.readInt();
                if(meso < 0){
                    AutobanManager.getInstance().autoban(c, "Trading negative Meso");
                } else if (trade != null) {
		    trade.setMeso(meso);
		}
		break;
            }
            case SET_ITEMS: {
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                MapleInventoryType ivType = MapleInventoryType.getByType(slea.readByte());
                IItem item = chr.getInventory(ivType).getItem((byte) slea.readShort());
                short quantity = slea.readShort();
                byte targetSlot = slea.readByte();
                if (chr.getTrade() != null) {
                    if ((quantity <= item.getQuantity() && quantity >= 0) || InventoryConstants.isRechargable(item.getItemId())) {
                        if (ii.isDropRestricted(item.getItemId()) && SpecialStuff.getInstance().canStoreTradeDrop(item, chr)) { // ensure that undroppable items do not make it to the trade window
                            c.getSession().write(MaplePacketCreator.enableActions());
                            return;
                        }
                        IItem tradeItem = item.copy();
                        if (InventoryConstants.isRechargable(item.getItemId())) {
                            tradeItem.setQuantity(item.getQuantity());
                            MapleInventoryManipulator.removeFromSlot(c, ivType, item.getPosition(), item.getQuantity(), true);
                        } else {
                            tradeItem.setQuantity(quantity);
                            MapleInventoryManipulator.removeFromSlot(c, ivType, item.getPosition(), quantity, true);
                        }
                        tradeItem.setPosition(targetSlot);
                        chr.getTrade().addItem(tradeItem);
                        return;
                    }
                }
            }
            case CONFIRM: {
                if (chr.getTrade() != null) {
		    MapleTrade.completeTrade(chr);
		}
		break;
            }
            case ADD_ITEM:
            case PUT_ITEM:
            {
                final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
		final byte slot = (byte) slea.readShort();
		final short bundles = slea.readShort(); // How many in a bundle
		final short perBundle = slea.readShort(); // Price per bundle
		final int price = slea.readInt();

		if (price <= 0 || bundles <= 0 || perBundle <= 0) {
		    return;
		}
		final IMaplePlayerShop shop = chr.getPlayerShop();

		if (shop == null || !shop.isOwner(chr)) {
		    return;
		}
		final IItem ivItem = chr.getInventory(type).getItem(slot);

		if (ivItem != null) {
		    final short bundles_perbundle = (short) (bundles * perBundle);
		    if (bundles_perbundle < 0) { // int_16 overflow
			return;
		    }
		    if (ivItem.getQuantity() >= bundles_perbundle) {
			final byte flag = ivItem.getFlag();

			if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
			    c.getSession().write(MaplePacketCreator.enableActions());
			    return;
			}
			if (MapleItemInformationProvider.getInstance().isDropRestricted(ivItem.getItemId())) {
			    if (!(ItemFlag.KARMA_EQ.check(flag) || ItemFlag.KARMA_USE.check(flag))) {
				c.getSession().write(MaplePacketCreator.enableActions());
				return;
			    }
			}
			if (InventoryConstants.isThrowingStar(ivItem.getItemId()) || InventoryConstants.isBullet(ivItem.getItemId())) {
			    // Ignore the bundles
			    MapleInventoryManipulator.removeFromSlot(c, type, slot, ivItem.getQuantity(), true);

			    final IItem sellItem = ivItem.copy();
			    shop.addItem(new MaplePlayerShopItem(sellItem, (short) 1, price));
			} else {
			    MapleInventoryManipulator.removeFromSlot(c, type, slot, bundles_perbundle, true);

			    final IItem sellItem = ivItem.copy();
			    sellItem.setQuantity(perBundle);
			    shop.addItem(new MaplePlayerShopItem(sellItem, bundles, price));
			}
			c.getSession().write(PlayerShopPacket.shopItemUpdate(shop));
		    }
		}
		break;
	    }
            case REMOVE_ITEM: {
                final int slot = slea.readShort();
		final IMaplePlayerShop shop = chr.getPlayerShop();

		if (shop == null || !shop.isOwner(chr)) {
		    return;
		}
		final MaplePlayerShopItem item = shop.getItems().get(slot);

		if (item != null) {
		    if (item.bundles > 0) {
			IItem item_get = item.item.copy();
			item_get.setQuantity((short) (item.bundles * item.item.getQuantity()));
			if (MapleInventoryManipulator.addFromDrop(c, item_get, false)) {
			    item.bundles = 0;
			    shop.removeFromSlot(slot);
			}
		    }
		}
		c.getSession().write(PlayerShopPacket.shopItemUpdate(shop));
		break;
	    }
            case BUY:
            case MERCHANT_BUY: {
                final int item = slea.readByte();
		final short quantity = slea.readShort();
		final IMaplePlayerShop shop = chr.getPlayerShop();

		if (shop == null || shop.isOwner(chr)) {
		    return;
		}
		final MaplePlayerShopItem tobuy = shop.getItems().get(item);

		if (quantity < 0
			|| tobuy == null
			|| tobuy.bundles < quantity
			|| (tobuy.bundles % quantity != 0 && InventoryConstants.isEquip(tobuy.item.getItemId())) // Buying
			|| ((short) (tobuy.bundles * quantity)) < 0
			|| (quantity * tobuy.price) < 0
			|| quantity * tobuy.item.getQuantity() < 0
			|| chr.getMeso() - (quantity * tobuy.price) < 0
			|| shop.getMeso() + (quantity * tobuy.price) < 0) {
		    return;
		}
		shop.buy(c, item, quantity);
		shop.broadcastToVisitors(PlayerShopPacket.shopItemUpdate(shop));
		break;
            }
            case TAKE_ITEM_BACK: {
		final int slot = slea.readShort();
		final IMaplePlayerShop shop = chr.getPlayerShop();

		if (shop == null || !shop.isOwner(chr)) {
		    return;
		}
		final MaplePlayerShopItem item = shop.getItems().get(slot);

		if (item != null) {
		    if (item.bundles > 0) {
			IItem item_get = item.item.copy();
			item_get.setQuantity((short) (item.bundles * item.item.getQuantity()));
			if (MapleInventoryManipulator.addFromDrop(c, item_get, false)) {
			    item.bundles = 0;
			    shop.removeFromSlot(slot);
			}
		    }
		}
		c.getSession().write(PlayerShopPacket.shopItemUpdate(shop));
		break;
	    }
            case CLOSE_MERCHANT: {
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                    boolean save = false;

                    if (chr.getMeso() + merchant.getMeso() < 0) {
                        save = true;
                    } else {
                        if (merchant.getMeso() > 0) {
                            chr.gainMeso(merchant.getMeso(), false);
                        }
                        merchant.setMeso(0);

                        if (merchant.getItems().size() > 0) {
                            for (MaplePlayerShopItem items : merchant.getItems()) {
                                if (items.bundles > 0) {
                                    IItem item_get = items.item.copy();
                                    item_get.setQuantity((short) (items.bundles * items.item.getQuantity()));
                                    if (MapleInventoryManipulator.addFromDrop(c, item_get, false)) {
                                        items.bundles = 0;
                                    } else {
                                        save = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    c.getSession().write(PlayerShopPacket.shopErrorMessage(0x10, 0));
                    merchant.closeShop(save, true);
                    chr.setPlayerShop(null);
                }
            }            
            case BAN_PLAYER: {
                if (chr.getPlayerShop() != null && chr.getPlayerShop().isOwner(chr)) {
                    if(chr.getPlayerShop().getShopType() == 2){
                        MaplePlayerShop  shop = (MaplePlayerShop) chr.getPlayerShop();
                        shop.banPlayer(slea.readMapleAsciiString());
                    }
                }
            }
            case MAINTENANCE_OFF: {
		final IMaplePlayerShop shop = chr.getPlayerShop();
		if (shop != null && shop instanceof HiredMerchant && shop.isOwner(chr)) {
		    shop.setOpen(true);
		    chr.setPlayerShop(null);
		}
		break;
	    }
            case MERCHANT_ORGANISE: {
		final IMaplePlayerShop imps = chr.getPlayerShop();
		if (imps.isOwner(chr)) {
		    for (int i = 0; i < imps.getItems().size(); i++) {
			if (imps.getItems().get(i).bundles == 0) {
			    imps.getItems().remove(i);
			}
		    }
		    if (chr.getMeso() + imps.getMeso() < 0) {
			c.getSession().write(PlayerShopPacket.shopItemUpdate(imps));
		    } else {
			chr.gainMeso(imps.getMeso(), false);
			imps.setMeso(0);
			c.getSession().write(PlayerShopPacket.shopItemUpdate(imps));
		    }
		}
		break;
	    }
        }
    }
}