## 1. 概述

数据结构是计算机编程中的一个重要分支，知道何时以及为什么使用它们非常重要。

本文简要介绍trie(发音为“try”)数据结构、实现和复杂度分析。

## 2. Trie

trie是一种离散的数据结构，在典型的算法课程中不太广为人知或被广泛提及，但仍然是一个重要的结构。

trie(也称为数字树)，有时甚至是基数树或前缀树(因为它们可以通过前缀搜索)，是一种有序的树结构，它利用了它存储的键-通常是字符串。

节点在树中的位置定义了与该节点相关联的键，这使得尝试与二叉搜索树相比有所不同，在二叉搜索树中，节点存储仅对应于该节点的键。

一个节点的所有后代都有一个与该节点关联的字符串的公共前缀，而根与一个空字符串相关联。

这里我们有一个 TrieNode 的预览，我们将在我们的Trie实现中使用它：

```java
public class TrieNode {
  private HashMap<Character, TrieNode> children;
  private String content;
  private boolean isWord;
  // ...
}
```

可能存在trie是二叉搜索树的情况，但总的来说，这些情况是不同的。二叉搜索树和trie树都是树，但是二叉搜索树中的每个节点总是有两个子节点，而tries的节点可以有更多子节点。

在trie中，每个节点(根节点除外)都存储一个字符或一个数字。通过将trie从根节点向下遍历到特定节点n，可以形成字符或数字的公共前缀，该前缀也由trie的其他分支共享。

通过将trie从叶节点向上遍历到根节点，可以形成字符串或数字序列。

以下是Trie类，它表示Trie数据结构的实现：

```java
public class Trie {
  private TrieNode root;
  //...
}
```

## 3. 常见操作

现在，让我们看看如何实现基本操作。

### 3.1 插入元素

我们将介绍的第一个操作是插入新节点。

在我们开始实现之前，了解算法很重要：

1. 将当前节点设置为根节点
2. 将当前字母设置为单词的第一个字母
3. 如果当前节点已经存在对当前字母的引用(通过“children”字段中的一个元素)，则将当前节点设置为该引用节点。
   否则，创建一个新节点，将字母设置为当前字母，并将当前节点初始化为这个新节点
4. 重复第3步，直到遍历key

此操作的复杂度为O(n)，其中n表示key的大小。

以下是此算法的实现：

```
void insert(String word) {
  TrieNode current = root;
  for (char l : word.toCharArray()) {
    current = current.getChildren().computeIfAbsent(l, c -> new TrieNode());
  }
  current.setEndOfWord(true);
}
```

现在，让我们看看如何使用此方法在trie中插入新元素：

```
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

我们可以测试trie是否已经插入了新节点：

```
@Test
void whenEmptyTrie_thenNoElements() {
  Trie trie = new Trie();
  assertFalse(trie.isEmpty());
}
```

### 3.2 查找元素

现在，让我们添加一个方法来检查特定元素是否已经存在于trie中：

1. 获取根的children
2. 遍历字符串的每个字符
3. 检查该字符是否已经是子trie的一部分。如果它不存在于trie中的任何位置，则停止搜索并返回false
4. 重复第二步和第三步，直到字符串中没有任何字符。如果到达字符串的结尾，则返回true

该算法的复杂度为O(n)，其中n表示key的长度。

具体实现如下所示：

```
boolean containsNode(String word) {
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

测试：

```
@Test
public void givenATrie_whenAddingElements_thenTrieHasThoseElements() {
  Trie trie = createExampleTrie();
  assertFalse(trie.containsNode("3"));
  assertFalse(trie.containsNode("vida"));
  assertTrue(trie.containsNode("Programming"));
  assertTrue(trie.containsNode("is"));
  assertTrue(trie.containsNode("a"));
  assertTrue(trie.containsNode("way"));
  assertTrue(trie.containsNode("of"));
  assertTrue(trie.containsNode("life"));
}
```

### 3.3 删除元素

除了插入和查找元素之外，很明显我们还需要能够删除元素。

对于删除过程，我们需要遵循以下步骤：

1. 检查此元素是否已经是trie的一部分
2. 如果找到该元素，则将其从trie中删除

该算法的复杂度为O(n)，其中n表示key的长度。

让我们快速了解一下实现：

```
boolean delete(String word) {
  return delete(root, word, 0);
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

测试：

```
@Test
void givenATrie_whenDeletingElements_thenTreeDoesNotContainThoseElements() {
  Trie trie = createExampleTrie();
  assertTrue(trie.containsNode("Programming"));
  trie.delete("Programming");
  assertFalse(trie.containsNode("Programming"));
}
```