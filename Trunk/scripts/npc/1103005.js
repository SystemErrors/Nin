var status = 0;
importPackage (net.sf.odinms.client.NinjaMS.Processors);
importPackage (net.sf.odinms.client);
importPackage (net.sf.odinms.net.channel);
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            var text = " Hello #b#h #!#k I can help you see your stats";
            text += " or spy on some one else'.\r\n#r Choose what you";
            text += " want : #b \r\n #L1# See My Stats#l \r\n";
            text += " #L2# Spy on some one #l";
            if(cm.getPlayer().isChunin()){
                text += "#L3# Offline GM Spy #l";
            }
            cm.sendSimple(text);
        } else if (status == 1){
            if (selection == 1){
                text =  CharInfoProcessor.getNPCNinjaGlare(cm.getPlayer());
                cm.sendOk(text);
                cm.dispose();
            } else if (selection == 2){
                cm.sendGetText("Enter the name of the person you want to spy in the box : ");
            } else {
                cm.sendGetText("Enter the name of the person you want to spy in the box : ");
                status = 9;
            }
        } else if (status == 2){
            var name = cm.getText();
            text =  CharInfoProcessor.getNPCNinjaGlare(cm.getC(), name);
            cm.sendOk(text);
            cm.dispose();
        } else if (status == 10) {
             var namee = cm.getText();
            text =  CharInfoProcessor.getCharInfoOffline(cm.getC(), namee);
            cm.sendOk(text);
            cm.dispose();
        } else {
            cm.sendOk("Under construction");
            cm.dispose();
        }
    }
}

