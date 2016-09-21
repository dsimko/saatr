
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
import org.apache.wicket.model.CompoundPropertyModel;
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

    private final QueryDocument query = new QueryDocument();

    public AggregationPage() {
        IModel<String> pipelinesModel = new Model<>();
        IModel<QueryDocument> predefinedPipelinesModel = new Model<>();
        IModel<String> collectionModel = new Model<>();
        IModel<String> resultModel = new Model<>();
        Form<Void> form = new Form<Void>("form") {

            @Override
            protected void onSubmit() {
                try {
                    resultModel.setObject(buildRepository.aggregate(pipelinesModel.getObject()));
                } catch (Exception e) {
                    error(e.getMessage());
                    log.info(e.getMessage(), e);
                }
            }
        };
        add(form.setOutputMarkupId(true));
        form.add(new BootstrapFeedbackPanel("feedback", new ContainerFeedbackMessageFilter(form)));
        form.add(new DropDownChoice<String>("collection", collectionModel, queryRepository.findDistinctCategories()) {
        }.add(new OnChangeAjaxBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                query.setCategory(collectionModel.getObject());
                target.add(form);
            }
        }));
        form.add(new DropDownChoice<QueryDocument>("predefinedPipelines", predefinedPipelinesModel, new AbstractReadOnlyModel<List<QueryDocument>>() {

            @Override
            public List<QueryDocument> getObject() {
                if (collectionModel.getObject() != null) {
                    return queryRepository.findByCategory(collectionModel.getObject());
                }
                return Collections.emptyList();
            }
        }, new ChoiceRenderer<>("name")) {
        }.setNullValid(true).add(new OnChangeAjaxBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                pipelinesModel.setObject(predefinedPipelinesModel.getObject().getQuery());
                query.setName(predefinedPipelinesModel.getObject().getName());
                query.setQuery(predefinedPipelinesModel.getObject().getQuery());
                target.add(form);
            }
        }));
        form.add(new TextArea<>("pipelines", pipelinesModel));
        add(new Label("result", resultModel));
        final ModalWindow modalWindow = new ModalWindow("modal");
        modalWindow.setContent(new WindowContent(ModalWindow.CONTENT_ID, new PropertyModel<>(this, "query")));
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

        public WindowContent(String id, IModel<QueryDocument> model) {
            super(id, "frag1", AggregationPage.this, new CompoundPropertyModel<>(model));
            Form<Void> form = new Form<Void>("form");
            add(form.setOutputMarkupId(true));
            form.add(new BootstrapFeedbackPanel("feedback", new ContainerFeedbackMessageFilter(form)));
            form.add(new TextField<>("category").setRequired(true));
            form.add(new TextField<>("name").setRequired(true));
            form.add(new TextArea<>("query").setRequired(true));
            form.add(new AjaxButton("save") {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    queryRepository.save(model.getObject());
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
