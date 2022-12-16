package cn.tuyucheng.taketoday.l.advanced;

import java.io.File;
import java.io.IOException;

public interface FileSystem {
	File[] listFiles(String path);

	void deleteFile(String path) throws IOException;
}