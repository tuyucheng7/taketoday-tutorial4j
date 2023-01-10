package cn.tuyucheng.taketoday.springcloudgateway.rewrite;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class URLRewriteGatewayApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(URLRewriteGatewayApplication.class)
              .profiles("url-rewrite")
              .run(args);
    }

}
