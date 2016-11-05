
package org.jboss.qa.tool.saatr.web.comp.group;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jboss.qa.tool.saatr.domain.build.Group;
import org.jboss.qa.tool.saatr.repo.build.GroupRepository;
import org.jboss.qa.tool.saatr.web.comp.DocumentModel;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapFeedbackPanel;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapTable;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class GroupsPanel extends GenericPanel<Group> {

    @SpringBean
    private GroupRepository groupRepository;

    public GroupsPanel(String id) {
        super(id, new DocumentModel<>(Group.class, null));
        setOutputMarkupId(true);
        List<IColumn<Group, String>> columns = new ArrayList<IColumn<Group, String>>();
        columns.add(new PropertyColumn<Group, String>(new Model<String>("name"), "name"));
        BootstrapTable<Group, String> dataTable = new BootstrapTable<Group, String>("table", columns, new GroupProvider(), 100, getModel()) {

            @Override
            protected void onRowClicked(AjaxRequestTarget target, Group group) {
                setModelObject(group);
                target.add(GroupsPanel.this);
            }

        };
        add(dataTable);
        add(new GroupPanel("group", getModel()));
        Form<Group> form = new Form<>("form", new CompoundPropertyModel<>(new Group()));
        form.add(new BootstrapFeedbackPanel("feedback"));
        form.add(new TextField<>("name").setRequired(true));
        form.add(new AjaxSubmitLink("submit") {

            @Override
            @SuppressWarnings({ "rawtypes", "unchecked" })
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                groupRepository.save((Group) form.getModelObject());
                form.setModelObject(new Group());
                target.add(GroupsPanel.this);
            }
        });
        add(form);
    }
}