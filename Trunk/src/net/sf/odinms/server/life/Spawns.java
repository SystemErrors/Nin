/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.server.life;

import net.sf.odinms.server.maps.MapleMap;

/**
 *
 * @author Admin
 */
public abstract class Spawns {
    public abstract MapleMonster getMonster();
    public abstract byte getCarnivalTeam();
    public abstract boolean shouldSpawn();
    public abstract MapleMonster spawnMonster(MapleMap map);
}
