package org.jboss.qa.tool.saatr.web.page;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.jboss.qa.tool.saatr.entity.Document;
import org.jboss.qa.tool.saatr.entity.Field;
import org.jboss.qa.tool.saatr.util.DocumentUtils;
import org.jboss.qa.tool.saatr.web.component.FieldPanel;
import org.jboss.qa.tool.saatr.web.component.bootstrap.BootstrapFeedbackPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * This page generates form based on given document for filling its values.
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class DocumentPage extends BasePage<Document> {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentPage.class);

    public DocumentPage() {
        redirectToHomePage();
    }

    public DocumentPage(final Document document) {
        Form<Document> form = new StatelessForm<Document>("form");
        form.add(new BootstrapFeedbackPanel("feedback"));
        RepeatingView view = new RepeatingView("props");
        for (Field prop : document.getFields()) {
            view.add(new FieldPanel(view.newChildId(), new CompoundPropertyModel<Field>(prop)));
        }
        form.add(view);
        form.add(new Button("submit") {
            @Override
            public void onSubmit() {
                try {
                    String json = DocumentUtils.persist(document);
                    setResponsePage(new InfoPage(json));
                } catch (JsonProcessingException e) {
                    LOG.warn(e.getMessage(), e);
                    error(e.getMessage());
                }
            }
        });
        form.add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                redirectToHomePage();
            }
        });
        add(form);
    }
}