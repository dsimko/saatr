
package org.jboss.qa.tool.saatr.domain.build;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = AdditionalInfoDocument.COLLECTION_NAME)
@SuppressWarnings("serial")
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalInfoDocument implements DocumentWithID<ObjectId> {

    public static final String COLLECTION_NAME = "additionalInfo";

    @Id
    private ObjectId id;

    private String consoleText;

}
