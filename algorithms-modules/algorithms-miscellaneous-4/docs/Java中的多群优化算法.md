## 1. 简介

在本文中，我们将了解多群优化算法。与同类的其他算法一样，它的目的是通过最大化或最小化特定函数(称为适应度函数)来找到问题的最佳解决方案。

让我们从一些理论开始。

## 2. 多群优化如何工作

Multi-swarm 是[Swarm](https://en.wikipedia.org/wiki/Particle_swarm_optimization)算法的变体。顾名思义，Swarm 算法通过模拟一组对象在可能解空间中的运动来解决问题。在多群版本中，有多个群而不是只有一个。

群体的基本组成部分称为粒子。粒子由其实际位置定义，这也是我们问题的可能解决方案，以及用于计算下一个位置的速度。

粒子的速度不断变化，以一定的随机性向所有群体中的所有粒子中找到的最佳位置倾斜，以增加覆盖的空间量。

这最终导致大多数粒子到达一组有限的点，这些点是适应度函数中的局部最小值或最大值，具体取决于我们是试图最小化还是最大化它。

尽管找到的点始终是函数的局部最小值或最大值，但它不一定是全局点，因为无法保证算法已完全探索解空间。

出于这个原因，多群被称为[元启发式](https://www.baeldung.com/cs/nature-inspired-algorithms)——它找到的解决方案是最好的，但它们可能不是绝对最好的。

## 3.实施

现在我们知道了多群是什么以及它是如何工作的，让我们来看看如何实现它。

对于我们的示例，我们将尝试解决[StackExchange 上发布的这个现实生活中的优化问题](https://matheducators.stackexchange.com/questions/1550/optimization-problems-that-todays-students-might-actually-encounter/1561#1561)：

>   在英雄联盟中，玩家在防御物理伤害时的有效生命值由E=H(100+A)/100给出，其中H是生命值，A是护甲。
>
>   生命值每单位 2.5 金币，护甲值每单位 18 金币。你有 3600 金币，你需要优化你的生命值和护甲的有效性E，以便尽可能长时间地抵抗敌方队伍的攻击。你应该买多少？

### 3.1. 粒子

我们首先对我们的基本结构建模，一个粒子。粒子的状态包括它的当前位置，这是解决问题的一对健康和护甲值，粒子在两个轴上的速度和粒子适应度分数。

我们还将存储我们找到的最佳位置和适应度分数，因为我们需要它们来更新粒子速度：

```java
public class Particle {
    private long[] position;
    private long[] speed;
    private double fitness;
    private long[] bestPosition;	
    private double bestFitness = Double.NEGATIVE_INFINITY;

    // constructors and other methods
}
```

我们选择使用长数组来表示速度和位置，因为我们可以从问题陈述中推断出我们不能购买装甲或健康的分数，因此解决方案必须在整数域中。

我们不想使用int，因为这会在计算过程中导致溢出问题。

### 3.2. 一群

接下来，让我们将群体定义为粒子的集合。我们还将再次存储历史最佳位置和得分以供以后计算。

蜂群还需要通过为每个粒子分配一个随机的初始位置和速度来处理其粒子的初始化。

我们可以粗略估计解决方案的边界，因此我们将此限制添加到随机数生成器。

这将减少运行算法所需的计算能力和时间：

```java
public class Swarm {
    private Particle[] particles;
    private long[] bestPosition;
    private double bestFitness = Double.NEGATIVE_INFINITY;
    
    public Swarm(int numParticles) {
        particles = new Particle[numParticles];
        for (int i = 0; i < numParticles; i++) {
            long[] initialParticlePosition = { 
              random.nextInt(Constants.PARTICLE_UPPER_BOUND),
              random.nextInt(Constants.PARTICLE_UPPER_BOUND) 
            };
            long[] initialParticleSpeed = { 
              random.nextInt(Constants.PARTICLE_UPPER_BOUND),
              random.nextInt(Constants.PARTICLE_UPPER_BOUND) 
            };
            particles[i] = new Particle(
              initialParticlePosition, initialParticleSpeed);
        }
    }

    // methods omitted
}
```

### 3.3. 多群

最后，让我们通过创建一个 Multiswarm 类来结束我们的模型。

与群类似，我们将跟踪群的集合以及在所有群中发现的最佳粒子位置和适应度。

我们还将存储对适应度函数的引用以备后用：

```java
public class Multiswarm {
    private Swarm[] swarms;
    private long[] bestPosition;
    private double bestFitness = Double.NEGATIVE_INFINITY;
    private FitnessFunction fitnessFunction;

    public Multiswarm(
      int numSwarms, int particlesPerSwarm, FitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
        this.swarms = new Swarm[numSwarms];
        for (int i = 0; i < numSwarms; i++) {
            swarms[i] = new Swarm(particlesPerSwarm);
        }
    }

    // methods omitted
}
```

### 3.4. 健身功能

现在让我们实现适应度函数。

为了将算法逻辑与这个特定问题分离，我们将引入一个具有单一方法的接口。

此方法将粒子位置作为参数并返回一个值，表明它有多好：

```java
public interface FitnessFunction {
    public double getFitness(long[] particlePosition);
}
```

如果发现的结果根据问题约束是有效的，则测量适应度只是返回我们想要最大化的计算有效健康的问题。

对于我们的问题，我们有以下特定的验证约束：

-   解只能是正整数
-   解决方案必须在提供的黄金数量下可行

当其中一个约束被违反时，我们返回一个负数，表示我们距离有效性边界有多远。

这要么是前者的数量，要么是后者的不可用黄金数量：

```java
public class LolFitnessFunction implements FitnessFunction {

    @Override
    public double getFitness(long[] particlePosition) {
        long health = particlePosition[0];
        long armor = particlePosition[1];

        if (health < 0 && armor < 0) {
            return -(health  armor);
        } else if (health < 0) {
            return health;
        } else if (armor < 0) {
            return armor;
        }

        double cost = (health  2.5) + (armor  18);
        if (cost > 3600) {
            return 3600 - cost;
        } else {
            long fitness = (health  (100 + armor)) / 100;
            return fitness;
        }
    }
}
```

### 3.5. 主循环

主程序将在所有群体中的所有粒子之间迭代并执行以下操作：

-   计算粒子适应度
-   如果找到新的最佳位置，则更新粒子、群和多群历史
-   通过将当前速度添加到每个维度来计算新的粒子位置
-   计算新的粒子速度

目前，我们将通过创建专用方法将速度更新留给下一节：

```java
public void mainLoop() {
    for (Swarm swarm : swarms) {
        for (Particle particle : swarm.getParticles()) {
            long[] particleOldPosition = particle.getPosition().clone();
            particle.setFitness(fitnessFunction.getFitness(particleOldPosition));
       
            if (particle.getFitness() > particle.getBestFitness()) {
                particle.setBestFitness(particle.getFitness());				
                particle.setBestPosition(particleOldPosition);
                if (particle.getFitness() > swarm.getBestFitness()) {						
                    swarm.setBestFitness(particle.getFitness());
                    swarm.setBestPosition(particleOldPosition);
                    if (swarm.getBestFitness() > bestFitness) {
                        bestFitness = swarm.getBestFitness();
                        bestPosition = swarm.getBestPosition().clone();
                    }
                }
            }

            long[] position = particle.getPosition();
            long[] speed = particle.getSpeed();
            position[0] += speed[0];
            position[1] += speed[1];
            speed[0] = getNewParticleSpeedForIndex(particle, swarm, 0);
            speed[1] = getNewParticleSpeedForIndex(particle, swarm, 1);
        }
    }
}
```

### 3.6. 速度更新

粒子改变其速度至关重要，因为这是它设法探索不同可能解决方案的方式。

粒子的速度将需要使粒子移动到它自己、它的群体和所有群体找到的最佳位置，为每个群体分配一定的权重。我们将这些权重分别称为认知权重、社会权重和全局权重。

为了增加一些变化，我们将这些权重中的每一个乘以一个介于 0 和 1 之间的随机数。我们还将在公式中添加一个惯性因子，以激励粒子不要减速太多：

```java
private int getNewParticleSpeedForIndex(
  Particle particle, Swarm swarm, int index) {
 
    return (int) ((Constants.INERTIA_FACTOR  particle.getSpeed()[index])
      + (randomizePercentage(Constants.COGNITIVE_WEIGHT)
       (particle.getBestPosition()[index] - particle.getPosition()[index]))
      + (randomizePercentage(Constants.SOCIAL_WEIGHT) 
       (swarm.getBestPosition()[index] - particle.getPosition()[index]))
      + (randomizePercentage(Constants.GLOBAL_WEIGHT) 
       (bestPosition[index] - particle.getPosition()[index])));
}
```

惯性、认知、社会和全局权重的可接受值分别为 0.729、1.49445、1.49445 和 0.3645。

## 4. 总结

在本教程中，我们介绍了群体算法的理论和实现。我们还看到了如何根据特定问题设计适应度函数。

如果你想了解更多有关此主题的信息，请查看[这本书](https://books.google.it/books?id=Xl9uCQAAQBAJ)和[这篇文章](https://msdn.microsoft.com/en-us/magazine/dn385711.aspx)，它们也用作本文的信息来源。