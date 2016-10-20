
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
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.CheckedFolder;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.HumanTheme;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.domain.hierarchical.JobRun;
import org.jboss.qa.tool.saatr.domain.hierarchical.JobRunFilter;
import org.jboss.qa.tool.saatr.repo.build.JobRunRepository;
import org.jboss.qa.tool.saatr.web.comp.build.JobRunsTreeTablePanel.JobRunDto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class JobRunsTreePanel extends GenericPanel<JobRun> {

    private static final String BUILD_PARAM_NAME = "build";

    @SpringBean
    private JobRunRepository buildRepository;

    private NestedTree<JobRunDto> tree;

    private Set<String> selectedParents = new HashSet<>();

    private Set<ObjectId> selectedIds = new HashSet<>();

    public JobRunsTreePanel(String id, IModel<JobRun> model, IModel<BuildFilter> filterModel) {
        super(id, model);

        final List<IColumn<JobRunDto, String>> columns = new ArrayList<>();
        columns.add(new TreeColumn<JobRunDto, String>(Model.of("Job Name")));
        columns.add(new PropertyColumn<JobRunDto, String>(Model.of("Count"), "childCount"));
        columns.add(new PropertyColumn<JobRunDto, String>(Model.of("Build"), "buildNumber"));
        // columns.add(new StatsColumn());
        // columns.add(new StatusColumn());
        final Label selectedCount = new Label("selectedCount", new PropertyModel<>(this, "selectedIds.size"));
        add(selectedCount.setOutputMarkupId(true));
        tree = new NestedTree<JobRunDto>("tree", new BuildsProvider(filterModel), new BuildsExpansionModel()) {

            @Override
            protected Component newContentComponent(String id, IModel<JobRunDto> model) {

                return new CheckedFolder<JobRunDto>(id, tree, model) {

                    @Override
                    protected IModel<Boolean> newCheckBoxModel(final IModel<JobRunDto> model) {
                        return new CheckBoxModel(model);
                    }

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        final String name = getModelObject().getNameWithConfiguration();
                        tree.visitChildren(CheckedFolder.class, new IVisitor<CheckedFolder<JobRunDto>, Void>() {

                            @Override
                            public void component(final CheckedFolder<JobRunDto> item, IVisit<Void> visit) {
                                if (item.getModelObject().getNameWithConfiguration().startsWith(name)) {
                                    target.add(item);
                                    visit.stop();
                                }
                                visit.dontGoDeeper();
                            }
                        });
                        target.add(selectedCount);
                    }

                    @Override
                    protected boolean isSelected() {
                        return JobRunsTreePanel.this.getModelObject() != null && JobRunsTreePanel.this.getModelObject().equals(getModelObject());
                    }

                    @Override
                    protected IModel<?> newLabelModel(IModel<JobRunDto> model) {
                        return new AbstractReadOnlyModel<String>() {

                            @Override
                            public String getObject() {
                                JobRunDto jobRun = model.getObject();
                                if (jobRun != null) {
                                    if (jobRun.getId() != null) {
                                        // TODO
                                        return jobRun.getName();
                                    } else if (jobRun.getConfiguration() != null) {
                                        return jobRun.getConfiguration();
                                    } else {
                                        return jobRun.getName();
                                    }
                                }
                                return null;
                            }
                        };
                    }

                    @Override
                    protected MarkupContainer newLinkComponent(String id, IModel<JobRunDto> model) {
                        JobRunDto build = model.getObject();
                        if (tree.getProvider().hasChildren(build)) {
                            return super.newLinkComponent(id, model);
                        } else {
                            PageParameters parameters = new PageParameters(JobRunsTreePanel.this.getPage().getPageParameters());
                            parameters.set(BUILD_PARAM_NAME, build.getId());
                            return new BookmarkablePageLink<>(id, tree.getPage().getClass(), parameters);
                        }
                    }
                }.setOutputMarkupId(true);

            }

        };

        // tree.getTable().addTopToolbar(new HeadersToolbar<>(tree.getTable(), null));
        // tree.getTable().addBottomToolbar(new NoRecordsToolbar(tree.getTable()));
        tree.add(new HumanTheme());
        add(tree);
        add(new Link<Void>("expandAll") {

            @Override
            public void onClick() {
                JobRunExpansion.get().expandAll();
            }
        });
        add(new Link<Void>("collapseAll") {

            @Override
            public void onClick() {
                JobRunExpansion.get().collapseAll();
            }
        });
        add(new AjaxLink<Void>("selectAll") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                tree.visitChildren(CheckedFolder.class, new IVisitor<CheckedFolder<JobRunDto>, Void>() {

                    @Override
                    public void component(CheckedFolder<JobRunDto> component, IVisit<Void> visit) {
                        JobRunDto buildDocument = component.getModelObject();
                        if (buildDocument.getId() != null) {
                            selectedIds.add(buildDocument.getId());
                        } else {
                            selectedParents.add(buildDocument.getNameWithConfiguration());
                        }
                        target.add(component);
                        visit.dontGoDeeper();
                    }
                });
                target.add(selectedCount);
            }
        });
        add(new AjaxLink<Void>("deselectAll") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                selectedIds.clear();
                selectedParents.clear();
                tree.visitChildren(CheckedFolder.class, new IVisitor<CheckedFolder<JobRunDto>, Void>() {

                    @Override
                    public void component(final CheckedFolder<JobRunDto> item, IVisit<Void> visit) {
                        target.add(item);
                        visit.dontGoDeeper();
                    }
                });
                target.add(selectedCount);
            }
        });
        add(new AjaxLink<Void>("addColumn") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                // FIXME
                // columns.add(columns.size() - 1, new VariableColumn("EAP ver.",
                // "EAP_VERSION"));
                target.add(tree);
            }
        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        String buildId = getPage().getPageParameters().get(BUILD_PARAM_NAME).toString(null);
        if (buildId != null) {
            JobRun build = buildRepository.findOne(new ObjectId(buildId));
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

    private class BuildsExpansionModel implements IModel<Set<JobRunDto>> {

        @Override
        public Set<JobRunDto> getObject() {
            return JobRunExpansion.get();
        }

        @Override
        public void setObject(Set<JobRunDto> object) {

        }

        @Override
        public void detach() {

        }
    }

    @AllArgsConstructor
    private class CheckBoxModel extends Model<Boolean> {

        final IModel<JobRunDto> model;

        @Override
        public Boolean getObject() {
            String jobName = model.getObject().getNameWithConfiguration();
            ObjectId id = model.getObject().getId();
            if (id != null) {
                return selectedIds.contains(id);
            } else {
                return selectedParents.contains(jobName);
            }
        }

        @Override
        public void setObject(Boolean object) {
            String jobName = model.getObject().getNameWithConfiguration();
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
                    if (tree.getProvider().hasChildren(model.getObject())) {
                        Iterator<? extends JobRunDto> it = tree.getProvider().getChildren(model.getObject());
                        while (it.hasNext()) {
                            JobRunDto b = it.next();
                            if (b.getId() != null) {
                                if (selectedIds.contains(b.getId())) {
                                    selectedIds.remove(b.getId());
                                }
                            } else {
                                if (selectedParents.contains(b.getNameWithConfiguration())) {
                                    selectedParents.remove(b.getNameWithConfiguration());
                                    if (tree.getProvider().hasChildren(b)) {
                                        Iterator<? extends JobRunDto> it2 = tree.getProvider().getChildren(b);
                                        while (it2.hasNext()) {
                                            JobRunDto c = it2.next();
                                            if (selectedIds.contains(c.getId())) {
                                                selectedIds.remove(c.getId());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    selectedParents.add(jobName);
                    if (tree.getProvider().hasChildren(model.getObject())) {
                        Iterator<? extends JobRunDto> it = tree.getProvider().getChildren(model.getObject());
                        while (it.hasNext()) {
                            JobRunDto b = it.next();
                            if (b.getId() != null) {
                                if (!selectedIds.contains(b.getId())) {
                                    selectedIds.add(b.getId());
                                }
                            } else {
                                if (!selectedParents.contains(b.getNameWithConfiguration())) {
                                    selectedParents.add(b.getNameWithConfiguration());
                                    if (tree.getProvider().hasChildren(b)) {
                                        Iterator<? extends JobRunDto> it2 = tree.getProvider().getChildren(b);
                                        while (it2.hasNext()) {
                                            JobRunDto c = it2.next();
                                            if (!selectedIds.contains(c.getId())) {
                                                selectedIds.add(c.getId());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static class BuildsProvider implements ITreeProvider<JobRunDto> {

        final IModel<BuildFilter> filter;

        @SpringBean
        JobRunRepository buildRepository;

        BuildsProvider(IModel<BuildFilter> filter) {
            this.filter = filter;
            Injector.get().inject(this);
        }

        @Override
        public void detach() {
            filter.detach();
        }

        @Override
        public Iterator<? extends JobRunDto> getRoots() {
            return buildRepository.getRoots(JobRunFilter.create(filter.getObject()));
        }

        @Override
        public boolean hasChildren(JobRunDto node) {
            if (node == null) {
                // this is because Junction component relies on it in 'isEnabled' method
                return true;
            } else {
                return node.getId() == null;
            }
        }

        @Override
        public Iterator<? extends JobRunDto> getChildren(JobRunDto node) {
            return buildRepository.getChildren(node, JobRunFilter.create(filter.getObject()));
        }

        @Override
        public IModel<JobRunDto> model(JobRunDto object) {
            return new Model<JobRunDto>(object);
        }
    }

    @Data
    @AllArgsConstructor
    public static class CopyToAllSelectedEvent implements Serializable {

        private Set<PropertyData> properties;

        private Component feedbackComponent;
    }
}
