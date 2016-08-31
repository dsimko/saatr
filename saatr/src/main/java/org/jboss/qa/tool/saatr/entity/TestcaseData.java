package org.jboss.qa.tool.saatr.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.entity.Build.PropertyData;
import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite.Testcase;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import lombok.Data;

@Data
@Entity
@SuppressWarnings("serial")
public class TestcaseData implements PersistableWithProperties {

    public static enum Status {
        Success, Skipped, FlakyFailure, Failure, FlakyError, Error
    }

    @Id
    ObjectId id;
    final Set<PropertyData> properties = new TreeSet<>();
    final List<FailureData> failure = new ArrayList<>();
    final List<FailureData> flakyErrors = new ArrayList<>();
    final List<FailureData> flakyFailures = new ArrayList<>();
    final List<FailureData> rerunFailure = new ArrayList<>();
    FailureData skipped;
    FailureData error;
    String systemOut;
    String systemErr;
    String name;
    String classname;
    String group;
    Double time;
    Status status;

    @Data
    public static class FailureData implements Serializable {

        String value;
        String message;
        String type;
        Double time;

    }

    @Override
    public String toString() {
        return "TestcaseData [id=" + id + ", name=" + name + ", classname=" + classname + "]";
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
