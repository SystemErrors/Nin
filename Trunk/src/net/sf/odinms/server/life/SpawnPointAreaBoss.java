/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.server.life;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicBoolean;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Randomizer;

/**
 *
 * @author Admin
 */
public class SpawnPointAreaBoss extends Spawns {

    private MapleMonster monster;
    private Point pos1;
    private Point pos2;
    private Point pos3;
    private long nextPossibleSpawn;
    private int mobTime;
    private AtomicBoolean spawned = new AtomicBoolean(false);
    private String msg;

    public SpawnPointAreaBoss(final MapleMonster monster, final Point pos1, final Point pos2, final Point pos3, final int mobTime, final String msg) {
	this.monster = monster;
	this.pos1 = pos1;
	this.pos2 = pos2;
	this.pos3 = pos3;
	this.mobTime = mobTime * 1000;
	this.msg = msg;
	this.nextPossibleSpawn = System.currentTimeMillis();
    }

    @Override
    public final MapleMonster getMonster() {
	return monster;
    }

    @Override
    public final byte getCarnivalTeam() {
	return 0;
    }

    @Override
    public final boolean shouldSpawn() {
	if (mobTime < 0) {
	    return false;
	}
	if (spawned.get()) {
	    return false;
	}
	return nextPossibleSpawn <= System.currentTimeMillis();
    }

    @Override
    public final MapleMonster spawnMonster(final MapleMap map) {
	final MapleMonster mob = new MapleMonster(monster);
	final int rand = Randomizer.nextInt(3);
	mob.setPosition(rand == 0 ? pos1 : rand == 1 ? pos2 : pos3);
	spawned.set(true);
	mob.addListener(new MonsterListener() {

	    @Override
	    public void monsterKilled() {
		nextPossibleSpawn = System.currentTimeMillis();

		if (mobTime > 0) {
		    nextPossibleSpawn += mobTime;
		}
		spawned.set(false);
	    }
	});
	map.spawnMonster(mob, -2);

	if (msg != null) {
	    map.broadcastMessage(MaplePacketCreator.serverNotice(6, msg));
//	    map.broadcastMessage(MaplePacketCreator.musicChange("Bgm04/WhiteChristmas_"));
	}
	return mob;
    }
}