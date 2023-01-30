## 1. 概述

在本教程中，我们将讨论动态规划和 Q-Learning 之间的区别。两者都是基于给定环境创建模型的算法类。它们用于创建策略，决定哪些操作会产生最佳结果。

## 2. 动态规划

要使用动态规划算法，我们需要一个有限马尔可夫决策过程 (MDP) 来描述我们的环境。因此，我们需要知道我们的行为的概率。我们产生的结果是确定性的，例如，我们借助动态规划算法检索的策略始终相同。另一个好处是它总是收敛的。

由于这是对马尔可夫决策过程和动态规划如何工作的相当抽象的描述，让我们从一个例子开始。

### 2.1. 动态规划示例

马尔可夫决策过程的一个简单示例是描述婴儿的状态。正如我们所见，婴儿可以睡觉、平静、哭泣或快乐。从睡眠和平静状态，可以在两个动作之间进行选择，唱歌或等待：

![宝贝.drawio](https://www.baeldung.com/wp-content/uploads/sites/4/2021/11/Baby.drawio.png)

我们可以看到所有的概率都是给定的。如果我们选择一个动作，我们就会确切地知道我们以多大的概率进入哪个状态。例如，等待产生快乐的可能性非常小，哭泣的可能性也很小。

与之相反的是唱歌。它会带来很高的幸福机会，但也会增加婴儿哭闹的风险。我们希望通过动态规划实现的结果是知道哪些决策会带来最高的回报。这意味着孩子变得快乐的可能性很高。但孩子开始哭泣的可能性也很低。

我们的最终政策![pi](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed7678864de5d2f3ff6739ada3fd00e9_l3.svg)将告诉我们在哪个州采取哪个行动。从数学的角度来看，我们看一下价值函数![V^pi](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e7987f0831d4bca0730d42f5a07e44a6_l3.svg)：

![V^pi(s) = E_{pi} {G_t vert s_t = s}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8aff231a4d152b555b1eca4181ac6d77_l3.svg)

价值函数描述了预期的回报，从 state ![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)following policy开始![pi(s)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-94a6c8fce241848251dbd30ea24f9706_l3.svg)。

### 2.2. 使用动态规划解决示例

为了解决我们的 MDP，我们想要获得一个策略![pi](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed7678864de5d2f3ff6739ada3fd00e9_l3.svg)来告诉我们在哪个状态下应该采取哪个动作。动态规划包括许多不同的算法。我们选择价值迭代算法来解决我们的 MDP。[价值迭代](https://www.baeldung.com/cs/ml-value-iteration-vs-policy-iteration)采用我们的价值函数![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)，插入一个起始值，并迭代优化它。

至此，我们可以看出动态规划的一个重要特点，就是把一个问题分解成更小的部分，分别求解。也就是说，要找到我们MDP的完美解，首先要找到从平静到快乐，然后从沉睡到快乐的最佳策略。然后，我们可以结合解决方案来解决主要问题。

### 2.3. 动态规划用例

在捕鱼的背景下，人们面临着在保持种群规模的同时最大限度地增加捕捞鱼类数量的挑战。为此，我们的一系列行动包括我们捕捞了多少鲑鱼，我们的状态是不同的捕鱼季节。我们可以特别好地对此进行建模，因为我们知道鲑鱼的数量是如何减少和增长的。

另一个用例存在于在线营销领域。我们假设有一个包含 100000 人的电子邮件列表和一个广告选项列表，例如直接报价、产品介绍、重复出现的时事通讯等。现在我们想弄清楚是什么让客户从不感兴趣的状态转变为购买和满意的客户状态。由于我们有大量数据并且知道客户何时访问你的网站或购买商品，因此我们可以轻松创建马尔可夫决策问题。

动态规划的另一个优点是它确定性地工作，因此对于相同的环境总是产生相同的结果。因此它在金融领域非常受欢迎，因为它受到非常严格的监管。

## 3.Q学习

与动态规划相反，Q-Learning 不期望马尔可夫决策过程。它可以仅通过评估其哪些行为返回更高的奖励来工作。虽然这是一个巨大的优势，因为我们并不总是拥有完整的环境模型，但并不总是确定我们的模型会朝着特定方向收敛。

### 3.1. Q-学习示例

为了使这个想法更清楚，我们来看一下快乐宝贝的同一个例子。这次我们没有每个动作的概率。我们甚至不知道哪个动作会导致哪个状态：

![宝宝.Q_学习](https://www.baeldung.com/wp-content/uploads/sites/4/2021/11/Baby.Q_Learning.png)

这个模型可能更现实。在许多现实世界的案例中，我们没有行动结果的概率。

### 3.2. 用 Q-Learning 解决我们的例子

Q-Learning 的原理是获取未来的奖励，并将这些奖励的一部分添加到之前的步骤中。从而创建一条通向奖励的“路径”，在这条路径上我们只选择那些具有高预期奖励的动作。但是我们如何保存那些预期的回报并选择正确的路径呢？

为此，我们使用 Q 表。在 Q 表中，我们保存了关于我们的行为和状态的所有预期奖励。但首先，我们用随机值初始化它。为了进一步形象化，我们看一下示例的 Q 表：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-22ab7e7a8deb361dcba855785661ff77_l3.svg)

因为我们已经知道哭泣和快乐的预期奖励，所以我们不必随机初始化它们。现在我们评估表。由于唱歌在第一状态的价值最高，所以我们选择唱歌。

唱歌随机地把我们带到哭泣的状态。因此，我们将睡眠的预期奖励从 77 降低到 60。计算此预期奖励的公式为：

![{displaystyle Q^{new}(s_{t},a_{t})leftarrow Q(s_{t},a_{t}) + alpha cdot ( r_{t} + gamma cdot {最大 _{a}Q(s_{t+1},a)} -Q(s_{t},a_{t}))](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a1ac0fcc0d64f46b498a8869a949af3b_l3.svg)

![英石}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8cee8a32c6fe150661e17b00e0bba265_l3.svg)描述了我们目前所处的状态

![在}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a3c5c256f37669d23454496f48d95602_l3.svg)描述我们正在采取的行动

![α](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5f44d9bbc8046069be4aa2989bff19aa_l3.svg)描述学习率

![伽玛](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7b9abe136d2f0d53300727f373cfed43_l3.svg)折扣因素

![Q(s_{t+1},a)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dc7b9fd2b5f63ecce7fad60a3eb14526_l3.svg)下一个状态的预期奖励

这个公式描述了新的预期奖励是如何由我们的旧奖励和新值与各种参数(如学习率和折扣因子)结合而成的。学习率![α](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5f44d9bbc8046069be4aa2989bff19aa_l3.svg)描述了新值应该覆盖旧值的程度。折扣因子![伽玛](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7b9abe136d2f0d53300727f373cfed43_l3.svg)描述了远距离奖励对当前状态的影响程度。

哭泣状态是一种终结状态。这就是为什么我们通过重置我们的环境并再次从我们的睡眠开始状态开始继续前进。现在，当然，我们选择等待，因为等待的预期回报 71 高于唱歌 60。

我们在固定数量的剧集中继续这个学习过程。多少集取决于环境的上下文。由于我们的示例非常小，即使 200 集也足够了。对于其他更常见的示例，例如[山地车环境](https://gym.openai.com/envs/MountainCarContinuous-v0/)，我们需要 25000 集或更多的学习。

应用此方法后，我们获得了一个 q 表，它告诉我们在每个过程中做出哪个决定：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9b4ea3aaaee8917b9151f3b212248f72_l3.svg)

我们可以看到，选择在睡眠状态下等待和选择在平静状态下唱歌的回报最高。

### 3.3. 流程图

为了可视化这个学习过程，我们看一下下面的流程图：

![流程图_Q_学习](https://www.baeldung.com/wp-content/uploads/sites/4/2021/11/Flowchart_Q_Learning.drawio-1.png)

首先，用随机数初始化一个 Q-Table。![{displaystyle Q^{new}(s_{t}, a_{t})](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-775e2e014544f18477e79baf9bf21e20_l3.svg)然后我们开始我们的第一个学习情节，如上所述，在我们的 Q 函数的帮助下评估我们的预期奖励。

只要我们碰巧处于终止状态，我们就会开始下一个学习阶段，只要学习阶段还没有结束。如果学习阶段已经完成，我们会从 Q 表中检索我们的最优决策策略。我们只需选择每行中具有最高值的单元格即可。

### 3.4. Q-学习用例

在许多用例中，我们没有改变给定动作和状态的特定状态的概率，因此强化学习比动态规划产生更好的结果。此外，马尔可夫决策过程可能变得非常庞大，从而导致资源或时间挑战。

后者是 Q-Learning 成为在游戏中创建人工智能的绝佳工具的原因。最大的例子就是google的Alpha Go和OpenAI的OpenAI Give。他们都成功击败了以前没有算法达到的世界领先冠军。

另一个应用程序可以在机器人技术中找到，其中强化学习使机器人能够学习如何走路。同样，我们没有马尔可夫决策过程的概率，因为这个过程的物理过程极其复杂。

## 4.比较方法

动态规划基于给定的环境模型创建最优策略。与此相反的是 Q-Learning。它仅根据通过与环境交互获得的奖励来制定政策。

因此，要让 Q-Learning 算法发挥作用，我们只需要知道我们想要优化模型中的哪些参数。而要使 DP 算法起作用，你需要对环境有更深入的了解。对于这两个模型，我们都需要一组状态、动作和奖励。

## 5. 与其他机器学习和数据科学算法的比较

动态规划和 Q-Learning 都是强化学习算法。因此，它们被开发为在给定环境中最大化奖励。与此相反的是经典的机器学习方法，例如[SVM](https://www.baeldung.com/cs/ml-support-vector-machines)或[神经网络，](https://www.baeldung.com/cs/neural-net-advantages-disadvantages)它们具有一组给定的数据并根据它们得出总结。但也有两种可能的组合，例如[Deep Q-Learning](https://www.baeldung.com/cs/reinforcement-learning-neural-network)。

通过深度 Q 学习，我们将 Q 表与神经网络交换，因为 Q 表可以很快变得非常大。想象一下包含时间或位置的状态空间。这也导致我们的算法不再是确定性的。

## 六，总结

在本文中，我们讨论了动态规划和 Q-Learning 之间的区别。

他们都使用不同的环境模型来制定决策政策。一方面，我们看到动态规划是确定性的。它给了我们一个最优解。

这就是为什么它的强度取决于潜在的马尔可夫决策过程的强度。另一方面，强化学习通过与环境交互并选择具有高预期回报的动作来工作。