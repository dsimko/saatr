
package org.jboss.qa.tool.saatr.repo.build;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.ConsoleTextDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * A repository interface assembling CRUD functionality.
 * 
 * @author dsimko@redhat.com
 */
public interface ConsoleTextRepository extends MongoRepository<ConsoleTextDocument, ObjectId> {

}
