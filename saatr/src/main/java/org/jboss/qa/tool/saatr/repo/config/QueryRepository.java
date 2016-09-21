
package org.jboss.qa.tool.saatr.repo.config;

import java.util.List;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.config.QueryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * A repository interface assembling CRUD functionality as well as the API to invoke the
 * methods implemented manually.
 * 
 * @author dsimko@redhat.com
 */
public interface QueryRepository extends MongoRepository<QueryDocument, ObjectId>, QueryRepositoryCustom {

    List<QueryDocument> findByCategory(String category);

}
