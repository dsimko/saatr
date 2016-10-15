
package org.jboss.qa.tool.saatr.web.comp.build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.CheckedFolder;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.HumanTheme;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.comp.DocumentModel;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class BuildsTreeTablePanel extends GenericPanel<BuildDocument> {

    private static final String BUILD_PARAM_NAME = "build";

    @SpringBean
    private BuildRepository buildRepository;

    private TableTree<BuildDocument, String> tree;

    private Set<String> selectedParents = new HashSet<>();

    private Set<ObjectId> selectedIds = new HashSet<>();

    public BuildsTreeTablePanel(String id, IModel<BuildDocument> model, IModel<BuildFilter> filterModel) {
        super(id, model);

        final List<IColumn<BuildDocument, String>> columns = new ArrayList<>();
        columns.add(new TreeColumn<BuildDocument, String>(Model.of("Job Name")));
        columns.add(new PropertyColumn<BuildDocument, String>(Model.of("Count"), "numberOfChildren"));
        columns.add(new PropertyColumn<BuildDocument, String>(Model.of("Build"), "buildNumber"));
        columns.add(new StatsColumn());
        columns.add(new StatusColumn());
        final Label selectedCount = new Label("selectedCount", new PropertyModel<>(this, "selectedIds.size"));
        add(selectedCount.setOutputMarkupId(true));
        tree = new TableTree<BuildDocument, String>("tree", columns, new BuildsProvider(filterModel), Integer.MAX_VALUE, new BuildsExpansionModel()) {

            @Override
            protected Component newContentComponent(String id, IModel<BuildDocument> model) {

                return new CheckedFolder<BuildDocument>(id, tree, model) {

                    @Override
                    protected IModel<Boolean> newCheckBoxModel(final IModel<BuildDocument> model) {
                        return new CheckBoxModel(model);
                    }

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        tree.updateBranch(getModelObject(), target);
                        target.add(selectedCount);
                    }

                    @Override
                    protected boolean isSelected() {
                        return BuildsTreeTablePanel.this.getModelObject() != null && BuildsTreeTablePanel.this.getModelObject().equals(getModelObject());
                    }

                    @Override
                    protected IModel<?> newLabelModel(IModel<BuildDocument> model) {
                        return new AbstractReadOnlyModel<String>() {

                            @Override
                            public String getObject() {
                                BuildDocument buildDocument = model.getObject();
                                if (buildDocument != null) {
                                    String name = buildDocument.getJobName();
                                    if (name.contains("/")) {
                                        return name.substring(name.indexOf("/") + 1, name.length());
                                    } else {
                                        return name;
                                    }
                                }
                                return null;
                            }
                        };
                    }

                    @Override
                    protected MarkupContainer newLinkComponent(String id, IModel<BuildDocument> model) {
                        BuildDocument build = model.getObject();
                        if (tree.getProvider().hasChildren(build)) {
                            return super.newLinkComponent(id, model);
                        } else {
                            PageParameters parameters = new PageParameters(BuildsTreeTablePanel.this.getPage().getPageParameters());
                            parameters.set(BUILD_PARAM_NAME, build.getId());
                            return new BookmarkablePageLink<>(id, tree.getPage().getClass(), parameters);
                        }
                    }
                };

            }

            @Override
            protected Item<BuildDocument> newRowItem(String id, int index, IModel<BuildDocument> model) {
                return new OddEvenItem<>(id, index, model);
            }

        };
        tree.getTable().addTopToolbar(new HeadersToolbar<>(tree.getTable(), null));
        tree.getTable().addBottomToolbar(new NoRecordsToolbar(tree.getTable()));
        tree.add(new HumanTheme());
        add(tree);
        add(new Link<Void>("expandAll") {

            @Override
            public void onClick() {
                BuildExpansion.get().expandAll();
            }
        });
        add(new Link<Void>("collapseAll") {

            @Override
            public void onClick() {
                BuildExpansion.get().collapseAll();
            }
        });
        add(new AjaxLink<Void>("selectAll") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                tree.visitChildren(CheckedFolder.class, new IVisitor<CheckedFolder<BuildDocument>, Void>() {

                    @Override
                    public void component(CheckedFolder<BuildDocument> component, IVisit<Void> visit) {
                        BuildDocument buildDocument = component.getModelObject();
                        if (buildDocument.getId() != null) {
                            selectedIds.add(buildDocument.getId());
                        } else {
                            selectedParents.add(buildDocument.getJobName());
                        }
                    }
                });
                target.add(tree);
                target.add(selectedCount);
            }
        });
        add(new AjaxLink<Void>("deselectAll") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                selectedIds.clear();
                selectedParents.clear();
                target.add(tree);
                target.add(selectedCount);
            }
        });
        add(new AjaxLink<Void>("addColumn") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                columns.add(columns.size() - 1, new VariableColumn("EAP ver.", "EAP_VERSION"));
                target.add(tree);
            }
        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        String buildId = getPage().getPageParameters().get(BUILD_PARAM_NAME).toString(null);
        if (buildId != null) {
            BuildDocument build = buildRepository.findOne(new ObjectId(buildId));
            if (build != null) {
                setModelObject(build);
            }
        }
    }

    @Override
    public void onEvent(IEvent<?> event) {
        if (event.getPayload() instanceof CopyToAllSelectedEvent) {
            CopyToAllSelectedEvent copyEvent = (CopyToAllSelectedEvent) event.getPayload();
            for (ObjectId objectId : selectedIds) {
                buildRepository.addOrUpdateProperties(buildRepository.findOne(objectId), copyEvent.getProperties());
            }
            if (selectedIds.size() > 0) {
                copyEvent.getFeedbackComponent().info("Successfully copied to " + selectedIds.size() + " documents.");
            } else {
                copyEvent.getFeedbackComponent().warn("0 documents selected.");
            }
        }
    }

    private class BuildsExpansionModel implements IModel<Set<BuildDocument>> {

        @Override
        public Set<BuildDocument> getObject() {
            return BuildExpansion.get();
        }

        @Override
        public void setObject(Set<BuildDocument> object) {

        }

        @Override
        public void detach() {

        }
    }

    @AllArgsConstructor
    private class CheckBoxModel extends Model<Boolean> {

        final IModel<BuildDocument> model;

        @Override
        public Boolean getObject() {
            String jobName = model.getObject().getJobName();
            ObjectId id = model.getObject().getId();
            if (id != null) {
                return selectedIds.contains(id);
            } else {
                return selectedParents.contains(jobName);
            }
        }

        @Override
        public void setObject(Boolean object) {
            String jobName = model.getObject().getJobName();
            ObjectId id = model.getObject().getId();
            if (id != null) {
                if (selectedIds.contains(id)) {
                    selectedIds.remove(id);
                } else {
                    selectedIds.add(id);
                }
            } else {
                if (selectedParents.contains(jobName)) {
                    selectedParents.remove(jobName);
                    tree.getProvider().getChildren(model.getObject()).forEachRemaining(b -> {
                        if (b.getId() != null) {
                            if (selectedIds.contains(b.getId())) {
                                selectedIds.remove(b.getId());
                            }
                        } else {
                            if (selectedParents.contains(b.getJobName())) {
                                selectedParents.remove(b.getJobName());
                                tree.getProvider().getChildren(b).forEachRemaining(c -> {
                                    if (selectedIds.contains(c.getId())) {
                                        selectedIds.remove(c.getId());
                                    }
                                });
                            }
                        }
                    });
                } else {
                    selectedParents.add(jobName);
                    tree.getProvider().getChildren(model.getObject()).forEachRemaining(b -> {
                        if (b.getId() != null) {
                            if (!selectedIds.contains(b.getId())) {
                                selectedIds.add(b.getId());
                            }
                        } else {
                            if (!selectedParents.contains(b.getJobName())) {
                                selectedParents.add(b.getJobName());
                                tree.getProvider().getChildren(b).forEachRemaining(c -> {
                                    if (!selectedIds.contains(c.getId())) {
                                        selectedIds.add(c.getId());
                                    }
                                });
                            }
                        }

                    });
                }
            }
        }
    }

    private static class BuildsProvider implements ITreeProvider<BuildDocument> {

        final IModel<BuildFilter> filter;

        @SpringBean
        BuildRepository buildRepository;

        BuildsProvider(IModel<BuildFilter> filter) {
            this.filter = filter;
            Injector.get().inject(this);
        }

        @Override
        public void detach() {
            filter.detach();
        }

        @Override
        public Iterator<? extends BuildDocument> getRoots() {
            return buildRepository.getRoots(filter.getObject());
        }

        @Override
        public boolean hasChildren(BuildDocument node) {
            if (node == null) {
                // this is because Junction component relies on it in 'isEnabled' method
                return true;
            } else if (node.getJobName().contains("/")) {
                return node.getNumberOfChildren() != null && node.getNumberOfChildren() > 1;
            } else {
                return node.getNumberOfChildren() > 0;
            }
        }

        @Override
        public Iterator<? extends BuildDocument> getChildren(BuildDocument node) {
            return buildRepository.getChildren(node, filter.getObject());
        }

        @Override
        public IModel<BuildDocument> model(BuildDocument object) {
            return new DocumentModel<>(object);
        }
    }

    @Data
    @AllArgsConstructor
    public static class CopyToAllSelectedEvent implements Serializable {

        private Set<PropertyData> properties;

        private Component feedbackComponent;
    }
}
