
package org.jboss.qa.tool.saatr.jaxb.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.qa.tool.saatr.domain.config.ConfigDocument;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dsimko@redhat.com
 */
@Data
@SuppressWarnings("serial")
@XmlRootElement(name = "config")
@XmlAccessorType(XmlAccessType.FIELD)
public class Config implements Serializable {

    @XmlElement(name = "property")
    @XmlElementWrapper(name = "properties")
    private List<Property> properties = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Property implements Serializable {

        @XmlAttribute(name = "name", required = true)
        private String name;

        @XmlAttribute(name = "value", required = true)
        private String value;

        @XmlElement(name = "option")
        @XmlElementWrapper(name = "options")
        private List<String> options = new ArrayList<>();

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Property other = (Property) obj;
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
    }

    public static Config create(ConfigDocument configData) {
        Config config = new Config();
        configData.getProperties().forEach(p -> config.properties.add(new Property(p.getName(), p.getValue(), p.getOptions())));
        return config;
    }
}
