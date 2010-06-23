/** * @NPC: 9901001 - iDOlly * @Location: Henesys * @Function: Tao Shop * @author: System * @credits : iDolly, Oliver**/

var status = 0;
var chairs = Array (3010000,3010001,3010002,3010003,3010004,3010005,3010006,3010007,3010008,3010009,3010010,3010011,
    3010012,3010013,3010014,3010015,3010016,3010017,3010018,3010019,3010022,3010023,3010024,3010025,3010026,
    3010028,3010040,3010041,3010045,3010046,3010047,3010057,3010058,3010072,3011000);
var noob = new Array(1050018,1051017,1072344,1050127,1051140,1050100,1051098,1012070,1012071,1012073,1102084,1102041,1102086,1102042,1082149,1002357,1122000);
var tao = 4032016;
var megaphones = new Array(5072000, 5076000, 5390000, 5390001, 5390002, 5090000);
var megaphonesprice = new Array(1, 2, 5, 5, 5, 1);
var gachatix = 5220000;
var seltype = 1;
var mounts = Array(1902000,1902001,1902002,1912000,1902008,1902009,1902011,1902012,1912003,1912004,1912007,1912008,1902005,1902006,1912005);
var morphs = Array(2210000, 2210001, 2210002, 2210003, 2210005, 2210006, 2210007, 2210008, 2210010, 2210011, 2210012, 2210016, 2210017, 2210018, 2210021,
    2210032, 2210033);
var special =  Array (1302036,// - Maple Flag : 1000 Days - (no description)
1302049,// - Glowing Whip - (no description)
1302058,// - Maple Umbrella - (no description)
1302062,// - Pumpkin Basket (2006 SE) - (no description)
1312013,// - Green Paint Brush - (no description)
1312014,// - Black Paint Brush - (no description)
1322003,// - Lollipop - (no description)
1322010,// - Square Shovel - (no description)
1322011,// - Pointed Shovel - (no description)
1322012,// - Red Brick - (no description)
1322027,// - Frying Pan - (no description)
1322031,// - Pig Illustrated - (no description)
1322033,// - Goblin Bat - (no description)
1322051,// - Fruity Bamboo - (no description)
1322062,// - Crushed Skull - (no description)
1332032,// - Christmas Tree - (no description)
1402009,// - Wooden Baseball Bat - (no description)
1402010,// - Aluminum Baseball Bat - (no description)
1422011,// - Sake Bottle - (no description)
1442016,// - Dark Snowboard - (no description)
1442017,// - Blood Snowboard - (no description)
1442021,// - Yellow Mop - (no description)
1442022,// - White Mop - (no description)
1442030,// - Maple Snowboard - (no description)
1442046,// - Super Snowboard - (no description)
1442047,// - Yellow Valentine Rose - (no description)
1442048,// - Red Valentine Rose - (no description)
1442049,// - Blue Valentine Rose - (no description)
1442050,// - White Valentine Rose - (no description)
1442066,// - Bullseye Board - (no description)
1442061,// - Versalmas Cactus - (no description)
1302025,// - Red Umbrella - (no description)
1302026,// - Black Umbrella - (no description)
1302027,// - Green Umbrella - (no description)
1302028,// - Light Purple Umbrella - (no description)
1302029); // - Beige Umbrella - (no description)

