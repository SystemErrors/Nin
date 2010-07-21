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

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class NotProcessedPacketsHandler {

    public final static void createCygnus(MapleClient c) {
        AutobanManager.getInstance().autoban(c, "Creating Cygnus in our server in GMS way");
    }

    public static void strangeData(MapleClient c) {
        // Do nothing. :) 
    }

    public static void player_DC(MapleClient c) {
        // do nothing
    }

    public static void EnergyChargeAttack() {
        // Do nothing.
    }

    public static void useTelePortRock() {
        // Do nothing.
    }

    public static void spouseChat() {
        // do nothing
    }

    public static void adminLog() {
        // Do nothing.
    }

    public static void openFamily() {
        // do nth
    }

    public static void pet_Talk() {
        //do nth
    }

    public static void couponCode() {
        // do nth
    }

    public static void mtsOp() {
        // do nth
    }
}
