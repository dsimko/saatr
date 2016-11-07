
package org.jboss.qa.tool.saatr.web.page;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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
import org.jboss.qa.tool.saatr.repo.UserRepository;
import org.jboss.qa.tool.saatr.web.comp.group.GroupsPanel;
import org.jboss.qa.tool.saatr.web.comp.user.UsersPanel;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class AdminPage extends BasePage<Void> {

    @Inject
    private MongoOperations mongoOperations;

    @Inject
    private UserRepository userRepository;

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
        add(new Link<Void>("tmp") {

            @Override
            public void onClick() {
                User user = userRepository.getCurrentUser();
                Group group = user.getGroups().stream().filter(g -> "transaction".equals(g.getName())).collect(Collectors.toList()).get(0);
                if (group != null) {
                    for (Build build : mongoOperations.findAll(Build.class)) {
                        mongoOperations.updateFirst(Query.query(where("id").is(build.getId())), Update.update("groupId", group.getId()), Build.class);
                    }
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
