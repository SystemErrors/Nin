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
public class MapleCoolDownValueHolder {

    public int skillId;
    public long startTime;
    public long length;
    public ScheduledFuture<?> timer;

    public MapleCoolDownValueHolder(int skillId, long startTime, long length, ScheduledFuture<?> timer) {
        super();
        this.skillId = skillId;
        this.startTime = startTime;
        this.length = length;
        this.timer = timer;
    }
}
