package cn.hanabi.utils.animation;

import static java.lang.Math.*;

public class EaseUtils {
    public static double easeInSine(double x) {
        return 1 - cos((x * PI) / 2);
    }

    public static double easeOutSine(double x) {
        return sin((x * PI) / 2);
    }

    public static double easeInOutSine(double x) {
        return -(cos(PI * x) - 1) / 2;
    }

    public static double easeInQuad(double x) {
        return x * x;
    }

    public static double easeOutQuad(double x) {
        return 1 - (1 - x) * (1 - x);
    }

    public static double easeInOutQuad(double x) {
        return (x < 0.5) ? 2 * x * x : 1 - pow(-2 * x + 2, 2) / 2;
    }
}
