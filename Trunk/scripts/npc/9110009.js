importPackage(net.sf.odinms.client.NinjaMS);
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else { // determine below - if it is send default message, it will dispose upon clicking no.
        // if it is status--, it goes back upon no. sophistication includes if (status >= 2 && mode == 0). DIY.
        cm.voteMSG();
        return; // return only if using senddefaultmess
    //status--;
    }

    if (status == 0) {
        var selStr = "";
        selStr += " Hello there Elite Ninja, #b#h ##k, I am the #rCharity Box Gacha#k,";
        selStr += " and I am in charge of the Donation points Gambling";
        selStr += " I can reward you with various goodies if you are lucky.\r\n\r\n";
        selStr += " #rChoose What you want with me : \r\n\r\n";
        selStr += " #L1#See the possible rewards list#l";
        selStr += " #L2#Gamble a point";
        cm.sendSimple(selStr);
    } else if (status == 1){
        if(selection == 2){
            if(cm.getPlayer().getDPoints() > 0){
                cm.getPlayer().modifyDPoints(-1);
                if(Donations.Gacha(cm.getPlayer())){
                    cm.sendOk("Hope to see you soon");
                } else {
                    cm.sendOk("Some thing wrong");
                }
            } else {
                cm.sendOk("You do not have any dPoints");
            }
            cm.dispose();
        }else {
            cm.voteMSG();
        }
    }
}
