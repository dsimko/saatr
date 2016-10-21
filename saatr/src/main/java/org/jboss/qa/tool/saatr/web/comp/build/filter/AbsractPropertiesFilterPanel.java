
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter.PropertyDto;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dsimko@redhat.com
 */
@Slf4j
@SuppressWarnings("serial")
public abstract class AbsractPropertiesFilterPanel extends GenericPanel<BuildFilter> {

    private List<String> variableNames = new ArrayList<>();

    private Map<Integer, List<String>> variableValues = new HashedMap<>();

    @SpringBean
    protected BuildRepository buildRepository;

    public AbsractPropertiesFilterPanel(String id, IModel<BuildFilter> model) {
        super(id, model);
        initVariables();
        setOutputMarkupId(true);
        add(new RefreshingView<PropertyDto>("properties") {

            @Override
            protected Iterator<IModel<PropertyDto>> getItemModels() {
                return getProperties().stream().map(p -> ((IModel<PropertyDto>) Model.of(p))).collect(Collectors.toList()).iterator();
            }

            @Override
            protected void populateItem(Item<PropertyDto> item) {
                List<String> values = variableValues.get(item.getIndex());
                if (values == null) {
                    values = new ArrayList<>();
                    variableValues.put(item.getIndex(), values);
                }
                item.add(new BuildsPropertyFilterPanel("property", getTitle(), item.getModel(), variableNames, values,
                        item.getIndex() == getProperties().size() - 1) {

                    @Override
                    protected Iterable<String> findDistinctValues(String name) {
                        return getPropertyValues(name);
                    }
                });
            }
        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (getProperties().isEmpty()) {
            getProperties().add(new PropertyDto());
        }
    }

    @Override
    public void onEvent(IEvent<?> event) {
        if (event.getPayload() instanceof AddPropertyEvent) {
            getProperties().add(new PropertyDto());
            ((AddPropertyEvent) event.getPayload()).target.add(this);
        } else if (event.getPayload() instanceof RemovePropertyEvent) {
            getProperties().remove(getProperties().size() - 1);
            ((RemovePropertyEvent) event.getPayload()).target.add(this);
        }
    }

    protected void initVariables() {
        long start = System.currentTimeMillis();
        variableNames.clear();
        variableValues.clear();
        getPropertyNames().forEach(name -> variableNames.add(name));
        log.debug("Loading variables filter took {} ms.", System.currentTimeMillis() - start);
    }

    @AllArgsConstructor
    static class AddPropertyEvent implements Serializable {

        AjaxRequestTarget target;
    }

    @AllArgsConstructor
    static class RemovePropertyEvent implements Serializable {

        AjaxRequestTarget target;
    }

    protected abstract String getTitle();

    protected abstract List<PropertyDto> getProperties();

    protected abstract Iterable<String> getPropertyNames();

    protected abstract Iterable<String> getPropertyValues(String name);

}
