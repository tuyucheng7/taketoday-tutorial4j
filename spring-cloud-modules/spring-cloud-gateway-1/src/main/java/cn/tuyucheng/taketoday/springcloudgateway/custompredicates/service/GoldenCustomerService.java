package cn.tuyucheng.taketoday.springcloudgateway.custompredicates.service;

import org.springframework.stereotype.Component;

@Component
public class GoldenCustomerService {

    public boolean isGoldenCustomer(String customerId) {

        // TODO: Add some AI logic to check is this customer deserves a "golden" status ;^)
        if ("baeldung".equalsIgnoreCase(customerId)) {
            return true;
        } else {
            return false;
        }
    }

}
