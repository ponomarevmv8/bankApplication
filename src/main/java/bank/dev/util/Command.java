package bank.dev.util;


import bank.dev.service.AccountService;
import bank.dev.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class Command {

    private final Map<String, Supplier<Object>> commands = new HashMap<>();

    private final UserService userService;
    private final AccountService accountService;

    public Command(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @PostConstruct
    private void initCommands() {
        commands.put("USER_CREATE", userService::createUser);
        commands.put("SHOW_ALL_USERS", userService::showUsers);
        commands.put("ACCOUNT_CREATE", accountService::createAccount);
        commands.put("ACCOUNT_CLOSED", accountService::closeAccount);
        commands.put("ACCOUNT_TRANSFER", accountService::accountTransfer);
        commands.put("ACCOUNT_DEPOSIT", accountService::accountDeposit);
        commands.put("ACCOUNT_WITHDRAW", accountService::accountWithdraw);
    }

    public Supplier<Object> getCommand(String command) {
        if (!commands.containsKey(command)) {
            return null;
        }
        return commands.get(command);
    }

}
