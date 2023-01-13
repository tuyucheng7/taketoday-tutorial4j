package cn.tuyucheng.taketoday.kotlingradleopenapi.service

import java.math.BigDecimal
import java.time.Year

data class Car(
	val id: Int,
	val model: String,
	val make: String,
	val year: Year,
	val price: BigDecimal
)