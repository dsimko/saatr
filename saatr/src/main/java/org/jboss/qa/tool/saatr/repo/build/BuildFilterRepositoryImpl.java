
package org.jboss.qa.tool.saatr.repo.build;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Date;
import java.util.Iterator;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
            template.updateFirst(query, Update.update("lastUsed", new Date()), BuildFilter.class);
        }
        return buildFilter;
    }

    @Override
    public Iterator<BuildFilter> query(long first, long count, String creatorUsername) {
        final Query query = new Query();
        query.addCriteria(where("creatorUsername").is(creatorUsername));
        query.limit((int) count);
        query.skip((int) first);
        query.with(new Sort(Sort.Direction.DESC, "lastUsed"));
        return template.find(query, BuildFilter.class).iterator();
    }

    @Override
    public long count(String creatorUsername) {
        final Query query = new Query();
        query.addCriteria(where("creatorUsername").is(creatorUsername));
        return template.count(query, BuildFilter.class);
    }

    @Override
    public void saveIfNewOrChanged(BuildFilter filter) {
        if (filter.getId() == null) {
            template.save(filter);
        } else {
            BuildFilter original = template.findById(filter.getId(), BuildFilter.class);
            if (!filter.equals(original)) {
                filter.setId(null);
                filter.setCreated(new Date());
                filter.setLastUsed(new Date());
                template.save(filter);
            }
        }
    }
}
