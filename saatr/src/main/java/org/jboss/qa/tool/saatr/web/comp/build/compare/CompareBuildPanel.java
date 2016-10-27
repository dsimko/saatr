
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
import org.jboss.qa.tool.saatr.web.comp.DocumentModel;
import org.jboss.qa.tool.saatr.web.comp.build.BuildPanel;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class CompareBuildPanel extends Panel {

    public CompareBuildPanel(String id, final Set<ObjectId> buildIds) {
        super(id);
        add(new RefreshingView<Build>("builds") {

            @Override
            protected Iterator<IModel<Build>> getItemModels() {
                return buildIds.stream().map(id -> (IModel<Build>) new DocumentModel<>(Build.class, id)).collect(Collectors.toList()).iterator();
            }

            @Override
            protected void populateItem(Item<Build> item) {
                item.add(new BuildPanel("build", item.getModel()));
            }
        });

    }

}