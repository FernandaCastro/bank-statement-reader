package com.example.statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

//@Qualifier("statementPropertiesHandle")
@Component
public class StatementPropertiesHandle {

    @Autowired
    private Environment env;

    public Map<String, String> getCategories(String owner) {
        String categoryPrefix = "statement." + owner + ".category.";
        Map<String, String> map = new HashMap<>();
        if (env instanceof ConfigurableEnvironment) {
            for (PropertySource<?> propertySource : ((ConfigurableEnvironment) env).getPropertySources()) {
                if (propertySource instanceof EnumerablePropertySource) {
                    for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
                        if (key.startsWith(categoryPrefix)) {
                            map.put(key.replace(categoryPrefix, "").toUpperCase(), (String) propertySource.getProperty(key));
                        }
                    }
                }
            }
        }
        return map;
    }
}
