
package org.jboss.qa.tool.saatr.domain.build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.domain.DocumentWithName;
import org.jboss.qa.tool.saatr.jaxb.surefire.Testsuite.Testcase;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.Data;

/**
 * An embedded document representing an {@link TestCase}.
 * 
 * @author dsimko@redhat.com
 */
@Data
@SuppressWarnings("serial")
public class TestCase implements DocumentWithProperties<UUID>, DocumentWithName, Comparable<TestCase> {

    public static enum Status {
        Success(1), Skipped(0), FlakyFailure(1), FlakyError(1), Error(2), Failure(3);
        
        final int weight;
        
        private Status(int weight) {
            this.weight = weight;
        }
    }

    final Set<BuildProperty> properties = new TreeSet<>();

    final List<Fragment> failure = new ArrayList<>();

    final List<Fragment> flakyErrors = new ArrayList<>();

    final List<Fragment> flakyFailures = new ArrayList<>();

    final List<Fragment> rerunFailure = new ArrayList<>();

    Fragment skipped;

    Fragment error;

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
    public static class Fragment implements Serializable {

        String value;

        String message;

        String type;

        Double time;

    }
    
    @Override
    public String toString() {
        return "Testcase [name=" + name + ", classname=" + classname + "]";
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

    @Override
    public int compareTo(TestCase o) {
        if (this.equals(o) || status == null || status == o.status) {
            return 0;
        }
        if (o.status == null) {
            return 1;
        }
        return o.status.weight - status.weight;
    }
}
