package org.jboss.qa.tool.saatr.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.entity.Build.PropertyData;
import org.mongodb.morphia.annotations.Id;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class TestcaseData implements PersistableWithProperties {

    @Id
    ObjectId id;
    final Set<PropertyData> properties = new TreeSet<>();
    final List<FailureData> failure = new ArrayList<>();
    final List<RerunFailureData> rerunFailure = new ArrayList<>();
    SkippedData skipped;
    ErrorData error;
    String systemOut;
    String systemErr;
    String name;
    String classname;
    String group;
    Double time;

    @Data
    public static class ErrorData implements Serializable {

        String value;
        String message;
        String type;

    }

    @Data
    public static class FailureData implements Serializable {

        String value;
        String message;
        String type;
        Double time;

    }

    @Data
    public static class RerunFailureData implements Serializable {

        String value;
        String message;
        String type;
        Double time;

    }

    @Data
    public static class SkippedData implements Serializable {

        String value;
        String message;

    }

    @Override
    public String toString() {
        return "TestcaseData [id=" + id + ", name=" + name + ", classname=" + classname + "]";
    }

}
