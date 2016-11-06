
package org.jboss.qa.tool.saatr.web.comp.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.CloseButtonCallback;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
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
        final ModalWindow modalWindow = new ModalWindow("modal");
        modalWindow.setContent(new EmptyPanel(ModalWindow.CONTENT_ID));
        modalWindow.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
        modalWindow.setInitialHeight(450);
        modalWindow.setTitle("Add new user");
        modalWindow.setCloseButtonCallback(new CloseButtonCallback() {

            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget target) {
                target.add(UsersPanel.this);
                return true;
            }
        });
        Form<Void> form = new Form<Void>("form");
        form.add(modalWindow);
        form.add(new AjaxLink<Void>("add") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                modalWindow.show(target);
            }
        });
        add(form);
        add(new UserPanel("user", getModel()));
    }
}