package cn.tuyucheng.taketoday.springboottestkotlin

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class BankAccountService(var bankAccountRepository: BankAccountRepository) {

   fun addBankAccount(bankAccount: BankAccount): BankAccount {
      return bankAccountRepository.save(bankAccount);
   }

   fun getBankAccount(id: Long): BankAccount? {
      return bankAccountRepository.findByIdOrNull(id)
   }

}