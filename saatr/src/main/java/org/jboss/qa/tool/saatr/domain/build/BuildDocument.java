
package org.jboss.qa.tool.saatr.domain.build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithID;
import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.jaxb.surefire.Testsuite.Properties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An entity representing an {@link BuildDocument}.
 * 
 * @author dsimko@redhat.com
 */
@Data
@Document(collection = BuildDocument.COLLECTION_NAME)
@SuppressWarnings("serial")
public class BuildDocument implements DocumentWithProperties<ObjectId>, DocumentWithID<ObjectId> {

    public static final String COLLECTION_NAME = "builds";

    public static enum Status {
        Success, SuccessWithFlakyFailure, SuccessWithFlakyError, Failed
    }

    @Id
    private ObjectId id;

    @Indexed
    private String jobName;

    private Long buildNumber;

    private Long timestamp;

    @Indexed
    private Status status;

    private Long duration;

    private ObjectId consoleTextId;

    private final Set<PropertyData> systemProperties = new TreeSet<>();

    private final Set<PropertyData> variables = new TreeSet<>();

    private final Set<PropertyData> properties = new TreeSet<>();

    private final List<TestsuiteDocument> testsuites = new ArrayList<>();

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

    public static Status determineStatus(List<TestsuiteDocument> testsuites) {
        Status status = Status.Success;
        for (TestsuiteDocument testsuiteData : testsuites) {
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
        BuildDocument other = (BuildDocument) obj;
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
