package io.b1ackr0se.vsociety.util;

import java.math.BigInteger;
import java.security.MessageDigest;

public class Utils {
    public static String md5(String pass) {
        String md5;
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] data = pass.getBytes();
            m.update(data, 0, data.length);
            BigInteger i = new BigInteger(1, m.digest());
            md5 = String.format("%1$032x", i);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return md5;
    }

}
