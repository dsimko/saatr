package org.jboss.qa.tool.saatr.web.comp.build.properties;

import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.entity.PersistableWithProperties;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class PropertiesPanel<T extends PersistableWithProperties> extends GenericPanel<T> {

    public PropertiesPanel(String id, final IModel<T> model) {
        super(id, model);
        add(new PropertiesViewPanel<>("view", model));
        add(new PropertiesEditPanel<>("edit", model));
    }

}