
package org.jboss.qa.tool.saatr.web.comp.build.properties;

import java.util.Iterator;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.domain.build.BuildProperty;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapFeedbackPanel;
import org.jboss.qa.tool.saatr.web.comp.build.AbstractBuildsPanel.CopyToAllSelectedEvent;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
class PropertiesViewPanel<T extends DocumentWithProperties<?>> extends GenericPanel<T> {

    public PropertiesViewPanel(String id, final IModel<T> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new RefreshingView<BuildProperty>("properties") {

            @Override
            protected Iterator<IModel<BuildProperty>> getItemModels() {
                return getModelObject().getProperties().stream().filter(p -> p.getValue() != null).sorted().map(
                        p -> (IModel<BuildProperty>) new CompoundPropertyModel<>(p)).iterator();
            }

            @Override
            protected void populateItem(Item<BuildProperty> item) {
                item.add(new Label("name"));
                item.add(new Label("value"));
            }
        });
        FeedbackPanel feedbackPanel = new BootstrapFeedbackPanel("feedback");
        add(feedbackPanel.setOutputMarkupId(true));
        add(new AjaxLink<T>("copyToAllSelected", model) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                send(getPage(), Broadcast.DEPTH, new CopyToAllSelectedEvent(getModelObject().getProperties(), feedbackPanel));
                target.add(feedbackPanel);
            }
        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(!getModelObject().getProperties().isEmpty());
    }

}