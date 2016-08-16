package org.jboss.qa.tool.saatr.web.component.build;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.jboss.qa.tool.saatr.entity.Build.TestsuiteData.TestcaseData.ErrorData;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class ErrorPanel extends GenericPanel<ErrorData> {

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(getModelObject() != null);
    }

    public ErrorPanel(String id) {
        super(id);
        add(new Label("error.value"));
        add(new Label("error.message"));
        add(new Label("error.type"));
    }
}