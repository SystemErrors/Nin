/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.Enums;

/**
 *
 * @author Owner
 */
public enum Village {

    UNDECIDED(0),
    LEAF(1),
    SAND(2),
    ROCK(3),
    CLOUD(4),
    MIST(5)
    ;
    private final int id;

    private Village(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Village getById(int id) {
        for (Village l : Village.values()) {
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
