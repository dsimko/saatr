package org.jboss.qa.tool.saatr.entity;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBElement;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.entity.Build.PropertyData;
import org.jboss.qa.tool.saatr.entity.TestcaseData.FailureData;
import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite;
import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite.Testcase;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import org.w3c.dom.Element;

import lombok.Data;

@Data
@Entity
@SuppressWarnings("serial")
public class TestsuiteData implements PersistableWithProperties, Comparable<TestsuiteData> {

    public static enum Status {
        Success, FlakyError, FlakyFailure, Error, Failure
    }

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
    private Status status;

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
            testcaseData.status = TestcaseData.determineStatus(testcase);
            JAXBElement<Testsuite.Testcase.Error> error = testcase.getError();
            if (error != null) {
                testcaseData.error = new FailureData();
                testcaseData.error.message = error.getValue().getMessage();
                testcaseData.error.type = error.getValue().getType();
                testcaseData.error.value = error.getValue().getValue();
            }
            JAXBElement<Testsuite.Testcase.Skipped> skipped = testcase.getSkipped();
            if (skipped != null) {
                testcaseData.skipped = new FailureData();
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
            for (Testsuite.Testcase.FlakyFailure failure : testcase.getFlakyFailure()) {
                FailureData failureData = new FailureData();
                failureData.message = failure.getMessage();
                failureData.time = toDouble(failure.getTime());
                failureData.type = failure.getType();
                failureData.value = failure.getValue();
                testcaseData.flakyFailures.add(failureData);
            }
            for (Testsuite.Testcase.FlakyError failure : testcase.getFlakyError()) {
                FailureData failureData = new FailureData();
                failureData.message = failure.getMessage();
                failureData.time = toDouble(failure.getTime());
                failureData.type = failure.getType();
                failureData.value = failure.getValue();
                testcaseData.flakyErrors.add(failureData);
            }
            for (Testsuite.Testcase.RerunFailure failure : testcase.getRerunFailure()) {
                FailureData failureData = new FailureData();
                failureData.message = failure.getMessage();
                failureData.time = toDouble(failure.getTime());
                failureData.type = failure.getType();
                failureData.value = failure.getValue();
                testcaseData.rerunFailure.add(failureData);
            }
            testsuiteData.testcases.add(testcaseData);
        }
        testsuiteData.status = determineStatus(testsuiteData.testcases);
        return testsuiteData;
    }

    private static Status determineStatus(List<TestcaseData> testcases) {
        Status status = Status.Success;
        for (TestcaseData testcaseData : testcases) {
            switch (testcaseData.getStatus()) {
            case Failure:
                return Status.Failure;
            case Error:
                status = Status.Error;
                break;
            case FlakyFailure:
                if (status != Status.Error)
                    status = Status.FlakyFailure;
                break;
            case FlakyError:
                if (status != Status.Error && status != Status.FlakyFailure)
                    status = Status.FlakyError;
                break;
            case Skipped:
            case Success:
            }
        }
        return status;
    }

    private static Double toDouble(String input) {
        if (input == null) {
            return null;
        }
        try {
            return NumberFormat.getInstance().parse(input).doubleValue();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static Integer toInteger(String input) {
        return input != null ? Integer.valueOf(input) : null;
    }

    private static String toString(JAXBElement<?> input) {
        if (input != null) {
            Object value = input.getValue();
            if (value instanceof Element) {
                Element element = (Element) value;
                return element.getTextContent();
            } else {
                throw new IllegalArgumentException("Input not known " + input);
            }
        }
        return null;
    }

    @Override
    public int compareTo(TestsuiteData o) {
        if (this.equals(o) || status == null || status == o.status) {
            return 0;
        }
        if (o.status == null) {
            return 1;
        }
        return o.status.ordinal() - status.ordinal();
    }

    @Override
    public String toString() {
        return "TestsuiteData [id=" + id + ", name=" + name + "]";
    }
}
