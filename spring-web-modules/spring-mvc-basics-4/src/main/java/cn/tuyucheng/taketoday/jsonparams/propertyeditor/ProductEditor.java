package cn.tuyucheng.taketoday.jsonparams.propertyeditor;

import java.beans.PropertyEditorSupport;

import cn.tuyucheng.taketoday.jsonparams.model.Product;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductEditor extends PropertyEditorSupport {

    private ObjectMapper objectMapper;

    public ProductEditor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (!StringUtils.hasText(text)) {
            setValue(null);
        } else {
            Product prod = new Product();
            try {
                prod = objectMapper.readValue(text, Product.class);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException(e);
            }
            setValue(prod);
        }
    }

}
