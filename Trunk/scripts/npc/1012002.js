/**
 * @npc : Vicious - Item Maker
 * @id : 1012002
 * @location : NLC MALL
 * @author : System of NinjaMS
 * @function : All Accessory Seller.
 */
var status = 0;
var pr = 10;
    var face1 = Array (1010000, 1010001, 1010002, 1010003, 1010004, 1010005, 1010006, 1011000, 1011001, 
    1011002, 1011003, 1012000, 1012001, 1012002, 1012003, 1012004, 1012005, 1012006, 1012007,
    1012008, 1012009, 1012010, 1012011, 1012012, 1012013, 1012014, 1012015, 1012016, 1012017,
    1012018, 1012019, 1012020, 1012021, 1012022, 1012023, 1012024, 1012025, 1012026, 1012027,
    1012028, 1012029, 1012030, 1012031, 1012032, 1012033, 1012034, 1012035, 1012036, 1012037,
    1012038, 1012039, 1012040, 1012041, 1012042, 1012043, 1012044, 1012047, 1012048, 1012049);
    
    var face2 = Array(1012050, 1012051, 1012052, 1012053, 1012054, 1012055, 1012056, 1012057, 1012058, 1012059,
    1012060, 1012061, 1012062, 1012063, 1012070, 1012071, 1012072, 1012073, 1012074, 1012075,
    1012076, 1012077, 1012078, 1012079, 1012080, 1012081, 1012082, 1012083, 1012084, 1012085,
    1012086, 1012087, 1012088, 1012090, 1012096, 1012097, 1012098, 1012099, 1012100, 1012101,
    1012102, 1012103, 1012104, 1012105, 1012106, 1012107, 1012108, 1012109, 1012110, 1012111,
    1012112, 1012113, 1012114, 1012121, 1012122, 1012123, 1012124, 1012125, 1012126, 1012127,
    1012128, 1012129, 1012132, 1012133, 1012134, 1012137, 1012147);
	
	var eye1 = Array (1020000,1021000,1022000,1022001,1022002,1022003,1022004,1022005,1022006,1022007,1022008,1022009,1022010,1022011,	1022012,1022013,1022014,1022015,1022016,1022017,1022018,1022019,1022020,1022021,1022022,1022023,1022024,1022025,1022026,1022027,
	1022028,1022029,1022030,1022031,1022032,1022033,1022034,1022035,1022036,1022037,1022038,1022039);

	var eye2 = Array (1022040,1022041,1022042,1022043,1022044,1022045,1022046,1022047,1022049,1022050,1022051,1022052,1022053,1022054,1022055,1022056,1022057,1022058,1022059,1022060,1022061,1022062,1022063,1022064,1022066,1022068,1022069,1022070,1022071,1022072,1022073,1022074,1022075,1022079,1022081,1022082,1022083,1022084,1022085,1022086,1022087);

var ear1 = Array (1032000,1032001,1032002,1032003,1032004,1032005,1032006,1032007,1032008,1032009,1032010,1032011,1032012,1032013,1032014,1032015,1032016,1032017,1032018,1032019,1032020,1032021,1032022,1032023,1032024,1032025,1032026,1032027,1032028,1032029,1032030);

var ear2 = Array (1032031,1032032,1032033,1032034,1032036,1032038,1032039,1032040,1032041,1032042,1032043,1032044,1032045,1032046,1032047,1032048,1032049,1032050,1032051,1032052,1032053,1032054,1032055,1032056,1032057,1032058,1032059,1032060,1032061,1032070,1032071,1032072);

var shield1 = Array(1092000,1092001,1092002,1092003,1092004,1092005,1092006,1092007,1092008,1092009,1092010,1092011,1092012,1092013,1092014,1092015,1092016,1092017,1092018,1092019,1092020,1092021,1092022,1092023,1092024,1092025,1092026,1092027);

var shield2 = Array(1092028,1092029,1092030,1092031,1092032,1092033,1092034,1092035,1092036,1092037,1092038,1092040,1092041,1092042,1092044,1092045,1092046,1092047,1092049,1092050,1092052,1092056,1092057,1092058,1092059,1092060,1092061,1092062);

var cape1 = Array(1102000, 1102001, 1102002, 1102003, 1102004, 1102005, 1102006, 1102007, 1102008,
    1102009, 1102010, 1102011, 1102012, 1102013, 1102014, 1102015, 1102016, 1102017, 1102018,
    1102019, 1102020, 1102021, 1102022, 1102023, 1102024, 1102025, 1102026, 1102027, 1102028,
    1102029, 1102030, 1102031, 1102032, 1102033, 1102034, 1102035, 1102036, 1102037, 1102038,
    1102039, 1102040, 1102041, 1102042, 1102043, 1102044, 1102045, 1102046, 1102047, 1102048,
    1102049, 1102050, 1102051, 1102052, 1102053, 1102054, 1102055, 1102056, 1102057, 1102058);
    
