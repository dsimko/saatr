package org.jboss.qa.tool.saatr.web.comp.bootstrap;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

@SuppressWarnings("serial")
public class BootstrapNavbarLink extends Panel {

    private final Class<? extends Page> pageClass;

    public <C extends Page> BootstrapNavbarLink(String id, Class<C> pageClass, IModel<?> label) {
        this(id, pageClass, label, null);
    }

    public <C extends Page> BootstrapNavbarLink(String id, Class<C> pageClass, IModel<?> label, String cssClass) {
        this(id, pageClass, label, cssClass, null);
    }

    public <C extends Page> BootstrapNavbarLink(String id, Class<C> pageClass, IModel<?> label, String cssClassBeforeLabel,
            String cssClassAfterLabel) {
        super(id, label);
        this.pageClass = pageClass;
        BookmarkablePageLink<Void> link = new BookmarkablePageLink<Void>("link", pageClass);
        link.add(newLabel(label));
        link.add(new WebComponent("iconBeforeLabel").add(AttributeAppender.append("class", cssClassBeforeLabel))
                .setVisible(cssClassBeforeLabel != null));
        link.add(new WebComponent("iconAfterLabel").add(AttributeAppender.append("class", cssClassAfterLabel))
                .setVisible(cssClassAfterLabel != null));
        add(link);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        if (isPageActive(getPage().getClass())) {
            tag.append("class", "active", " ");
        }
    }

    protected Component newLabel(IModel<?> label) {
        return new Label("label", label);
    }

    protected boolean isPageActive(Class<? extends Page> actualPageClass) {
        return actualPageClass.equals(pageClass);
    }
}
