
package org.jboss.qa.tool.saatr.web.page;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.domain.config.ConfigDocument;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.repo.config.ConfigRepository;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class AdminPage extends BasePage<Void> {

    @Inject
    private BuildRepository buildRepository;

    @Inject
    private ConfigRepository configRepository;

    @Inject
    private MongoOperations mongoOperations;

    @SuppressWarnings("unused")
    private String results;

    public AdminPage() {
        add(new Link<Void>("deleteAllBuilds") {

            @Override
            public void onClick() {
                buildRepository.deleteAll();
                results = "All Builds deleted.";
            }
        });
        add(new Link<Void>("showBuildsIndexes") {

            @Override
            public void onClick() {
                showAllIndexes(BuildDocument.COLLECTION_NAME);
            }
        });
        add(new Link<Void>("showBuildsStats") {

            @Override
            public void onClick() {
                showStats(BuildDocument.COLLECTION_NAME);
            }
        });
        add(new Link<Void>("deleteAllConfigs") {

            @Override
            public void onClick() {
                configRepository.deleteAll();
                results = "All Configs deleted.";
            }
        });
        add(new Link<Void>("showConfigsIndexes") {

            @Override
            public void onClick() {
                showAllIndexes(ConfigDocument.COLLECTION_NAME);
            }
        });
        add(new Link<Void>("showConfigsStats") {

            @Override
            public void onClick() {
                showStats(ConfigDocument.COLLECTION_NAME);
            }
        });
        add(new Label("results", new PropertyModel<>(this, "results")));

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
        results = mongoOperations.getCollection(collectionName).getStats().toJson();
    }

}
