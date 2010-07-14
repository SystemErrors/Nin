/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.server.constants;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Village;

/**
 *
 * @author Owner
 */
public class Rates {  
        
    public static int getExpRate(MapleCharacter noob){
        if(noob.getGMSMode() > 0 && !noob.isJounin()){
            return 0;
        }
        double expRate = Village.getExpRate(noob.getVillage());
        if(Modes.getInstance(noob).hasKyubi()){
            if(noob.getVillage() != 5) {
                expRate *= 2;
            } else {
                expRate *= 1.3;
            }
        }
        expRate += (noob.getExpBoost() * expRate / 100);
        if(noob.isGenin()){
            expRate += (expRate * 0.25);
        }         
        if(noob.hasRasengan()){
            expRate *= 0.70;
        }
        double reduce = Math.min((noob.getMaxStatItems() * 100), noob.getReborns());
        reduce = Math.min(reduce, (expRate /2));
        expRate -= reduce;
        if(Village.isOtherVillage(noob)){
            expRate /=2;
        }
        return (int) Math.floor(expRate);
    }

    public static int getMesoRate(MapleCharacter noob){
        double mesoRate = Village.getMesoRate(noob.getVillage());
        if(Modes.getInstance(noob).hasSage()){
           if(noob.getVillage() != 5) {
                mesoRate *= 2;
            } else {
                mesoRate *= 1.3;
            }
        }
        mesoRate += (noob.getMesoBoost() * mesoRate / 100);
        if(noob.isGenin()){            
            mesoRate += (mesoRate * 0.25);
        }
        if(noob.getInventory(MapleInventoryType.EQUIPPED).findById(1812006) != null){
            mesoRate *= 0.75;
        }
        double reduce = Math.min((noob.getMaxStatItems() * 100), noob.getReborns());
        reduce = Math.min(reduce, (mesoRate /2));
        mesoRate -= reduce;
        if(Village.isOtherVillage(noob)){
            mesoRate /=2;
        }
        return (int) Math.floor(mesoRate);
    }

    public static int getDropRate(MapleCharacter noob){
        double dropRate = Village.getDropRate(noob.getVillage());
        if(Modes.getInstance(noob).hasHachibi()){
            if(noob.getVillage() != 5) {
                dropRate *= 2;
            } else {
                dropRate *= 1.3;
            }
        }
        dropRate += (noob.getDropBoost() * dropRate / 100);
        if(noob.isGenin()){
            dropRate += (dropRate * 0.25);
        }       
        double reduce = Math.min((noob.getMaxStatItems()), (noob.getReborns()/100));
        reduce = Math.min(reduce, (dropRate /2));
        dropRate -= reduce;
        if(Village.isOtherVillage(noob)){
            dropRate /=2;
        }
        return (int) Math.floor(dropRate);
    }

    public static int getBossDropRate(MapleCharacter noob){
        double bdropRate = Village.getBossRate(noob.getVillage());
        if(Modes.getInstance(noob).hasShakaku()){
             if(noob.getVillage() != 5) {
                bdropRate *= 2;
            } else {
                bdropRate *= 1.3;
            }
        }
        bdropRate += (noob.getBossDropBoost() * bdropRate / 100);
        if(noob.isGenin()){
            bdropRate += (bdropRate * 0.25);
        }        
        if(noob.hasRasengan()){
            bdropRate *= 0.80;
        }
        if(noob.getInventory(MapleInventoryType.EQUIPPED).findById(1812006) != null){
            bdropRate *= 0.80;
        }
        double reduce = Math.min((noob.getMaxStatItems()), (noob.getReborns()/100));
        reduce = Math.min(reduce, (bdropRate /2));
        bdropRate -= reduce;
        if(Village.isOtherVillage(noob)){
            bdropRate /=2;
        }
        return (int) Math.floor(bdropRate);
    }
}
