package Utils;

import java.util.Random;

public class StringUtils {
    private static Random R = new Random(System.currentTimeMillis());

    public static String RandomLetters(int numberOfLetters) {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < numberOfLetters; ++i) {
            buff.append((char) ('A' + R.nextInt(25)));
        }
        return buff.toString();
    }

    public static String RandomAlfa(int number) {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < number; ++i) {
            int next = R.nextInt(3);
            if (next == 0) {//caps
                buff.append((char) ('A' + R.nextInt(25)));
            } else if (next == 1) {//number
                buff.append((char) ('0' + R.nextInt(9)));
            } else {//small letters
                buff.append((char) ('a' + R.nextInt(25)));
            }
        }
        return buff.toString();
    }
}
