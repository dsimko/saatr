
package org.jboss.qa.tool.saatr.repo;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId>, UserRepositoryCustom {

    User findByUsername(String username);
}
