
package org.jboss.qa.tool.saatr.web.page;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.CloseButtonCallback;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
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
public class AggregationPage extends BasePage<QueryDocument> {

    @Inject
    private BuildRepository buildRepository;

    @Inject
    private QueryRepository queryRepository;

    public AggregationPage() {
        super(Model.of(new QueryDocument()));
        IModel<String> resultModel = new Model<>();
        Form<Void> form = new Form<Void>("form");
        add(form.setOutputMarkupId(true));
        form.add(new BootstrapFeedbackPanel("feedback", new ContainerFeedbackMessageFilter(form)));
        form.add(new DropDownChoice<String>("category", new PropertyModel<>(getModel(), "category"), queryRepository.findDistinctCategories()).add(
                new OnChangeAjaxBehavior() {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        target.add(form);
                    }
                }));
        form.add(new DropDownChoice<QueryDocument>("name", getModel(), new AbstractReadOnlyModel<List<QueryDocument>>() {

            @Override
            public List<QueryDocument> getObject() {
                if (getModelObject() != null && getModelObject().getCategory() != null) {
                    return queryRepository.findByCategory(getModelObject().getCategory());
                }
                return Collections.emptyList();
            }
        }, new ChoiceRenderer<>("name", "id")).setNullValid(true).add(new OnChangeAjaxBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(form);
            }
        }));
        form.add(new TextArea<>("query", new PropertyModel<>(getModel(), "query")));
        form.add(new Button("search") {

            @Override
            public void onSubmit() {
                try {
                    resultModel.setObject(buildRepository.aggregate(AggregationPage.this.getModelObject().getQuery()));
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
                    queryRepository.delete(AggregationPage.this.getModelObject());
                    AggregationPage.this.setModelObject(new QueryDocument());
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
        modalWindow.setCloseButtonCallback(new CloseButtonCallback() {

            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget target) {
                target.add(form);
                return true;
            }
        });
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
            form.add(new TextField<>("category", new PropertyModel<>(AggregationPage.this.getModel(), "category")).setRequired(true));
            form.add(new TextField<>("name", new PropertyModel<>(AggregationPage.this.getModel(), "name")).setRequired(true));
            form.add(new TextArea<>("query", new PropertyModel<>(AggregationPage.this.getModel(), "query")).setRequired(true));
            form.add(new AjaxButton("save") {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    QueryDocument query = AggregationPage.this.getModelObject();
                    query.setId(null);
                    queryRepository.save(query);
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
