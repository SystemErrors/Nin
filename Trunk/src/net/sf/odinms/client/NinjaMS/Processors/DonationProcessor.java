package net.sf.odinms.client.NinjaMS.Processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;

public class DonationProcessor {

    public static void doDonation(MapleClient c, String name, short damount, String email) {
        ChannelServer cserv = c.getChannelServer();
        try {
            WorldLocation loc = cserv.getWorldInterface().getLocation(name);
            if (loc != null) {
                MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(name);
                if (victim == null) {
                    c.dropMessage("Player " + name + " does not exist.");
                    return;
                }
                // first add points...
                victim.modifyDAmount(damount);
                victim.modifyDPoints(damount);
                victim.showMessage(1, "[TheBass] Thank you for donating $" + damount + " to FarmerStory. A total of " + victim.getDPoints() + " Donator Points has been set for you by default.\r\n\r\nSee your chatbox if you have any extra functions given to you.");
                c.dropMessage("[TheBass] Successfully distributed " + damount + " to " + victim.getName());
                // extra point at one time!
                short extrapoints = 0;
                for (int i = 10; i <= damount; i += 10) {
                    extrapoints += 1;
                }
                if (extrapoints > 0) {
                    victim.modifyDPoints(extrapoints);
                    victim.showMessage(5, "[TheBass] You have gained " + extrapoints + " extra Donator Points. (You get 1 more for every $10 you donate at once.)");
                    c.dropMessage("[TheBass] Successfully distributed " + extrapoints + " extra donator point because of higher donation amount to " + victim.getName());
                }
                // rewards package
                if (victim.getDAmount() >= 20 && !victim.isGenin()) {
                    victim.setGMStatus(1);
                    victim.showMessage(5, "[TheBass]You have successfully gained Donator Status! See #commands for your commands!");
                    c.dropMessage("[TheBass]" + victim.getName() + " has gained donator points.");
                }
                donationCheck(c, victim, damount, email);
                // complete
                victim.showMessage(5, "[TheBass]Donation process complete.");
                c.dropMessage("[TheBass]Donation process complete.");
                victim.forceSave(true, false);
            } else {
                c.dropMessage("[Anbu] Ninja named '" + name + "' does not exist, is CCing, or is offline.");
            }
        } catch (Exception e) {
            c.dropMessage("[Anbu] Ninja named '" + name + "' does not exist, is CCing, or is offline.");
        }
    }

    public static void donationCheck(MapleClient c, MapleCharacter victim, int damount, String email) {
        if (!victim.isJounin()) { // not in donationcheck.java. maybe someday i aint a lazy asshole
            c.dropMessage("Processing Donation into MySQL...");
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("INSERT INTO donations (accountid, ign, damount, email, gmacc) VALUES (?, ?, ?, ?, ?)");
                ps.setInt(1, victim.getClient().getAccID());
                ps.setString(2, victim.getName());
                ps.setInt(3, damount);
                ps.setString(4, email);
                ps.setString(5, c.getAccountName());
                ps.executeUpdate();
                ps.close();
                c.dropMessage("Stored into MySQL Database Successfully.");
            } catch (SQLException e) {
                c.dropMessage("Error: " + e.getErrorCode());
                c.dropMessage("Statement: " + e.getSQLState());
            }
        }
    }

    public static void setDAmount(MapleClient c, String name, short amt) {
        ChannelServer cserv = c.getChannelServer();
        try {
            WorldLocation loc = cserv.getWorldInterface().getLocation(name);
            if (loc != null) {
                MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(name);
                if (victim == null) {
                    victim.dropMessage("Player " + name + " does not exist.");
                    return;
                }
                short lol = (short) (amt - victim.getDAmount());
                victim.modifyDAmount(lol);
                c.dropMessage("[TheBass] The Ninja " + name + "'s damount has been set to " + amt);
            } else {
                c.dropMessage("[Anbu] Ninja named '" + name + "' does not exist, is CCing, or is offline.");
            }
        } catch (Exception e) {
            c.dropMessage("[Anbu] Ninja named '" + name + "' does not exist, is CCing, or is offline.");
        }
    }

    public static void setDPoints(MapleClient c, String name, short amt) {
        ChannelServer cserv = c.getChannelServer();
        try {
            WorldLocation loc = cserv.getWorldInterface().getLocation(name);
            if (loc != null) {
                MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(name);
                if (victim == null) {
                    victim.dropMessage("Player " + name + " does not exist.");
                    return;
                }
                short lol = (short) (amt - victim.getDPoints());
                victim.modifyDPoints(lol);
                c.dropMessage("[TheBass] The Ninja " + name + "'s dpoints has been set to " + amt);
            } else {
                c.dropMessage("[Anbu] Ninja named '" + name + "' does not exist, is CCing, or is offline.");
            }
        } catch (Exception e) {
            c.dropMessage("[Anbu] Ninja named '" + name + "' does not exist, is CCing, or is offline.");
        }
    }
}
