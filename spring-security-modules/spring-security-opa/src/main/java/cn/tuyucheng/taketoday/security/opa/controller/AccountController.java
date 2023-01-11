package cn.tuyucheng.taketoday.security.opa.controller;

import cn.tuyucheng.taketoday.security.opa.domain.Account;
import cn.tuyucheng.taketoday.security.opa.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/account/{accountId}")
    public Mono<Account> getAccount(@PathVariable("accountId") String accountId) {
        return accountService.findByAccountId(accountId);
    }
}
