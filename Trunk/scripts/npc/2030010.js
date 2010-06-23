/**
 * @npc : Amon
 * @id : 2030010
 * @location : Zakum Altar
 * @author : System of NinjaMS
 * @function : Zakum altar NPC.
 */

importPackage(net.sf.odinms.server);

/*
	Amon
*/
var status = 0;
var map = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.voteMSG();
            return;
        } else if (mode == 1){
            status++;
        } else {
            cm.voteMSG();
            return;
        }
        if (status == 0) {
            cm.sendSimple("Hi #b#h #!#k Welcome to #rZakum's altar#k. I can not help with the fight, but can I do anything else for you?\r\n#L1#I'd like to purchase an #bEye Of Fire#k (10 Tao Of Sight)#l\r\n\#L2#Take me to #bEl Nath#k!#l\r\n\#L3#Take me to #bThe Door To Zakum#k!#l");
        } else if (status == 1) {
            if (selection == 1) {
                if(cm.getPlayer().haveSight(10)) {
                    cm.gainItem(4032016, -10); // tao of sight
                    cm.gainItem(4001017, 1); // Eye of fire
                    cm.sendOk("Enjoy your Zakum battle my elite Ninja Hero :) !");
                } else {
                    cm.sendOk("Sorry #b#h #,#r you do not have enough Tao.");
                }
                cm.dispose();
            } else if (selection == 2) {
                cm.warp(211000000, 0);
                cm.dispose();
            } else if (selection == 3) {
                cm.warp(211042300, 0);
                cm.dispose();
            }
        }
    }
}