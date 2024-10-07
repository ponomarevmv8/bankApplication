package bank.dev.service;

import bank.dev.entity.Account;
import bank.dev.entity.User;
import bank.dev.util.Message;
import bank.dev.util.Util;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserService {

    private final AccountService accountService;

    private final Map<String, User> users = new HashMap<>();

    private final Map<Long, String> logins = new HashMap<>();

    public UserService(@Lazy AccountService accountService) {
        this.accountService = accountService;
    }

    public User createUser() {
        try {
            System.out.println("Создание Юзера...");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String command;
            System.out.println(Message.ENTER_LOGIN_USER_ID.getMessage());
            while ((command = reader.readLine()) != null) {
                String login = command.trim();
                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Завершение создания Юзера");
                    break;
                }
                if(users.containsKey(login)) {
                    System.out.printf(Message.USER_LOGIN_EXISTS.getMessage(), login);
                    continue;
                }
                User user = createUser(login);
                System.out.printf(Message.USER_CREATED.getMessage(), login);
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User createUser(String login) {
        if(users.containsKey(login)) {
            throw new RuntimeException("Пользователь с данным логином существует");
        }
        User user = new User();
        user.setLogin(login);
        user.setId(Util.generateId());
        user.setAccounts(List.of(accountService.createAccount(user.getId())));
        users.put(login, user);
        logins.put(user.getId(), login);
        return user;
    }

    public List<User> showUsers() {
        System.out.println("List of all users: " + users.values());
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
