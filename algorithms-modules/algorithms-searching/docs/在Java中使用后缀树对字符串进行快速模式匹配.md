## 1. 概述

在本教程中，我们将探讨字符串模式匹配的概念以及如何使其更快。然后，我们将介绍它在Java中的实现。

## 2. 字符串的模式匹配

### 2.1. 定义

在字符串中，模式匹配是在称为文本的字符序列中检查称为模式的给定字符序列的过程。

当模式不是正则表达式时，模式匹配的基本期望是：

-   匹配应该是精确的——而不是部分的
-   结果应包含所有匹配项——而不仅仅是第一个匹配项
-   结果应包含每个匹配项在文本中的位置

### 2.2. 寻找模式

让我们用一个例子来理解一个简单的模式匹配问题：

```bash
Pattern:   NA
Text:      HAVANABANANA
Match1:    ----NA------
Match2:    --------NA--
Match3:    ----------NA
```

我们可以看到模式NA在文本中出现了三次。为了得到这个结果，我们可以考虑一次将模式向下滑动一个字符并检查是否匹配。

然而，这是一种时间复杂度为O(pt)的蛮力方法，其中p是模式的长度，t是文本的长度。

假设我们要搜索的模式不止一种。然后，时间复杂度也线性增加，因为每个模式都需要单独的迭代。

### 2.3. Trie 数据结构来存储模式

