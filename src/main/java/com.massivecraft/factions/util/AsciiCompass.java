/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;

public class AsciiCompass {
    public static Point getCompassPointForDirection(double inDegrees) {
        double degrees = (inDegrees - 180.0) % 360.0;
        if (degrees < 0.0) {
            degrees += 360.0;
        }
        if (0.0 <= degrees && degrees < 22.5) {
            return Point.N;
        }
        if (22.5 <= degrees && degrees < 67.5) {
            return Point.NE;
        }
        if (67.5 <= degrees && degrees < 112.5) {
            return Point.E;
        }
        if (112.5 <= degrees && degrees < 157.5) {
            return Point.SE;
        }
        if (157.5 <= degrees && degrees < 202.5) {
            return Point.S;
        }
        if (202.5 <= degrees && degrees < 247.5) {
            return Point.SW;
        }
        if (247.5 <= degrees && degrees < 292.5) {
            return Point.W;
        }
        if (292.5 <= degrees && degrees < 337.5) {
            return Point.NW;
        }
        if (337.5 <= degrees && degrees < 360.0) {
            return Point.N;
        }
        return null;
    }

    public static ArrayList<String> getAsciiCompass(Point point, ChatColor colorActive, String colorDefault) {
        ArrayList<String> ret = new ArrayList<String>();
        String row = "";
        row = row + Point.NW.toString(Point.NW == point, colorActive, colorDefault);
        row = row + Point.N.toString(Point.N == point, colorActive, colorDefault);
        row = row + Point.NE.toString(Point.NE == point, colorActive, colorDefault);
        ret.add(row);
        row = "";
        row = row + Point.W.toString(Point.W == point, colorActive, colorDefault);
        row = row + colorDefault + "+";
        row = row + Point.E.toString(Point.E == point, colorActive, colorDefault);
        ret.add(row);
        row = "";
        row = row + Point.SW.toString(Point.SW == point, colorActive, colorDefault);
        row = row + Point.S.toString(Point.S == point, colorActive, colorDefault);
        row = row + Point.SE.toString(Point.SE == point, colorActive, colorDefault);
        ret.add(row);
        return ret;
    }

    public static ArrayList<String> getAsciiCompass(double inDegrees, ChatColor colorActive, String colorDefault) {
        return AsciiCompass.getAsciiCompass(AsciiCompass.getCompassPointForDirection(inDegrees), colorActive, colorDefault);
    }

    public enum Point {
        N('N'),
        NE('/'),
        E('E'),
        SE('\\'),
        S('S'),
        SW('/'),
        W('W'),
        NW('\\');

        public final char asciiChar;

        Point(char asciiChar) {
            this.asciiChar = asciiChar;
        }

        public String toString() {
            return String.valueOf(this.asciiChar);
        }

        public String toString(boolean isActive, ChatColor colorActive, String colorDefault) {
            return (isActive ? colorActive : colorDefault) + String.valueOf(this.asciiChar);
        }
    }

}

