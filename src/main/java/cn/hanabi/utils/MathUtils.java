package cn.hanabi.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {
    private static final Random rng;
    private static final Random random = new Random();

    static {
        rng = new Random();
    }

    public static double clamp(double value, double minimum, double maximum) {
        return value > maximum ? maximum : Math.max(value, minimum);
    }

    public static int random(int min, int max) {
        int range = max - min;
        return min + random.nextInt(range + 1);
    }

    public static float map(float x, float prev_min, float prev_max, float new_min, float new_max) {
        return (x - prev_min) / (prev_max - prev_min) * (new_max - new_min) + new_min;
    }

    public static boolean contains(float x, float y, float minX, float minY, float maxX, float maxY) {
        return x > minX && x < maxX && y > minY && y < maxY;
    }

    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double round(double num, double increment) {
        if (increment < 0.0D) {
            throw new IllegalArgumentException();
        } else {
            BigDecimal bd = new BigDecimal(num);
            bd = bd.setScale((int)increment, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }
    }


    public static float getRandom() {
        return rng.nextFloat();
    }



    public static int getRandom(final int min, final int max) {
        if (max < min) return min;
        return min + random.nextInt((max - min) + 1);
    }

    public static double getRandom(double min, double max) {
        final double range = max - min;

        double scaled = random.nextDouble() * range;
        if (scaled > max) scaled = max;

        double shifted = scaled + min;
        if (shifted > max) shifted = max;

        return shifted;
    }


    public static int randInt(final int min, final int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static float clampValue(final float value, final float floor, final float cap) {
        if (value < floor) {
            return floor;
        }
        return Math.min(value, cap);
    }

    public static float getSimilarity(final String string1, final String string2) {
        final int halflen = Math.min(string1.length(), string2.length()) / 2
                + Math.min(string1.length(), string2.length()) % 2;
        final StringBuffer common1 = getCommonCharacters(string1, string2, halflen);
        final StringBuffer common2 = getCommonCharacters(string2, string1, halflen);
        if (common1.length() == 0 || common2.length() == 0) {
            return 0.0f;
        }
        if (common1.length() != common2.length()) {
            return 0.0f;
        }
        int transpositions = 0;
        for (int n = common1.length(), i = 0; i < n; ++i) {
            if (common1.charAt(i) != common2.charAt(i)) {
                ++transpositions;
            }
        }
        transpositions /= (int) 2.0f;
        return (common1.length() / string1.length() + common2.length() / string2.length()
                + (common1.length() - transpositions) / common1.length()) / 3.0f;
    }

    private static StringBuffer getCommonCharacters(final String string1, final String string2, final int distanceSep) {
        final StringBuffer returnCommons = new StringBuffer();
        final StringBuilder copy = new StringBuilder(string2);
        final int n = string1.length();
        final int m = string2.length();
        for (int i = 0; i < n; ++i) {
            final char ch = string1.charAt(i);
            boolean foundIt = false;
            for (int j = Math.max(0, i - distanceSep); !foundIt && j < Math.min(i + distanceSep, m - 1); ++j) {
                if (copy.charAt(j) == ch) {
                    foundIt = true;
                    returnCommons.append(ch);
                    copy.setCharAt(j, '\0');
                }
            }
        }
        return returnCommons;
    }

    public static double meme(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static int customRandInt(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static double roundToPlace(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double getDistance(final double source, final double target) {
        double diff = source - target;
        return Math.sqrt(diff * diff);
    }

    public static double roundDouble(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static float[] constrainAngle(float[] vector) {

        vector[0] = (vector[0] % 360F);
        vector[1] = (vector[1] % 360F);

        while (vector[0] <= -180) {
            vector[0] = (vector[0] + 360);
        }

        while (vector[1] <= -180) {
            vector[1] = (vector[1] + 360);
        }

        while (vector[0] > 180) {
            vector[0] = (vector[0] - 360);
        }

        while (vector[1] > 180) {
            vector[1] = (vector[1] - 360);
        }

        return vector;
    }

    public final int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public final long randomLong(long min, long max) {
        return ThreadLocalRandom.current().nextLong(min, max);
    }

    public final float randomFloat(float min, float max) {
        return (float) ThreadLocalRandom.current().nextDouble(min, max);
    }

    public final double randomGaussian(double tolerance, double average, boolean multiplyGaussian) {
        return random.nextGaussian() * (multiplyGaussian ? random.nextGaussian() : 1.0D) * tolerance + average;
    }

    public static Double[] lerp(Double[] a, Double[] b, double t) {
        return new Double[]{a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t};
    }

    public static double distanceSq(Double[] a, Double[] b) {
        return Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2);
    }

    public static double distanceToSegmentSq(Double[] p, Double[] v, Double[] w) {
        double l2 = distanceSq(v, w);
        if (l2 == 0.0) {
            return distanceSq(p, v);
        }
        return distanceSq(p, lerp(v, w, GLUtil.glmClamp(((p[0] - v[0]) * (w[0] - v[0]) + (p[1] - v[1]) * (w[1] - v[1])) / l2, 0, 1)));
    }

    public static Double[] calcCurvePoint(Double[][] points, double t) {
        final ArrayList<Double[]> cpoints = new ArrayList<>();
        for(int i = 0; i < (points.length - 1); i++) {
            cpoints.add(lerp(points[i], points[i + 1], t));
        }
        return cpoints.size() == 1 ? cpoints.get(0) : calcCurvePoint(cpoints.toArray(new Double[0][0]), t);
    }

    /**
     * 计算贝塞尔曲线上的点
     * @return 路径
     */
    public static Double[][] getPointsOnCurve(Double[][] points, int num) {
        final ArrayList<Double[]> cpoints = new ArrayList<>();
        for(int i = 0; i < num; i++) {
            double t = i / (num - 1.0);
            cpoints.add(calcCurvePoint(points, t));
        }
        return cpoints.toArray(new Double[0][0]);
    }

    /**
     * 精简路径上的点， 用于加速渲染
     */
    public static Double[][] simplifyPoints(Double[][] points, double epsilon) {
        return simplifyPoints(points, epsilon, 0, points.length, new ArrayList<>());
    }

    public static Double[][] simplifyPoints(Double[][] points, double epsilon, int start, int end, ArrayList<Double[]> outPoints) {
        final Double[] s = points[start];
        final Double[] e = points[end - 1];
        double maxDistSq = 0.0;
        int maxNdx = 1;
        for (int i = start + 1; i < end - 1; i++) {
            double distSq = distanceToSegmentSq(points[i], s, e);
            if (distSq > maxDistSq) {
                maxDistSq = distSq;
                maxNdx = i;
            }
        }

        // if that point is too far
        if (Math.sqrt(maxDistSq) > epsilon) {
            // split
            simplifyPoints(points, epsilon, start, maxNdx + 1, outPoints);
            simplifyPoints(points, epsilon, maxNdx, end, outPoints);
        } else {
            // add the 2 end points
            outPoints.add(s);
            outPoints.add(e);
        }

        return outPoints.toArray(new Double[0][0]);
    }
}
