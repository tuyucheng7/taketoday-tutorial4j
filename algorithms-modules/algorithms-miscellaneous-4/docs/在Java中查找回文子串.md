## 1. 概述

在本快速教程中，我们将通过不同的方法来查找给定字符串中的所有回文子字符串。我们还将注意每种方法的时间复杂度。

## 2.蛮力法

在这种方法中，我们将简单地遍历输入字符串以找到所有子字符串。同时，我们将检查子字符串是否为回文：

```java
public Set<String> findAllPalindromesUsingBruteForceApproach(String input) {
    Set<String> palindromes = new HashSet<>();
    for (int i = 0; i < input.length(); i++) {
        for (int j = i + 1; j <= input.length(); j++) {
            if (isPalindrome(input.substring(i, j))) {
                palindromes.add(input.substring(i, j));
            }
        }
    }
    return palindromes;
}
```

In the example above, we just compare the substring to its reverse to see if it's a palindrome:

```java
private boolean isPalindrome(String input) {
    StringBuilder plain = new StringBuilder(input);
    StringBuilder reverse = plain.reverse();
    return (reverse.toString()).equals(input);
}Copy
```

Of course, we can easily choose from [several other approaches](https://www.baeldung.com/java-palindrome).

The time complexity of this approach is O(n^3). While this may be acceptable for small input strings, we'll need a more efficient approach if we're checking for palindromes in large volumes of text.

## 3. Centralization Approach

The idea in the centralization approach is to consider each character as the pivot and expand in both directions to find palindromes.

We'll only expand if the characters on the left and right side match, qualifying the string to be a palindrome. Otherwise, we continue to the next character.

Let's see a quick demonstration wherein we'll consider each character as the center of a palindrome:

```java
public Set<String> findAllPalindromesUsingCenter(String input) {
    Set<String> palindromes = new HashSet<>();
    for (int i = 0; i < input.length(); i++) {
        palindromes.addAll(findPalindromes(input, i, i + 1));
        palindromes.addAll(findPalindromes(input, i, i));
    }
    return palindromes;
}Copy
```

Within the loop above, we expand in both directions to get the set of all palindromes centered at each position. We'll find both even and odd length palindromes by calling the method findPalindromes twice in the loop:

```java
private Set<String> findPalindromes(String input, int low, int high) {
    Set<String> result = new HashSet<>();
    while (low >= 0 && high < input.length() && input.charAt(low) == input.charAt(high)) {
        result.add(input.substring(low, high + 1));
        low--;
        high++;
    }
    return result;
}Copy
```

The time complexity of this approach is O(n^2). This is an improvement over our brute-force approach, but we can do even better, as we'll see in the next section.

## 4. Manacher's Algorithm

[Manacher’s algorithm](https://www.baeldung.com/cs/manachers-algorithm) finds the longest palindromic substring in linear time. We'll use this algorithm to find all substrings that are palindromes.

Before we dive into the algorithm, we'll initialize a few variables.

First, we'll guard the input string with a boundary character at the beginning and end before converting the resulting string to a character array:

```java
String formattedInput = "@" + input + "#";
char inputCharArr[] = formattedInput.toCharArray();Copy
```

Then, we'll use a two-dimensional array radius with two rows — one to store the lengths of odd-length palindromes, and the other to store lengths of even-length palindromes:

```java
int radius[][] = new int[2][input.length() + 1];Copy
```

Next, we'll iterate over the input array to find the length of the palindrome centered at position i and store this length in radius[][]:

```java
Set<String> palindromes = new HashSet<>();
int max;
for (int j = 0; j <= 1; j++) {
    radius[j][0] = max = 0;
    int i = 1;
    while (i <= input.length()) {
        palindromes.add(Character.toString(inputCharArr[i]));
        while (inputCharArr[i - max - 1] == inputCharArr[i + j + max])
            max++;
        radius[j][i] = max;
        int k = 1;
        while ((radius[j][i - k] != max - k) && (k < max)) {
            radius[j][i + k] = Math.min(radius[j][i - k], max - k);
            k++;
        }
        max = Math.max(max - k, 0);
        i += k;
    }
}Copy
```

Finally, we'll traverse through the array radius[][] to calculate the palindromic substrings centered at each position:

```java
for (int i = 1; i <= input.length(); i++) {
    for (int j = 0; j <= 1; j++) {
        for (max = radius[j][i]; max > 0; max--) {
            palindromes.add(input.substring(i - max - 1, max + j + i - 1));
        }
    }
}Copy
```

The time complexity of this approach is O(n).

## 5. Conclusion

在这篇简短的文章中，我们讨论了查找回文子串的不同方法的时间复杂度。