
package org.jboss.qa.tool.saatr.domain.hierarchical;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithID;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.Status;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter.PropertyDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@SuppressWarnings("serial")
@Document(collection = JobRunFilter.COLLECTION_NAME)
@EqualsAndHashCode(exclude="lastUsed")
public class JobRunFilter implements DocumentWithID<ObjectId> {

    public static final String COLLECTION_NAME = "jobRunFilters";

    @Id
    private ObjectId id;

    private Date created = new Date();

    private Date lastUsed = new Date();

    private String jobName;

    private String jobConfiguration;

    private Long buildNumber;

    private Status status;

    private Date createdFrom;

    private Date createdTo;

    private final List<PropertyDto> variables = new ArrayList<>();

    private final List<PropertyDto> systemParams = new ArrayList<>();

    private final List<PropertyDto> properties = new ArrayList<>();


    public static JobRunFilter create(BuildFilter buildFilter){
        JobRunFilter filter = new JobRunFilter();
        filter.setBuildNumber(buildFilter.getBuildNumber());
        filter.setCreated(buildFilter.getCreated());
        filter.setJobName(buildFilter.getJobName());
        filter.setStatus(buildFilter.getStatus());
        return filter;
    }
    
}
