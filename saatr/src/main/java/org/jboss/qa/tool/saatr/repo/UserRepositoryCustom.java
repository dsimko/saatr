
package org.jboss.qa.tool.saatr.repo;

import java.util.Set;

import org.jboss.qa.tool.saatr.domain.User;
import org.jboss.qa.tool.saatr.domain.User.Role;
import org.jboss.qa.tool.saatr.domain.build.Group;

/**
 * The interface for repository functionality that will be implemented manually.
 * 
 * @author dsimko@redhat.com
 */
interface UserRepositoryCustom {

    String getCurrentUserName();

    User getCurrentUser();

    Set<Group> getCurrentUserGroups();

    void createUser(String username, String password, Role... role);

}
