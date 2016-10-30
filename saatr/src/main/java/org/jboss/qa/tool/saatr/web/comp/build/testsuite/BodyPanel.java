
package org.jboss.qa.tool.saatr.web.comp.build.testsuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.TestCase;
import org.jboss.qa.tool.saatr.domain.build.TestSuite;
import org.jboss.qa.tool.saatr.util.NameComparator;
import org.jboss.qa.tool.saatr.web.comp.build.compare.CompareBuildPanel;
import org.jboss.qa.tool.saatr.web.comp.build.properties.PropertiesPanel;
import org.jboss.qa.tool.saatr.web.comp.build.testsuite.testcase.TestcaseModel;
import org.jboss.qa.tool.saatr.web.comp.build.testsuite.testcase.TestcasePanel;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
class BodyPanel extends GenericPanel<TestSuite> {

    public BodyPanel(String id, final IModel<TestSuite> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new PropertiesPanel<>("properties", model));
        add(new RefreshingView<TestCase>("testcases") {

            @Override
            protected Iterator<IModel<TestCase>> getItemModels() {
                List<IModel<TestCase>> models = new ArrayList<>();
                int index = 0;
                for (TestCase tc : getModelObject().getTestcases()) {
                    if (tc.getIndex() == null) {
                        tc.setIndex(index++);
                    }
                }
                if (findParent(CompareBuildPanel.class) != null) {
                    Collections.sort(getModelObject().getTestcases(), new NameComparator<>());
                } else {
                    Collections.sort(getModelObject().getTestcases());
                }
                getModelObject().getTestcases().stream().forEach(tc -> models.add(new TestcaseModel(tc)));
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<TestCase> item) {
                item.add(new TestcasePanel("testcase", item.getModel()));
            }
        });
    }
}