var status = 0;
var pr = 10;
var b1 = Array(
1060000,1060001,1060002,1060003,1060004,1060005,1060006,1060007,1060008,1060009,
1060010,1060011,1060012,1060013,1060014,1060015,1060016,1060017,1060018,1060019,
1060020,1060021,1060022,1060023,1060024,1060025,1060026,1060027,1060028,1060029,
1060030,1060031,1060032,1060033,1060034,1060035,1060036,1060037,1060038,1060039,
1060040,1060041,1060042,1060043,1060044,1060045,1060046,1060047,1060048,1060049,
1060050,1060051,1060052,1060053,1060054,1060055,1060056,1060057,1060058,1060059);
var b2 = Array(
1060060,1060061,1060062,1060063,1060064,1060065,1060066,1060067,1060068,1060069,
1060070,1060071,1060072,1060073,1060074,1060075,1060076,1060077,1060078,1060079,
1060080,1060081,1060082,1060083,1060084,1060085,1060086,1060087,1060088,1060089,
1060090,1060091,1060092,1060093,1060094,1060095,1060096,1060097,1060098,1060099,
1060100,1060101,1060102,1060103,1060104,1060105,1060106,1060107,1060108,1060109,
1060110,1060111,1060112,1060113,1060114,1060116,1060117,1060118,1060119,1060120);
var b3 = Array(
1060121,1060122,1060123,1060125,1060126,1060127,1060128,1060129,1060130,1060131,
1060132,1060133,1061000,1061001,1061002,1061003,1061004,1061005,1061006,1061007,
1061008,1061009,1061010,1061011,1061012,1061013,1061014,1061015,1061016,1061017,
1061018,1061019,1061020,1061021,1061022,1061023,1061024,1061025,1061026,1061027,
1061028,1061029,1061030,1061031,1061032,1061033,1061034,1061035,1061036,1061037,
1061038,1061039,1061040,1061041,1061042,1061043,1061044,1061045,1061046,1061047);
var b4 = Array(
1061048,1061049,1061050,1061051,1061052,1061053,1061054,1061055,1061056,1061057,
1061058,1061059,1061060,1061061,1061062,1061063,1061064,1061065,1061066,1061067,
1061068,1061069,1061070,1061071,1061072,1061073,1061074,1061075,1061076,1061077,
1061078,1061079,1061080,1061081,1061082,1061083,1061084,1061085,1061086,1061087,
1061088,1061089,1061090,1061091,1061092,1061093,1061094,1061095,1061096,1061097,
1061098,1061099,1061100,1061101,1061102,1061103,1061104,1061105,1061106,1061107);
var b5 = Array(
1061108,1061109,1061110,1061111,1061112,1061113,1061114,1061115,1061116,1061117,
1061118,1061119,1061120,1061121,1061122,1061123,1061124,1061126,1061127,1061128,
1061129,1061130,1061131,1061132,1061133,1061134,1061135,1061136,1061137,1061138,
1061139,1061140,1061141,1061142,1061143,1061144,1061147,1061148,1061149,1061150,
1061151,1061152,1061153,1061154,1061155,1062000,1062001,1062002,1062003,1062004,
1062005,1062006,1062008,1062009,1062010,1062011,1062012,1062013,1062014);
var b6 = Array(
1062015,1062016,1062017,1062018,1062019,1062020,1062021,1062022,1062023,1062024,
1062025,1062026,1062027,1062028,1062029,1062030,1062031,1062032,1062033,1062034,
1062035,1062038,1062039,1062040,1062041,1062042,1062043,1062044,1062045,1062046,
1062047,1062048,1062049,1062050,1062051,1062052,1062053,1062054,1062055,1062056,
1062057,1062058,1062059,1062060,1062061,1062062,1062063,1062064,1062065,1062066);
var b7 = Array(
1062067,1062068,1062069,1062070,1062071,1062072,1062073,1062074,1062075,1062076,
1062077,1062080,1062081,1062082,1062083,1062084,1062085,1062086,1062087,1062088,
1062089,1062091,1062092,1062093,1062094,1062095,1062096,1062097,1062098,1062100,
1062101,1062102,1062103,1062104,1062105,1062106,1062107,1062108,1062109,1062111,
1062113,1062114,1062116);


