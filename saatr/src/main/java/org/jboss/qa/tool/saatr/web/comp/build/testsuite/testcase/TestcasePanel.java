
package org.jboss.qa.tool.saatr.web.comp.build.testsuite.testcase;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.build.TestCase;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class TestcasePanel extends GenericPanel<TestCase> {

    private Component body;

    public TestcasePanel(String id, final IModel<TestCase> model) {
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
        body = new EmptyPanel("bodyPanel");
        add(panel.setOutputMarkupId(true));
        panel.add(body);
        WebMarkupContainer panelHead = new WebMarkupContainer("head");
        panelHead.add(new AjaxEventBehavior("click") {

            @Override
            protected void onEvent(AjaxRequestTarget target) {
                if (body instanceof BodyPanel) {
                    replaceBodyPanel(new EmptyPanel(body.getId()));
                } else {
                    replaceBodyPanel(new BodyPanel(body.getId(), model));
                }
                target.add(panel);
            }

        });
        panelHead.add(new Label("name"));
        panelHead.add(new Label("collapse", Model.of("")) {

            @Override
            public boolean isVisible() {
                return body instanceof BodyPanel;
            }
        });
        panelHead.add(new Label("expand", Model.of("")) {

            @Override
            public boolean isVisible() {
                return body instanceof EmptyPanel;
            }
        });
        panel.add(panelHead);

    }

    private void replaceBodyPanel(Panel with) {
        body.replaceWith(with);
        body = with;
    }

}