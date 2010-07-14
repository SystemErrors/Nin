/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.server.life;

/**
 *
 * @author Admin
 */
public class OverrideMonsterStats {
    private int exp, hp, mp;
    private boolean gm;

    public int getExp() {
        return exp;
    }

    public void setOExp(int exp) {
        this.exp = exp;
    }

    public int getHp() {
        return hp;
    }

    public void setOHp(int hp) {
        this.hp = hp;
    }

    public int getMp() {
        return mp;
    }

    public void setOMp(int mp) {
        this.mp = mp;
    }
    public boolean isGmSpawn(){
        return gm;
    }
    public void setGmSpawn(){
        gm = true;
    }
}