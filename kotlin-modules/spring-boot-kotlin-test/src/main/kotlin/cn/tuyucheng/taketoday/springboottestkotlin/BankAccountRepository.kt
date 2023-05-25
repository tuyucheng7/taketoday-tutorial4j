package cn.tuyucheng.taketoday.springboottestkotlin

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BankAccountRepository : CrudRepository<BankAccount, Long> {

}
