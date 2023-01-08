## 1. 概述

在本教程中，我们将探讨如何使用[Jackson 库](https://www.baeldung.com/jackson)中基于推导的多态性特性。

## 2. 基于名称的多态性

假设我们有一个如下图所示的类结构。

[![人物图](https://www.baeldung.com/wp-content/uploads/2022/04/CharacterDiagram-1-300x208.png)](https://www.baeldung.com/wp-content/uploads/2022/04/CharacterDiagram-1.png)

首先，NamedCharacter和 ImperialSpy 类实现 了 Character 接口。其次，King 和 Knight 类正在实现 NamedCharacter类。最后，我们有一个 ControlledCharacter 类，它包含对玩家控制的角色的引用。

我们希望将 JSON 对象解析为Java对象，而不必修改接收到的 JSON 的结构。

那么让我们看一下类的定义。请注意，对于基本接口，我们必须使用 Jackson 注解来声明我们要使用的推导。此外，我们还必须添加@JsonSubTypes注解来声明我们要扣除哪些类。

```java
@JsonTypeInfo(use = Id.NAME)
@JsonSubTypes({ @Type(ImperialSpy.class), @Type(King.class), @Type(Knight.class) })
public interface Character {
}
```

此外，我们还可以在接口Character和 King和 Knight 类之间有一个中间类。因此，杰克逊，我们也将知道如何在这种情况下推导出多态性：

```java
public class NamedCharacter implements Character {
    private String name;

    // standard setters and getters
}
```

随后，我们将实现 Character接口的子类。我们已经在前面的代码示例中将这些子类声明为子类型。因此，该实现对 Jackson 库没有任何依赖性：

```java
public class ImperialSpy implements Character {
}
public class King extends NamedCharacter {
    private String land;

    // standard setters and getters
}
public class Knight extends NamedCharacter {
    private String weapon;

    // standard setters and getters
}
```

我们想要映射的 JSON 示例如下：

```json
{
    "name": "Old King Allant",
    "land": "Boletaria",
}
```

首先，如果我们尝试读取上面的 JSON 结构，Jackson 会抛出一个运行时异常，消息为Could not resolve subtype of [simple type, class deductionbasedpolymorphism.cn.tuyucheng.taketoday.jackson.Character]: missing type id property '@type' :

```java
@Test
void givenAKingWithoutType_whenMapping_thenExpectAnError() {
    String kingJson = formatJson("{'name': 'Old King Allant', 'land':'Boletaria'}");
    assertThrows(InvalidTypeIdException.class, () -> objectMapper.readValue(kingJson, Character.class));
}
```

此外， formatJson实用方法通过将引号字符转换为双引号来帮助我们保持测试中的代码简单，正如JSON 所要求的那样：

```java
public static String formatJson(String input) {
    return input.replaceAll("'", """);
}
```

因此，为了能够多态地推断出我们角色的类型，我们必须修改 JSON 结构并显式添加对象的类型。因此，我们必须将多态行为与我们的 JSON 结构相结合：

```json
{
    "@type": "King"
    "name": "Old King Allant",
    "land": "Boletaria",
}
@Test
void givenAKing_whenMapping_thenExpectAKingType() throws Exception {
    String kingJson = formatJson("{'name': 'Old King Allant', 'land':'Boletaria', '@type':'King'}");

    Character character = objectMapper.readValue(kingJson, Character.class);
    assertTrue(character instanceof King);
    assertSame(character.getClass(), King.class);
    King king = (King) character;
    assertEquals("Boletaria", king.getLand());
}
```

## 3. 基于推导的多态性

要激活基于推导的多态性，我们唯一要做的改变就是使用@JsonTypeInfo(use = Id.DEDUCTION)：

```java
@JsonTypeInfo(use = Id.DEDUCTION)
@JsonSubTypes({ @Type(ImperialSpy.class), @Type(King.class), @Type(Knight.class) })
public interface Character {
}
```

## 4. 简单推理

让我们探索如何通过简单的推理以多态方式读取 JSON。我们要读取的对象如下：

```json
{
    "name": "Ostrava, of Boletaria",
    "weapon": "Rune Sword",
}
```

首先，我们将读取Character对象中的值。然后，我们将测试 Jackson是否正确推导了 JSON 的类型：

```java
@Test
void givenAKnight_whenMapping_thenExpectAKnightType() throws Exception {
    String knightJson = formatJson("{'name':'Ostrava, of Boletaria', 'weapon':'Rune Sword'}");

    Character character = objectMapper.readValue(knightJson, Character.class);

    assertTrue(character instanceof Knight);
    assertSame(character.getClass(), Knight.class);
    Knight king = (Knight) character;
    assertEquals("Ostrava, of Boletaria", king.getName());
    assertEquals("Rune Sword", king.getWeapon());
}
```

此外，如果 JSON 是一个空对象，Jackson 会将其解释为 ImperialSpy，这是一个没有属性的类：

```java
@Test
void givenAnEmptyObject_whenMapping_thenExpectAnImperialSpy() throws Exception {
    String imperialSpyJson = "{}";

    Character character = objectMapper.readValue(imperialSpyJson, Character.class);

    assertTrue(character instanceof ImperialSpy);
}
```

此外，空 JSON 对象也将被 Jackson 扣除为空对象：

```java
@Test
void givenANullObject_whenMapping_thenExpectANullObject() throws Exception {
    Character character = objectMapper.readValue("null", Character.class);

    assertNull(character);
}
```

## 5.不区分大小写的推断

杰克逊也可以推导出多态性，即使是属性不匹配的情况。首先，我们将实例化一个启用了ACCEPT_CASE_INSENSITIVE_PROPERTIES 的对象映射器：

```java
ObjectMapper objectMapper = JsonMapper.builder().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).build();
```

然后，使用实例化的objectMapper， 我们可以测试是否正确推导了多态性：

```json
{
    "NaMe": "Ostrava, of Boletaria",
    "WeaPON": "Rune Sword",
}
@Test
void givenACaseInsensitiveKnight_whenMapping_thenExpectKnight() throws Exception {
    String knightJson = formatJson("{'NaMe':'Ostrava, of Boletaria', 'WeaPON':'Rune Sword'}");

    Character character = objectMapper.readValue(knightJson, Character.class);

    assertTrue(character instanceof Knight);
    assertSame(character.getClass(), Knight.class);
    Knight knight = (Knight) character;
    assertEquals("Ostrava, of Boletaria", knight.getName());
    assertEquals("Rune Sword", knight.getWeapon());
}
```

## 6.包含推理

我们还可以推导出包含在其他对象中的对象的多态性。我们将使用 ControlledCharacter类定义来演示以下 JSON 的映射：

```json
{
    "character": {
        "name": "Ostrava, of Boletaria",
        "weapon": "Rune Sword"
    }
}
@Test
void givenAKnightControlledCharacter_whenMapping_thenExpectAControlledCharacterWithKnight() throws Exception {
    String controlledCharacterJson = formatJson("{'character': {'name': 'Ostrava, of Boletaria', 'weapon': 'Rune Sword'}}");

    ControlledCharacter controlledCharacter = objectMapper.readValue(controlledCharacterJson, ControlledCharacter.class);
    Character character = controlledCharacter.getCharacter();

    assertTrue(character instanceof Knight);
    assertSame(character.getClass(), Knight.class);
    Knight knight = (Knight) character;
    assertEquals("Ostrava, of Boletaria", knight.getName());
    assertEquals("Rune Sword", knight.getWeapon());
}
```

## 七. 总结

在本教程中，我们探索了如何使用 Jackson 库使用基于推导的多态性。