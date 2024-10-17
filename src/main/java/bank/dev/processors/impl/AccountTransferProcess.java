package bank.dev.processors.impl;

import bank.dev.processors.OperationProcess;
import bank.dev.processors.OperationType;
import bank.dev.service.AccountService;
import bank.dev.util.Message;
import bank.dev.util.UtilValidate;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountTransferProcess implements OperationProcess {

    private final AccountService accountService;
    private final Scanner scanner;

    public AccountTransferProcess(AccountService accountService, Scanner scanner) {
        this.accountService = accountService;
        this.scanner = scanner;
    }

    @Override
    public void process() {
        while (true) {
            System.out.println("Soruce " + Message.ENTER_ACCOUNT_ID.getMessage());
            var sourceAccountId = scanner.nextLine();
            if(!UtilValidate.isNumeric(sourceAccountId)) {
                continue;
            }
            System.out.println("Target " + Message.ENTER_ACCOUNT_ID.getMessage());
            var targetAccountId = scanner.nextLine();
            if(!UtilValidate.isNumeric(targetAccountId)) {
                continue;
            }
            System.out.println(Message.AMOUNT.getMessage());
            var amount = scanner.nextLine();
            if(!UtilValidate.isNumberAndPositive(amount)) {
                continue;
            }
            accountService.accountTransfer(Long.parseLong(sourceAccountId), Long.parseLong(targetAccountId),
                    Double.parseDouble(amount));
            System.out.printf(Message.TRANSFER.getMessage(), amount, sourceAccountId, targetAccountId);
            break;
        }
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.ACCOUNT_TRANSFER;
    }
}
