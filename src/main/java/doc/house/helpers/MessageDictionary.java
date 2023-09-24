package doc.house.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MessageDictionary {
    private final Properties messages;
    public MessageDictionary(String name) {
        messages = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(String.format("%s.properties",name))) {
            messages.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMessage(String key) {
        return messages.getProperty(key, key);
    }
}

