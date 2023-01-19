## 1. 概述

在本教程中，我们将介绍Locked Ownable Synchronizers的含义。编写一个使用Lock进行同步的简单程序，并查看它在thread dump中的样子。

## 2. 什么是Locked Ownable Synchronizers?

每个线程都可能有一个同步器对象列表。该列表中的条目表示线程已为其获取锁的可拥有同步器。

AbstractOwnableSynchronizer类的实例可以用作同步器。它最常见的子类之一是Sync类，它是Lock接口实现的一个字段，如ReentrantReadWriteLock。

当我们调用 ReentrantReadWriteLock.lock()方法时，代码在内部将其委托给Sync.lock()方法。
一旦我们获得了锁，Lock对象就会被添加到线程的已锁定可拥有同步器列表中。

我们可以在典型的线程转储中查看此列表：

