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
            cm.sendNext("Hello #b#h ##k#n, I can sell you the weapons and armours of #e#rDawn Warrior and Warrior#k#n class.");
        } else if (status == 1) {
            cm.sendSimple("Please choose what type of #eWarrior Equipment#n you wish to buy.\r\n\r\n#b"
                + "#L0#Shoes#l\r\n#L1#Bottom#l\r\n#L2#Top#l\r\n#L3#Overalls#l\r\n#L4#Gloves#l\r\n#L5#Hats#l\r\n#L6#Shields#l\r\n#L7#One-Handed Axe#l\r\n#L8#Two-Handed Axe#l\r\n#L9#One-Handed BW#l\r\n#L10#Two-Handed BW#l\r\n#L11#One-Handed Sword#l\r\n#L12#Two-Handed Sword#l\r\n#L13#Spear#l\r\n#L14#Pole Arm#l");
        } else if (status == 2) {
            cm.dispose();
            if (selection == 0) {
                cm.openShop (1995);
            } else if (selection == 1) {
                cm.openShop (2006);
            } else if (selection == 2) {
                cm.openShop (2007);
            } else if (selection == 3) {
                cm.openShop (2008);
            } else if (selection == 4) {
                cm.openShop (2009);
            } else if (selection == 5) {
                cm.openShop (2010);
            } else if (selection == 6) {
                cm.openShop (2011);
            } else if (selection == 7) {
                cm.openShop (2012);
            } else if (selection == 8) {
                cm.openShop (2013);
            } else if (selection == 9) {
                cm.openShop (2014);
            } else if (selection == 10) {
                cm.openShop (2015);
            } else if (selection == 11) {
                cm.openShop (2016);
            } else if (selection == 12) {
                cm.openShop (2017);
            } else if (selection == 13) {
                cm.openShop (2018);
            } else if (selection == 14) {
                cm.openShop (2019);
            }
        } else {
            cm.sendDefaultMessage();
        }
    }
}
