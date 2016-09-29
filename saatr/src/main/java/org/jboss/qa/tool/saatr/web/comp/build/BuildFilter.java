
package org.jboss.qa.tool.saatr.web.comp.build;

import java.io.Serializable;
import java.util.Date;

import org.jboss.qa.tool.saatr.domain.build.BuildDocument.Status;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class BuildFilter implements Serializable, Cloneable {

    private Long buildNumber;

    private String jobName;

    private String jobCategory;

    private Status status;

    private String variableName;

    private String variableValue;
    
    private Date createdFrom;

    private Date createdTo;

    @Override
    public BuildFilter clone() {
        try {
            return (BuildFilter) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
