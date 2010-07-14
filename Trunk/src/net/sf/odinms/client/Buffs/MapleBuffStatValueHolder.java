/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.Buffs;

import java.util.concurrent.ScheduledFuture;

/**
 *
 * @author Admin
 */
public class MapleBuffStatValueHolder {

    public MapleStatEffect effect;
    public long startTime;
    public int value;
    public ScheduledFuture<?> schedule;

    public MapleBuffStatValueHolder(MapleStatEffect effect, long startTime, ScheduledFuture<?> schedule, int value) {
	super();
	this.effect = effect;
	this.startTime = startTime;
	this.schedule = schedule;
	this.value = value;
    }
}

