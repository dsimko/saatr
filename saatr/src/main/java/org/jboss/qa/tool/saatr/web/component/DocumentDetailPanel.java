package org.jboss.qa.tool.saatr.web.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.util.DocumentUtils;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class DocumentDetailPanel extends GenericPanel<Build> {

    public DocumentDetailPanel(String id, final IModel<Build> model) {
        super(id, model);
        add(new Label("json", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return DocumentUtils.toJson(getModelObject());
            }
        }).setEscapeModelStrings(false));
    }
}