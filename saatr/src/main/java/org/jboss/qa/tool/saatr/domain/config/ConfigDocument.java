
package org.jboss.qa.tool.saatr.domain.config;

import java.util.Set;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithID;
import org.jboss.qa.tool.saatr.domain.config.ConfigProperty.Component;
import org.jboss.qa.tool.saatr.jaxb.config.Config;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * An entity representing an {@link ConfigDocument}.
 * 
 * @author dsimko@redhat.com
 */
@Data
@Document(collection = ConfigDocument.COLLECTION_NAME)
@SuppressWarnings("serial")
public class ConfigDocument implements DocumentWithID<ObjectId> {

    public static final String COLLECTION_NAME = "configs";

    @Id
    private ObjectId id;

    private String name;

    private final Set<ConfigProperty> properties = new TreeSet<>();

    public static ConfigDocument create(Config config, String name) {
        ConfigDocument configData = new ConfigDocument();
        configData.setName(name);
        config.getProperties().forEach(p -> {
            ConfigProperty configProperty = new ConfigProperty(p.getName(), p.getValue(), p.getOptions());
            try {
                configProperty.setComponent(Component.valueOf(p.getComponent()));
            } catch (Exception e) {
                configProperty.setComponent(Component.TEXT_FIELD);
            }
            configData.properties.add(configProperty);
        });
        return configData;
    }
}
