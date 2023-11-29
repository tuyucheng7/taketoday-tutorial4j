package cn.tuyucheng.taketoday.spring.cloud.aws.s3;

import cn.tuyucheng.taketoday.spring.cloud.aws.SpringCloudAwsTestUtil;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * To run this Live Test, we need to have an AWS account and have API keys generated for programmatic access.
 * <p>
 * Check the README file in this module for more information.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
class SpringCloudS3LiveTest {

   @Autowired
   private SpringCloudS3 springCloudS3;

   private static String bucketName;
   private static String testFileToDownload;
   private static String testFileToUpload;

   private static String[] filesWithSimilarName;
   private static List<File> similarNameFiles;

   @BeforeAll
   static void setupResources() throws IOException {
      bucketName = UUID.randomUUID().toString();
      testFileToDownload = "test-file-download.txt";
      testFileToUpload = "test-file-upload.txt";

      filesWithSimilarName = new String[]{"foo/hello-apple.txt", "foo/hello-orange.txt", "bar/hello-grapes.txt",};

      similarNameFiles = new ArrayList<>();
      for (String name : filesWithSimilarName) {
         similarNameFiles.add(new File(name.substring(0, name.lastIndexOf("/") + 1)));
      }

      Files.write(Paths.get(testFileToUpload), "Hello World Uploaded!".getBytes());

      AmazonS3 amazonS3 = SpringCloudAwsTestUtil.amazonS3();
      amazonS3.createBucket(bucketName);

      amazonS3.putObject(bucketName, testFileToDownload, "Hello World");

      for (String s3Key : filesWithSimilarName) {
         amazonS3.putObject(bucketName, s3Key, "Hello World");
      }
   }

   @Test
   void whenS3ObjectDownloaded_thenSuccess() throws IOException {
      String s3Url = "s3://" + bucketName + "/" + testFileToDownload;
      springCloudS3.downloadS3Object(s3Url);
      assertThat(new File(testFileToDownload)).exists();
   }

   @Test
   void whenS3ObjectUploaded_thenSuccess() throws IOException {
      String s3Url = "s3://" + bucketName + "/" + testFileToUpload;
      File file = new File(testFileToUpload);
      springCloudS3.uploadFileToS3(file, s3Url);
   }

   @Test
   void whenMultipleS3ObjectsDownloaded_thenSuccess() throws IOException {
      String s3Url = "s3://" + bucketName + "/**/hello-*.txt";
      springCloudS3.downloadMultipleS3Objects(s3Url);
      similarNameFiles.forEach(f -> assertThat(f).exists());
   }

   @AfterAll
   static void cleanUpResources() {
      AmazonS3 amazonS3 = SpringCloudAwsTestUtil.amazonS3();
      ListObjectsV2Result listObjectsV2Result = amazonS3.listObjectsV2(bucketName);
      for (S3ObjectSummary objectSummary : listObjectsV2Result.getObjectSummaries()) {
         amazonS3.deleteObject(bucketName, objectSummary.getKey());
      }
      amazonS3.deleteBucket(bucketName);

      new File(testFileToDownload).delete();
      new File(testFileToUpload).delete();
      similarNameFiles.forEach(File::delete);
   }
}