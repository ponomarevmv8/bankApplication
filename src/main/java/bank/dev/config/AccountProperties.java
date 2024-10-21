package bank.dev.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccountProperties {

    private final double transferCommission;
    private final double defaultBalance;

    public AccountProperties(@Value("${account.transfer-commission}") double transferCommission,
                             @Value("${account.default-amount}") int defaultBalance) {
        this.transferCommission = transferCommission;
        this.defaultBalance = defaultBalance;
    }

    public double getTransferCommission() {
        return transferCommission;
    }

    public double getDefaultBalance() {
        return defaultBalance;
    }
}
