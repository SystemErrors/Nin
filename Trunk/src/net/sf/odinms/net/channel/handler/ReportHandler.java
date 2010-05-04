/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class ReportHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.readByte();
        String victim = slea.readMapleAsciiString();
        slea.readByte();
        String description = slea.readMapleAsciiString();
        if (c.getPlayer().getCheatTracker().spam(1, 30000)) {
            c.showMessage(5, "You can report only once every minute");
            return;
        }
        c.showMessage(6, "[Anbu]Your Report has been submitted successfully.");
        c.getChannelServer().broadcastGMPacket(MaplePacketCreator.serverNotice(5, "[Report] " + c.getPlayer().getName() + " (" + MapleCharacterUtil.makeMapleReadable(c.getPlayer().getName()) + ") has reported " +  victim + " ("  + MapleCharacterUtil.makeMapleReadable(victim) + ") for: " + description));
    }
}