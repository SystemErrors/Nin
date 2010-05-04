/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.Enums;

/**
 *
 * @author Owner
 */
public enum Clans {

    UNDECIDED(0),
    EARTH(1),
    WIND(2),
    WATER(3),
    FIRE(4),
    NARUTO(5),
    LIGHTNING(6),
    ;
    private final int id;

    private Clans(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Clans getById(int id) {
        for (Clans l : Clans.values()) {
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }
   
    public String getName(){
        switch (id) {
            case 0:
                return " UNDECIDED ";
            case 1:
                return " EARTH ";
            case 2:
                return " WIND ";
            case 3:
                return " WATER ";
            case 4:
                return " FIRE ";
            case 5:
                return " NARUTO ";
            case 6:
                return " LIGHTNING ";
            default:
                return " Error ";
        }
    }
}
