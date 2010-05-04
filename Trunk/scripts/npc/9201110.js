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
            cm.sendNext("Hello #b#h ##k, #n, I can help you get the armours and weapons of the #e#rThief and Night Walker#k#n class.");
        } else if (status == 1) {
            cm.sendSimple("Please choose what type of #eTheif Equipment#n you wish to buy.\r\n\r\n#b"
                + "#L0#Shoes#l\r\n#L1#Bottoms#l\r\n#L2#Tops#l\r\n#L4#Gloves#l\r\n#L5#Hats#l\r\n#L6#Shields#l\r\n#L7#Daggers#l\r\n#L8#Claws#l\r\n#L9#Throwing Stars#l");
        } else if (status == 2) {
			cm.dispose();
            if (selection == 0) {
                cm.openShop (1993);
            } else if (selection == 1) {
                cm.openShop (1997);
            } else if (selection == 2) {
                cm.openShop (1998);
            }  else if (selection == 4) {
                cm.openShop (2000);
            } else if (selection == 5) {
                cm.openShop (2001);
            } else if (selection == 6) {
                cm.openShop (2002);
            } else if (selection == 7) {
                cm.openShop (2003);
            } else if (selection == 8) {
                cm.openShop (2004);
            } else if (selection == 9) {
                cm.openShop (2005);
            }
			cm.dispose();
        } else {
            cm.voteMSG();
			cm.dispose();
        }
    }
}
