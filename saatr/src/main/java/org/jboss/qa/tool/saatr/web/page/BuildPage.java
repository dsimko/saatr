
package org.jboss.qa.tool.saatr.web.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebApplication;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.Status;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.comp.DocumentModel;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapTabbedPanel;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapTable;
import org.jboss.qa.tool.saatr.web.comp.build.BuildJsonPanel;
import org.jboss.qa.tool.saatr.web.comp.build.BuildPanel;
import org.jboss.qa.tool.saatr.web.comp.build.BuildProvider;
import org.jboss.qa.tool.saatr.web.comp.build.BuildProvider.BuildFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
@Slf4j
public class BuildPage extends BasePage<BuildDocument> {

    private IModel<BuildFilter> filter = Model.of(new BuildFilter());

    private List<String> variableNames = new ArrayList<>();

    private List<String> variableValues = new ArrayList<>();

    @Inject
    private BuildRepository buildRepository;

    public BuildPage() {
        super(new DocumentModel<BuildDocument>(BuildDocument.class, null));
        initVariables();
        Form<BuildFilter> form = new Form<>("form", new CompoundPropertyModel<BuildFilter>(filter));
        form.add(new TextField<>("jobName"));
        form.add(new TextField<>("buildNumber"));
        form.add(new DropDownChoice<>("status", Arrays.asList(Status.values())).setNullValid(true));
        form.add(new DropDownChoice<>("variableName", variableNames).setNullValid(true));
        AutoCompleteSettings settings = new AutoCompleteSettings();
        settings.setShowListOnEmptyInput(true);
        form.add(new AutoCompleteTextField<String>("variableValue", settings) {

            @Override
            protected Iterator<String> getChoices(String input) {
                List<String> choices = new ArrayList<>(10);
                for (final String option : variableValues) {
                    if (option.toLowerCase().startsWith(input.toLowerCase())) {
                        choices.add(option);
                        if (choices.size() == 10) {
                            break;
                        }
                    }
                }
                return choices.iterator();
            }
        });

        form.add(new Link<Void>("clear") {

            @Override
            public void onClick() {
                filter.setObject(new BuildFilter());
            }
        });
        add(form);
        List<IColumn<BuildDocument, String>> columns = new ArrayList<IColumn<BuildDocument, String>>();
        columns.add(new PropertyColumn<BuildDocument, String>(new Model<String>("Job Name"), "jobName"));
        columns.add(new PropertyColumn<BuildDocument, String>(new Model<String>("Build Number"), "buildNumber"));
        columns.add(new PropertyColumn<BuildDocument, String>(new Model<String>("Status"), "status") {

            @Override
            public void populateItem(final Item<ICellPopulator<BuildDocument>> item, final String componentId, final IModel<BuildDocument> rowModel) {
                item.add(new Label(componentId, new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return getStatusHtml(getModelObject());
                    }
                }).setEscapeModelStrings(false));
            }
        });

        BootstrapTable<BuildDocument, String> dataTable = new BootstrapTable<BuildDocument, String>("table", columns, new BuildProvider(filter), 10,
                getModel()) {

            @Override
            protected void selectRow(BuildDocument build) {
                setModelObject(build);
            }
        };
        add(dataTable);
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add(new AbstractTab(new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return "<span class=\"glyphicon glyphicon-th-list\"></span> Tables";
            }
        }) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new BuildPanel(panelId, getModel());
            }
        });

        tabs.add(new AbstractTab(new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return "<span class=\"glyphicon glyphicon-paste\"></span> JSON";
            }
        }) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new BuildJsonPanel(panelId, getModel());
            }
        });
        add(new BootstrapTabbedPanel<>("tabs", tabs));
    }

    private void initVariables() {
        long start = System.currentTimeMillis();
        buildRepository.findDistinctVariableNames().forEach(name -> variableNames.add(name));
        buildRepository.findDistinctVariableValues().forEach(val -> variableValues.add(val));
        log.debug("Loading variables filter took {} ms.", System.currentTimeMillis() - start);
    }

    public static String getStatusHtml(BuildDocument build) {
        StringBuilder builder = new StringBuilder();
        builder.append("<img src=\"");
        builder.append(WebApplication.get().getServletContext().getContextPath());
        builder.append("/images/");
        if (build == null || build.getStatus() == null) {
            builder.append("aborted16.png\" />");
            return builder.toString();
        } else {
            Status status = build.getStatus();
            if (status == Status.Failed) {
                builder.append("yellow16.png");
            } else {
                builder.append("blue16.png");
            }
            builder.append("\" /> ");
            builder.append(status);
            return builder.toString();
        }
    }

}
