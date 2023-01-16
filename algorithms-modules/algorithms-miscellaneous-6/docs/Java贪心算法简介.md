## 1. 简介

在本教程中，我们将[介绍Java生态系统中的贪婪算法](https://www.baeldung.com/cs/greedy-approach-vs-dynamic-programming)。 

## 2.贪心问题

面对一个数学问题时，可能有几种方法来设计解决方案。我们可以实现迭代解决方案，或一些高级技术，例如分而治之原则(例如[快速排序算法](https://www.baeldung.com/java-quicksort))或动态规划方法(例如[背包问题](https://www.baeldung.com/java-knapsack))等等。

大多数时候，我们都在寻找最佳解决方案，但遗憾的是，我们并不总能得到这样的结果。但是，在某些情况下，即使是次优结果也很有价值。在某些特定策略或 启发式的帮助下，我们可能会为自己赢得如此宝贵的奖励。

在这种情况下，给定一个可分问题， 在过程的每个阶段都采取局部最优选择或“贪心选择”[的策略称为贪心算法。](https://www.baeldung.com/cs/greedy-approach-vs-dynamic-programming)

我们声明我们应该解决一个“可分割”的问题：一种可以描述为一组具有几乎相同特征的子问题的情况。因此，大多数时候，贪婪算法将被实现为递归算法。

尽管环境恶劣，但贪心算法可以引导我们找到合理的解决方案；缺乏计算资源、执行时间限制、API 限制或任何其他类型的限制。

### 2.1. 设想

在这个简短的教程中，我们将实施贪婪策略，以使用其 API 从社交网络中提取数据。

假设我们希望在“little-blue-bird”社交网站上吸引更多用户。实现我们目标的最佳方式是发布原创内容或转发能够引起广大受众兴趣的内容。

我们如何找到这样的观众？好吧，我们必须找到一个拥有很多粉丝的帐户并为他们发推文。

### 2.2. 经典与贪婪

我们考虑以下情况：我们的账户有四个关注者，每个关注者如下图所示，分别有 2、2、1 和 3 个关注者，依此类推：

[![藻类1](https://www.baeldung.com/wp-content/uploads/2020/01/alg1.png)](https://www.baeldung.com/wp-content/uploads/2020/01/alg1.png)

考虑到这个目的，我们将在我们帐户的关注者中选择关注者更多的人。然后我们将重复这个过程两次，直到我们达到第 3 级连接(总共四个步骤)。

通过这种方式，我们定义了一条由用户组成的路径，引导我们从我们的帐户中获得最大的关注者群。如果我们可以向他们提供一些内容，他们肯定会访问我们的页面。

我们可以从“传统”方法开始。在每一个步骤中，我们都会执行查询以获取帐户的关注者。作为我们选择过程的结果，帐户数量每一步都会增加。

令人惊讶的是，我们最终总共执行了 25 次查询：

[![算法3](https://www.baeldung.com/wp-content/uploads/2020/01/alg3.png)](https://www.baeldung.com/wp-content/uploads/2020/01/alg3.png)

这里出现了一个问题：例如，Twitter API 将这种类型的查询限制为每 15 分钟 15 次。如果我们尝试执行比允许更多的调用，我们将得到“超出速率限制代码 – 88 ”，或者“由于应用程序的资源速率限制已耗尽而无法满足请求时在 API v1.1 中返回” “。我们怎样才能克服这样的限制呢？

好吧，答案就在我们面前：贪心算法。如果我们使用这种方法，在每个步骤中，我们可以假设拥有最多关注者的用户是唯一要考虑的用户：最后，我们只需要四个查询。相当大的进步！

[![算法2](https://www.baeldung.com/wp-content/uploads/2020/01/alg2.png)](https://www.baeldung.com/wp-content/uploads/2020/01/alg2.png)

这两种方法的结果将是不同的。在第一种情况下，我们得到 16，即最佳解决方案，而在后者中，可达到的追随者的最大数量仅为 12。

这种差异会如此有价值吗？我们稍后再决定。

## 3.实施

为实现上述逻辑，我们初始化一个小型Java程序，我们将在其中模拟 Twitter API。我们还将使用[Lombok](https://www.baeldung.com/intro-to-project-lombok)库。

现在，让我们定义我们的组件SocialConnector，我们将在其中实现我们的逻辑。请注意，我们将放置一个计数器来模拟呼叫限制，但我们会将其降低到四个：

```java
public class SocialConnector {
    private boolean isCounterEnabled = true;
    private int counter = 4;
    @Getter @Setter
    List users;

    public SocialConnector() {
        users = new ArrayList<>();
    }

    public boolean switchCounter() {
        this.isCounterEnabled = !this.isCounterEnabled;
        return this.isCounterEnabled;
    }
}

```

然后我们将添加一个方法来检索特定帐户的关注者列表：

```java
public List getFollowers(String account) {
    if (counter < 0) {
        throw new IllegalStateException ("API limit reached");
    } else {
        if (this.isCounterEnabled) {
            counter--;
        }
        for (SocialUser user : users) {
            if (user.getUsername().equals(account)) {
                return user.getFollowers();
            }
        }
     }
     return new ArrayList<>();
}

```

为了支持我们的流程，我们需要一些类来为我们的用户实体建模：

```java
public class SocialUser {
    @Getter
    private String username;
    @Getter
    private List<SocialUser> followers;

    @Override
    public boolean equals(Object obj) {
        return ((SocialUser) obj).getUsername().equals(username);
    }

    public SocialUser(String username) {
        this.username = username;
        this.followers = new ArrayList<>();
    }

    public SocialUser(String username, List<SocialUser> followers) {
        this.username = username;
        this.followers = followers;
    }

    public void addFollowers(List<SocialUser> followers) {
        this.followers.addAll(followers);
    }
}
```

### 3.1. 贪心算法

最后，是时候实施我们的贪心策略了，所以让我们添加一个新组件——GreedyAlgorithm——我们将在其中执行递归：

```java
public class GreedyAlgorithm {
    int currentLevel = 0;
    final int maxLevel = 3;
    SocialConnector sc;
    public GreedyAlgorithm(SocialConnector sc) {
        this.sc = sc;
    }
}
```

然后我们需要插入一个方法findMostFollowersPath，我们将在其中找到拥有最多粉丝的用户，统计他们，然后继续下一步：

```java
public long findMostFollowersPath(String account) {
    long max = 0;
    SocialUser toFollow = null;

    List followers = sc.getFollowers(account);
    for (SocialUser el : followers) {
        long followersCount = el.getFollowersCount();
        if (followersCount > max) {
            toFollow = el;
            max = followersCount;
        }
    }
    if (currentLevel < maxLevel - 1) {
        currentLevel++;
        max += findMostFollowersPath(toFollow.getUsername());
    } 
    return max;
}
```

请记住：这是我们执行贪婪选择的地方。因此，每次我们调用此方法时，我们将从列表中选择一个且仅一个元素并继续：我们永远不会改变我们的决定！

完美的！我们准备好了，我们可以测试我们的应用程序了。在此之前，我们需要记住填充我们的微型网络，最后执行以下单元测试：

```java
public void greedyAlgorithmTest() {
    GreedyAlgorithm ga = new GreedyAlgorithm(prepareNetwork());
    assertEquals(ga.findMostFollowersPath("root"), 5);
}
```

### 3.2. 非贪心算法

让我们创建一个非贪婪的方法，只是为了用我们的眼睛检查会发生什么。因此，我们需要从构建NonGreedyAlgorithm类开始：

```java
public class NonGreedyAlgorithm {
    int currentLevel = 0;
    final int maxLevel = 3; 
    SocialConnector tc;

    public NonGreedyAlgorithm(SocialConnector tc, int level) {
        this.tc = tc;
        this.currentLevel = level;
    }
}
```

让我们创建一个等效的方法来检索关注者：

```java
public long findMostFollowersPath(String account) {		
    List<SocialUser> followers = tc.getFollowers(account);
    long total = currentLevel > 0 ? followers.size() : 0;

    if (currentLevel < maxLevel ) {
        currentLevel++;
        long[] count = new long[followers.size()];
        int i = 0;
        for (SocialUser el : followers) {
            NonGreedyAlgorithm sub = new NonGreedyAlgorithm(tc, currentLevel);
            count[i] = sub.findMostFollowersPath(el.getUsername());
            i++;
        }

        long max = 0;
        for (; i > 0; i--) {
            if (count[i-1] > max) {
                max = count[i-1];
            }
        }		
        return total + max;
     }	
     return total;
}
```

当我们的类准备就绪时，我们可以准备一些单元测试：一个用于验证调用限制是否超过，另一个用于检查使用非贪婪策略返回的值：

```java
public void nongreedyAlgorithmTest() {
    NonGreedyAlgorithm nga = new NonGreedyAlgorithm(prepareNetwork(), 0);
    Assertions.assertThrows(IllegalStateException.class, () -> {
        nga.findMostFollowersPath("root");
    });
}

public void nongreedyAlgorithmUnboundedTest() {
    SocialConnector sc = prepareNetwork();
    sc.switchCounter();
    NonGreedyAlgorithm nga = new NonGreedyAlgorithm(sc, 0);
    assertEquals(nga.findMostFollowersPath("root"), 6);
}
```

## 4. 结果

是时候回顾一下我们的工作了！

首先，我们尝试了我们的贪心策略，检查其有效性。然后我们通过详尽的搜索验证了情况，有和没有 API 限制。

我们的快速贪心程序每次都会做出局部最优选择，并返回一个数值。另一方面，由于环境限制，我们不会从非贪婪算法中得到任何东西。

比较这两种方法的输出，我们可以理解我们的贪婪策略是如何拯救我们的，即使检索到的值不是最优的。我们可以称之为局部最优。

## 5.总结

在社交媒体等瞬息万变的环境中，需要找到最佳解决方案的问题可能会变成可怕的幻想：难以实现，同时又不切实际。

克服限制和优化 API 调用是一个很重要的主题，但正如我们所讨论的，贪心策略是有效的。选择这种方法可以为我们省去很多痛苦，换来有价值的结果。

请记住，并非每种情况都适合：我们每次都需要评估我们的情况。