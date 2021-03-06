
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
import org.jboss.qa.tool.saatr.domain.User;
import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.domain.build.ConsoleText;
import org.jboss.qa.tool.saatr.domain.build.Group;
import org.jboss.qa.tool.saatr.domain.config.ConfigDocument;
import org.jboss.qa.tool.saatr.domain.config.QueryDocument;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.comp.group.GroupsPanel;
import org.jboss.qa.tool.saatr.web.comp.user.UsersPanel;
import org.springframework.cache.CacheManager;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class AdminPage extends BasePage<Void> {

    @Inject
    private MongoOperations mongoOperations;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private BuildRepository buildRepository;

    @SuppressWarnings("unused")
    private String results;

    public AdminPage() {
        add(new RefreshingView<String>("collections") {

            @Override
            protected Iterator<IModel<String>> getItemModels() {
                List<IModel<String>> models = Arrays.asList(Model.of(Build.COLLECTION_NAME), Model.of(ConfigDocument.COLLECTION_NAME),
                        Model.of(QueryDocument.COLLECTION_NAME), Model.of(ConsoleText.COLLECTION_NAME), Model.of(BuildFilter.COLLECTION_NAME),
                        Model.of(User.COLLECTION_NAME), Model.of(Group.COLLECTION_NAME));
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
        add(new UsersPanel("users"));
        add(new GroupsPanel("groups"));
        add(new Link<Void>("clearCache") {

            @Override
            public void onClick() {
                for (String name : cacheManager.getCacheNames()) {
                    cacheManager.getCache(name).clear();
                }
            }
        });
        add(new Link<Void>("warmUpCache") {

            @Override
            public void onClick() {
                buildRepository.getRoots(new BuildFilter());
                buildRepository.findDistinctSystemPropertiesNames();
                for (String name : buildRepository.findDistinctPropertiesNames()) {
                    buildRepository.findDistinctPropertiesValues(name);
                }
                for (String name : buildRepository.findDistinctVariableNames()) {
                    buildRepository.findDistinctVariableValues(name);
                }
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
