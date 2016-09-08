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
        Success, SuccessWithFlakyFailure, SuccessWithFlakyError, Failed
    }

    @Id
    private ObjectId id;
    private String jobName;
    private Long buildNumber;
    private Long timestamp;
    private Status status;
    private Long duration;
    private final Set<PropertyData> systemProperties = new TreeSet<>();
    private final Set<PropertyData> variables = new TreeSet<>();
    private final Set<PropertyData> properties = new TreeSet<>();

    @Reference(idOnly = true)
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

    public static Status determineStatus(List<TestsuiteData> testsuites) {
        Status status = Status.Success;
        for (TestsuiteData testsuiteData : testsuites) {
            switch (testsuiteData.getStatus()) {
            case Failure:
            case Error:
                return Status.Failed;
            case FlakyFailure:
                status = Status.SuccessWithFlakyFailure;
                break;
            case FlakyError:
                if (status != Status.SuccessWithFlakyFailure)
                    status = Status.SuccessWithFlakyError;
                break;
            case Success:
                break;
            }
        }
        return status;
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
