package org.jboss.qa.tool.saatr.web.component.build;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.entity.Build.PropertyData;
import org.jboss.qa.tool.saatr.entity.Build.TestsuiteData;
import org.jboss.qa.tool.saatr.web.component.build.addinfo.AddInfoPanel;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class BuildDetailPanel extends GenericPanel<Build> {

    public BuildDetailPanel(String id, final IModel<Build> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new Label("jobName"));
        add(new Label("buildNumber"));
        add(new Label("duration"));
        add(new Label("timestamp"));
        add(new RefreshingView<PropertyData>("properties") {
            @Override
            protected Iterator<IModel<PropertyData>> getItemModels() {
                return getModelObject().getProperties().stream().sorted().map(p -> (IModel<PropertyData>) new CompoundPropertyModel<>(p))
                        .iterator();
            }

            @Override
            protected void populateItem(Item<PropertyData> item) {
                item.add(new Label("name"));
                item.add(new Label("value"));
            }
        });
        add(new AddInfoPanel<>("addinfoPanel", model));
        add(new RefreshingView<TestsuiteData>("testsuites") {
            @Override
            protected Iterator<IModel<TestsuiteData>> getItemModels() {
                List<IModel<TestsuiteData>> models = new ArrayList<>();
                for (TestsuiteData testsuiteData : getModelObject().getTestsuites()) {
                    models.add(new CompoundPropertyModel<>(testsuiteData));
                }
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<TestsuiteData> item) {
                item.add(new TestsuitePanel("testsuite", item.getModel()));
            }
        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(getModelObject() != null);
    }

}