package org.jboss.qa.tool.saatr.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite.Properties;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@Entity
@Data
@SuppressWarnings("serial")
public class Build implements PersistableWithProperties {

    public static enum Status {
        Success, Failed
    }

    @Id
    private ObjectId id;
    private String jobName;
    private Long buildNumber;
    private Long timestamp;
    private Status status;
    private Long duration;
    private final Set<PropertyData> properties = new TreeSet<>();
    @Reference
    private final List<TestsuiteData> testsuites = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropertyData implements Serializable, Comparable<PropertyData> {

        private String name;
        private String value;

        public static List<PropertyData> create(List<Properties> properties) {
            List<PropertyData> list = new ArrayList<>();
            for (Properties props : properties) {
                for (Properties.Property prop : props.getProperty()) {
                    list.add(new PropertyData(prop.getName(), prop.getValue()));
                }
            }
            return list;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PropertyData other = (PropertyData) obj;
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
        public int compareTo(PropertyData o) {
            if (this.name == null || o == null) {
                return 0;
            }
            return this.name.compareToIgnoreCase(o.name);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Build other = (Build) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Build [id=" + id + ", jobName=" + jobName + ", buildNumber=" + buildNumber + "]";
    }

}
