package cn.tuyucheng.taketoday.arguments

fun main() {
   cn.tuyucheng.taketoday.arguments.resizePane(newSize = 10, forceResize = true, noAnimation = false)

   // Swap the order of last two named arguments
   cn.tuyucheng.taketoday.arguments.resizePane(newSize = 11, noAnimation = false, forceResize = true)

   // Named arguments can be passed in any order
   cn.tuyucheng.taketoday.arguments.resizePane(forceResize = true, newSize = 12, noAnimation = false)

   // Mixing Named and Positional Arguments
   // Kotlin 1.3 would allow us to name only the arguments after the positional ones
   cn.tuyucheng.taketoday.arguments.resizePane(20, true, noAnimation = false)

   // Using a positional argument in the middle of named arguments (supported from Kotlin 1.4.0)
   // resizePane(newSize = 20, true, noAnimation = false)

   // Only the last argument as a positional argument (supported from Kotlin 1.4.0)
   // resizePane(newSize = 30, forceResize = true, false)

   // Use a named argument in the middle of positional arguments (supported from Kotlin 1.4.0)
   // resizePane(40, forceResize = true, false)
}

fun resizePane(newSize: Int, forceResize: Boolean, noAnimation: Boolean) {
   println("The parameters are newSize = $newSize, forceResize = $forceResize, noAnimation = $noAnimation")
}