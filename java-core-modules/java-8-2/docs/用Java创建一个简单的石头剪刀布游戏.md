## 1. 概述

在这个简短的教程中，我们将了解如何在Java创建一个简单的“石头剪刀布”游戏。

## 2. 创建我们的“石头剪刀布”游戏

我们的游戏将允许玩家输入“rock”、“paper”或“scissors”作为每个动作的值。

首先，我们为这三种情况创建一个枚举：

```java
enum Move {
    ROCK("rock"),
    PAPER("paper"),
    SCISSORS("scissors");

    private String value;

    // ...
}
```

然后，让我们创建一个生成随机整数并返回计算机动作的方法：

```java
private static String getComputerMove() {
    Random random = new Random();
    int randomNumber = random.nextInt(3);
    String computerMove = Move.values()[randomNumber].getValue();
    System.out.println("Computer move: " + computerMove);
    return computerMove;
}
```

以及一种检查玩家是否获胜的方法：

```java
private static boolean isPlayerWin(String playerMove, String computerMove) {
	return playerMove.equals(Move.ROCK.value) && computerMove.equals(Move.SCISSORS.value)
	    || (playerMove.equals(Move.SCISSORS.value) && computerMove.equals(Move.PAPER.value))
	    || (playerMove.equals(Move.PAPER.value) && computerMove.equals(Move.ROCK.value));
}
```

最后，我们使用这些方法来组成一个完整的程序：

```java
public static void main(String[] args) {
	Scanner scanner = new Scanner(System.in);
	int wins = 0;
	int losses = 0;

	System.out.println("Welcome to Rock-Paper-Scissors! Please enter \"rock\", \"paper\", \"scissors\", or \"quit\" to exit.");

	while (true) {
		System.out.println("-------------------------");
		System.out.print("Enter your move: ");
		String playerMove = scanner.nextLine();

		if (playerMove.equals("quit")) {
			System.out.println("You won " + wins + " times and lost " + losses + " times.");
			System.out.println("Thanks for playing! See you again.");
			break;
		}

		if (Arrays.stream(Move.values()).noneMatch(x -> x.getValue().equals(playerMove))) {
			System.out.println("Your move isn't valid!");
			continue;
		}

		String computerMove = getComputerMove();

		if (playerMove.equals(computerMove)) {
			System.out.println("It's a tie!");
		} else if (isPlayerWin(playerMove, computerMove)) {
			System.out.println("You won!");
			wins++;
		} else {
			System.out.println("You lost!");
			losses++;
		}
	}
}
```

如上所示，我们使用Java的Scanner类来读取用户输入值。

现在我们可以运行该程序然后试着玩两局：

```shell
Welcome to Rock-Paper-Scissors! Please enter "rock", "paper", "scissors", or "quit" to exit.
-------------------------
Enter your move: rock
Computer move: scissors
You won!
-------------------------
Enter your move: paper
Computer move: paper
It's a tie!
-------------------------
Enter your move: quit
You won 1 times and lost 0 times.
Thanks for playing! See you again.
```

## 3. 总结

在这个快速教程中，我们学习了如何用Java创建一个简单的“石头剪刀布”游戏。