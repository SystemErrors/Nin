var status = 0;var tao = 4032016;var text = "";var itemid = 0;var accessoryreq = new Array (350, 4000240, //- Small Flaming Feather    250, 2022251, // - Maple Pop ( COMFIRM DROPPING)    350, 4031674, //- Elpam Magnet ( Quest Item )    2500, 4000325 //- Carrot    );    var capreq = new Array (200, 4000017, //Pig Head    1500, 4000077, //Dark Cloud Foxtail    69, 4031093, //- Tasty walnut ( Quest Item )    1500, 4000150 // Ice Piece    );var capereq = new Array (200, 4032007,// - Windraider Badge ( Quest Item )    2000, 4000205,// Dirty Bandage    250, 4000228 //Anesthetic Powder    );var coatreq = new Array (250, 4000125, //- Chief Gray's Sign    2500, 4000137, //- Subordinate D Fingernail    300, 4031458 //- Thanatos's Black Tornado ( Quest Item )    );var glovereq = new Array (1000, 4000061, // - Luster Pixie's Sunpiece    1669, 4000075, //- Triangular Bandana of the Nightghost    250, 4031098 //- All-purpose Clock Spring ( Quest Item )    );var longcoatreq = new Array (1337, 4000437, // - Black Mushroom Spore    250, 4000240,// - Flaming Feather    500, 4001075, // - Cornian's Marrow ( Quest Item )    1337, 4000041 // - Malady's Experimental Frog    );var pantsreq = new Array (2000, 4000153, //- Snorkle    150, 4031215, // - Taurospear's Spirit Rock ( Quest Item )    2000, 4000128 // - Buffy Hat    );var peteqreq = new Array (250, 4031460,// - Cold Heart of a Wolf ( Quest Item )    250, 2022013, // - Sushi(salmon)    250,  2022247, // - Red Gummy Slime    1500, 4032005 // - Typhon Feather    );var shoesreq = new Array (300, 4031253, //- Pianus's Scream ( Quest Item )    2000, 4000066,// - Cloud Foxtail    2000, 4032010 // - Elder Ashes    );var tamingreq = new Array (350, 4031195, //- Aurora Marble ( Quest Item )    350, 4000082, //- Zombie's Lost Gold Tooth    350, 4000124, //- Rombot's Memory Card    700, 4000336 //- Bible of the Corrupt    );function start() {    status = -1;    action(1, 0, 0);}function action(mode, type, selection) {    if (mode == 1)        status++;    else {        cm.voteMSG();        cm.dispose();        return;    }    if (status == 0) {        text = "Hey #h #! I'm Angy the Hokage of NinjaMS.";        text += "I can help you to get Shuriken ";        text += "Items (Max Stat Items) if you would like some.";        text += "\r\n\r\n#rChoose What you want : #b";              text += "\r\n#L1#Shuriken Items#l";        text += "\r\n#L2#Shuriken Items Info#l";        cm.sendSimple(text);    } else if (status == 1) {        var msicount = cm.getPlayer().getMaxStatItems();        if (selection == 1){                        text = " Every shuriken Item has different requirements";            text += " according to the item you choose and the amount of MSI";            text += " you already have and your rebirths.";            text += " shadow shuriken Item can be any item except";            text += " Wizet items or magic scales";            text += " \r\n\r\n#r#ePlease enter the itemid of the Item you want ";            text += " to be made as Max stat in the box ";            cm.sendGetText(text);        } else if (selection == 2){            text = " Every shuriken Item has different requirements";            text += " according to the item you choose and the amount of MSI";            text += " you already have and your rebirths.";            text += " shuriken Item can be any item except";            text += " Wizet items or magic scales";            text += " \r\n\r\n#r#ePlease enter the itemid of the Item you want ";            text += " to be made as Max stat in the box to get the list of requirements ";            cm.sendGetText(text);            status = 49;        }    } else if (status == 2){        itemid = cm.getNumber();        var x = cm.getItemType(itemid);        var taocount = cm.getPlayer().getMaxStatItems() * 200;        var gm = cm.getPlayer().isJounin();        if(cm.isCashItem(itemid)){            taocount *= 2;        }        if(cm.isBlockedItem(itemid)){            cm.sendNext("You have entered a item which is blocked from being obtained");            status = 99;        } else if (cm.nonExistantItem(itemid)){            cm.sendNext("You have entered a item which does not exist");            status = 99;        } else if (!cm.getPlayer().hasAllStatMax() && !gm) {            cm.sendOk("You haven't maxxed all your stats");            status = 99;        } else if (!cm.getPlayer().haveItem(tao, taocount) && !gm) {            cm.sendOk("You are such a poor Fag! you need " + taocount + " Tao Of Sight.");            status = 99;        } else if (!haveEtcReq(itemid) && !gm){            cm.sendNext("You do not have the required Etc Irems" + getEtcReq(itemid));            status = 99;        } else if (cm.getPlayer().getBossPoints() < (taocount * 20) && !gm && !(taocount < 600)){            cm.sendNext("You do not have required boss points You need :"+ taocount * 20);            status = 99;        } else if (cm.getPlayer().getDojoPoints() < taocount  && !gm && !(taocount < 600)){            cm.sendNext("You do not have required dojo points You need :"+ taocount );            status = 99;        } else {            cm.sendOk("Are you sure you want to make #v" + itemid + "# a Shuriken Item? If yes,press Ok");        }           } else if (status == 3) {        if(cm.checkSpace(1302000, 1)){            if (!cm.getPlayer().isJounin()){                removeItems(itemid);                cm.getPlayer().wipeStats();                var taocountd = cm.getPlayer().getMaxStatItems() * 200;                if(cm.isCashItem(itemid)){                    taocountd *= 2;                }                cm.gainItem(tao, -taocountd);                if(taocountd > 600) {                    cm.getPlayer().setBossPoints(cm.getPlayer().getBossPoints() - (taocountd * 20));                    cm.getPlayer().setDojoPoints(cm.getPlayer().getDojoPoints() - taocountd);                }            }            cm.gainStatItem(itemid, 32767, 10, 10);            cm.getPlayer().addMaxStatItem();            cm.sendServerNotice(6, "[Anbu] " + cm.getPlayer().getName() + " - Has just got himself a Shuriken Item. Now he has a Total of " + cm.getPlayer().getMaxStatItems() + " Max stat Items!");            cm.sendOk("Have Fun bish");            cm.dispose();        } else {            cm.sendNext(" You do not have enough inventory space");            cm.dispose();        }    } else if (status == 50){        itemid = cm.getNumber();               cm.sendOk(getEtcReq(itemid));        cm.dispose();    } else if (status == 100){        cm.sendOk(getEtcReq(itemid));        cm.dispose();        } else {        cm.voteMSG();        cm.dispose();    }}function haveEtcReq(itemid){    if (cm.getPlayer().getMaxStatItems() < 3) {        return true;    }    var etcarray;    var x = cm.getItemType(itemid);    switch (x) {        case 1:            etcarray = accessoryreq;            break;        case 2:            etcarray = capreq;            break;        case 3:            etcarray = capereq;            break;        case 4:            etcarray = coatreq;            break;        case 5:            etcarray = glovereq;            break;        case 6:            etcarray = longcoatreq;            break;        case 7:            etcarray = pantsreq;            break;        case 8:            etcarray = peteqreq;            break;        case 9:            etcarray = shoesreq;            break;        default:            etcarray = tamingreq;            break;    }    for(var i = 0; i < etcarray.length; i++){        var item = etcarray[i+1];        var amt = etcarray[i];        if(cm.isCashItem(itemid)){            amt *= 2;        }        cm.dropMessage("req itemid : " + item + " amount : " + amt);        if(!cm.haveItem(item, amt)){            return false;        }        i++;    }    return true;}function removeItems(itemid){    if (cm.getPlayer().getMaxStatItems() < 3) {        return;    }    var etcarray;    var x = cm.getItemType(itemid);    switch (x) {        case 1:            etcarray = accessoryreq;            break;        case 2:            etcarray = capreq;            break;        case 3:            etcarray = capereq;            break;        case 4:            etcarray = coatreq;            break;        case 5:            etcarray = glovereq;            break;        case 6:            etcarray = longcoatreq;            break;        case 7:            etcarray = pantsreq;            break;        case 8:            etcarray = peteqreq;            break;        case 9:            etcarray = shoesreq;            break;        default:            etcarray = tamingreq;            break;    }    for(var i = 0; i < etcarray.length; i++){        var item = etcarray[i+1];        var amt = etcarray[i];        if(cm.isCashItem(itemid)){            amt *= 2;        }        cm.gainItem(item, -amt);        i++;    }}function getEtcReq(itemid){    var etcarray;    var x = cm.getItemType(itemid);    switch (x) {        case 1:            etcarray = accessoryreq;            break;        case 2:            etcarray = capreq;            break;        case 3:            etcarray = capereq;            break;        case 4:            etcarray = coatreq;            break;        case 5:            etcarray = glovereq;            break;        case 6:            etcarray = longcoatreq;            break;        case 7:            etcarray = pantsreq;            break;        case 8:            etcarray = peteqreq;            break;        case 9:            etcarray = shoesreq;            break;        default:            etcarray = tamingreq;            break;    }    var omg = " You need : \r\n";    for(var i = 0; i < etcarray.length; i++){        var item = etcarray[i+1];        var amt = etcarray[i];        if(cm.isCashItem(itemid)){            amt *= 2;        }        omg += amt + " of #v"+item+"# ~ #t"+item+"# \r\n"        i++;    }    var taocountd = cm.getPlayer().getMaxStatItems() * 200;    if(taocountd < 600){        omg = " You should have maxxed all your stats (str, dex, luk & int). \r\n";        omg += " and should have "+taocountd+ " Tao of sights ";    } else {        omg += " You also should have maxxed all your stats (str, dex, int and luk).\r\n";        omg += " and should have "+taocountd+ " Tao of sights ";        omg += " and " + (taocountd * 20) + " bosspoints";        omg += " and " + (taocountd) + " dojo points";    }    return omg;}