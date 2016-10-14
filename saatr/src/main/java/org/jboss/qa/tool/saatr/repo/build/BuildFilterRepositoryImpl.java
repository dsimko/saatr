
package org.jboss.qa.tool.saatr.repo.build;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Date;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * The manual implementation parts for {@link BuildRepository}. This will automatically be
 * picked up by the Spring Data infrastructure as we follow the naming convention of
 * extending the core repository interface's name with {@code Impl}.
 * 
 * @author dsimko@redhat.com
 */
@Component
class BuildFilterRepositoryImpl implements BuildFilterRepositoryCustom {

    private final MongoTemplate template;

    @Autowired
    public BuildFilterRepositoryImpl(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public BuildFilter findAndUpdateLastUsed(String id) {
        Query query = Query.query(where("id").is(new ObjectId(id)));
        BuildFilter buildFilter = template.findOne(query, BuildFilter.class);
        if (buildFilter != null) {
            template.updateFirst(query, Update.update("lastUsed", new Date()), BuildDocument.class);
        }
        return buildFilter;
    }
}
