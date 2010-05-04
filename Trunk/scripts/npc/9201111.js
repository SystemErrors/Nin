// default message
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.voteMSG();
    } else {
        if (mode == 1) {
            status++;
        } else { // determine below - if it is send default message, it will dispose upon clicking no.
            // if it is status--, it goes back upon no. sophistication includes if (status >= 2 && mode == 0). DIY.
            cm.voteMSG(); return; // return only if using senddefaultmess
        //status--;
        }

        if (status == 0) {
            cm.sendNext("Hello #b#h ##k#n, I can sell you the weapons of  #e#rThunder Breaker or Pirate#k#n class.");
        } else if (status == 1) {
            cm.sendSimple("Please choose what type of #ePirate Equipment#n you wish to buy.\r\n\r\n#b"
                + "#L0#Shoes#l\r\n#L1#Overalls#l\r\n#L2#Gloves#l\r\n#L3#Hats#l\r\n#L4#Knuckles#l\r\n#L5#Guns#l\r\n#L6#Bullets#l");
        } else if (status == 2) {
            cm.dispose();
            if (selection == 0) {
                cm.openShop (4701);
            } else if (selection == 1) {
                cm.openShop (4702);
            } else if (selection == 2) {
                cm.openShop (4703);
            } else if (selection == 3) {
                cm.openShop (4704);
            } else if (selection == 4) {
                cm.openShop (4705);
            } else if (selection == 5) {
                cm.openShop (4706);
            } else if (selection == 6) {
                cm.openShop (4707);
            } else {
                cm.dispose();
            }
        } else {
            cm.voteMSG();
        }
    }
}
