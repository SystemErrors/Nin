/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.net.channel.handler;

import java.security.SecureRandom;
import java.util.Random;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class MobFriendlyDamageMobHandler extends AbstractMaplePacketHandler {
    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        SecureRandom secureRandom = new SecureRandom();
        Random rand = new Random(secureRandom.nextLong());

        int attacker = slea.readInt();
        slea.readInt(); //charId
        int damaged = slea.readInt();
        int damage = rand.nextInt(((c.getPlayer().getMap().getMonsterByOid(damaged).getMaxHp() / 13 + c.getPlayer().getMap().getMonsterByOid(attacker).getPADamage() * 10)) * 2 + 500); //Beng's formula.
        if (c.getPlayer().getMap().getMonsterByOid(damaged) == null || c.getPlayer().getMap().getMonsterByOid(attacker) == null)
            return;
        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.MobDamageMobFriendly(c.getPlayer().getMap().getMonsterByOid(damaged), damage), c.getPlayer().getMap().getMonsterByOid(damaged).getPosition());
        c.getSession().write(MaplePacketCreator.enableActions());
    }

}