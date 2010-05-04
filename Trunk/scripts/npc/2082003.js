/* Author: Sunny
	NPC Name: Corba 
	Map(s):	Maple Road : Entrance - Mushroom Town Training Camp (0)
	Description: First NPC
 */
importPackage(net.sf.odinms.client);

var status = 0;
var text = "";

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {   
    if (cm.getPlayer().getMapId() == 0 ) {
        if (mode == -1) {
            cm.voteMSG();
            cm.dispose();
            return;
        } else {
            if (mode == 1)
                status++;
            else
                status--;
            if (status == 0) {
                text = " Hello #b#h #!#k";
                text += " #dWelcome#k to #rNinjaMS.#k";
                text += " We are currently still adding many features,";
                text += " Please be patient while this happens.";
                text += " Also please be sure to vote every 24";
                text += " hours at #bhttp://ninjams.org/vote#k";
                text += " for special prizes.";
                text += " Talk to Charles in Henesys for rewards.";
                text += " Please try to donate if possible at the forums";
                text += " Please take part in our community activities";
                text += " (Forums /IRC / Radio)";
                cm.sendNext(text);
            } else if (status == 1){
                text = " The rates for our server vary depending on";
                text += " the village you choose. Each village is";
                text += " special for their own rates.";
                text += " You may choose your village, but be aware,";
                text += " #ryou may only choose ONE village for your";
                text += " ENTIRE ACCOUNT (all 6 characters).";
                text += " #n#b These are the village base rates:\r\n"
                text += " Leaf Village ~ 1000x exp / 3000x meso / 10x drop /10x bossdrop \r\n";
                text += " Sand Village ~ 600x exp / 5000x meso / 10x drop /10x bossdrop\r\n";
                text += " Rock Village ~ 600x exp / 3000x meso / 30x drop /10x bossdrop\r\n";
                text += " Cloud Village ~ 600x exp / 3000x meso / 10x drop /20x bossdrop\r\n";
                text += " Mist Village ~ 750x exp / 4000x meso / 20x drop /12x bossdrop\r\n";
                cm.sendNext(text);
            } else if (status == 2) {
                text = " Villages: This is your home town for your village.";
                text += " Each village is different. If you do choose to";
                text += " train at a different village your rates will be";
                text += " reduced by 50%. This village is for your entire account.";
                text += " Please see .@ninjaglare for your rates.";
                cm.sendNext(text);
            } else if (status == 3){
                text = " Custom Spawns: There are many places to train here.";
                text += " Every village has a training area.";
                text += " For the village training areas go to either";
                text += " the left portal or right portal, which ever";
                text += " suits you the best. Also henesys hunting ground";
                text += " I,II, and III have custom spawns.";
                text += " Rebirthing is quite easy too.";
                cm.sendNext(text);
            } else if (status == 4){
                text = "Custom Features: Our server is very unique in many ways. ";
                text += "We are the creators of many client hacks, for v62 and 75 ";
                text += " for example; UFJ, spamhack, swearhack, supertubi etc., ";
                text += " Also we have developed a 3 keyboard feature.";
                text += " What this is, is a keyboard for every path.";
                text += " One keyboard for the adventure path, one for KoC path,";
                text += " and one for GMS mode. Be sure to .@save<path>kb whenever";
                text += " changing paths.(EX: For ADVENTURE path you would put .";
                text += "@saveADVkb) Another thing is we have GMS mode. ";
                text += "What this is, is putting you into Global Maplestory mode";
                text += " to do fun stuff like kpqing, jq, boss pq, dojo, and";
                text += " many other cool features. This makes it so you don't";
                text += " just 1 hit kill everything which will be boring as fuck.";
                cm.sendNext(text);
            } else if(status == 5){
                text = "#d#nNPC's:#e Unlike many other servers where all NPCs are ";
                text += " clustered in one map, in NinjaMS, we have the NPCs placed";
                text += " all over the ninja world. Most impoertant NPCs you will";
                text += " need in the beginning is located in your home town";
                text += " (village) or Henesys. To get to henesys just talk to";
                text += " spinel, town maps, then Henesys.";
                text += " Also in Dead Man's gorge, there are 2 very important";
                text += " NPC's. Joko and Fiona. Joko teaches you KageBunshin";
                text += " (Clones) and the Fame Quest for the Missions.";
                text += " Fiona is the jq person.";
                cm.sendNext(text);
            } else if (status == 6){
                text = "Rebirthing: It is quite simple here at NinjaMS.";
                text += " Once you have obtained a certain level(usually 200";
                text += " and over) you will receive a message that says you";
                text += " can rebirth. The level to rebirth depends on the";
                text += " number of rebirths you have. The more rebirths,";
                text += " the higher the level is. After you have gotten to";
                text += " that level, simply just do .@rebirth and you rebirth";
                text += " to a beginner. Talk to Lohd in your home village to";
                text += " change jobs. Or you can do .@rebirthjob which";
                text += " rebirths you to the same job, for a fee.";
                text += " Every 250th rb will requirw some thing special";
                text += " 250th RB will require you to finish Rank D Missions ";
                cm.sendNext(text);
            } else if (status == 7){
                text = "Missions: This a very cool feature of NinjaMS.";
                text += " These are challenging quests used for you to";
                text += " advance and be awarded. Talk to Chun Ji for more";
                text += " information and what you need for the mission.";
                text += " After a certain amount of rebirths you are required";
                text += " to do these, so we suggest getting started ASAP.";
                cm.sendNext(text);
            } else if (status == 8){
                text = "If you have any doubts or questions, please type @guide";
                text += "and check out our guide NPC before asking others";
                text += " If you are a lazy bum and choose tom ask everything";
                text += " with out reading this text or @guide, you will not be";
                text += " welcomed by our server. Too bad So sad! :p";
                text += " Stop being lazy and look around before panicking";
                text += " Use forums and IRC for all the help you need";
                text += " Enjoy the Server and help others do the same";
                text += "Have Fun!";
                cm.sendNext(text);
            } else if (status == 9){
                text = " For now, to choose village, hunt the monsters in this map";
                text += " Bring me 69 special etc items from the mobs in the next map \r\n";
                text += " #v4000351# ~ #t4000351# ";
                cm.sendOk(text);
            } else if (status == 10) {
                if (cm.getPlayer().getReborns() > 0 || cm.getPlayer().getVillage().getId() > 0){
                    cm.warp(40000, 0);
                    cm.dispose();
                    return;
                } else {
                    cm.warp(1, 0);
                    cm.dispose();
                }
            } else {
                cm.warp(40000, 0);
                cm.dispose();
            }
        }
    } else if ((cm.getPlayer().getMapId() > 0) && (cm.getPlayer().getMapId() < 4)) {
        if (cm.getPlayer().getReborns() > 0 || cm.getPlayer().getVillage().getId() > 0){
            cm.warp(40000, 0);
            cm.dispose();
            return;
        } else {
            if (mode == -1) {
                cm.voteMSG();
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
                    var ban = Array(0, 1002067, 1002014, 1002068, 1002066, 1002069);
                    cm.gainItem(4000351, -69);
                    cm.getPlayer().setVillage(selection);
                    cm.gainStatItem(ban[selection], 69, 10, 10);
                    cm.sendOk("You have chosen the "+ cm.getPlayer().getVillage().getName() +" Village. you have gained a head protector of your village");
                    cm.sendServerNotice(5," [The Elitist Ninjas] "+ cm.getPlayer().getName() + " has just created his first character. Lets welcome our new "+cm.getPlayer().getVillage().getName()+ " villager.")
                    cm.getPlayer().goHome();
                    cm.dispose();
                }
            }
        }
    }
}