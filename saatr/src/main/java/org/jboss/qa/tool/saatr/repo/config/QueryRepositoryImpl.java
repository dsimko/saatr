
package org.jboss.qa.tool.saatr.repo.config;

import java.util.List;

import org.jboss.qa.tool.saatr.domain.config.QueryDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * The manual implementation parts for {@link QueryRepository}. This will automatically be
 * picked up by the Spring Data infrastructure as we follow the naming convention of
 * extending the core repository interface's name with {@code Impl}.
 * 
 * @author dsimko@redhat.com
 */
@Component
class QueryRepositoryImpl implements QueryRepositoryCustom {

    private final MongoTemplate template;

    @Autowired
    public QueryRepositoryImpl(MongoTemplate mongoTemplate) {
        this.template = mongoTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> findDistinctCategories() {
        return template.getCollection(QueryDocument.COLLECTION_NAME).distinct("category");
    }

}
