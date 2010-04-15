var status = 0;
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
            cm.sendOk("Hello there#b #h #. #kI'm Santa Claus. I became a Ninja here in #eNinjaMS. #n I can give you a special Bandana if you are a good kid. Press Ok if you want one.");
        } else if (status == 1){
            if((cm.getAccountId() <= 100) && !cm.haveItem(1002419, 1)){
                cm.sendOk("You seem like a good kid. You are listed in the first 100 ninjas of this ninja world. Here! have a mark of Beta. If you ever lose it, come back and find me :)");
                cm.gainStatItem(1002419, 69, 69, 69);
            } else {
                cm.sendOk("oh you have been a bad kid. I'm a very kind hearted person so I will give you a headband of your village");
                var ban = Array(0, 1002067, 1002014, 1002068, 1002066, 1002069);
                var it = cm.getPlayer().getVillage().getId();
                cm.gainStatItem(ban[it], 69, 10, 10);
            }
            cm.dispose();
        }
    }
}
