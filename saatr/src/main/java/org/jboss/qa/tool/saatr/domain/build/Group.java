
package org.jboss.qa.tool.saatr.domain.build;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = Group.COLLECTION_NAME)
@SuppressWarnings("serial")
public class Group implements DocumentWithID<ObjectId> {

    public static final String COLLECTION_NAME = "groups";

    @Id
    private ObjectId id;

    @Indexed
    private String name;

}
