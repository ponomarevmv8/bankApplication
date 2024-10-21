package bank.dev.processors.impl;

import bank.dev.processors.OperationProcessor;
import bank.dev.processors.OperationType;
import bank.dev.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class ShowAllUsersProcessor implements OperationProcessor {

    private final UserService userService;

    public ShowAllUsersProcessor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void process() {
        System.out.println("Listing all users:\n" + userService.showUsers());
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.SHOW_ALL_USERS;
    }
}
