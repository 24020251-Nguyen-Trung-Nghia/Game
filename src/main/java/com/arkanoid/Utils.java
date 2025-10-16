package com.arkanoid;

public class Utils {
    public static double clamp(final double min, final double max, final double value) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    static int clamp(final int min, final int max, final int value) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    static double computeLineIntersectionX(final double x1, final double y1, final double x2, final double y2, final double x3, final double y3, final double x4, final double y4) {
        return computeLineIntersection(x1, y1, x2, y2, x3, y3, x4, y4, true);
    }

    static double computeLineIntersectionY(final double x1, final double y1, final double x2, final double y2, final double x3, final double y3, final double x4, final double y4) {
        return computeLineIntersection(x1, y1, x2, y2, x3, y3, x4, y4, false);
    }

    static double computeLineIntersection(final double x1, final double y1, final double x2, final double y2, final double x3, final double y3, final double x4, final double y4, final boolean x) {
        final double a1 = y2 - y1;
        final double b1 = x1 - x2;
        final double c1 = a1 * x1 + b1 * y1;

        final double a2 = y4 - y3;
        final double b2 = x3 - x4;
        final double c2 = a2 * x3 + b2 * y3;

        final double delta = a1 * b2 - a2 * b1;
        return x ? (b2 * c1 - b1 * c2) / delta : (a1 * c2 - a2 * c1) / delta;
    }

    static double distance(final double x0, final double y0, final double x1, final double y1) {
        final double dx = x1 - x0, dy = y1 - y0;
        return Math.sqrt(dx * dx + dy * dy);
    }
}