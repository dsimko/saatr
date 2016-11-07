
package org.jboss.qa.tool.saatr.repo;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.User;
import org.jboss.qa.tool.saatr.domain.User.Role;
import org.jboss.qa.tool.saatr.domain.build.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * The manual implementation parts for {@link UserRepository}. This will automatically be
 * picked up by the Spring Data infrastructure as we follow the naming convention of
 * extending the core repository interface's name with {@code Impl}.
 * 
 * @author dsimko@redhat.com
 */
@Component
class UserRepositoryImpl implements UserRepositoryCustom {

    private static final Group EMPTY_GROUP = new Group(new ObjectId(), "EMPTY");

    private static final Set<Group> EMPTY_GROUPS = new HashSet<>();

    static {
        EMPTY_GROUPS.add(EMPTY_GROUP);
    }

    private final MongoTemplate template;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserRepositoryImpl(MongoTemplate template, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.template = template;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public String getCurrentUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public User getCurrentUser() {
        return template.findOne(Query.query(where("username").is(getCurrentUserName())), User.class);
    }

    @Override
    public Set<Group> getCurrentUserGroups() {
        User user = getCurrentUser();
        if (user != null) {
            Set<Group> groups = getCurrentUser().getGroups();
            if (!groups.isEmpty()) {
                return groups;
            }
        }
        return EMPTY_GROUPS;
    }

    @Override
    public void createUser(String username, String password, Role... roles) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        for (Role role : roles) {
            user.getRoles().add(role);
        }
        template.save(user);
    }

}
