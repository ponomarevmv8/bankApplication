package bank.dev.service;

import bank.dev.config.AccountProperties;
import bank.dev.entity.Account;
import bank.dev.util.Message;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AccountService {

    private final Map<Long, List<Account>> accountsUserId = new HashMap<>();
    private final Map<Long, Account> accounts = new HashMap<>();
    private long accountId = 1;

    private final UserService userService;
    private final AccountProperties accountProperties;


    public AccountService(UserService userService, AccountProperties accountProperties) {
        this.userService = userService;
        this.accountProperties = accountProperties;
    }

    public Account createAccount(Long userId) {
        return createAccount(userId, false);
    }

    public Account createAccount(Long userId, boolean isService) {
        if (!userService.checkLogin(userId) && !isService) {
            throw new IllegalStateException(String.format(Message.NOT_FOUND_USER.getMessage(),
                    userId));
        }
        Account account = new Account();
        account.setUserId(userId);
        account.setId(accountId++);
        if (!accountsUserId.containsKey(userId)) {
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

    public Account closeAccount(Long accountId) throws RuntimeException {
        if (!accounts.containsKey(accountId)) {
            throw new RuntimeException(String.format(Message.NOT_FOUND_ACCOUNT.getMessage(), accountId));
        }
        List<Account> accountsUser = accountsUserId.get(accounts.get(accountId).getUserId());
        if (accountsUser.size() < 2) {
            throw new RuntimeException("Аккаунт невозможно закрыть, так как он единственный");
        }

        Account accountClosed = accounts.get(accountId);
        Account accountTransfer = accountsUser.get(0).getId().equals(accountId) ? accountsUser.get(1) : accountsUser.get(0);
        accountTransfer.setMoneyAmount(accountTransfer.getMoneyAmount() + accountClosed.getMoneyAmount());
        accountsUser.remove(accountClosed);
        return accountClosed;
    }

    public Account accountTransfer(Long sourceAccountId, Long targetAccountId, Double amount) throws RuntimeException {
        Account sourceAccount = accounts.get(sourceAccountId);
        Account targetAccount = accounts.get(targetAccountId);
        if (sourceAccount == null) {
            throw new RuntimeException(Message.NOT_FOUND_ACCOUNT.getMessage().formatted(sourceAccountId));
        }
        if(targetAccount == null){
            throw new RuntimeException(Message.NOT_FOUND_ACCOUNT.getMessage().formatted(targetAccountId));
        }
        if (amount < 0) {
            throw new RuntimeException("Сумма перевода должна быть положительная");
        }
        if (sourceAccount.getMoneyAmount() < amount) {
            throw new RuntimeException(String.format("Недостаточно средств на счету %s, на данныйм момент %s\n",
                    sourceAccountId, sourceAccount.getMoneyAmount()));
        }
        sourceAccount.setMoneyAmount(sourceAccount.getMoneyAmount() - amount);
        if (!sourceAccount.getUserId().equals(targetAccount.getUserId())) {
            amount = amount - (amount * accountProperties.getTransferCommission());
        }
        targetAccount.setMoneyAmount(targetAccount.getMoneyAmount() + amount);
        return targetAccount;
    }

    public Account accountDeposit(Long accountId, Double amount) throws RuntimeException {
        if (!accounts.containsKey(accountId)) {
            throw new RuntimeException(Message.NOT_FOUND_ACCOUNT.getMessage().formatted(accountId));
        }
        if (amount < 0) {
            throw new RuntimeException("Сумма пополнения должна быть положительная");
        }
        Account account = accounts.get(accountId);
        account.setMoneyAmount(account.getMoneyAmount() + amount);
        return account;
    }

    public Account accountWithdraw(Long accountId, Double amount) throws RuntimeException {
        if (!accounts.containsKey(accountId)) {
            throw new RuntimeException(Message.NOT_FOUND_ACCOUNT.getMessage().formatted(accountId));
        }
        if (amount < 0) {
            throw new RuntimeException("Сумма списания должна быть положительная");
        }
        Account account = accounts.get(accountId);
        if (amount > account.getMoneyAmount()) {
            throw new RuntimeException("Недостаточно средств для списания");
        }
        account.setMoneyAmount(account.getMoneyAmount() - amount);
        return account;
    }
}
