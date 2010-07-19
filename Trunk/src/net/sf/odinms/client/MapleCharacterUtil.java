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
package net.sf.odinms.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;
import net.sf.odinms.database.DatabaseConnection;

public class MapleCharacterUtil {

    private static final Pattern namePattern = Pattern.compile("[a-zA-Z0-9_-]{3,12}");
    private static final Pattern petPattern = Pattern.compile("[a-zA-Z0-9_-]{4,12}");

    public static boolean canCreateChar(String name) {
        if (!isNameLegal(name)) {
            return false;
        }
        if (MapleCharacter.getIdByName(name) != -1) {
            return false;
        }
        return true;
    }

    public static final boolean canChangePetName(final String name) {
        if (petPattern.matcher(name).matches()) {
            return true;
        }
        return false;
    }

    public static boolean isNameLegal(String name) {
        if (name.length() < 3 || name.length() > 12) {
            return false;
        }

        return namePattern.matcher(name).matches();
    }

    public static String makeMapleReadable(String in) {
        String wui = in.replace('I', 'i');
        wui = wui.replace('l', 'L');
        wui = wui.replace("rn", "Rn");
        wui = wui.replace("vv", "Vv");
        wui = wui.replace("VV", "Vv");
        return wui;
    }

    public static final int getIdByName(final String name) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT id FROM characters WHERE name = ?");
            ps.setString(1, name);
            final ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            final int id = rs.getInt("id");
            rs.close();
            ps.close();

            return id;
        } catch (SQLException e) {
            System.err.println("error 'getIdByName' " + e);
        }
        return -1;
    }
}
