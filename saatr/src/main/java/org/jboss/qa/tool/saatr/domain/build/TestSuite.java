
package org.jboss.qa.tool.saatr.domain.build;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.xml.bind.JAXBElement;

import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.domain.DocumentWithName;
import org.jboss.qa.tool.saatr.domain.build.TestCase.Fragment;
import org.jboss.qa.tool.saatr.jaxb.surefire.Testsuite;
import org.jboss.qa.tool.saatr.jaxb.surefire.Testsuite.Testcase;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.w3c.dom.Element;

import lombok.Data;

/**
 * An embedded document representing an {@link TestSuite}.
 * 
 * @author dsimko@redhat.com
 */
@Data
@SuppressWarnings("serial")
public class TestSuite implements DocumentWithProperties<UUID>, DocumentWithName, Comparable<TestSuite> {

    public static enum Status {
        Success, FlakyError, FlakyFailure, Error, Failure
    }

    private final Set<BuildProperty> properties = new TreeSet<>();

    private final List<TestCase> testcases = new ArrayList<>();

    @Indexed
    private String name;

    private Double time;

    private Integer tests;

    private Integer errors;

    private Integer skipped;

    private Integer failures;

    private String group;

    @Indexed
    private Status status;

    @Indexed
    private final UUID id = UUID.randomUUID();

    @Transient
    private boolean dirty;
    
    public static TestSuite create(Testsuite testsuite) {

        TestSuite testsuiteData = new TestSuite();
        testsuiteData.name = testsuite.getName();
        testsuiteData.time = toDouble(testsuite.getTime());
        testsuiteData.tests = toInteger(testsuite.getTests());
        testsuiteData.errors = toInteger(testsuite.getErrors());
        testsuiteData.skipped = toInteger(testsuite.getSkipped());
        testsuiteData.failures = toInteger(testsuite.getFailures());
        testsuiteData.group = testsuite.getGroup();

        for (Testcase testcase : testsuite.getTestcase()) {
            TestCase testcaseData = new TestCase();
            testcaseData.systemOut = toString(testcase.getSystemOut());
            testcaseData.systemErr = toString(testcase.getSystemErr());
            testcaseData.name = testcase.getName();
            testcaseData.classname = testcase.getClassname();
            testcaseData.group = testcase.getGroup();
            testcaseData.time = toDouble(testcase.getTime());
            testcaseData.status = TestCase.determineStatus(testcase);
            JAXBElement<Testsuite.Testcase.Error> error = testcase.getError();
            if (error != null) {
                testcaseData.error = new Fragment();
                testcaseData.error.message = error.getValue().getMessage();
                testcaseData.error.type = error.getValue().getType();
                testcaseData.error.value = error.getValue().getValue();
            }
            JAXBElement<Testsuite.Testcase.Skipped> skipped = testcase.getSkipped();
            if (skipped != null) {
                testcaseData.skipped = new Fragment();
                testcaseData.skipped.message = skipped.getValue().getMessage();
                testcaseData.skipped.value = skipped.getValue().getValue();
            }
            for (Testsuite.Testcase.Failure failure : testcase.getFailure()) {
                Fragment failureData = new Fragment();
                failureData.message = failure.getMessage();
                failureData.time = toDouble(failure.getTime());
                failureData.type = failure.getType();
                failureData.value = failure.getValue();
                testcaseData.failure.add(failureData);
            }
            for (Testsuite.Testcase.FlakyFailure failure : testcase.getFlakyFailure()) {
                Fragment failureData = new Fragment();
                failureData.message = failure.getMessage();
                failureData.time = toDouble(failure.getTime());
                failureData.type = failure.getType();
                failureData.value = failure.getValue();
                testcaseData.flakyFailures.add(failureData);
            }
            for (Testsuite.Testcase.FlakyError failure : testcase.getFlakyError()) {
                Fragment failureData = new Fragment();
                failureData.message = failure.getMessage();
                failureData.time = toDouble(failure.getTime());
                failureData.type = failure.getType();
                failureData.value = failure.getValue();
                testcaseData.flakyErrors.add(failureData);
            }
            for (Testsuite.Testcase.RerunFailure failure : testcase.getRerunFailure()) {
                Fragment failureData = new Fragment();
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

    private static Status determineStatus(List<TestCase> testcases) {
        Status status = Status.Success;
        for (TestCase testcaseData : testcases) {
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
            return NumberFormat.getInstance(Locale.ENGLISH).parse(input).doubleValue();
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
    public int compareTo(TestSuite o) {
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
        return "TestSuite [name=" + name + "]";
    }
}
