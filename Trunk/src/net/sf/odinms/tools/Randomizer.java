/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.sf.odinms.tools;
import java.util.Random;

public class Randomizer {

    private final static Random rand = new Random();

    public static final int nextInt() {
	return rand.nextInt();
    }

    public static final int nextInt(final int arg0) {
	return rand.nextInt(arg0);
    }

    public static final void nextBytes(final byte[] bytes) {
	rand.nextBytes(bytes);
    }

    public static final boolean nextBoolean() {
	return rand.nextBoolean();
    }

    public static final double nextDouble() {
	return rand.nextDouble();
    }

    public static final float nextFloat() {
	return rand.nextFloat();
    }

    public static final long nextLong() {
	return rand.nextLong();
    }

    public static final int rand(final int lbound, final int ubound) {
	return (int) ((rand.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
}