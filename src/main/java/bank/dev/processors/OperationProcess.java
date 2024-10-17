package bank.dev.processors;

public interface OperationProcess {

    void process();

    OperationType getOperationType();

}
