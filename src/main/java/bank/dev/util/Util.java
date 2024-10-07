package bank.dev.util;

public class Util {

    private static Long countId = 0L;

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

    public static boolean isNumberPositive(Long number) {
        boolean result = number.doubleValue() > 0;
        if(!result){
            System.out.println("ОШИБКА: Число должно быть положительным");
        }
        return result;
    }

    public static Long generateId() {
        return ++countId;
    }

}
