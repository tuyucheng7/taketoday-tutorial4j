## 1. 问题描述

给定一个表示树的数组，数组索引是树节点中的值，数组值给出该特定索引(或节点)的父节点。
根节点索引的值将始终为-1，因为根节点没有父节点。从这个给定的表示构造给定二叉树的标准链接表示。

## 2. 表示方式

树可以用以下两种方式表示：

1. 动态节点表示(链接表示)。
2. 数组表示(顺序表示)。

现在，我们将讨论树的顺序表示。为了使用数组来表示树，节点的编号可以从0~(n-1)或1~n开始，如下所示：

```
       A(0)    
     /   
    B(1)  C(2)  
  /         
 D(3)  E(4)   F(6) 
OR,
      A(1)    
     /   
    B(2)  C(3)  
  /         
 D(4)  E(5)   F(7)  
```

## 3. 具体实现

注意：father、left_son和right_son是数组的索引值。

情况1：(0~n-1)

```
if (假设) father = p;
then left_son = (2  p) + 1;
and right_son = (2  p) + 2;
```

情况2：(1~n)

```
if (假设) father = p;
then left_son = (2  p);
then left_son = (2  p) + 1;
```

以下为情况1的具体实现：

```java
public class ArrayBinaryTree {
  static int root = 0;
  String[] tree = new String[10];

  public void root(String key) {
    tree[0] = key;
  }

  public void setLeft(String key, int root) {
    int t = root  2 + 1;
    if (tree[root] == null)
      System.out.printf("Can't set child at %d, no parent foundn", t);
    else
      tree[t] = key;
  }

  public void setRight(String key, int root) {
    int t = root  2 + 2;
    if (tree[root] == null)
      System.out.printf("Can't set child at %d, no parent foundn", t);
    else
      tree[t] = key;
  }

  public void printTree() {
    for (int i = 0; i < 10; i++) {
      if (tree[i] != null)
        System.out.print(tree[i]);
      else
        System.out.print("-");
    }
  }
}
```