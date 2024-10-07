package bank.dev.service;

import bank.dev.config.AccountProperties;
import bank.dev.entity.Account;
import bank.dev.util.Message;
import bank.dev.util.Util;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AccountService {

    private final Map<Long, List<Account>> accountsUserId = new HashMap<>();
    private final Map<Long, Account> accounts = new HashMap<>();

    private final UserService userService;
    private final AccountProperties accountProperties;

    private double commissionRate = 2.5;

    public AccountService(@Lazy UserService userService, AccountProperties accountProperties) {
        this.userService = userService;
        this.accountProperties = accountProperties;
    }

    public Account createAccount() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String userId;
            System.out.println(Message.ENTER_USER_ID.getMessage());
            while ((userId = reader.readLine()) != null) {
                if(userId.equalsIgnoreCase("exit")){
                    System.out.println("Создание аккаунта остановили... ");
                    return null;
                }
                if(!Util.isNumeric(userId)) {
                    System.out.println(Message.ENTER_USER_ID.getMessage());
                    continue;
                }
                Long id = Long.parseLong(userId);
                if(!userService.checkLogin(id)) {
                    System.out.printf(Message.NOT_FOUND_USER.getMessage(), id);
                    System.out.println(Message.ENTER_USER_ID.getMessage());
                    continue;
                }
                Account newAccount = createAccount(id);
                System.out.printf(Message.CREATE_ACCOUNT.getMessage(),
                        newAccount.getId(),
                        userService.getUser(id).getLogin());
                return newAccount;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Account createAccount(Long userId) {
        Account account = new Account();
        account.setUserId(userId);
        account.setId(Util.generateId());
        if(!accountsUserId.containsKey(userId)) {
            account.setMoneyAmount(accountProperties.getDefaultBalance());
            accountsUserId.put(userId, new ArrayList<>(List.of(account)));
        } else {
            List<Account> accountList = accountsUserId.get(userId);
            accountList.add(account);
            userService.modifyAccount(userId, accountList);
        }
        accounts.put(account.getId(), account);
        return account;
    }

    public Account closeAccount(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String accountId;
            System.out.println(Message.ENTER_ACCOUNT_ID.getMessage());
            while ((accountId = reader.readLine()) != null) {
                if(accountId.equalsIgnoreCase("exit")){
                    System.out.println("Закрытие счета остановленна...");
                    return null;
                }
                if(!Util.isNumeric(accountId)) {
                    System.out.println(Message.ENTER_ACCOUNT_ID.getMessage());
                    continue;
                }
                Long id = Long.parseLong(accountId);
                if(!accounts.containsKey(id)) {
                    System.out.printf(Message.NOT_FOUND_ACCOUNT.getMessage(), id);
                    System.out.println(Message.ENTER_ACCOUNT_ID.getMessage());
                    continue;
                }
                Account accountClosed;
                try{
                    accountClosed = closeAccount(id);
                } catch (RuntimeException e) {
                    System.out.printf(Message.NOT_CLOSED_ACCOUNT.getMessage(), accountId);
                    System.out.println(Message.ENTER_ACCOUNT_ID.getMessage());
                    continue;
                }
                System.out.printf(Message.CLOSE_ACCOUNT.getMessage(),
                        accountClosed.getId());
                return accountClosed;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Account closeAccount(Long accountId) throws RuntimeException {
        if(!accounts.containsKey(accountId)) {
            throw new RuntimeException("Аккаунта с данным ID не существует");
        }
        List<Account> accountsUser = accountsUserId.get(accounts.get(accountId).getUserId());
        if(accountsUser.size() < 2) {
            throw new RuntimeException("Аккаунт невозможно закрыть, так как он единственный");
        }

        Account accountClosed = accounts.get(accountId);
        Account accountTransfer = accountsUser.get(0).getId().equals(accountId) ? accountsUser.get(1) : accountsUser.get(0);
        accountTransfer.setMoneyAmount(accountTransfer.getMoneyAmount() +accountClosed.getMoneyAmount());
        accountsUser.remove(accountClosed);
        return accountClosed;
    }

    public Account accountTransfer(){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(Message.SOURCE_ACCOUNT.getMessage());
            String input;
            Long sourceAccountId = null;
            Long targetAccountId = null;
            double transferAmount = 0d;
            while ((input = reader.readLine()) != null) {
                if(input.equalsIgnoreCase("exit")){
                    System.out.println("Отмена перевода");
                    return null;
                }
                if(!Util.isNumeric(input)) {
                    System.out.println(Message.SOURCE_ACCOUNT.getMessage());
                    continue;
                }
                if(!accounts.containsKey(Long.parseLong(input))) {
                    System.out.printf(Message.NOT_FOUND_ACCOUNT.getMessage(), input);
                    System.out.println(Message.SOURCE_ACCOUNT.getMessage());
                    continue;
                }
                sourceAccountId = Long.parseLong(input);
                input = null;
                break;
            }
            System.out.println(Message.TARGET_ACCOUNT.getMessage());
            while ((input = reader.readLine()) != null) {
                if(input.equalsIgnoreCase("exit")){
                    System.out.println("Отмена перевода");
                    return null;
                }
                if(!Util.isNumeric(input)) {
                    System.out.println(Message.TARGET_ACCOUNT.getMessage());
                    continue;
                }
                if(!accounts.containsKey(Long.parseLong(input))) {
                    System.out.printf(Message.NOT_FOUND_ACCOUNT.getMessage(), input);
                    System.out.println(Message.TARGET_ACCOUNT.getMessage());
                    continue;
                }
                targetAccountId = Long.parseLong(input);
                input = null;
                break;
            }
            System.out.println(Message.AMOUNT.getMessage());
            while ((input = reader.readLine()) != null) {
                if(input.equalsIgnoreCase("exit")){
                    System.out.println("Отмена перевода");
                    return null;
                }
                if(!Util.isNumeric(input)) {
                    System.out.println(Message.AMOUNT.getMessage());
                    continue;
                }
                transferAmount = Double.parseDouble(input);
                if(!Util.isNumberPositive(transferAmount)){
                    System.out.println(Message.AMOUNT.getMessage());
                    continue;
                }
                Account accountTrasfer;
                try {
                    accountTrasfer = accountTransfer(sourceAccountId, targetAccountId, transferAmount);
                } catch (RuntimeException e) {
                    System.out.println(Message.AMOUNT.getMessage());
                    continue;
                }
                System.out.printf(Message.TRANSFER.getMessage(), transferAmount, sourceAccountId, targetAccountId);
                return accountTrasfer;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Account accountTransfer(Long sourceAccountId, Long targetAccountId, Double amount) throws RuntimeException {
        Account sourceAccount = accounts.get(sourceAccountId);
        Account targetAccount = accounts.get(targetAccountId);
        if(sourceAccount == null || targetAccount == null) {
            throw new RuntimeException("Указан не существующий аккаунт");
        }
        if(amount < 0) {
            throw new RuntimeException("Сумма перевода должна быть положительная");
        }
        if(sourceAccount.getMoneyAmount() < amount) {
            System.out.printf("Недостаточно средств на счету %s, на данныйм момент %s\n",
                    sourceAccountId, sourceAccount.getMoneyAmount());
            throw new RuntimeException(String.format("Недостаточно средств на счету %s, на данныйм момент %s\n",
                    sourceAccountId, sourceAccount.getMoneyAmount()));
        }
        sourceAccount.setMoneyAmount(sourceAccount.getMoneyAmount() - amount);
        if (!sourceAccount.getUserId().equals(targetAccount.getUserId())) {
            amount = amount - ( amount * accountProperties.getTransferCommission());
        }
        targetAccount.setMoneyAmount(targetAccount.getMoneyAmount() + amount);
        return targetAccount;
    }


    public Account accountDeposit() {
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(Message.ENTER_ACCOUNT_ID.getMessage());
            String input;
            Long sourceAccountId = null;
            double amount = 0d;
            while ((input= reader.readLine()) != null) {
                if(input.equalsIgnoreCase("exit")){
                    System.out.println("Отмена пополнения счета");
                    return null;
                }
                if(!Util.isNumeric(input)) {
                    System.out.println(Message.ENTER_ACCOUNT_ID.getMessage());
                    continue;
                }
                sourceAccountId = Long.parseLong(input);
                if(!accounts.containsKey(sourceAccountId)) {
                    System.out.printf(Message.NOT_FOUND_ACCOUNT.getMessage(), input);
                    System.out.println(Message.ENTER_ACCOUNT_ID.getMessage());
                    continue;
                }
                input = null;
                break;
            }
            System.out.println(Message.AMOUNT.getMessage());
            while ((input = reader.readLine()) != null) {
                if(input.equalsIgnoreCase("exit")){
                    System.out.println("Отмена пополнения счета");
                    return null;
                }
                if(!Util.isNumeric(input)) {
                    System.out.println(Message.AMOUNT.getMessage());
                    continue;
                }
                amount = Double.parseDouble(input);
                if(!Util.isNumberPositive(amount)) {
                    System.out.println(Message.AMOUNT.getMessage());
                    continue;
                }
                Account sourceAccount;
                try {
                    sourceAccount = accountDeposit(sourceAccountId, amount);
                } catch (RuntimeException e) {
                    return null;
                }
                System.out.printf(Message.DEPOSIT.getMessage(), amount, sourceAccountId);
                return sourceAccount;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Account accountDeposit(Long accountId, Double amount) throws RuntimeException {
        if(!accounts.containsKey(accountId)) {
            throw new RuntimeException("Указан не существующий аккаунт");
        }
        if(amount < 0) {
            throw new RuntimeException("Сумма пополнения должна быть положительная");
        }
        Account account = accounts.get(accountId);
        account.setMoneyAmount(account.getMoneyAmount() + amount);
        return account;
    }

    public Account accountWithdraw(){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(Message.ENTER_ACCOUNT_ID.getMessage());
            String input;
            Long sourceAccountId = null;
            double amount = 0d;
            while ((input= reader.readLine()) != null) {
                if(input.equalsIgnoreCase("exit")){
                    System.out.println("Отмена списания со счета");
                    return null;
                }
                if(!Util.isNumeric(input)) {
                    System.out.println(Message.ENTER_ACCOUNT_ID.getMessage());
                    continue;
                }
                sourceAccountId = Long.parseLong(input);
                if(!accounts.containsKey(sourceAccountId)) {
                    System.out.printf(Message.NOT_FOUND_USER.getMessage(), input);
                    System.out.println(Message.ENTER_ACCOUNT_ID.getMessage());
                    continue;
                }
                input = null;
                break;
            }
            System.out.println(Message.AMOUNT.getMessage());
            while ((input = reader.readLine()) != null) {
                if(input.equalsIgnoreCase("exit")){
                    System.out.println("Отмена списания со счета");
                    return null;
                }
                if(!Util.isNumeric(input)) {
                    System.out.println(Message.AMOUNT.getMessage());
                    continue;
                }
                amount = Double.parseDouble(input);
                if(!Util.isNumberPositive(amount)) {
                    System.out.println(Message.AMOUNT.getMessage());
                    continue;
                }
                Account sourceAccount;
                try {
                    sourceAccount = accountWithdraw(sourceAccountId, amount);
                } catch (RuntimeException e) {
                    System.out.println(Message.AMOUNT.getMessage());
                    continue;
                }
                System.out.printf(Message.WITHDRAW.getMessage(), amount, sourceAccountId);
                return sourceAccount;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Account accountWithdraw(Long accountId, Double amount) throws RuntimeException {
        if(!accounts.containsKey(accountId)) {
            throw new RuntimeException("Указан не существующий аккаунт");
        }
        if(amount < 0) {
            throw new RuntimeException("Сумма списания должна быть положительная");
        }
        Account account = accounts.get(accountId);
        if(amount > account.getMoneyAmount()) {
            throw new RuntimeException("Недостаточно средств для списания");
        }
        account.setMoneyAmount(account.getMoneyAmount() - amount);
        return account;
    }
}
