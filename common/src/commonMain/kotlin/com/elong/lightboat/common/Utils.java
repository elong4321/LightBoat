package com.elong.lightboat.common;

import java.util.Locale;

public class Utils {

    public static String normalizeMimeType(String type) {
        if (type == null) {
            return null;
        }

        type = type.trim().toLowerCase(Locale.ROOT);

        final int semicolonIndex = type.indexOf(';');
        if (semicolonIndex != -1) {
            type = type.substring(0, semicolonIndex);
        }
        return type;
    }

    public static String getStracktraceString(StackTraceElement[] elements){
        StringBuilder builder = new StringBuilder(128);
        for(StackTraceElement element : elements){
            builder.append(element.toString());
        }
        return builder.toString();
    }

}
