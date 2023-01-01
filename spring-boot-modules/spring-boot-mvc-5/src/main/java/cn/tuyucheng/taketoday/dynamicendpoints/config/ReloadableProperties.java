package cn.tuyucheng.taketoday.dynamicendpoints.config;

import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ReloadableProperties extends Properties {
    private PropertiesConfiguration propertiesConfiguration;

    public ReloadableProperties(PropertiesConfiguration propertiesConfiguration) throws IOException {
        super.load(new FileReader(propertiesConfiguration.getFile()));
        this.propertiesConfiguration = propertiesConfiguration;
    }

    @Override
    public String getProperty(String key) {
        String val = propertiesConfiguration.getString(key);
        super.setProperty(key, val);
        return val;
    }
}