var status = 0;
var beauty = 0;
var haircolor = Array();
var skin = new Array(0, 1, 2, 3, 4, 5, 9, 10);
var lol = false;

var fhair = Array(31000, 31010, 31020, 31030, 31040, 31050, 31060, 31070, 31080, 31090, 31100, 31110, 31120, 31130, 31140, 31150, 31160, 31170, 31180, 31190, 31200, 31210, 31220, 31230, 31240, 31250, 31260, 31270, 31280, 31290, 31300, 31310, 31320, 31330, 31340, 31350, 31400, 31410, 31420, 31430, 31440, 31450, 31460, 31470, 31480, 31490, 31510, 31520, 31530, 31540, 31550, 31560, 31570, 31580, 31590, 31600, 31610, 31620, 31630, 31640, 31650, 31660, 31670, 31680, 31690, 31700, 31710, 31720, 31730, 31740, 31750, 31760, 31770, 31790, 31800, 31810, 31820, 31830, 31840, 31850, 31860, 31870, 31880, 31890, 31910);
var fface = Array(21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21009, 21010, 21011, 21012, 21013, 21014, 21016, 21017, 21018, 21019, 21020, 21021, 21022, 21023, 21024, 21025, 21026);
var hair = Array(30000, 30020, 30030, 30040, 30050, 30060, 30110, 30120, 30130, 30140, 30150, 30160, 30170, 30180, 30190, 30200, 30210, 30220, 30230, 30240, 30250, 30260, 30270, 30280, 30290, 30300, 30310, 30320, 30330, 30340, 30350, 30360, 30370, 30400, 30410, 30420, 30430, 30440, 30450, 30460, 30470, 30480, 30490, 30510, 30520, 30530, 30540, 30550, 30560, 30570, 30580, 30590, 30600, 30610, 30620, 30630, 30640, 30650, 30660, 30700, 30710, 30720, 30780, 307090, 30800, 30810, 30730, 30760, 30770, 30780, 30790, 30800, 30810, 30820, 30830, 30840, 30850, 30860, 30870, 30880, 30890, 30900, 30910, 30920);
var face = Array(20000, 20001, 20002, 20003, 20004, 20005, 20006, 20007, 20008, 20009, 20010, 20011, 20012, 20013, 20014, 20015, 20016, 20017, 20018, 20019, 20020, 20021, 20022, 20023, 20024, 20025, 20026, 20027, 20028);
var facenowhite = Array (20015, 20016, 20017, 20025, 20027, 21017);
var facee = Array(20000,20001,20002,20003,20004,
20005, 20006, 20007, 20008, 20009,
20010, 20011, 20012, 20013, 20014,
20018, 20019, 20020, 20021, 20022,
20023, 20024, 20026, 20028
);
var ffacee = Array(21000, 21001, 21002, 21003, 21004,
21005, 21006, 21007, 21008, 21009,
21010, 21011, 21012, 21013, 21014,
21016, 21018, 21019, 21021, 21022,
21023, 21024, 21025, 21026
);

