/**
 * @npc : Chunji
 * @id : 9000007
 * @location : All home towns
 * @author : System of NinjaMS
 * @function : D-Ranked mission Quest NPC
 */
var status = 0;
var dull = 4031135;
var battery = 4031115;
var spirit = 4031215;
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
            txt += " I'm the Mission D manager of NinjaMS.";
            if(cm.getPlayer().getMission() < 6) {
                txt += " You can get new D missions from me ";
                txt += "press Ok if you are Ready to get the next mission";
            } else {
                txt += "I do not deal with higher Missions. You may be want to check out my friends ";
            }
            cm.sendOk(txt);
        } else if (status == 1){
            if(cm.getPlayer().getMission() == 0){
                if(cm.getPlayer().getMobKilled() > 1000){
                    cm.sendOk("Congratulations on completing your first D mission successfully");
                    cm.giveRebirth();
                    cm.missionComplete();
                    cm.dispose();
                } else {
                    cm.sendOk("You should have 1000 mobkills to complete this mission. You can check your mobkills using command \@ninjaglare");
                    cm.dispose();
                }
            }else if(cm.getPlayer().getMission() == 1){
                if(cm.getPlayer().getBossKilled() > 1000){
                    cm.sendOk("Congratulations on completing your second D mission successfully");
                    cm.giveRebirth(2);
                    cm.missionComplete();
                    cm.dispose();
                } else {
                    cm.sendOk("You should have 1000 bosskills to complete this mission. You can check your bosskills using command \@ninjaglare");
                    cm.dispose();
                }
            } else if(cm.getPlayer().getMission() == 2){
                if(cm.getPlayer().getFame() > 100){
                    cm.sendOk("Congratulations on completing your third D mission successfully");
                    cm.giveRebirth(3);
                    cm.getPlayer().missionComplete();
                    cm.dispose();
                } else {
                    cm.sendOk("You should have 100 fame to complete this mission. ");
                    cm.dispose();
                }
            } else if(cm.getPlayer().getMission() == 3){
                if(cm.getPlayer().getNinjaTensu() >= 1){
                    cm.sendOk("Congratulations on completing your 4th D mission successfully");
                    cm.giveRebirth(4);
                    cm.getPlayer().reduceNinjaTensu();
                    cm.getPlayer().missionComplete();
                    cm.dispose();
                } else {
                    cm.sendOk("You should have 1 ninjatensu to complete this mission. ");
                    cm.dispose();
                }
            } else if(cm.getPlayer().getMission() == 4){
                if(cm.haveItem(dull, 69) && cm.haveItem(battery, 69) && cm.haveItem(spirit, 69)){
                    cm.gainItem(dull, -69);
                    cm.gainItem(battery, -69);
                    cm.gainItem(spirit, -69);
                    cm.sendOk("Congratulations on completing your last D mission successfully. Now you can proceed to The forest deep in Ellin where the fairies live for your Rank C Missions");
                    cm.giveRebirth(5);
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


