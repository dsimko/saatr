package org.jboss.qa.tool.saatr.web.comp.build.testsuite.testcase;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.jboss.qa.tool.saatr.domain.build.TestcaseDocument;
import org.jboss.qa.tool.saatr.web.comp.HideableLabel;
import org.jboss.qa.tool.saatr.web.comp.build.properties.PropertiesPanel;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class TestcasePanel extends GenericPanel<TestcaseDocument> {

    public TestcasePanel(String id, final IModel<TestcaseDocument> model) {
        super(id, new CompoundPropertyModel<>(model));
        WebMarkupContainer panel = new WebMarkupContainer("panel") {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                switch (getModelObject().getStatus()) {
                case Error:
                case Failure:
                    tag.append("class", "panel-danger", " ");
                    break;
                case FlakyFailure:
                case FlakyError:
                    tag.append("class", "panel-warning", " ");
                    break;
                case Skipped:
                    tag.append("class", "panel-info", " ");
                    break;
                case Success:
                    tag.append("class", "panel-success", " ");
                    break;
                }
            }
        };
        add(panel);
        panel.add(new Label("name"));
        panel.add(new Label("time") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                String value = getDefaultModelObjectAsString();
                if (Strings.isEmpty(value) || "0".equals(value)) {
                    setVisible(false);
                }
            }
        });
        panel.add(new SkippedPanel("skipped"));
        panel.add(new ErrorPanel("error"));
        panel.add(new FailuresPanel("failure", "Failures"));
        panel.add(new FailuresPanel("flakyErrors", "Flaky Errors"));
        panel.add(new FailuresPanel("flakyFailures", "Flaky Failures"));
        panel.add(new FailuresPanel("rerunFailure", "Rerun Failure"));
        panel.add(new HideableLabel("systemOut"));
        panel.add(new HideableLabel("systemErr"));
        panel.add(new PropertiesPanel<>("properties", model));

    }
}