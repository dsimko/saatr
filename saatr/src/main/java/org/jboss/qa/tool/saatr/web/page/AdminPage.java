
package org.jboss.qa.tool.saatr.web.page;

import static org.springframework.data.mongodb.core.query.Criteria.where;

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
import org.jboss.qa.tool.saatr.domain.build.ConsoleTextDocument;
import org.jboss.qa.tool.saatr.domain.build.TestcaseDocument;
import org.jboss.qa.tool.saatr.domain.build.TestsuiteDocument;
import org.jboss.qa.tool.saatr.domain.config.ConfigDocument;
import org.jboss.qa.tool.saatr.domain.config.QueryDocument;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class AdminPage extends BasePage<Void> {

    @Inject
    private BuildRepository buildRepository;

    @Inject
    private MongoOperations mongoOperations;

    @SuppressWarnings("unused")
    private String results;

    public AdminPage() {
        add(new RefreshingView<String>("collections") {

            @Override
            protected Iterator<IModel<String>> getItemModels() {
                List<IModel<String>> models = Arrays.asList(Model.of(BuildDocument.COLLECTION_NAME), Model.of(ConfigDocument.COLLECTION_NAME),
                        Model.of(QueryDocument.COLLECTION_NAME), Model.of(ConsoleTextDocument.COLLECTION_NAME));
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
                buildRepository.findAll().forEach(b -> {
                    int failedTestsuites = 0, errorTestsuites = 0, testcases = 0, failedTestcases = 0, errorTestcases = 0, skippedTestcases = 0;
                    for (TestsuiteDocument testsuite : b.getTestsuites()) {
                        if (testsuite.getStatus() == TestsuiteDocument.Status.Error) {
                            errorTestsuites++;
                        }
                        if (testsuite.getStatus() == TestsuiteDocument.Status.Failure) {
                            failedTestsuites++;
                        }
                        for (TestcaseDocument testcase : testsuite.getTestcases()) {
                            testcases++;
                            if (testcase.getStatus() == TestcaseDocument.Status.Error) {
                                errorTestcases++;
                            }
                            if (testcase.getStatus() == TestcaseDocument.Status.Failure) {
                                failedTestcases++;
                            }
                            if (testcase.getStatus() == TestcaseDocument.Status.Skipped) {
                                skippedTestcases++;
                            }
                        }
                    }
                    mongoOperations.updateFirst(Query.query(where("id").is(b.getId())), Update.update("failedTestsuites", failedTestsuites),
                            BuildDocument.class);
                    mongoOperations.updateFirst(Query.query(where("id").is(b.getId())), Update.update("errorTestsuites", errorTestsuites), BuildDocument.class);
                    mongoOperations.updateFirst(Query.query(where("id").is(b.getId())), Update.update("testcases", testcases), BuildDocument.class);
                    mongoOperations.updateFirst(Query.query(where("id").is(b.getId())), Update.update("failedTestcases", failedTestcases), BuildDocument.class);
                    mongoOperations.updateFirst(Query.query(where("id").is(b.getId())), Update.update("errorTestcases", errorTestcases), BuildDocument.class);
                    mongoOperations.updateFirst(Query.query(where("id").is(b.getId())), Update.update("skippedTestcases", skippedTestcases),
                            BuildDocument.class);

                });
            }
        });

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
