## 1. 概述

在本教程中，我们将学习如何使用Java播放声音。Java Sound APIs 被设计为流畅和连续地播放声音，甚至是很长的声音。

作为本教程的一部分，我们将使用Java 提供的Clip和SourceDataLine Sound API 播放音频文件。我们还将播放不同的音频格式文件。

此外，我们还将讨论每个 API 的优缺点。此外，我们将看到一些也可以播放声音的第三方Java库。

## 2. 播放声音的JavaAPI

通常， javax.sound包中的JavaSound API提供两种播放音频的方法。在这两种方法之间，声音文件数据的指定方式有所不同。Java Sound API 可以以流式缓冲方式和内存中非缓冲方式处理音频传输。Java 的两个最著名的声音 API 是Clip和SourceDataLine。

### 2.1. 剪辑API

Clip API 是一种用于Java的无缓冲或内存中声音 API。Clip类是javax.sound.sampled包的一部分，在读取和播放简短的声音文件时很有用 。在播放之前，整个音频文件被加载到内存中，用户可以完全控制播放。

除了循环播放声音外，它还允许用户从随机位置开始播放。

让我们首先创建一个示例类SoundPlayerWithClip，它实现了LineListener接口以便接收用于播放的线路事件(OPEN、CLOSE、START和STOP)。我们将实现LineListener的update()方法来检查播放状态：

```java
public class SoundPlayerUsingClip implements LineListener {

    boolean isPlaybackCompleted;
    
    @Override
    public void update(LineEvent event) {
        if (LineEvent.Type.START == event.getType()) {
            System.out.println("Playback started.");
        } else if (LineEvent.Type.STOP == event.getType()) {
            isPlaybackCompleted = true;
            System.out.println("Playback completed.");
        }
    }
}
```

其次，让我们从项目的资源文件夹中读取音频文件。我们的资源文件夹包含三种不同格式的音频文件——即 WAV、MP3 和 MPEG：

```java
InputStream inputStream = getClass().getClassLoader().getResourceAsStream(audioFilePath);
```

第三，从文件流中，我们将创建一个AudioInputStream：

```java
AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputStream);
```

现在，我们将创建一个DataLine.Info对象：

```java
AudioFormat audioFormat = audioStream.getFormat();
DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
```

让我们从这个DataLine.Info创建一个Clip对象并打开流，然后调用start开始播放音频：

```java
Clip audioClip = (Clip) AudioSystem.getLine(info);
audioClip.addLineListener(this);
audioClip.open(audioStream);
audioClip.start();
```

最后，我们需要关闭所有打开的资源：

```java
audioClip.close();
audioStream.close();
```

代码运行后，将播放音频文件。

由于音频已预加载到内存中，因此我们还有许多其他有用的 API 可以从中受益。

我们可以使用Clip.loop方法来循环播放音频片段。

例如，我们可以设置它播放五次音频：

```java
audioClip.loop(4);

```

或者，我们可以将其设置为无限时间播放音频(或直到中断)：

```java
audioClip.loop(Clip.LOOP_CONTINUOUSLY);
```

Clip.setMicrosecondPosition设置媒体位置。当剪辑下次开始播放时，它将从这个位置开始。比如从第30秒开始，我们可以设置为：

```java
audioClip.setMicrosecondPosition(30_000_000);
```

### 2.2. 源数据线API

SourceDataLine API 是用于 java 的缓冲或流式声音 API。SourceDataLine类是javax.sound.sampled包的一部分，它可以播放无法预加载到内存中的长声音文件。

当我们希望为大型音频文件优化内存或流式传输实时音频数据时，使用SourceDataLine更为有效。如果我们事先不知道声音持续多长时间以及何时结束，它也很有用。

让我们首先创建一个示例类并从我们项目的资源文件夹中读取音频文件。我们的资源文件夹包含三种不同格式的音频文件——即 WAV、MP3 和 MPEG：

```java
InputStream inputStream = getClass().getClassLoader().getResourceAsStream(audioFilePath);
```

其次，从文件输入流中，我们将创建一个AudioInputStream：

```java
AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputStream);
```

现在，我们将创建一个DataLine.Info对象：

```java
AudioFormat audioFormat = audioStream.getFormat();
DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
```

让我们从这个DataLine.Info创建一个SourceDataLine对象，打开流，然后调用start开始播放音频：

```java
SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
sourceDataLine.open(audioFormat);
sourceDataLine.start();
```

现在，在SourceDataLine的情况下，音频数据以块的形式加载，我们需要提供缓冲区大小：

```java
private static final int BUFFER_SIZE = 4096;
```

现在，让我们从AudioInputStream中读取音频数据并将其发送到SourceDataLine 的播放缓冲区，直到它到达流的末尾：

```java
byte[] bufferBytes = new byte[BUFFER_SIZE];
int readBytes = -1;
while ((readBytes = audioStream.read(bufferBytes)) != -1) {
    sourceDataLine.write(bufferBytes, 0, readBytes);
}
```

