package org.jboss.qa.tool.saatr.service;

import static org.jboss.qa.tool.saatr.entity.Build.Status.Failed;
import static org.jboss.qa.tool.saatr.entity.Build.Status.Success;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.entity.Build.PropertyData;
import org.jboss.qa.tool.saatr.entity.ConfigData.ConfigProperty;
import org.jboss.qa.tool.saatr.entity.Persistable;
import org.jboss.qa.tool.saatr.entity.PersistableWithProperties;
import org.jboss.qa.tool.saatr.entity.TestsuiteData;
import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite;
import org.jboss.qa.tool.saatr.web.comp.build.BuildProvider.BuildFilter;
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

    public void save(Build build) {
        build.setStatus(Success);
        build.getTestsuites().forEach(ts -> {
            if (isFailed(build, ts)) {
                build.setStatus(Failed);
            }
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
        query.order("-" + Mapper.ID_KEY);
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

            PropertyData.create(testsuite.getProperties()).forEach(p -> {
                addIfAbsent(p, build.getProperties());
            });

            build.getTestsuites().add(testsuiteData);
        }
    }

    public void addIfAbsent(PropertyData property, Set<PropertyData> properties) {
        if (!properties.contains(property)) {
            properties.add(property);
        } else {
            Optional<PropertyData> withDifferentValue = properties.stream()
                    .filter(p -> p.getName().equals(property.getName()) && !p.getValue().equals(property.getValue())).findFirst();
            if (withDifferentValue.isPresent()) {
                LOG.warn("Property [name = {}, value = {}] has not been added because already exists with value {}.", property.getName(),
                        property.getValue(), withDifferentValue.get().getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends PersistableWithProperties> void addOrUpdateProperties(T persistable, Set<ConfigProperty> configProperties) {

        LOG.info("Adding or updating properties {} for {}", configProperties, persistable);

        // clear properties
        datastore.update(persistable, datastore.createUpdateOperations((Class<T>) persistable.getClass()).unset("properties"));

        Stream<PropertyData> oldWithoutNewProperties = persistable.getProperties().stream()
                .filter(p -> !configProperties.contains(new ConfigProperty(p.getName(), null, null)));

        // merge old and new properties set
        Stream<PropertyData> allProperties = Stream.concat(oldWithoutNewProperties,
                configProperties.stream().map(p -> new PropertyData(p.getName(), p.getValue())));

        // update all non null properties
        allProperties.filter(property -> property.getValue() != null).forEach(property -> {
            datastore.update(persistable, datastore.createUpdateOperations((Class<T>) persistable.getClass()).add("properties", property));
        });
    }

    public <T extends Persistable<ObjectId>> T findById(ObjectId id, Class<T> clazz) {
        return datastore.find(clazz, Mapper.ID_KEY, id).get();
    }

    private Query<Build> createQueryAndApplyFilter(BuildFilter filter) {
        final Query<Build> query = datastore.createQuery(Build.class);
        if (filter.getBuildNumber() != null) {
            query.and(query.criteria("buildNumber").equal(filter.getBuildNumber()));
        }
        if (filter.getJobName() != null) {
            query.and(query.criteria("jobName").startsWith(filter.getJobName()));
        }
        if (filter.getStatus() != null) {
            query.and(query.criteria("status").equal(filter.getStatus()));
        }
        return query;
    }

    private boolean isFailed(Build build, TestsuiteData ts) {
        return build.getStatus() == Success && (ts.getErrors() > 0 || ts.getFailures() > 0);
    }

}
