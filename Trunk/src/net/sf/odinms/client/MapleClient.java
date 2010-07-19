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

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import javax.script.ScriptEngine;

import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.database.DatabaseException;
import net.sf.odinms.net.CashShop.CashShopServer;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.login.LoginServer;
import net.sf.odinms.net.world.MapleMessengerCharacter;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.PartyOperation;
import net.sf.odinms.net.world.guild.MapleGuildCharacter;
import net.sf.odinms.scripting.npc.NPCConversationManager;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.shops.IMaplePlayerShop;
import net.sf.odinms.tools.FileoutputUtil;
import net.sf.odinms.tools.IPAddressTool;
import net.sf.odinms.tools.MapleAESOFB;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Packets.LoginPacket;

import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapleClient {

    public static final transient byte LOGIN_NOTLOGGEDIN = 0,
	    LOGIN_SERVER_TRANSITION = 1,
	    LOGIN_LOGGEDIN = 2,
	    LOGIN_WAITING = 3,
	    CASH_SHOP_TRANSITION = 4,
	    LOGIN_CS_LOGGEDIN = 5,
	    CHANGE_CHANNEL = 6;
    public static final String CLIENT_KEY = "CLIENT";
    private static final Logger log = LoggerFactory.getLogger(MapleClient.class);
    private MapleAESOFB send;
    private MapleAESOFB receive;
    private IoSession session;
    private MapleCharacter player;
    private int channel = 1;
    private int accId = 1;
    private boolean loggedIn = false;
    private boolean serverTransition = false;
    private Calendar birthday = null;
    private Calendar tempban = null;
    private String accountName;
    private int world;
    private long lastPong;
    private boolean gm;
    private int gmlevel;
    private byte greason = 1;
    private Set<String> macs = new HashSet<String>();
    private Map<String, ScriptEngine> engines = new HashMap<String, ScriptEngine>();
    private ScheduledFuture<?> idleTask = null;
    public transient short loginAttempt = 0;
    private transient List<Integer> allowedChar = new LinkedList<Integer>();

    public MapleClient(MapleAESOFB send, MapleAESOFB receive, IoSession session) {
        this.send = send;
        this.receive = receive;
        this.session = session;
    }

    public MapleAESOFB getReceiveCrypto() {
        return receive;
    }

    public MapleAESOFB getSendCrypto() {
        return send;
    }

    public IoSession getSession() {
        return session;
    }

    public MapleCharacter getPlayer() {
        return player;
    }

    public void setPlayer(MapleCharacter player) {
        this.player = player;
    }

    public void sendCharList(int server) {
        this.session.write(LoginPacket.getCharList(this, server));
    }

    public final List<MapleCharacter> loadCharacters(final int serverId) { // TODO make this less costly zZz
        final List<MapleCharacter> chars = new LinkedList<MapleCharacter>();
        for (final CharNameAndId cni : loadCharactersInternal(serverId)) {            
                final MapleCharacter chr = MapleCharacter.loadCharFromDB(cni.id, this, false);
                chars.add(chr);
                allowedChar.add(chr.getId());           
        }
        return chars;
    }

    public List<String> loadCharacterNames(int serverId) {
        List<String> chars = new LinkedList<String>();
        for (CharNameAndId cni : loadCharactersInternal(serverId)) {
            chars.add(cni.name);
        }
        return chars;
    }

    private List<CharNameAndId> loadCharactersInternal(int serverId) {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        List<CharNameAndId> chars = new LinkedList<CharNameAndId>();
        try {
            ps = con.prepareStatement("SELECT id, name FROM characters WHERE accountid = ? AND world = ?");
            ps.setInt(1, this.accId);
            ps.setInt(2, serverId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                chars.add(new CharNameAndId(rs.getString("name"), rs.getInt("id")));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            log.error("THROW", e);
        }
        return chars;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    private Calendar getTempBanCalendar(ResultSet rs) throws SQLException {
        Calendar lTempban = Calendar.getInstance();
        long blubb = rs.getLong("tempban");
        if (blubb == 0) { // basically if timestamp in db is 0000-00-00
            lTempban.setTimeInMillis(0);
            return lTempban;
        }
        Calendar today = Calendar.getInstance();
        lTempban.setTimeInMillis(rs.getTimestamp("tempban").getTime());
        if (today.getTimeInMillis() < lTempban.getTimeInMillis()) {
            return lTempban;
        }
        lTempban.setTimeInMillis(0);
        return lTempban;
    }

    public Calendar getTempBanCalendar() {
        return tempban;
    }

    public byte getBanReason() {
        return greason;
    }

    public boolean hasBannedIP() {
        boolean ret = false;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM ipbans WHERE ? LIKE CONCAT(ip, '%')");
            ps.setString(1, session.getRemoteAddress().toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                ret = true;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            log.error("Error checking ip bans", ex);
        }
        return ret;
    }

    public boolean hasBannedMac() {
        if (macs.isEmpty()) {
            return false;
        }
        boolean ret = false;
        int i = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM macbans WHERE mac IN (");
            for (i = 0; i < macs.size(); i++) {
                sql.append("?");
                if (i != macs.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(")");
            PreparedStatement ps = con.prepareStatement(sql.toString());
            i = 0;
            for (String mac : macs) {
                i++;
                ps.setString(i, mac);
            }
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                ret = true;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            log.error("Error checking mac bans", ex);
        }
        return ret;
    }

    private void loadMacsIfNescessary() throws SQLException {
        if (macs.isEmpty()) {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT macs FROM accounts WHERE id = ?");
            ps.setInt(1, accId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String[] macData = rs.getString("macs").split(", ");
                for (String mac : macData) {
                    if (!mac.equals("")) {
                        macs.add(mac);
                    }
                }
            } else {
                throw new RuntimeException("No valid account associated with this client.");
            }
            rs.close();
            ps.close();
        }
    }

    public void banMacs() {
        Connection con = DatabaseConnection.getConnection();
        try {
            loadMacsIfNescessary();
            List<String> filtered = new LinkedList<String>();
            PreparedStatement ps = con.prepareStatement("SELECT filter FROM macfilters");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                filtered.add(rs.getString("filter"));
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("INSERT INTO macbans (mac) VALUES (?)");
            for (String mac : macs) {
                boolean matched = false;
                for (String filter : filtered) {
                    if (mac.matches(filter)) {
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    ps.setString(1, mac);
                    try {
                        ps.executeUpdate();
                    } catch (SQLException e) {
                        // can fail because of UNIQUE key, we dont care
                    }
                }
            }
            ps.close();
        } catch (SQLException e) {
            log.error("Error banning MACs", e);
        }
    }
  

    public int finishLogin() {
        synchronized (MapleClient.class) {
            final byte state = getLoginState();
            if (state > MapleClient.LOGIN_NOTLOGGEDIN && state != MapleClient.LOGIN_WAITING) { // already loggedin
                loggedIn = false;
                return 7;
            }
            updateLoginState(MapleClient.LOGIN_LOGGEDIN, null);
        }
        return 0;
    }

    public int login(String login, String pwd, boolean ipMacBanned) {
        int loginok = 5;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int banned = rs.getInt("banned");
                accId = rs.getInt("id");
                String passhash = rs.getString("pwd");
                String salt = rs.getString("salt");
                gmlevel = rs.getInt("gm");
                gm = gmlevel >= 3;
                greason = rs.getByte("greason");
                tempban = getTempBanCalendar(rs);
                //	    gender = rs.getByte("gender");
                //	    pin = rs.getInt("pin");
                if ((banned == 0 && !ipMacBanned) || banned == -1) {
                    PreparedStatement ips = con.prepareStatement("INSERT INTO iplog (accountid, ip) VALUES (?, ?)");
                    ips.setInt(1, accId);
                    String sockAddr = session.getRemoteAddress().toString();
                    ips.setString(2, sockAddr.substring(1, sockAddr.lastIndexOf(':')));
                    ips.executeUpdate();
                    ips.close();
                }
                ps.close();
                if (banned == 1) {
                    loginok = 3;
                } else {
                    if (banned == -1) {
                        unban();
                    }
                    if (getLoginState() > MapleClient.LOGIN_NOTLOGGEDIN) { // already loggedin
                        loggedIn = false;
                        loginok = 7;
                        if (pwd.equalsIgnoreCase("fixme")) {
                            try {
                                ps = con.prepareStatement("UPDATE accounts SET loggedin = 0 WHERE name = ?");
                                ps.setString(1, login);
                                ps.executeUpdate();
                                ps.close();
                            } catch (SQLException se) {
                                se.printStackTrace();
                            }
                        } else {
                            showMessage("It seems like you are stuck in game. Type \'fixme\' instead of password and press login to get unstuck");
                        }
                    } else {
                        boolean updatePasswordHash = false;
                        // Check if the passwords are correct here. :B
                        if (LoginCryptoLegacy.isLegacyPassword(passhash) && LoginCryptoLegacy.checkPassword(pwd, passhash)) {
                            // Check if a password upgrade is needed.
                            loginok = 0;
                            updatePasswordHash = true;
                        } else if (salt == null && LoginCrypto.checkSha1Hash(passhash, pwd)) {
                            loginok = 0;
                            updatePasswordHash = true;
                        } else if (LoginCrypto.checkSaltedSha512Hash(passhash, pwd, salt)) {
                            loginok = 0;
                        } else {
                            loggedIn = false;
                            loginok = 4;
                        }
                        if (updatePasswordHash) {
                            PreparedStatement pss = con.prepareStatement("UPDATE `accounts` SET `pwd` = ?, `salt` = ? WHERE id = ?");
                            try {
                                String newSalt = LoginCrypto.makeSalt();
                                pss.setString(1, LoginCrypto.makeSaltedSha512Hash(pwd, newSalt));
                                pss.setString(2, newSalt);
                                pss.setInt(3, accId);
                                pss.executeUpdate();
                            } finally {
                                pss.close();
                            }
                        }
                    }
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("ERROR" + e);
        }
        return loginok;
    }

    /**
     * Gets the special server IP if the client matches a certain subnet.
     *
     * @param subnetInfo A <code>Properties</code> instance containing all the subnet info.
     * @param clientIPAddress The IP address of the client as a dotted quad.
     * @param channel The requested channel to match with the subnet.
     * @return <code>0.0.0.0</code> if no subnet matched, or the IP if the subnet matched.
     */
    public static String getChannelServerIPFromSubnet(String clientIPAddress, int channel) {
        long ipAddress = IPAddressTool.dottedQuadToLong(clientIPAddress);
        Properties subnetInfo = LoginServer.getInstance().getSubnetInfo();

        if (subnetInfo.contains("net.sf.odinms.net.login.subnetcount")) {
            int subnetCount = Integer.parseInt(subnetInfo.getProperty("net.sf.odinms.net.login.subnetcount"));
            for (int i = 0; i < subnetCount; i++) {
                String[] connectionInfo = subnetInfo.getProperty("net.sf.odinms.net.login.subnet." + i).split(":");
                long subnet = IPAddressTool.dottedQuadToLong(connectionInfo[0]);
                long channelIP = IPAddressTool.dottedQuadToLong(connectionInfo[1]);
                int channelNumber = Integer.parseInt(connectionInfo[2]);

                if (((ipAddress & subnet) == (channelIP & subnet)) && (channel == channelNumber)) {
                    return connectionInfo[1];
                }
            }
        }

        return "0.0.0.0";
    }

    private void unban() {
        int i;
        try {
            Connection con = DatabaseConnection.getConnection();
            loadMacsIfNescessary();
            StringBuilder sql = new StringBuilder("DELETE FROM macbans WHERE mac IN (");
            for (i = 0; i < macs.size(); i++) {
                sql.append("?");
                if (i != macs.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(")");
            PreparedStatement ps = con.prepareStatement(sql.toString());
            i = 0;
            for (String mac : macs) {
                i++;
                ps.setString(i, mac);
            }
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("DELETE FROM ipbans WHERE ip LIKE CONCAT(?, '%')");
            ps.setString(1, getSessionIPAddress());
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("UPDATE accounts SET banned = 0 WHERE id = ?");
            ps.setInt(1, accId);
            ps.executeUpdate();
            ps.close();
            showMessage("You have been unbanned...please wait. Please login again if neccessary.");
        } catch (SQLException e) {
            log.error("Error while unbanning", e);
        }
    }

    public void updateMacs(String macData) {
        for (String mac : macData.split(", ")) {
            macs.add(mac);
        }
        StringBuilder newMacData = new StringBuilder();
        Iterator<String> iter = macs.iterator();
        while (iter.hasNext()) {
            String cur = iter.next();
            newMacData.append(cur);
            if (iter.hasNext()) {
                newMacData.append(", ");
            }
        }
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET macs = ? WHERE id = ?");
            ps.setString(1, newMacData.toString());
            ps.setInt(2, accId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            log.error("Error saving MACs", e);
        }
    }

    public void setAccID(int id) {
        this.accId = id;
    }

    public int getAccID() {
        return this.accId;
    }

    public final void updateLoginState(final int newstate, final String SessionID) { // TODO hide?
	try {
	    Connection con = DatabaseConnection.getConnection();
	    PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = ?, SessionIP = ?, lastlogin = CURRENT_TIMESTAMP() WHERE id = ?");
	    ps.setInt(1, newstate);
	    ps.setString(2, SessionID);
	    ps.setInt(3, getAccID());
	    ps.executeUpdate();
	    ps.close();
	} catch (SQLException e) {
	    System.err.println("error updating login state" + e);
	}
	if (newstate == MapleClient.LOGIN_NOTLOGGEDIN || newstate == MapleClient.LOGIN_WAITING) {
	    loggedIn = false;
	    serverTransition = false;
	} else {
	    serverTransition = (newstate == MapleClient.LOGIN_SERVER_TRANSITION || newstate == MapleClient.CHANGE_CHANNEL);
	    loggedIn = !serverTransition;
	}
    }

    
    public byte getLoginState() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = con.prepareStatement("SELECT loggedin, lastlogin, UNIX_TIMESTAMP(birthday) as birthday FROM accounts WHERE id = ?");
            ps.setInt(1, getAccID());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
                throw new DatabaseException("Everything sucks");
            }
            birthday = Calendar.getInstance();
            long blubb = rs.getLong("birthday");
            if (blubb > 0) {
                birthday.setTimeInMillis(blubb * 1000);
            }
            byte state = rs.getByte("loggedin");

	    if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL) {
		if (rs.getTimestamp("lastlogin").getTime() + 20000 < System.currentTimeMillis()) { // connecting to chanserver timeout
		    state = MapleClient.LOGIN_NOTLOGGEDIN;
		    updateLoginState(state, null);
		}
	    }
	    rs.close();
	    ps.close();
	    if (state == MapleClient.LOGIN_LOGGEDIN) {
		loggedIn = true;
	    } else {
		loggedIn = false;
	    }
	    return state;
        } catch (SQLException e) {
            loggedIn = false;
            log.error("ERROR", e);
            throw new DatabaseException("Everything sucks", e);
        }
    }

    public boolean checkBirthDate(Calendar date) {
        if (date.get(Calendar.YEAR) == birthday.get(Calendar.YEAR)
                && date.get(Calendar.MONTH) == birthday.get(Calendar.MONTH)
                && date.get(Calendar.DAY_OF_MONTH) == birthday.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }
        return false;
    }

    public final void removalTask() {
	try {
	    if (!player.getAllBuffs().isEmpty()) {
		player.cancelAllBuffs_();
	    }
	    if (!player.getAllDiseases().isEmpty()) {
		player.dispelAllDebuffs();
	    }
	    if (player.getTrade() != null) {
		MapleTrade.cancelTrade(player.getTrade());
	    }
	    NPCScriptManager.getInstance().dispose(this);

	    if (player.getEventInstance() != null) {
		player.getEventInstance().playerDisconnected(player);
	    }
	    player.getCheatTracker().dispose();
	    if (player.getMap() != null) {
		player.getMap().removePlayer(player);
	    }

	    final IMaplePlayerShop shop = player.getPlayerShop();
	    if (shop != null) {
		shop.removeVisitor(player);
		if (shop.isOwner(player)) {
		    shop.setOpen(true);
		}
	    }
	} catch (final Throwable e) {
	    FileoutputUtil.outputFileError(FileoutputUtil.Acc_Stuck, e);
	}
    }

    public final void disconnect(final boolean RemoveInChannelServer, final boolean fromCS) {
	if (player != null && isLoggedIn()) {
	    removalTask();

	    player.saveToDB(true, fromCS);


	    if (!fromCS) {
		final ChannelServer ch = ChannelServer.getInstance(channel);

		try {
		    if (player.getMessenger() != null) {
			ch.getWorldInterface().leaveMessenger(player.getMessenger().getId(), new MapleMessengerCharacter(player));
			player.setMessenger(null);
		    }
		    if (player.getParty() != null) {
			final MaplePartyCharacter chrp = new MaplePartyCharacter(player);
			chrp.setOnline(false);
			ch.getWorldInterface().updateParty(player.getParty().getId(), PartyOperation.LOG_ONOFF, chrp);
		    }
		    if (!serverTransition && isLoggedIn()) {
			ch.getWorldInterface().loggedOff(player.getName(), player.getId(), channel, player.getBuddylist().getBuddyIds());
		    } else { // Change channel
			ch.getWorldInterface().loggedOn(player.getName(), player.getId(), channel, player.getBuddylist().getBuddyIds());
		    }
		    if (player.getGuildId() > 0) {
			ch.getWorldInterface().setGuildMemberOnline(player.getMGC(), false, -1);
		    }

		} catch (final RemoteException e) {
		    ch.reconnectWorld();
		    player.setMessenger(null);
		} catch (final Exception e) {
		    e.printStackTrace();
		    System.err.println(getLogMessage(this, "ERROR") + e);
		} finally {
		    if (RemoveInChannelServer && ch != null) {
			ch.removePlayer(player);
		    }
		    player = null;
		}
	    } else {
		final CashShopServer cs = CashShopServer.getInstance();
		try {
		    if (player.getParty() != null) {
			final MaplePartyCharacter chrp = new MaplePartyCharacter(player);
			chrp.setOnline(false);
			cs.getCSInterface().updateParty(player.getParty().getId(), PartyOperation.LOG_ONOFF, chrp);
		    }
		    if (!serverTransition && isLoggedIn()) {
			cs.getCSInterface().loggedOff(player.getName(), player.getId(), channel, player.getBuddylist().getBuddyIds());
		    } else { // Change channel
			cs.getCSInterface().loggedOn(player.getName(), player.getId(), channel, player.getBuddylist().getBuddyIds());
		    }
		    if (player.getGuildId() > 0) {
			cs.getCSInterface().setGuildMemberOnline(player.getMGC(), false, -1);
		    }

		} catch (final RemoteException e) {
		    cs.reconnectWorld();
		    player.setMessenger(null);
		} catch (final Exception e) {
		    e.printStackTrace();
		    System.err.println(getLogMessage(this, "ERROR") + e);
		} finally {
		    if (RemoveInChannelServer && cs != null) {
			cs.getPlayerStorage().deregisterPlayer(player);
		    }
		    player = null;
		}
	    }
	}
	if (!serverTransition && isLoggedIn()) {
	    updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN, null);
	}
    }

    public void dropDebugMessage(MessageCallback mc) {
        StringBuilder builder = new StringBuilder();
        builder.append("Connected: ");
        builder.append(getSession().isConnected());
        builder.append(" Closing: ");
        builder.append(getSession().isClosing());
        builder.append(" ClientKeySet: ");
        builder.append(getSession().getAttribute(MapleClient.CLIENT_KEY) != null);
        builder.append(" loggedin: ");
        builder.append(isLoggedIn());
        builder.append(" has char: ");
        builder.append(getPlayer() != null);
        mc.dropMessage(builder.toString());
    }

    /**
     * Undefined when not logged to a channel
     *
     * @return the channel the client is connected to
     */
    public int getChannel() {
        return channel;
    }

    /**
     * Convinence method to get the ChannelServer object this client is logged
     * on to.
     *
     * @return The ChannelServer instance of the client.
     */
    public ChannelServer getChannelServer() {
        return ChannelServer.getInstance(getChannel());
    }

     public final boolean deleteCharacter(final int cid) {
	try {
	    final Connection con = DatabaseConnection.getConnection();
	    PreparedStatement ps = con.prepareStatement("SELECT id, guildid, guildrank, name FROM characters WHERE id = ? AND accountid = ?");
	    ps.setInt(1, cid);
	    ps.setInt(2, accId);
	    ResultSet rs = ps.executeQuery();

	    if (!rs.next()) {
		rs.close();
		ps.close();
		return false;
	    }
	    if (rs.getInt("guildid") > 0) { // is in a guild when deleted
		final MapleGuildCharacter mgc = new MapleGuildCharacter(cid, (short) 0, rs.getString("name"), (byte) -1, 0, rs.getInt("guildrank"), rs.getInt("guildid"), false);
		try {
		    LoginServer.getInstance().getWorldInterface().deleteGuildCharacter(mgc);
		} catch (RemoteException e) {
		    return false;
		}
	    }
	    rs.close();
	    ps.close();

	    ps = con.prepareStatement("DELETE FROM characters WHERE id = ?");
	    ps.setInt(1, cid);
	    ps.executeUpdate();
	    ps.close();

	    ps = con.prepareStatement("DELETE FROM cheatlog WHERE characterid = ?");
	    ps.setInt(1, cid);
	    ps.executeUpdate();
	    ps.close();

	    ps = con.prepareStatement("DELETE FROM hiredmerch WHERE characterid = ?");
	    ps.setInt(1, cid);
	    ps.executeUpdate();
	    ps.close();

	    ps = con.prepareStatement("DELETE FROM mountdata WHERE characterid = ?");
	    ps.setInt(1, cid);
	    ps.executeUpdate();
	    ps.close();

	    ps = con.prepareStatement("DELETE FROM monsterbook WHERE charid = ?");
	    ps.setInt(1, cid);
	    ps.executeUpdate();
	    ps.close();

	    return true;
	} catch (final SQLException e) {
	    System.err.println("DeleteChar error" + e);
	}
	return false;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getWorld() {
        return world;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    public void pongReceived() {
        lastPong = System.currentTimeMillis();
    }

    public void sendPing() {
        final long then = System.currentTimeMillis();
        getSession().write(LoginPacket.getPing());
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                try {
                    if (lastPong - then < 0) {
                        if (getSession().isConnected()) {
                            log.info(getLogMessage(MapleClient.this, "Autodc"));
                            getSession().close();
                        }
                    }
                } catch (NullPointerException e) {
                    // client already gone
                }
            }
        }, 15000); // note: idletime gets added to this too
    }

    public static String getLogMessage(MapleClient cfor, String message) {
        return getLogMessage(cfor, message, new Object[0]);
    }

    public static String getLogMessage(MapleCharacter cfor, String message) {
        return getLogMessage(cfor == null ? null : cfor.getClient(), message);
    }

    public static String getLogMessage(MapleCharacter cfor, String message, Object... parms) {
        return getLogMessage(cfor == null ? null : cfor.getClient(), message, parms);
    }

    public static String getLogMessage(MapleClient cfor, String message, Object... parms) {
        StringBuilder builder = new StringBuilder();
        if (cfor != null) {
            if (cfor.getPlayer() != null) {
                builder.append("<");
                builder.append(MapleCharacterUtil.makeMapleReadable(cfor.getPlayer().getName()));
                builder.append(" (cid: ");
                builder.append(cfor.getPlayer().getId());
                builder.append(")> ");
            }
            if (cfor.getAccountName() != null) {
                builder.append("(Account: ");
                builder.append(MapleCharacterUtil.makeMapleReadable(cfor.getAccountName()));
                builder.append(") ");
            }
        }
        builder.append(message);
        for (Object parm : parms) {
            int start = builder.indexOf("{}");
            builder.replace(start, start + 2, parm.toString());
        }
        return builder.toString();
    }

    public static int findAccIdForCharacterName(String charName) {
        Connection con = DatabaseConnection.getConnection();

        try {
            PreparedStatement ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            ps.setString(1, charName);
            ResultSet rs = ps.executeQuery();

            int ret = -1;
            if (rs.next()) {
                ret = rs.getInt("accountid");
            }
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException e) {
            log.error("SQL THROW");
        }
        return -1;
    }

    public Set<String> getMacs() {
        return Collections.unmodifiableSet(macs);
    }

    public boolean isJounin() {
        return gm;
    }

    public int getGMLevel() {
        return gmlevel;
    }

    public void setScriptEngine(String name, ScriptEngine e) {
        engines.put(name, e);
    }

    public ScriptEngine getScriptEngine(String name) {
        return engines.get(name);
    }

    public void removeScriptEngine(String name) {
        engines.remove(name);
    }

    public ScheduledFuture<?> getIdleTask() {
        return idleTask;
    }

    public void setIdleTask(ScheduledFuture<?> idleTask) {
        this.idleTask = idleTask;
    }

    public void showMessage(String string) {
        getSession().write(MaplePacketCreator.serverNotice(1, string));
    }

    public void dropMessage(String string) {
        getSession().write(MaplePacketCreator.serverNotice(5, string));
    }

    public void showMessage(int fuck, String string) {
        getSession().write(MaplePacketCreator.serverNotice(fuck, string));
    }

    public void createdChar(final int id) {
        allowedChar.add(id);
    }

    public final boolean login_Auth(final int id) {
        return allowedChar.contains(id);
    }

    private static class CharNameAndId {

        public String name;
        public int id;

        public CharNameAndId(String name, int id) {
            super();
            this.name = name;
            this.id = id;
        }
    }

    public NPCConversationManager getCM() {
        return NPCScriptManager.getInstance().getCM(this);
    }

    public final boolean CheckIPAddress() {
        try {
            final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT sessionip FROM accounts WHERE id = ?");
            ps.setInt(1, this.accId);
            final ResultSet rs = ps.executeQuery();
            boolean canlogin = false;
            if (rs.next()) {
                final String sessionIP = rs.getString("SessionIP");
                if (sessionIP != null) { // Probably a login proced skipper?
                    canlogin = getSessionIPAddress().equals(sessionIP.split(":")[0]);
                }
            }
            rs.close();
            ps.close();
            return canlogin;
        } catch (final SQLException e) {
            System.out.println("Failed in checking IP address for client.");
        }
        return false;
    }

    public final String getSessionIPAddress() {
        return session.getRemoteAddress().toString().split(":")[0];
    }
}
