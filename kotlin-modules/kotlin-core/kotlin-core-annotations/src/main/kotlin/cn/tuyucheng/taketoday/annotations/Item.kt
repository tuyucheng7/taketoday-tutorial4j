package cn.tuyucheng.taketoday.annotations

class Item(@Positive val amount: Float, @AllowedNames(["Alice", "Bob"]) val name: String)