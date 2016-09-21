
package org.jboss.qa.tool.saatr.domain.build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;
import org.jboss.qa.tool.saatr.jaxb.surefire.Testsuite.Testcase;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class TestcaseDocument implements DocumentWithProperties<UUID> {

    public static enum Status {
        Success, Skipped, FlakyFailure, Failure, FlakyError, Error
    }

    final Set<PropertyData> properties = new TreeSet<>();

    final List<FailureData> failure = new ArrayList<>();

    final List<FailureData> flakyErrors = new ArrayList<>();

    final List<FailureData> flakyFailures = new ArrayList<>();

    final List<FailureData> rerunFailure = new ArrayList<>();

    FailureData skipped;

    FailureData error;

    String systemOut;

    String systemErr;

    @Indexed
    String name;

    String classname;

    String group;

    Double time;

    @Indexed
    Status status;

    @Indexed
    final UUID id = UUID.randomUUID();

    @Transient
    private Integer index;

    @Transient
    private boolean dirty;

    @Data
    public static class FailureData implements Serializable {

        String value;

        String message;

        String type;

        Double time;

    }

    @Override
    public String toString() {
        return "TestcaseData [name=" + name + ", classname=" + classname + "]";
    }

    public static Status determineStatus(Testcase testcase) {
        if (!testcase.getFailure().isEmpty()) {
            return Status.Failure;
        }
        if (testcase.getError() != null) {
            return Status.Error;
        }
        if (!testcase.getFlakyFailure().isEmpty()) {
            return Status.FlakyFailure;
        }
        if (!testcase.getFlakyError().isEmpty()) {
            return Status.FlakyError;
        }
        if (testcase.getSkipped() != null) {
            return Status.Skipped;
        }
        return Status.Success;
    }

}
