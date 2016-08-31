package org.jboss.qa.tool.saatr.web.comp.build.testsuite.testcase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.entity.TestcaseData.FailureData;
import org.jboss.qa.tool.saatr.web.comp.HideableLabel;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
class FailuresPanel extends GenericPanel<List<FailureData>> {

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(!getModelObject().isEmpty());
    }

    public FailuresPanel(String id, String name) {
        super(id);
        add(new Label("name", name));
        add(new RefreshingView<FailureData>("failures") {

            @Override
            protected Iterator<IModel<FailureData>> getItemModels() {
                List<IModel<FailureData>> models = new ArrayList<>();
                for (FailureData failureData : getModelObject()) {
                    models.add(new CompoundPropertyModel<>(failureData));
                }
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<FailureData> item) {
                item.add(new HideableLabel("value"));
                item.add(new HideableLabel("message"));
                item.add(new HideableLabel("type"));
            }
        });
    }
}