
package org.jboss.qa.tool.saatr.repo.config;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Iterator;

import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.domain.config.ConfigDocument;
import org.jboss.qa.tool.saatr.web.comp.config.ConfigProvider.ConfigFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * @author dsimko@redhat.com
 */
@Component
class ConfigRepositoryImpl implements ConfigRepositoryCustom {

    private final MongoTemplate template;

    @Autowired
    public ConfigRepositoryImpl(MongoTemplate mongoTemplate) {
        this.template = mongoTemplate;
    }

    @Override
    public void prefillValues(ConfigDocument config, DocumentWithProperties<?> persistable) {
        config.getProperties().forEach(cp -> {
            persistable.getProperties().stream().filter(p -> p.getName().equals(cp.getName())).findFirst().ifPresent(found -> cp.setValue(found.getValue()));
        });
    }

    @Override
    public Iterator<ConfigDocument> query(long first, long count, ConfigFilter filter) {
        final Query query = createQueryAndApplyFilter(filter);
        query.limit((int) count);
        query.skip((int) first);
        query.with(new Sort(Sort.Direction.DESC, "id"));
        return template.find(query, ConfigDocument.class).iterator();
    }

    @Override
    public long count(ConfigFilter filter) {
        return template.count(createQueryAndApplyFilter(filter), ConfigDocument.class);
    }

    private Query createQueryAndApplyFilter(ConfigFilter filter) {
        Query query = new Query();
        if (filter.getName() != null) {
            query.addCriteria(where("name").regex(filter.getName() + ".*"));
        }
        return query;
    }

}
