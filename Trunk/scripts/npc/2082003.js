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
	if (cm.getChar().getMapId() == 0 ) {
		if (mode == -1) {
			cm.dispose();
		} else {
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			if (cm.getPlayer().getReborns() > 0) {
				cm.warp(40000, 0);
				cm.dispose();
			} else {				
				if(cm.getPlayer().getVillage().getId > 0){
					cm.warp(40000, 0);
					cm.dispose();
				} else {
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
				}
			}
		} else if (status == 1) {				
			var tex = " For now, to choose village, hunt the monsters in this map";
				tex += " Bring me 25 special etc items that belongs to the village you want to join \r\n";
				tex += " #v4001063# ~ #t4001063# - Represents Cloud Village ";
				tex += " #v4000332# ~ #4000332# - Represents Sand Village";
				tex += " #v4000131# ~ #t4000131# - Represents Rock Village";
				tex += " #v4000241# ~ #t4000241# - Represents Leaf Village";
				" #v4000415# ~ #t4000415# - Represents Mist Village ";
                        cm.sendOk(tex);
                        cm.dispose();
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
	} else {
		if (mode == -1) {
			cm.dispose();
		} else {
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendNext("This is the image room where your first training program begins. In this room, you will have an advance look into the job of your choice.");
		} else if (status == 1) {
			cm.sendPrev("Once you train hard enough, you will be entitled to occupy a job. You can become a Bowman in Henesys, a Magician in Ellinia, a Warrior in Perion, and a Thief in Kerning City...");
		} else if (status == 2) {
			cm.dispose();
			}
		}
	}
}