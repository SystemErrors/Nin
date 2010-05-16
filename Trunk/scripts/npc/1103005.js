var status = 0;
importPackage (net.sf.odinms.client.NinjaMS.Processors);
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
                    text += " #L2# Spy on some one";
                    cm.sendSimple(text);
                } else if (status == 1){
                    if (selection == 2){
                        cm.sendGetText("Enter the name of the person you want to spy in the box : ");
                    } else {
                        text =  CharInfoProcessor.getNinjaGlare(cm.getPlayer());
                        cm.sendOk(text);
                        cm.dispose();
                    }
                } else if (status == 2){

                } else {
                    cm.sendOk("Under construction");
                    cm.dispose();
                }
        }
}

