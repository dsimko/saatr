package org.jboss.qa.tool.saatr.web.comp;

import java.io.Serializable;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

@SuppressWarnings("serial")
public class HideableLabel extends Label {

    @Override
    protected void onConfigure() {
        super.onConfigure();
        String value = getDefaultModelObjectAsString();
        if (Strings.isEmpty(value)) {
            setVisible(false);
        }
    }

    /**
     * Constructor
     * 
     * @param id
     *            See Component
     */
    public HideableLabel(final String id) {
        super(id);
    }

    /**
     * Convenience constructor. Same as Label(String, Model.of(Serializable))
     * 
     * @param id
     *            See Component
     * @param label
     *            The label text or object, converted to a string via the
     *            {@link org.apache.wicket.util.convert.IConverter}.
     * 
     * @see org.apache.wicket.Component#Component(String, IModel)
     */
    public HideableLabel(final String id, Serializable label) {
        super(id, Model.of(label));
    }

    /**
     * @param id
     * @param model
     * @see org.apache.wicket.Component#Component(String, IModel)
     */
    public HideableLabel(final String id, IModel<?> model) {
        super(id, model);
    }

}
