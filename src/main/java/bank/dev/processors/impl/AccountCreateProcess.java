package bank.dev.processors.impl;

import bank.dev.entity.Account;
import bank.dev.processors.OperationProcess;
import bank.dev.processors.OperationType;
import bank.dev.service.AccountService;
import bank.dev.util.Message;
import bank.dev.util.UtilValidate;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountCreateProcess implements OperationProcess {

    private final Scanner scanner;
    private final AccountService accountService;

    public AccountCreateProcess(Scanner scanner, AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void process() {
        while (true) {
            System.out.println(Message.ENTER_USER_ID.getMessage());
            var userId = scanner.nextLine();
            if(userId.equalsIgnoreCase("exit")) {
                System.out.println("Остановка создания аккаунта");
                break;
            }
            if(!UtilValidate.isNumeric(userId)) {
                continue;
            }
            Account newAccount = accountService.createAccount(Long.parseLong(userId));
            System.out.printf(Message.CREATE_ACCOUNT.getMessage(), newAccount.getId(), newAccount.getUserId());
            break;
        }
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.ACCOUNT_CREATE;
    }
}
