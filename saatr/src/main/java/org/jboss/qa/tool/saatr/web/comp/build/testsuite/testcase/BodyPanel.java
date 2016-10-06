
package org.jboss.qa.tool.saatr.web.comp.build.testsuite.testcase;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.jboss.qa.tool.saatr.domain.build.TestcaseDocument;
import org.jboss.qa.tool.saatr.web.comp.LongTextLabel;
import org.jboss.qa.tool.saatr.web.comp.build.properties.PropertiesPanel;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
class BodyPanel extends GenericPanel<TestcaseDocument> {

    public BodyPanel(String id, final IModel<TestcaseDocument> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new Label("name"));
        add(new Label("time") {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                String value = getDefaultModelObjectAsString();
                if (Strings.isEmpty(value) || "0".equals(value)) {
                    setVisible(false);
                }
            }
        });
        add(new SkippedPanel("skipped"));
        add(new ErrorPanel("error"));
        add(new FailuresPanel("failure", "Failures"));
        add(new FailuresPanel("flakyErrors", "Flaky Errors"));
        add(new FailuresPanel("flakyFailures", "Flaky Failures"));
        add(new FailuresPanel("rerunFailure", "Rerun Failure"));
        add(new LongTextLabel("systemOut", new PropertyModel<>(model, "systemOut")));
        add(new LongTextLabel("systemErr", new PropertyModel<>(model, "systemErr")));
        add(new PropertiesPanel<>("properties", model));
    }
}