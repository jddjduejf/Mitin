package chat.mitin.app;

import android.util.Base64;

public class Encryptor {
    private static final byte XOR_KEY = 0x5A;
    private static final byte XOR_KEY2 = 0x2F;
    private static final byte XOR_KEY3 = 0x7C;

    public static String decode(String input) {
        try {
            byte[] data = Base64.decode(input, Base64.DEFAULT);
            for (int i = 0; i < data.length; i++) {
                data[i] ^= XOR_KEY;
            }
            return new String(data);
        } catch (Exception e) {
            return input;
        }
    }

    public static String decode2(String input) {
        try {
            byte[] data = Base64.decode(input, Base64.DEFAULT);
            for (int i = 0; i < data.length; i++) {
                data[i] ^= XOR_KEY2;
                data[i] ^= XOR_KEY3;
            }
            return new String(data);
        } catch (Exception e) {
            return input;
        }
    }

    public static String encode(String input) {
        try {
            byte[] data = input.getBytes();
            for (int i = 0; i < data.length; i++) {
                data[i] ^= XOR_KEY;
            }
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            return input;
        }
    }
}
