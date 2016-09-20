package org.jboss.qa.tool.saatr.domain.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithID;
import org.jboss.qa.tool.saatr.jaxb.config.Config;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dsimko@redhat.com
 */
@Data
@Document
@SuppressWarnings("serial")
public class ConfigDocument implements DocumentWithID<ObjectId> {

    @Id
    private ObjectId id;
    private String name;
    private final Set<ConfigProperty> properties = new TreeSet<>();

    public static ConfigDocument create(Config config, String name) {
        ConfigDocument configData = new ConfigDocument();
        configData.setName(name);
        config.getProperties().forEach(p -> {
            configData.properties.add(new ConfigProperty(p.getName(), p.getValue(), p.getOptions()));
        });
        return configData;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConfigProperty implements Serializable, Comparable<ConfigProperty> {
        private String name;
        private String value;
        private List<String> options = new ArrayList<>();

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