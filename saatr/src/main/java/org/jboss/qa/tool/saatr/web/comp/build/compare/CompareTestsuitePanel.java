
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

    private String previousName = "";

    private String previousConfig = "";

    @SpringBean
    private BuildRepository buildRepository;

    public CompareTestsuitePanel(String id, final List<ObjectId> buildIds, String testsuiteName) {
        super(id);
        SortedSet<String> testsuitesNames = new TreeSet<>();
        Map<ObjectId, List<TestSuite>> testsuites = new HashMap<>();
        final List<Build> builds = buildRepository.find(buildIds, testsuiteName);
        for (Build build : builds) {
            testsuites.put(build.getId(), new ArrayList<>());
            for (TestSuite testSuite : build.getTestsuites()) {
                if (testSuite.getName().contains(testsuiteName)) {
                    testsuitesNames.add(testSuite.getName());
                    testsuites.get(build.getId()).add(testSuite);
                }
            }
        }
        List<IModel<String>> testsuitesNamesModel = testsuitesNames.stream().map(name -> (IModel<String>) new Model<>(name)).collect(Collectors.toList());
        add(new RefreshingView<String>("testsuites") {

            @Override
            protected Iterator<IModel<String>> getItemModels() {
                return testsuitesNamesModel.iterator();
            }

            @Override
            protected void populateItem(Item<String> item) {
                item.add(new Label("testsuite", item.getModel()));
            }
        });
        add(new RefreshingView<Build>("rows") {

            @Override
            protected Iterator<IModel<Build>> getItemModels() {
                return builds.stream().map(build -> (IModel<Build>) new Model<>(build)).collect(Collectors.toList()).iterator();

            }

            @Override
            protected void populateItem(Item<Build> item) {
                Build build = item.getModelObject();
                if (!previousName.equals(build.getName())) {
                    previousName = build.getName();
                    previousConfig = "";
                    item.add(new Label("name", build.getName()));
                } else {
                    item.add(new Label("name", ""));
                }
                if (!previousConfig.equals(build.getConfiguration())) {
                    previousConfig = build.getConfiguration();
                    item.add(new Label("config", build.getConfiguration()));
                } else {
                    item.add(new Label("config", ""));
                }
                item.add(new Label("build", Build.HtmlRenderer.getBuildLabel(build)));
                item.add(new RefreshingView<String>("filters") {

                    @Override
                    protected Iterator<IModel<String>> getItemModels() {
                        return testsuitesNamesModel.iterator();
                    }

                    @Override
                    protected void populateItem(Item<String> item2) {
                        TestSuite testSuite = findTestsuiteByName(testsuites.get(item.getModelObject().getId()), item2.getModelObject());
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