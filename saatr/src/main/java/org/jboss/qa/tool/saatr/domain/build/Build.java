
package org.jboss.qa.tool.saatr.domain.build;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.wicket.protocol.http.WebApplication;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithID;
import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * An entity representing a {@link Build}.
 * 
 * @author dsimko@redhat.com
 */
@Data
@Document(collection = Build.COLLECTION_NAME)
@SuppressWarnings("serial")
public class Build implements DocumentWithProperties<ObjectId>, DocumentWithID<ObjectId> {

    public static final String COLLECTION_NAME = "build";

    private static final String NAME_CONFIG_DELIMITER = "/";

    public static enum Status {

        Success(0), SuccessWithFlakyFailure(0), SuccessWithFlakyError(0), Failed(1);

        int weight;

        Status(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return weight;
        }

    }

    @Id
    private ObjectId id;

    @Indexed
    private String name;

    @Indexed
    private String configuration;

    @Indexed
    private Status status;

    @Indexed
    private Integer statusWeight;

    @Indexed
    private String fullName;

    private Long buildNumber;

    private Date created = new Date();

    private Long duration;

    private Integer failedTestsuitesCount;

    private Integer errorTestsuitesCount;

    private Integer totalTestsuitesCount;

    private Integer failedTestcasesCount;

    private Integer errorTestcasesCount;

    private Integer skippedTestcasesCount;

    private Integer totalTestcasesCount;

    private ObjectId consoleTextId;

    private final Set<BuildProperty> systemProperties = new TreeSet<>();

    private final Set<BuildProperty> buildProperties = new TreeSet<>();

    private final Set<BuildProperty> properties = new TreeSet<>();

    private final List<TestSuite> testsuites = new ArrayList<>();

