
package org.jboss.qa.tool.saatr.domain.hierarchical;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithID;
import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.Status;
import org.jboss.qa.tool.saatr.domain.build.TestsuiteDocument;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * An entity representing a {@link JobRun}.
 * 
 * @author dsimko@redhat.com
 */
@Data
@Document(collection = JobRun.COLLECTION_NAME)
@SuppressWarnings("serial")
public class JobRun implements DocumentWithProperties<ObjectId>, DocumentWithID<ObjectId> {

    public static final String COLLECTION_NAME = "jobRuns";

    @Id
    private ObjectId id;

    @Indexed
    private String name;

    @Indexed
    private String configuration;

    @Indexed
    private Status status;

    @Indexed
    private Integer statusWeight;

    @Indexed
    private String fullName;

    private Long buildNumber;

    private Date created = new Date();

    private Long duration;

    private Integer failedTestsuitesCount;

    private Integer errorTestsuitesCount;

    private Integer testcasesCount;

    private Integer failedTestcasesCount;

    private Integer errorTestcasesCount;

    private Integer skippedTestcasesCount;

    private ObjectId consoleTextId;

    //TODO rename
    private final Set<PropertyData> systemProperties = new TreeSet<>();

    private final Set<PropertyData> variables = new TreeSet<>();

    private final Set<PropertyData> properties = new TreeSet<>();

    private final List<TestsuiteDocument> testsuites = new ArrayList<>();

    private Integer childCount;

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
        JobRun other = (JobRun) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
