/* Author: Sunny
	NPC Name: Corba 
	Map(s):	Maple Road : Entrance - Mushroom Town Training Camp (0)
	Description: First NPC
*/
importPackage(net.sf.odinms.client);

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (cm.getPlayer().getReborns() > 0 || cm.getPlayer().getVillage().getId() > 0){
        cm.warp(40000, 0);
        cm.dispose();
        return;
    }
    if (cm.getPlayer().getMapId() == 0 ) {
        if (mode == -1) {
            cm.dispose();
            return;
        } else {
            if (mode == 1)
                status++;
            else
                status--;
            if (status == 0) {
                var text = "Hello #h #! Welcome to the world of NinjaMS.";
                text += " I'll train you to become a great ninja.";
                text += " Choosing a village is the first step in your shinobi way.";
                text += " You need to prove yourself worthy of joinin a village";
                text += " by hunting 25 etc items fromt he mobs in this map.";
                text += " The Exp/Drop/Meso/BossDrop rates are mainly governed";
                text += " by villages you choose.\r\n #r#e the village you choose";
                text += " will be for all 6 character of this account.";
                text += " You cannot change your village, so be careful";
                text += " of what you choose.#n#b These are the village base rates:\r\n"
                text += " Leaf Village ~ 750x exp / 3000x meso / 10x drop /10x bossdrop \r\n";
                text += " Sand Village ~ 400x exp / 5000x meso / 10x drop /10x bossdrop\r\n";
                text += " Rock Village ~ 400x exp / 3000x meso / 30x drop /10x bossdrop\r\n";
                text += " Cloud Village ~ 400x exp / 3000x meso / 10x drop /20x bossdrop\r\n";
                text += " Mist Village ~ 550x exp / 4000x meso / 20x drop /12x bossdrop\r\n";
                text += " Each village will have 2 clans available for the taking. ";
                text += " Clans will be introduced at a later time. ";
                cm.sendOk(text);
            } else if (status == 1) {
                var tex = " For now, to choose village, hunt the monsters in this map";
                tex += " Bring me 69 special etc items from the mobs in the next map \r\n";
                tex += " #v4000351# ~ #t4000351# ";
                cm.sendOk(tex);
            } else if (status == 2) {
                cm.warp(1, 0);
                cm.dispose();
            } else if (status == 3) {
                cm.warp(40000, 0);
                cm.dispose();
            } else if (status == 4) {
                cm.warp(100000000, 0);
                cm.dispose();
            }
        }
    } else if ((cm.getPlayer().getMapId() > 0) && (cm.getPlayer().getMapId() < 4)) {
        if (mode == -1) {
            cm.dispose();
        } else {
            if (mode == 1)
                status++;
            else
                status--;
            if (status == 0) {
                if (cm.haveItem(4000351, 69)){
                    var tt = "Seems like you have collected 69 etc I asked for. ";
                    tt += "Choose which village you want to join. #r#e the village you choose";
                    tt += " will be for all 6 character of this account.";
                    tt += " You cannot change your village, so be careful";
                    tt += " of what you choose.#n#b These are the village base rates:\r\n"
                    tt += "#L1# Leaf Village ~ 750x exp / 3000x meso / 10x drop /10x bossdrop \r\n#l";
                    tt += "#L2# Sand Village ~ 400x exp / 5000x meso / 10x drop /10x bossdrop\r\n#l";
                    tt += "#L3# Rock Village ~ 400x exp / 3000x meso / 30x drop /10x bossdrop\r\n#l";
                    tt += "#L4# Cloud Village ~ 400x exp / 3000x meso / 10x drop /20x bossdrop\r\n#l";
                    tt += "#L5# Mist Village ~ 550x exp / 4000x meso / 20x drop /12x bossdrop\r\n#l";
                    cm.sendSimple(tt);
                } else {
                    cm.sendOk("What are you waiting for? Go hunt these mobs and bring me 69 #v4000351# - #t4000351#");
                    cm.dispose();
                }
            } else if (status == 1) {
                cm.gainItem(4000351, -69);
                cm.getPlayer().setVillage(selection);
                cm.sendOk("You have chosen the "+ cm.getPlayer().getVillage().getName() +" Village"); 
                cm.warp(40000, 0);
                cm.dispose();
            }
        }
    }
}