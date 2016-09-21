
package org.jboss.qa.tool.saatr.web.page;

import static org.jboss.qa.tool.saatr.service.AggregationService.COLLECTIONS;
import static org.jboss.qa.tool.saatr.service.AggregationService.PREDEFINED_PIPELINES;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.service.AggregationService;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapFeedbackPanel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dsimko@redhat.com
 */
@Slf4j
@SuppressWarnings("serial")
public class AggregationPage extends BasePage<Void> {

    @Inject
    private AggregationService service;

    public AggregationPage() {
        IModel<String> pipelinesModel = new Model<>();
        IModel<PredefinedPipelines> predefinedPipelinesModel = new Model<>();
        IModel<CollectionType> collectionModel = new Model<>();
        IModel<String> resultModel = new Model<>();
        Form<Void> form = new Form<Void>("form") {

            @Override
            protected void onSubmit() {
                try {
                    resultModel.setObject(service.aggregate(pipelinesModel.getObject(), collectionModel.getObject()));
                } catch (Exception e) {
                    error(e.getMessage());
                    log.info(e.getMessage(), e);
                }
            }
        };
        add(form.setOutputMarkupId(true));
        form.add(new BootstrapFeedbackPanel("feedback"));
        form.add(new DropDownChoice<CollectionType>("collection", collectionModel, COLLECTIONS, new ChoiceRenderer<>("name")) {
        }.add(new OnChangeAjaxBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(form);
            }
        }));
        form.add(new DropDownChoice<PredefinedPipelines>("predefinedPipelines", predefinedPipelinesModel,
                new AbstractReadOnlyModel<List<PredefinedPipelines>>() {

                    @Override
                    public List<PredefinedPipelines> getObject() {
                        if (collectionModel.getObject() != null) {
                            return PREDEFINED_PIPELINES.get(collectionModel.getObject().getType());
                        }
                        return Collections.emptyList();
                    }
                }, new ChoiceRenderer<>("name")) {
        }.setNullValid(true).add(new OnChangeAjaxBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                pipelinesModel.setObject(predefinedPipelinesModel.getObject().getPipelines());
                target.add(form);
            }
        }));
        form.add(new TextArea<>("pipelines", pipelinesModel));
        add(new Label("result", resultModel));
    }

    @Data
    @AllArgsConstructor
    public static class CollectionType implements Serializable {

        private String name;

        private Class<? extends DocumentWithProperties<?>> type;
    }

    @Data
    @AllArgsConstructor
    public static class PredefinedPipelines implements Serializable {

        private String name;

        private String pipelines;
    }
}
