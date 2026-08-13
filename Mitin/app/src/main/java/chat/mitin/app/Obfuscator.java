package chat.mitin.app;

import java.util.Random;

public class Obfuscator {
    private static final Random random = new Random();

    public static void junk() {
        int a = random.nextInt(100);
        int b = random.nextInt(100);
        int c = a + b;
        int d = a - b;
        int e = a * b;
        int f = a / (b + 1);
        String s = String.valueOf(c + d + e + f);
        if (s.length() > 10) {
            s = s.substring(0, 5);
        }
        double x = Math.sqrt(random.nextDouble());
        double y = Math.sin(x * 3.14159);
        int z = (int)(y * 100);
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(1);
            } catch (Exception ex) {}
        }
    }

    public static void deadCode() {
        int x = random.nextInt(100);
        int y = random.nextInt(100);
        if (x > y) {
            String s = "dead code " + x + y;
            if (s.length() > 100) {
                System.out.println(s);
            }
        }
    }

    public static boolean opaqueTrue() {
        int a = random.nextInt(10);
        int b = random.nextInt(10);
        int c = a * b + 1;
        int d = a * b + 2;
        return (c + 1) == d;
    }

    public static boolean opaqueFalse() {
        int a = random.nextInt(10);
        int b = random.nextInt(10);
        int c = a * b + 1;
        int d = a * b + 2;
        return (c + 2) == d;
    }
}
