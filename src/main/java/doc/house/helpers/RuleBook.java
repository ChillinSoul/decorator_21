package doc.house.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The RuleBook class is responsible for loading and managing game rules.
 * It reads rules from a properties file and provides access methods.
 */
public class RuleBook {

    private final Properties rules;

    /**
     * Constructor to initialize and load the rules.
     * Reads rules from the 'rules.properties' file located in the resource directory.
     * If the file cannot be loaded, it prints the stack trace.
     */
    public RuleBook() {
        rules = new Properties();

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("rules.properties")) {
            if (inputStream == null) {
                throw new IOException("Resource 'rules.properties' not found");
            }

            rules.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the value of a specific rule identified by its key.
     *
     * @param key the name of the rule.
     * @return the value of the rule as an integer, or 0 if the key is not found.
     */
    public Integer getRule(String key) {
        return Integer.parseInt(rules.getProperty(key, "0"));
    }
}

