
package org.jboss.qa.tool.saatr.web.comp.build.compare;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.web.comp.DocumentModel;
import org.jboss.qa.tool.saatr.web.comp.build.BuildsTablePanel;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class CompareBuildFilterPanel extends Panel {

    public CompareBuildFilterPanel(String id, final Set<ObjectId> buildFilterIds) {
        super(id);
        add(new RefreshingView<BuildFilter>("builds") {

            @Override
            protected Iterator<IModel<BuildFilter>> getItemModels() {
                return buildFilterIds.stream().map(id -> (IModel<BuildFilter>) new DocumentModel<>(BuildFilter.class, id)).collect(Collectors.toList()).iterator();
            }

            @Override
            protected void populateItem(Item<BuildFilter> item) {
                item.add(new BuildsTablePanel("build", new DocumentModel<Build>(Build.class, null), item.getModel()));
            }
        });

    }

}