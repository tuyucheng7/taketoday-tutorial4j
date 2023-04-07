## 一、概述

现代数据库系统经过量身定制，通过利用复杂的存储引擎来写入和读取数据，以保证一系列功能，例如可靠性、一致性、高吞吐量等。

在本教程中，我们将深入了解**[Apache Cassandra](https://www.baeldung.com/cassandra-with-java#Cassandra)****使用的存储引擎的内部结构，该引擎专为写入繁重的工作负载而设计，同时也保持良好的读取性能**。

## 2.日志结构合并树（LSMT）

**Apache Cassandra 利用基于两级日志结构合并树的数据结构进行存储**。在高层次上，在这样的 LSM 树中有两个树状组件，一个内存缓存组件 (C 0 )和一个磁盘组件 (C 1 )：
[![日志结构合并树](https://www.baeldung.com/wp-content/uploads/2022/09/LSMT.png)](https://www.baeldung.com/wp-content/uploads/2022/09/LSMT.png)

直接从内存读写通常比磁盘快。因此，根据设计，所有请求在到达 C 1之前先到达 C 0。此外，**同步操作会周期性地将数据从 C0 保存到 C1**。因此，它 **通过减少 I/O 操作来有效地使用网络带宽**。

在接下来的部分中，我们将详细了解Apache Cassandra 中二级 LSM 树的C 0和 C 1数据结构，通常分别称为 MemTable 和 SSTable。

## 3.内存表

顾名思义，**MemTable 是一种常驻内存的数据结构，如[红黑树，](https://www.baeldung.com/cs/red-black-trees)具有自平衡[二叉搜索树的](https://www.baeldung.com/cs/binary-search-trees)特性**。因此，所有的读写操作，即搜索、插入、更新和删除，都可以以 O(log n) 的时间复杂度来实现。

作为**内存中的可变数据结构，MemTable 使所有写入顺序进行并允许快速写入操作**。此外，由于物理内存的典型限制，例如容量有限和易失性，我们需要将数据从 MemTable 持久化到磁盘：
[![内存表](https://www.baeldung.com/wp-content/uploads/2022/09/MemTable-1024x499.png)](https://www.baeldung.com/wp-content/uploads/2022/09/MemTable.png)

一旦 MemTable 的大小达到阈值，所有的 r/w 请求都会切换到一个新的 MemTable，而旧的则在刷新到磁盘后被丢弃。

到目前为止，一切都很好！我们可以有效地处理大量的写入。但是，如果节点在刷新操作之前崩溃，会发生什么？嗯，很简单——我们会丢失尚未刷新到磁盘的数据。

在下一节中，我们将了解 Apache Cassandra 如何使用预写日志 (WAL) 的概念来解决这个问题。

## 4.提交日志

Apache Cassandra 推迟了将数据从内存保存到磁盘的刷新操作。因此，意外的节点或进程崩溃可能导致数据丢失。

持久性是任何现代数据库系统都必须具备的能力，Apache Cassandra 也不例外。**它通过确保所有写入都保存在名为 Commit Log 的仅附加文件中的磁盘上来保证持久性**。此后，它使用 MemTable 作为写路径中的回写缓存：

[![提交日志](https://www.baeldung.com/wp-content/uploads/2022/09/WAL-1024x618.png)](https://www.baeldung.com/wp-content/uploads/2022/09/WAL.png)

我们必须注意，仅追加操作速度很快，因为它们避免了磁盘上的随机查找。因此，提交日志在不影响写入性能的情况下带来了持久性能力。此外，**Apache Cassandra 仅在崩溃恢复场景中引用提交日志**，而常规读取请求不会转到提交日志。

## 5.SS表

**排序字符串表 (SSTable) 是 Apache Cassandra 存储引擎使用的 LSM 树的磁盘驻留组件**。它的名称源自 Google 的 BigTable 数据库最先使用的类似数据结构，并表示数据以排序格式提供。一般来说，MemTable 的每个刷新操作都会在 SSTable 中生成一个新的不可变段。

让我们尝试想象一下 SSTable 在包含有关动物园中饲养的各种动物数量的数据时的外观：
[![SS表](https://www.baeldung.com/wp-content/uploads/2022/09/SSTable-1024x543.png)](https://www.baeldung.com/wp-content/uploads/2022/09/SSTable.png)

虽然段是按键排序的，但是，相同的键可以出现在多个段中。因此，如果我们必须寻找特定的键，我们需要从最新的段开始搜索，并在找到它后立即返回结果。

使用这样的策略，最近写入的键的读取操作会很快。然而，在最坏的情况下，该算法的执行时间复杂度为 O( *N* *log( *K* ))，其中*N*是段总数，*K*是段大小。由于*K*是常数，我们可以说整体时间复杂度为 O( *N* )，效率不高。

在接下来的几节中，我们将了解 Apache Cassandra 如何优化 SSTable 的读取操作。

## 6. 稀疏索引

**Apache Cassandra 维护一个稀疏索引以限制它在查找键时需要扫描的段数**。

稀疏索引中的每个条目都包含段的第一个成员，以及它在磁盘上的页面偏移位置。此外，索引作为[B-Tree 数据结构](https://www.baeldung.com/cs/b-tree-data-structure)在内存中维护，因此我们可以以 O(log( *K* )) 的时间复杂度在索引中搜索偏移量。

假设我们要搜索键“啤酒”。我们将从搜索稀疏索引中出现在单词“beer”之前的所有键开始。之后，使用偏移值，我们将只查看有限数量的段。在本例中，我们将查看第一个键为“alligator”的第四段：

[![稀疏索引](https://www.baeldung.com/wp-content/uploads/2022/09/sparse-index-1024x398.png)](https://www.baeldung.com/wp-content/uploads/2022/09/sparse-index.png)

另一方面，如果我们必须搜索一个不存在的键，例如“kangaroo”，我们将不得不徒劳地查看所有片段。因此，我们意识到使用稀疏索引可以在有限的范围内优化搜索。

此外，我们应该注意到 SSTable 允许相同的键出现在不同的段中。因此，随着时间的推移，同一个键会发生越来越多的更新，从而在稀疏索引中也会产生重复的键。

在接下来的部分中，我们将了解 Apache Cassandra 如何借助布隆过滤器和压缩来解决这两个问题。

## 7.布隆过滤器

Apache Cassandra 使用称为[布隆过滤器的](https://www.baeldung.com/cs/bloom-filter)概率数据结构优化读取查询。简而言之，**它通过首先使用布隆过滤器对密钥执行成员资格检查来优化搜索。**

因此，通过将布隆过滤器附加到 SSTable 的每个段，我们可以显着优化我们的读取查询，尤其是对于段中不存在的键：

[![布隆过滤器](https://www.baeldung.com/wp-content/uploads/2022/09/Bloom-Filter-1024x445.png)](https://www.baeldung.com/wp-content/uploads/2022/09/Bloom-Filter.png)

由于布隆过滤器是概率数据结构，我们可以获得“可能”作为响应，即使对于丢失的键也是如此。但是，如果我们得到“否”作为响应，我们可以确定密钥肯定丢失了。

尽管它们有局限性，但**我们可以计划通过为它们分配更大的存储空间来提高布隆过滤器的准确性**。

## 8.压实

尽管使用布隆过滤器和稀疏索引，读取查询的性能会随着时间的推移而下降。这是因为包含不同版本键的段的数量可能会随着每次 MemTable 刷新操作而增加。

为了解决这个问题，Apache Cassandra 运行了一个后台压缩进程，将[较小的排序段合并为较大的段](https://www.baeldung.com/java-merge-sorted-arrays#algorithm)，同时只保留每个键的最新值。因此，**压缩过程提供了更快读取和更少存储的双重好处**。

让我们看看在我们现有的 SSTable 上运行一次压缩会是什么样子：

[![压实](https://www.baeldung.com/wp-content/uploads/2022/09/compaction-1024x536.png)](https://www.baeldung.com/wp-content/uploads/2022/09/compaction.png)

我们注意到压缩操作通过只保留最新版本回收了一些空间。例如，“elephant”和“tiger”等旧版本的键不再存在，从而释放了磁盘空间。

此外，压缩过程可以硬删除密钥。虽然删除操作会用墓碑标记一个键，但实际的删除操作会推迟到压缩时进行。

## 9.结论

在本文中，我们探讨了 Apache Cassandra 存储引擎的内部组件。在这样做的同时，**我们了解了高级数据结构概念，例如 LSM Tree、MemTable 和 SSTable**。此外，我们还学习了一些使用预写日志记录、布隆过滤器、稀疏索引和压缩的优化技术。