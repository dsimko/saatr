
package org.jboss.qa.tool.saatr.web.comp.group;

import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.build.Group;
import org.jboss.qa.tool.saatr.repo.build.GroupRepository;

@SuppressWarnings("serial")
public class GroupProvider extends SortableDataProvider<Group, String> {

    @Inject
    private GroupRepository groupRepository;


    public GroupProvider() {
        Injector.get().inject(this);
    }

    @Override
    public Iterator<Group> iterator(long first, long count) {
        return groupRepository.findAll().iterator();
    }

    @Override
    public long size() {
        return groupRepository.count();
    }

    @Override
    public IModel<Group> model(Group group) {
        return new Model<>(group);
    }
 }
