package cn.tuyucheng.taketoday.kotest

class TransactionRepo {
    fun getStatus(transactionId: Int): String {
        return "COMPLETE"
    }
}