
package org.jboss.qa.tool.saatr.web.comp.build;

import java.util.Iterator;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.CheckedFolder;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.HumanTheme;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
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
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.page.BuildPage;

import lombok.AllArgsConstructor;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class BuildsTreePanel extends AbstractBuildsPanel {

    private NestedTree<Build> tree;

    public BuildsTreePanel(String id, IModel<Build> model, IModel<BuildFilter> filterModel) {
        super(id, model, filterModel);
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
                        return BuildsTreePanel.this.getModelObject() != null && BuildsTreePanel.this.getModelObject().getId().equals(getModelObject().getId());
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
                            PageParameters parameters = BuildPage.createBuildDetailPageParameters(build.getId(),
                                    BuildsTreePanel.this.getPage().getPageParameters());
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
    }

    private Set<String> getSelectedParents() {
        return BuildSelection.get().getParents();
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

}
