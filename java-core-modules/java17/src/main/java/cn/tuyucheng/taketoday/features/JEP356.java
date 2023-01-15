package cn.tuyucheng.taketoday.features;

import java.util.random.RandomGeneratorFactory;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class JEP356 {

	public Stream<String> getAllAlgorithms() {
		return RandomGeneratorFactory.all().map(RandomGeneratorFactory::name);
	}

	public IntStream getPseudoInts(String algorithm, int streamSize) {
		// returns an IntStream with size @streamSize of random numbers generated using the @algorithm
		// where the lower bound is 0 and the upper is 100 (exclusive)
		return RandomGeneratorFactory.of(algorithm)
				.create()
				.ints(streamSize, 0, 100);
	}
	// D:\develop-tools\jdk-17.0.5\bin\java.exe -ea --enable-preview --enable-preview -Didea.test.cyclic.buffer.size=1048576
	// -javaagent:D:\software\apps\IDEA-U\ch-0\223.8214.52\lib\idea_rt.jar=60916:D:\software\apps\IDEA-U\ch-0\223.8214.52\bin
	// --patch-module core.java=D:/workspace/intellij/taketoday-tutorial4j/java-core-modules/java17/target/test-classes
	// --add-reads core.java=ALL-UNNAMED --add-opens core.java/cn.tuyucheng.taketoday.switchpatterns=ALL-UNNAMED
	// --add-modules core.java -Dfile.encoding=UTF-8 -classpath D:\develop-tools\apache-maven-3.8.4\repository\org\junit\platform\junit-platform-launcher\1.8.2\junit-platform-launcher-1.8.2.jar;D:\software\apps\IDEA-U\ch-0\223.8214.52\lib\idea_rt.jar;D:\software\apps\IDEA-U\ch-0\223.8214.52\plugins\junit\lib\junit5-rt.jar;D:\software\apps\IDEA-U\ch-0\223.8214.52\plugins\junit\lib\junit-rt.jar;D:\workspace\intellij\taketoday-tutorial4j\java-core-modules\java17\target\test-classes;D:\develop-tools\apache-maven-3.8.4\repository\net\sf\jopt-simple\jopt-simple\5.0.4\jopt-simple-5.0.4.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\apache\commons\commons-math3\3.2\commons-math3-3.2.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\openjdk\jmh\jmh-generator-annprocess\1.35\jmh-generator-annprocess-1.35.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar;D:\develop-tools\apache-maven-3.8.4\repository\ch\qos\logback\logback-classic\1.2.11\logback-classic-1.2.11.jar;D:\develop-tools\apache-maven-3.8.4\repository\ch\qos\logback\logback-core\1.2.11\logback-core-1.2.11.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\slf4j\jcl-over-slf4j\1.7.36\jcl-over-slf4j-1.7.36.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\junit\jupiter\junit-jupiter-engine\5.8.2\junit-jupiter-engine-5.8.2.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\junit\platform\junit-platform-engine\1.8.2\junit-platform-engine-1.8.2.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\apiguardian\apiguardian-api\1.1.2\apiguardian-api-1.1.2.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\junit\jupiter\junit-jupiter-params\5.8.2\junit-jupiter-params-5.8.2.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\junit\jupiter\junit-jupiter-api\5.8.2\junit-jupiter-api-5.8.2.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\opentest4j\opentest4j\1.2.0\opentest4j-1.2.0.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\junit\platform\junit-platform-commons\1.8.2\junit-platform-commons-1.8.2.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\junit\vintage\junit-vintage-engine\5.8.2\junit-vintage-engine-5.8.2.jar;D:\develop-tools\apache-maven-3.8.4\repository\junit\junit\4.13.2\junit-4.13.2.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\hamcrest\hamcrest-core\1.3\hamcrest-core-1.3.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\assertj\assertj-core\3.22.0\assertj-core-3.22.0.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\hamcrest\hamcrest\2.2\hamcrest-2.2.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\hamcrest\hamcrest-all\1.3\hamcrest-all-1.3.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\mockito\mockito-core\4.5.1\mockito-core-4.5.1.jar;D:\develop-tools\apache-maven-3.8.4\repository\net\bytebuddy\byte-buddy\1.12.9\byte-buddy-1.12.9.jar;D:\develop-tools\apache-maven-3.8.4\repository\net\bytebuddy\byte-buddy-agent\1.12.9\byte-buddy-agent-1.12.9.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\objenesis\objenesis\3.2\objenesis-3.2.jar;D:\develop-tools\apache-maven-3.8.4\repository\org\apache\maven\surefire\surefire-logger-api\3.0.0-M7\surefire-logger-api-3.0.0-M7.jar -p D:\workspace\intellij\taketoday-tutorial4j\java-core-modules\java17\target\classes;D:\develop-tools\apache-maven-3.8.4\repository\org\openjdk\jmh\jmh-core\1.35\jmh-core-1.35.jar com.intellij.rt.junit.JUnitStarter -ideVersion5 -junit5 cn.tuyucheng.taketoday.switchpatterns.GuardedPatternsUnitTest
}