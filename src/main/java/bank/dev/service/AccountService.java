package bank.dev.service;

import bank.dev.config.AccountProperties;
import bank.dev.entity.Account;
import bank.dev.entity.User;
import bank.dev.util.Message;
import bank.dev.util.TransactionHelper;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AccountService {

    private final UserService userService;
    private final AccountProperties accountProperties;
    private final TransactionHelper transactionHelper;
    private final SessionFactory sessionFactory;


    public AccountService(@Lazy UserService userService, AccountProperties accountProperties, TransactionHelper transactionHelper, SessionFactory sessionFactory) {
        this.userService = userService;
        this.accountProperties = accountProperties;
        this.transactionHelper = transactionHelper;
        this.sessionFactory = sessionFactory;
    }

    public Account createAccount(User user) {
        return transactionHelper.executeInTransaction(() -> {
            var session = sessionFactory.getCurrentSession();
            Account account = new Account();
            account.setUser(user);
            if (user.getAccounts().isEmpty()) {
                account.setMoneyAmount(accountProperties.getDefaultBalance());
            }
            session.persist(account);
            return account;
        });
    }

    public Account createAccount(Long userId) {
        User user = userService.getById(userId);
        return createAccount(user);
    }

    public void closeAccount(Long accountId) throws RuntimeException {

        transactionHelper.executeInTransaction(() -> {
            var session = sessionFactory.getCurrentSession();
            Account account = getById(accountId);
            List<Account> accountsUser = account.getUser().getAccounts();
            if (accountsUser.size() < 2) {
                throw new RuntimeException("Аккаунт невозможно закрыть, так как он единственный");
            }
            if (account.getMoneyAmount() != 0L) {
                Account accountTransfer = accountsUser.get(0).getId().equals(accountId) ?
                        accountsUser.get(1)
                        : accountsUser.get(0);
                accountTransfer.setMoneyAmount(accountTransfer.getMoneyAmount() + account.getMoneyAmount());
            }
            session.remove(account);
            return account;
        });
    }

    public Account getById(Long accountId) {

        return transactionHelper.executeInTransaction(() -> {
            var session = sessionFactory.getCurrentSession();
            return session.createQuery("""
                            select a from Account a left join fetch a.user u where a.id = :accountId
                            """, Account.class)
                    .setParameter("accountId", accountId)
                    .uniqueResultOptional().orElseThrow(
                            () -> new IllegalStateException(String.format(Message.NOT_FOUND_ACCOUNT.getMessage(), accountId))
                    );
        });

    }

    public Account accountTransfer(Long sourceAccountId, Long targetAccountId, Double amount) throws RuntimeException {
        Double amountTransfer = amount;
        if (amountTransfer < 0) {
            throw new RuntimeException("Сумма перевода должна быть положительная");
        }
        if (sourceAccountId == targetAccountId) {
            throw new RuntimeException("Выбранно два аккаунта с одинаковыми ID");
        }
        return transactionHelper.executeInTransaction(() -> {
            Account sourceAccount = getById(sourceAccountId);
            Account targetAccount = getById(targetAccountId);
            accountWithdraw(sourceAccountId, amountTransfer);
            Double amountTarget = amountTransfer;
            if (targetAccount.getUser().getId() != sourceAccount.getUser().getId()) {
                amountTarget = amountTransfer - (amountTransfer * accountProperties.getTransferCommission());
            }
            accountDeposit(targetAccountId, amountTarget);
            return targetAccount;
        });
    }

    public Account accountDeposit(Long accountId, Double amount) throws RuntimeException {
        if (amount < 0) {
            throw new RuntimeException("Сумма пополнения должна быть положительная");
        }
        return transactionHelper.executeInTransaction(() -> {
            Account account = getById(accountId);
            account.setMoneyAmount(account.getMoneyAmount() + amount);
//            if(account.getId() == accountId) {
//                throw new RuntimeException("Проверка отката транзакции");
//            }
            return account;
        });
    }

    public Account accountWithdraw(Long accountId, Double amount) throws RuntimeException {
        if (amount < 0) {
            throw new RuntimeException("Сумма списания должна быть положительная");
        }
        return transactionHelper.executeInTransaction(() -> {
            Account account = getById(accountId);
            if (amount > account.getMoneyAmount()) {
                throw new RuntimeException("Недостаточно средств для списания");
            }
            account.setMoneyAmount(account.getMoneyAmount() - amount);
            return account;
        });
    }
}
