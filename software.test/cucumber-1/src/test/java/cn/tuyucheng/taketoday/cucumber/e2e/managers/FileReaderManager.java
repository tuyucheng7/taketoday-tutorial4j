package cn.tuyucheng.taketoday.cucumber.e2e.managers;

import cn.tuyucheng.taketoday.cucumber.e2e.config.ConfigFileReader;
import cn.tuyucheng.taketoday.cucumber.e2e.config.JsonDataReader;

public class FileReaderManager {

   private static final FileReaderManager fileReaderManager = new FileReaderManager();
   private static ConfigFileReader configFileReader;
   private static JsonDataReader jsonDataReader;

   private FileReaderManager() {

   }

   public static FileReaderManager getInstance() {
      return fileReaderManager;
   }

   public ConfigFileReader getConfigFileReader() {
      return configFileReader == null ? new ConfigFileReader() : configFileReader;
   }

   public JsonDataReader getJsonDataReader() {
      return jsonDataReader == null ? new JsonDataReader() : jsonDataReader;
   }
}