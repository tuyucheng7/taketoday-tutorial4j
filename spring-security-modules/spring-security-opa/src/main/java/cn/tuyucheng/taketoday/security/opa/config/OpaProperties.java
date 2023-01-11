package cn.tuyucheng.taketoday.security.opa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.Nonnull;

@ConfigurationProperties(prefix = "opa")
@Data
public class OpaProperties {
    @Nonnull
    private String endpoint = "http://localhost:8181";
}
