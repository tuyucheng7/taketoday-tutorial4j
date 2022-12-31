package cn.tuyucheng.taketoday.immutable

class ImmutableList<T>(private val protectedList: List<T>) : List<T> by protectedList