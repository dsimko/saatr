
package org.jboss.qa.tool.saatr.domain;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@SuppressWarnings("serial")
@Document(collection = User.COLLECTION_NAME)
public class User implements DocumentWithID<ObjectId> {

    public static final String COLLECTION_NAME = "users";

    public static enum Role {
        Admin, User
    }

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String username;

    private String password;

    private Set<Role> roles = new HashSet<>();

}