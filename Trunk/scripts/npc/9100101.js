/**
 * NPC : Gachapon - 9100101
 * Location : Ellinia
 * Author : System of NinjaMS
 * Function : Chairs Gacha
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
            var tt = " Hello #b #h ##k!. I'm the Chairs Gachapon of ninjaMS. ";
            tt += "If you have Regular Gachapon tickets, ";
            tt += "I can let you gain Chairs, if you are lucky you might get a good one";
            cm.sendNext(tt);
        } else if (status == 1){
            var lol = Gacha.ChairGacha(cm.p());
            var text = " "
            if(lol == 0){
                text += "You do not have the regular gacha ticket. Don't try to scam me ";
            } else if(lol == 9){
                text += "You do not have enough inventory space";
            } else {
                text += "You have gained a Chair : #v"+lol+"# - #t"+lol+"#";
            }
            cm.sendOk(text);
            cm.dispose();
        } else {
            cm.dispose();
        }
    }
}

