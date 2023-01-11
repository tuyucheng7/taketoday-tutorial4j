package cn.tuyucheng.taketoday.security.opa.service;

import cn.tuyucheng.taketoday.security.opa.domain.Account;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class AccountService {

    private Map<String, Account> accounts = ImmutableMap.<String, Account>builder()
          .put("0001", Account.of("0001", BigDecimal.valueOf(100.00), "USD"))
          .put("0002", Account.of("0002", BigDecimal.valueOf(101.00), "EUR"))
          .put("0003", Account.of("0003", BigDecimal.valueOf(102.00), "BRL"))
          .put("0004", Account.of("0004", BigDecimal.valueOf(103.00), "AUD"))
          .put("0005", Account.of("0005", BigDecimal.valueOf(10400.00), "JPY"))
          .build();


    public Mono<Account> findByAccountId(String accountId) {
        return Mono.just(accounts.get(accountId))
              .switchIfEmpty(Mono.error(new IllegalArgumentException("invalid.account")));
    }

}
