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
import org.jboss.qa.tool.saatr.entity.TestcaseData;
import org.jboss.qa.tool.saatr.entity.TestsuiteData;
import org.jboss.qa.tool.saatr.web.comp.EntityModel;
import org.jboss.qa.tool.saatr.web.comp.build.properties.PropertiesPanel;
import org.jboss.qa.tool.saatr.web.comp.build.testsuite.testcase.TestcasePanel;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
class BodyPanel extends GenericPanel<TestsuiteData> {

    public BodyPanel(String id, final IModel<TestsuiteData> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new Label("time"));
        add(new Label("tests"));
        add(new Label("errors"));
        add(new Label("skipped"));
        add(new Label("failures"));
        add(new PropertiesPanel<>("properties", model));
        add(new RefreshingView<TestcaseData>("testcases") {
            @Override
            protected Iterator<IModel<TestcaseData>> getItemModels() {
                List<IModel<TestcaseData>> models = new ArrayList<>();
                for (TestcaseData testcaseData : getModelObject().getTestcases()) {
                    models.add(new EntityModel<>(testcaseData));
                }
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<TestcaseData> item) {
                item.add(new TestcasePanel("testcase", item.getModel()));
            }
        });
    }
}