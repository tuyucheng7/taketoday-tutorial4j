package cn.tuyucheng.taketoday.lines;

public class Main {

    private static final String INPUT_FILE_NAME = "src/main/resources/input.txt";

    public static void main(String... args) throws Exception {
        System.out.printf("Total Number of Lines Using BufferedReader: %s%n", NumberOfLineFinder.getTotalNumberOfLinesUsingBufferedReader(INPUT_FILE_NAME));
        System.out.printf("Total Number of Lines Using LineNumberReader: %s%n", NumberOfLineFinder.getTotalNumberOfLinesUsingLineNumberReader(INPUT_FILE_NAME));
        System.out.printf("Total Number of Lines Using Scanner: %s%n", NumberOfLineFinder.getTotalNumberOfLinesUsingScanner(INPUT_FILE_NAME));
        System.out.printf("Total Number of Lines Using NIO Files: %s%n", NumberOfLineFinder.getTotalNumberOfLinesUsingNIOFiles(INPUT_FILE_NAME));
        System.out.printf("Total Number of Lines Using NIO Files#readAllLines: %s%n", NumberOfLineFinder.getTotalNumberOfLinesUsingNIOFilesReadAllLines(INPUT_FILE_NAME));
        System.out.printf("Total Number of Lines Using NIO FileChannel: %s%n", NumberOfLineFinder.getTotalNumberOfLinesUsingNIOFileChannel(INPUT_FILE_NAME));
        System.out.printf("Total Number of Lines Using Apache Commons IO: %s%n", NumberOfLineFinder.getTotalNumberOfLinesUsingApacheCommonsIO(INPUT_FILE_NAME));
        System.out.printf("Total Number of Lines Using NIO Google Guava: %s%n", NumberOfLineFinder.getTotalNumberOfLinesUsingGoogleGuava(INPUT_FILE_NAME));
    }
}