var cape2 = Array(1102059, 1102060, 1102061, 1102062, 1102063, 1102064, 1102065, 1102066, 1102067, 1102068,
    1102069, 1102070, 1102072, 1102073, 1102074, 1102075, 1102076, 1102077, 1102078, 1102079,
    1102080, 1102081, 1102082, 1102083, 1102084, 1102085, 1102086, 1102087, 1102091, 1102092,
    1102093, 1102094, 1102095, 1102096, 1102097, 1102098, 1102099, 1102100, 1102101, 1102102,
    1102103, 1102104, 1102105, 1102106, 1102107, 1102108, 1102109, 1102110, 1102111, 1102112,
    1102135, 1102136, 1102137, 1102138, 1102139, 1102140, 1102141, 1102142, 1102143, 1102144);
    
    var cape3 = Array(1102145, 1102146, 1102147, 1102148, 1102149, 1102150, 1102151, 1102152, 1102153, 1102154,
    1102155, 1102156, 1102157, 1102158, 1102159, 1102160, 1102164, 1102165, 1102166, 1102167,
    1102168, 1102169, 1102172, 1102174, 1102175, 1102176, 1102177, 1102178, 1102179, 1102180,
    1102181, 1102182, 1102183, 1102184, 1102185, 1102186, 1102191, 1102192, 1102193, 1102194,
    1102196, 1102203, 1102205, 1102206, 1102207, 1102216, 1102218, 1102222);

var neck = Array(1122000,/* bow ties 1122001,1122002,1122003,1122004,1122005,1122006,*/1122007,1122010,1122011,1122012,1122013,1122014,1122015,1122017,1122018,1122039,1122040,1122041,1122042,1122043,1122044,1122045,1122046,1122047,1122048,1122049,1122050,1122051,1122052,1122053,1122054,1122055,1122056,1122057,1122058,1122059,1132000,1132001,1132002,1132003,1132004);

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
			cm.sendOk("Hello there#b #h #. #kI'm Accesories Seller of #eNinjaMS#n who sell every available Accessory in NinjaMS For you. Of course for a small fee. Are you ready to go shopping with me?\r\n #r note : All items will cost you #b"+pr+"#r tao each and every item will be tagged with your name :)");
		} else if (status == 1){
			if (!cm.getPlayer().haveSight(pr)) {
				cm.sendOk("Seems like you are a bit low on funds . Sorry, I dont do charity");
				cm.dispose();
			} else {
				var selstr = "choose what you want #b";				
				selstr += " \r\n#L0# Face Accessories - 1 #l";
				selstr += " \r\n#L1# Face Accessories - 2 #l";
				selstr += " \r\n#L2# Eye Accessories - 1 #l";
				selstr += " \r\n#L3# Eye Accessories - 2 #l";
				selstr += " \r\n#L4# Earrings - 1 #l";
				selstr += " \r\n#L5# Earrings - 2 #l";
				selstr += " \r\n#L6# shields - 1 #l";
				selstr += " \r\n#L7# shields - 2 #l";
				selstr += " \r\n#L8# cape - 1 #l";
				selstr += " \r\n#L9# cape - 2 #l";
				selstr += " \r\n#L10# cape - 3 #l";
				selstr += " \r\n#L11# Necklace and Belts #l";				
				cm.sendSimple(selstr);
			}
		} else if (status == 2){
			var showItem = " Choose What you want : #b";
			switch(selection) {
				case 0:
					for (i = 0; i < face1.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + face1[i] + "# - #t" + face1[i] + "# "; 
					}
					status = 100;
					break;
				case 1:
					for (i = 0; i < face2.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + face2[i] + "# - #t" + face2[i] + "# "; 
					}
					status = 110;
					break;
				case 2:
					for (i = 0; i < eye1.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + eye1[i] + "# - #t" + eye1[i] + "# "; 
					}
					status = 120;
					break;
				case 3:
					for (i = 0; i < eye2.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + eye2[i] + "# - #t" + eye2[i] + "# "; 
					}
					status = 130;
					break;
				case 4:
					for (i = 0; i < ear1.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + ear1[i] + "# - #t" + ear1[i] + "# "; 
					}
					status = 140;
					break; 
				case 5:
					for (i = 0; i < ear2.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + ear2[i] + "# - #t" + ear2[i] + "# "; 
					}
					status = 150;
					break;
				case 6:
					for (i = 0; i < shield1.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + shield1[i] + "# - #t" + shield1[i] + "# "; 
					}
					status = 160;
					break;
				case 7:
					for (i = 0; i < shield2.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + shield2[i] + "# - #t" + shield2[i] + "# "; 
					}
					status = 170;
					break;
				case 8:
					for (i = 0; i < cape1.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + cape1[i] + "# - #t" + cape1[i] + "# "; 
					}
					status = 180;
					break;
				case 9:
					for (i = 0; i < cape2.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + cape2[i] + "# - #t" + cape2[i] + "# "; 
					}
					status = 190;
					break;
				case 10:
					for (i = 0; i < cape3.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + cape3[i] + "# - #t" + cape3[i] + "# "; 
					}
					status = 200;
					break;
				case 11:
					for (i = 0; i < neck.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + neck[i] + "# - #t" + neck[i] + "# "; 
					}
					status = 210;
					break;				
			}
			cm.sendSimple(showItem);				
		} else if (status == 101) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(face1[selection], 1);
			cm.dispose();
		}else if (status == 111) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(face2[selection], 1);
			cm.dispose();
		}else if (status == 121) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(eye1[selection], 1);
			cm.dispose();
		}else if (status == 131) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(eye2[selection], 1);
			cm.dispose();
		}else if (status == 141) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(ear1[selection], 1);
			cm.dispose();
		}else if (status == 151) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(ear2[selection], 1);
			cm.dispose();
		}else if (status == 161) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(shield1[selection], 1);
			cm.dispose();
		}else if (status == 171) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(shield2[selection], 1);
			cm.dispose();
		}else if (status == 181) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(cape1[selection], 1);
			cm.dispose();
		}else if (status == 191) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(cape2[selection], 1);
			cm.dispose();
		}else if (status == 201) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(cape3[selection], 1);
			cm.dispose();
		}else if (status == 211) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(neck[selection], 1);
			cm.dispose();
		}
	}
}