function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == 1){
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        var sel1 = "#r Hello #h #! I'm iDolly the Tao shop manager of NinjaMS.";
        sel1 += "These are the shops I manage. Choose which one you want : #b";
        sel1 += "\r\n #L1# Smega/note Shop #r - (Pink - 1 tao ea and Yellow - 2) #l#b ";
        sel1 += "\r\n #L2# New-bie shop - #r - (1 tao each) #l#b ";
        sel1 += "\r\n #L3# Mounts shop #r - (1 tao each)[Expires in 24 hrs]#l #b";
        sel1 += "\r\n #L4# Morph Shop #r - (1 tao each)#l #b"
        sel1 += "\r\n #L5# Chair Shop #r - (1 tao each)[Expires in 24 hrs] #l#b ";
        sel1 += "\r\n #L6# Some Special Weapons Shop #r - (1 Tao Each) #l";
        sel1 += "\r\n";
        cm.sendSimple(sel1);
    } else if (status == 1) {
        var sel2= " ";
        if(selection == 1) {
            sel2 += " #dChoose what you need : #r(If you annoy others with spam you will be muted or Jailed)\r\n";
            for (i = 0; i < megaphones.length; i++)	
                sel2 += "#b #L"+i+"##t"+megaphones[i]+ "# - #r " + megaphonesprice[i] + " tao each#l\r\n";
            cm.sendSimple(sel2);
            status = 9;
        } else if (selection == 2) {
            sel2 += " #dChoose what you need :  #rThey all cost 1 Tao Each\r\n";
            for (i = 0; i < noob.length; i++)		
                sel2 += "#b #L"+i+"##t"+noob[i]+ "# - #v" + noob[i]+ "##l\r\n";
            cm.sendSimple(sel2);
            status = 14;
        } else if (selection == 3){
            sel2 += " #dChoose what you need : #r\r\n";
            for (i = 0; i < mounts.length; i++)
                sel2 += "#b #L"+ i +"# #v" + mounts[i] + "# - #r 1 tao each#l\r\n";
            cm.sendSimple(sel2);
            status = 19;
        } else if (selection == 4) {
            sel2 += " #dChoose what you need : #r\r\n";
            for (i = 0; i < morphs.length; i++)
                sel2 += "#b #L"+ i +"# #v" + morphs[i] + "# - #r 1 tao each#l\r\n";
            cm.sendSimple(sel2);
            status = 24;
        } else if (selection == 5) {
            sel2 += " #dChoose what you need : #r\r\n";
            for (i = 0; i < chairs.length; i++)		
                sel2 += "#b #L"+i+"# - #v"+chairs[i]
                + "# -#r 1 tao each#l\r\n";
            cm.sendSimple(sel2);
            status = 29;
        } else if (selection == 6){
            sel2 += " #dChoose what you need : #r\r\n";
            for (i = 0; i < special.length; i++)
                sel2 += "#b #L"+i+"# - #v"+special[i]
                + "# -#r 1 tao each#l\r\n";
            cm.sendSimple(sel2);
            status = 34;
        }
    } else if (status == 10) {
        seltype = selection;
        var prompt = "So, you want me to make some #t" + megaphones[selection] + "#s  for " + megaphonesprice[selection] +" each ?In that case, how many do you want me to make?";
        cm.sendGetNumber(prompt,1,1,100);
    } else if (status == 11){
        var qty = selection;
        if (cm.getPlayer().haveSight(qty * megaphonesprice[seltype])){
            cm.gainItem(tao, - (qty * megaphonesprice[seltype]));
            cm.gainItem(megaphones[seltype], qty);
            cm.sendOk("You have gained " + qty + " #t"+megaphones[seltype]+"#.");
        } else {
            cm.sendOk("You do not have enough Tao");
        }
        cm.dispose();
    } else if (status == 15){
        if (cm.getPlayer().haveSight(1)){
            cm.gainItem(tao, - 1);
            cm.gainItem(noob[selection], 1);
            cm.sendOk("You have gained a #t"+noob[selection]+"#.");
        } else {
            cm.sendOk("You do not have enough Tao");
        }
        cm.dispose();
    } else if (status == 20){
        if (cm.getPlayer().haveSight(1)){
            cm.gainItem(tao, - 1);
            cm.gainExpiringItem(mounts[selection], 1440); // 24 hrs
            cm.sendOk("You have gained a expiring mount : #v"+ mounts[selection]+"# .");
        } else {
            cm.sendOk("You do not have enough Tao");
        }
        cm.dispose();
    } else if (status == 25){
        if (cm.getPlayer().haveSight(1)){
            cm.gainItem(tao, - 1);
            cm.gainItem(morphs[selection], 1);
            cm.sendOk("You have gained a Morph : #t"+ morph[selection] +"#.");
        } else {
            cm.sendOk("You do not have enough Tao");
        }
        cm.dispose();
    } else if (status == 30){
        if (cm.getPlayer().haveSight(1)){
            cm.gainItem(tao, - 1);
            cm.gainExpiringItem(chairs[selection], 1440); // 24 hrs
            cm.sendOk("You have gained a expiring Chair : #t" + chairs[selection] + "#.");
        } else {
            cm.sendOk("You do not have enough Tao");
        }
        cm.dispose();
    } else if (status == 35){
        if (cm.getPlayer().haveSight(1)){
            cm.gainItem(tao, - 1);
            cm.gainItem(special[selection], 1);
            cm.sendOk("You have gained a special weapon : #t"+special[selection]+"#.");
        } else {
            cm.sendOk("You do not have enough Tao");
        }
        cm.dispose();
    } else {
        cm.voteMSG();
        cm.dispose();
    }
}