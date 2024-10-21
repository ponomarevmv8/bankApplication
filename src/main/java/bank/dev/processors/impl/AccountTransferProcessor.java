package bank.dev.processors.impl;

import bank.dev.processors.OperationProcessor;
import bank.dev.processors.OperationType;
import bank.dev.service.AccountService;
import bank.dev.util.Message;
import bank.dev.util.UtilValidate;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountTransferProcessor implements OperationProcessor {

    private final AccountService accountService;
    private final Scanner scanner;

    public AccountTransferProcessor(AccountService accountService, Scanner scanner) {
        this.accountService = accountService;
        this.scanner = scanner;
    }

    @Override
    public void process() {
        System.out.println(Message.EXIT.getMessage());
        System.out.println("Soruce " + Message.ENTER_ACCOUNT_ID.getMessage());
        var sourceAccountId = scanner.nextLine();
        if(sourceAccountId.equalsIgnoreCase("exit")) {
            System.out.println("Остановка операции перевода");
            return;
        }
        if (!UtilValidate.isNumberAndPositive(sourceAccountId)) {
            return;
        }
        System.out.println("Target " + Message.ENTER_ACCOUNT_ID.getMessage());
        var targetAccountId = scanner.nextLine();
        if (!UtilValidate.isNumberAndPositive(targetAccountId)) {
            return;
        }
        System.out.println(Message.AMOUNT.getMessage());
        var amount = scanner.nextLine();
        if (!UtilValidate.isNumberAndPositive(amount)) {
            return;
        }
        accountService.accountTransfer(Long.parseLong(sourceAccountId), Long.parseLong(targetAccountId),
                Double.parseDouble(amount));
        System.out.printf(Message.TRANSFER.getMessage(), amount, sourceAccountId, targetAccountId);
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.ACCOUNT_TRANSFER;
    }
}
