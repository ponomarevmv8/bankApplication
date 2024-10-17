package bank.dev.processors.impl;

import bank.dev.entity.User;
import bank.dev.processors.OperationProcess;
import bank.dev.processors.OperationType;
import bank.dev.service.UserService;
import bank.dev.util.Message;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class UserCreateProcess implements OperationProcess {

    private final UserService userService;
    private final Scanner scanner;

    public UserCreateProcess(UserService userService, Scanner scanner) {
        this.userService = userService;
        this.scanner = scanner;
    }


    @Override
    public void process() {
        System.out.println(Message.ENTER_LOGIN_USER_ID.getMessage());
        var loginUser = scanner.nextLine();
        if(loginUser.equalsIgnoreCase("exit") || loginUser.isEmpty()) {
            System.out.println("Завершение создания Юзера");
            return;
        }
        User newUser = userService.createUser(loginUser);
        System.out.printf(Message.USER_CREATED.getMessage(), newUser.getLogin());
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.USER_CREATE;
    }
}
