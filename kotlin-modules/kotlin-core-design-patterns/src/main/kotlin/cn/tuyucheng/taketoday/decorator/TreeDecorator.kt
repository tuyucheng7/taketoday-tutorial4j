package cn.tuyucheng.taketoday.decorator

abstract class TreeDecorator
(private val tree: ChristmasTree) : ChristmasTree {

   override fun decorate(): String {
      return tree.decorate()
   }
}