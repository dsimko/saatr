
package org.jboss.qa.tool.saatr.web.comp.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.User;
import org.jboss.qa.tool.saatr.web.comp.DocumentModel;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapTable;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class UsersPanel extends GenericPanel<User> {

    public UsersPanel(String id) {
        super(id, new DocumentModel<>(User.class, null));
        setOutputMarkupId(true);
        List<IColumn<User, String>> columns = new ArrayList<IColumn<User, String>>();
        columns.add(new PropertyColumn<User, String>(new Model<String>("name"), "username"));
        BootstrapTable<User, String> dataTable = new BootstrapTable<User, String>("table", columns, new UserProvider(), 100, getModel()) {

            @Override
            protected void onRowClicked(AjaxRequestTarget target, User user) {
                setModelObject(user);
                target.add(UsersPanel.this);
            }

        };
        add(dataTable);
        add(new UserPanel("user", getModel()));
    }
}