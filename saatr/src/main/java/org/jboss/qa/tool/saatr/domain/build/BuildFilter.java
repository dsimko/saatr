
package org.jboss.qa.tool.saatr.domain.build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithID;
import org.jboss.qa.tool.saatr.domain.build.Build.Status;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@SuppressWarnings("serial")
@Document(collection = BuildFilter.COLLECTION_NAME)
@EqualsAndHashCode(exclude="lastUsed")
public class BuildFilter implements DocumentWithID<ObjectId>, Cloneable {

    public static final String COLLECTION_NAME = "buildFilters";

    @Id
    private ObjectId id;

    private Date created = new Date();

    private Date lastUsed = new Date();

    private Long buildNumber;

    private String jobName;

    private String jobConfiguration;

    private Status status;

    private Date createdFrom;

    private Date createdTo;

    private final List<PropertyDto> buildProperties = new ArrayList<>();

    private final List<PropertyDto> systemProperties = new ArrayList<>();

    private final List<PropertyDto> properties = new ArrayList<>();

    private String errorMessage;
    
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
