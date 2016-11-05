
package org.jboss.qa.tool.saatr.web.comp.user;

import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.User;
import org.jboss.qa.tool.saatr.repo.UserRepository;

@SuppressWarnings("serial")
public class UserProvider extends SortableDataProvider<User, String> {

    @Inject
    private UserRepository userRepository;


    public UserProvider() {
        Injector.get().inject(this);
    }

    @Override
    public Iterator<User> iterator(long first, long count) {
        return userRepository.findAll().iterator();
    }

    @Override
    public long size() {
        return userRepository.count();
    }

    @Override
    public IModel<User> model(User user) {
        return new Model<>(user);
    }
 }
