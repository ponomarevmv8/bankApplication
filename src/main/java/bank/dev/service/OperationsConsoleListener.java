package bank.dev.service;

import bank.dev.entity.Account;
import bank.dev.util.Command;
import bank.dev.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.function.Supplier;

@Component
public class OperationsConsoleListener {

    private final Command commandList;

    @Autowired
    public OperationsConsoleListener(Command commandList) {
        this.commandList = commandList;
    }

    public void startBank(){
        try {
            System.out.println(Message.START.getMessage());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String commandStr;
            while ((commandStr = reader.readLine()) != null) {
                if (commandStr.equalsIgnoreCase("exit")) {
                    System.out.println("Завершение работы...");
                    break;
                }
                Supplier<Object> command = commandList.getCommand(commandStr.trim().toUpperCase());
                if(Objects.nonNull(command)){
                    command.get();
                } else {
                    System.out.println(Message.COMMAND_NOT_FOUND.getMessage());
                }
                System.out.println(Message.START.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
