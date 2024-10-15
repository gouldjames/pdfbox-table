package com.moebiusgames.pdfbox.table;

import java.awt.*;

public final class ColorUtils {

    public static Color getHSLColor(int h, int s, int l) {
        float sf = (float) s / 100;
        float lf = (float) l / 100;
        float a = sf * Math.min(lf, 1 - lf);

        return new Color(f(0, h, lf, a), f(8, h, lf, a), f(4, h, lf, a));
    }

    private static float f(float n, float h, float l, float a) {
        return l - a * Math.max(-1, Math.min(k(n, h) - 3, Math.min(9 - k(n, h), 1)));
    }

    private static float k(float n, float h) {
        return (n + h / 30) % 12;
    }
}

