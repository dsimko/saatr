
package org.jboss.qa.tool.saatr.web.comp.bootstrap;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

@SuppressWarnings("serial")
public class BootstrapTabbedPanel<T extends ITab> extends TabbedPanel<T> {

    /**
     * {@inheritDoc}
     */
    public BootstrapTabbedPanel(String id, List<T> tabs) {
        super(id, tabs);
    }

    @Override
    protected String getTabContainerCssClass() {
        return "nav nav-tabs";
    }

    @Override
    protected String getSelectedTabCssClass() {
        return "active";
    }

    @Override
    protected Component newTitle(String titleId, IModel<?> titleModel, int index) {
        Label label = new Label(titleId, titleModel);
        return label.setEscapeModelStrings(false);
    }

}
