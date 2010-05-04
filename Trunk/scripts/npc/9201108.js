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
            cm.sendNext("Hello #b#h ##k, I can help you get the weapons and armours of #e#rBowman and Wind Archer#k#n class.");
        } else if (status == 1) {
            cm.sendSimple("Please choose what type of #eArcher Equipment#n you wish to buy.\r\n\r\n#b"
                + "#L0#Shoes#l\r\n#L1#Overalls#l\r\n#L2#Gloves#l\r\n#L3#Hats#l\r\n#L4#Bows#l\r\n#L5#Crossbows#l\r\n#L6#Arrows#l");
        } else if (status == 2) {
cm.dispose();
            if (selection == 0) {
                cm.openShop (1994);
            } else if (selection == 1) {
                cm.openShop (2020);
            } else if (selection == 2) {
                cm.openShop (2021);
            } else if (selection == 3) {
                cm.openShop (2022);
            } else if (selection == 4) {
                cm.openShop (2023);
            } else if (selection == 5) {
                cm.openShop (2024);
            } else if (selection == 6) {
                cm.openShop (2025);
            }
        } else {
            cm.voteMSG();
        }
    }
}
