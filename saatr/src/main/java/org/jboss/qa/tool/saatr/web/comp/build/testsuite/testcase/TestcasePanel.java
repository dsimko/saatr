package org.jboss.qa.tool.saatr.web.comp.build.testsuite.testcase;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.entity.TestcaseData;
import org.jboss.qa.tool.saatr.web.comp.build.properties.PropertiesPanel;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class TestcasePanel extends GenericPanel<TestcaseData> {

    public TestcasePanel(String id, final IModel<TestcaseData> model) {
        super(id, new CompoundPropertyModel<>(model));
        WebMarkupContainer panel = new WebMarkupContainer("panel") {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                if (getModelObject().getError() != null || !getModelObject().getFailure().isEmpty()) {
                    tag.append("class", "panel-danger", " ");
                } else if (getModelObject().getSkipped() != null) {
                    tag.append("class", "panel-warning", " ");
                } else {
                    tag.append("class", "panel-success", " ");
                }
            }
        };
        add(panel);
        panel.add(new Label("name"));
        panel.add(new Label("time"));
        panel.add(new SkippedPanel("skipped"));
        panel.add(new ErrorPanel("error"));
        panel.add(new FailuresPanel("failure"));
        panel.add(new PropertiesPanel<>("properties", model));

    }
}