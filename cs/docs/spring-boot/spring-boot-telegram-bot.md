## 1. 简介

在本教程中，我们将使用Spring Boot创建一个 Telegram 机器人。

Telegram 机器人是一种在 Telegram 消息平台内运行的自动化程序。它利用[Telegram Bot API](https://core.telegram.org/bots/api)与用户交互并执行各种任务。我们将使用[Java 库，](https://github.com/rubenlagus/TelegramBots)而不是直接与 API 交互。机器人帮助我们响应用户命令、提供信息并执行自动化操作。

我们将从设置一个全新的机器人开始，然后继续描述如何使用Java库来实现简单的操作。

## 2. 创建 Telegram 机器人

首先，我们需要在 Telegram 平台上创建一个新的机器人。我们直接使用 Telegram 消息应用程序并在搜索栏中搜索[BotFather来完成此操作。](https://t.me/botfather)打开后，我们将键入/newbot命令来创建机器人并按照 BotFather 的说明进行操作。它会询问我们要分配给机器人的用户名，根据 Telegram 的政策，该用户名需要以机器人结尾：

[![img](https://www.baeldung.com/wp-content/uploads/2023/08/Screenshot-2023-05-28-at-20.18.58-1-e1692534899760.png)](https://www.baeldung.com/wp-content/uploads/2023/08/Screenshot-2023-05-28-at-20.18.58-1.png)

上面，BotFather 生成了一个令牌，我们必须将其保存在安全的地方，以便稍后用于配置我们的应用程序。

## 3. 设置应用程序

其次，我们必须有一个要集成 Telegram Bot 的[Spring Boot 项目。](https://www.baeldung.com/spring-boot-start)我们将修改pom.xml文件并包含[telegrambots-spring-boot-starter](https://mvnrepository.com/artifact/org.telegram/telegrambots-spring-boot-starter/6.7.0)和[telegrambots-powered](https://mvnrepository.com/artifact/org.telegram/telegrambots-abilities/6.7.0)库：

```xml
<dependency>
    <groupId>org.telegram</groupId>
    <artifactId>telegrambots-spring-boot-starter</artifactId>
    <version>6.7.0</version>
</dependency>
<dependency>
    <groupId>org.telegram</groupId>
    <artifactId>telegrambots-abilities</artifactId>
    <version>6.7.0</version>
</dependency>
```

在底层，AbilityBot 使用[webhook](https://www.baeldung.com/cs/webhooks-polling-real-time-information)与 Telegrams API 进行通信，但我们不需要担心这一点。事实上，该库实现了[Telegram Bot API](https://core.telegram.org/bots/api)提供的所有接口。

现在，我们可以实现我们的机器人了。

## 4. 解释 PizzaBot

我们将实现一个模拟披萨店的简单机器人，以演示如何使用Spring Boot库。此外，我们将与机器人进行一组预定义的交互：

[![Pizzabot交互图](https://www.baeldung.com/wp-content/uploads/2023/08/pizzabot-1024x646.png)](https://www.baeldung.com/wp-content/uploads/2023/08/pizzabot.png)

简而言之，我们首先询问用户的姓名。然后，我们会提示他选择披萨或饮料。如果是饮料，我们会显示一条消息，说明我们不出售饮料。否则，我们会向他们询问披萨的配料。选择可用的配料后，我们将确认用户是否想再次订购。在这种情况下，我们将重复该流程。或者，我们会感谢他们并通过结束消息结束聊天。

## 5. 配置并注册 PizzaBot

让我们首先为我们的新 PizzaShop 配置SkillBot：

```java
@Component
public class PizzaBot extends AbilityBot {
    private final ResponseHandler responseHandler;
    @Autowired
    public PizzaBot(Environment env) {
        super(env.getProperty("botToken"), "baeldungbot");
        responseHandler = new ResponseHandler(silent, db);
    }
    @Override
    public long creatorId() {
        return 1L;
    }
}
```

我们在构造函数中读取作为[环境变量注入的](https://www.baeldung.com/properties-with-spring#usage)botToken属性。我们必须保证令牌的安全，不要将其推入代码库。在此示例中，我们在运行应用程序之前将其导出到我们的环境中。或者，我们可以在属性文件中定义它。此外，我们必须提供一个唯一的 creatorId来描述我们的机器人。

此外，我们还扩展了SkillBot类，它减少了样板代码并通过ReplyFlow提供状态机等常用工具。但是，我们将仅使用嵌入式数据库并在ResponseHandler内显式管理状态：

```java
public class ResponseHandler {
    private final SilentSender sender;
    private final Map<Long, UserState> chatStates;

    public ResponseHandler(SilentSender sender, DBContext db) {
        this.sender = sender;
        chatStates = db.getMap(Constants.CHAT_STATES);
    }

}
```

### 5.1.Spring Boot 3兼容性问题

使用Spring Boot版本 3 时，当声明为@Component时，库不会自动配置机器人。因此，我们必须在主应用程序类中手动初始化它：

```java
TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
botsApi.registerBot(ctx.getBean("pizzaBot", TelegramLongPollingBot.class));
```

我们创建TelegramBotsApi的新实例，然后从[Spring 应用程序上下文提供](https://www.baeldung.com/spring-application-context)PizzaBot组件实例。

## 6. 实现 PizzaBot

Telegram API 非常庞大，并且可能会重复执行新命令。因此，我们使用能力来简化开发新能力的过程。在设计交互流程时，我们应该考虑最终用户、必要条件和执行流程。

在我们的 PizzaBot 中，我们将仅使用一个功能来启动与机器人的对话：

```java
public Ability startBot() {
    return Ability
      .builder()
      .name("start")
      .info(Constants.START_DESCRIPTION)
      .locality(USER)
      .privacy(PUBLIC)
      .action(ctx -> responseHandler.replyToStart(ctx.chatId()))
      .build();
}
```

我们使用[构建器模式](https://www.baeldung.com/creational-design-patterns#builder)来构建能力。首先，我们定义技能的名称，该名称与技能的命令相同。其次，我们提供这个新能力的描述字符串。当我们运行/commands命令来获取机器人的能力时，这将很有帮助。第三，我们定义机器人的位置和隐私。最后，我们定义收到命令后必须采取的操作。在此示例中，我们将聊天的 id 转发到ResponseHandler类。按照上图的设计，我们将询问用户的姓名并将其以其初始状态保存在地图中：

```java
public void replyToStart(long chatId) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId);
    message.setText(START_TEXT);
    sender.execute(message);
    chatStates.put(chatId, AWAITING_NAME);
}
```

在此方法中，我们创建一个SendMessage命令并使用sender执行它。然后，我们将聊天状态设置为AWAITING_NAME，表示我们正在等待用户的名字：

```java
private void replyToName(long chatId, Message message) {
    promptWithKeyboardForState(chatId, "Hello " + message.getText() + ". What would you like to have?",
      KeyboardFactory.getPizzaOrDrinkKeyboard(),
      UserState.FOOD_DRINK_SELECTION);
}
```

用户输入姓名后，我们向他们发送一个ReplyKeyboardMarkup，提示他们两个选项：

```java
public static ReplyKeyboard getPizzaToppingsKeyboard() {
    KeyboardRow row = new KeyboardRow();
    row.add("Margherita");
    row.add("Pepperoni");
    return new ReplyKeyboardMarkup(List.of(row));
}
```

这将隐藏键盘并向用户显示带有两个按钮的界面：

[![PizzaBot 按钮菜单](https://www.baeldung.com/wp-content/uploads/2023/08/Screenshot-2023-08-14-at-23.49.08.png)](https://www.baeldung.com/wp-content/uploads/2023/08/Screenshot-2023-08-14-at-23.49.08.png)现在，用户可以选择我们的披萨店不提供的披萨或饮料。当选择两个选项中的任何一个时，Telegram 会发送一条带有响应的短信。

对于上图中的所有绿色菱形元素，我们遵循类似的过程。因此，我们这里不再重复。相反，让我们专注于处理对按钮的响应。

## 7. 处理用户回复

对于所有传入消息和聊天的当前状态，我们在PizzaBot类中以不同的方式处理响应：

```coffeescript
public Reply replyToButtons() {
    BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> responseHandler.replyToButtons(getChatId(upd), upd.getMessage());
    return Reply.of(action, Flag.TEXT,upd -> responseHandler.userIsActive(getChatId(upd)));
}
```

这 。replyToButtons() 获取所有文本回复，并将它们与chatId和传入的Message对象一起转发到ResponseHandler。然后，在ResponseHandler内部， .replyToButtons ()方法决定如何处理消息：

```java
public void replyToButtons(long chatId, Message message) {
    if (message.getText().equalsIgnoreCase("/stop")) {
        stopChat(chatId);
    }

    switch (chatStates.get(chatId)) {
        case AWAITING_NAME -> replyToName(chatId, message);
        case FOOD_DRINK_SELECTION -> replyToFoodDrinkSelection(chatId, message);
        case PIZZA_TOPPINGS -> replyToPizzaToppings(chatId, message);
        case AWAITING_CONFIRMATION -> replyToOrder(chatId, message);
        default -> unexpectedMessage(chatId);
    }
}
```

在交换机内，我们检查聊天的当前状态并相应地回复用户。例如，当用户的当前状态是FOOD_DRINK_SELECTION时，我们会处理响应并在用户点击选项Pizza时转到下一个状态：

```java
private void replyToFoodDrinkSelection(long chatId, Message message) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    if ("drink".equalsIgnoreCase(message.getText())) {
        sendMessage.setText("We don't sell drinks.\nBring your own drink!! :)");
        sendMessage.setReplyMarkup(KeyboardFactory.getPizzaOrDrinkKeyboard());
        sender.execute(sendMessage);
    } else if ("pizza".equalsIgnoreCase(message.getText())) {
        sendMessage.setText("We love Pizza in here.\nSelect the toppings!");
        sendMessage.setReplyMarkup(KeyboardFactory.getPizzaToppingsKeyboard());
        sender.execute(sendMessage);
        chatStates.put(chatId, UserState.PIZZA_TOPPINGS);
    } else {
        sendMessage.setText("We don't sell " + message.getText() + ". Please select from the options below.");
        sendMessage.setReplyMarkup(KeyboardFactory.getPizzaOrDrinkKeyboard());
        sender.execute(sendMessage);
    }
}

```

另外，里面。replyToButtons() 中，我们立即检查用户是否发送了/stop命令。在这种情况下，我们停止聊天并从chatStates映射中删除chatId：

```java
private void stopChat(long chatId) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setText("Thank you for your order. See you soon!\nPress /start to order again");
    chatStates.remove(chatId);
    sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
    sender.execute(sendMessage);
}
```

这将从数据库中删除用户的状态。要再次交互，用户必须编写/start命令。

## 八、总结

在本教程中，我们讨论了使用Spring Boot实现 Telegram 机器人。

首先，我们使用 BotFather 在 Telegram 平台上创建了一个新的机器人。其次，我们设置了Spring Boot项目并解释了 PizzaBot 的功能以及它如何与用户交互。然后，我们使用简化新命令开发的功能来实现 PizzaBot。最后，我们处理了用户的回复，并根据聊天状态提供了适当的响应。