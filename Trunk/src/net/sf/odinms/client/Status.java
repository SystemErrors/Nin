/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.sf.odinms.client.NinjaMS.Processors.NoticeProcessor;
import net.sf.odinms.database.DatabaseConnection;

/**
 *
 * @author Owner
 */
public class Status {

    public static final String getName(final byte level) {
        switch (level) {
            case 0:
                return "Rookie";
            case 1:
                return "Genin";
            case 2:
                return "Chunin";
            case 3:
                return "Jounin";
            case 4:
                return "Sannin";
            case 5:
                return "Hokage";
            default:
                return "SystemError";

        }
    }

    public static final void setGMLevel(MapleCharacter c, byte lvl) {
        c.setGM(lvl);
        PreparedStatement ps = null;
        Connection con = DatabaseConnection.getConnection();
        try {
            ps = con.prepareStatement("UPDATE `accounts` SET `gm` = ? WHERE `id` = ?");
            ps.setByte(1, (byte) lvl);
            ps.setInt(2, c.getAccountID());
            ps.execute();
            NoticeProcessor.sendBlueNotice("[The Elite ninjaGang] We are glad to announce that " + c.getName() + " is now a " + getName(lvl));

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if(ps != null){
                try {
                    ps.close();
                } catch(SQLException ex){
                    ex.printStackTrace();
                }
            }
        }
    }
}
