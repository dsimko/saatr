
package org.jboss.qa.tool.saatr.repo.config;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.config.ConfigDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigRepository extends MongoRepository<ConfigDocument, ObjectId>, ConfigRepositoryCustom {

}
