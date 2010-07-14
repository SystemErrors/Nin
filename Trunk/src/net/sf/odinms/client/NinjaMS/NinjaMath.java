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
package net.sf.odinms.client.NinjaMS;

import net.sf.odinms.tools.Randomizer;

/**
 *
 * @author Admin
 */
public class NinjaMath {

    public static final boolean greaterAndLess(int x, int a, int b) {
        boolean bool = true;
        if (x < a) {
            bool = false;
        } else if (x > b) {
            bool = false;
        }
        return bool;
    }

    public static final boolean greaterOrEquals(int a, int b) {
        return a >= b;
    }

    public static final boolean lesserOrEquals(int a, int b) {
        return a <= b;
    }

    public static final boolean greater(int a, int b) {
        return a > b;
    }

    public static final boolean lesser(int a, int b) {
        return a < b;
    }

    public static final int random(int x) {
        return Randomizer.nextInt(x);
    }

     public static final int random(int a, int b) {
        int x = 0;
        if (greater(a, b)){
        x = (Randomizer.nextInt(b - a) + a);
        } else if (lesser(a, b)){
            x = (Randomizer.nextInt(a - b) + b);
        } else {
            x = a;
        }
        return x;
    }
}
