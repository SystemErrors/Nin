/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.odinms.client;

public class ExpTable {

    private static int[] exp = {0, 15, 34, 57, 92, 135, 372, 560, 840, 1242, 1143, 1573, 2143, 2799, 3639, 4699, 5893,
        7359, 9143, 11119, 13477, 16267, 19319, 22879, 27007, 31477, 36599, 42443, 48719, 55812, 76560, 86784, 98208, 110932,
        124432, 139372, 155865, 173280, 192400, 213345, 235372, 259392, 285532, 312928, 342624, 374760, 408336, 444544, 483532,
        524160, 567772, 598886, 631704, 666321, 702836, 741351, 781976, 824828, 870028, 917625, 967995, 1021040, 1076993, 1136012,
        1198265, 1263930, 1333193, 1406252, 1483314, 1564600, 1650340, 1740778, 1836172, 1936794, 2042930, 2154882, 2272969,
        2397528, 2528912, 2667496, 2813674, 2967863, 3130501, 3302052, 3483004, 3673872, 3875200, 4087561, 4311559, 4547832,
        4797052, 5059931, 5337215, 5629694, 5938201, 6263614, 6606860, 6968915, 7350811, 7753635, 8178534, 8626717, 9099461,
        9598112, 10124088, 10678888, 11264090, 11881362, 12532460, 13219239, 13943652, 14707764, 15513749, 16363902, 17260644,
        18206527, 19204244, 20256636, 21366700, 22537594, 23772654, 25075395, 26449526, 27898960, 29427822, 31040466, 32741483,
        34535716, 36428272, 38424541, 40530206, 42751261, 45094030, 47565183, 50171755, 52921167, 55821246, 58880250, 62106888,
        65510344, 69100311, 72887008, 76881216, 81094306, 85594273, 90225770, 95170142, 100385465, 105886588, 111689173, 117809740,
        124265713, 131075474, 138258409, 145834970, 153826726, 162256430, 171148082, 180526996, 190419876, 200854884, 211861732,
        223471711, 235718006, 248635352, 262260569, 276632448, 291791906, 307782102, 324648561, 342439302, 361204976, 380999008,
        401877753, 423900654, 447130409, 471633156, 497478652, 524740482, 553496260, 583827855, 615821621, 649568646, 685165008,
        722712050, 762316670, 804091623, 848155844, 894634784, 943660769, 995373379, 1049919840, 1107455447, 1168144005, 1232158296,
        1299680571, 1370903066, 1446028554, 1525246918, 1608855764, 2121276324};
    private static int[] closeness = {0, 1, 3, 6, 14, 31, 60, 108, 181, 287,
        434, 632, 891, 1224, 1642, 2161, 2793, 3557, 4467, 5542, 6801, 8263,
        9950, 11882, 14084, 16578, 19391, 22547, 26074, 30000};
    private static final int[] mountexp = {0, 6, 25, 50, 105, 134, 196, 254,
        263, 315, 367, 430, 543, 587, 679, 725, 897, 1146, 1394, 1701, 2247,
        2543, 2898, 3156, 3313, 3584, 3923, 4150, 4305, 4550};

    public static int getExpNeededForLevel(int level) {
        if (level >= 200) {
            return Integer.MAX_VALUE - 10;
        }
        return exp[level];
    }

    public static int getClosenessNeededForLevel(int level) {
        return closeness[level - 1];
    }

    public static final int getMountExpNeededForLevel(final int level) {
        return mountexp[level - 1];
    }
}
