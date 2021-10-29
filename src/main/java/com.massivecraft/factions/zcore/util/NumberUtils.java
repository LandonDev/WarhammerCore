/*
 * Decompiled with CFR 0.145.
 */
package com.massivecraft.factions.zcore.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumberUtils {
    private static DecimalFormat formatter = new DecimalFormat("#");
    private static DecimalFormat df = new DecimalFormat("#,##0");

    public static String formatMoney(long balance) {
        if ((double) balance >= 1.0E12) {
            if ((double) balance % 1.0E12 == 0.0) {
                return formatter.format((double) balance / 1.0E12) + "Trillion";
            }
            return df.format(balance) + "";
        }
        if ((double) balance >= 1.0E9) {
            if ((double) balance % 1.0E9 == 0.0) {
                return formatter.format((double) balance / 1.0E9) + "Billion";
            }
            return df.format(balance) + "";
        }
        if ((double) balance >= 1000000.0) {
            if ((double) balance % 1000000.0 == 0.0) {
                return formatter.format((double) balance / 1000000.0) + "Million";
            }
            return df.format(balance) + "";
        }
        if (balance > 1000L) {
            if (balance % 1000L == 0L) {
                return formatter.format((double) balance / 1000.0) + "k";
            }
            return df.format(balance) + "";
        }
        return df.format(balance) + "";
    }

    private static String insertCommas(BigDecimal number) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(number);
    }

    private static String insertCommas(String number) {
        try {
            return NumberUtils.insertCommas(new BigDecimal(number));
        } catch (Exception err) {
            return number;
        }
    }
}