var s1 = Array (
1070000,1070001,1070002,1070003,1070004,1070005,1070006,1070007,1070009,
1070014,1070015,1070016,1071000,1071001,1071002,1071003,1071004,1071005,
1071006,1071007,1071008,1071009,1071010,1071011,1071012,1071013,1071014,
1071015,1071016,1071017,1071018,1071019,1071020,1071021,1071024,1071025,
1071026,1072000,1072001,1072002,1072003,1072004,1072005,1072006,1072007,
1072008,1072009,1072010,1072011,1072012,1072013,1072014,1072015,1072016);

var s2 = Array (
1072017,1072018,1072019,1072020,1072021,1072022,1072023,1072024,1072025,
1072026,1072027,1072028,1072029,1072030,1072031,1072032,1072033,1072034,
1072035,1072036,1072037,1072038,1072039,1072040,1072041,1072042,1072043,
1072044,1072045,1072046,1072047,1072048,1072049,1072050,1072051,1072052,
1072053,1072054,1072055,1072056,1072057,1072058,1072059,1072060,1072061,
1072062,1072063,1072064,1072065,1072066,1072067,1072068,1072069,1072070);

var s3 = Array (
1072071,1072072,1072073,1072074,1072075,1072076,1072077,1072078,1072079,
1072080,1072081,1072082,1072083,1072084,1072085,1072086,1072087,1072088,
1072089,1072090,1072091,1072092,1072093,1072094,1072095,1072096,1072097,
1072098,1072099,1072100,1072101,1072102,1072103,1072104,1072105,1072106,
1072107,1072108,1072109,1072110,1072111,1072112,1072113,1072114,1072115,
1072116,1072117,1072118,1072119,1072120,1072121,1072122,1072123,1072124);

var s4 = Array (
1072125,1072126,1072127,1072128,1072129,1072130,1072131,1072132,1072133,
1072134,1072135,1072136,1072137,1072138,1072139,1072140,1072141,1072142,
1072143,1072144,1072145,1072146,1072147,1072148,1072149,1072150,1072151,
1072152,1072153,1072154,1072155,1072156,1072157,1072158,1072159,1072160,
1072161,1072162,1072163,1072164,1072165,1072166,1072167,1072168,1072169,
1072170,1072171,1072172,1072173,1072174,1072175,1072176,1072177,1072178);

var s5 = Array(
1072179,1072180,1072181,1072182,1072183,1072184,1072185,1072186,1072187,
1072188,1072189,1072190,1072191,1072192,1072193,1072194,1072195,1072196,
1072197,1072198,1072199,1072200,1072201,1072202,1072203,1072204,1072205,
1072206,1072207,1072208,1072209,1072210,1072211,1072212,1072213,1072214,
1072215,1072216,1072217,1072218,1072219,1072220,1072221,1072222,1072223,
1072224,1072225,1072226,1072227,1072228,1072229,1072230,1072231,1072232);

var s6 = Array(
1072233,1072234,1072235,1072236,1072237,1072238,1072239,1072240,1072241,
1072242,1072243,1072244,1072245,1072246,1072247,1072250,1072251,1072252,
1072253,1072254,1072255,1072256,1072257,1072258,1072259,1072260,1072261,
1072262,1072263,1072264,1072265,1072266,1072267,1072268,1072269,1072272,
1072273,1072274,1072275,1072276,1072277,1072278,1072279,1072280,1072281,
1072282,1072283,1072284,1072285,1072288,1072291,1072294,1072297,1072300);

var s7 = Array(
1072303,1072306,1072309,1072312,1072315,1072318,1072321,1072322,1072323,
1072324,1072325,1072326,1072327,1072328,1072329,1072330,1072331,1072332,
1072333,1072334,1072335,1072336,1072337,1072338,1072341,1072342,1072343,
1072344,1072345,1072346,1072347,1072348,1072349,1072350,1072351,1072352,
1072353,1072354,1072355,1072356,1072357,1072358,1072359,1072360,1072361,
1072362,1072363,1072364,1072365,1072366,1072367,1072368,1072369,1072370,
1072371,1072373,1072379,1072380,1072384,1072392,1072394,1072395);
			
