/**
 * @npc : Chunji
 * @id : 9000007
 * @location : All home towns
 * @author : System of NinjaMS
 * @function : D-RankeC Mission Quest NPC
 */
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection){
    if (mode == -1) {
        cm.voteMSG();
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0){
            var txt = "Hello there #b#h #!.#k";
            txt += " I'm the Mission C manager of NinjaMS.";
            if(cm.getPlayer().getMission() < 10) {
                txt += " You can get new Rank C missions from me ";
                txt += "press Ok if you are Ready to get the next mission";
            } else {
                txt += "I do not deal with higher Missions. You may be want to check out my friends ";
            }
            cm.sendOk(txt);
        } else if (status == 1){
            if(cm.getPlayer().getMission() == 5){
                var bp = cm.getPlayer().getBossPoints();
                if(bp > 20000){
                    cm.sendOk("Congratulations on completing your first C Mission successfully");
                    cm.giveRebirth();
                    cm.getplayer().gainItem(5220000, 1);
                    cm.getPlayer().setBossPoints(bq - 20000);
                    cm.missionComplete();
                    cm.dispose();
                } else {
                    cm.sendOk("You should have 20000 Boss PQ Points to complete this mission. You can check your Boss PQ points using command \@ninjaglare");
                    cm.dispose();
                }
            }else if(cm.getPlayer().getMission() == 6){
                if(cm.getPlayer().getKpqpoints() >= 5){
                    cm.sendOk("Congratulations on completing your second C Mission successfully");
                    cm.giveRebirth(2);
                    cm.getplayer().gainItem(5220000, 2);
                    cm.missionComplete();
                    cm.dispose();
                } else {
                    cm.sendOk("You should have 5 KPQPoints to complete this mission. You can check your KPQpoints using command \@ninjaglare");
                    cm.dispose();
                }
            } else if(cm.getPlayer().getMission() == 7){
                if(cm.getPlayer().getFame() > 200){
                    cm.sendOk("Congratulations on completing your third C Mission successfully");
                    cm.giveRebirth(3);
                    cm.getplayer().gainItem(5220000, 3);
                    cm.getPlayer().missionComplete();
                    cm.dispose();
                } else {
                    cm.sendOk("You should have 200 fame to complete this mission. ");
                    cm.dispose();
                }
            } else if(cm.getPlayer().getMission() == 8){
                if(cm.getPlayer().getExpBoost() >= 25){
                    cm.sendOk("Congratulations on completing your 4th C Mission successfully");
                    cm.giveRebirth(4);
                    cm.getplayer().gainItem(5220000, 4);                    
                    cm.getPlayer().missionComplete();
                    cm.dispose();
                } else {
                    cm.sendOk("You should have more than 25% exp boost to complete this mission. ");
                    cm.dispose();
                }
            } else if(cm.getPlayer().getMission() == 9){
                if(cm.haveItem(dull, 69) && cm.haveItem(battery, 69) && cm.haveItem(spirit, 69)){
                    cm.gainItem(dull, -69);
                    cm.gainItem(battery, -69);
                    cm.gainItem(spirit, -69);
                    cm.sendOk("Congratulations on completing your last C Mission successfully. Now you can proceed to The forest deep in Ellin where the fairies live for your Rank C Missions");
                    cm.giveRebirth(5);
                    cm.getplayer().gainItem(5220000, 5);
                    cm.getPlayer().missionComplete();
                    cm.dispose();
                } else {
                    cm.sendOk("You should have 69 each of #v"+dull+"# ~ #t"+dull+"#, #v"+battery+"# ~ #t"+battery+"#,  #v"+spirit+"# ~ #t"+spirit+"#,  to complete this mission. ");
                    cm.dispose();
                }
            } else {
                cm.sendOk("Now you can proceed to The forest deep in Ellin where the fairies live for your rank C Missions");
                cm.dispose();
            }
        }
    }
}


