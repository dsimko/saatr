
package org.jboss.qa.tool.saatr.web.comp.build.testsuite.testcase;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.TestcaseDocument;
import org.jboss.qa.tool.saatr.domain.build.TestcaseDocument.Status;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class TestcasePanel extends GenericPanel<TestcaseDocument> {

    private Component body;

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
        if (getModelObject().getStatus() == Status.Failure || getModelObject().getStatus() == Status.Error) {
            body = new BodyPanel("bodyPanel", model);
        } else {
            body = new EmptyPanel("bodyPanel");
        }
        add(panel.setOutputMarkupId(true));
        panel.add(new Label("name"));
        panel.add(body);
        panel.add(new AjaxLink<Void>("collapse") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                replaceBodyPanel(new EmptyPanel(body.getId()));
                target.add(panel);
            }

            @Override
            public boolean isVisible() {
                return body instanceof BodyPanel;
            }
        });
        panel.add(new AjaxLink<Void>("expand") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                replaceBodyPanel(new BodyPanel(body.getId(), model));
                target.add(panel);
            }

            @Override
            public boolean isVisible() {
                return body instanceof EmptyPanel;
            }
        });
    }
    
    private void replaceBodyPanel(Panel with) {
        body.replaceWith(with);
        body = with;
    }

}