## 1. 概述

在本文中，我们将了解如何使用Java捕获麦克风并录制传入的音频以将其保存为 WAV 文件。为了捕获来自麦克风的传入声音，我们使用Java生态系统的一部分JavaSound API。

Java Sound API 是一个强大的 API，用于捕获、处理和播放音频，由 4 个包组成。我们将关注javax.sound.sampled包，它提供了捕获传入音频所需的所有接口和类。

## 2. 什么是目标数据线？

TargetDataLine是一种[数据线](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/sound/sampled/DataLine.html)对象，我们使用它来捕获和读取与音频相关的数据，它从麦克风等音频捕获设备捕获数据[。](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/sound/sampled/TargetDataLine.html)该接口提供读取和捕获数据所需的所有方法，并从目标数据行的缓冲区中读取数据。

我们可以调用 AudioSystem 的getLine () 方法并为其提供DataLine.Info对象，该对象提供音频的所有传输控制方法。Oracle[文档](https://docs.oracle.com/javase/8/docs/technotes/guides/sound/programmer_guide/chapter2.html)详细解释了JavaSound API 的工作原理。

让我们来看看用Java从麦克风捕获音频所需的步骤。

## 3. 捕捉声音的步骤

为了保存捕获的音频，Java 支持：AU、AIFF、AIFC、SND 和 WAVE 文件格式。我们将使用 WAVE (.wav) 文件格式来保存我们的文件。

该过程的第一步是初始化AudioFormat实例。AudioFormat通知Java如何解释和处理传入声音流中的信息位。我们在示例中使用以下AudioFormat类构造函数：

```java
AudioFormat(AudioFormat.Encoding encoding, float sampleRate, int sampleSizeInBits, int channels, int frameSize, float frameRate, boolean bigEndian)
```

之后，我们打开一个DataLine.Info对象。该对象保存与数据线(输入)相关的所有信息。使用DataLine.Info对象，我们可以创建TargetDataLine的实例，它将所有传入数据读取到音频流中。为了生成TargetDataLine实例，我们使用AudioSystem.getLine()方法并传递DataLine.Info对象：

```java
line = (TargetDataLine) AudioSystem.getLine(info);
```

行是TargetDataLine实例，信息是DataLine.Info实例。

创建完成后，我们可以打开线路来读取所有传入的声音。我们可以使用 AudioInputStream来读取传入的数据。总之，我们可以将这些数据写入 WAV 文件并关闭所有流。

为了理解这个过程，我们将看一个记录输入声音的小程序。

## 4. 示例应用

要查看JavaSound API 的运行情况，让我们创建一个简单的程序。我们将把它分成三个部分，首先构建 AudioFormat ，其次构建TargetDataLine，最后将数据保存为文件。

### 4.1. 构建AudioFormat

AudioFormat类定义了TargetDataLine实例可以捕获的数据类型。因此，第一步是在我们打开新数据线之前初始化AudioFormat类实例。App类是应用程序的主类并进行所有调用。我们在名为ApplicationProperties的常量类中定义AudioFormat的属性。我们绕过所有必要的参数构建AudioFormat实例：

```java
public static AudioFormat buildAudioFormatInstance() {
    ApplicationProperties aConstants = new ApplicationProperties();
    AudioFormat.Encoding encoding = aConstants.ENCODING;
    float rate = aConstants.RATE;
    int channels = aConstants.CHANNELS;
    int sampleSize = aConstants.SAMPLE_SIZE;
    boolean bigEndian = aConstants.BIG_ENDIAN;

    return new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8)  channels, rate, bigEndian);
}
```

现在我们已经准备好AudioFormat，我们可以继续构建TargetDataLine实例。

### 4.2. 构建TargetDataLine

我们使用TargetDataLine类从麦克风读取音频数据。在我们的示例中，我们获取并运行SoundRecorder类中的 TargetDataLine。getTargetDataLineForRecord ()方法构建TargetDataLine 实例。

我们读取并处理了音频输入并将其转储到AudioInputStream对象中。我们创建TargetDataLine实例的方式是：

```java
private TargetDataLine getTargetDataLineForRecord() {
    TargetDataLine line;
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
    if (!AudioSystem.isLineSupported(info)) {
        return null;
    }
    line = (TargetDataLine) AudioSystem.getLine(info);
    line.open(format, line.getBufferSize());
    return line;
}
```

### 4.3. 构建和填充AudioInputStream

到目前为止，在我们的示例中，我们已经创建了一个AudioFormat实例并将其应用于 TargetDataLine ，并打开数据线以读取音频数据。我们还创建了一个线程来帮助自动运行 <em>SoundRecorder</em> 实例。我们首先在线程运行时构建一个字节输出流，然后将其转换为一个AudioInputStream实例。我们构建AudioInputStream实例所需的参数是：

```java
int frameSizeInBytes = format.getFrameSize();
int bufferLengthInFrames = line.getBufferSize() / 8;
final int bufferLengthInBytes = bufferLengthInFrames  frameSizeInBytes;
```

请注意，在上面的代码中，我们将 bufferSize 减少了 8。我们这样做是为了使缓冲区和数组的长度相同，以便记录器可以在读取数据后立即将数据传送到线路。

现在我们已经初始化了所有需要的参数，下一步是构建字节输出流。下一步是将生成的输出流(捕获的声音数据)转换为AudioInputStream实例。

```java
buildByteOutputStream(out, line, frameSizeInBytes, bufferLengthInBytes);
this.audioInputStream = new AudioInputStream(line);

setAudioInputStream(convertToAudioIStream(out, frameSizeInBytes));
audioInputStream.reset();
```

在我们设置InputStream之前，我们将构建字节[OutputStream](https://www.baeldung.com/java-outputstream)：

```java
public void buildByteOutputStream(final ByteArrayOutputStream out, final TargetDataLine line, int frameSizeInBytes, final int bufferLengthInBytes) throws IOException {
    final byte[] data = new byte[bufferLengthInBytes];
    int numBytesRead;

    line.start();
    while (thread != null) {
        if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
            break;
        }
        out.write(data, 0, numBytesRead);
    }
}
```

然后我们[将字节Outstream转换为AudioInputStream](https://www.baeldung.com/convert-byte-array-to-input-stream)为：

```java
public AudioInputStream convertToAudioIStream(final ByteArrayOutputStream out, int frameSizeInBytes) {
    byte audioBytes[] = out.toByteArray();
    ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
    AudioInputStream audioStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);
    long milliseconds = (long) ((audioInputStream.getFrameLength()  1000) / format.getFrameRate());
    duration = milliseconds / 1000.0;
    return audioStream;
}
```

### 4.4. 将AudioInputStream保存到 Wav 文件

我们已经创建并填充了 AudioInputStream并将其存储为SoundRecorder类的成员变量。我们将使用SoundRecorder实例 getter 属性在App类中检索此AudioInputStream并将其传递给WaveDataUtil类：

```java
wd.saveToFile("/SoundClip", AudioFileFormat.Type.WAVE, soundRecorder.getAudioInputStream());
```

WaveDataUtil类具有将AudioInputStream转换为 .wav 文件的代码 ：

```java
AudioSystem.write(audioInputStream, fileType, myFile);
```

## 5.总结

本文展示了一个使用JavaSound API 通过麦克风捕获和录制音频的快速示例。