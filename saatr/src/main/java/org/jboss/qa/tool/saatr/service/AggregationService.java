
package org.jboss.qa.tool.saatr.service;

/**
 * @author dsimko@redhat.com
 */
@Deprecated
public class AggregationService {

//    static {
//        List<PredefinedPipelines> buildQueries = new ArrayList<>();
//        buildQueries.add(new PredefinedPipelines("The most failing job", createMostFailingJobPipelines()));
//        buildQueries.add(new PredefinedPipelines("Find jobs which contains certain Testcase", createFindJobsWithTestcasePipelines()));
//        List<PredefinedPipelines> testsuiteQueries = new ArrayList<>();
//        testsuiteQueries.add(new PredefinedPipelines("The most failing testsuite", createMostFailingTestsuitePipelines()));
//        List<PredefinedPipelines> testcaseQueries = new ArrayList<>();
//        testcaseQueries.add(new PredefinedPipelines("The most failing testcase", createMostFailingTestcasePipelines()));
//    }

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
        builder.append(" { $group: { _id: { name: \"$testsuites.testcases.name\", classname: \"$testsuites.testcases.classname\"  }, count: {$sum: 1 } } },\n");
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
