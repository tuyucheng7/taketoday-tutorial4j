## 1. 概述

在本教程中，我们将展示如何按字母顺序对String进行排序。

我们想要这样做的原因可能有很多——其中之一可能是验证两个单词是否由相同的字符集组成。这样，我们将验证它们是否是字谜。

## 2. 对字符串进行排序

在内部，String使用字符数组进行操作。因此，我们可以利用 toCharArray() : char[]方法，对数组进行排序，并 根据结果创建一个新的String ：

```java
@Test
void givenString_whenSort_thenSorted() {
    String abcd = "bdca";
    char[] chars = abcd.toCharArray();

    Arrays.sort(chars);
    String sorted = new String(chars);

    assertThat(sorted).isEqualTo("abcd");
}
```

在Java8 中，我们可以利用Stream API 为我们对String进行排序：

```java
@Test
void givenString_whenSortJava8_thenSorted() {
    String sorted = "bdca".chars()
      .sorted()
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString();

    assertThat(sorted).isEqualTo("abcd");
}
```

在这里，我们使用与第一个示例相同的算法，但使用Stream sorted()方法对 char 数组进行排序。

请注意，字符按其 ASCII 代码排序，因此大写字母将始终出现在开头。所以，如果我们想对“abC”进行排序，排序结果将是“Cab”。

要解决它，我们需要使用toLowerCase()方法转换字符串 。我们将在 Anagram 验证器示例中执行此操作。

## 3. 测试

为了测试排序，我们将构建 anagram 验证器。如前所述，当两个不同的单词或句子由同一组字符组合而成时，就会出现字谜。

让我们看看我们的 AnagramValidator类：

```java
public class AnagramValidator {

    public static boolean isValid(String text, String anagram) {
        text = prepare(text);
        anagram = prepare(anagram);

        String sortedText = sort(text);
        String sortedAnagram = sort(anagram);

        return sortedText.equals(sortedAnagram);
    }

    private static String sort(String text) {
        char[] chars = prepare(text).toCharArray();

        Arrays.sort(chars);
        return new String(chars);
    }

    private static String prepare(String text) {
        return text.toLowerCase()
          .trim()
          .replaceAll("s+", "");
    }
}
```

现在，我们将使用我们的排序方法并验证字谜是否有效：

```java
@Test
void givenValidAnagrams_whenSorted_thenEqual() {
    boolean isValidAnagram = AnagramValidator.isValid("Avida Dollars", "Salvador Dali");
        
    assertTrue(isValidAnagram);
}
```

## 4. 总结

在这篇简短的文章中，我们展示了如何以两种方式按字母顺序对字符串进行排序。此外，我们还实现了使用字符串排序方法的 Anagram 验证器。