/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


var status = 0;
var pets = new Array(5000000, 5000001, 5000002, 5000003, 5000004, 5000005, 5000006, 5000007, 5000008, 5000009, 5000010, 5000011, 5000012, 5000013, 5000014, 5000015, 5000017, 5000018, 5000020, 5000021, 5000022, 5000023, 5000024, 5000025, 5000034, 5000036, 5000037, 5000039, 5000041, 5000042, 5000044, 5000045, 5000055, 5000058, 5000029, 5000030, 5000031, 5000032, 5000033, 5000049, 5000050, 5000051, 5000052, 5000053);
var petprice = 10;
var tos = 4032016;
var specpetequip = new Array(1812004, 1812005);
var specprice = 250;
var text;

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
            cm.voteMSG();
            return; // return only if using senddefaultmess
        //status--;
        }
        if(status == 0){            
            text += "Ohaithar #b#h #!#k I'm doofus the";
            text += " pet merchant Ninja of NinjaMS.";
            text += " You can buy pets and their equipments";
            text += " from me for a small price.\r\n #rChoose what you need :";
            text += "\r\n\r\n#L1#Buy pets #l";
            text += "\r\n#L2#Buy pet eq#l ";
            text += "\r\n#L3#Buy pet food#l";
            text += "\r\n#L4#Buy special pet eq#l";
            cm.sendSimple(text);
        } else if(status == 1){
            if(selection == 1){
                var selStr = "Please select which pet you wish to obtain. (Price is " + petprice + " tao of sight each)\r\n\r\n";
                for (var i = 0; i < pets.length; i++) {
                    selStr += "#L" + i + "# #v" + pets[i] + "# - #b#t" + pets[i] + "##k#l\r\n";
                }
                cm.sendSimple(selStr);
                status = 9;
            } else if (selection == 2) {
                cm.dispose();
                cm.openShop(10007);
            } else if (selection == 3) {
                cm.dispose();
                cm.openShop(10006);
            }else if (selection == 4) {
                status = 29;
                var selStrr = "Which of the following #especial pet equipment#n do you wish to purchase, #bconsidering they are worth #r" + specprice + " Tao of sight each.\r\n\r\n";
                for (var x = 0; x < specpetequip.length; x++) {
                    selStrr += "#L" + x + "# #v" + specpetequip[x] + "# - #b#t" + specpetequip[x] + "##k#l\r\n";
                }
                cm.sendSimple(selStrr);
            }
        } else if(status == 10){
            var petid = pets[selection];
            if (cm.checkSpace(petid, 1)) {
                if (cm.haveItem(4032016, petprice)) {
                    cm.dispose();
                    cm.sendOk("#e#bHave a nice day#k#n!");
                    cm.gainItem(petid, 1);
                    cm.gainItem(4032016, -petprice);
                } else {
                    cm.dispose();
                    cm.sendOk("You do not have enough Tao of Sight to obtain this.");
                }
            }
        } else if (status == 30) {
            var peti = specpetequip[selection];
            if (cm.checkSpace(peti, 1)) {
                if (cm.haveItem(4032016, specprice)) {
                    cm.dispose();
                    cm.sendOk("#e#bHave a nice day#k#n!");
                    cm.gainItem(peti, 1);
                    cm.gainItem(4032016, -specprice);
                } else {
                    cm.dispose();
                    cm.sendOk("You do not have enough Tao of Sight to obtain this.");
                }
            } else {
                cm.dispose();
                cm.sendOk("You do not have enough inventory space.");
            }
        } else {
            cm.voteMSG();
        }
    }
}
