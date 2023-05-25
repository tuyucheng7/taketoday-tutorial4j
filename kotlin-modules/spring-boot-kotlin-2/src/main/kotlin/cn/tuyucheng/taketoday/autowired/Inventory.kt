package cn.tuyucheng.taketoday.autowired

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/inventory")
class InventoryController(val inventoryService: cn.tuyucheng.taketoday.autowired.InventoryService) {
   @GetMapping
   fun inventory(): List<cn.tuyucheng.taketoday.autowired.Vehicle> {
      return inventoryService.inventory()
   }
}

@Service
class InventoryService @Autowired constructor(
      val vehicleDao: cn.tuyucheng.taketoday.autowired.VehicleDao
) {
   fun inventory(): List<cn.tuyucheng.taketoday.autowired.Vehicle> {
      return listOf(
            cn.tuyucheng.taketoday.autowired.Vehicle(2022, "Honda", "CRV", "LE", cn.tuyucheng.taketoday.autowired.Dealer(1001, "ABC Automotive")),
            cn.tuyucheng.taketoday.autowired.Vehicle(2022, "Toyota", "Camry", "XLE", cn.tuyucheng.taketoday.autowired.Dealer(1002, "XYZ Dealership"))
      )
   }

   @Autowired
   lateinit var dealerDao: cn.tuyucheng.taketoday.autowired.DealerDao
}

@Component
class DealerDao {
   @set: Autowired
   lateinit var reviews: cn.tuyucheng.taketoday.autowired.DealerReviewsDao
}

@Component
class DealerReviewsDao {

}

@Component
class VehicleDao {
   lateinit var vehicleValueFinder: cn.tuyucheng.taketoday.autowired.VehicleValueFinder
   lateinit var vehicleReviewDao: cn.tuyucheng.taketoday.autowired.VehicleReviewDao

   @Autowired
   fun initialize(vehicleValueFinder: cn.tuyucheng.taketoday.autowired.VehicleValueFinder,
                  vehicleReviewDao: cn.tuyucheng.taketoday.autowired.VehicleReviewDao) {
      this.vehicleValueFinder = vehicleValueFinder
      this.vehicleReviewDao = vehicleReviewDao
   }
}

@Component
class VehicleReviewDao {

}

@Component
class VehicleValueFinder {

}

data class Vehicle(
      val year: Int,
      val make: String,
      val model: String,
      val trim: String,
      val dealer: cn.tuyucheng.taketoday.autowired.Dealer
)

data class Dealer(
      val dealerId: Int,
      val dealerName: String
)
