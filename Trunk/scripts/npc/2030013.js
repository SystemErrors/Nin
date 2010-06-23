/**
 * @npc : Adobis
 * @id : 2030010
 * @location : outside Zakum Altar
 * @author : System of NinjaMS
 * @function : Zakum gate keeper NPC.
 */

var status = 0;

function start() {
    if (cm.getPlayer().getGMSMode() < 1 && !cm.getPlayer().isJounin()){
        cm.needGMSModeMsg(5);
        return;
    }
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.voteMSG();
    } else {
        if (mode == 0) {
            cm.voteMSG();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            if (cm.getParty() == null){
                cm.sendOk("Sorry #b#h #, #kbut you are not in a party. To take down a boss like Zakum, you should be in a party.");
                cm.dispose();
                return;
            }
            var party = cm.getParty().getMembers();
            var modecheck = cm.checkPartyGMSMode(5, party);
            var mnq = cm.membersNotQualified(5, party);
            if (!modecheck) {
                cm.sendOk("Sorry #b#h #,#k but not all of your party members are in GMS Mode 5 to participate in this quest. Members not qualified are :" + mnq);
                cm.dispose();
            } else if (cm.getPlayerCount(280030000) == 0) {
                cm.sendSimple("Hey Elite ninja #b#h #!#k Would you like to assemble a team to take on the mighty Zakum?\r\n#b#L1#Lets get this going!#l\r\n\#L2#No, I think I'll wait a bit...#l");
                cm.killMobsInMap(280030000);
                cm.resetReactors(280030000);
            } else {
                cm.sendOk("Sorry #b#h #,#k but The Boss fight seems to have already started. Try again next time.");
                cm.dispose();
            }
        } else if (status == 1) {
            var party1 = cm.getParty().getMembers();
            var mapId = cm.getChar().getMapId();
            var it = party1.iterator();
            var inMap = 0;
            var next = true;
            while (it.hasNext()) {
                var cPlayer = it.next();
                if (cPlayer.getMapid() == mapId) {
                    inMap += 1;
                } else {
                    next = false;
                }
            }
            if (inMap < party1.size()){
                next = false;
            }
            if (next){
                cm.warpParty(280030000);
                cm.dispose();
            } else {
                cm.sendOk("Sorry #b#h #,#k but your party does not have 2 members or they are not in this map. I see " + inMap + " members in this map.");
                cm.dispose();
            }
        }
    }
}
