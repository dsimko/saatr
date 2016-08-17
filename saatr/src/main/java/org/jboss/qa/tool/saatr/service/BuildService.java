package org.jboss.qa.tool.saatr.service;

import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.entity.Build.PropertyData;
import org.jboss.qa.tool.saatr.entity.Build.TestsuiteData;
import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite;
import org.jboss.qa.tool.saatr.web.component.build.BuildProvider.BuildFilter;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
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

    public Build findById(ObjectId id) {
        return datastore.find(Build.class, Mapper.ID_KEY, id).get();
    }

    public void save(Build build) {
        build.getTestsuites().forEach(ts -> {
            ts.getTestcases().forEach(tc -> {
                datastore.save(tc);
            });
            datastore.save(ts);
        });
        datastore.save(build);
        LOG.info("Build successfully stored in MongoDB.");
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
