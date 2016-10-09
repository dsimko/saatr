
package org.jboss.qa.tool.saatr.domain.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithID;
import org.jboss.qa.tool.saatr.domain.config.ConfigDocument.ConfigProperty.Component;
import org.jboss.qa.tool.saatr.jaxb.config.Config;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConfigProperty implements Serializable, Comparable<ConfigProperty> {

        public static enum Component {
            TEXT_FIELD, TEXT_AREA
        }

        private String name;

        private String value;

        private Component component = Component.TEXT_FIELD;

        private List<String> options = new ArrayList<>();

        public ConfigProperty(String name, String value, List<String> options) {
            this.name = name;
            this.value = value;
            this.options = options;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ConfigProperty other = (ConfigProperty) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public int compareTo(ConfigProperty o) {
            if (this.name == null || o.name == null) {
                return 0;
            }
            return this.name.compareTo(o.name);
        }

    }
}