    private Integer childCount;

    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.name = getJobName();
        this.configuration = getJobConfiguration();
    }

    public void setStatus(Status status) {
        this.status = status;
        if (status == null) {
            statusWeight = Status.Success.weight;
        } else {
            statusWeight = status.weight;
        }
    }

    private String getJobName() {
        if (fullName == null) {
            return null;
        }
        int index = fullName.indexOf(NAME_CONFIG_DELIMITER);
        if (index != -1) {
            return fullName.substring(0, index);
        } else {
            return fullName;
        }
    }

    private String getJobConfiguration() {
        if (fullName == null) {
            return null;
        }
        int index = fullName.lastIndexOf(NAME_CONFIG_DELIMITER);
        if (index != -1) {
            return fullName.substring(index + 1, fullName.length());
        } else {
            return fullName;
        }
    }

    public static Status determineStatus(List<TestSuite> testsuites) {
        Status status = Status.Success;
        for (TestSuite testsuiteData : testsuites) {
            switch (testsuiteData.getStatus()) {
                case Failure:
                case Error:
                    return Status.Failed;
                case FlakyFailure:
                    status = Status.SuccessWithFlakyFailure;
                    break;
                case FlakyError:
                    if (status != Status.SuccessWithFlakyFailure)
                        status = Status.SuccessWithFlakyError;
                    break;
                case Success:
                    break;
            }
        }
        return status;
    }

    public String getNameWithConfiguration() {
        return name + (this.configuration == null ? "" : this.configuration);
    }

    public String getContentHtml() {
        return HtmlRenderer.getContentHtml(this);
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
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (configuration == null) {
            if (other.configuration != null)
                return false;
        } else if (!configuration.equals(other.configuration))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
        return result;
    }

    public static class HtmlRenderer {

        private static final String HTML_SPACE = "&nbsp;";

        private static final String PROPERTY_NAME = "EAP_VERSION";

        private static final int LABEL_WIDTH = 750;

        private static final int CHILD_COUNT_WIDTH = 30;

        private static final int TEST_SUITE_STATISTICS_WIDTH = 150;

        private static final int TEST_CASE_STATISTICS_WIDTH = 200;

        private static final int STATUS_WIDTH = 80;

        private static final int SUB_TREE_MARGIN = 18;

        public static String getContentHtml(Build jobRun) {
            int width = LABEL_WIDTH;
            String label = jobRun.getName();
            if (jobRun.getConfiguration() != null) {
                width -= SUB_TREE_MARGIN;
                label = jobRun.getConfiguration();
            }
            if (jobRun.getId() != null) {
                width -= SUB_TREE_MARGIN;
                label = "Build #" + jobRun.buildNumber + ", " + PROPERTY_NAME + " = " + getPropertyValue(jobRun.getBuildProperties(), PROPERTY_NAME);
            }
            StringBuilder builder = new StringBuilder();
            builder.append("<span style=\"width:" + width + "px;\" class=\"tree-column\">");
            builder.append(label);
            builder.append("</span>");
            builder.append("<span style=\"width:" + CHILD_COUNT_WIDTH + "px;\" class=\"tree-column\">");
            builder.append(jobRun.getChildCount() == null ? HTML_SPACE : jobRun.getChildCount());
            builder.append("</span>");
            builder.append("<span style=\"width:" + TEST_SUITE_STATISTICS_WIDTH + "px;\" class=\"tree-column\">");
            builder.append(getTestsuiteStatisticsHtml(jobRun));
            builder.append("</span>");
            builder.append("<span style=\"width:" + TEST_CASE_STATISTICS_WIDTH + "px;\" class=\"tree-column\">");
            builder.append(getTestcaseStatisticsHtml(jobRun));
            builder.append("</span>");
            builder.append("<span style=\"width:" + STATUS_WIDTH + "px;\" class=\"tree-column\">");
            builder.append(getStatusHtml(jobRun));
            builder.append("</span>");
            return builder.toString();
        }

        private static String getPropertyValue(Set<BuildProperty> properties, String propertyName) {
            for (BuildProperty buildProperty : properties) {
                if (propertyName.equals(buildProperty.getName())) {
                    return buildProperty.getValue();
                }
            }
            return "";
        }

        public static String getStatusHtml(Build build) {
            StringBuilder builder = new StringBuilder();
            builder.append("<img src=\"");
            builder.append(WebApplication.get().getServletContext().getContextPath());
            builder.append("/images/");
            if (build == null || (build.getStatus() == null && build.getStatusWeight() == null)) {
                builder.append("aborted16.png\" />");
                return builder.toString();
            } else {
                Status status = build.getStatus();
                if (status == null) {
                    status = build.getStatusWeight() == 0 ? Status.Success : Status.Failed;
                }
                if (status == Status.Failed) {
                    builder.append("yellow16.png");
                } else {
                    builder.append("blue16.png");
                }
                builder.append("\" /> ");
                if (Status.SuccessWithFlakyError == status || Status.SuccessWithFlakyFailure == status) {
                    builder.append("Flaky");
                } else {
                    builder.append(status);
                }
                return builder.toString();
            }
        }

        public static String getTestcaseStatisticsHtml(Build build) {
            if (build.getId() == null) {
                return HTML_SPACE;
            }
            StringBuilder builder = new StringBuilder();
            builder.append("<span class=\"text-danger\">F: ");
            builder.append(build.getFailedTestcasesCount());
            builder.append("</span><span class=\"text-muted\"> | </span><span class=\"text-warning\">E:  ");
            builder.append(build.getErrorTestcasesCount());
            builder.append("</span><span class=\"text-muted\"> | S: ");
            builder.append(build.getSkippedTestcasesCount());
            builder.append(" | </span> T: ");
            builder.append(build.getTotalTestcasesCount());
            return builder.toString();
        }

        public static String getTestsuiteStatisticsHtml(Build build) {
            if (build.getId() == null) {
                return HTML_SPACE;
            }
            StringBuilder builder = new StringBuilder();
            builder.append("<span class=\"text-danger\">F: ");
            builder.append(build.getFailedTestsuitesCount());
            builder.append("</span><span class=\"text-muted\"> | </span><span class=\"text-warning\">E:  ");
            builder.append(build.getErrorTestsuitesCount());
            builder.append("</span><span class=\"text-muted\"> | </span>T: ");
            builder.append(build.getTotalTestsuitesCount());
            return builder.toString();
        }

    }
}
