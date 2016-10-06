
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.qa.tool.saatr.domain.build.BuildDocument.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@SuppressWarnings("serial")
public class BuildFilter implements Serializable, Cloneable {

    private Long buildNumber;

    private String jobName;

    private String jobCategory;

    private Status status;

    private Date createdFrom;

    private Date createdTo;

    private final List<PropertyDto> variables = new ArrayList<>();

    private final List<PropertyDto> systemParams = new ArrayList<>();

    private final List<PropertyDto> properties = new ArrayList<>();

    @Override
    public BuildFilter clone() {
        try {
            return (BuildFilter) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropertyDto implements Serializable {

        public static enum Operation {
            EQUAL("="), NOT_EQUAL("!=");

            final String label;

            private Operation(String label) {
                this.label = label;
            }

            public String getLabel() {
                return label;
            }
        }

        private String name;

        private String value;

        private Operation operation = Operation.EQUAL;
    }

}
