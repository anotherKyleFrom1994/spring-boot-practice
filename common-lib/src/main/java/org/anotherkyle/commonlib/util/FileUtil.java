package org.anotherkyle.commonlib.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
@Scope("singleton")
public class FileUtil {
    private final ResourceLoader resourceLoader;

    @Autowired
    public FileUtil(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String retrieveTxtContent(String filepath) throws FileNotFoundException {
        String result;
        try {
            Resource resource = resourceLoader.getResource(filepath);
            result = readFromInputStream(resource.getInputStream());

        } catch (Exception e) {
            throw new FileNotFoundException(e.getMessage() + Arrays.toString(e.getStackTrace()));
        }
        return result;
    }


    public Map<String, String> retrieveProperties(String filepath) {
        Map<String, String> result = new HashMap<>();
        Properties properties = new Properties();

        try {
            Resource resource = resourceLoader.getResource(filepath);
            InputStream in = resource.getInputStream();
            properties.load(in);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        for (Object key : properties.keySet()) {
            result.put(key.toString(), properties.get(key).toString());
        }

        return result;
    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
}


