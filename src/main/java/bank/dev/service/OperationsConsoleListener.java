package bank.dev.service;

import bank.dev.processors.OperationProcessor;
import bank.dev.processors.OperationType;
import bank.dev.util.Message;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class OperationsConsoleListener {

    private final Map<OperationType, OperationProcessor> operationProcesses;
    private final Scanner scanner;


    public OperationsConsoleListener(Scanner scanner, List<OperationProcessor> operationProcesses) {
        this.scanner = scanner;
        this.operationProcesses = operationProcesses.stream()
                .collect(
                        Collectors.toMap(
                                OperationProcessor::getOperationType,
                                operationProcess -> operationProcess
                        )
                );
    }

    public void listenConsole() {
        while (true) {
            var operationType = listenNextOperation();
            if (operationType == null) {
                return;
            }
            processNextOperation(operationType);
        }
    }

    private OperationType listenNextOperation() {
        System.out.println("\n" + Message.START.getMessage());
        while (true) {
            var operationType = scanner.nextLine().trim().toUpperCase();
            if (operationType.equals("EXIT")) {
                System.out.println("Exit program");
                return null;
            }
            try {
                return OperationType.valueOf(operationType);
            } catch (Exception e) {
                System.out.println(Message.COMMAND_NOT_FOUND.getMessage());
            }
        }
    }

    private void processNextOperation(OperationType operationType) {
        try {
            operationProcesses.get(operationType).process();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
