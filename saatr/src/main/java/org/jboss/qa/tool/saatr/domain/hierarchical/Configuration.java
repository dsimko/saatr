
package org.jboss.qa.tool.saatr.domain.hierarchical;

import java.util.List;

import lombok.Data;

@Data
public class Configuration {

    private String name;
    private List<JobRun> jobRuns;
}
