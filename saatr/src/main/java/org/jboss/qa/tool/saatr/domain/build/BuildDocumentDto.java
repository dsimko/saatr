
package org.jboss.qa.tool.saatr.domain.build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.Status;

import lombok.Data;

/**
 * An entity representing an {@link BuildDocumentDto}.
 * 
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
@Data
public class BuildDocumentDto implements Serializable {

    private Object id;
    
    private int numberOfChildren;

    private String jobCategory;

    private String jobName;

    private Integer jobStatus;

    private Long buildNumber;

    private Long timestamp;

    private Status status;

    private Long duration;

    private ObjectId consoleTextId;

    private final Set<PropertyData> systemProperties = new TreeSet<>();

    private final Set<PropertyData> variables = new TreeSet<>();

    private final Set<PropertyData> properties = new TreeSet<>();

    private final List<TestsuiteDocument> testsuites = new ArrayList<>();

    
    public BuildDocumentDto() {
    }
    
    public BuildDocumentDto(BuildDocument buildDocument, int numberOfChildren) {
        this.id = buildDocument.getJobName();
        this.numberOfChildren = numberOfChildren;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

}
