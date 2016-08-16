package org.jboss.qa.tool.saatr.web.component.build;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.util.IOUtils;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class BuildJsonPanel extends GenericPanel<Build> {

    public BuildJsonPanel(String id, final IModel<Build> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new Label("json", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return IOUtils.toJson(model.getObject());
            }
        }).setEscapeModelStrings(false));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(getModelObject() != null);
    }
}