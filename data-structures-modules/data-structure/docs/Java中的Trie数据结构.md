## 1. 概述

数据结构是计算机编程中的一项重要资产，了解何时以及为何使用它们非常重要。

本文简要介绍了 trie(发音为“try”)数据结构及其实现和复杂性分析。

## 2.特里

[trie](https://www.baeldung.com/cs/tries-prefix-trees)是一种离散的数据结构，在典型的算法课程中并不为人所知或广泛提及，但仍然是一个重要的数据结构。

特里树(也称为数字树)，有时甚至是基数树或前缀树(因为它们可以通过前缀搜索)是一种有序的树结构，它利用了它存储的键——通常是字符串。

节点在树中的位置定义了与该节点关联的键，这使得尝试与二叉搜索树不同，在二叉搜索树中，节点存储仅对应于该节点的键。

一个节点的所有后代都有一个与该节点相关联的字符串的公共前缀，而根与一个空字符串相关联。

这里我们有一个TrieNode的预览，我们将在我们的Trie 实现中使用它：

```java
public class TrieNode {
    private HashMap<Character, TrieNode> children;
    private String content;
    private boolean isWord;
    
   // ...
}
```

可能存在 trie 是二叉搜索树的情况，但总的来说，这些是不同的。二叉搜索树和尝试都是树，但是二叉搜索树中的每个节点总是有两个孩子，而另一方面，尝试的节点可以有更多。

在 trie 中，每个节点(根节点除外)存储一个字符或一个数字。通过从根节点向下遍历 trie 到特定节点n，可以形成字符或数字的公共前缀，该前缀也可以由 trie 的其他分支共享。

通过从叶节点向上遍历 trie 到根节点，可以形成String或数字序列。

下面是Trie类，它代表了 trie 数据结构的一个实现：

```java
public class Trie {
    private TrieNode root;
    //...
}
```

## 三、常用操作

现在，让我们看看如何实现基本操作。

### 3.1. 插入元素

我们要描述的第一个操作是插入新节点。

在开始实施之前，了解算法很重要：

1.  将当前节点设置为根节点
2.  将当前字母设置为单词的第一个字母
3.  如果当前节点已经存在对当前字母的引用(通过“children”字段中的元素之一)，则将当前节点设置为该引用节点。否则，创建一个新节点，将字母设置为当前字母，并将当前节点初始化为这个新节点
4.  重复步骤3，直到遍历key

此操作的复杂度为O(n)，其中n表示密钥大小。

下面是这个算法的实现：

```java
public void insert(String word) {
    TrieNode current = root;

    for (char l: word.toCharArray()) {
        current = current.getChildren().computeIfAbsent(l, c -> new TrieNode());
    }
    current.setEndOfWord(true);
}
```

现在让我们看看如何使用此方法在 trie 中插入新元素：

```java
private Trie createExampleTrie() {
    Trie trie = new Trie();

    trie.insert("Programming");
    trie.insert("is");
    trie.insert("a");
    trie.insert("way");
    trie.insert("of");
    trie.insert("life");

    return trie;
}
```

我们可以通过以下测试测试 trie 是否已经填充了新节点：

```java
@Test
public void givenATrie_WhenAddingElements_ThenTrieNotEmpty() {
    Trie trie = createTrie();

    assertFalse(trie.isEmpty());
}
```

### 3.2. 查找元素

现在让我们添加一个方法来检查特定元素是否已经存在于 trie 中：

1.  获取根的孩子
2.  遍历字符串的每个字符
3.  检查该字符是否已经是子树的一部分。如果它不存在于 trie 中的任何地方，则停止搜索并返回false
4.  重复第二步和第三步，直到字符串中没有任何字符为止。如果到达字符串的末尾，则返回true

该算法的复杂度为O(n)，其中 n 表示密钥的长度。

Java 实现可能如下所示：

```java
public boolean find(String word) {
    TrieNode current = root;
    for (int i = 0; i < word.length(); i++) {
        char ch = word.charAt(i);
        TrieNode node = current.getChildren().get(ch);
        if (node == null) {
            return false;
        }
        current = node;
    }
    return current.isEndOfWord();
}
```

在行动中：

```java
@Test
public void givenATrie_WhenAddingElements_ThenTrieContainsThoseElements() {
    Trie trie = createExampleTrie();

    assertFalse(trie.containsNode("3"));
    assertFalse(trie.containsNode("vida"));
    assertTrue(trie.containsNode("life"));
}
```

### 3.3. 删除元素

除了插入和查找元素外，显然我们还需要能够删除元素。

对于删除过程，我们需要按照以下步骤进行：

1.  检查这个元素是否已经是 trie 的一部分
2.  如果找到该元素，则将其从 trie 中删除

该算法的复杂度为O(n)，其中 n 表示密钥的长度。

让我们快速看一下实现：

```java
public void delete(String word) {
    delete(root, word, 0);
}

private boolean delete(TrieNode current, String word, int index) {
    if (index == word.length()) {
        if (!current.isEndOfWord()) {
            return false;
        }
        current.setEndOfWord(false);
        return current.getChildren().isEmpty();
    }
    char ch = word.charAt(index);
    TrieNode node = current.getChildren().get(ch);
    if (node == null) {
        return false;
    }
    boolean shouldDeleteCurrentNode = delete(node, word, index + 1) && !node.isEndOfWord();

    if (shouldDeleteCurrentNode) {
        current.getChildren().remove(ch);
        return current.getChildren().isEmpty();
    }
    return false;
}
```

在行动中：

```java
@Test
void whenDeletingElements_ThenTreeDoesNotContainThoseElements() {
    Trie trie = createTrie();

    assertTrue(trie.containsNode("Programming"));
 
    trie.delete("Programming");
    assertFalse(trie.containsNode("Programming"));
}
```

## 4. 总结

在本文中，我们简要介绍了 trie 数据结构及其最常见的操作及其实现。