/* [NPC]
	Charity Box - Donation NPC
	Location - Aquarium;
 */

var status = 0;
var dpoint;
var damount;
var itemid;
var newname;
var chairs = Array (3010000,3010001,3010002,3010003,3010004,3010005,3010006,3010007,3010008,3010009,3010010,3010011,
    3010012,3010013,3010014,3010015,3010016,3010017,3010018,3010019,3010022,3010023,3010024,3010025,3010026,
    3010028,3010040,3010041,3010045,3010046,3010047,3010057,3010058,3010072,3011000);

var mount1 = Array (1902000,// - Hog
    1902001,// - Silver Mane
    1902002,// - Red Draco
    1912000// - Saddle
    );

var mount2 = Array(1912003,// - Frog Cover
    1912004,// - Ostrich Cover
    1902008,// - Frog
    1902009,// - Ostrich
    1902011,// - Turtle
    1902012,// - Yeti
    1912008,// - Yeti Cover
    1912007// - Turtle Mount
    );


var mount3 = Array (1902005,// - Mimiana
    1902006,// - Mimio
    1912005// - Saddle
    );

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
        if (status == 0) {
            dpoint = cm.getPlayer().getDPoints();
            damount = cm.getPlayer().getDAmount();
            var fuck1 = "Hello #b#h ##k. I'm #rThe Charity Box#k.";
            fuck1 += "I'm a npc to help other donators who support this awesome server.";
            fuck1 += "These are the stuff I can do for you.";
            fuck1 += "\r\n#dYour total Donated Amount :#e#r";
            fuck1 += " " + damount + " ";
            fuck1 += " #n \r\n #dYour Current DPoints : #e#r";
            fuck1 += " " + dpoint + " ";
            fuck1 += "#b\r\n#L1# Change Name #r(5 Dpoints)#l#b";
            fuck1 += "#b\r\n#L2# Get all Pet loot EQ #r(5 Dpoints)#l#b";
            fuck1 += "\r\n#L3# Get 100k NX #r(1 DPoint)#l#b";
            fuck1 += "\r\n#L4# Get 1337 stat 100 WA Item#r(5 Dpoints)#l#b";
            fuck1 += "\r\n#L5# Get a perma chair #r(1 Dpoints)#l#b";
            fuck1 += "\r\n#L6# Get 1000 Tao #r(5 DPoints)#l#b";
            fuck1 += "\r\n#L7# Get MaxStatItem #r(50 DPoints)#l#b";
            fuck1 += "\r\n#L8# Get 10 RB #r(2 DPoints)#l#b";
            fuck1 += "\r\n#L9# Get 10 of monster attack package#r(1 DPoints)#l#b";
            fuck1 += "\r\n#L10# Get Magic Scales #r(50 DPoints)#l#b";
            fuck1 += "\r\n#L11# Get 500 fame #r(3 DPoints) #l#b";
            fuck1 += "\r\n#L12# Get a mount package #r(3 DPoints) #l#b";
            cm.sendSimple(fuck1);
        } else if (status == 1) {
            if (selection == 1){
                cm.sendOk("Please use command \#namechange to change name.");
                cm.dispose();
            } else if (selection == 2){
                if (dpoint >= 5 || cm.getPlayer().isJounin()){
                    if(cm.checkSpace(1302000, 4)){
                        cm.getPlayer().modifyDPoints(-5);
                        cm.gainTaggedItem(1812000, 1);
                        cm.gainTaggedItem(1812001, 1);
                        cm.gainTaggedItem(1812004, 1);
                        cm.gainTaggedItem(1812005, 1);
                        cm.sendOk("There you go. Have Fun");
                        cm.dispose();
                    } else {
                        cm.sendOk("You do not have enough space in inventory");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("You do not have enough dPoints.");
                    cm.dispose();
                }
            } else if (selection == 3){
                if (dpoint >= 1 || cm.getPlayer().isJounin()){
                    cm.getPlayer().modifyDPoints(-1);
                    cm.gainNX(20000);
                    cm.getPlayer().saveToDB(true);
                    cm.sendOk("You have gained Maple Points. Check Cash Shop :)");
                } else {
                    cm.sendOk("You do not have enough dPoints.");
                    cm.dispose();
                }
            } else if (selection == 4){
                if (dpoint >= 5 || cm.getPlayer().isJounin()) {
                    var fuckla = "Enter the ItemId of what you want. If you try to get GM items you will get Scammed :";
                    cm.sendGetText(fuckla);
                    status = 9;
                } else {
                    cm.sendOk("You do not have the herbs and Spices");
                    cm.dispose();
                }
            } else if (selection == 5){
                if (dpoint >= 2 || cm.getPlayer().isJounin()){
                    var ok = "Choose Which Chair you want. Every one needs to sit and rest at times #b";
                    for (i = 0; i < chairs.length; i++){
                        ok += "\r\n#L" + i + "##v" + chairs[i] + "# ~ #t" + chairs[i] + "##l\r\n";
                    }
                    cm.sendSimple(ok);
                    status = 199;
                } else {
                    cm.sendOk("You do not have the herbs and Spices");
                    cm.dispose();
                }
            } else if (selection == 6){
                if (dpoint >= 2 || cm.getPlayer().isJounin()){
                    cm.getPlayer().modifyDPoints(-2);
                    cm.gainItem(4032016, 200);
                    cm.sendOk("You have gained a gold ");
                } else {
                    cm.sendOk("You do not have enough dPoints.");
                    cm.dispose();
                }
            } else if (selection == 7){
                if (dpoint >= 50) {
                    var fuckla = "Enter the ItemId of what you want. If you try to get GM items you will get Scammed :";
                    cm.sendGetText(fuckla);
                    status = 99;
                } else {
                    cm.sendOk("You do not have enough dPoints.");
                    cm.dispose();
                }
            } else if (selection == 8) {
                if (dpoint >= 2 || cm.getPlayer().isJounin()) {
                    if(cm.getPlayer().getRemainingAp() < 31767){
                        cm.getPlayer().modifyDPoints(-2);
                        cm.giveRebirth(10);
                        cm.sendOk("You have gained 10 Rb");
                        cm.dispose();
                    } else {
                        cm.sendOk("You cannot have more than 32767 Stats");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("You do not have enough dPoints.");
                    cm.dispose();
                }
            } else if (selection == 9) {
                if(dpoint >= 1 || cm.getPlayer().isJounin()){
                    cm.getPlayer().gainItem(2102008, 5);
                    cm.getPlayer().gainItem(2102009, 5);
                    cm.getPlayer().modifyDPoints(-1);
                    cm.dispose();
                } else {
                    cm.sendOk("You dont have enough dPoints");
                    cm.dispose();
                }
            } else if (selection == 10){
                if(dpoint >= 50){
                    if(cm.getPlayer().checkSpace(1812006)){
                        cm.getPlayer().modifyDPoints(-50);
                        cm.gainItem(1812006, 1);
                        cm.sendOk("Have fun with your magic scales");
                        cm.dispose();
                    } else {
                        cm.sendNext("You don't seem to have enough inventory slots");
                        cm.dispose();
                    }
                }
            } else if (selection == 11){
                if (dpoint >=3){
                    cm.gainFame(500);
                    cm.getPlayer().modifyDPoints(-3);
                } else {
                    cm.sendOk("You are not Genin");
                    cm.dispose();
                }
            } else if (selection == 12){
                if(dpoint >= 3){
                    var lol = "#e#dPlease choose what you want : #n#b";
                    lol += "\r\n#L1#Adventurer Mount package#l";
                    lol += "\r\n#L2#Cygnus Mount Package#l";
                    lol += "\r\n#L3#NX Mount Package#l";
                    lol += "\r\n#r#eNote : Each of them will cost you 3 dpoints.";
                    lol += "Make sure you have Inventory slots or you will not be refunded";
                    cm.sendSimple("lol");
                } else {
                    cm.sendOk("You dont have enough dPoints");
                    cm.dispose();
                }
            }
        } else if (status == 2){
            if(selection == 1){
                for(i = 0; i < mount1.length; i++){
                    cm.gainItem(mount1[i]);
                }
                cm.sendOk("Enjoy your new Mounts");
            } else if (selecion == 2){
                for(i = 0; i < mount2.length; i++){
                    cm.gainItem(mount2[i]);
                }
                cm.sendOk("Enjoy your new Mounts");
            } else if (selection == 3){
                for(i = 0; i < mount3.length; i++){
                    cm.gainItem(mount3[i]);
                }
                cm.sendOk("Enjoy your new Mounts");
            }
            cm.getPlayer().modifyDPoints(-3);
            cm.dispose();
        } else if (status == 10){
            itemid = cm.getText();
            itemid = Math.floor(itemid);
            itemid = Math.round(itemid);
            var fucklar1 = "Please confirm if you want this Item to be 1337 by pressing Next.\r\n The Item You entered is";
            cm.sendNext(fucklar1 + " #v" +itemid+ "# - #t" +itemid+ "#");
        } else if (status == 11) {
            if (dpoint >= 5 || cm.getPlayer().isJounin()){
                cm.getPlayer().modifyDPoints(-5);
                cm.gainStatItem(itemid, 1337, 100, 100);
                cm.sendOk("There you go. Have Fun");
            } else {
                cm.sendOk("You do not have enough dPoints.");
                cm.dispose();
            }
        }else if (status == 100){
            itemid = cm.getText();
            itemid = Math.floor(itemid);
            itemid = Math.round(itemid);
            var fucklar1 = "Please confirm if you want this Item to be max stat by pressing Next.\r\n The Item You entered is";
            cm.sendNext(fucklar1 + " #v" +itemid+ "# - #t" +itemid+ "#");
        } else if (status == 101) {
            if (dpoint >= 50 || cm.getPlayer().isJounin()){
                cm.getPlayer().modifyDPoints(-50);
                cm.gainStatItem(itemid, 32767, 100, 100);
                cm.sendOk("There you go. Have Fun");
            } else {
                cm.sendOk("You do not have enough dPoints.");
                cm.dispose();
            }
        } else if (status == 200){
            cm.getPlayer().modifyDPoints(-2);
            cm.gainItem(chairs[selection], 1);
            cm.sendOk("Have fun with your new chair :)");
            cm.dispose();
        } else {
            cm.voteMSG();
            cm.dispose();
        }
    }
}


		