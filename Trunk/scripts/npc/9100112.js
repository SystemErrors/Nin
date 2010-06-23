/**
 * NPC : EXP Gacha - 9100112
 * Location : SouthPerry
 * Author : System of NinjaMS
 * Function : Level / Rb Gacha
 */

importPackage(net.sf.odinms.client.NinjaMS);

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.voteMSG();
        cm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0){
            var tt = " Hello #b #h ##k!. I'm the EXP Gachapon of ninjaMS. ";
            tt += "If you have Regular Gachapon tickets, ";
            tt += "I can let you gain Rebirths or levels, if you are lucky ";
            cm.sendNext(tt);
        } else if (status == 1){
            var lol = Gacha.expGacha(cm.p());
            var text = " "
            switch (lol){
                case 0:
                    text += "You do not have the regular gacha ticket. Don't try to scam me ";
                    break;
                case 1:
                    text += "You have gained Rebirths. You seem to be very lucky!";
                    break;
                case 2:
                    text += "You have been leveled to the max level. you seem to be lucky";
                    break;
                case 3:
                    text += "You have gained Levels. Better Luck next time!";
                    break;
                default :
                    text += "Some thing is wrong";
            }
            cm.sendOk(text);
            cm.dispose();
        } else {
            cm.dispose();
        }
    }
}

