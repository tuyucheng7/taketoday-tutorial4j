package cn.tuyucheng.taketoday.enums

interface IPrimaryColor : IColor {
   override fun type() = ColorType.PRIMARY
}