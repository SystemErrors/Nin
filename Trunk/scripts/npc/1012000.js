var status = 0;
var maps = Array (240040511, 260010000, 102010000, 200010000, 251010000, //skele
    240011000, 260010100, 102020000, 200020000, 251010100, //pap
    240011000, 260010200, 102020100, 200030000, 251010200, // anego
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
        cm.sendDefaultMessage();
    } else {
        if (mode == 1) {
            status++;
        } else {
            cm.voteMSG();
            cm.dispose();
            return;         
        }
        if (status == 0){
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
            cm.sendSimple(text);
        } else {
            cm.warp(maps[selection]);
            cm.dispose();
        }
    }
}