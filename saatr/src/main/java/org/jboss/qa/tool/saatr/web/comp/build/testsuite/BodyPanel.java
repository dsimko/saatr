package org.jboss.qa.tool.saatr.web.comp.build.testsuite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.TestcaseDocument;
import org.jboss.qa.tool.saatr.domain.build.TestsuiteDocument;
import org.jboss.qa.tool.saatr.web.comp.build.properties.PropertiesPanel;
import org.jboss.qa.tool.saatr.web.comp.build.testsuite.testcase.TestcaseModel;
import org.jboss.qa.tool.saatr.web.comp.build.testsuite.testcase.TestcasePanel;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
class BodyPanel extends GenericPanel<TestsuiteDocument> {

    public BodyPanel(String id, final IModel<TestsuiteDocument> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new Label("time"));
        add(new Label("tests"));
        add(new Label("errors"));
        add(new Label("skipped"));
        add(new Label("failures"));
        add(new PropertiesPanel<>("properties", model));
        add(new RefreshingView<TestcaseDocument>("testcases") {
            @Override
            protected Iterator<IModel<TestcaseDocument>> getItemModels() {
                List<IModel<TestcaseDocument>> models = new ArrayList<>();
                int index = 0;
                for (TestcaseDocument testcaseData : getModelObject().getTestcases()) {
                    testcaseData.setIndex(index++);
                	models.add(new TestcaseModel(testcaseData));
                    // FIXME
                    // models.add(new EntityModel<>(testcaseData));
                }
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<TestcaseDocument> item) {
                item.add(new TestcasePanel("testcase", item.getModel()));
            }
        });
    }
}