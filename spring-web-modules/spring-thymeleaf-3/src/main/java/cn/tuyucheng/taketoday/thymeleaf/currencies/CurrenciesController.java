package cn.tuyucheng.taketoday.thymeleaf.currencies;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;

@Controller
public class CurrenciesController {

    @GetMapping(value = "/currency")
    public String exchange(
          @RequestParam(value = "amount", required = false) String amount,
          @RequestParam(value = "amountList", required = false) List amountList,
          Locale locale) {

        return "currencies/currencies";
    }
}