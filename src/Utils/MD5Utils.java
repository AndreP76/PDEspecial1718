package Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    @Deprecated
    public static String generateMD5() {
        return generateMD5(StringUtils.RandomLetters(10));
    }

    @Deprecated
    public static String generateMD5(String dataToCode) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        StringBuffer hexString = new StringBuffer();
        byte[] data = md.digest(dataToCode.getBytes());
        for (int i = 0; i < data.length; i++) {
            hexString.append(Integer.toHexString(data[i]));
        }
        return hexString.toString();
    }
}
