
package org.jboss.qa.tool.saatr.web.comp.build;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.CheckedFolder;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.HumanTheme;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.domain.build.BuildProperty;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.page.BuildPage.CompareBuildsEvent;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class BuildTreePanel extends GenericPanel<Build> {

    private static final String BUILD_PARAM_NAME = "build";
    public static final String ANCHOR_PARAM_NAME = "anchor";

    @SpringBean
    private BuildRepository buildRepository;

    private NestedTree<Build> tree;

    public BuildTreePanel(String id, IModel<Build> model, IModel<BuildFilter> filterModel) {
        super(id, model);
        final Label selectedCount = new Label("selectedCount", new PropertyModel<>(this, "selectedIds.size"));
        add(selectedCount.setOutputMarkupId(true));
        tree = new NestedTree<Build>("tree", new BuildsProvider(filterModel), new BuildsExpansionModel()) {

            @Override
            protected Component newContentComponent(String id, IModel<Build> model) {

                return new TreeCheckedFolder(id, tree, model) {

                    @Override
                    protected IModel<Boolean> newCheckBoxModel(final IModel<Build> model) {
                        return new CheckBoxModel(model);
                    }

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        final String name = getModelObject().getNameWithConfiguration();
                        tree.visitChildren(CheckedFolder.class, new IVisitor<CheckedFolder<Build>, Void>() {

                            @Override
                            public void component(final CheckedFolder<Build> item, IVisit<Void> visit) {
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
                        return BuildTreePanel.this.getModelObject() != null
                                && BuildTreePanel.this.getModelObject().getId().equals(getModelObject().getId());
                    }

                    @Override
                    protected Component newLabelComponent(String id, IModel<Build> model) {
                        return super.newLabelComponent(id, new PropertyModel<>(model, "contentHtml")).setEscapeModelStrings(false);
                    }

                    @Override
                    protected MarkupContainer newLinkComponent(String id, IModel<Build> model) {
                        Build build = model.getObject();
                        if (tree.getProvider().hasChildren(build)) {
                            return super.newLinkComponent(id, model);
                        } else {
                            PageParameters parameters = new PageParameters(BuildTreePanel.this.getPage().getPageParameters());
                            parameters.set(BUILD_PARAM_NAME, build.getId());
                            parameters.set(ANCHOR_PARAM_NAME, true);
                            return new BookmarkablePageLink<>(id, tree.getPage().getClass(), parameters);
                        }
                    }
                }.setOutputMarkupId(true);

            }

        };
        tree.add(new HumanTheme());
        add(tree.setOutputMarkupId(true));
        add(new AjaxLink<Void>("expandAll") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                BuildExpansion.get().expandAll();
                target.add(tree);
            }
        });
        add(new AjaxLink<Void>("collapseAll") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                BuildExpansion.get().collapseAll();
                target.add(tree);
            }
        });
        add(new AjaxLink<Void>("selectAll") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                tree.visitChildren(CheckedFolder.class, new IVisitor<CheckedFolder<Build>, Void>() {

                    @Override
                    public void component(CheckedFolder<Build> component, IVisit<Void> visit) {
                        Build buildDocument = component.getModelObject();
                        if (buildDocument.getId() != null) {
                            getSelectedIds().add(buildDocument.getId());
                        } else {
                            getSelectedParents().add(buildDocument.getNameWithConfiguration());
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
                getSelectedIds().clear();
                getSelectedParents().clear();
                tree.visitChildren(CheckedFolder.class, new IVisitor<CheckedFolder<Build>, Void>() {

                    @Override
                    public void component(final CheckedFolder<Build> item, IVisit<Void> visit) {
                        target.add(item);
                        visit.dontGoDeeper();
                    }
                });
                target.add(selectedCount);
            }
        });
        add(new Link<Void>("compare") {

            @Override
            public void onClick() {
                getPage().send(getPage(), Broadcast.EXACT, new CompareBuildsEvent(getSelectedIds()));
            }
        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        String buildId = getPage().getPageParameters().get(BUILD_PARAM_NAME).toString(null);
        if (buildId != null) {
            Build build = buildRepository.findOne(new ObjectId(buildId));
            if (build != null) {
                setModelObject(build);
            }
        }
    }

    @Override
    public void onEvent(IEvent<?> event) {
        if (event.getPayload() instanceof CopyToAllSelectedEvent) {
            CopyToAllSelectedEvent copyEvent = (CopyToAllSelectedEvent) event.getPayload();
            for (ObjectId objectId : getSelectedIds()) {
                buildRepository.addOrUpdateProperties(buildRepository.findOne(objectId), copyEvent.getProperties());
            }
            if (getSelectedIds().size() > 0) {
                copyEvent.getFeedbackComponent().info("Successfully copied to " + getSelectedIds().size() + " documents.");
            } else {
                copyEvent.getFeedbackComponent().warn("0 documents selected.");
            }
        }
    }

    private Set<String> getSelectedParents() {
        return BuildSelection.get().getParents();
    }

    public Set<ObjectId> getSelectedIds() {
        return BuildSelection.get().getIds();
    }

    private class BuildsExpansionModel implements IModel<Set<Build>> {

        @Override
        public Set<Build> getObject() {
            return BuildExpansion.get();
        }

        @Override
        public void setObject(Set<Build> object) {

        }

        @Override
        public void detach() {

        }
    }

    @AllArgsConstructor
    private class CheckBoxModel extends Model<Boolean> {

        final IModel<Build> model;

        @Override
        public Boolean getObject() {
            String jobName = model.getObject().getNameWithConfiguration();
            ObjectId id = model.getObject().getId();
            if (id != null) {
                return getSelectedIds().contains(id);
            } else {
                return getSelectedParents().contains(jobName);
            }
        }

        @Override
        public void setObject(Boolean object) {
            String jobName = model.getObject().getNameWithConfiguration();
            ObjectId id = model.getObject().getId();
            if (id != null) {
                if (getSelectedIds().contains(id)) {
                    getSelectedIds().remove(id);
                } else {
                    getSelectedIds().add(id);
                }
            } else {
                if (getSelectedParents().contains(jobName)) {
                    getSelectedParents().remove(jobName);
                    if (tree.getProvider().hasChildren(model.getObject())) {
                        Iterator<? extends Build> it = tree.getProvider().getChildren(model.getObject());
                        while (it.hasNext()) {
                            Build b = it.next();
                            if (b.getId() != null) {
                                if (getSelectedIds().contains(b.getId())) {
                                    getSelectedIds().remove(b.getId());
                                }
                            } else {
                                if (getSelectedParents().contains(b.getNameWithConfiguration())) {
                                    getSelectedParents().remove(b.getNameWithConfiguration());
                                    if (tree.getProvider().hasChildren(b)) {
                                        Iterator<? extends Build> it2 = tree.getProvider().getChildren(b);
                                        while (it2.hasNext()) {
                                            Build c = it2.next();
                                            if (getSelectedIds().contains(c.getId())) {
                                                getSelectedIds().remove(c.getId());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    getSelectedParents().add(jobName);
                    if (tree.getProvider().hasChildren(model.getObject())) {
                        Iterator<? extends Build> it = tree.getProvider().getChildren(model.getObject());
                        while (it.hasNext()) {
                            Build b = it.next();
                            if (b.getId() != null) {
                                if (!getSelectedIds().contains(b.getId())) {
                                    getSelectedIds().add(b.getId());
                                }
                            } else {
                                if (!getSelectedParents().contains(b.getNameWithConfiguration())) {
                                    getSelectedParents().add(b.getNameWithConfiguration());
                                    if (tree.getProvider().hasChildren(b)) {
                                        Iterator<? extends Build> it2 = tree.getProvider().getChildren(b);
                                        while (it2.hasNext()) {
                                            Build c = it2.next();
                                            if (!getSelectedIds().contains(c.getId())) {
                                                getSelectedIds().add(c.getId());
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

    private static class BuildsProvider implements ITreeProvider<Build> {

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
        public Iterator<? extends Build> getRoots() {
            return buildRepository.getRoots(filter.getObject());
        }

        @Override
        public boolean hasChildren(Build node) {
            if (node == null) {
                // this is because Junction component relies on it in 'isEnabled' method
                return true;
            } else {
                return node.getId() == null;
            }
        }

        @Override
        public Iterator<? extends Build> getChildren(Build node) {
            return buildRepository.getChildren(node, filter.getObject());
        }

        @Override
        public IModel<Build> model(Build object) {
            return new Model<Build>(object);
        }
    }

    @Data
    @AllArgsConstructor
    public static class CopyToAllSelectedEvent implements Serializable {

        private Set<BuildProperty> properties;

        private Component feedbackComponent;
    }

}
