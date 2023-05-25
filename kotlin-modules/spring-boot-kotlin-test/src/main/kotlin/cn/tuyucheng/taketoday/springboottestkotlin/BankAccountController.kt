package cn.tuyucheng.taketoday.springboottestkotlin

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bankAccount")
class BankController(var bankAccountService: BankAccountService) {

   @PostMapping
   fun addBankAccount(@RequestBody bankAccount: BankAccount): ResponseEntity<BankAccount> {
      return ResponseEntity.ok(bankAccountService.addBankAccount(bankAccount))
   }

   @GetMapping
   fun getBankAccount(@RequestParam id: Long): ResponseEntity<BankAccount> {
      var bankAccount: BankAccount? = bankAccountService.getBankAccount(id);
      if (bankAccount != null) {
         return ResponseEntity(bankAccount, HttpStatus.OK)
      } else {
         return ResponseEntity(HttpStatus.BAD_REQUEST)
      }
   }
}