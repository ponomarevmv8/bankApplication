package bank.dev.service;

import bank.dev.entity.Account;
import bank.dev.entity.User;
import bank.dev.util.Message;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserService {

    private final AccountService accountService;

    private final Map<String, User> users = new HashMap<>();

    private final Map<Long, String> logins = new HashMap<>();

    private long userId = 1;

    public UserService(@Lazy AccountService accountService) {
        this.accountService = accountService;
    }

    public User createUser(String login) {
        if(users.containsKey(login)) {
            throw new RuntimeException(String.format(Message.USER_LOGIN_EXISTS.getMessage(), login));
        }
        User user = new User();
        user.setLogin(login);
        user.setId(userId++);
        user.setAccounts(List.of(accountService.createAccount(user.getId(), true)));
        users.put(login, user);
        logins.put(user.getId(), login);
        return user;
    }

    public List<User> showUsers() {
        return new ArrayList<>(users.values());
    }

    public boolean checkLogin(Long userId) {
        return logins.containsKey(userId);
    }

    public User getUser(String login) {
        if(!logins.containsKey(login)) {
            return null;
        }
        return users.get(login);
    }

    public User getUser(Long userId) {
        if(!logins.containsKey(userId)) {
            return null;
        }
        return users.get(logins.get(userId));
    }

    public void modifyAccount(Long userId, List<Account> accounts) {
        if(!logins.containsKey(userId)) {
            return;
        }
        User user = getUser(userId);
        user.setAccounts(accounts);
    }
}
