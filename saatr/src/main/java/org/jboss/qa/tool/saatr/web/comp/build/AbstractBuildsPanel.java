
package org.jboss.qa.tool.saatr.web.comp.build;

import java.io.Serializable;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.domain.build.BuildProperty;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.comp.build.filter.BuildFilterPanel.SubmitFilterEvent;
import org.jboss.qa.tool.saatr.web.page.BuildPage.CompareBuildsEvent;
import org.jboss.qa.tool.saatr.web.page.BuildPage.CompareTestsuitesEvent;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public abstract class AbstractBuildsPanel extends GenericPanel<Build> {

    @SpringBean
    protected BuildRepository buildRepository;

    protected Label selectedCount;

    public AbstractBuildsPanel(String id, IModel<Build> model, IModel<BuildFilter> filterModel) {
        super(id, model);
        selectedCount = new Label("selectedCount", new PropertyModel<>(this, "selectedIds.size"));
        add(selectedCount.setOutputMarkupId(true));
        add(new Link<Void>("compare") {

            @Override
            public void onClick() {
                getPage().send(getPage(), Broadcast.EXACT, new CompareBuildsEvent(getSelectedIds()));
            }
        });
        add(new Link<Void>("showSelected") {

            @Override
            public void onClick() {
                if (!getSelectedIds().isEmpty()) {
                    getPage().send(getPage(), Broadcast.BREADTH, new SubmitFilterEvent(new BuildFilter(getSelectedIds())));
                }
            }
        });
        add(new Link<Void>("compareTestsuites") {

            @Override
            public void onClick() {
                getPage().send(getPage(), Broadcast.EXACT, new CompareTestsuitesEvent(filterModel.getObject().getTestsuiteName(), getSelectedIds()));
            }

            @Override
            public boolean isVisible() {
                return !Strings.isEmpty(filterModel.getObject().getTestsuiteName());
            }
        });
    }

    @Override
    public void onEvent(IEvent<?> event) {
        if (event.getPayload() instanceof CopyToAllSelectedEvent) {
            CopyToAllSelectedEvent copyEvent = (CopyToAllSelectedEvent) event.getPayload();
            for (ObjectId objectId : getSelectedIds()) {
                buildRepository.addOrUpdateProperties(buildRepository.findOne(objectId), copyEvent.getProperties());
            }
            if (getSelectedIds().size() > 0) {
                copyEvent.getFeedbackComponent().info("Successfully copied to " + getSelectedIds().size() + " documents.");
            } else {
                copyEvent.getFeedbackComponent().warn("0 documents selected.");
            }
        }
    }

    public Set<ObjectId> getSelectedIds() {
        return BuildSelection.get().getIds();
    }

    @Data
    @AllArgsConstructor
    public static class CopyToAllSelectedEvent implements Serializable {

        private Set<BuildProperty> properties;

        private Component feedbackComponent;
    }

}
