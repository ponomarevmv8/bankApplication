package bank.dev.processors;

public interface OperationProcessor {

    void process();

    OperationType getOperationType();

}
