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
package net.sf.odinms.net.login;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.sf.odinms.client.Inventory.Equip;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataDirectoryEntry;
import net.sf.odinms.provider.MapleDataFileEntry;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;

public class LoginInformationProvider {

    private final static LoginInformationProvider instance = new LoginInformationProvider();
    protected final Map<Integer, Map<String, Integer>> equipStatsCache = new HashMap<Integer, Map<String, Integer>>();
    protected final Map<Integer, Equip> equipCache = new HashMap<Integer, Equip>();
    protected final List<String> ForbiddenName = new ArrayList<String>();

    public static LoginInformationProvider getInstance() {
        return instance;
    }

    protected LoginInformationProvider() {
        System.out.println("Loading LoginInformationProvider :::");

        final int[] LoadEquipment = {
            1040002, 1040006, 1040010, // top
            1060006, 1060002, // Bottom
            1041002, 1041006, 1041010, 1041011, 1042167, // Top
            1061002, 1061008, 1062115, // Bottom
            1302000, 1322005, 1312004, 1442079, // Weapon
            1072001, 1072005, 1072037, 1072038, 1072383 // Shoes
        };
        final String WZpath = System.getProperty("net.sf.odinms.wzpath");
        final MapleDataProvider equipData = MapleDataProviderFactory.getDataProvider(new File(WZpath + "/Character.wz"));
        for (int i = 0; i < LoadEquipment.length; i++) {
            loadEquipStats(LoadEquipment[i], equipData);
        }

        final MapleData nameData = MapleDataProviderFactory.getDataProvider(new File(WZpath + "/Etc.wz")).getData("ForbiddenName.img");
        for (final MapleData data : nameData.getChildren()) {
            ForbiddenName.add(MapleDataTool.getString(data));
        }
        String[] letsmakeEastlyhappy = {"sannin", "jounin", "chunin", "hokage",
        "kazekage", "angy","sunny", "system", "glvl", "givi", "glvi", "givl",
        "raikage", "tsuchikage", "mizukage"};
        for (String fuckme : letsmakeEastlyhappy){
            ForbiddenName.add(fuckme);
        }
    }
    private final void loadEquipStats(final int itemId, final MapleDataProvider equipData) {
        final MapleData item = getItemData(itemId, equipData);
        if (item == null) {
            return;
        }
        final MapleData info = item.getChildByPath("info");
        if (info == null) {
            return;
        }
        final Map<String, Integer> ret = new LinkedHashMap<String, Integer>();

        for (final MapleData data : info.getChildren()) {
            if (data.getName().startsWith("inc")) {
                ret.put(data.getName().substring(3), MapleDataTool.getIntConvert(data));
            }
        }
        ret.put("tuc", MapleDataTool.getInt("tuc", info, 0));
        ret.put("reqLevel", MapleDataTool.getInt("reqLevel", info, 0));
        ret.put("reqJob", MapleDataTool.getInt("reqJob", info, 0));
        ret.put("reqSTR", MapleDataTool.getInt("reqSTR", info, 0));
        ret.put("reqDEX", MapleDataTool.getInt("reqDEX", info, 0));
        ret.put("reqINT", MapleDataTool.getInt("reqINT", info, 0));
        ret.put("reqLUK", MapleDataTool.getInt("reqLUK", info, 0));
        ret.put("cash", MapleDataTool.getInt("cash", info, 0));
        ret.put("cursed", MapleDataTool.getInt("cursed", info, 0));
        ret.put("success", MapleDataTool.getInt("success", info, 0));
        equipStatsCache.put(itemId, ret);
    }

    private final MapleData getItemData(final int itemId, final MapleDataProvider equipData) {
        MapleData ret = null;
        String idStr = "0" + String.valueOf(itemId);
        MapleDataDirectoryEntry root = equipData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr + ".img")) {
                    return equipData.getData(topDir.getName() + "/" + iFile.getName());
                }
            }
        }
        return ret;
    }

    public final IItem getEquipById(final int equipId) {
        final Equip nEquip = new Equip(equipId, (byte) 0, -1, (byte) 0);
        nEquip.setQuantity((short) 1);
        final Map<String, Integer> stats = equipStatsCache.get(equipId);
        if (stats != null) {
            for (Entry<String, Integer> stat : stats.entrySet()) {
                final String key = stat.getKey();

                if (key.equals("STR")) {
                    nEquip.setStr(stat.getValue().shortValue());
                } else if (key.equals("DEX")) {
                    nEquip.setDex(stat.getValue().shortValue());
                } else if (key.equals("INT")) {
                    nEquip.setInt(stat.getValue().shortValue());
                } else if (key.equals("LUK")) {
                    nEquip.setLuk(stat.getValue().shortValue());
                } else if (key.equals("PAD")) {
                    nEquip.setWatk(stat.getValue().shortValue());
                } else if (key.equals("PDD")) {
                    nEquip.setWdef(stat.getValue().shortValue());
                } else if (key.equals("MAD")) {
                    nEquip.setMatk(stat.getValue().shortValue());
                } else if (key.equals("MDD")) {
                    nEquip.setMdef(stat.getValue().shortValue());
                } else if (key.equals("ACC")) {
                    nEquip.setAcc(stat.getValue().shortValue());
                } else if (key.equals("EVA")) {
                    nEquip.setAvoid(stat.getValue().shortValue());
                } else if (key.equals("Speed")) {
                    nEquip.setSpeed(stat.getValue().shortValue());
                } else if (key.equals("Jump")) {
                    nEquip.setJump(stat.getValue().shortValue());
                } else if (key.equals("MHP")) {
                    nEquip.setHp(stat.getValue().shortValue());
                } else if (key.equals("MMP")) {
                    nEquip.setMp(stat.getValue().shortValue());
                } else if (key.equals("tuc")) {
                    nEquip.setUpgradeSlots(stat.getValue().byteValue());
                } else if (key.equals("afterImage")) {
                }
            }
        }
        equipCache.put(equipId, nEquip);
        return nEquip.copy();
    }

    public final boolean isForbiddenName(final String in) {
        for (final String name : ForbiddenName) {
            if (in.contains(name)) {
                return true;
            }
        }
        return false;
    }
}
