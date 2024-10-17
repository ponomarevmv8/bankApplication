package bank.dev.processors.impl;

import bank.dev.processors.OperationProcess;
import bank.dev.processors.OperationType;
import bank.dev.service.AccountService;
import bank.dev.util.Message;
import bank.dev.util.UtilValidate;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountDepositProcess implements OperationProcess {

    private final AccountService accountService;
    private final Scanner scanner;

    public AccountDepositProcess(AccountService accountService, Scanner scanner) {
        this.accountService = accountService;
        this.scanner = scanner;
    }

    @Override
    public void process() {
        while (true) {
            System.out.println(Message.ENTER_ACCOUNT_ID.getMessage());
            var accountId = scanner.nextLine();
            if(!UtilValidate.isNumeric(accountId)){
                continue;
            }
            System.out.println(Message.AMOUNT.getMessage());
            var amount = scanner.nextLine();
            if(!UtilValidate.isNumberAndPositive(amount)){
                continue;
            }
            accountService.accountDeposit(Long.parseLong(accountId), Double.parseDouble(amount));
            System.out.printf(Message.DEPOSIT.getMessage(), amount, accountId);
            break;
        }
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.ACCOUNT_DEPOSIT;
    }
}
