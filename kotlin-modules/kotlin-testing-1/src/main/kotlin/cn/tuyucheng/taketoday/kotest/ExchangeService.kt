package cn.tuyucheng.taketoday.kotest

class ExchangeService(val exchangeRateProvider: ExchangeRateProvider) {
    fun exchange(money: Money, targetCurrency: String) =
            (exchangeRateProvider.rate(money.currencyCode + targetCurrency) * money.minorAmount).toLong()
}