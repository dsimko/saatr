
package org.jboss.qa.tool.saatr.web.comp.build.properties;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.config.ConfigDocument.ConfigProperty;

/**
 * Panel for filling fields with values.
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
class PropertyTextAreaPanel extends GenericPanel<ConfigProperty> {

    public PropertyTextAreaPanel(String id, final IModel<ConfigProperty> model) {
        super(id, model);
        add(new Label("name"));
        add(new TextArea<>("value"));
    }
}