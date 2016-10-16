
package org.jboss.qa.tool.saatr.web.comp;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CharSequenceResource;
import org.jboss.qa.tool.saatr.web.comp.build.ConsoleTextResource;
import org.springframework.util.StringUtils;

@SuppressWarnings("serial")
public class LongTextLabel extends GenericPanel<String> {

    private static final int MAX_PREVIEW_LENGTH = 100_000;

    private static final int MAX_PREVIEW_LENGTH_WITHOUT_OPEN_ON_NEW_PAGE_LINK = 1_000;

    private IModel<String> previewModel = new Model<>();

    private IModel<String> linkLabelModel = new Model<>();

    private final Component link;

    public LongTextLabel(final String id, final IModel<String> model) {
        super(id, model);
        add(new Label("text", previewModel));
        add(link = new ResourceLink<>("fullLink", new CharSequenceResource(ConsoleTextResource.CONTENT_TYPE, getModelObject())).add(
                new Label("label", linkLabelModel)));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (StringUtils.isEmpty(getModelObject())) {
            setVisible(false);
        } else {
            if (getModelObject().length() > MAX_PREVIEW_LENGTH) {
                previewModel.setObject(getModelObject().substring(0, MAX_PREVIEW_LENGTH));
                linkLabelModel.setObject("See full output");
            } else if (getModelObject().length() > MAX_PREVIEW_LENGTH_WITHOUT_OPEN_ON_NEW_PAGE_LINK) {
                previewModel.setObject(getModelObject());
                linkLabelModel.setObject("Open on new page");
            } else {
                previewModel.setObject(getModelObject());
                link.setVisible(false);
            }
        }
    }
}
