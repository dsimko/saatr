
package org.jboss.qa.tool.saatr.web.comp.user;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jboss.qa.tool.saatr.domain.User;
import org.jboss.qa.tool.saatr.domain.User.Role;
import org.jboss.qa.tool.saatr.repo.UserRepository;
import org.jboss.qa.tool.saatr.repo.build.GroupRepository;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class UserFormPanel extends GenericPanel<User> {

    @SpringBean
    private GroupRepository groupRepository;

    @SpringBean
    private UserRepository userRepository;

    public UserFormPanel(String id) {
        super(id, new Model<>(new User()));
        Form<User> form = new Form<>("form", new CompoundPropertyModel<>(getModel()));
        form.add(new TextField<>("username").setRequired(true));
        form.add(new TextField<>("password").setRequired(true));
        form.add(new AjaxSubmitLink("submit") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                userRepository.createUser(getModelObject().getUsername(), getModelObject().getPassword(), Role.User);
                UserFormPanel.this.onSubmit(target);
            }
        });
        add(form);
    }

    protected void onSubmit(AjaxRequestTarget target) {

    }

}