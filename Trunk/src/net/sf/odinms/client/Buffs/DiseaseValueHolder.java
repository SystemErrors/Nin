/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.Buffs;

/**
 *
 * @author Admin
 */

public class DiseaseValueHolder {

//    public int skillId;
    public long startTime;
    public long length;

    public DiseaseValueHolder(long startTime, long length) {
	super();
//	this.skillId = skillId;
	this.startTime = startTime;
	this.length = length;
    }
}
