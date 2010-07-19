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
package net.sf.odinms.server.constants;

import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Inventory.MapleWeaponType;
import net.sf.odinms.server.MapleItemInformationProvider;

public class InventoryConstants {

    public static final boolean isThrowingStar(final int itemId) {
        return itemId >= 2070000 && itemId < 2080000;
    }

    public static final boolean isBullet(final int itemId) {
        final int id = itemId / 10000;
        if (id == 233) {
            return true;
        } else {
            return false;
        }
    }

    public static final boolean isRechargable(final int itemId) {
        final int id = itemId / 10000;
        switch (id) {
            case 233:
            case 207:
                return true;
        }
        return false;
    }

    public static final boolean isOverall(final int itemId) {
        return itemId >= 1050000 && itemId < 1060000;
    }

    public static final boolean isPet(final int itemId) {
        return itemId >= 5000000 && itemId <= 5000100;
    }

    public static final boolean isArrowForCrossBow(final int itemId) {
        return itemId >= 2061000 && itemId < 2062000;
    }

    public static final boolean isArrowForBow(final int itemId) {
        return itemId >= 2060000 && itemId < 2061000;
    }

    public static final boolean isMagicWeapon(final int itemId) {
        final int s = itemId / 10000;
        return s == 137 || s == 138;
    }

    public static final boolean isWeapon(final int itemId) {
        return itemId >= 1302000 && itemId < 1492024;
    }

    public static final MapleInventoryType getInventoryType(final int itemId) {
        final byte type = (byte) (itemId / 1000000);
        if (type < 1 || type > 5) {
            return MapleInventoryType.UNDEFINED;
        }
        return MapleInventoryType.getByType(type);
    }

    public static final MapleWeaponType getWeaponType(final int itemId) {
        int cat = itemId / 10000;
        cat = cat % 100;
        switch (cat) {
            case 30:
                return MapleWeaponType.SWORD1H;
            case 31:
                return MapleWeaponType.AXE1H;
            case 32:
                return MapleWeaponType.BLUNT1H;
            case 33:
                return MapleWeaponType.DAGGER;
            case 37:
                return MapleWeaponType.WAND;
            case 38:
                return MapleWeaponType.STAFF;
            case 40:
                return MapleWeaponType.SWORD2H;
            case 41:
                return MapleWeaponType.AXE2H;
            case 42:
                return MapleWeaponType.BLUNT2H;
            case 43:
                return MapleWeaponType.SPEAR;
            case 44:
                return MapleWeaponType.POLE_ARM;
            case 45:
                return MapleWeaponType.BOW;
            case 46:
                return MapleWeaponType.CROSSBOW;
            case 47:
                return MapleWeaponType.CLAW;
            case 48:
                return MapleWeaponType.KNUCKLE;
            case 49:
                return MapleWeaponType.GUN;
        }
        return MapleWeaponType.NOT_A_WEAPON;
    }

    public static final boolean isShield(final int itemId) {
        int cat = itemId / 10000;
        cat = cat % 100;
        return cat == 9;
    }

    public static final boolean isEquip(final int itemId) {
        return itemId / 1000000 == 1;
    }

    public static final boolean isCleanSlate(final int scrollId) {
        switch (scrollId) {
            case 2049000:
            case 2049001:
            case 2049002:
            case 2049003:
                return true;
        }
        return false;
    }

    public static final boolean isChaosScroll(final int scrollId) {
        switch (scrollId) {
            case 2049100: // Chaos Scroll
            case 2049101: // Liar's Wood Liquid
            case 2049102: // Maple Syrup
            case 2049104: // Angent Equipmenet scroll
            case 2049103: // Beach Sandals Scroll
                return true;
        }
        return false;
    }

    public static final boolean isSpecialScroll(final int scrollId) {
        switch (scrollId) {
            case 2040727: // Spikes on show
            case 2041058: // Cape for Cold protection
                return true;
        }
        return false;
    }

    public static final boolean isTwoHanded(final int itemId) {
        switch (getWeaponType(itemId)) {
            case AXE2H:
            case GUN:
            case KNUCKLE:
            case BLUNT2H:
            case BOW:
            case CLAW:
            case CROSSBOW:
            case POLE_ARM:
            case SPEAR:
            case SWORD2H:
                return true;
            default:
                return false;
        }
    }

    public static final boolean isTownScroll(final int id) {
        return id >= 2030000 && id < 2030020;
    }

    public static final boolean isGun(final int id) {
        return id >= 1492000 && id <= 1492024;
    }

    public static final boolean isUse(final int id) {
        return id >= 2000000 && id <= 2490000;
    }

    public static final boolean isSummonSack(final int id) {
        return id / 10000 == 210;
    }

    public static final boolean isMonsterCard(final int id) {
        return id / 10000 == 238;
    }

    public static final boolean isSpecialCard(final int id) {
        return id / 100 >= 2388;
    }

    public static final int getCardShortId(final int id) {
        return id % 10000;
    }

    public static final boolean isGem(final int id) {
        return id >= 4250000 && id <= 4251402;
    }

    public static final boolean isCashEquip(int itemId) {
        return MapleItemInformationProvider.getInstance().isCashEquip(itemId);
    }

    public static final byte gachaponRareItem(int itemId) {
        return 1;
    }
}

   