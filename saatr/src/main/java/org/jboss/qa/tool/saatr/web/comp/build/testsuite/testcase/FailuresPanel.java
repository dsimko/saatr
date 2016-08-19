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

    public FailuresPanel(String id) {
        super(id);
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
                item.add(new Label("value"));
                item.add(new Label("message"));
                item.add(new Label("type"));
            }
        });
    }
}