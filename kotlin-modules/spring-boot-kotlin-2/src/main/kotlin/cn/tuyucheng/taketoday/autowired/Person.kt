package cn.tuyucheng.taketoday.autowired

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/person")
class PersonController(
      val personService: cn.tuyucheng.taketoday.autowired.PersonService,
      val addressService: cn.tuyucheng.taketoday.autowired.AddressService,
      val locationService: cn.tuyucheng.taketoday.autowired.LocationService
) {

   @GetMapping
   fun person(): String {
      return "Hello ${personService.person.name}, Address: ${personService.address.fullAddress}"
   }
}

@Service
class PersonService {
   var person: cn.tuyucheng.taketoday.autowired.Person
   lateinit var address: cn.tuyucheng.taketoday.autowired.Address

   constructor(person: cn.tuyucheng.taketoday.autowired.Person) {
      this.person = person
   }

   @Autowired
   constructor(person: cn.tuyucheng.taketoday.autowired.Person, address: cn.tuyucheng.taketoday.autowired.Address) : this(person) {
      this.address = address
   }
}

@Service
class LocationService() {
   var lat: Double = 109.344550
   var lon: Double = 133.973849

   constructor(lat: Double, lon: Double) : this() {
      this.lat = lat
      this.lon = lon
   }
}

@Service
class AddressService @Autowired constructor(val address: cn.tuyucheng.taketoday.autowired.Address)

@Component
class Address(val fullAddress: String = "101 st, abc city, xyz state 12345")

@Component
class Person(val name: String = "John Doe")