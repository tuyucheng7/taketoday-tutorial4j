## 1. 概述

在本教程中，我们将讨论继承，这是面向对象编程的重要概念之一。在Java中，用于继承的两个主要关键字是extends和implements。

## 2.扩展与实现

让我们讨论这两个关键字之间的区别。

我们使用extends关键字从类继承属性和方法。充当父类的类称为基类，继承自该基类的类称为派生类或子类。extends关键字主要用于将父类的功能扩展到派生类。另外，一个基类可以有多个派生类，但是一个派生类只能有一个基类，因为Java不支持多重继承。

另一方面，我们使用implements关键字来实现接口。接口仅由抽象方法组成。一个类将实现接口并根据所需的功能定义这些抽象方法。与extends不同，任何类都可以实现多个接口。

尽管这两个关键字都符合继承的概念，但implements关键字主要与抽象相关联并用于定义契约，而extends用于扩展类的现有功能。

## 3.实施

让我们跳到实现，并一一详细了解扩展、实现和多重继承。

### 3.1. 延伸

让我们首先创建一个名为Media的类，该类具有id、title和artist。此类将充当基类。VideoMedia和AudioMedia将扩展此类的功能：

```java
public class Media {

    private int id;
    private String title;
    private String artist;
    // standard getters and setters
}
```

现在，让我们创建另一个名为VideoMedia的类，它扩展类Media并继承其属性。此外，它还有自己的属性，如resolution和aspectRatio：

```java
public class VideoMedia extends Media {

    private String resolution;
    private String aspectRatio;
    // standard getters and setters
}
```

同样，AudioMedia类也扩展了Media 类，并将拥有自己的附加属性，如比特率和频率：

```java
public class AudioMedia extends Media {

    private int bitrate;
    private String frequency;
    // standard getters and setters

    @Override
    public void printTitle() {
        System.out.println("AudioMedia Title");
    }
}
```

让我们为基类和派生类创建对象来查看继承的属性：

```java
Media media = new Media();
media.setId(001);
media.setTitle("Media1");
media.setArtist("Artist001");

AudioMedia audioMedia = new AudioMedia();
audioMedia.setId(101);
audioMedia.setTitle("Audio1");
audioMedia.setArtist("Artist101");
audioMedia.setBitrate(3500);
audioMedia.setFrequency("256kbps");

VideoMedia videoMedia = new VideoMedia();
videoMedia.setId(201);
videoMedia.setTitle("Video1");
videoMedia.setArtist("Artist201");
videoMedia.setResolution("1024x768");
videoMedia.setAspectRatio("16:9");

System.out.println(media);
System.out.println(audioMedia);
System.out.println(videoMedia);
```

所有三个类都打印关联的属性：

```java
Media{id=1, title='Media1', artist='Artist001'}
AudioMedia{id=101, title='Audio1', artist='Artist101', bitrate=3500, frequency='256kbps'} 
VideoMedia{id=201, title='Video1', artist='Artist201'resolution='1024x768', aspectRatio='16:9'} 
```

### 3.2. 工具

为了理解抽象和接口，我们将创建一个接口MediaPlayer，它有两个方法，称为播放和暂停。如前所述，此接口中的所有方法都是抽象的。换句话说，接口只包含方法声明。

在Java中，接口不需要将方法显式声明为abstract或public。实现接口MediaPlayer的类将定义这些方法：

```java
public interface MediaPlayer {

    void play();

    void pause();
}
```

AudioMediaPlayer类实现了MediaPlayer，它将定义音频媒体的播放和暂停方法： 

```java
public class AudioMediaPlayer implements MediaPlayer {

    @Override
    public void play() {
        System.out.println("AudioMediaPlayer is Playing");
    }

    @Override
    public void pause() {
        System.out.println("AudioMediaPlayer is Paused");
    }
}
```

同样，VideoMediaPlayer 实现了 MediaPlayer并提供了播放和暂停视频媒体的方法定义：

```java
public class VideoMediaPlayer implements MediaPlayer {

    @Override
    public void play() {
        System.out.println("VideoMediaPlayer is Playing");
    }

    @Override
    public void pause() {
        System.out.println("VideoMediaPlayer is Paused");
    }
}
```

此外，让我们创建AudioMediaPlayer 和VideoMediaPlayer的实例，并为它们调用播放和 暂停 方法：

```java
AudioMediaPlayer audioMediaPlayer = new AudioMediaPlayer();
audioMediaPlayer.play();
audioMediaPlayer.pause();

VideoMediaPlayer videoMediaPlayer = new VideoMediaPlayer();
videoMediaPlayer.play();
videoMediaPlayer.pause();
```

AudioMediaPlayer和VideoMediaPlayer 调用各自的play和pause实现：

```java
AudioMediaPlayer is Playing
AudioMediaPlayer is Paused

VideoMediaPlayer is Playing
VideoMediaPlayer is Paused
```

### 3.3. 多重继承

由于歧义问题，Java 不直接支持多重继承。当一个类继承自多个父类，并且两个父类都具有同名的方法或属性时，就会出现歧义问题。因此，子类无法解决要继承的方法或属性的冲突。但是，一个类可以继承自多个接口。让我们创建一个接口AdvancedPlayerOptions：

```java
public interface AdvancedPlayerOptions {

    void seek();

    void fastForward();
}
```

MultiMediaPlayer 类实现了 MediaPlayer和AdvancedPlayerOptions 并定义了在这两个接口中声明的方法：

```java
public class MultiMediaPlayer implements MediaPlayer, AdvancedPlayerOptions {

    @Override
    public void play() {
        System.out.println("MultiMediaPlayer is Playing");
    }

    @Override
    public void pause() {
        System.out.println("MultiMediaPlayer is Paused");
    }

    @Override
    public void seek() {
        System.out.println("MultiMediaPlayer is being seeked");
    }

    @Override
    public void fastForward() {
        System.out.println("MultiMediaPlayer is being fast forwarded");
    }
}
```

现在，我们将创建一个MultiMediaPlayer 类的实例并调用所有已实现的方法：

```java
MultiMediaPlayer multiMediaPlayer = new MultiMediaPlayer();
multiMediaPlayer.play();
multiMediaPlayer.pause();
multiMediaPlayer.seek();
multiMediaPlayer.fastForward();
```

正如预期的那样，MultiMediaPlayer调用其播放和暂停的实现：

```java
MultiMediaPlayer is Playing
MultiMediaPlayer is Paused 
MultiMediaPlayer is being seeked 
MultiMediaPlayer is being fast forwarded
```

## 4。总结

在本教程中，我们讨论了extends和implements之间的显着差异。此外，我们创建了类和接口来演示extends和implements的概念。此外，我们还讨论了多重继承以及如何使用接口来实现它。