package com.example.dictionary.reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DictionaryReference {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryReference.class);

    private static Map<String, String> dictionary;

    static {
        try {
            readDictionaryFile();
        } catch (Exception e) {
            logger.error("There was a problem reading the dictionary file.", e);
        }
    }

    private DictionaryReference() {
        // blocking instantiation
    }

    private static void readDictionaryFile() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            InputStream inputStream = DictionaryReference.class
                    .getClassLoader()
                    .getResourceAsStream("dictionary.json");

            if (inputStream == null) {
                throw new RuntimeException("dictionary.json not found in resources");
            }

            try (BufferedReader bufferedReader =
                         new BufferedReader(new InputStreamReader(inputStream))) {

                String json = bufferedReader.lines()
                        .collect(Collectors.joining("\n"));

                ObjectMapper objectMapper = new ObjectMapper();

                dictionary = objectMapper.readValue(
                        json,
                        new TypeReference<Map<String, String>>() {}
                );
            }

            stopWatch.stop();

            logger.info("Dictionary created with {} entries in {} ms",
                    dictionary.size(),
                    stopWatch.getTotalTimeMillis());

        } catch (Exception e) {
            logger.error("Error reading dictionary file", e);
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> getDictionary() {
        return dictionary;
    }
}