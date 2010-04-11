var status = 0;

function start() {
    if (cm.getPlayer().getGMSMode() != 1){
        cm.needGMSModeMsg(1);
        return;
    }
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        cm.voteMSG();
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            cm.voteMSG();
            cm.dispose();
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            var simple = "Hello #h #. I'm fiona the nifty ninja of the NinjaMS. Welcome to our ninja World. I manage the Jump Quest for NinjaMS. So what you want to do? #b\r\n#L1#JumpQuest / Bonus rewards#l\r\n#L2#Duck out#l#k";
            if (cm.getPlayer().getMapId() == 100000000){
                cm.sendSimple(simple);
            } else {
                cm.sendYesNo("Grats on finishing the JQ "+(cm.getLastJQ() + 1)+". Do you want to get your reward?");
                status = 100;
            }
        } else if (status == 1){
            if (selection == 1){
                if (cm.getPlayer().getReborns() < 10){
                    cm.sendOk("Jq is available only for those who have passed 10 Rebirths. Head to Room 13/ 14 to RB faster. It takes barely 15 minutes");
                    cm.dispose();
                } else {
                    cm.sendSimple("So you are interested in JQ? Please behave and do not hack. Jq is only available for those above 10 Rebirths and only in channel 3. Select what you want to do #b\r\n#L0#Start a JQ#l\r\n#L1#Claim rewards#l\r\n#k");
                    status = 200;
                }
            } else if (selection == 2) {
                cm.sendOk("Ok bye. I shall kill you for disturbing me");
                cm.getPlayer().kill();
                cm.dispose();
            }
        } else if (status == 101){
            if (cm.getC().getChannel() == 3) {
                cm.jqComplete();
                cm.dispose();
            } else {
                cm.sendOk("trying to cheat eh? ");
                cm.warp(100000000);
                cm.dispose();
            }
        } else if (status == 201) {
            if (selection == 0) {
                if (cm.getC().getChannel() == 3){
                    var fuck = " Are you ready to do JQ number ";
                    fuck += (cm.getLastJQ() + 1);
                    fuck += " If yes, press Ok"
                    cm.sendOk(fuck);
                    status = 210;
                } else {
                    cm.sendOk("JumpQuest is only available in channel 3");
                }
            } else if (selection == 1) {
                cm.sendSimple("Here are the bonus Rewards: #b\r\n#L0# Gamble with 1 JQ point#l#b\r\n#L1#Get 5 rebirths #rfor 1 jq point#l#b\r\n#L2#bow-tie#l#k");
                status = 220;
            }
        } else if (status == 211){
            if (cm.getC().getChannel() == 3) {                
                cm.startJQ((cm.getLastJQ() + 1));
                cm.dispose();
            } else {
                cm.sendOk("Only in channel 3");
                cm.dispose();
            }
        } else if (status == 221) {
            if (selection == 0) {
                cm.jqBonus();
                cm.dispose();
            } else if (selection == 1) {
                cm.jqRBBonus();
                cm.dispose();
            } else if (selection == 2) {
                cm.superYellowSnowShoes();
                cm.dispose();
            }
        } else {
            cm.dispose();
        }
    }
}
