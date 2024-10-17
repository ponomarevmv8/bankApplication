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
public class AccountCloseProcess implements OperationProcess {

    private final AccountService accountService;
    private final Scanner scanner;

    public AccountCloseProcess(AccountService accountService, Scanner scanner) {
        this.accountService = accountService;
        this.scanner = scanner;
    }

    @Override
    public void process() {
        while (true) {
            System.out.println(Message.ENTER_ACCOUNT_ID.getMessage());
            var accountId = scanner.nextLine();
            if(accountId.equalsIgnoreCase("exit")) {
                System.out.println("Остановка операции по закрытию аккаунта");
            }
            if(!UtilValidate.isNumeric(accountId)) {
                continue;
            }
            Account accountClosed = accountService.closeAccount(Long.parseLong(accountId));
            System.out.printf(Message.CLOSE_ACCOUNT.getMessage(), accountClosed.getId());
            break;
        }
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.ACCOUNT_CLOSED;
    }
}
