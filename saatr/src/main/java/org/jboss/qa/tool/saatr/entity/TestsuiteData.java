package org.jboss.qa.tool.saatr.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBElement;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.entity.Build.PropertyData;
import org.jboss.qa.tool.saatr.entity.TestcaseData.ErrorData;
import org.jboss.qa.tool.saatr.entity.TestcaseData.FailureData;
import org.jboss.qa.tool.saatr.entity.TestcaseData.RerunFailureData;
import org.jboss.qa.tool.saatr.entity.TestcaseData.SkippedData;
import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite;
import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite.Testcase;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class TestsuiteData implements PersistableWithProperties {

    @Id
    private ObjectId id;
    private final Set<PropertyData> properties = new TreeSet<>();
    @Reference
    private final List<TestcaseData> testcases = new ArrayList<>();
    private String name;
    private Double time;
    private Integer tests;
    private Integer errors;
    private Integer skipped;
    private Integer failures;
    private String group;

    public static TestsuiteData create(Testsuite testsuite) {

        TestsuiteData testsuiteData = new TestsuiteData();
        testsuiteData.name = testsuite.getName();
        testsuiteData.time = toDouble(testsuite.getTime());
        testsuiteData.tests = toInteger(testsuite.getTests());
        testsuiteData.errors = toInteger(testsuite.getErrors());
        testsuiteData.skipped = toInteger(testsuite.getSkipped());
        testsuiteData.failures = toInteger(testsuite.getFailures());
        testsuiteData.group = testsuite.getGroup();

        for (Testcase testcase : testsuite.getTestcase()) {
            TestcaseData testcaseData = new TestcaseData();
            testcaseData.systemOut = toString(testcase.getSystemOut());
            testcaseData.systemErr = toString(testcase.getSystemErr());
            testcaseData.name = testcase.getName();
            testcaseData.classname = testcase.getClassname();
            testcaseData.group = testcase.getGroup();
            testcaseData.time = toDouble(testcase.getTime());
            JAXBElement<Testsuite.Testcase.Error> error = testcase.getError();
            if (error != null) {
                testcaseData.error = new ErrorData();
                testcaseData.error.message = error.getValue().getMessage();
                testcaseData.error.type = error.getValue().getType();
                testcaseData.error.value = error.getValue().getValue();
            }
            JAXBElement<Testsuite.Testcase.Skipped> skipped = testcase.getSkipped();
            if (skipped != null) {
                testcaseData.skipped = new SkippedData();
                testcaseData.skipped.message = skipped.getValue().getMessage();
                testcaseData.skipped.value = skipped.getValue().getValue();
            }
            for (Testsuite.Testcase.Failure failure : testcase.getFailure()) {
                FailureData failureData = new FailureData();
                failureData.message = failure.getMessage();
                failureData.time = toDouble(failure.getTime());
                failureData.type = failure.getType();
                failureData.value = failure.getValue();
                testcaseData.failure.add(failureData);
            }
            for (Testsuite.Testcase.RerunFailure failure : testcase.getRerunFailure()) {
                RerunFailureData failureData = new RerunFailureData();
                failureData.message = failure.getMessage();
                failureData.time = toDouble(failure.getTime());
                failureData.type = failure.getType();
                failureData.value = failure.getValue();
                testcaseData.rerunFailure.add(failureData);
            }
            testsuiteData.testcases.add(testcaseData);
        }
        return testsuiteData;
    }

    private static Double toDouble(String input) {
        return input != null ? Double.valueOf(input) : null;
    }

    private static Integer toInteger(String input) {
        return input != null ? Integer.valueOf(input) : null;
    }

    private static String toString(JAXBElement<?> input) {
        return input != null && input.getValue() != null ? input.getValue().toString() : null;
    }

    @Override
    public String toString() {
        return "TestsuiteData [id=" + id + ", name=" + name + "]";
    }

}
