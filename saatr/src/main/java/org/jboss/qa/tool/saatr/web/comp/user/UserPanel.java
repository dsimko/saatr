
package org.jboss.qa.tool.saatr.web.comp.user;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jboss.qa.tool.saatr.domain.User;
import org.jboss.qa.tool.saatr.domain.build.Group;
import org.jboss.qa.tool.saatr.repo.UserRepository;
import org.jboss.qa.tool.saatr.repo.build.GroupRepository;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class UserPanel extends GenericPanel<User> {

    @SpringBean
    private GroupRepository groupRepository;

    @SpringBean
    private UserRepository userRepository;

    public UserPanel(String id, final IModel<User> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new Label("username"));
        add(new Label("roles"));
        add(new ListView<Group>("groups", groupRepository.findAll()) {

            @Override
            protected void populateItem(ListItem<Group> item) {
                item.add(new Label("name", item.getModelObject().getName()));
                item.add(new AjaxCheckBox("checkbox", new Model<Boolean>() {

                    @Override
                    public Boolean getObject() {
                        if (model.getObject() == null) {
                            return false;
                        }
                        return model.getObject().getGroups().contains(item.getModelObject());
                    }

                    @Override
                    public void setObject(Boolean bool) {
                        if (bool) {
                            model.getObject().getGroups().add(item.getModelObject());
                        } else {
                            model.getObject().getGroups().remove(item.getModelObject());
                        }
                        userRepository.save(model.getObject());
                    }
                }) {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {

                    }
                });

            }

        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(getModelObject() != null);
    }

}