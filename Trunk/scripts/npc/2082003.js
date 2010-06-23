/**
 * @npc : Corba
 * @id : 2082003
 * @location : Map 0
 * @author : System of NinjaMS
 * @function : Starter NPC.
 */

importPackage(net.sf.odinms.client);

var status = 0;
var text = "";

function start() {
    status = -1;
    action(1, 0, 0);
}

function wrong(){
    cm.sendOk("Sorry #b#h #,#k but the answer you gave was wrong. please read the guide again.");
    cm.dispose();
}

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) {
        cm.voteMSG();
        cm.dispose();
        return;
    } else {

        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            text = " Hello there #b#h #!#k";
            text += " #dWelcome#k to #rNinjaMS.#k";
            text += " In order to proceed into the Ninja world,";
            text += " you have to answer a few questions";
            text += " the questions will be from my beginner's tutorial \r\n";
            text += " #rChoose what you want :#b\r\n";
            text += "#L1# Read my beginner's Tutorial#l \r\n";
            text += "#L2# Take the Quiz #l\r\n"
            cm.sendSimple(text);
        } else if (status == 1){
            if(selection == 1){
                text = "If you have any doubts or questions, please type @guide";
                text += "and check out our guide NPC before asking others";
                text += " If you are a lazy bum and choose to ask everything";
                text += " with out reading this tutorial or @guide, you will not be";
                text += " welcomed by our server. Too bad So sad! :p";
                text += " Stop being lazy and look around ";
                text += " Use forums and IRC for all the help you need";
                text += " Enjoy the Server and help others do the same";
                text += "Have Fun!";
                text += " #rChoose which chapter you need: #b\r\n\r\n";
                text += "#L1# Villages #l\r\n";
                text += "#L2# Training #l\r\n";
                text += "#L3# Money #l\r\n";
                text += "#L4# Items #l\r\n";
                text += "#L5# Rates #l\r\n";
                text += "#L6# Keyboard #l\r\n";
                text += "#L7# NPC #l\r\n";
                text += "#L8# Rebirths #l\r\n";
                text += "#L9# Commands #l\r\n";
                text += "#L10# Events and PQs #l\r\n";
                text += "#L11# Community #l\r\n"
                cm.sendSimple(text);
                status = 99;
            } else if(selection == 2){
                text = " Oh a brave ninja wannabe! Are you ready? ";
                cm.sendNext(text);
            }
        } else if (status == 2){
            text = "#dHere is your first question about villages : \r\n\r\n#r"
            var village1 = "How many Villages you can choose for your account?";
            village1 += " \r\n#b#L1# Three#l\r\n#L2# Two#l\r\n#L9# One #l";
            var village2 = "The Exp and meso rates for the Villages are :";
            village2 += "\r\n#b#L9#Different#l\r\n#L1#Same#l\r\n";
            var village3 = "How many times you can change your village?";
            village3 += "\r\n#b#L1#twice#l\r\n#L9#never#l\r\n#L2#thrice#l\r\n#L3#Once#l";
            var village4 = " Each village have their own custom training grounds :";
            village4 += "\r\n#b#L9#True#l\r\n#L1#False#l\r\n";
            var village5 = " Which NPc will take you to the special training grounds?";
            village5 += "\r\n#b#L1#Duru#l\r\n#L2#VIP Cab #l\r\n#L9#RegularCab#l\r\n#L3#Amos#l";
            var qn1 = new Array(village1, village2, village3, village4, village5);
            var n1 = Math.floor(Math.random() * qn1.length);
            text += qn1[n1];
            cm.sendSimple(text);
        } else if (status == 3){
            if (selection == 9){
                text = " #e#bCongratulations on getting the previous question right#n#d";
                text += " Here is the question about Training in NinjaMS :#r\r\n\r\n";
                var t1 = " In NinjaMS there are village specific custom training grounds :";
                t1 += "\r\n#b#L9#True#l\r\n#L1#False#l\r\n";
                var t2 = " Which NPC will take you to the custom training grounds?";
                t2 += "\r\n#b#L1#Duru#l\r\n#L2#VIP Cab #l\r\n#L9#RegularCab#l\r\n#L3#Amos#l";
                var t3 = " Training in training grounds of other village will :";
                t3 += "\r\n#b#L1#reduce by 10%#l\r\n#L2#increase by 10% #l";
                t3 += "\r\n#L9#reduce by 50%#l\r\n#L3#increase by 50%#l";
                var qn2 = new Array(t1, t2,t3);
                var n2 = Math.floor(Math.random() * qn2.length);
                text += qn2[n2];
                cm.sendSimple(text);
            } else {
                wrong();
            }
        } else if (status == 4){
            if (selection == 9){
                text = " #e#bCongratulations on getting the previous question right#n#d";
                text += " Here is the question about money in NinjaMS :#r\r\n\r\n";
                var m1 = "What currency system we have in NinjaMS?";
                m1 += "\r\n#b#L1#Chicken System#l\r\n#L9#Tao system #l";
                m1 += "\r\n#L2#Golden Leaf System#l\r\n#L3#Meso bag system#l";
                var m2 = "How much does the command to buy tao @buytao cost?";
                m2 += "\r\n#b#L1#1 billion mesos#l\r\n#L3#10 million mesos#l";
                m2 += "\r\n#L2#1 million mesos#l\r\n#L9#2 billion mesos#l";
                var m3 = "What is the command to get 2 bil mesos using tao?";
                m3 += "\r\n#b#L1#@mesos#l\r\n#L9#@mesarz #l";
                m3 += "\r\n#L2#@buymesos#l\r\n#L3#@mesauce#l";
                var m4 = "You cannot get Taos from vote rewards,";
                m4 += " donation rewards, boss drops and GM events\r\n";
                m4 += "\r\n#b#L1#True#l\r\n#L9#False#l\r\n";
                var qn3 = new Array(m1, m2, m3, m4);
                var n3 = Math.floor(Math.random() * qn3.length);
                text += qn3[n3];
                cm.sendSimple(text);
            } else {
                wrong();
            }
        }else if (status == 5){
            if (selection == 9){
                text = " #e#bCongratulations on getting the previous question right#n#d";
                text += " Here is the question about Equipments in NinjaMS :#r\r\n\r\n";
                var i1 = " Which NPC sells regular Weapons?";
                i1 += "#b\r\n#L9#Master Jobs(statues)#l\r\n#L1#iDolly#l";
                i1 += "\r\n#L2#NLC MALL#l\r\n#L3#Hokage#l";
                var i2 = " Which NPC sells special Weapons?";
                i2 += "#b\r\n#L1#Master Jobs(statues)#l\r\n#L9#iDolly#l";
                i2 += "\r\n#L2#NLC MALL#l\r\n#L3#Hokage#l";
                var i3 = " Which gachapon give special Weapons?";
                i3 += "#b\r\n#L2#Aquarium#l\r\n#L1#Elnath#l";
                i3 += "\r\n#L9#Mushroom shrine#l\r\n#L3#henesys#l";
                var i4 = "NLC Mall sell all armours available and NX items:";
                i4 += "#b\r\n#L9#True#l\r\n#L1#false#l";
                var qn4 = new Array(i1, i2, i3, i4);
                var n4 = Math.floor(Math.random() * qn4.length);
                text += qn4[n4];
                cm.sendSimple(text);
            } else {
                wrong();
            }
        } else if (status == 6){
            if (selection == 9){
                text = " #e#bCongratulations on getting the previous question right#n#d";
                text += " Here is the question about rates in NinjaMS :#r\r\n\r\n";
                var r1 = "The Exp and meso rates for the Villages are :";
                r1 += "\r\n#b#L9#Different#l\r\n#L1#Same#l\r\n";
                var r2 = "1000x exp / 3000x meso / 10x drop /10x bossdrop is";
                r2 += " the rates of \r\n";
                r2 += "\r\n#b#L9#Leaf village#l\r\n#L3#Sand village#l";
                r2 += "\r\n#L2#Rock Village#l\r\n#L1#Cloud village#l";
                r2 += "\r\n#L4#Mist village#l";
                var r3 = "600x exp / 5000x meso / 10x drop /10x bossdrop is";
                r3 += " the rates of \r\n";
                r3 += "\r\n#b#L3#Leaf village#l\r\n#L9#Sand village#l";
                r3 += "\r\n#L2#Rock Village#l\r\n#L1#Cloud village#l";
                r3 += "\r\n#L4#Mist village#l";
                var r4 = "600x exp / 3000x meso / 30x drop /10x bossdrop is";
                r4 += " the rates of \r\n";
                r4 += "\r\n#b#L2#Leaf village#l\r\n#L3#Sand village#l";
                r4 += "\r\n#L9#Rock Village#l\r\n#L1#Cloud village#l";
                r4 += "\r\n#L4#Mist village#l";
                var r5 = "600x exp / 3000x meso / 10x drop /20x bossdrop is";
                r5 += " the rates of \r\n";
                r5 += "\r\n#b#L1#Leaf village#l\r\n#L3#Sand village#l";
                r5 += "\r\n#L2#Rock Village#l\r\n#L9#Cloud village#l";
                r5 += "\r\n#L4#Mist village#l";
                var r6 = "750x exp / 4000x meso / 20x drop /12x bossdrop is";
                r6 += " the rates of \r\n";
                r6 += "\r\n#b#L1#Leaf village#l\r\n#L3#Sand village#l";
                r6 += "\r\n#L2#Rock Village#l\r\n#L4#Cloud village#l";
                r6 += "\r\n#L9#Mist village#l";
                var r7 = "How many % extra rate do Genins aka donators get?";
                r7 += "\r\n#b#L9#25#l\r\n#L1#10#l";
                r7 += "\r\n#L2#15#l\r\n#L3#50#l";
                var r8 = "Modes to increase rates can be turned on by talking to NPC :";
                r8 += "\r\n#b#L1#iDolly#l\r\n#L9#Shadrion#l\r\n";
                r8 += "\r\n#L3#Maple Administrator#l\r\n#L4#Hokage#l\r\n";
                var qn5 = new Array(r1,r2,r3,r4,r5,r6,r7,r8);
                var n5 = Math.floor(Math.random() * qn5.length);
                text += qn5[n5];
                cm.sendSimple(text);
            } else {
                wrong();
            }
        } else if (status == 7){
            if (selection == 9){
                text = " #e#bCongratulations on getting the previous question right#n#d";
                text += " Here is the question about Keyboards in NinjaMS :#r\r\n\r\n";
                var k1 = " How many keyboards can you have?";
                k1 += "#b\r\n#L9#three#l\r\n#L1#2#l";
                k1 += "\r\n#L2#1#l\r\n#L3#5#l";
                var k2 = " Which command will you use to save adventurer path keyboard?";
                k2 += "#b\r\n#L1#@loadadvkb#l\r\n#L2#@saveGMSkb#l";
                k2 += "\r\n#L9#@saveadvkb#l\r\n#L3#@savekockb#l";
                var qn6 = new Array(k1, k2);
                var n6 = Math.floor(Math.random() * qn6.length);
                text += qn6[n6];
                cm.sendSimple(text);
            } else {
                wrong();
            }
        } else if (status == 8){
            if (selection == 9){
                text = " #e#bCongratulations on getting the previous question right#n#d";
                text += " Here is the question about NPCs in NinjaMS :#r\r\n\r\n";
                var npc1 = " Whatis the function of NPC Shati in village home towns do?";
                npc1 += "#b\r\n#L9#Cut hair#l\r\n#L1#Rebirth NPC#l";
                npc1 += "\r\n#L2#Item seller#l\r\n#L3#Pot seller#l";
                var npc2 = " What is the function of NPC Miki in FM?";
                npc2 += "#b\r\n#L3#Cut hair#l\r\n#L1#Rebirth NPC#l";
                npc2 += "\r\n#L2#Item seller#l\r\n#L9#Pot seller#l";
                var npc3 = " Whatis the function of NPC iDolly";
                npc3 += "#b\r\n#L2#Cut hair#l\r\n#L1#Rebirth NPC#l";
                npc3 += "\r\n#L9#Tao shop#l\r\n#L3#Pot seller#l";
                var npc4 = " Which is the job advancer NPC in NinjaMS";
                npc4 += "#b\r\n#L3#Maple Admin#l\r\n#L1#iDolly#l";
                npc4 += "\r\n#L2#Duey#l\r\n#L9#Lohd#l";
                var npc5 = " Which is the vote rewards manager NPC in NinjaMS";
                npc5 += "#b\r\n#L3#Maple Admin#l\r\n#L1#iDolly#l";
                npc5 += "\r\n#L9#Charles#l\r\n#L2#Lohd#l";
                var npc6 = " Which is the Mission manager NPC in NinjaMS";
                npc6 += "#b\r\n#L3#Maple Admin#l\r\n#L1#iDolly#l";
                npc6 += "\r\n#L2#Duey#l\r\n#L9#Chunji#l";
                var qn7 = new Array(npc1, npc2, npc3, npc4, npc5, npc6);
                var n7 = Math.floor(Math.random() * qn7.length);
                text += qn7[n7];
                cm.sendSimple(text);
            } else {
                wrong();
            }
        } else if (status == 9){
            if (selection == 9){
                text = " #e#bCongratulations on getting the previous question right#n#d";
                text += " Here is the question about rebirths in NinjaMS :#r\r\n\r\n";
                var rb1 = "What is the level to rebirth in NinjaMS ?"
                rb1 += "\r\n#L1#200#l\r\n#L9#depends on rebirths u have#l\r\n#L2#255#l\r\n";
                var rb2 = "The command to rebirth is :";
                rb2 += "\r\n#L1#@rebirth#l\r\n#L2#@rebirthjob#l\r\n#L9#both#l\r\n";
                var rb3 = "Special requirement for rebirth is for every : ";
                rb3 += "\r\n#L1#200th rb#l\r\n#L9#250th rb#l\r\n#L2#500th rb#l\r\n";
                var qn8 = new Array(rb1, rb2);
                var n8 = Math.floor(Math.random() * qn8.length);
                text += qn8[n8];
                cm.sendSimple(text);
            } else {
                wrong();
            }
        } else if (status == 10){
            if (selection == 9){
                text = " #e#bCongratulations on getting the previous question right#n#d";
                text += " Here is the question about Commands in NinjaMS :#r\r\n\r\n";
                var c1 = "What is the command to open scroll shop?";
                c1 += "#b\r\n#L9#@scroll#l\r\n#L1#@shop#l\r\n#L2#@scrollshop#";
                var c2 = " What command you should use if you got stuck in a map?"
                c2 += "#b\r\n#L1#@unstuck#l\r\n#L9#@home#l\r\n#L2#@dispose#";
                var qn9 = new Array(c1, c2);
                var n9 = Math.floor(Math.random() * qn9.length);
                text += qn9[n9];
                cm.sendSimple(text);
            } else {
                wrong();
            }
        } else if (status == 11){
            if (selection == 9){
                text = " #e#bCongratulations on getting the previous question right#n#d";
                text += " Here is the question about Events and PQs in NinjaMS:#r\r\n\r\n";
                var e1 = "What is the auto event in NinjaMS?";
                e1 += "#b\r\n#L1#Chunin Exams#l\r\n#L2#Ninja Slime bash#l";
                e1 += "\r\n#L9#Ninja Shiken#l\r\n#L3#Dojo#l";
                var e2 = "Which NPC lets you enter BossPQ?";
                e2 += "#b\r\n#L1#Spigelman#l\r\n#L2#Lakelis#l";
                e2 += "\r\n#L3#Spinel#l\r\n#L9#Stirgeman#l";
                var e3 = "What should you do when you want a GM event?";
                e3 += "#b\r\n#L1#Ask the GM#l\r\n#L2#Smega repeatedly#l";
                e3 += "\r\n#L9#Wait for a GM to host one#l";
                e3 += "\r\n#L3#Send your nudies to a GM#l";
                var qn10 = new Array(e1, e2, e3);
                var n10 = Math.floor(Math.random() * qn10.length);
                text += qn10[n10];
                cm.sendSimple(text);
            } else {
                wrong();
            }
        } else if (status == 12){
            if (selection == 9){
                text = " #e#bCongratulations on getting the previous question right#n#d";
                text += " Here is the question about the community of NinjaMS:#r\r\n\r\n";
                var z1 = " If you are stupid and Lazy what you should do? ";
                z1 += "#b\r\n#L1#Dance#l\r\n#L2#Train#l";
                z1 += "\r\n#L9#Leave NinjaMS#l";
                var z2 = "Can you be GM in this server?";
                z2 += "#b\r\n#L9#No#l\r\n#L2#Yes#l";
                var z3 = " What is allowed in NinjaMS?"
                z3 += "#b\r\n#L1#Hacking#l\r\n#L2#Scamming#l";
                z3 += "\r\n#L3#Trolling#l\r\n#L9#none of the above#l";
                var z4 = "If you smega \"can any one train me?\" or \"Can any one give me Tao?\", you are :";
                z4 += "#b\r\n#L1#Good boy#l\r\n#L2#Pro#l";
                z4 += "\r\n#L9#Retarded Nub Fuck who should GTFO#l";
                var qn11 = new Array(z1, z2, z3, z4);
                var n11 = Math.floor(Math.random() * qn11.length);
                text += qn11[n11];
                cm.sendSimple(text);
            } else {
                wrong();
            }
        } else if (status == 13) {
            if(selection == 9) {
                text = " #e#bCongratulations on getting all the questions right#n#d";
                text += "Choose which village you want to join. #r#e the village you choose";
                text += " will be for all 6 character of this account.";
                text += " You CANNOT CHANGE your village, so be careful";
                text += " of what you choose.#n#b These are the village base rates:\r\n"
                text += "#L1# Leaf Village ~ 1000x exp/3000x meso/10x drop/10x bossdrop#l \r\n";
                text += "#L2# Sand Village ~ 600x exp/5000x meso/10x drop/10x bossdrop#l\r\n";
                text += "#L3# Rock Village ~ 600x exp/3000x meso/30x drop/10x bossdrop#l\r\n";
                text += "#L4# Cloud Village ~ 600x exp/3000x meso/10x drop/20x bossdrop#l\r\n";
                text += "#L5# Mist Village ~ 750x exp/4000x meso/20x drop/12x bossdrop#l\r\n";
                cm.sendSimple(text);
            } else {
                wrong();
            }
        } else if (status == 14) {
            if(cm.getPlayer().getVillage().getId() > 0){
                cm.sendOk(" You already have a Village so I will just warp you home");
                cm.getPlayer().goHome();
            } else {
                var ban = Array(0, 1002067, 1002014, 1002068, 1002066, 1002069);
                cm.getPlayer().setVillage(selection);
                cm.gainStatItem(ban[selection], 69, 10, 10);
                cm.sendOk("You have chosen the "+ cm.getPlayer().getVillage().getName() +" Village. you have gained a head protector of your village");
                cm.sendServerNotice(5," [The Elitist Ninjas] "+ cm.getPlayer().getName() + " has just created his first character. Lets welcome our new "+cm.getPlayer().getVillage().getName()+ " villager.")
                cm.getPlayer().goHome();
            }
            cm.dispose();
        } else if (status == 100) {
            switch(selection){
                case 1:
                    text = " Our server has Village system.\r\n\r\n";
                    text += " Every Account has a village from the start\r\n\r\n";
                    text += " You get to choose your village, but be aware,";
                    text += " #ryou may only choose ONE village for your";
                    text += " ENTIRE ACCOUNT (all 6 characters).\r\n\r\n";
                    text += " Each village will have different Rates.\r\n"
                    text += " #n#b You CANNOT change your village";
                    text += " for your account after choosing it\r\n";
                    text += " Each village has specific home town \r\n";
                    text += " You can reach there by using command @home\r\n";
                    text += " Each village has special training grounds\r\n";
                    text += " You can reach there by using NPC Regular cab";
                    text += " found in the home town of your village.";
                    text += " You can reach there by using command @home\r\n";
                    break;
                case 2:
                    text = " With the pretty high exp rate we have,";
                    text += " it is pretty easy to train and gain rebirths.\r\n";
                    text += " there are special training grounds with";
                    text += " custom spawn.\r\n These training grounds";
                    text += " are village specific. \r\n";
                    text += " You can reach the training grounds of all";
                    text += " villages by talking to NPC regular cab\r\n";
                    text += " Training in training grounds of other village,";
                    text += " will reduce your rates by 50%\r\n";
                    break;
                case 3:
                    text = " In NinjaMS, we have Tao system.\r\n";
                    text += " Tao of sights serves as a currency here";
                    text += " You can buy tao of sights by using command";
                    text += " @buytao. It requires 2 billion mesos \r\n";
                    text += " You can convert Tao into mesos by using";
                    text += " command @mesarz. It sets your mesos to 2bil\r\n";
                    text += " You can also get Taos from vote rewards,";
                    text += " donation rewards, boss drops and GM events\r\n";
                    break;
                case 4:
                    text = " Unlike GMS, here you can get the items easily.\r\n";
                    text += " You can get regular weapons from Master jobs";
                    text += " NPC (statues) in all home towns\r\n";
                    text += " You can get special weapons from NPC iDolly or";
                    text += " Weapons gachapon in Mushroom Shrine\r\n";
                    text += " All available armours and NX items are";
                    text += " available from NLC Mall.\r\n ";
                    break;
                case 5:
                    text = " The rates for our server vary depending on";
                    text += " your village.\r\n";
                    text += " #n#b These are the base rates of villages:\r\n"
                    text += " Leaf Village ~ 1000x exp / 3000x meso / 10x drop /10x bossdrop \r\n";
                    text += " Sand Village ~ 600x exp / 5000x meso / 10x drop /10x bossdrop\r\n";
                    text += " Rock Village ~ 600x exp / 3000x meso / 30x drop /10x bossdrop\r\n";
                    text += " Cloud Village ~ 600x exp / 3000x meso / 10x drop /20x bossdrop\r\n";
                    text += " Mist Village ~ 750x exp / 4000x meso / 20x drop /12x bossdrop\r\n";
                    text += " Donators aka genins get 25% extra rates\r\n";
                    text += " Modes to increase your rates temporarily can be turned";
                    text += " on from NPC Shadrion by doing a small Quest\r\n";
                    text += " Training in training grounds of other village,";
                    text += " will reduce your rates by 50%\r\n";
                    break;
                case 6:
                    text = " In NinjaMS we have a 3 keyboard feature.\r\n";
                    text += " We have a different keyboard for every path.\r\n";
                    text += " One keyboard for the adventure path, one for KoC path,";
                    text += " and one for GMS mode.\r\n";
                    text += " Be sure to .@save<path>kb whenever";
                    text += " (ex : @saveadvkb, )";
                    text += " changing paths.(EX: For ADVENTURE path you would put .";
                    text += "@saveADVkb, for Cygnus path @savekockb,";
                    text += " and for GMs mode @savegmskb)\r\n";
                    text += " You can reload these keyboard when you need";
                    text += " using commands : @loadadvkb, @loadkockb, @loadgmskb";
                    break;
                case 7:
                    text = " We have a lot of custom NPCs in NinjaMS\r\n";
                    text += " #rThese are the most important NPCs in NinjaMS";
                    text += " you must know about : \r\n#k";
                    text += " NPCs in your home town (reach there by @home)\r\n";
                    text += " #bSpinel#k - Transporter NPC\r\n";
                    text += " #bLohd#k - Job advancer\r\n";
                    text += " #bMaster Jobs (statues)#k - Weapon seller\r\n";
                    text += " #bRegular Cab #k- Training maps warper\r\n";
                    text += " #bShati#k - Hair and face Stylist NPC\r\n";
                    text += " #bChunji#k - Mission Manager NPC (Quest)\r\n\r\n"
                    text += " #diDolly#k - Tao shop (player NPC in henesys)\r\n";
                    text += " #dHokage#k - Max Stat Item manager (player NPC in henesys)\r\n";
                    text += " #dJoko#k - Quests NPC in map : dead man's gorge\r\n";
                    text += " #dFiona#k - Jump Quest Manager in map : Dead man's gorge\r\n";
                    text += " #dCharles#k - Vote rewards manager in henesys\r\n";
                    text += " #dMiki#k - Pot seller in FM,Hometowns, henesys & Newleafcity\r\n";
                    break;
                case 8:
                    text = "Rebirthing: It is quite simple here at NinjaMS.";
                    text += " Once you have obtained a certain level(200";
                    text += " and above) you will receive a message to inform you that you";
                    text += " can rebirth. The level to rebirth depends on the";
                    text += " number of rebirths you have. The more rebirths,";
                    text += " the higher the level is. After you have got to";
                    text += " that level, simply just do .@rebirth and you rebirth";
                    text += " to a beginner/noblesse. Talk to NPC Lohd in your home village to";
                    text += " change jobs. Or you can do .@rebirthjob which";
                    text += " rebirths you to the same job, for a fee.";
                    text += " Every 250th rb there will be a special requirement to rebirth";
                    text += " 250th RB will require you to finish Rank D Missions ";
                    break;
                case 9:
                    text = "In NinjaMS we have a lot of commands for players.";
                    text += " These are the commands you must know to start with: \r\n";
                    text += " @home - Warps you to your home town from";
                    text += "anywhere (use it if you got stuck in a map)\r\n";
                    text += " @buytao - buys a tao of sight for 2 billion mesos.\r\n";
                    text += " @mesarz - sets your meso to 2 billion for 1 tao of sight\r\n";
                    text += " @unstuck - Use this from another account if";
                    text += " your character is stuck in game\r\n";
                    text += " @commands <1/2/3/4/5/6>- to see the list of all commands\r\n";
                    text += " @guide - guide npc\r\n";
                    text += " @storage - Opens Storage\r\n";
                    text += " @scroll - opens scroll shop\r\n";
                    break;
                case 10:
                    text = "#b In NinjaMs there is an auto event also";
                    text += " called as Ninja Shiken. This event starts";
                    text += " once every 2 hours. You can join this event";
                    text += " by talking to NPC RIA in Ellinia \r\n\r\n";
                    text += " #rCustom KPQ is available in Kerning City\r\n\r\n";
                    text += " #dBossPq is available from Stirgeman in Henesys\r\n\r\n";
                    text += " #bMulung dojo is available from the Dojo";
                    text += " bulletin board in Ellinia\r\n\r\n#r";
                    text += " you are not allowed to annoy the GMs with";
                    text += " \" GMs Event please \".";
                    text += " They will do events when they can";
                    break;
                default:
                    text = "NinjaMS is a very friendly community.";
                    text += " That being said, we do not tolerate";
                    text += " lazy bums who is too lazy to look around,";
                    text += " read guides and forums. If you are too";
                    text += " lazy or stupid or both, this server is #rNOT#k";
                    text += " for you. \r\n";
                    text += " All your questions should preferably be";
                    text += " asked in Forums or IRC\r\n";
                    text += " No one will help you train / earn mesos.";
                    text += " With this much high rates, its too easy ";
                    text += " and you dont need to leech or beg\r\n";
                    text += " Hacking, scamming, begging, trolling is not";
                    text += " allowed in our community\r\n";
                    text += " We are not looking for GM and you cannot";
                    text += " be a Gm in this server. If you ask";
                    text += " about being GM, you will be punished\r\n";
                    text += " All new updates, rules, promotions will be\r\n";
                    text += " announced and handled in the forums or IRC\r\n";
                    text += " Link to our forums : #bhttp://ninjams.org#k\r\n";
                    text += " Link to our IRC : #bhttp://ninjams.info#k\r\n ";
                    text += " Please take part in our community and HAVE FUN!\r\n";
            }
            cm.sendOk(text);
            cm.dispose();
        }  
    }
}