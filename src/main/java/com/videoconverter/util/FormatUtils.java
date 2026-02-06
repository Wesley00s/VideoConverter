package com.videoconverter.util;

import java.text.DecimalFormat;

public class FormatUtils {
    private FormatUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String formatBytes(long size) {
        if (size <= 0) return "0 B";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        if (digitGroups >= units.length) {
            digitGroups = units.length - 1;
        }

        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }
}