package cn.tuyucheng.taketoday.zip;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1)
public class ZipBenchmark {

   public static final int NUM_OF_FILES = 10;
   public static final int DATA_SIZE = 204800;

   @State(Scope.Thread)
   public static class SourceState {

      public File compressedFile;

      @Setup(Level.Trial)
      public void setup() throws IOException {
         ZipSampleFileStore sampleFileStore = new ZipSampleFileStore(NUM_OF_FILES, DATA_SIZE);
         compressedFile = sampleFileStore.getFile();
      }

      @TearDown(Level.Trial)
      public void cleanup() {
         if (compressedFile.exists()) {
            compressedFile.delete();
         }
      }
   }

   @Benchmark
   public static void readAllEntriesByZipFile(SourceState sourceState, Blackhole blackhole) throws IOException {

      try (ZipFile zipFile = new ZipFile(sourceState.compressedFile)) {
         Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
         while (zipEntries.hasMoreElements()) {
            ZipEntry zipEntry = zipEntries.nextElement();
            try (InputStream inputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry))) {
               blackhole.consume(ZipSampleFileStore.getString(inputStream));
            }
         }
      }
   }

   @Benchmark
   public static void readAllEntriesByZipInputStream(SourceState sourceState, Blackhole blackhole) throws IOException {

      try (
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceState.compressedFile));
            ZipInputStream zipInputStream = new ZipInputStream(bis)
      ) {
         ZipEntry entry;
         while ((entry = zipInputStream.getNextEntry()) != null) {
            blackhole.consume(ZipSampleFileStore.getString(zipInputStream));
         }
      }
   }

   @Benchmark
   public static void readLastEntryByZipFile(SourceState sourceState, Blackhole blackhole) throws IOException {

      try (ZipFile zipFile = new ZipFile(sourceState.compressedFile)) {
         ZipEntry zipEntry = zipFile.getEntry(getLastEntryName());
         try (InputStream inputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry))) {
            blackhole.consume(ZipSampleFileStore.getString(inputStream));
         }
      }
   }

   @Benchmark
   public static void readLastEntryByZipInputStream(SourceState sourceState, Blackhole blackhole) throws IOException {

      try (
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceState.compressedFile));
            ZipInputStream zipInputStream = new ZipInputStream(bis)
      ) {
         ZipEntry entry;
         while ((entry = zipInputStream.getNextEntry()) != null) {
            if (Objects.equals(entry.getName(), getLastEntryName())) {
               blackhole.consume(ZipSampleFileStore.getString(zipInputStream));
            }
         }
      }
   }

   private static String getLastEntryName() {
      return String.format(ZipSampleFileStore.ENTRY_NAME_PATTERN, NUM_OF_FILES);
   }

   public static void main(String[] args) throws Exception {
      Options options = new OptionsBuilder()
            .include(ZipBenchmark.class.getSimpleName()).threads(1)
            .shouldFailOnError(true)
            .shouldDoGC(true)
            .jvmArgs("-server").build();
      new Runner(options).run();
   }

}