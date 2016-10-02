
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.Status;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class BuildFilter implements Serializable, Cloneable {

    private Long buildNumber;

    private String jobName;

    private String jobCategory;

    private Status status;

    private Date createdFrom;

    private Date createdTo;

    private final List<PropertyData> variables = new ArrayList<>();

    private final List<PropertyData> systemParams = new ArrayList<>();
    
    private final List<PropertyData> properties = new ArrayList<>();
    
    @Override
    public BuildFilter clone() {
        try {
            return (BuildFilter) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
