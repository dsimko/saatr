
package org.jboss.qa.tool.saatr.web.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.HumanTheme;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.Status;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapTabbedPanel;
import org.jboss.qa.tool.saatr.web.comp.build.BuildProvider.BuildFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
@Slf4j
public class BuildPage2 extends BasePage<BuildDocument> {

    private IModel<BuildFilter> filter = Model.of(new BuildFilter());

    private List<String> variableNames = new ArrayList<>();

    private List<String> variableValues = new ArrayList<>();

    private AbstractTree<BuildDocument> tree;

    @Inject
    private BuildRepository buildRepository;

    public BuildPage2() {
        super(new Model<BuildDocument>());
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

        final Behavior theme = new HumanTheme();
        tree = createTree(new FooProvider(), new FooExpansionModel());
        tree.add(new Behavior() {

            private static final long serialVersionUID = 1L;

            @Override
            public void onComponentTag(Component component, ComponentTag tag) {
                theme.onComponentTag(component, tag);
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                theme.renderHead(component, response);
            }
        });
        add(tree);

        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add(new AbstractTab(new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return "<span class=\"glyphicon glyphicon-th-list\"></span> Tables";
            }
        }) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new EmptyPanel(panelId);
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
                return new EmptyPanel(panelId);
            }
        });
        add(new BootstrapTabbedPanel<>("tabs", tabs));
        add(new Link<Void>("expandAll") {

            @Override
            public void onClick() {
                FooExpansion.get().expandAll();
            }
        });

        add(new Link<Void>("collapseAll") {

            @Override
            public void onClick() {
                FooExpansion.get().collapseAll();
            }
        });
    }

    protected AbstractTree<BuildDocument> createTree(FooProvider provider, IModel<Set<BuildDocument>> state) {
        List<IColumn<BuildDocument, String>> columns = createColumns();

        final TableTree<BuildDocument, String> tree = new TableTree<BuildDocument, String>("tree", columns, provider, Integer.MAX_VALUE, state) {

            private static final long serialVersionUID = 1L;

            @Override
            protected Component newContentComponent(String id, IModel<BuildDocument> model) {
                return BuildPage2.this.newContentComponent(id, model);
            }

            @Override
            protected Item<BuildDocument> newRowItem(String id, int index, IModel<BuildDocument> model) {
                return new OddEvenItem<>(id, index, model);
            }
        };
        tree.getTable().addTopToolbar(new HeadersToolbar<>(tree.getTable(), null));
        tree.getTable().addBottomToolbar(new NoRecordsToolbar(tree.getTable()));
        return tree;
    }

    private List<IColumn<BuildDocument, String>> createColumns() {
        List<IColumn<BuildDocument, String>> columns = new ArrayList<>();

        // columns.add(new PropertyColumn<BuildDocumentDto, String>(Model.of("ID"),
        // "id"));

        columns.add(new TreeColumn<BuildDocument, String>(Model.of("Tree")));
        columns.add(new PropertyColumn<BuildDocument, String>(Model.of("JobStatus"), "jobStatus"));

        return columns;
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

    protected Component newContentComponent(String id, IModel<BuildDocument> model) {
        return new Label(id, model);
    }

    private class FooExpansionModel implements IModel<Set<BuildDocument>> {

        @Override
        public Set<BuildDocument> getObject() {
            return FooExpansion.get();
        }

        @Override
        public void setObject(Set<BuildDocument> object) {

        }

        @Override
        public void detach() {

        }
    }

    private static class FooProvider implements ITreeProvider<BuildDocument> {

        @SpringBean
        private BuildRepository buildRepository;

        public FooProvider() {
            Injector.get().inject(this);
        }

        @Override
        public void detach() {
            // TODO Auto-generated method stub

        }

        @Override
        public Iterator<? extends BuildDocument> getRoots() {
            return buildRepository.getRoots();
        }

        @Override
        public boolean hasChildren(BuildDocument node) {
            if (node.getJobName().contains("/")) {
                return node.getNumberOfChildren() != null && node.getNumberOfChildren() > 1;
            }
            return node.getNumberOfChildren() > 0;
        }

        @Override
        public Iterator<? extends BuildDocument> getChildren(BuildDocument node) {
            return buildRepository.getChildren(node);
        }

        @Override
        public IModel<BuildDocument> model(BuildDocument object) {
            // TODO Auto-generated method stub
            return Model.of(object);
        }

    }
}
