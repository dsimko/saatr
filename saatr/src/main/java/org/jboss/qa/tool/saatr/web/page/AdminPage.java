
package org.jboss.qa.tool.saatr.web.page;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.domain.build.ConsoleTextDocument;
import org.jboss.qa.tool.saatr.domain.config.ConfigDocument;
import org.jboss.qa.tool.saatr.domain.config.QueryDocument;
import org.jboss.qa.tool.saatr.domain.hierarchical.JobRun;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.repo.build.JobRunRepository;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class AdminPage extends BasePage<Void> {

    @Inject
    private MongoOperations mongoOperations;

    @Inject
    private BuildRepository buildRepository;

    @Inject
    private JobRunRepository jobRunRepository;

    @SuppressWarnings("unused")
    private String results;

    public AdminPage() {
        add(new RefreshingView<String>("collections") {

            @Override
            protected Iterator<IModel<String>> getItemModels() {
                List<IModel<String>> models = Arrays.asList(Model.of(BuildDocument.COLLECTION_NAME), Model.of(ConfigDocument.COLLECTION_NAME),
                        Model.of(QueryDocument.COLLECTION_NAME), Model.of(ConsoleTextDocument.COLLECTION_NAME), Model.of(BuildFilter.COLLECTION_NAME),
                        Model.of(JobRun.COLLECTION_NAME));
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<String> item) {
                item.add(new Label("name", item.getModelObject()));
                item.add(new Link<Void>("deleteAll") {

                    @Override
                    public void onClick() {
                        mongoOperations.getCollection(item.getModelObject()).drop();
                        results = MessageFormat.format("All {0} deleted.", item.getModelObject());
                    }
                });
                item.add(new Link<Void>("showIndexes") {

                    @Override
                    public void onClick() {
                        showAllIndexes(item.getModelObject());
                    }
                });
                item.add(new Link<Void>("showStats") {

                    @Override
                    public void onClick() {
                        showStats(item.getModelObject());
                    }
                });
                item.add(new Link<Void>("showAllRows") {

                    @Override
                    public void onClick() {
                        showAllRows(item.getModelObject());
                    }
                });
            }
        });
        add(new Label("results", new PropertyModel<>(this, "results")));
        add(new Link<Void>("invalidateSession") {

            @Override
            public void onClick() {
                getSession().invalidateNow();
            }
        });
        add(new Link<Void>("tmp") {

            @Override
            public void onClick() {
                for (BuildDocument build : buildRepository.findAll()) {
                    JobRun jobRun = new JobRun();
                    jobRun.setBuildNumber(build.getBuildNumber());
                    jobRun.setChildCount(build.getNumberOfChildren());
                    jobRun.setConfiguration(getJobConfiguration(build.getJobName()));
                    System.out.println("Configuration: " + jobRun.getConfiguration());
                    jobRun.setConsoleTextId(build.getConsoleTextId());
                    jobRun.setCreated(build.getCreated());
                    jobRun.setDuration(build.getDuration());
                    jobRun.setErrorTestcasesCount(build.getErrorTestcases());
                    jobRun.setErrorTestsuitesCount(build.getErrorTestsuites());
                    jobRun.setFailedTestcasesCount(build.getFailedTestcases());
                    jobRun.setFailedTestsuitesCount(build.getFailedTestsuites());
                    jobRun.setFullName(build.getJobName());
                    jobRun.setName(build.getJobCategory());
                    System.out.println("Name: " + jobRun.getName());
                    jobRun.setSkippedTestcasesCount(build.getSkippedTestcases());
                    jobRun.setStatus(build.getStatus());
                    jobRun.setStatusWeight(build.getStatus().getStatus());
                    jobRun.setTestcasesCount(build.getTestcases());
                    jobRun.getTestsuites().addAll(build.getTestsuites());
                    jobRun.getProperties().addAll(build.getProperties());
                    jobRun.getSystemProperties().addAll(build.getSystemProperties());
                    jobRun.getVariables().addAll(build.getVariables());
                    jobRunRepository.save(jobRun);
                }
            }
        });
    }

    private String getJobConfiguration(String jobName) {
        int index = jobName.lastIndexOf("/");
        if (index != -1) {
            return jobName.substring(index + 1, jobName.length());
        } else {
            return jobName;
        }
    }

    private void showAllIndexes(String collectionName) {
        StringBuilder builder = new StringBuilder();
        mongoOperations.getCollection(collectionName).getIndexInfo().forEach(r -> {
            if (builder.length() != 0) {
                builder.append("\n");
            }
            builder.append(r);
        });
        results = builder.toString();
    }

    private void showStats(String collectionName) {
        try {
            results = mongoOperations.getCollection(collectionName).getStats().toJson();
        } catch (Exception e) {
            results = e.getMessage();
        }
    }

    private void showAllRows(String collectionName) {
        StringBuilder builder = new StringBuilder();
        mongoOperations.getCollection(collectionName).find().forEach(r -> {
            if (builder.length() != 0) {
                builder.append("\n");
            }
            builder.append(r);
        });
        results = builder.toString();
    }

}
