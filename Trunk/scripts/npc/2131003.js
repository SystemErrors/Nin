var status = 0;
function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.voteMSG();
        cm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            if(cm.getPlayer().getFame() >= 32750){
                cm.sendOk("Hello thar ninja! how are you?");
                cm.dispose();
                return;
            } else {
                cm.sendOk("Hello #b#h #!#k If you have lunar wrist band from Joko, I can give you fames in exchange");
            }
        } else if (status == 1){
            if(cm.haveItem(4031199, 1)){               
                cm.gainItem(4031199, -1);
                cm.gainFame(10);
                cm.sendOk("You have gained 10 fame in exchange for a wrist band. Bring more of these wrist bands to me");
                cm.dispose();
            }else {
                cm.sendOk("Go and get lunar wrist bands from Joko in dead man's gorge");
                cm.dispose();
            }
        }
    }
}