var g1= Array(1080000,1080001,1081000,1081001,1081002,1081003,1081004,1082000,1082001,1082002,
1082003,1082004,1082005,1082006,1082007,1082008,1082009,1082010,1082011,1082012,
1082013,1082014,1082015,1082016,1082017,1082018,1082019,1082020,1082021,1082022,
1082023,1082024,1082025,1082026,1082027,1082028,1082029,1082030,1082031,1082032,
1082033,1082034,1082035,1082036,1082037,1082038,1082039,1082040,1082041,1082042);
var g2= Array(1082043,1082044,1082045,1082046,1082047,1082048,1082049,1082050,1082051,1082052,
1082053,1082054,1082055,1082056,1082057,1082058,1082059,1082060,1082061,1082062,
1082063,1082064,1082065,1082066,1082067,1082068,1082069,1082070,1082071,1082072,
1082073,1082074,1082075,1082076,1082077,1082078,1082079,1082080,1082081,1082082,
1082083,1082084,1082085,1082086,1082087,1082088,1082089,1082090,1082091,1082092);
var g3= Array(1082093,1082094,1082095,1082096,1082097,1082098,1082099,1082100,1082101,1082102,
1082103,1082104,1082105,1082106,1082107,1082108,1082109,1082110,1082111,1082112,
1082113,1082114,1082115,1082116,1082117,1082118,1082119,1082120,1082121,1082122,
1082123,1082124,1082125,1082126,1082127,1082128,1082129,1082130,1082131,1082132,
1082133,1082134,1082135,1082136,1082137,1082138,1082139,1082140,1082141,1082142);
var g4= Array(1082143,1082144,1082145,1082146,1082147,1082148,1082149,1082150,1082151,1082152,
1082153,1082154,1082155,1082156,1082157,1082158,1082159,1082160,1082161,1082162,
1082163,1082164,1082167,1082168,1082169,1082170,1082171,1082172,1082173,1082174,
1082175,1082176,1082177,1082178,1082179,1082180,1082183,1082186,1082189,1082192,
1082195,1082198,1082201,1082204,1082207,1082210,1082213,1082216,1082218,1082222);
var g5= Array(1082223,1082224,1082225,1082227,1082228,1082229,1082230,1082231,1082232,1082233,
1082234,1082235,1082236,1082237,1082238,1082239,1082240,1082241,1082242,1082243,
1082244,1082245,1082246,1082247,1082249,1082251);
			
    
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
			cm.sendOk("Hello there#b #h #. #kI'm Bottoms, shoes and gloves Seller of #eNinjaMS#n who sell every available bottoms, shoes and gloves in NinjaMS For you. Of course for a small fee. Are you ready to go shopping with me?\r\n #r note : All items will cost you #b "+pr+"#r tao each and every item will be tagged with your name :)");
		} else if (status == 1){
			if (!cm.getPlayer().haveSight(pr)){
				cm.sendOk("Seems like you are a bit low on funds . Sorry, I dont do charity");
				cm.dispose();
			} else {
				var selstr = "choose what you want #b";		
				for (i = 0; i < 7; i++){
					selstr += " \r\n#L"+i+"# Bottoms Shop - " + (i + 1)+" #l";
				}
				for (i = 0; i < 7; i++){
					selstr += " \r\n#L" +(i + 7)+"# Shoes Shop - " + (i + 1)+" #l";
				}	
				for (i = 0; i < 5; i++){
					selstr += " \r\n#L" +(i + 14)+"# Gloves Shop - " + (i + 1)+" #l";
				}	
				cm.sendSimple(selstr);
			}
		} else if (status == 2){
			var showItem = " Choose What you want : #b";
			switch(selection) {
				case 0:
					for (i = 0; i < b1.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + b1[i] + "# - #t" + b1[i] + "# "; 
					}
					status = 100;
					break;
				case 1:
					for (i = 0; i < b2.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + b2[i] + "# - #t" + b2[i] + "# "; 
					}
					status = 110;
					break;
				case 2:
					for (i = 0; i < b3.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + b3[i] + "# - #t" + b3[i] + "# "; 
					}
					status = 120;
					break;
				case 3:
					for (i = 0; i < b4.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + b4[i] + "# - #t" + b4[i] + "# "; 
					}
					status = 130;
					break;
				case 4:
					for (i = 0; i < b5.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + b5[i] + "# - #t" + b5[i] + "# "; 
					}
					status = 140;
					break; 
				case 5:
					for (i = 0; i < b6.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + b6[i] + "# - #t" + b6[i] + "# "; 
					}
					status = 150;
					break;
				case 6:
					for (i = 0; i < b7.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + b7[i] + "# - #t" + b7[i] + "# "; 
					}
					status = 160;
					break;
				case 7:
					for (i = 0; i < s1.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + s1[i] + "# - #t" + s1[i] + "# "; 
					}
					status = 170;
					break;
				case 8:
					for (i = 0; i < s2.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + s2[i] + "# - #t" + s2[i] + "# "; 
					}
					status = 180;
					break;
				case 9:
					for (i = 0; i < s3.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + s3[i] + "# - #t" + s3[i] + "# "; 
					}
					status = 190;
					break;
				case 10:
					for (i = 0; i < s4.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + s4[i] + "# - #t" + s4[i] + "# "; 
					}
					status = 200;
					break;
				case 11:
					for (i = 0; i < s5.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + s5[i] + "# - #t" + s5[i] + "# "; 
					}
					status = 210;
					break;	
				case 12:
					for (i = 0; i < s6.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + s6[i] + "# - #t" + s6[i] + "# "; 
					}
					status = 220;
					break;	
				case 13:
					for (i = 0; i < s7.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + s7[i] + "# - #t" + s7[i] + "# "; 
					}
					status = 230;
					break;			
				case 14:
					for (i = 0; i < g1.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + g1[i] + "# - #t" + g1[i] + "# "; 
					}
					status = 240;
					break;
				case 15:
					for (i = 0; i < g2.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + g2[i] + "# - #t" + g2[i] + "# "; 
					}
					status = 250;
					break;
				case 16:
					for (i = 0; i < g3.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + g3[i] + "# - #t" + g3[i] + "# "; 
					}
					status = 260;
					break;
				case 17:
					for (i = 0; i < g4.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + g4[i] + "# - #t" + g4[i] + "# "; 
					}
					status = 270;
					break;
				case 18:
					for (i = 0; i < g5.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + g5[i] + "# - #t" + g5[i] + "# "; 
					}
					status = 280;
					break;						
			}
			cm.sendSimple(showItem);				
		} else if (status == 101) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(b1[selection], 1);
			cm.dispose();
		}else if (status == 111) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(b2[selection], 1);
			cm.dispose();
		}else if (status == 121) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(b3[selection], 1);
			cm.dispose();
		}else if (status == 131) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(b4[selection], 1);
			cm.dispose();
		}else if (status == 141) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(b5[selection], 1);
			cm.dispose();
		}else if (status == 151) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(b6[selection], 1);
			cm.dispose();
		}else if (status == 161) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(b7[selection], 1);
			cm.dispose();
		}else if (status == 171) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(s1[selection], 1);
			cm.dispose();
		}else if (status == 181) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(s2[selection], 1);
			cm.dispose();
		}else if (status == 191) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(s3[selection], 1);
			cm.dispose();
		}else if (status == 201) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(s4[selection], 1);
			cm.dispose();
		}else if (status == 211) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(s5[selection], 1);
			cm.dispose();
		}else if (status == 221) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(s6[selection], 1);
			cm.dispose();
		}else if (status == 231) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(s7[selection], 1);
			cm.dispose();
		}else if (status == 231) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(s7[selection], 1);
			cm.dispose();
		}else if (status == 241) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(g1[selection], 1);
			cm.dispose();
		}else if (status == 251) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(g2[selection], 1);
			cm.dispose();
		}else if (status == 261) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(g3[selection], 1);
			cm.dispose();
		}else if (status == 271) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(g4[selection], 1);
			cm.dispose();
		}else if (status == 281) {
			cm.gainItem(4032016, -pr)
			cm.gainTaggedItem(g5[selection], 1);
			cm.dispose();
		}
	}
}
