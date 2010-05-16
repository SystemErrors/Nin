var status = 0;
var it = 0;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
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
        if (status == 0) {
            it = cm.getPlayer().getVillage().getId();
            var tt = "Hello there#b #h #. #kI'm Santa Claus.";
            tt += "I became a Ninja here in #eNinjaMS. #n I can";
            tt += "give you a special Bandana if you are a good kid.";
            tt += "#r\r\n\r\n Select which one you want #b\r\n\r\n #L1# Mark of";
            tt += " the beta (only to the first 100 accounts made";
            tt += " in this server)#l\r\n#L2# Village Head protector#l";
            cm.sendSimple(tt);
        } else if (status == 1){
            if(selection == 1) {
                if(cm.getAccountId() <= 100){
                    if(!cm.haveItem(1002419, 1)){
                        cm.sendOk("You seem like a good kid. You are listed in the first 100 ninjas of this ninja world. Here! have a mark of Beta. If you ever lose it, come back and find me :)");
                        cm.gainStatItem(1002419, 69, 69, 69);
                    } else {
                        cm.sendOk("looks like you already have a mark of beta");
                    }
                } else {
                    cm.sendOk("looks like you are not in the first 100 accounts made here");
                }
            } else {
                cm.sendOk("oh you have been a bad kid. I'm a very kind hearted person so I will give you a headband of your village");
                var ban = Array(0, 1002067, 1002014, 1002068, 1002066, 1002069);
               
                cm.gainStatItem(ban[it], 69, 10, 10);
            }
            cm.dispose();
        }
    }
}
