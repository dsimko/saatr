package org.jboss.qa.tool.saatr.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBElement;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.entity.Build.TestsuiteData.TestcaseData.ErrorData;
import org.jboss.qa.tool.saatr.entity.Build.TestsuiteData.TestcaseData.FailureData;
import org.jboss.qa.tool.saatr.entity.Build.TestsuiteData.TestcaseData.RerunFailureData;
import org.jboss.qa.tool.saatr.entity.Build.TestsuiteData.TestcaseData.SkippedData;
import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite;
import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite.Properties;
import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite.Testcase;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@Entity
@Data
@SuppressWarnings("serial")
public class Build implements WithProperties {

    @Id
    private ObjectId id;
    private String jobName;
    private Long buildNumber;
    private Long timestamp;
    private Long duration;
    private final Set<PropertyData> properties = new TreeSet<>();
    private final List<TestsuiteData> testsuites = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropertyData implements Serializable, Comparable<PropertyData> {

        private String name;
        private String value;

        public static List<PropertyData> create(List<Properties> properties) {
            List<PropertyData> list = new ArrayList<>();
            for (Properties props : properties) {
                for (Properties.Property prop : props.getProperty()) {
                    list.add(new PropertyData(prop.getName(), prop.getValue()));
                }
            }
            return list;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PropertyData other = (PropertyData) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public int compareTo(PropertyData o) {
            if (this.name == null || o == null) {
                return 0;
            }
            return this.name.compareToIgnoreCase(o.name);
        }

    }

    @Data
    public static class TestsuiteData implements WithProperties {

        private final Set<PropertyData> properties = new TreeSet<>();
        private final List<TestcaseData> testcases = new ArrayList<>();
        private String name;
        private Double time;
        private Integer tests;
        private Integer errors;
        private Integer skipped;
        private Integer failures;
        private String group;

        @Data
        public static class TestcaseData implements WithProperties {

            private final Set<PropertyData> properties = new TreeSet<>();
            private final List<FailureData> failure = new ArrayList<>();
            private final List<RerunFailureData> rerunFailure = new ArrayList<>();
            private SkippedData skipped;
            private ErrorData error;
            private String systemOut;
            private String systemErr;
            private String name;
            private String classname;
            private String group;
            private Double time;

            @Data
            public static class ErrorData implements Serializable {

                private String value;
                private String message;
                private String type;

            }

            @Data
            public static class FailureData implements Serializable {

                private String value;
                private String message;
                private String type;
                private Double time;

            }

            @Data
            public static class RerunFailureData implements Serializable {

                private String value;
                private String message;
                private String type;
                private Double time;

            }

            @Data
            public static class SkippedData implements Serializable {

                private String value;
                private String message;

            }

        }

        public static TestsuiteData create(Testsuite testsuite) {

            TestsuiteData testsuiteData = new TestsuiteData();
            testsuiteData.name = testsuite.getName();
            testsuiteData.time = toDouble(testsuite.getTime());
            testsuiteData.tests = toInteger(testsuite.getTests());
            testsuiteData.errors = toInteger(testsuite.getErrors());
            testsuiteData.skipped = toInteger(testsuite.getSkipped());
            testsuiteData.failures = toInteger(testsuite.getFailures());
            testsuiteData.group = testsuite.getGroup();

            TestcaseData testcaseData = new TestcaseData();
            for (Testcase testcase : testsuite.getTestcase()) {
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
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Build other = (Build) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
