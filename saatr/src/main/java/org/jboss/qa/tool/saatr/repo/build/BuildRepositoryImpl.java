
package org.jboss.qa.tool.saatr.repo.build;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.domain.build.Build.Status;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter.PropertyDto;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter.PropertyDto.Operation;
import org.jboss.qa.tool.saatr.domain.build.BuildProperty;
import org.jboss.qa.tool.saatr.domain.build.ConsoleText;
import org.jboss.qa.tool.saatr.domain.build.TestCase;
import org.jboss.qa.tool.saatr.domain.build.TestSuite;
import org.jboss.qa.tool.saatr.jaxb.surefire.Testsuite;
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
import com.mongodb.BasicDBObject;
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
    public Iterator<Build> query(long first, long count, BuildFilter filter) {
        final Query query = new Query();
        query.limit((int) count);
        query.skip((int) first);
        query.with(new Sort(Sort.Direction.DESC, "id"));
        query.addCriteria(createCriteria(filter, false));
        return template.find(query, Build.class).iterator();
    }

    @Override
    public long count(BuildFilter filter) {
        return template.count(Query.query(createCriteria(filter, false)), Build.class);
    }

    @Override
    public void fillBuildByTestsuites(List<Testsuite> input, Build build) {
        for (Testsuite testsuite : input) {
            TestSuite testsuiteData = TestSuite.create(testsuite);

            BuildProperty.create(testsuite.getProperties()).forEach(p -> {
                addIfAbsent(p, build.getSystemProperties());
            });

            build.getTestsuites().add(testsuiteData);
        }
        build.setStatus(Build.determineStatus(build.getTestsuites()));
        calculateStatistics(build);
    }

    private void calculateStatistics(Build build) {
        int failedTestsuites = 0, errorTestsuites = 0, testcases = 0, failedTestcases = 0, errorTestcases = 0, skippedTestcases = 0;
        for (TestSuite testsuite : build.getTestsuites()) {
            if (testsuite.getStatus() == TestSuite.Status.Error) {
                errorTestsuites++;
            }
            if (testsuite.getStatus() == TestSuite.Status.Failure) {
                failedTestsuites++;
            }
            for (TestCase testcase : testsuite.getTestcases()) {
                testcases++;
                if (testcase.getStatus() == TestCase.Status.Error) {
                    errorTestcases++;
                }
                if (testcase.getStatus() == TestCase.Status.Failure) {
                    failedTestcases++;
                }
                if (testcase.getStatus() == TestCase.Status.Skipped) {
                    skippedTestcases++;
                }
            }
        }
        build.setFailedTestsuitesCount(failedTestsuites);
        build.setErrorTestsuitesCount(errorTestsuites);
        build.setTotalTestsuitesCount(build.getTestsuites().size());
        build.setTotalTestcasesCount(testcases);
        build.setFailedTestcasesCount(failedTestcases);
        build.setErrorTestcasesCount(errorTestcases);
        build.setSkippedTestcasesCount(skippedTestcases);
    }

    @Override
    public void addIfAbsent(BuildProperty property, Set<BuildProperty> properties) {
        if (!properties.contains(property)) {
            properties.add(property);
        } else {
            Optional<BuildProperty> withDifferentValue = properties.stream().filter(
                    p -> p.getName().equals(property.getName()) && !p.getValue().equals(property.getValue())).findFirst();
            if (withDifferentValue.isPresent()) {
                log.warn("Property [name = {}, value = {}] has not been added because already exists with value {}.", property.getName(), property.getValue(),
                        withDifferentValue.get().getValue());
            }
        }
    }

    @Override
    public <T extends DocumentWithProperties<?>> void addOrUpdateProperties(T document, Set<BuildProperty> properties) {
        log.info("Adding or updating properties {} for {}", properties, document);
        List<BuildProperty> filteredProperties = properties.stream().filter(c -> c.getValue() != null).collect(Collectors.toList());
        if (document instanceof Build) {
            template.updateFirst(Query.query(where("id").is(document.getId())), Update.update("properties", filteredProperties), Build.class);
        } else if (document instanceof TestSuite) {
            template.updateFirst(Query.query(where("testsuites.id").is(document.getId())), Update.update("testsuites.$.properties", filteredProperties),
                    Build.class);
        } else if (document instanceof TestCase) {
            TestCase testcaseData = (TestCase) document;
            template.updateFirst(Query.query(where("testsuites.testcases.id").is(testcaseData.getId())),
                    Update.update("testsuites.$.testcases." + testcaseData.getIndex() + ".properties", filteredProperties), Build.class);
        }
    }

    @Override
    public TestSuite findTestsuiteById(UUID id) {
        Query query = new Query();
        query.addCriteria(where("testsuites.id").is(id));
        query.fields().include("testsuites.$");
        return template.findOne(query, Build.class).getTestsuites().get(0);
    }

    @Override
    public TestCase findTestcaseById(UUID id, int index) {
        Query query = new Query();
        query.addCriteria(where("testsuites.testcases.id").is(id));
        query.fields().include("testsuites.$.testcases");
        TestCase testcaseData = template.findOne(query, Build.class).getTestsuites().get(0).getTestcases().get(index);
        testcaseData.setIndex(index);
        return testcaseData;
    }

    @Override
    public Iterable<String> findDistinctVariableNames() {
        return findDistinctPropertyNames("buildProperties");
    }

    @Override
    public Iterable<String> findDistinctVariableValues(String name) {
        return findDistinctPropertyValues("buildProperties", name);
    }

    @Override
    public Iterable<String> findDistinctSystemPropertiesNames() {
        return findDistinctPropertyNames("systemProperties");
    }

    @Override
    public Iterable<String> findDistinctSystemPropertiesValues(String name) {
        return findDistinctPropertyValues("systemProperties", name);
    }

    @Override
    public Iterable<String> findDistinctPropertiesNames() {
        return findDistinctPropertyNames("properties");
    }

    @Override
    public Iterable<String> findDistinctPropertiesValues(String name) {
        return findDistinctPropertyValues("properties", name);
    }

    @SuppressWarnings("unchecked")
    private Iterable<String> findDistinctPropertyNames(String fieldName) {
        return template.getCollection(Build.COLLECTION_NAME).distinct(fieldName + ".name");
    }

    @SuppressWarnings("unchecked")
    private Iterable<String> findDistinctPropertyValues(String fieldName, String propertyName) {
        if (propertyName == null) {
            return template.getCollection(Build.COLLECTION_NAME).distinct(fieldName + ".value");
        }
        return (Iterable<String>) template.getCollection(Build.COLLECTION_NAME).distinct(fieldName).stream().filter(
                p -> p != null && propertyName.equals(((BasicDBObject) p).get("name"))).map(p -> ((BasicDBObject) p).get("value")).collect(Collectors.toList());
    }

    @Override
    public String aggregate(String query) {
        Object json = JSON.parse(query);
        StringBuilder result = new StringBuilder();
        if (json instanceof BasicDBList) {
            DBCollection suites = template.getCollection(Build.COLLECTION_NAME);
            @SuppressWarnings("unchecked")
            AggregationOutput suitesIt = suites.aggregate((List<? extends DBObject>) json);
            suitesIt.results().forEach(c -> {
                result.append(c.toString() + "\n");
            });
        }
        return result.toString();
    }

    @Override
    public List<Build> findFailedWithoutAdditionalInfo() {
        Query query = Query.query(where("consoleTextId").is(null).andOperator(where("status").is(Status.Failed)));
        query.fields().exclude("testsuites");
        return template.find(query, Build.class);
    }

    @Override
    public void addConsoleText(Build buildDocument, String consoleText) {
        ConsoleText consoleTextDocument = new ConsoleText(null, consoleText);
        template.save(consoleTextDocument);
        template.updateFirst(Query.query(where("id").is(buildDocument.getId())), Update.update("consoleTextId", consoleTextDocument.getId()), Build.class);
    }

    @Override
    public Iterator<Build> getRoots(BuildFilter filter) {
        Aggregation agg = newAggregation(match(createCriteria(filter, true)), group("name").count().as("childCount").sum("statusWeight").as("statusWeight"),
                sort(Direction.ASC, "_id"), project("statusWeight", "childCount").and("_id").as("name").andExclude("_id"));
        AggregationResults<Build> results = template.aggregate(agg, Build.COLLECTION_NAME, Build.class);
        List<Build> mappedResult = results.getMappedResults();
        return mappedResult.iterator();
    }

    @Override
    public Iterator<Build> getChildren(Build parent, final BuildFilter filter) {
        if (parent.getId() != null) {
            return Collections.emptyIterator();
        }
        if (parent.getConfiguration() == null) {
            Aggregation agg = newAggregation(match(createCriteria(filter, true, where("name").is(parent.getName()))),
                    group("name", "configuration").count().as("childCount").sum("statusWeight").as("statusWeight"), sort(Direction.ASC, "_id"),
                    project("statusWeight", "childCount").and("_id.name").as("name").and("_id.configuration").as("configuration").andExclude("_id"));
            AggregationResults<Build> results = template.aggregate(agg, Build.COLLECTION_NAME, Build.class);
            return results.getMappedResults().iterator();
        } else {
            Query query = Query.query(createCriteria(filter, false, where("name").is(parent.getName()), where("configuration").is(parent.getConfiguration())));
            query.fields().include("id");
            query.fields().include("name");
            query.fields().include("configuration");
            query.fields().include("buildNumber");
            query.fields().include("status");
            query.fields().include("failedTestsuitesCount");
            query.fields().include("errorTestsuitesCount");
            query.fields().include("totalTestcasesCount");
            query.fields().include("failedTestcasesCount");
            query.fields().include("errorTestcasesCount");
            query.fields().include("skippedTestcasesCount");
            query.fields().include("totalTestsuitesCount");
            query.fields().include("buildProperties");
            return template.find(query, Build.class, Build.COLLECTION_NAME).iterator();
        }
    }

    private Criteria createCriteria(BuildFilter filter, boolean convertoToBson, Criteria... additionaleCriterias) {
        List<Criteria> criterias = new ArrayList<>();
        for (Criteria criteria : additionaleCriterias) {
            criterias.add(criteria);
        }
        if (filter.getBuildNumber() != null) {
            criterias.add(where("buildNumber").is(filter.getBuildNumber()));
        }
        if (filter.getJobName() != null) {
            criterias.add(where("fullName").regex(filter.getJobName() + ".*"));
        }
        if (filter.getJobConfiguration() != null) {
            criterias.add(where("configuration").is(filter.getJobConfiguration()));
        }
        if (filter.getStatus() != null) {
            criterias.add(where("status").is(filter.getStatus().name()));
        }
        if (filter.getCreatedFrom() != null) {
            criterias.add(where("created").gte(filter.getCreatedFrom()));
        }
        if (filter.getCreatedTo() != null) {
            criterias.add(where("created").lte(filter.getCreatedTo()));
        }
        if (!filter.getBuildProperties().isEmpty()) {
            addPropertiesCriteria(filter.getBuildProperties(), convertoToBson, criterias, "buildProperties");
        }
        if (!filter.getSystemProperties().isEmpty()) {
            addPropertiesCriteria(filter.getSystemProperties(), convertoToBson, criterias, "systemProperties");
        }
        if (!filter.getProperties().isEmpty()) {
            addPropertiesCriteria(filter.getProperties(), convertoToBson, criterias, "properties");
        }
        if (filter.getErrorMessage() != null) {
            criterias.add(where("testsuites.testcases.error.message").is(filter.getErrorMessage()));
        }
        Criteria criteria = new Criteria();
        if (!criterias.isEmpty()) {
            criteria.andOperator(criterias.toArray(new Criteria[0]));
        }
        return criteria;
    }

    private void addPropertiesCriteria(List<PropertyDto> properties, boolean convertoToBson, List<Criteria> criterias, String fieldName) {
        for (PropertyDto property : properties) {
            if (property.getName() != null && property.getValue() != null) {
                Object o;
                if (convertoToBson) {
                    BsonDocument document = new BsonDocument();
                    document.put("name", new BsonString(property.getName()));
                    document.put("value", new BsonString(property.getValue()));
                    o = document;
                } else {
                    o = new BuildProperty(property.getName(), property.getValue());
                }
                if (property.getOperation() == Operation.EQUAL) {
                    criterias.add(where(fieldName).in(o));
                } else if (property.getOperation() == Operation.NOT_EQUAL) {
                    criterias.add(where(fieldName).nin(o));
                }
            }
        }
    }

    public ObjectId findSimilar(String errorMessage) {
        BuildFilter buildFilter = new BuildFilter();
        buildFilter.setErrorMessage(errorMessage);
        template.save(buildFilter);
        return buildFilter.getId();
    }
}
