package bank.dev.processors.impl;

import bank.dev.entity.Account;
import bank.dev.processors.OperationProcessor;
import bank.dev.processors.OperationType;
import bank.dev.service.AccountService;
import bank.dev.util.Message;
import bank.dev.util.UtilValidate;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountCloseProcessor implements OperationProcessor {

    private final AccountService accountService;
    private final Scanner scanner;

    public AccountCloseProcessor(AccountService accountService, Scanner scanner) {
        this.accountService = accountService;
        this.scanner = scanner;
    }

    @Override
    public void process() {
        System.out.println(Message.EXIT.getMessage());
        System.out.println(Message.ENTER_ACCOUNT_ID.getMessage());
        var accountId = scanner.nextLine();
        if (accountId.equalsIgnoreCase("exit")) {
            System.out.println("Остановка операции по закрытию аккаунта");
        }
        if (!UtilValidate.isNumberAndPositive(accountId)) {
            return;
        }
        Account accountClosed = accountService.closeAccount(Long.parseLong(accountId));
        System.out.printf(Message.CLOSE_ACCOUNT.getMessage(), accountClosed.getId());
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.ACCOUNT_CLOSED;
    }
}
