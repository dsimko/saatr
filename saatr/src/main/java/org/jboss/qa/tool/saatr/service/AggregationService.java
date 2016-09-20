package org.jboss.qa.tool.saatr.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.domain.build.TestcaseDocument;
import org.jboss.qa.tool.saatr.domain.build.TestsuiteDocument;
import org.jboss.qa.tool.saatr.web.page.AggregationPage.CollectionType;
import org.jboss.qa.tool.saatr.web.page.AggregationPage.PredefinedPipelines;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * @author dsimko@redhat.com
 */
@Component
public class AggregationService {

    // @Autowired
    // private MongoDatabase mongoDatabase;

    @Autowired
    private MongoTemplate operations;

    // TODO move to mongo ------------------
    public static final List<CollectionType> COLLECTIONS = Arrays.asList(new CollectionType("Build", BuildDocument.class),
            new CollectionType("Testsuite", TestsuiteDocument.class), new CollectionType("Testcase", TestcaseDocument.class));
    public static final Map<Class<?>, List<PredefinedPipelines>> PREDEFINED_PIPELINES = new HashMap<>();

    static {
        List<PredefinedPipelines> buildQueries = new ArrayList<>();
        buildQueries.add(new PredefinedPipelines("The most failing job", createMostFailingJobPipelines()));
        buildQueries.add(new PredefinedPipelines("Find jobs which contains certain Testcase", createFindJobsWithTestcasePipelines()));
        PREDEFINED_PIPELINES.put(BuildDocument.class, buildQueries);
        List<PredefinedPipelines> testsuiteQueries = new ArrayList<>();
        testsuiteQueries.add(new PredefinedPipelines("The most failing testsuite", createMostFailingTestsuitePipelines()));
        PREDEFINED_PIPELINES.put(TestsuiteDocument.class, testsuiteQueries);
        List<PredefinedPipelines> testcaseQueries = new ArrayList<>();
        testcaseQueries.add(new PredefinedPipelines("The most failing testcase", createMostFailingTestcasePipelines()));
        PREDEFINED_PIPELINES.put(TestcaseDocument.class, testcaseQueries);
    }

    private static String createMostFailingJobPipelines() {
        StringBuilder builder = new StringBuilder();
        builder.append("[\n");
        builder.append(" { $match: { status : { $ne: \"Success\" } } },\n");
        builder.append(" { $group: {_id: \"$jobName\", count: {$sum: 1 } } },\n");
        builder.append(" { $sort: {count : -1 } },\n");
        builder.append(" { $limit: 20 },\n");
        builder.append(" { $project: {Name : \"$_id\", Count: \"$count\", _id : 0 } }\n");
        builder.append("]");
        return builder.toString();
    }

    private static String createMostFailingTestsuitePipelines() {
        StringBuilder builder = new StringBuilder();
        builder.append("[\n");
        builder.append(" { $match: { $or: [ { \"status\" : { $eq: \"Failure\" } }, { \"status\" : { $eq: \"Error\" } } ] } },\n");
        builder.append(" { $group: {_id: \"$name\", count: {$sum: 1 } } },\n");
        builder.append(" { $sort: {count : -1 } },\n");
        builder.append(" { $limit: 20 },\n");
        builder.append(" { $project: {Name : \"$_id\", Count: \"$count\", _id : 0 } }\n");
        builder.append("]");
        return builder.toString();
    }

    private static String createMostFailingTestcasePipelines() {
        StringBuilder builder = new StringBuilder();
        builder.append("[\n");
        builder.append(" { $match: { $or: [ { \"status\" : { $eq: \"Failure\" } }, { \"status\" : { $eq: \"Error\" } } ] } },\n");
        builder.append(" { $group: { _id: { name: \"$name\", classname: \"$classname\"  }, count: {$sum: 1 } } },\n");
        builder.append(" { $sort: {count : -1 } },\n");
        builder.append(" { $limit: 20 },\n");
        builder.append(" { $project: {ClassName : \"$_id.classname\", Name : \"$_id.name\", Count: \"$count\", _id : 0 } }\n");
        builder.append("]");
        return builder.toString();
    }

    private static String createFindJobsWithTestcasePipelines() {
        StringBuilder builder = new StringBuilder();
        builder.append("[\n");
        builder.append(" { $unwind: \"$testsuites\" },\n");
        builder.append(" { $lookup:\n");
        builder.append("   { from: \"TestsuiteData\",\n");
        builder.append("     localField: \"testsuites\",\n");
        builder.append("     foreignField: \"_id\",\n");
        builder.append("     as: \"ts\"\n");
        builder.append("   }\n");
        builder.append(" },\n");
        builder.append(" { $match: { \"ts.name\" : { $eq: \"@TESTCASE_NAME@\" } } },\n");
        builder.append(" { $group: { _id: { jobName: \"$jobName\", testcaseName: \"$ts.name\" } } },\n");
        builder.append(" { $project: {Job_Name: \"$_id.jobName\", TestcaseName: \"$_id.testcaseName\", _id : 0 } }\n");
        builder.append("]");
        return builder.toString();
    }

    // ------move-to-mongo---------------------

    public String aggregate(String query, CollectionType database) {

        // Aggregation agg = newAggregation(Aggregation.unwind("testsuites"),
        // Aggregation.lookup("testsuiteData", "testsuites._id", "_id", "ts"),
        // match(Criteria.where("ts.name")
        // .is("org.jboss.as.test.jbossts.crashrec.higherload.test.TxInconsistentWhenHigherLoadTestCase(jta)")),
        // group("jobName").count().as("total"));
        //
        // AggregationResults<Build> builds = operations.aggregate(agg, "build",
        // Build.class);
        // return builds.getRawResults().toString();

        // AggregationResults<HostingCount> groupResults =
        // mongoTemplate.aggregate(agg, Domain.class, HostingCount.class);
        // List<HostingCount> result = groupResults.getMappedResults();

        Object json = JSON.parse(query);
        StringBuilder result = new StringBuilder();
        if (json instanceof BasicDBList) {
            DBCollection suites = operations.getCollection("build");
            @SuppressWarnings("unchecked")
            AggregationOutput suitesIt = suites.aggregate((List<? extends DBObject>) json);
            suitesIt.results().forEach(c -> {
                result.append(c.toString() + "\n");
            });
            // suitesIt.results().forEach(new Block<Document>() {
            // @Override
            // public void apply(final Document document) {
            // result.append(document.toJson() + "\n");
            // }
            // });
        }
        return result.toString();
    }
}
