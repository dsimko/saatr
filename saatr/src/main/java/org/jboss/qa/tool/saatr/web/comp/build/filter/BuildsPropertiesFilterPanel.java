
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
@Slf4j
public class BuildsPropertiesFilterPanel extends GenericPanel<BuildFilter> {

    private List<String> variableNames = new ArrayList<>();

    private List<String> variableValues = new ArrayList<>();

    @Inject
    private BuildRepository buildRepository;

    public BuildsPropertiesFilterPanel(String id, IModel<BuildFilter> model) {
        super(id, model);
        initVariables();
        setOutputMarkupId(true);
        add(new RefreshingView<PropertyData>("properties") {

            @Override
            protected Iterator<IModel<PropertyData>> getItemModels() {
                return getModelObject().getVariables().stream().map(p -> ((IModel<PropertyData>) Model.of(p))).collect(Collectors.toList()).iterator();
            }

            @Override
            protected void populateItem(Item<PropertyData> item) {
                item.add(new BuildsPropertyFilterPanel("property", item.getModel(), new ArrayList<>(variableNames), new ArrayList<>(variableValues)));
            }
        });
        add(new AjaxLink<BuildFilter>("add", model) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                getModelObject().getVariables().add(new PropertyData());
                target.add(BuildsPropertiesFilterPanel.this);
            }
        });
    }

    private void initVariables() {
        long start = System.currentTimeMillis();
        buildRepository.findDistinctVariableNames().forEach(name -> variableNames.add(name));
        buildRepository.findDistinctVariableValues(null).forEach(val -> variableValues.add(val));
        log.debug("Loading variables filter took {} ms.", System.currentTimeMillis() - start);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (getModelObject().getVariables().isEmpty()) {
            getModelObject().getVariables().add(new PropertyData());
        }
    }
}
