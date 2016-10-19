package org.jboss.qa.tool.saatr.domain.hierarchical;

import java.util.List;

import lombok.Data;

@Data
public class Job {

    private String name;
    private List<Configuration> configurations;
}
