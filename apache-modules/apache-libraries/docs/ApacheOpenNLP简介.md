## 1. 概述

Apache OpenNLP 是一个开源的自然语言处理Java库。

它具有用于命名实体识别、句子检测、POS 标记和令牌化等用例的 API。

在本教程中，我们将了解如何将此 API 用于不同的用例。

## 2.Maven 设置

首先，我们需要将主要依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.opennlp</groupId>
    <artifactId>opennlp-tools</artifactId>
    <version>1.8.4</version>
</dependency>
```

最新的稳定版本可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|a%3A"opennlp-tools")上找到。

一些用例需要经过训练的模型。你可以在此处下载预定义模型，并[在](https://opennlp.apache.org/models.html)[此处](http://opennlp.sourceforge.net/models-1.5/)下载有关这些模型的详细信息。

## 3.句子检测

让我们从理解什么是句子开始。

句子检测是关于识别句子的开头和结尾，这通常取决于手头的语言。这也称为“句子边界消歧”(SBD)。 

在某些情况下，由于句号的歧义性，句子检测非常具有挑战性。句点通常表示句子的结尾，但也可以出现在电子邮件地址、缩写、小数点和许多其他地方。

对于大多数 NLP 任务，对于句子检测，我们需要一个经过训练的模型作为输入，我们希望它位于/resources文件夹中。

为了实现句子检测，我们加载模型并将其传递到 SentenceDetectorME的实例中。然后，我们只需将文本传递给sentDetect()方法，以在句子边界处将其拆分：

```java
@Test
public void givenEnglishModel_whenDetect_thenSentencesAreDetected() 
  throws Exception {

    String paragraph = "This is a statement. This is another statement." 
      + "Now is an abstract word for time, "
      + "that is always flying. And my email address is google@gmail.com.";

    InputStream is = getClass().getResourceAsStream("/models/en-sent.bin");
    SentenceModel model = new SentenceModel(is);

    SentenceDetectorME sdetector = new SentenceDetectorME(model);

    String sentences[] = sdetector.sentDetect(paragraph);
    assertThat(sentences).contains(
      "This is a statement.",
      "This is another statement.",
      "Now is an abstract word for time, that is always flying.",
      "And my email address is google@gmail.com.");
}
```

注意： 后缀“ME”用于 Apache OpenNLP 的许多类名中，代表一种基于“最大熵”的算法。

## 4.标记化

现在我们可以将文本语料库分成句子，我们可以开始更详细地分析句子。

标记化的目标是将一个句子分成更小的部分，称为标记。通常，这些标记是单词、数字或标点符号。

OpenNLP 中提供三种类型的分词器。

### 4.1. 使用TokenizerME

在这种情况下，我们首先需要加载模型。我们可以从[这里](http://opennlp.sourceforge.net/models-1.5/)下载模型文件，将其放入/resources文件夹并从那里加载。

接下来，我们将使用加载的模型创建一个TokenizerME实例 ，并使用tokenize()方法对任何字符串执行标记化：

```java
@Test
public void givenEnglishModel_whenTokenize_thenTokensAreDetected() 
  throws Exception {
 
    InputStream inputStream = getClass()
      .getResourceAsStream("/models/en-token.bin");
    TokenizerModel model = new TokenizerModel(inputStream);
    TokenizerME tokenizer = new TokenizerME(model);
    String[] tokens = tokenizer.tokenize("Baeldung is a Spring Resource.");
 
    assertThat(tokens).contains(
      "Baeldung", "is", "a", "Spring", "Resource", ".");
}
```

正如我们所见，标记器已将所有单词和句点字符识别为单独的标记。此分词器也可以与自定义训练模型一起使用。

### 4.2. WhitespaceTokenizer

顾名思义，这个分词器只是使用空白字符作为分隔符将句子拆分为分词：

```java
@Test
public void givenWhitespaceTokenizer_whenTokenize_thenTokensAreDetected() 
  throws Exception {
 
    WhitespaceTokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
    String[] tokens = tokenizer.tokenize("Baeldung is a Spring Resource.");
 
    assertThat(tokens)
      .contains("Baeldung", "is", "a", "Spring", "Resource.");
  }
```

我们可以看到句子被空格分开了，因此我们得到了“Resource”。(末尾带有句点字符)作为单个标记，而不是单词“资源”和句点字符的两个不同标记。

### 4.3. 简单分词器

这个 tokenizer 比WhitespaceTokenizer稍微复杂一点，它将句子分成单词、数字和标点符号。这是默认行为，不需要任何模型：

```java
@Test
public void givenSimpleTokenizer_whenTokenize_thenTokensAreDetected() 
  throws Exception {
 
    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    String[] tokens = tokenizer
      .tokenize("Baeldung is a Spring Resource.");
 
    assertThat(tokens)
      .contains("Baeldung", "is", "a", "Spring", "Resource", ".");
  }
