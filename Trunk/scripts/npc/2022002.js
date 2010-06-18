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
            cm.sendNext("Hello #b#h ##k#n, I'm the Scroll Seller of ninjaMS. Wanty to buy some scrolls??");
        } else if (status == 1) {
            cm.sendSimple("Please choose what type of #ePirate Equipment#n you "
                +"wish to buy.\r\n\r\n#b#L0# 10% scrolls#l\r\n#L1# 30% scrolls"
                +"#l\r\n#L2# 60% scrolls#l\r\n#L3# 70% scrolls#l\r\n"
                +"#L4# Anniversary scrolls#l\r\n#L5# 15% scrolls #l"
                +"\r\n#L6#65% scrolls#l");
        } else if (status == 2) {
            cm.dispose();
            if (selection == 0) {
                cm.openShop (11);
            } else if (selection == 1) {
                cm.openShop (12);
            } else if (selection == 2) {
                cm.openShop (13);
            } else if (selection == 3) {
                cm.openShop (14);
            } else if (selection == 4) {
                cm.openShop (15);
            } else if (selection == 5) {
                cm.openShop (16);
            } else if (selection == 6) {
                cm.openShop (17);
            } else {
                cm.dispose();
            }
        } else {
            cm.voteMSG();
        }
    }
}
