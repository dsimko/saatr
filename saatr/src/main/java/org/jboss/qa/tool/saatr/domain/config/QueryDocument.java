
package org.jboss.qa.tool.saatr.domain.config;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An entity representing an {@link QueryDocument}.
 * 
 * @author dsimko@redhat.com
 */
@Data
@Document(collection = QueryDocument.COLLECTION_NAME)
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("serial")
public class QueryDocument implements DocumentWithID<ObjectId> {

    public static final String COLLECTION_NAME = "queries";

    @Id
    private ObjectId id;

    private String category;

    private String name;

    private String query;

    public static final class DataInitializer {

        public static List<QueryDocument> createInitalData() {
            List<QueryDocument> queries = new ArrayList<>();
            queries.add(new QueryDocument(null, "Job", "The most failing job", createMostFailingJobPipelines()));
            queries.add(new QueryDocument(null, "Job", "Find jobs which contains certain Testcase", createFindJobsWithTestcasePipelines()));
            queries.add(new QueryDocument(null, "Testsuite", "The most failing testsuite", createMostFailingTestsuitePipelines()));
            queries.add(new QueryDocument(null, "Testcase", "The most failing testcase", createMostFailingTestcasePipelines()));
            return queries;
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
            builder.append(" { $unwind: \"$testsuites\" },\n");
            builder.append(" { $match: { $or: [ { \"testsuites.status\" : { $eq: \"Failure\" } }, { \"testsuites.status\" : { $eq: \"Error\" } } ] } },\n");
            builder.append(" { $group: {_id: \"$testsuites.name\", count: {$sum: 1 } } },\n");
            builder.append(" { $sort: {count : -1 } },\n");
            builder.append(" { $limit: 20 },\n");
            builder.append(" { $project: {Name : \"$_id\", Count: \"$count\", _id : 0 } }\n");
            builder.append("]");
            return builder.toString();
        }

        private static String createMostFailingTestcasePipelines() {
            StringBuilder builder = new StringBuilder();
            builder.append("[\n");
            builder.append(" { $unwind: \"$testsuites\" },\n");
            builder.append(" { $unwind: \"$testsuites.testcases\" },\n");
            builder.append(
                    " { $match: { $or: [ { \"testsuites.testcases.status\" : { $eq: \"Failure\" } }, { \"testsuites.testcases.status\" : { $eq: \"Error\" } } ] } },\n");
            builder.append(
                    " { $group: { _id: { name: \"$testsuites.testcases.name\", classname: \"$testsuites.testcases.classname\"  }, count: {$sum: 1 } } },\n");
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
            builder.append(" { $match: { \"testsuites.name\" : { $regex: \".*TESTCASE_NAME.*\" } } },\n");
            builder.append(" { $group: { _id: { jobName: \"$jobName\", testcaseName: \"$testsuites.name\" } } },\n");
            builder.append(" { $project: {Job_Name: \"$_id.jobName\", _id : 0 } }\n");
            builder.append("]");
            return builder.toString();
        }
    }
}
