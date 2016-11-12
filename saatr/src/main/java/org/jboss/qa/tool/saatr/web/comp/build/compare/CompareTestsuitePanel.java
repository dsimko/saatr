
package org.jboss.qa.tool.saatr.web.comp.build.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.domain.build.TestSuite;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.comp.build.testsuite.TestsuitePanel;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class CompareTestsuitePanel extends Panel {

    @SpringBean
    private BuildRepository buildRepository;

    public CompareTestsuitePanel(String id, final List<ObjectId> buildIds, String testsuiteName) {
        super(id);
        SortedSet<String> allTestsuitesNames = new TreeSet<>();
        Map<ObjectId, List<TestSuite>> allTestsuites = new HashMap<>();
        for (ObjectId buildId : buildIds) {
            Build build = buildRepository.findOne(buildId);
            allTestsuites.put(buildId, new ArrayList<>());
            for (TestSuite testSuite : build.getTestsuites()) {
                if (testSuite.getName().contains(testsuiteName)) {
                    allTestsuitesNames.add(testSuite.getName());
                    allTestsuites.get(buildId).add(testSuite);
                }
            }
        }
        add(new RefreshingView<String>("testsuites") {

            @Override
            protected Iterator<IModel<String>> getItemModels() {
                return allTestsuitesNames.stream().map(name -> (IModel<String>) new Model<>(name)).collect(Collectors.toList()).iterator();
            }

            @Override
            protected void populateItem(Item<String> item) {
                item.add(new Label("testsuite", item.getModel()));
            }
        });
        add(new RefreshingView<ObjectId>("rows") {

            @Override
            protected Iterator<IModel<ObjectId>> getItemModels() {
                return buildIds.stream().map(id -> (IModel<ObjectId>) new Model<>(id)).collect(Collectors.toList()).iterator();

            }

            @Override
            protected void populateItem(Item<ObjectId> item) {
                Build build = buildRepository.findOne(item.getModelObject());
                item.add(new Label("name", build.getName()));
                item.add(new Label("config", build.getConfiguration()));
                item.add(new RefreshingView<String>("filters") {

                    @Override
                    protected Iterator<IModel<String>> getItemModels() {
                        return allTestsuitesNames.stream().map(name -> (IModel<String>) new Model<>(name)).collect(Collectors.toList()).iterator();
                    }

                    @Override
                    protected void populateItem(Item<String> item2) {
                        TestSuite testSuite = findTestsuiteByName(allTestsuites.get(item.getModelObject()), item2.getModelObject());
                        item2.add(new TestsuitePanel("testsuite", new Model<>(testSuite)));
                    }
                });
            }
        });

    }

    private TestSuite findTestsuiteByName(List<TestSuite> testSuites, String name) {
        for (TestSuite testSuite : testSuites) {
            if (name.equals(testSuite.getName())) {
                return testSuite;
            }
        }
        return null;
    }

}