最后，让我们关闭所有打开的资源：

```java
sourceDataLine.drain();
sourceDataLine.close();
audioStream.close();
```

代码运行后，将播放音频文件。

在这里，我们不需要实现任何LineListener接口。

### 2.3. Clip与SourceDataLine 的比较

让我们讨论一下两者的优缺点：

| 夹子                                                     | 源数据行                                                 |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| 支持从音频中的任意位置播放。 请参见setMicrosecondPosition(long)或setFramePosition(int)。 | 无法从声音中的任意位置开始播放。                             |
| 支持循环播放(全部或部分声音)。 参见setLoopPoints(int, int)和loop(int)。 | 无法播放(循环)全部或部分声音。                             |
| 可以在播放前知道声音的持续时间。 请参见getFrameLength()或getMicrosecondLength()。 | 在播放之前无法知道声音的持续时间。                           |
| 可以在当前位置停止播放并稍后继续播放。参见stop() 和 start() | 中途停不下来继续玩。                                         |
| 播放内存中的大音频文件不合适且效率低下。                     | 它适合且高效地播放长声音文件或实时流式传输声音。             |
| Clip的 start ( )方法确实会播放声音，但不会阻塞当前线程(立即返回)，所以需要实现LineListener接口才能知道播放状态。 | 与 Clip不同，我们不必实现 LineListener 接口即可知道播放何时完成。 |
| 无法控制将哪些声音数据写入音频线的播放缓冲区。               | 可以控制将哪些声音数据写入音频线的播放缓冲区。               |

### 2.4.JavaAPI 支持 MP3 格式

目前，Clip和SourceDataLine都可以播放 AIFC、AIFF、AU、SND 和 WAV 格式的音频文件。

我们可以使用AudioSystem检查支持的音频格式：

```java
Type[] list = AudioSystem.getAudioFileTypes();
StringBuilder supportedFormat = new StringBuilder("Supported formats:");
for (Type type : list) {
    supportedFormat.append(", " + type.toString());
}
System.out.println(supportedFormat.toString());
```

但是，我们无法使用JavaSound APIs Clip和SourceDataLine 播放流行的音频格式 MP3/MPEG。我们需要寻找一些可以播放MP3格式的第三方库。

如果我们将 MP3 格式文件提供给Clip或SourceDataLine API，我们将得到UnsupportedAudioFileException：

```java
javax.sound.sampled.UnsupportedAudioFileException: could not get audio input stream from input file
    at javax.sound.sampled.AudioSystem.getAudioInputStream(AudioSystem.java:1189)
```

## 3. 播放声音的第三方JavaAPI

让我们看看一对第三方库也可以播放不同的音频格式文件。

### 3.1. JavaFX 库

[JavaFX](https://www.baeldung.com/javafx)具有可播放 MP3 文件的Media 和 MediaPlayer类。它还可以播放其他音频格式，如 WAV。

让我们创建一个示例类并使用Media和 MediaPlayer类来播放我们的 MP3 文件：

```java
String audioFilePath = "AudioFileWithMp3Format.mp3";
SoundPlayerUsingJavaFx soundPlayerWithJavaFx = new SoundPlayerUsingJavaFx();
try {
    com.sun.javafx.application.PlatformImpl.startup(() -> {});
    Media media = new Media(
      soundPlayerWithJavaFx.getClass().getClassLoader().getResource(audioFilePath).toExternalForm());
    MediaPlayer mp3Player = new MediaPlayer(media);
    mp3Player.play();
} catch (Exception ex) {
    System.out.println("Error occured during playback process:" + ex.getMessage());
}
```

这个 API 的一个优点是它可以播放 WAV、MP3 和 MPEG 音频格式。

### 3.2. J层库

JLayer库可以播放 MPEG 格式等音频格式，包括 MP3 [。](https://github.com/umjammer/jlayer)但是，它不能播放其他格式，如 WAV。

让我们使用 Javazoom Player类创建一个示例类：

```java
String audioFilePath = "AudioFileWithMp3Format.mp3";
SoundPlayerUsingJavaZoom player = new SoundPlayerUsingJavaZoom();
try {
    BufferedInputStream buffer = new BufferedInputStream(
      player.getClass().getClassLoader().getResourceAsStream(audioFilePath));
    Player mp3Player = new Player(buffer);
    mp3Player.play();
} catch (Exception ex) {
    System.out.println("Error occured during playback process:" + ex.getMessage());
}
```

## 4. 总结

在本文中，我们学习了如何使用Java播放声音。我们还学习了两种不同的JavaSound API，Clip和SourceDataLine。后来，我们看到了Clip和SourceDataLine API之间的区别，这将帮助我们为任何用例选择合适的 API。

最后，我们看到一些第三方库也可以播放音频并支持其他格式，例如 MP3。