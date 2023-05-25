package cn.tuyucheng.taketoday.mockk

fun coinFlip() = if (cn.tuyucheng.taketoday.mockk.RandomNumberGenerator.random() < 0.5) "heads" else "tails"