var hairnew = Array();
var facenew = Array();
var colors = Array();

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;

        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            for (i = 0; i < facenowhite.length; i++){
                if (facenowhite[i] == current){
                    lol = true;
                    break;
                }
                if(cm.getChar().getFace() % 1000 > 700){
                    lol = true;
                    break;
                }
            }
            cm.sendSimple("Hello there, #b#h ##k, I am the  #eStylist#n of NinjaMS.\r\n\r\nI #bprovide skin changes, eye changes and hair changes...#efor free#n#k!\r\n\r\n" +
                "Please select your area of interest.\r\n\r\n#b"
                + "#L1#Men's Styles#l\r\n"
                + "#L2#Women's Styles#l\r\n");
        } else if (status == 1) {
            if (selection == 1) {
                status = 9;
                cm.sendSimple("Please choose what you want...\r\n\r\n#b"
                    + "#L0#Skin Colour#l\r\n"
                    + "#L1#Hair#l\r\n"
                    + "#L2#Hair Colour#l\r\n"
                    + "#L3#Eye#l\r\n"
                    + "#L4#Eye Colour#l");
            } else if (selection == 2) {
                status = 19;
                cm.sendSimple("Please choose what you want...\r\n\r\n#b"
                    + "#L0#Skin Colour#l\r\n"
                    + "#L1#Hair#l\r\n"
                    + "#L2#Hair Colour#l\r\n"
                    + "#L3#Eye#l\r\n"
                    + "#L4#Eye Colour#l");
            } else {
                cm.voteMSG();
                cm.dispose();
            }
        } else if (status == 10) {
            if (selection == 0) {
                beauty = 1;
                cm.sendStyle("Oh you look so handsome!", skin);
            } else if (selection == 1) {
                beauty = 2;
                hairnew = Array();
                for(var i = 0; i < hair.length; i++) {
                    hairnew.push(hair[i] + parseInt(cm.getChar().getHair() % 10));
                }
                cm.sendStyle("Oh you look so handsome!", hairnew);
            } else if (selection == 2) {
                beauty = 3;
                haircolor = Array();
                var current = parseInt(cm.getChar().getHair()/10)*10;
                for(var i = 0; i < 8; i++) {
                    haircolor.push(current + i);
                }
                cm.sendStyle("Oh you look so handsome!", haircolor);
            } else if (selection == 3) {
                beauty = 4;
                facenew = Array();
                if (!lol){
                    for(var i = 0; i < face.length; i++) {
                        facenew.push(face[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
                    }
                } else {
                    for(var i = 0; i < facee.length; i++) {
                        facenew.push(facee[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
                    }
                }
                cm.sendStyle("Oh you look so handsome!", facenew);
            } else if (selection == 4) {
                beauty = 5;
                var current = cm.getChar().getFace() % 100 + 20000;
                colors = Array();
                if (!lol){
                    colors = Array(current , current + 100, current + 200, current + 300, current +400, current + 500, current + 600, current + 700, current + 800);
                } else {
                    colors = Array(current , current + 100, current + 200, current + 300, current +400, current + 500, current + 600, current + 700);
                }
                cm.sendStyle("Oh you look so handsome! #r For White eyes, choose women styles", colors);
            }
        } else if (status == 20) {
            if (selection == 0) {
                beauty = 1;
                cm.sendStyle("Oh you look so Sexyy!!!!!", skin);
            } else if (selection == 1) {
                beauty = 2;
                hairnew = Array();
                for(var i = 0; i < fhair.length; i++) {
                    hairnew.push(fhair[i] + parseInt(cm.getChar().getHair() % 10));
                }
                cm.sendStyle("Oh you look so Sexyy!!!!!", hairnew);
            } else if (selection == 2) {
                beauty = 3;
                haircolor = Array();
                var current = parseInt(cm.getChar().getHair()/10)*10;
                for(var i = 0; i < 8; i++) {
                    haircolor.push(current + i);
                }
                cm.sendStyle("Oh you look so Sexyy!!!!!", haircolor);
            } else if (selection == 3) {
                beauty = 4;
                facenew = Array();
                if (!lol){
                    for(var i = 0; i < fface.length; i++) {
                        facenew.push(fface[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
                    }
                } else {
                    for(var i = 0; i < ffacee.length; i++) {
                        facenew.push(ffacee[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
                    }
                }
                cm.sendStyle("Oh you look so Sexyy!!!!!", facenew);
            } else if (selection == 4) {
                beauty = 5;
                var current = cm.getChar().getFace() % 100 + 21000;
                colors = Array();
                if (!lol){
                    colors = Array(current , current + 100, current + 200, current + 300, current +400, current + 500, current + 600, current + 700, current + 800);
                } else {
                    colors = Array(current , current + 100, current + 200, current + 300, current +400, current + 500, current + 600, current + 700);
                }
                cm.sendStyle("Oh you look so Sexyy!!!!!", colors);
            }
        } else if (status == 11 || status == 21) {
            cm.dispose();
            if (beauty == 1){
                cm.setSkin(skin[selection]);
            }
            if (beauty == 2){
                cm.setHair(hairnew[selection]);
            }
            if (beauty == 3){
                cm.setHair(haircolor[selection]);
            }
            if (beauty == 4){
                cm.setFace(facenew[selection]);
            }
            if (beauty == 5){
                cm.setFace(colors[selection]);
            }
        } else {
            cm.voteMSG();
            cm.dispose();
        }
    }
}