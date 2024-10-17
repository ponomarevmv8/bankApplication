package bank.dev.util;

public class UtilValidate {

    public static boolean isNumeric(String str) {
        boolean result = str.matches("-?\\d+(\\.\\d+)?");
        if(!result){
            System.out.println("ОШИБКА: Введите число");
        }
        return result;
    }

    public static boolean isNumberPositive(Double number) {
        boolean result = number.doubleValue() > 0;
        if(!result){
            System.out.println("ОШИБКА: Число должно быть положительным");
        }
        return result;
    }

    public static boolean isNumberAndPositive(String str) {
        boolean result = isNumeric(str);
        if(!result){
            return false;
        }
        return isNumberPositive(Double.parseDouble(str));
    }
}
