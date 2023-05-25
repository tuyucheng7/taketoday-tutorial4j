package cn.tuyucheng.taketoday.abstractfactory

class Katana : Weapon {

   companion object Factory : WeaponFactory() {
      override fun buildWeapon() = Katana()
   }

   override fun use(): String {
      return "Using katana weapon"
   }
}