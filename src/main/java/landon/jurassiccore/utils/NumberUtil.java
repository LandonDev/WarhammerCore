package landon.jurassiccore.utils;

import java.math.BigInteger;
import java.text.DecimalFormat;

public class NumberUtil {
    private static DecimalFormat decimalFormat = new DecimalFormat("##.#");

    public static String formatNumberByCommas(int number) {
        return String.format("%,d", new Object[]{Integer.valueOf(number)});
    }

    public static String formatNumberByCommas(String number) {
        String numberValue = "", numberDecimalValue = "";
        if (number.contains(".")) {
            numberDecimalValue = "." + number.split("\\.")[1];
            numberValue = number.replace(numberDecimalValue, "");
        } else {
            return String.format("%,d", new Object[]{new BigInteger(number)});
        }
        if (numberDecimalValue.equals(".0"))
            numberDecimalValue = "";
        return String.valueOf(String.format("%,d", new Object[]{new BigInteger(numberValue)})) + numberDecimalValue;
    }

    public static String formatNumberByDecimal(float number) {
        return decimalFormat.format(number);
    }

    public static String formatNumberBySuffix(double number) {
        if (number < 1000.0D)
            return String.valueOf(number);
        int exp = (int) (Math.log(number) / Math.log(1000.0D));
        return String.format("%.1f%c", new Object[]{Double.valueOf(number / Math.pow(1000.0D, exp)), Character.valueOf("kMBTPE".charAt(exp - 1))});
    }
}
