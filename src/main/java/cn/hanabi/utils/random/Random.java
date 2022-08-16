/*
 * Decompiled with CFR 0.151.
 */
package cn.hanabi.utils.random;

public class Random {
    public static int nextInt(int startInclusive, int endExclusive) {
        if (endExclusive - startInclusive <= 0) {
            return startInclusive;
        }
        return startInclusive + new java.util.Random().nextInt(endExclusive - startInclusive);
    }

    public static double nextDouble(double startInclusive, double endInclusive) {
        if (startInclusive == endInclusive || endInclusive - startInclusive <= 0.0) {
            return startInclusive;
        }
        return startInclusive + (endInclusive - startInclusive) * Math.random();
    }

    public static float nextFloat(float startInclusive, float endInclusive) {
        if (startInclusive == endInclusive || endInclusive - startInclusive <= 0.0f) {
            return startInclusive;
        }
        return (float) ((double) startInclusive + (double) (endInclusive - startInclusive) * Math.random());
    }

    public static String randomNumber(int length) {
        return Random.random(length, "123456789");
    }

    public static String randomString(int length) {
        return Random.random(length, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    public static String random(int length, String chars) {
        return Random.random(length, chars.toCharArray());
    }

    public static String random(int length, char[] chars) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while (i < length) {
            stringBuilder.append(chars[new java.util.Random().nextInt(chars.length)]);
            ++i;
        }
        return stringBuilder.toString();
    }
}

