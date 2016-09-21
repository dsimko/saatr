
package org.jboss.qa.tool.saatr.repo.build;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BuildRepository extends MongoRepository<BuildDocument, ObjectId>, BuildRepositoryCustom {

}
