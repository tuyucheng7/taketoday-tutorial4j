package cn.tuyucheng.taketoday.kotlingradleopenapi.api

import cn.tuyucheng.taketoday.car.api.CarsApi
import cn.tuyucheng.taketoday.car.model.Car
import cn.tuyucheng.taketoday.car.model.CarBody
import cn.tuyucheng.taketoday.kotlingradleopenapi.service.CarService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.Year
import cn.tuyucheng.taketoday.kotlingradleopenapi.service.Car as CarModel

@RestController
class CarCrudController(private val carService: CarService) : CarsApi {
	override fun createCar(carBody: CarBody): ResponseEntity<Car> {
		val (model, make, year) = carBody
		return carService.createCar(model, make, Year.of(year))
			.let { ResponseEntity.ok(it.toApiCar()) }
	}

	override fun getCar(id: Int): ResponseEntity<Car> {
		return carService.getCar(id)?.toApiCar()
			?.let { ResponseEntity.ok(it) }
			?: throw IllegalArgumentException("Car by id $id is not found")
	}

	override fun getCars(): ResponseEntity<List<Car>> {
		return carService.getAllCars().map { it.toApiCar() }
			.let { ResponseEntity.ok(it) }
	}

	private fun CarModel.toApiCar(): Car = Car(
		id = id,
		model = model,
		make = make,
		year = year.value,
	)
}