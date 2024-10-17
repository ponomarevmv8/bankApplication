package bank.dev.processors.impl;

import bank.dev.processors.OperationProcess;
import bank.dev.processors.OperationType;
import bank.dev.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class ShowAllUsersProcess implements OperationProcess {

    private final UserService userService;

    public ShowAllUsersProcess(UserService userService) {
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
