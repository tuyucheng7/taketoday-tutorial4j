package cn.tuyucheng.taketoday.enums

interface ISecondaryColor : IColor {
   override fun type() = ColorType.SECONDARY
}