```

## 5. 命名实体识别

现在我们已经了解了标记化，让我们看一下基于成功标记化的第一个用例：命名实体识别 (NER)。

NER 的目标是在给定的文本中找到命名实体，如人物、位置、组织和其他命名的事物。

OpenNLP 为人名、日期和时间、位置和组织使用预定义模型。我们需要使用TokenNameFinderModel加载模型并将 其传递到 NameFinderME 的实例中。然后我们可以使用find()方法在给定文本中查找命名实体：

```java
@Test
public void 
  givenEnglishPersonModel_whenNER_thenPersonsAreDetected() 
  throws Exception {

    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    String[] tokens = tokenizer
      .tokenize("John is 26 years old. His best friend's "  
        + "name is Leonard. He has a sister named Penny.");

    InputStream inputStreamNameFinder = getClass()
      .getResourceAsStream("/models/en-ner-person.bin");
    TokenNameFinderModel model = new TokenNameFinderModel(
      inputStreamNameFinder);
    NameFinderME nameFinderME = new NameFinderME(model);
    List<Span> spans = Arrays.asList(nameFinderME.find(tokens));

    assertThat(spans.toString())
      .isEqualTo("[[0..1) person, [13..14) person, [20..21) person]");
}
```

正如我们在断言中看到的那样，结果是一个Span对象列表，其中包含组成文本中命名实体的标记的开始和结束索引。

## 6. 词性标注

另一个需要标记列表作为输入的用例是词性标注。

词性 (POS) 标识单词的类型。OpenNLP 对不同的词性使用以下标签：

-   NN——名词，单数或质量
-   DT——限定词
-   VB –动词，基本形式
-   VBD——动词，过去时
-   VBZ –动词，第三人称单数现在时
-   IN –介词或从属连词
-   NNP——专有名词，单数
-   TO—— “to”这个词
-   JJ——形容词

这些标签与 Penn Tree Bank 中定义的标签相同。如需完整列表，请参阅 [此列表](https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html)。

与 NER 示例类似，我们加载适当的模型，然后 在一组标记上使用POSTaggerME 及其方法tag()来标记句子：

```java
@Test
public void givenPOSModel_whenPOSTagging_thenPOSAreDetected() 
  throws Exception {
 
    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    String[] tokens = tokenizer.tokenize("John has a sister named Penny.");

    InputStream inputStreamPOSTagger = getClass()
      .getResourceAsStream("/models/en-pos-maxent.bin");
    POSModel posModel = new POSModel(inputStreamPOSTagger);
    POSTaggerME posTagger = new POSTaggerME(posModel);
    String tags[] = posTagger.tag(tokens);
 
    assertThat(tags).contains("NNP", "VBZ", "DT", "NN", "VBN", "NNP", ".");
}
```

tag()方法将标记映射到 POS 标记列表中。示例中的结果是：

1.  “约翰” - NNP(专有名词)
2.  “有”——VBZ(动词)
3.  “a”——DT(限定符)
4.  “姐姐”——NN(名词)
5.  “命名”——VBZ(动词)
6.  “Penny”—— NNP(专有名词)
7.  “。” - 时期

## 7. 词形还原

现在我们有了句子中标记的词性信息，我们可以进一步分析文本。

词形还原是将具有时态、性别、语气或其他信息的词形映射到词的基本形式的过程——也称为它的“词元”。

词形还原器将标记及其词性标记作为输入并返回单词的词元。因此，在词形还原之前，句子应该通过分词器和词性标注器。

Apache OpenNLP 提供两种类型的词形还原：

-   统计—— 需要一个使用训练数据构建的词形还原器模型来查找给定单词的词元
-   基于字典—— 需要一个包含单词、词性标签和相应词条的所有有效组合的字典

对于统计词形还原，我们需要训练一个模型，而对于字典词形还原，我们只需要一个像[这样的字典文件。](https://raw.githubusercontent.com/richardwilly98/elasticsearch-opennlp-auto-tagging/master/src/main/resources/models/en-lemmatizer.dict)

让我们看一个使用字典文件的代码示例：

```java
@Test
public void givenEnglishDictionary_whenLemmatize_thenLemmasAreDetected() 
  throws Exception {

    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    String[] tokens = tokenizer.tokenize("John has a sister named Penny.");

    InputStream inputStreamPOSTagger = getClass()
      .getResourceAsStream("/models/en-pos-maxent.bin");
    POSModel posModel = new POSModel(inputStreamPOSTagger);
    POSTaggerME posTagger = new POSTaggerME(posModel);
    String tags[] = posTagger.tag(tokens);
    InputStream dictLemmatizer = getClass()
      .getResourceAsStream("/models/en-lemmatizer.dict");
    DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(
      dictLemmatizer);
    String[] lemmas = lemmatizer.lemmatize(tokens, tags);

    assertThat(lemmas)
      .contains("O", "have", "a", "sister", "name", "O", "O");
}
```

如我们所见，我们得到了每个标记的引理。“O”表示无法确定词条，因为该词是专有名词。所以，我们没有“John”和“Penny”的引理。

但是我们已经确定了句子中其他词的引理：

-   有——有
-   一个 - 一个
-   姐姐——姐姐
-   命名——名字

## 8.分块

词性信息在分块中也很重要—— 将句子分成具有语法意义的词组，如名词组或动词组。

与之前类似，我们在调用chunk()方法之前标记一个句子并在标记上使用词性标记 ：

```java
@Test
public void 
  givenChunkerModel_whenChunk_thenChunksAreDetected() 
  throws Exception {

    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    String[] tokens = tokenizer.tokenize("He reckons the current account 
      deficit will narrow to only 8 billion.");

    InputStream inputStreamPOSTagger = getClass()
      .getResourceAsStream("/models/en-pos-maxent.bin");
    POSModel posModel = new POSModel(inputStreamPOSTagger);
    POSTaggerME posTagger = new POSTaggerME(posModel);
    String tags[] = posTagger.tag(tokens);

    InputStream inputStreamChunker = getClass()
      .getResourceAsStream("/models/en-chunker.bin");
    ChunkerModel chunkerModel
     = new ChunkerModel(inputStreamChunker);
    ChunkerME chunker = new ChunkerME(chunkerModel);
    String[] chunks = chunker.chunk(tokens, tags);
    assertThat(chunks).contains(
      "B-NP", "B-VP", "B-NP", "I-NP", 
      "I-NP", "I-NP", "B-VP", "I-VP", 
      "B-PP", "B-NP", "I-NP", "I-NP", "O");
}
```

如我们所见，我们从分词器中获得了每个标记的输出。“B”代表块的开始，“I”代表块的延续，“O”代表没有块。

解析示例的输出，我们得到 6 个块：

1.  “他”——名词短语
2.  “估计”——动词短语
3.  “经常账户赤字”——名词短语
4.  “will narrow”——动词短语
5.  “to”——介词短语
6.  “只有 80 亿”——名词短语

## 9. 语言检测

除了已经讨论过的用例之外，OpenNLP 还提供了一个语言检测 API，可以识别特定文本的语言。 

对于语言检测，我们需要一个训练数据文件。这样的文件包含带有某种语言的句子的行。每行都标有正确的语言，以向机器学习算法提供输入。

可在[此处](https://github.com/apache/opennlp/blob/master/opennlp-tools/src/test/resources/opennlp/tools/doccat/DoccatSample.txt)下载用于语言检测的样本训练数据文件。

我们可以将训练数据文件加载到 LanguageDetectorSampleStream 中， 定义一些训练数据参数，创建模型，然后使用该模型检测文本的语言：

```java
@Test
public void 
  givenLanguageDictionary_whenLanguageDetect_thenLanguageIsDetected() 
  throws FileNotFoundException, IOException {
 
    InputStreamFactory dataIn
     = new MarkableFileInputStreamFactory(
       new File("src/main/resources/models/DoccatSample.txt"));
    ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
    LanguageDetectorSampleStream sampleStream
     = new LanguageDetectorSampleStream(lineStream);
    TrainingParameters params = new TrainingParameters();
    params.put(TrainingParameters.ITERATIONS_PARAM, 100);
    params.put(TrainingParameters.CUTOFF_PARAM, 5);
    params.put("DataIndexer", "TwoPass");
    params.put(TrainingParameters.ALGORITHM_PARAM, "NAIVEBAYES");

    LanguageDetectorModel model = LanguageDetectorME
      .train(sampleStream, params, new LanguageDetectorFactory());

    LanguageDetector ld = new LanguageDetectorME(model);
    Language[] languages = ld
      .predictLanguages("estava em uma marcenaria na Rua Bruno");
    assertThat(Arrays.asList(languages))
      .extracting("lang", "confidence")
      .contains(
        tuple("pob", 0.9999999950605625),
        tuple("ita", 4.939427661577956E-9), 
        tuple("spa", 9.665954064665144E-15),
        tuple("fra", 8.250349924885834E-25)));
}
```

结果是最可能的语言列表以及置信度分数。

而且，有了丰富的模型，我们可以通过这种类型的检测实现更高的准确度。

## 5.总结

我们在这里探索了很多，从 OpenNLP 的有趣功能。我们专注于一些有趣的功能来执行 NLP 任务，例如词形还原、词性标记、标记化、句子检测、语言检测等。