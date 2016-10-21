
package org.jboss.qa.tool.saatr.repo.build;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.Build;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * A repository interface assembling CRUD functionality as well as the API to invoke the
 * methods implemented manually.
 * 
 * @author dsimko@redhat.com
 */
public interface BuildRepository extends MongoRepository<Build, ObjectId>, BuildRepositoryCustom {

}
