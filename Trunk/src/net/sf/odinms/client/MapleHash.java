/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client;

import java.security.MessageDigest;
import net.sf.odinms.tools.HexTool;

/**
 *
 * @author Admin
 */
public class MapleHash {

    private static boolean checkHash(String hash, String type, String password) {
        try {
            MessageDigest digester = MessageDigest.getInstance(type);
            digester.update(password.getBytes("UTF-8"), 0, password.length());
            return HexTool.toString(digester.digest()).replace(" ", "").toLowerCase().equals(hash);
        } catch (Exception e) {
            throw new RuntimeException("Encoding the string failed", e);
        }
    }
}
