package bank.dev.service;

import bank.dev.config.AccountProperties;
import bank.dev.entity.Account;
import bank.dev.entity.User;
import bank.dev.util.Message;
import bank.dev.util.TransactionHelper;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionImplementor;
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
        return transactionHelper.executeInTransaction(session -> {
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
        Optional<User> user = userService.getById(userId);
        if (!user.isPresent()) {
            throw new IllegalStateException(String.format(Message.NOT_FOUND_USER.getMessage(),
                    userId));
        }
        return createAccount(user.get());
    }

    public void closeAccount(Long accountId) throws RuntimeException {

        transactionHelper.executeInTransaction(session -> {
            Optional<Account> accountOptional = getById(accountId);
            if (!accountOptional.isPresent()) {
                throw new RuntimeException(String.format(Message.NOT_FOUND_ACCOUNT.getMessage(), accountId));
            }
            Account account = accountOptional.get();
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
        });
    }

    private void printPersistent(Session session) {
        SessionImplementor sessionImpl = (SessionImplementor) session;
        PersistenceContext persistenceContext = sessionImpl.getPersistenceContext();
        var entity = persistenceContext.reentrantSafeEntityEntries();
        System.out.println(" --- смотрим всех persistent объекты ---");
        System.out.println(" --- кол-во " + entity.length + " ---");
        for (int i = 0; i < entity.length; i++) {
            var key = entity[i].getKey();
            System.out.println(" --- Entity persistent: " + key);
        }
    }

    public Optional<Account> getById(Long accountId) {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
        } catch (HibernateException e) {
            System.out.println("Not found opened session");
        }
        if (session == null) {
            try (Session sessionNew = sessionFactory.openSession()) {
                return sessionNew.createQuery("""
                                select a from Account a left join fetch a.user u where a.id = :accountId
                                """, Account.class)
                        .setParameter("accountId", accountId)
                        .uniqueResultOptional();
            }
        } else {
            if (session.getTransaction().isActive()) {
                SessionImplementor sessionImpl = (SessionImplementor) session;
                PersistenceContext persistenceContext = sessionImpl.getPersistenceContext();
                var account = persistenceContext.getEntity(new EntityKey(accountId, sessionImpl.getEntityPersister(Account.class.getName(), null)));
                if (account != null) {
                    return Optional.of((Account) account);
                }
                return session.createQuery("""
                                select a from Account a left join fetch a.user u where a.id = :accountId
                                """, Account.class)
                        .setParameter("accountId", accountId)
                        .uniqueResultOptional();
            }
            try (Session sessionNew = sessionFactory.openSession()) {
                return sessionNew.createQuery("""
                                select a from Account a left join fetch a.user u where a.id = :accountId
                                """, Account.class)
                        .setParameter("accountId", accountId)
                        .uniqueResultOptional();
            }
        }
    }

    public Account accountTransfer(Long sourceAccountId, Long targetAccountId, Double amount) throws RuntimeException {
        Double amountTransfer = amount;
        if (amountTransfer < 0) {
            throw new RuntimeException("Сумма перевода должна быть положительная");
        }
        if (sourceAccountId == targetAccountId) {
            throw new RuntimeException("Выбранно два аккаунта с одинаковыми ID");
        }
        return transactionHelper.executeInTransaction(session -> {
            Optional<Account> sourceAccountOpt = getById(sourceAccountId);
            Optional<Account> targetAccountOpt = getById(targetAccountId);
            if (!sourceAccountOpt.isPresent()) {
                throw new RuntimeException(Message.NOT_FOUND_ACCOUNT.getMessage().formatted(sourceAccountId));
            }
            if (!targetAccountOpt.isPresent()) {
                throw new RuntimeException(Message.NOT_FOUND_ACCOUNT.getMessage().formatted(targetAccountId));
            }
            var sourceAccount = sourceAccountOpt.get();
            var targetAccount = targetAccountOpt.get();

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
        return transactionHelper.executeInTransaction(session -> {
            Optional<Account> accountOptional = getById(accountId);
            if (!accountOptional.isPresent()) {
                throw new RuntimeException(Message.NOT_FOUND_ACCOUNT.getMessage().formatted(accountId));
            }
            Account account = accountOptional.get();
            account.setMoneyAmount(account.getMoneyAmount() + amount);
            session.merge(account);
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
        return transactionHelper.executeInTransaction(session -> {
            Optional<Account> accountOptional = getById(accountId);
            if (!accountOptional.isPresent()) {
                throw new RuntimeException(Message.NOT_FOUND_ACCOUNT.getMessage().formatted(accountId));
            }
            Account account = accountOptional.get();
            if (amount > account.getMoneyAmount()) {
                throw new RuntimeException("Недостаточно средств для списания");
            }
            account.setMoneyAmount(account.getMoneyAmount() - amount);
            session.merge(account);
            return account;
        });
    }
}
