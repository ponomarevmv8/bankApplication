package bank.dev.processors.impl;

import bank.dev.processors.OperationProcessor;
import bank.dev.processors.OperationType;
import bank.dev.service.AccountService;
import bank.dev.util.Message;
import bank.dev.util.UtilValidate;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountWithdrawProcessor implements OperationProcessor {

    private final AccountService accountService;
    private final Scanner scanner;

    public AccountWithdrawProcessor(AccountService accountService, Scanner scanner) {
        this.accountService = accountService;
        this.scanner = scanner;
    }

    @Override
    public void process() {
        System.out.println(Message.EXIT.getMessage());
        System.out.println(Message.ENTER_ACCOUNT_ID.getMessage());
        var accountId = scanner.nextLine();
        if (!UtilValidate.isNumberAndPositive(accountId)) {
            return;
        }
        System.out.println(Message.AMOUNT.getMessage());
        var amount = scanner.nextLine();
        if (!UtilValidate.isNumberAndPositive(amount)) {
            return;
        }
        accountService.accountWithdraw(Long.parseLong(accountId), Double.parseDouble(amount));
        System.out.printf(Message.WITHDRAW.getMessage(), amount, accountId);
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.ACCOUNT_WITHDRAW;
    }
}
