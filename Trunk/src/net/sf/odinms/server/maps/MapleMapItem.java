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
 * MapleMapItem.java
 *
 * Created on 29. November 2007, 12:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package net.sf.odinms.server.maps;

import java.awt.Point;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Matze
 */
public class MapleMapItem extends AbstractMapleMapObject {

    protected IItem item;
    protected MapleMapObject dropper;
    protected int owner, meso, questid = -1;
    protected byte type;
    protected boolean pickedUp = false, playerDrop;
    /** Creates a new instance of MapleMapItem */
    public MapleMapItem(IItem item, Point position, MapleMapObject dropper, MapleCharacter owner, byte type, boolean playerDrop) {
	setPosition(position);
	this.item = item;
	this.dropper = dropper;
	this.owner = owner.getId();
	this.meso = 0;
	this.type = type;
	this.playerDrop = playerDrop;
    }

    public MapleMapItem(IItem item, Point position, MapleMapObject dropper, MapleCharacter owner, byte type, boolean playerDrop, int questid) {
	setPosition(position);
	this.item = item;
	this.dropper = dropper;
	this.owner = owner.getId();
	this.meso = 0;
	this.type = type;
	this.playerDrop = playerDrop;
	this.questid = questid;
    }

    public MapleMapItem(int meso, Point position, MapleMapObject dropper, MapleCharacter owner, byte type, boolean playerDrop) {
	setPosition(position);
	this.item = null;
	this.dropper = dropper;
	this.owner = owner.getId();
	this.meso = meso;
	this.type = type;
	this.playerDrop = playerDrop;
    }

    public final IItem getItem() {
	return item;
    }

    public final int getQuest() {
	return questid;
    }

    public final int getItemId() {
	if (getMeso() > 0) {
	    return meso;
	}
	return item.getItemId();
    }

    public final MapleMapObject getDropper() {
	return dropper;
    }

    public final int getOwner() {
	return owner;
    }

    public final int getMeso() {
	return meso;
    }

    public final boolean isPlayerDrop() {
	return playerDrop;
    }

    public final boolean isPickedUp() {
	return pickedUp;
    }

    public void setPickedUp(final boolean pickedUp) {
	this.pickedUp = pickedUp;
    }

    public byte getDropType() {
	return type;
    }

    @Override
    public final MapleMapObjectType getType() {
	return MapleMapObjectType.ITEM;
    }

    @Override
    public void sendSpawnData(final MapleClient client) {
	if (questid <= 0 || client.getPlayer().getQuestStatus(questid) == 1) {
	    client.getSession().write(MaplePacketCreator.dropItemFromMapObject(this, null, getPosition(), (byte) 2));
	}
    }

    @Override
    public void sendDestroyData(final MapleClient client) {
	client.getSession().write(MaplePacketCreator.removeItemFromMap(getObjectId(), 1, 0));
    }
}