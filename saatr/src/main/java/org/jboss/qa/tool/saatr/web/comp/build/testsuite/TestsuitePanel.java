package org.jboss.qa.tool.saatr.web.comp.build.testsuite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
public class TestsuitePanel extends GenericPanel<TestsuiteData> {

    public TestsuitePanel(String id, final IModel<TestsuiteData> model) {
        super(id, new CompoundPropertyModel<>(model));
        WebMarkupContainer panel = new WebMarkupContainer("panel") {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                if (getModelObject().getErrors() > 0 || getModelObject().getFailures() > 0) {
                    tag.append("class", "panel-danger", " ");
                } else if (getModelObject().getTests().equals(getModelObject().getSkipped())) {
                    tag.append("class", "panel-warning", " ");
                } else {
                    tag.append("class", "panel-success", " ");
                }
            }
        };
        add(panel);
        panel.add(new Label("name"));
        panel.add(new Label("time"));
        panel.add(new Label("tests"));
        panel.add(new Label("errors"));
        panel.add(new Label("skipped"));
        panel.add(new Label("failures"));
        panel.add(new PropertiesPanel<>("properties", model));
        panel.add(new RefreshingView<TestcaseData>("testcases") {
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