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
                return " LEAF ";
            case 2:
                return " SAND ";
            case 3:
                return " ROCK ";
            case 4:
                return " CLOUD ";
            case 5:
                return " MIST ";
            default:
                return " Error ";
        }
    }

     public int getExpRate() {
        switch (id) {
            case 0:
                return 1000;
            case 1:
                return 1000;
            case 2:
            case 3:
            case 4:
                return 600;
            case 5:
                return 750;
            case 6:
                return 1000;
            default:
                return 200;
        }
    }

    public int getMesoRate() {
        switch (id) {
            case 0:
                return 5000;
            case 1:
                return 3000;
            case 2:
                return 5000;
            case 3:
            case 4:
                return 3000;
            case 5:
                return 4000;
            case 6:
                return 5000;
            default:
                return 3000;
        }
    }

    public int getDropRate() {
        switch (id) {
            case 0:
                return 20;
            case 1:
                return 10;
            case 2:
                return 10;
            case 3:
                return 30;
            case 4:
                return 10;
            case 5:
                return 20;
            case 6:
                return 30;
            default:
                return 10;
        }
    }

    public int getBossRate() {
        switch (id) {
            case 0:
            case 1:
            case 2:
            case 3:
                return 10;
            case 4:
                return 20;
            case 5:
                return 12;
            case 6:
                return 20;
            default:
                return 10;
        }
    }

    public int getHomeTown(){
        int i = 0;
        switch(id){
            case 0:
                break;
            case 1:
                i = 240000000;
                break;
            case 2:
                i = 260000000;
                break;
            case 3:
                i = 102000000;
                break;
            case 4:
                i = 200000000;
                break;
            case 5:
                i = 251000000;
                break;

        }
        return i;
    }

    public int villageItem(){
        int i = 0;
        switch(id){
            case 0:
                break;
            case 1:
                i = 4000241;
                break;
            case 2:
                i = 4000332;
                break;
            case 3:
                i = 4000131;
                break;
            case 4:
                i = 4001063;
                break;
            case 5:
                i = 4000415;
                break;
        }
        return i;
    }
}
