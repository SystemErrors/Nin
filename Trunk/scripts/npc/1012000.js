/**
 * @npc : Regular cab
 * @id : 1012000
 * @location : All home towns and henesys
 * @author : System of NinjaMS
 * @function : custom training ground warper
 */
var status = 0;
var maps = Array (240040511, 260010000, 102010000, 200010000, 251010000, //skele
    240011000, 260010100, 102020000, 200020000, 251010100, //pap
    240010000, 260010200, 102020100, 200030000, 251010200, // anego
    240010100, 260010300, 102020200, 200040000, 251010300, // crow
    240020400, 260010700, 101040000, 200010100, 251010400, // Azure ocelet
    240020000, 260010600, 106000000, 200060000, 251010401, // Scarlet Phoenix
    240010800, 260010400, 101040003, 200040001, 251010402, // Hsalf
    240010501, 260020401, 101040002, 200050001, 251010102); // Mano maps
var village = Array ("Leaf", "Sand", "Rock", "Cloud", "Mist");
var mobs = Array ("Skelegon", "Fake Pap", "Female Boss", "Crow",
    "Azure Ocelet", "Scarlet Phoenix", "Hsalf", "mano");



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
        } else {
            cm.voteMSG();
            return;         
        }
        if(status == 0){
            cm.sendNext("hello #b#h #! I'm the training ground warp manager of NinjaMS. I can warp you to various custom training gronds. Are you interested?")
        }else if (status == 1){
            var text = "#e#rPlease choose where you want to go : #n";
            for (i = 0; i < maps.length; i++){
                var x = Math.floor((i / 5))
                if((i % 5) == 0){
                    text += "#d#e\r\n\r\n\r\n ------------------------------------ \r\n";
                    text += mobs[x] + " Custom Spawn ";
                    text += "\r\n ------------------------------------#n";
                }
                text += "#b\r\n#L" + i + "#" +  village[i % 5];
                text += " Village .#l";
            }
            if(cm.getPlayer().getReborns() < 50){
                text += "#d#e\r\n\r\n\r\n ------------------------------------ \r\n";
                text += "Special map for beginners upto 50 Rebirths ";
                text += "\r\n ------------------------------------#n";
                text += "\r\n#L"+(maps.length + 10)+"#CubeSlimes map#l";
                text += "\r\n#L"+(maps.length + 20)+"#Dark lord Clone map#l";
            }
            cm.sendSimple(text);
        } else {
            if (selection < (maps.length+5)){
                cm.warp(maps[selection]);
            } else if(selection == (maps.length + 10)){
                cm.warp(800010001);
            } else {
                cm.warp(800020101);
            }
            cm.voteMSG();
        }
    }
}