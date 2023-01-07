package cn.tuyucheng.taketoday.searchfilesbywildcards;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearchFileByWildcardUnitTest {
    @Test
    public void whenFourFilenameMatch_thenListOfFour() throws IOException {
        SearchFileByWildcard sfbw = new SearchFileByWildcard();
        List<String> actual = sfbw.searchWithWc(Paths.get("src/test/resources/sfbw"), "glob:*.{txt,docx}");

        assertEquals(new HashSet<>(Arrays.asList("six.txt", "three.txt", "two.docx", "one.txt")), new HashSet<>(actual));
    }

    @Test
    public void whenOneFilenameMatch_thenListOfOne() throws IOException {
        SearchFileByWildcard sfbw = new SearchFileByWildcard();
        List<String> actual = sfbw.searchWithWc(Paths.get("src/test/resources/sfbw"), "glob:????.{csv}");

        assertEquals(new HashSet<>(Arrays.asList("five.csv")), new HashSet<>(actual));
    }
}