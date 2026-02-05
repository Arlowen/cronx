package io.cronx.toolkit.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertyUtil {

    public static final String CLASSPATH_PREFIX = "classpath:";

    public static Properties getPropertiesFromClassPath(String resourceFileName) {
        Properties prop = new Properties();
        InputStream is = PropertyUtil.class.getClassLoader().getResourceAsStream(resourceFileName);
        try {
            prop.load(is);
        } catch (IOException e) {
            log.error(resourceFileName + " file is not exist!");
        }
        return prop;
    }

    public static Properties getProperties(String confPath, Class clz) {
        try {
            Properties prop = new Properties();
            if (confPath.startsWith(CLASSPATH_PREFIX)) {
                confPath = StringUtils.substringAfter(confPath, CLASSPATH_PREFIX);
                prop.load(Objects.requireNonNull(clz.getClassLoader().getResourceAsStream(confPath)));
            } else {
                prop.load(new FileInputStream(confPath));
            }

            return prop;
        } catch (Exception e) {
            String msg = "load " + confPath + " error.msg:" + ExceptionUtils.getRootCauseMessage(e);
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}
