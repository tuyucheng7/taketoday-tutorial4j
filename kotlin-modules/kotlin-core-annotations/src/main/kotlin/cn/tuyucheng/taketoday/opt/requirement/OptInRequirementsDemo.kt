@file:OptIn(ExperimentalClassApi::class)

package cn.tuyucheng.taketoday.opt.requirement

class OptInRequirementsDemo {

   @ExperimentalClassApi
   fun someExperimentalApi(): Unit {
   }

   @ExperimentalClassApi
   fun anotherExperimentalApi() {
      someExperimentalApi()
   }

   @OptIn(ExperimentalClassApi::class)
   fun OptInOnEntireFunction() {
      someExperimentalApi()
   }

   @OptIn(ExperimentalClassApi::class)
   var i = someExperimentalApi()
}