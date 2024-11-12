package bank.dev.service;

import bank.dev.entity.User;
import bank.dev.util.Message;
import bank.dev.util.TransactionHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserService {

    private final AccountService accountService;
    private final TransactionHelper transactionHelper;
    private final SessionFactory sessionFactory;

    public UserService(AccountService accountService, TransactionHelper transactionHelper, SessionFactory sessionFactory) {
        this.accountService = accountService;
        this.transactionHelper = transactionHelper;
        this.sessionFactory = sessionFactory;
    }

    public User createUser(String login) {
        if (checkLogin(login)) {
            throw new RuntimeException(String.format(Message.USER_LOGIN_EXISTS.getMessage(), login));
        }
        User userCreate = transactionHelper.executeInTransaction(session -> {
            User user = new User();
            user.setLogin(login);
            session.persist(user);
            user.setAccounts(new ArrayList<>(Arrays.asList(accountService.createAccount(user))));
            return user;
        });
        return userCreate;
    }

    private boolean checkLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            Optional<Integer> count = session.createQuery("SELECT 1 from User u where u.login = :login", Integer.class)
                    .setParameter("login", login)
                    .uniqueResultOptional();
            return count.isPresent();
        }
    }

    public List<User> showUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select u from User u left join fetch u.accounts a", User.class).list();
        }
    }

    // TODO: в будущем добавить проверку на наличие сущности в persistant
    public Optional<User> getById(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select u from User u left join fetch Account a on u = a.user where u.id=:userId", User.class)
                    .setParameter("userId", userId)
                    .uniqueResultOptional();
        }
    }
}
