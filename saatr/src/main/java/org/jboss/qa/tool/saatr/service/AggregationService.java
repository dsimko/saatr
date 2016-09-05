package org.jboss.qa.tool.saatr.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.entity.TestcaseData;
import org.jboss.qa.tool.saatr.entity.TestsuiteData;
import org.jboss.qa.tool.saatr.web.page.AggregationPage.CollectionType;
import org.jboss.qa.tool.saatr.web.page.AggregationPage.PredefinedPipelines;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBList;
import com.mongodb.Block;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

/**
 * @author dsimko@redhat.com
 */
@Component
public class AggregationService {

    @Autowired
    private MongoDatabase mongoDatabase;

    // TODO move to mongo ------------------
    public static final List<CollectionType> COLLECTIONS = Arrays.asList(new CollectionType("Build", Build.class),
            new CollectionType("Testsuite", TestsuiteData.class), new CollectionType("Testcase", TestcaseData.class));
    public static final Map<Class<?>, List<PredefinedPipelines>> PREDEFINED_PIPELINES = new HashMap<>();

    static {
        List<PredefinedPipelines> buildQueries = new ArrayList<>();
        buildQueries.add(new PredefinedPipelines("The most failing job", createMostFailingJobPipelines()));
        PREDEFINED_PIPELINES.put(Build.class, buildQueries);
        List<PredefinedPipelines> testsuiteQueries = new ArrayList<>();
        testsuiteQueries.add(new PredefinedPipelines("The most failing testsuite", createMostFailingTestsuitePipelines()));
        PREDEFINED_PIPELINES.put(TestsuiteData.class, testsuiteQueries);
        List<PredefinedPipelines> testcaseQueries = new ArrayList<>();
        testcaseQueries.add(new PredefinedPipelines("The most failing testcase", createMostFailingTestcasePipelines()));
        PREDEFINED_PIPELINES.put(TestcaseData.class, testcaseQueries);
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

    // ------move-to-mongo---------------------

    public String aggregate(String query, CollectionType database) {
        Object json = JSON.parse(query);
        StringBuilder result = new StringBuilder();
        if (json instanceof BasicDBList) {
            MongoCollection<Document> suites = mongoDatabase.getCollection(database.getType().getSimpleName());
            @SuppressWarnings("unchecked")
            AggregateIterable<Document> suitesIt = suites.aggregate((List<? extends Bson>) json);
            suitesIt.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    result.append(document.toJson() + "\n");
                }
            });
        }
        return result.toString();
    }
}
