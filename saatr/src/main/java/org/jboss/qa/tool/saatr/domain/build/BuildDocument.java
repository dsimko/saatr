
package org.jboss.qa.tool.saatr.domain.build;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithID;
import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.domain.build.Build.Status;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

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

    @Id
    private ObjectId id;

    @Indexed
    private String jobCategory;

    @Indexed
    private Integer jobStatus;

    private Integer numberOfChildren;

    @Indexed
    private String jobName;

    private Long buildNumber;

    private Long timestamp;

    private Date created = new Date();

    @Indexed
    private Status status;

    private Long duration;

    private Integer failedTestsuites;

    private Integer errorTestsuites;

    private Integer testcases;

    private Integer failedTestcases;

    private Integer errorTestcases;

    private Integer skippedTestcases;

    private ObjectId consoleTextId;

    private final Set<BuildProperty> systemProperties = new TreeSet<>();

    private final Set<BuildProperty> variables = new TreeSet<>();

    private final Set<BuildProperty> properties = new TreeSet<>();

    private final List<TestSuite> testsuites = new ArrayList<>();

    public void setJobName(String jobName) {
        if (jobName == null) {
            this.jobCategory = null;
        } else {
            if (jobName.contains("/")) {
                this.jobName = jobName;
            } else {
                this.jobName = jobName + "/";
            }
            this.jobCategory = this.jobName.substring(0, this.jobName.indexOf("/"));
        }
    }

    public void setStatus(Status status) {
        this.status = status;
        if (status == null) {
            this.jobStatus = 0;
        } else {
            this.jobStatus = status.getWeight();
        }
    }

    public static Status determineStatus(List<TestSuite> testsuites) {
        Status status = Status.Success;
        for (TestSuite testsuiteData : testsuites) {
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
