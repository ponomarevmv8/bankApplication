package bank.dev.util;


public enum Message {

    START("Please enter one of operation type:\n" +
            "-ACCOUNT_CREATE\n" +
            "-SHOW_ALL_USERS\n" +
            "-ACCOUNT_CLOSED\n" +
            "-ACCOUNT_WITHDRAW\n" +
            "-ACCOUNT_DEPOSIT\n" +
            "-ACCOUNT_TRANSFER\n" +
            "-USER_CREATE\n"),
    COMMAND_NOT_FOUND("Command not found\n"),
    SOURCE_ACCOUNT("Enter source account ID:\n"),
    TARGET_ACCOUNT("Enter target account ID:\n"),
    TRANSFER("Amount %s transferred from account ID %s to account ID %s.\n"),
    DEPOSIT("Amount %s deposited to account ID %s.\n"),
    WITHDRAW("Amount %s withdrawn from account ID %s.\n"),
    NOT_FOUND_ACCOUNT("Account ID: %s not found\n"),
    ENTER_USER_ID("Enter user ID:\n"),
    ENTER_LOGIN_USER_ID("Enter login for new User:\n"),
    USER_LOGIN_EXISTS("User %s already exists.\n"),
    USER_CREATED("User %s created.\n"),
    ENTER_ACCOUNT_ID("Enter account ID:\n"),
    NOT_FOUND_USER("User ID: %s not found\n"),
    AMOUNT("Enter amount\n"),
    CREATE_ACCOUNT("New account created with ID: %s for user: %s\n"),
    CLOSE_ACCOUNT("Account with ID %s has been closed.\n"),
    EXIT("Enter \"exit\" to stop operations\n"),
    NOT_CLOSED_ACCOUNT("Account with ID %s has not been closed.\n");


    private final String message;

    Message(String text){
        this.message = text;
    }

    public String getMessage(){
        return message;
    }
}
