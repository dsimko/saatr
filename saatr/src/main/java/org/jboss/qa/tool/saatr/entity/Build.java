package org.jboss.qa.tool.saatr.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import lombok.Data;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@Entity
@Data
@SuppressWarnings("serial")
public class Build implements Serializable {

    @Id
    private ObjectId id;
    private String jobName;
    private Long buildNumber;
    private Long timestamp;
    private Long duration;
    private final Set<Property> props = new HashSet<>();
    private final Set<TestsuiteData> testsuites = new HashSet<>();

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

}
