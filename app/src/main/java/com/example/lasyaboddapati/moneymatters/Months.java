package com.example.lasyaboddapati.moneymatters;

/**
 * Created by lasyaboddapati on 2/18/15.
 */
public class Months {
    public static enum MONTHS { January, February, March, April, May, June, July
                       , August, September, October, November, December }

    public static int size() {
        return MONTHS.values().length;
    }

    public static String[] names() {
        String[] names = new String[size()];

        for (int i = 0; i < size(); i++) {
            names[i] = MONTHS.values()[i].name();
        }
        return names;
    }

    public static int[] values() {
        int[] values = new int[size()];

        for (int i=0; i<size(); i++) {
            values[i] = MONTHS.values()[i].ordinal();
        }
        return values;
    }

    public static int valueOf(String month) {
        return MONTHS.valueOf(month).ordinal();
    }

    public static String nameOf(int mm) {
        return names()[mm-1];
    }
}
