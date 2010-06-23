/**
 * @npc : Eckhart (statue)
 * @id : 1104206
 * @location : all hojme towns
 * @author : System of NinjaMS
 * @function : Santa claus.
 */
var status = 0;
var it = 0;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.voteMSG();
    } else {
        if (status >= 0 && mode == 0) {
            cm.voteMSG();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            it = cm.getPlayer().getVillage().getId();
            var tt = "Hello there#b #h #. #kI'm Santa Claus.";
            /*tt += "I became a Ninja here in #eNinjaMS. #n I can";
            tt += "give you a special Bandana if you are a good kid.";
            tt += "#r\r\n\r\n Select which one you want #b\r\n\r\n #L1# Mark of";
            tt += " the beta (only to the first 100 accounts made";
            tt += " in this server)#l\r\n#L2# Village Head protector#l";
            cm.sendSimple(tt);*/
            cm.sendOk(tt);
        } else {
            cm.voteMSG();
        }
    }
}
