
package org.jboss.qa.tool.saatr.repo.build;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.Status;
import org.jboss.qa.tool.saatr.domain.build.ConsoleTextDocument;
import org.jboss.qa.tool.saatr.domain.build.TestcaseDocument;
import org.jboss.qa.tool.saatr.domain.build.TestsuiteDocument;
import org.jboss.qa.tool.saatr.domain.config.ConfigDocument.ConfigProperty;
import org.jboss.qa.tool.saatr.jaxb.surefire.Testsuite;
import org.jboss.qa.tool.saatr.web.comp.build.BuildProvider.BuildFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * The manual implementation parts for {@link BuildRepository}. This will automatically be
 * picked up by the Spring Data infrastructure as we follow the naming convention of
 * extending the core repository interface's name with {@code Impl}.
 * 
 * @author dsimko@redhat.com
 */
@Component
@Slf4j
class BuildRepositoryImpl implements BuildRepositoryCustom {

    private final MongoTemplate template;

    @Autowired
    public BuildRepositoryImpl(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public Iterator<BuildDocument> query(long first, long count, BuildFilter filter) {
        final Query query = createQueryAndApplyFilter(filter);
        query.limit((int) count);
        query.skip((int) first);
        query.with(new Sort(Sort.Direction.DESC, "id"));
        return template.find(query, BuildDocument.class).iterator();
    }

    @Override
    public long count(BuildFilter filter) {
        return template.count(createQueryAndApplyFilter(filter), BuildDocument.class);
    }

    @Override
    public void fillBuildByTestsuites(List<Testsuite> input, BuildDocument build) {
        for (Testsuite testsuite : input) {
            TestsuiteDocument testsuiteData = TestsuiteDocument.create(testsuite);

            PropertyData.create(testsuite.getProperties()).forEach(p -> {
                addIfAbsent(p, build.getSystemProperties());
            });

            build.getTestsuites().add(testsuiteData);
        }
        build.setStatus(BuildDocument.determineStatus(build.getTestsuites()));
    }

    @Override
    public void addIfAbsent(PropertyData property, Set<PropertyData> properties) {
        if (!properties.contains(property)) {
            properties.add(property);
        } else {
            Optional<PropertyData> withDifferentValue = properties.stream().filter(
                    p -> p.getName().equals(property.getName()) && !p.getValue().equals(property.getValue())).findFirst();
            if (withDifferentValue.isPresent()) {
                log.warn("Property [name = {}, value = {}] has not been added because already exists with value {}.", property.getName(), property.getValue(),
                        withDifferentValue.get().getValue());
            }
        }
    }

    @Override
    public <T extends DocumentWithProperties<?>> void addOrUpdateProperties(T document, Set<ConfigProperty> configProperties) {
        log.info("Adding or updating properties {} for {}", configProperties, document);
        if (document instanceof BuildDocument) {
            template.updateFirst(Query.query(where("id").is(document.getId())), Update.update("properties", configProperties), BuildDocument.class);
        } else if (document instanceof TestsuiteDocument) {
            template.updateFirst(Query.query(where("testsuites.id").is(document.getId())), Update.update("testsuites.$.properties", configProperties),
                    BuildDocument.class);
        } else if (document instanceof TestcaseDocument) {
            TestcaseDocument testcaseData = (TestcaseDocument) document;
            template.updateFirst(Query.query(where("testsuites.testcases.id").is(testcaseData.getId())),
                    Update.update("testsuites.$.testcases." + testcaseData.getIndex() + ".properties", configProperties), BuildDocument.class);
        }
    }

    @Override
    public TestsuiteDocument findTestsuiteById(UUID id) {
        Query query = new Query();
        query.addCriteria(where("testsuites.id").is(id));
        query.fields().include("testsuites.$");
        return template.findOne(query, BuildDocument.class).getTestsuites().get(0);
    }

    @Override
    public TestcaseDocument findTestcaseById(UUID id, int index) {
        Query query = new Query();
        query.addCriteria(where("testsuites.testcases.id").is(id));
        query.fields().include("testsuites.$.testcases");
        TestcaseDocument testcaseData = template.findOne(query, BuildDocument.class).getTestsuites().get(0).getTestcases().get(index);
        testcaseData.setIndex(index);
        return testcaseData;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<String> findDistinctVariableNames() {
        return template.getCollection(BuildDocument.COLLECTION_NAME).distinct("variables.name");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<String> findDistinctVariableValues() {
        return template.getCollection(BuildDocument.COLLECTION_NAME).distinct("variables.value");
    }

    @Override
    public String aggregate(String query) {
        Object json = JSON.parse(query);
        StringBuilder result = new StringBuilder();
        if (json instanceof BasicDBList) {
            DBCollection suites = template.getCollection(BuildDocument.COLLECTION_NAME);
            @SuppressWarnings("unchecked")
            AggregationOutput suitesIt = suites.aggregate((List<? extends DBObject>) json);
            suitesIt.results().forEach(c -> {
                result.append(c.toString() + "\n");
            });
        }
        return result.toString();
    }

    @Override
    public List<BuildDocument> findFailedWithoutAdditionalInfo() {
        Query query = Query.query(where("consoleTextId").is(null).andOperator(where("status").is(Status.Failed)));
        return template.find(query, BuildDocument.class);
    }

    @Override
    public void addConsoleText(BuildDocument buildDocument, String consoleText) {
        ConsoleTextDocument consoleTextDocument = new ConsoleTextDocument(null, consoleText);
        template.save(consoleTextDocument);
        template.updateFirst(Query.query(where("id").is(buildDocument.getId())), Update.update("consoleTextId", consoleTextDocument.getId()),
                BuildDocument.class);
    }

    @Override
    public Iterator<BuildDocument> getRoots() {
        Aggregation agg = newAggregation(group("jobCategory").count().as("numberOfChildren").sum("jobStatus").as("jobStatus"), sort(Direction.ASC, "_id"),
                project("jobStatus", "numberOfChildren").and("_id").as("jobName").andExclude("_id"));
        AggregationResults<BuildDocument> results = template.aggregate(agg, BuildDocument.COLLECTION_NAME, BuildDocument.class);
        List<BuildDocument> mappedResult = results.getMappedResults();
        return mappedResult.iterator();
    }

    public Iterator<BuildDocument> getChildren(BuildDocument parent) {
        if (parent.getJobName().contains("/") && parent.getNumberOfChildren() != null && parent.getNumberOfChildren() > 0) {
            Query query = Query.query(where("jobName").is(parent.getJobName()));
            return template.find(query, BuildDocument.class).iterator();
        } else {
            Aggregation agg = newAggregation(match(Criteria.where("jobCategory").is(parent.getJobName())),
                    group("jobName").count().as("numberOfChildren").sum("jobStatus").as("jobStatus"), sort(Direction.ASC, "_id"),
                    project("jobStatus", "numberOfChildren").and("_id").as("jobName").andExclude("_id"));
            AggregationResults<BuildDocument> results = template.aggregate(agg, BuildDocument.COLLECTION_NAME, BuildDocument.class);
            return results.getMappedResults().stream().map(b -> {
                if(b.getNumberOfChildren() > 1){
                    return b;    
                }else{
                    return template.findOne(Query.query(Criteria.where("jobName").is(b.getJobName())), BuildDocument.class);
                }
            }).iterator();
        }
    }

    private Query createQueryAndApplyFilter(BuildFilter filter) {
        Query query = new Query();
        if (filter.getBuildNumber() != null) {
            query.addCriteria(where("buildNumber").is(filter.getBuildNumber()));
        }
        if (filter.getJobName() != null) {
            query.addCriteria(where("jobName").regex(filter.getJobName() + ".*"));
        }
        if (filter.getStatus() != null) {
            query.addCriteria(where("status").is(filter.getStatus()));
        }
        if (filter.getVariableName() != null) {
            query.addCriteria(where("variables").in(new PropertyData(filter.getVariableName(), filter.getVariableValue())));
        }
        return query;
    }

}
