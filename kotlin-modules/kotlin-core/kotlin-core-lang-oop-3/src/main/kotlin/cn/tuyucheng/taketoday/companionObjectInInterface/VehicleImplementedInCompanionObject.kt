package cn.tuyucheng.taketoday.companionObjectInInterface

class VehicleImplementedInCompanionObject {

	companion object Bicycle : Vehicle {
		override fun getNumberOfWheels(): Int {
			return 2
		}
	}
}