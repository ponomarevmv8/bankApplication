package bank.dev;

import bank.dev.service.OperationsConsoleListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("bank.dev");
        OperationsConsoleListener consoleListener = context.getBean(OperationsConsoleListener.class);
        consoleListener.startBank();
    }
}