
package org.jboss.qa.tool.saatr.domain.config;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An entity representing an {@link QueryDocument}.
 * 
 * @author dsimko@redhat.com
 */
@Data
@Document(collection = QueryDocument.COLLECTION_NAME)
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("serial")
public class QueryDocument implements DocumentWithID<ObjectId> {

    public static final String COLLECTION_NAME = "queries";

    @Id
    private ObjectId id;
    private String name;
    private String category;
    private String query;

}
