package org.jboss.qa.tool.saatr.service;

import java.util.Iterator;
import java.util.List;

import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.entity.Build.PropertyData;
import org.jboss.qa.tool.saatr.entity.Build.TestsuiteData;
import org.jboss.qa.tool.saatr.entity.jaxb.config.Config;
import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite;
import org.jboss.qa.tool.saatr.web.component.build.BuildProvider.BuildFilter;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dsimko@redhat.com
 */
@Component
public class BuildService {

    private static final Logger LOG = LoggerFactory.getLogger(BuildService.class);

    @Autowired
    private Datastore datastore;

    public void save(Build build) {
        datastore.save(build);
        LOG.info("Build successfully stored in MongoDB.");
    }

    public void update(Build build, List<Config.Property> properties) {
        Query<Build> updateQuery = datastore.createQuery(Build.class).field(Mapper.ID_KEY).equal(build.getId());
        for (Config.Property property : properties) {
            UpdateOperations<Build> ops = datastore.createUpdateOperations(Build.class).add("properties",
                    new PropertyData(property.getName(), property.getValue()), true);
            datastore.update(updateQuery, ops);
        }
    }

    public Iterator<Build> query(long first, long count, BuildFilter filter) {
        final Query<Build> query = createQueryAndApplyFilter(filter);
        query.limit((int) count);
        query.offset((int) first);
        return query.iterator();
    }

    public long count(BuildFilter filter) {
        return datastore.getCount(createQueryAndApplyFilter(filter));
    }

    public void deleteAll() {
        datastore.delete(datastore.createQuery(Build.class));
    }

    public void fillBuildByTestsuites(List<Testsuite> input, Build build) {
        for (Testsuite testsuite : input) {
            TestsuiteData testsuiteData = TestsuiteData.create(testsuite);
            // only first testsuite properties are added to the build
            if (build.getTestsuites().isEmpty()) {
                build.getProperties().addAll(PropertyData.create(testsuite.getProperties()));
            }
            build.getTestsuites().add(testsuiteData);
        }
    }

    private Query<Build> createQueryAndApplyFilter(BuildFilter filter) {
        final Query<Build> query = datastore.createQuery(Build.class);
        if (filter.getBuildNumber() != null) {
            query.and(query.criteria("buildNumber").equal(filter.getBuildNumber()));
        }
        return query;
    }

}
