package org.jboss.qa.tool.saatr.web.comp.build.testsuite;

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
import org.jboss.qa.tool.saatr.entity.TestsuiteData;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class TestsuitePanel extends GenericPanel<TestsuiteData> {

    private Component body;

    public TestsuitePanel(String id, final IModel<TestsuiteData> model) {
        super(id, new CompoundPropertyModel<>(model));
        final WebMarkupContainer panel = new WebMarkupContainer("panel") {
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
        add(panel.setOutputMarkupId(true));
        panel.add(new Label("name"));
        panel.add(body = new BodyPanel("bodyPanel", model));
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