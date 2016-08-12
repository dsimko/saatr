package org.jboss.qa.tool.saatr.web.component.bootstrap;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

@SuppressWarnings("serial")
public abstract class BootstrapButton<T> extends Link<T> {

    public BootstrapButton(final String id) {
        super(id);
    }

    public BootstrapButton(final String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        if (!isEnabled()) {
            tag.append("class", "disabled", " ");
        }
    }

}
