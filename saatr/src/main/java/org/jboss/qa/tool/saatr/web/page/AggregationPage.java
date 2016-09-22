
package org.jboss.qa.tool.saatr.web.page;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.jboss.qa.tool.saatr.domain.config.QueryDocument;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.repo.config.QueryRepository;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapFeedbackPanel;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dsimko@redhat.com
 */
@Slf4j
@SuppressWarnings("serial")
public class AggregationPage extends BasePage<Void> {

    @Inject
    private BuildRepository buildRepository;

    @Inject
    private QueryRepository queryRepository;

    private QueryDocument query = new QueryDocument();

    public AggregationPage() {
        IModel<String> resultModel = new Model<>();
        Form<Void> form = new Form<Void>("form");
        add(form.setOutputMarkupId(true));
        form.add(new BootstrapFeedbackPanel("feedback", new ContainerFeedbackMessageFilter(form)));
        form.add(new DropDownChoice<String>("category", new PropertyModel<>(this, "query.category"), queryRepository.findDistinctCategories()).add(
                new OnChangeAjaxBehavior() {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        target.add(form);
                    }
                }));
        form.add(new DropDownChoice<QueryDocument>("name", new PropertyModel<>(this, "query"), new AbstractReadOnlyModel<List<QueryDocument>>() {

            @Override
            public List<QueryDocument> getObject() {
                if (query != null && query.getCategory() != null) {
                    return queryRepository.findByCategory(query.getCategory());
                }
                return Collections.emptyList();
            }
        }, new ChoiceRenderer<>("name")).setNullValid(true).add(new OnChangeAjaxBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(form);
            }
        }));
        form.add(new TextArea<>("query", new PropertyModel<>(this, "query.query")));
        form.add(new AjaxButton("search") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    resultModel.setObject(buildRepository.aggregate(query.getQuery()));
                } catch (Exception e) {
                    error(e);
                    log.debug(e.getMessage(), e);
                }
            }
        });
        form.add(new AjaxButton("delete") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    queryRepository.delete(query);
                    query = new QueryDocument();
                } catch (Exception e) {
                    log.debug(e.getMessage(), e);
                    form.error(e);
                }
                target.add(form);
            }

        });
        add(new Label("result", resultModel));
        final ModalWindow modalWindow = new ModalWindow("modal");
        modalWindow.setContent(new WindowContent(ModalWindow.CONTENT_ID));
        modalWindow.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
        modalWindow.setInitialHeight(450);
        modalWindow.setTitle("Save Query as..");
        add(modalWindow);
        form.add(new AjaxButton("saveAs") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                modalWindow.show(target);
            }
        });
    }

    private class WindowContent extends Fragment {

        public WindowContent(String id) {
            super(id, "frag1", AggregationPage.this);
            Form<Void> form = new Form<Void>("form");
            add(form.setOutputMarkupId(true));
            form.add(new BootstrapFeedbackPanel("feedback", new ContainerFeedbackMessageFilter(form)));
            form.add(new TextField<>("category", new PropertyModel<>(AggregationPage.this, "query.category")).setRequired(true));
            form.add(new TextField<>("name", new PropertyModel<>(AggregationPage.this, "query.name")).setRequired(true));
            form.add(new TextArea<>("query", new PropertyModel<>(AggregationPage.this, "query.query")).setRequired(true));
            form.add(new AjaxButton("save") {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    query.setId(null);
                    query = queryRepository.save(query);
                    info("Successfully saved.");
                    target.add(form);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(form);
                }
            });
        }

    }

}
