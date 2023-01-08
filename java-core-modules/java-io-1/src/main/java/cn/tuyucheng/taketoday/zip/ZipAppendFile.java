package cn.tuyucheng.taketoday.zip;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class ZipAppendFile {
	public static void main(final String[] args) throws IOException {
		ZipMultipleFiles.main(args);
		String file3 = ZipAppendFile.class.getClassLoader().getResource("zipTest/file3.txt").getPath();
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		Path path = Paths.get(Paths.get(file3).getParent() + "/compressed.zip");
		URI uri = URI.create("jar:" + path.toUri());
		try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
			Path nf = fs.getPath("newFile3.txt");
			Files.write(nf, Files.readAllBytes(Paths.get(file3)), StandardOpenOption.CREATE);
		}
	}
}