/**
 * @npc : Lea - Guild emblem creator
 * @id : 2010008
 * @location : Guild headquarters
 * @author : OdinMS team
 * @function : Guild Emblem manager.
 */
var status = 0;
var sel;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;

        if (status == 0) {
            cm.sendSimple("Hello there Elite ninja #b#h #!#kWhat would you like to do?\r\n#b#L0#Create/Change your Guild Emblem#l#k");
        }else if (status == 1){
            sel = selection;
            if (selection == 0){
                if (cm.getChar().getGuildRank() == 1){
                    cm.sendYesNo("Creating or changing Guild Emblem costs #b" + cm.getChar().emblemCost() + " Tao of sights#k, are you sure you want to continue?");
                } else {
                    cm.sendOk("You must be the Guild Leader to change the Emblem.  Please tell your leader to speak with me.");
                    cm.dispose();
                }
            }				
        } else if (status == 2){
            if (sel == 0){
                cm.getChar().genericGuildMessage(17);
                cm.dispose();
            }
        }
    }
}
