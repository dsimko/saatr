
package org.jboss.qa.tool.saatr.web.comp.group;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jboss.qa.tool.saatr.domain.build.Group;
import org.jboss.qa.tool.saatr.repo.build.GroupRepository;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class GroupPanel extends GenericPanel<Group> {

    @SpringBean
    private GroupRepository groupRepository;

    public GroupPanel(String id, IModel<Group> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new Label("name"));
        add(new Link<Group>("delete", model) {

            @Override
            public void onClick() {
                groupRepository.delete(getModelObject());
                setModelObject(null);
            }
        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(getModelObject() != null);
    }

}