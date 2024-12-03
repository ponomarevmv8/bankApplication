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
        return transactionHelper.executeInTransaction(() -> {
            var session = sessionFactory.getCurrentSession();
            Optional<Integer> count = session.createQuery("SELECT 1 from User u where u.login = :login", Integer.class)
                    .setParameter("login", login)
                    .uniqueResultOptional();
            if (count.isPresent()) {
                throw new RuntimeException(String.format(Message.USER_LOGIN_EXISTS.getMessage(), login));
            }
            User user = new User();
            user.setLogin(login);
            session.persist(user);
            user.setAccounts(new ArrayList<>(Arrays.asList(accountService.createAccount(user))));
            return user;
        });
    }

    public List<User> showUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select u from User u left join fetch u.accounts a", User.class).list();
        }
    }

    public User getById(Long userId) {
        return transactionHelper.executeInTransaction(() -> {
            var session = sessionFactory.getCurrentSession();
            return session.createQuery("select u from User u left join fetch Account a on u = a.user where u.id=:userId", User.class)
                    .setParameter("userId", userId)
                    .uniqueResultOptional()
                    .orElseThrow(
                            () -> new IllegalStateException(String.format(Message.NOT_FOUND_USER.getMessage(),
                                    userId))
                    );
        });
    }
}
