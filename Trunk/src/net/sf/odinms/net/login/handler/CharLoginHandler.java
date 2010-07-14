/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.net.login.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.login.LoginInformationProvider;
import net.sf.odinms.net.login.LoginServer;
import net.sf.odinms.net.login.LoginWorker;
import net.sf.odinms.server.constants.Items;
import net.sf.odinms.tools.KoreanDateUtil;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Packets.LoginPacket;
import net.sf.odinms.tools.Randomizer;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class CharLoginHandler {

    private static final boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        if (c.loginAttempt > 5) {
            return true;
        }
        return false;
    }

    public static final void login(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final String login = slea.readMapleAsciiString();
        final String pwd = slea.readMapleAsciiString();
        c.setAccountName(login);
        final boolean ipBan = c.hasBannedIP();
        // final boolean macBan = false; // MSEA doesn't sent mac
        final boolean macBan = c.hasBannedMac();

        int loginok = c.login(login, pwd, ipBan || macBan);
        final Calendar tempbannedTill = c.getTempBanCalendar();

        if (loginok == 0 && (ipBan || macBan)) {
            loginok = 3;
            if (macBan) {
                // this is only an ipban o.O" - maybe we should refactor this a bit so it's more readable
                MapleCharacter.ban(c.getSession().getRemoteAddress().toString().split(":")[0], "Enforcing account ban, account " + login, false);
            }
        }
        if (loginok != 0) {
            if (!loginFailCount(c)) {
                c.getSession().write(LoginPacket.getLoginFailed(loginok));
            }
        } else if (tempbannedTill.getTimeInMillis() != 0) {
            if (!loginFailCount(c)) {
                c.getSession().write(LoginPacket.getTempBan(KoreanDateUtil.getTempBanTimestamp(tempbannedTill.getTimeInMillis()), c.getBanReason()));
            }
        } else {
            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
        }
    }

    public static final void ServerListRequest(final MapleClient c) {
        final LoginServer ls = LoginServer.getInstance();
        c.getSession().write(LoginPacket.getServerList(6, ls.getServerName(), ls.getLoad()));
        c.getSession().write(LoginPacket.getEndOfServerList());
    }

    public static final void ServerStatusRequest(final MapleClient c) {
        // 0 = Select world normally
        // 1 = "Since there are many users, you may encounter some..."
        // 2 = "The concurrent users in this world have reached the max"

        int numPlayer = 0;
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            numPlayer += cserv.getPlayerStorage().getConnectedClients();
        }
        final int userLimit = LoginServer.getInstance().getUserLimit();
        if (numPlayer >= userLimit) {
            c.getSession().write(LoginPacket.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            c.getSession().write(LoginPacket.getServerStatus(1));
        } else {
            c.getSession().write(LoginPacket.getServerStatus(0));
        }
    }

    public static final void CharlistRequest(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int server = slea.readByte();
        final int channel = slea.readByte() + 1;
        c.setWorld(server);
        System.out.println("Client is connecting to server " + server + " channel " + channel + "");
        c.setChannel(channel);
        c.sendCharList(server);
    }

    public static final void CheckCharName(final String name, final MapleClient c) {
        c.getSession().write(LoginPacket.charNameResponse(name,
                !MapleCharacterUtil.canCreateChar(name) || LoginInformationProvider.getInstance().isForbiddenName(name)));
    }

    public static final void CreateChar(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final String name = slea.readMapleAsciiString();
        final int face = slea.readInt();
        final int hair = slea.readInt();
        final int hairColor = slea.readInt();
        final int skinColor = slea.readInt();
        final int top = slea.readInt();
        final int bottom = slea.readInt();
        final int shoes = slea.readInt();
        final int weapon = slea.readInt();
        final int gender = slea.readByte();
        LoginInformationProvider li = LoginInformationProvider.getInstance();
        boolean charok = true;
        if (gender == 0) {
            if (face != 20000 && face != 20001 && face != 20002) {
                charok = false;
            }
            if (hair != 30000 && hair != 30020 && hair != 30030) {
                charok = false;
            }
            if (top != 1040002 && top != 1040006 && top != 1040010) {
                charok = false;
            }
            if (bottom != 1060006 && bottom != 1060002) {
                charok = false;
            }
        } else if (gender == 1) {
            if (face != 21000 && face != 21001 && face != 21002) {
                charok = false;
            }
            if (hair != 31000 && hair != 31040 && hair != 31050) {
                charok = false;
            }
            if (top != 1041002 && top != 1041006 && top != 1041010 && top != 1041011) { // Credits Traitor for adding the Cygnus armours
                charok = false;
            }
            if (bottom != 1061002 && bottom != 1061008) {
                charok = false;
            }
        } else {
            charok = false;
        }
        if (skinColor < 0 || skinColor > 3) {
            charok = false;
        }
        if (weapon != 1302000 && weapon != 1322005 && weapon != 1312004) {
            charok = false;
        }
        if (shoes != 1072001 && shoes != 1072005 && shoes != 1072037 && shoes != 1072038) {
            charok = false;
        }
        if (hairColor != 0 && hairColor != 2 && hairColor != 3 && hairColor != 7) {
            charok = false;
        }
        MapleCharacter newchar = MapleCharacter.getDefault(c);  // I have no idea why this moved down. but it looks better. only assign stuff is charok.
        if (charok) {
            newchar.setWorld(c.getWorld());
            newchar.setFace(face);
            newchar.setHair(hair + hairColor);
            newchar.setName(name);
            newchar.setSkinColor(skinColor);
            MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
            IItem eq_top = li.getEquipById(top);
            eq_top.setPosition((byte) -5);
            equip.addFromDB(eq_top);
            IItem eq_bottom = li.getEquipById(bottom);
            eq_bottom.setPosition((byte) -6);
            equip.addFromDB(eq_bottom);
            IItem eq_shoes = li.getEquipById(shoes);
            eq_shoes.setPosition((byte) -7);
            equip.addFromDB(eq_shoes);
            IItem eq_weapon = li.getEquipById(weapon);
            eq_weapon.setPosition((byte) -11);
            equip.addFromDB(eq_weapon);
            MapleInventory etc = newchar.getInventory(MapleInventoryType.ETC);
            int[] etcitems = {Items.currencyType.Sight,
                4006000, // Magic rock
                4006001, // Summon rock
                4290001}; // Bummer Effect
            int[] etcitemsquantity = {25, // Tao
                100, // Magic rock
                100, // Summon rock
                1}; // Bummer Effect
            for (int i = 0; i < etcitems.length; i++) {
                Item item = new Item(etcitems[i], (byte) (i + 1), (short) etcitemsquantity[i], (byte) 0);
                if (etcitems.length - 4 >= i + 1) {
                }
                etc.addItem(item);
            }
            MapleInventory use = newchar.getInventory(MapleInventoryType.USE);
            int[] useitems = {2022015, //mushroom miso ramen
                2022011, // Triangular sushi
                2022121, // Gelt Chocolate
                2050004, // All cure potion
                2070007, // Hwabi throwing stars
                2061004, //diamond arrow for xbow
                2060004, // diamond arrow for bow
                2330001, // split bullet
                2040807}; // GFA GM
            int[] useitemsquantity = {500, // mmr
                50, // triangular Sushi
                10, // Gelt Chocolate
                200, // All cure potion
                1000, // Hwabi throwing Star
                1000, //diamond arrow for xbow
                1000, //diamond arrow for bow
                1000, // split bullet
                7}; // GFA GM
            for (int i = 0; i < useitems.length; i++) {
                Item item = new Item(useitems[i], (byte) (i + 1), (short) useitemsquantity[i], (byte) 0);
                if (useitems.length - 4 >= i + 1) {
                }
                use.addItem(item);
            }
            MapleInventory setup = newchar.getInventory(MapleInventoryType.SETUP);
            setup.addItem(new Item(3010000, (byte) 1, (short) 1, (byte) 0));
            MapleInventory cash = newchar.getInventory(MapleInventoryType.CASH);
            int[] cashitems = {5072000, // Super mega
                5076000, // item mega
                5390000, // diablo
                5390001, // Cloud 9
                5390002, //Loveholic
                5121000}; // Fighting spirit (weather)
            int[] cashitemsquantity = {10, // smega
                5, // ismega
                1, // diablo
                1, // Cloud 9
                1, // Loveholic
                5}; // Fighting spirit (weather)
            for (int i = 0; i < cashitems.length; i++) {
                Item item = new Item(cashitems[i], (byte) (i + 1), (short) cashitemsquantity[i], (byte) 0);
                cash.addItem(item);
            }
            MapleInventory equipp = newchar.getInventory(MapleInventoryType.EQUIP);
            if (gender == 0) { // Men
                int[] equipitems = {1002240, // Hajimaki(hat)
                    1050115, // sea hermit robe
                    1082149, // Brown Work glove
                    1702031, // Liu Bei sword
                    1082077, // White bandage
                    1022023, // Crested Eye Patch
                    1010002, // Ninja Mask for Men
                    1071008, // Kimono sandals
                    1332066, // - Razor (DAGGER)
                    1472063, // - Magical Mitten (CLAW)
                    1492000, // - Pistol (GUN)
                    1482000, // - Steel Knuckler (KNUCKLER)
                    1432009, // - Bamboo Spear (SPEAR)
                    1442011, // - Surfboard (POLEARM)
                    1402044, // - Pumpkin Lantern (SWORD)
                    1382015, // - Poison Mushroom (WAND
                    1102061, // - Oxygen Tank (CAPE)
                };
                for (int i = 0; i < equipitems.length; i++) {
                    IItem thing = li.getEquipById(equipitems[i]);
                    thing.setPosition((byte) (i + 1));
                    equipp.addFromDB(thing);
                }
            } else {
                int[] equipitems = {1002240, // - Hajimaki (HAT)
                    1051126, // - Red Chinese Dress
                    1082149, // Brown Work Gloves
                    1022023, // - Crested Eye Patch (EYE ACCESSORY)
                    1011000, // - Ninja Mask for Women (ACCESSORY)
                    1071008, // - Kimono Sandals (SHOES)
                    1082077, // - White Bandage (GLOVES)
                    1702031, // - Liu Bei Sword (WEAPON)
                    1332066, // - Razor (DAGGER)
                    1472063, // - Magical Mitten (CLAW)
                    1492000, // - Pistol (GUN)
                    1482000, // - Steel Knuckler (KNUCKLER)
                    1432009, // - Bamboo Spear (SPEAR)
                    1442011, // - Surfboard (POLEARM)
                    1402044, // - Pumpkin Lantern (SWORD)
                    1382015, // - Poison Mushroom (WAND
                    1102061, // - Oxygen Tank (CAPE)
                };
                for (int i = 0; i < equipitems.length; i++) {
                    IItem thing = li.getEquipById(equipitems[i]);
                    thing.setPosition((byte) (i + 1));
                    equipp.addFromDB(thing);
                }
            }
        }
        if (charok && !li.isForbiddenName(name) && MapleCharacterUtil.canCreateChar(name)) {
            MapleCharacter.saveNewToDB(newchar);
            c.getSession().write(LoginPacket.addNewCharEntry(newchar, true));
            c.createdChar(newchar.getId());
        } else {
            c.getSession().write(LoginPacket.addNewCharEntry(newchar, false));
        }
    }

    public static final void DeleteChar(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int idate = slea.readInt();
        final int cid = slea.readInt();
        int year = idate / 10000;
        int month = (idate - year * 10000) / 100;
        int day = idate - year * 10000 - month * 100;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month - 1, day);
        boolean shallDelete = c.checkBirthDate(cal);
        byte state = 0;
        if (!c.login_Auth(cid)) {
            c.getSession().close();
            return; // Attempting to delete other character
        }
        if (shallDelete) {
            if (!c.deleteCharacter(cid)) {
                state = 1; //actually something else would be good o.o
            }
        }
        c.getSession().write(LoginPacket.deleteCharResponse(cid, state));
    }

    public static final void charSelected(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int charId = slea.readInt();
        final String macs = slea.readMapleAsciiString();
        if (loginFailCount(c) || !c.login_Auth(charId)) {
            c.getSession().close();
            return;
        }
        c.updateMacs(macs);
        if (c.hasBannedMac()) {
            c.getSession().close();
            return;
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }
        //c.getSession().write(LoginPacket.getServerIP(InetAddress.getByName("127.0.0.1"), 7575, charId));
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());

        String channelServerIP = MapleClient.getChannelServerIPFromSubnet(c.getSessionIPAddress(), c.getChannel());
        try {
            if (channelServerIP.equals("0.0.0.0")) {
                String[] socket = LoginServer.getInstance().getIP(c.getChannel()).split(":");
                c.getSession().write(LoginPacket.getServerIP(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]), charId));
            } else {
                String[] socket = LoginServer.getInstance().getIP(c.getChannel()).split(":");
                c.getSession().write(LoginPacket.getServerIP(InetAddress.getByName(channelServerIP), Integer.parseInt(socket[1]), charId));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static final void relogRequest(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        c.getSession().write(LoginPacket.getRelogResponse());
    }

    public static final void afterLogin(SeekableLittleEndianAccessor slea, MapleClient c) {
        final byte c2 = slea.readByte();
        final byte c3 = slea.readByte();
        if (c2 == 1 && c3 == 1) {
            // Official requests the pin here - but pins suck so we just accept
            c.getSession().write(LoginPacket.pinOperation((byte) 0));
        } else if (c2 == 1 && c3 == 0) {
            slea.seek(8);
            String pin = slea.readMapleAsciiString();
            if (pin.equals("1234")) {
                c.getSession().write(LoginPacket.pinOperation((byte) 0));
            } else {
                c.getSession().write(LoginPacket.pinOperation((byte) 2));
            }
        } else {
            // abort login attempt
        }
    }

    public final static void viewAllChar(SeekableLittleEndianAccessor slea, MapleClient c) {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT world, id FROM characters WHERE accountid = ?");
            ps.setInt(1, c.getAccID());
            int charsNum = 0;
            List<Integer> worlds = new ArrayList<Integer>();
            List<MapleCharacter> chars = new ArrayList<MapleCharacter>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int cworld = rs.getInt("world");
                boolean inside = false;
                for (int w : worlds) {
                    if (w == cworld) {
                        inside = true;
                    }
                }
                if (!inside) {
                    worlds.add(cworld);
                }
                MapleCharacter chr = MapleCharacter.loadCharFromDB(rs.getInt("id"), c, false);
                chars.add(chr);
                charsNum++;
            }
            rs.close();
            ps.close();
            int unk = charsNum + 3 - charsNum % 3;
            c.getSession().write(LoginPacket.showAllCharacter(charsNum, unk));
            for (int w : worlds) {
                List<MapleCharacter> chrsinworld = new ArrayList<MapleCharacter>();
                for (MapleCharacter chr : chars) {
                    if (chr.getWorld() == w) {
                        chrsinworld.add(chr);
                    }
                }
                c.getSession().write(LoginPacket.showAllCharacterInfo(w, chrsinworld));
            }
        } catch (Exception e) {
        }
    }

    public final static void pickAllChar(SeekableLittleEndianAccessor slea, MapleClient c) {
        int charId = slea.readInt();
        int world = slea.readInt();
        c.setWorld(world);
        String macs = slea.readMapleAsciiString();
        c.updateMacs(macs);
        if (c.hasBannedMac()) {
            c.getSession().close();
            return;
        }
        try {
            c.setChannel(Randomizer.nextInt(ChannelServer.getAllInstances().size()));
        } catch (Exception e) {
            c.setChannel(1);
        }
        try {
            if (c.getIdleTask() != null) {
                c.getIdleTask().cancel(true);
            }
            c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
            String channelServerIP = MapleClient.getChannelServerIPFromSubnet(c.getSession().getRemoteAddress().toString().replace("/", "").split(":")[0], c.getChannel());
            if (channelServerIP.equals("0.0.0.0")) {
                String[] socket = LoginServer.getInstance().getIP(c.getChannel()).split(":");
                c.getSession().write(LoginPacket.getServerIP(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]), charId));
            } else {
                c.getSession().write(LoginPacket.getServerIP(InetAddress.getByName(channelServerIP), Integer.parseInt(LoginServer.getInstance().getIP(c.getChannel()).split(":")[1]), charId));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