我们可以通过将模式存储在 trie 数据结构中来缩短搜索时间，该结构[以](https://www.baeldung.com/trie-java)快速检索项目而闻名。

我们知道 trie 数据结构以树状结构存储字符串的字符。因此，对于两个字符串{NA, NAB}，我们将得到一个有两条路径的树：

[![前缀2](https://www.baeldung.com/wp-content/uploads/2020/03/prefix2.png)](https://www.baeldung.com/wp-content/uploads/2020/03/prefix2.png)

创建一个 trie 使得可以在文本中滑动一组模式并在一次迭代中检查匹配项。

请注意，我们使用$字符来指示字符串的结尾。

### 2.4. 存储文本的后缀 Trie 数据结构

另一方面，后缀 trie是使用单个 string 的所有可能后缀构造的 trie 数据结构。

对于前面的例子HAVANABANANA，我们可以构造一个后缀树：

[![后缀trie2](https://www.baeldung.com/wp-content/uploads/2020/03/suffixtrie2.png)](https://www.baeldung.com/wp-content/uploads/2020/03/suffixtrie2.png)

为文本创建后缀尝试，通常作为预处理步骤的一部分完成。之后，可以通过找到与模式序列匹配的路径来快速搜索模式。

但是，众所周知，后缀特里树会占用大量空间，因为字符串的每个字符都存储在边中。

我们将在下一节中查看后缀 trie 的改进版本。

## 3.后缀树

后缀树只是一个压缩的后缀树。这意味着，通过连接边缘，我们可以存储一组字符，从而显着减少存储空间。

因此，我们可以为相同的文本HAVANABANANA创建一个后缀树：

[![后缀树2](https://www.baeldung.com/wp-content/uploads/2020/03/suffixtree2.png)](https://www.baeldung.com/wp-content/uploads/2020/03/suffixtree2.png)

从根到叶的每条路径都代表字符串HAVANABANANA的后缀。

后缀树还存储后缀在叶节点中的位置。例如，BANANA$是从第七位开始的后缀。因此，使用从零开始的编号，它的值为 6。同样，A->BANANA$是另一个从位置 5 开始的后缀，如上图所示。

因此，从长远来看，我们可以看到，当我们能够获得从根节点开始的路径，其边在位置上与给定模式完全匹配时，就会发生模式匹配。

如果路径在叶节点处结束，我们将获得后缀匹配。否则，我们只会得到一个子串匹配。例如，模式NA是HAVANABANA[NA]的后缀和HAVA[NA]BANANA的子字符串。

在下一节中，我们将了解如何在Java中实现此数据结构。

## 4. 数据结构

让我们创建一个后缀树数据结构。我们需要两个域类。

首先，我们需要一个类来表示树节点。它需要存储树的边及其子节点。另外，当它是叶节点时，需要存储后缀的位置值。

那么，让我们创建我们的Node类：

```java
public class Node {
    private String text;
    private List<Node> children;
    private int position;

    public Node(String word, int position) {
        this.text = word;
        this.position = position;
        this.children = new ArrayList<>();
    }

    // getters, setters, toString()
}
```

其次，我们需要一个类来表示树并存储根节点。它还需要存储生成后缀的全文。

因此，我们有一个SuffixTree类：

```java
public class SuffixTree {
    private static final String WORD_TERMINATION = "$";
    private static final int POSITION_UNDEFINED = -1;
    private Node root;
    private String fullText;

    public SuffixTree(String text) {
        root = new Node("", POSITION_UNDEFINED);
        fullText = text;
    }
}
```

## 5. 添加数据的辅助方法

在我们编写存储数据的核心逻辑之前，让我们添加一些辅助方法。这些将在以后证明是有用的。

让我们修改SuffixTree类，添加一些构建树所需的方法。

### 5.1. 添加子节点

首先，让我们有一个方法addChildNode来向任何给定的父节点添加一个新的子节点：

```java
private void addChildNode(Node parentNode, String text, int index) {
    parentNode.getChildren().add(new Node(text, index));
}
```

### 5.2. 寻找两个字符串的最长公共前缀

其次，我们将编写一个简单的实用方法getLongestCommonPrefix来查找两个字符串的最长公共前缀：

```java
private String getLongestCommonPrefix(String str1, String str2) {
    int compareLength = Math.min(str1.length(), str2.length());
    for (int i = 0; i < compareLength; i++) {
        if (str1.charAt(i) != str2.charAt(i)) {
            return str1.substring(0, i);
        }
    }
    return str1.substring(0, compareLength);
}
```

### 5.3. 拆分节点

第三，让我们有一个方法来从给定的 parent 中分割出一个子节点。在这个过程中，父节点的文本值会被截断，右截断的字符串成为子节点的文本值。此外，父节点的子节点将转移到子节点。

我们可以从下图中看到，ANA被拆分为A->NA。之后，新的后缀ABANANA$可以添加为A->BANANA$：

[![后缀树2分裂节点](https://www.baeldung.com/wp-content/uploads/2020/03/suffixtree2-splitnode.png)](https://www.baeldung.com/wp-content/uploads/2020/03/suffixtree2-splitnode.png)

简而言之，这是一个方便的方法，在插入新节点时会派上用场：

```java
private void splitNodeToParentAndChild(Node parentNode, String parentNewText, String childNewText) {
    Node childNode = new Node(childNewText, parentNode.getPosition());

    if (parentNode.getChildren().size() > 0) {
        while (parentNode.getChildren().size() > 0) {
            childNode.getChildren()
              .add(parentNode.getChildren().remove(0));
        }
    }

    parentNode.getChildren().add(childNode);
    parentNode.setText(parentNewText);
    parentNode.setPosition(POSITION_UNDEFINED);
}
```

## 6. 遍历辅助方法

现在让我们创建遍历树的逻辑。我们将使用此方法来构建树和搜索模式。

### 6.1. 部分匹配与完全匹配

首先，让我们通过考虑填充有几个后缀的树来理解部分匹配和完全匹配的概念：

[![后缀树 2 遍历 1](https://www.baeldung.com/wp-content/uploads/2020/03/suffixtree2-traverse1.png)](https://www.baeldung.com/wp-content/uploads/2020/03/suffixtree2-traverse1.png)

要添加新的后缀ANABANANA$，我们检查是否存在可以修改或扩展以适应新值的任何节点。为此，我们将新文本与所有节点进行比较，发现现有节点[A]VANABANANA$在第一个字符处匹配。所以，这就是我们需要修改的节点，这种匹配可以称为部分匹配。

另一方面，假设我们正在同一棵树上搜索模式VANE 。我们知道它在前三个字符上与[VAN]ABANANA$部分匹配。如果所有四个字符都匹配，我们可以称之为完全匹配。对于模式搜索，完全匹配是必要的。

所以总而言之，我们将在构建树时使用部分匹配，在搜索模式时使用完全匹配。我们将使用标志isAllowPartialMatch来指示每种情况下我们需要的匹配类型。

### 6.2. 遍历树

现在，让我们编写逻辑来遍历树，只要我们能够在位置上匹配给定的模式：

```java
List<Node> getAllNodesInTraversePath(String pattern, Node startNode, boolean isAllowPartialMatch) {
    // ...
}
```

我们将递归调用它并返回我们在路径中找到的所有节点的列表。

我们首先将模式文本的第一个字符与节点文本进行比较：

```java
if (pattern.charAt(0) == nodeText.charAt(0)) {
    // logic to handle remaining characters       
}

```

对于部分匹配，如果模式的长度短于或等于节点文本，我们将当前节点添加到我们的节点列表并在此处停止：

```java
if (isAllowPartialMatch && pattern.length() <= nodeText.length()) {
    nodes.add(currentNode);
    return nodes;
}

```

然后我们将这个节点文本的剩余字符与模式的字符进行比较。如果模式与节点文本的位置不匹配，我们就到此为止。当前节点仅包含在部分匹配的节点列表中：

```java
int compareLength = Math.min(nodeText.length(), pattern.length());
for (int j = 1; j < compareLength; j++) {
    if (pattern.charAt(j) != nodeText.charAt(j)) {
        if (isAllowPartialMatch) {
            nodes.add(currentNode);
        }
        return nodes;
    }
}

```

如果模式与节点文本匹配，我们将当前节点添加到我们的节点列表中：

```java
nodes.add(currentNode);
```

但是如果模式的字符数多于节点文本，我们需要检查子节点。为此，我们进行递归调用，将currentNode作为起始节点，并将模式的剩余部分作为新模式。如果此调用返回的节点列表不为空，则它会附加到我们的节点列表中。如果在完全匹配的情况下它为空，则表示存在不匹配，因此为了表明这一点，我们添加了一个空项。我们返回节点：

```java
if (pattern.length() > compareLength) {
    List nodes2 = getAllNodesInTraversePath(pattern.substring(compareLength), currentNode, 
      isAllowPartialMatch);
    if (nodes2.size() > 0) {
        nodes.addAll(nodes2);
    } else if (!isAllowPartialMatch) {
        nodes.add(null);
    }
}
return nodes;
```

将所有这些放在一起，让我们创建getAllNodesInTraversePath：

```java
private List<Node> getAllNodesInTraversePath(String pattern, Node startNode, boolean isAllowPartialMatch) {
    List<Node> nodes = new ArrayList<>();
    for (int i = 0; i < startNode.getChildren().size(); i++) {
        Node currentNode = startNode.getChildren().get(i);
        String nodeText = currentNode.getText();
        if (pattern.charAt(0) == nodeText.charAt(0)) {
            if (isAllowPartialMatch && pattern.length() <= nodeText.length()) {
                nodes.add(currentNode);
                return nodes;
            }

            int compareLength = Math.min(nodeText.length(), pattern.length());
            for (int j = 1; j < compareLength; j++) {
                if (pattern.charAt(j) != nodeText.charAt(j)) {
                    if (isAllowPartialMatch) {
                        nodes.add(currentNode);
                    }
                    return nodes;
                }
            }

            nodes.add(currentNode);
            if (pattern.length() > compareLength) {
                List<Node> nodes2 = getAllNodesInTraversePath(pattern.substring(compareLength), 
                  currentNode, isAllowPartialMatch);
                if (nodes2.size() > 0) {
                    nodes.addAll(nodes2);
                } else if (!isAllowPartialMatch) {
                    nodes.add(null);
                }
            }
            return nodes;
        }
    }
    return nodes;
}
```

## 7. 算法

### 7.1. 存储数据

我们现在可以编写我们的逻辑来存储数据。让我们从在SuffixTree类上定义一个新方法addSuffix开始：

```java
private void addSuffix(String suffix, int position) {
    // ...
}
```

调用者将提供后缀的位置。

接下来，让我们编写处理后缀的逻辑。首先，我们需要至少通过调用我们的辅助方法getAllNodesInTraversePath并将isAllowPartialMatch设置为true来检查是否存在与后缀部分匹配的路径。如果不存在路径，我们可以将我们的后缀作为子项添加到根：

```java
List<Node> nodes = getAllNodesInTraversePath(pattern, root, true);
if (nodes.size() == 0) {
    addChildNode(root, suffix, position);
}
```

但是，如果存在路径，则意味着我们需要修改现有节点。该节点将是节点列表中的最后一个。我们还需要弄清楚这个现有节点的新文本应该是什么。如果节点列表只有一项，那么我们使用后缀. 否则，我们从后缀中排除直到最后一个节点的公共前缀以获取newText：

```java
Node lastNode = nodes.remove(nodes.size() - 1);
String newText = suffix;
if (nodes.size() > 0) {
    String existingSuffixUptoLastNode = nodes.stream()
        .map(a -> a.getText())
        .reduce("", String::concat);
    newText = newText.substring(existingSuffixUptoLastNode.length());
}
```

为了修改现有节点，让我们创建一个新方法extendNode，我们将从我们在addSuffix方法中停止的地方调用它。此方法有两个主要职责。一种是将现有节点分解为父节点和子节点，另一种是将子节点添加到新创建的父节点。我们分解父节点只是为了让它成为所有子节点的公共节点。所以，我们的新方法准备好了：

```java
private void extendNode(Node node, String newText, int position) {
    String currentText = node.getText();
    String commonPrefix = getLongestCommonPrefix(currentText, newText);

    if (commonPrefix != currentText) {
        String parentText = currentText.substring(0, commonPrefix.length());
        String childText = currentText.substring(commonPrefix.length());
        splitNodeToParentAndChild(node, parentText, childText);
    }

    String remainingText = newText.substring(commonPrefix.length());
    addChildNode(node, remainingText, position);
}
```

我们现在可以回到添加后缀的方法，该方法现在已具备所有逻辑：

```java
private void addSuffix(String suffix, int position) {
    List<Node> nodes = getAllNodesInTraversePath(suffix, root, true);
    if (nodes.size() == 0) {
        addChildNode(root, suffix, position);
    } else {
        Node lastNode = nodes.remove(nodes.size() - 1);
        String newText = suffix;
        if (nodes.size() > 0) {
            String existingSuffixUptoLastNode = nodes.stream()
                .map(a -> a.getText())
                .reduce("", String::concat);
            newText = newText.substring(existingSuffixUptoLastNode.length());
        }
        extendNode(lastNode, newText, position);
    }
}
```

最后，让我们修改我们的SuffixTree构造函数来生成后缀，并调用我们之前的方法addSuffix将它们迭代地添加到我们的数据结构中：

```java
public void SuffixTree(String text) {
    root = new Node("", POSITION_UNDEFINED);
    for (int i = 0; i < text.length(); i++) {
        addSuffix(text.substring(i) + WORD_TERMINATION, i);
    }
    fullText = text;
}
```

### 7.2. 搜索数据

定义了用于存储数据的后缀树结构后，我们现在可以编写执行搜索的逻辑。

我们首先在SuffixTree类上添加一个新方法searchText ，将要搜索的模式作为输入：

```java
public List<String> searchText(String pattern) {
    // ...
}
```

接下来，为了检查后缀树中是否存在模式，我们调用辅助方法getAllNodesInTraversePath并设置标志以仅进行完全匹配，这与在添加数据时允许部分匹配不同：

```java
List<Node> nodes = getAllNodesInTraversePath(pattern, root, false);
```

然后我们得到与我们的模式匹配的节点列表。列表中的最后一个节点表示模式完全匹配到的节点。因此，我们的下一步将是获取源自最后一个匹配节点的所有叶节点，并获取存储在这些叶节点中的位置。

让我们创建一个单独的方法getPositions来执行此操作。我们将检查给定节点是否存储了后缀的最后部分，以决定是否需要返回其位置值。而且，我们将为给定节点的每个子节点递归执行此操作：

```java
private List<Integer> getPositions(Node node) {
    List<Integer> positions = new ArrayList<>();
    if (node.getText().endsWith(WORD_TERMINATION)) {
        positions.add(node.getPosition());
    }
    for (int i = 0; i < node.getChildren().size(); i++) {
        positions.addAll(getPositions(node.getChildren().get(i)));
    }
    return positions;
}
```

一旦我们有了一组位置，下一步就是用它来标记我们存储在后缀树中的文本的模式。位置值表示后缀从哪里开始，模式的长度表示从起点偏移多少个字符。应用这个逻辑，让我们创建一个简单的实用方法：

```java
private String markPatternInText(Integer startPosition, String pattern) {
    String matchingTextLHS = fullText.substring(0, startPosition);
    String matchingText = fullText.substring(startPosition, startPosition + pattern.length());
    String matchingTextRHS = fullText.substring(startPosition + pattern.length());
    return matchingTextLHS + "[" + matchingText + "]" + matchingTextRHS;
}
```

现在，我们已经准备好了我们的支持方法。因此，我们可以将它们添加到我们的搜索方法中并完成逻辑：

```java
public List<String> searchText(String pattern) {
    List<String> result = new ArrayList<>();
    List<Node> nodes = getAllNodesInTraversePath(pattern, root, false);
    
    if (nodes.size() > 0) {
        Node lastNode = nodes.get(nodes.size() - 1);
        if (lastNode != null) {
            List<Integer> positions = getPositions(lastNode);
            positions = positions.stream()
              .sorted()
              .collect(Collectors.toList());
            positions.forEach(m -> result.add((markPatternInText(m, pattern))));
        }
    }
    return result;
}
```

## 8. 测试

现在我们已经有了我们的算法，让我们来测试它。

首先，让我们在SuffixTree中存储一个文本：

```java
SuffixTree suffixTree = new SuffixTree("havanabanana");

```

接下来，让我们搜索一个有效的模式a：

```java
List<String> matches = suffixTree.searchText("a");
matches.stream().forEach(m -> LOGGER.debug(m));
```

运行代码会按预期为我们提供六个匹配项：

```bash
h[a]vanabanana
hav[a]nabanana
havan[a]banana
havanab[a]nana
havanaban[a]na
havanabanan[a]
```

接下来，让我们搜索另一个有效模式nab：

```java
List<String> matches = suffixTree.searchText("nab");
matches.stream().forEach(m -> LOGGER.debug(m));

```

运行代码只给我们一个预期的匹配：

```bash
hava[nab]anana
```

最后，让我们搜索一个无效的模式nag：

```java
List<String> matches = suffixTree.searchText("nag");
matches.stream().forEach(m -> LOGGER.debug(m));
```

运行代码没有给我们任何结果。我们看到匹配必须是精确的而不是部分的。

因此，我们的模式搜索算法已经能够满足我们在本教程开头提出的所有期望。

## 9.时间复杂度

为给定长度为t的文本构建后缀树时，时间复杂度为O(t)。

然后，为了搜索长度为p 的模式， 时间复杂度为O(p)。回想一下，对于蛮力搜索，它是O(pt)。因此，在对文本进行预处理后，模式搜索变得更快。

## 10.总结

在这篇文章中，我们首先了解了三种数据结构的概念——trie、suffix trie和suffix tree。然后我们看到了如何使用后缀树来紧凑地存储后缀。

后来，我们看到了如何使用后缀树来存储数据和执行模式搜索。