package com.myRetail.product.test.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtil.class);

    public static ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static <T> T readObjectFromFile(String filename, Class<T> clazz) {
        File file = new File(filename);
        try {
            InputStream inputStream = new FileInputStream(file);
            try {
                return objectMapper.readValue(inputStream, clazz);
            } catch (IOException e) {
                LOGGER.error("Exception encountered", e);
                return null;
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("Exception encountered", e);
            return null;
        }
    }
}
