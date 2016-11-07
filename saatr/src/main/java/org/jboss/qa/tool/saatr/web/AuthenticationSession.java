
package org.jboss.qa.tool.saatr.web;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jboss.qa.tool.saatr.domain.User;
import org.jboss.qa.tool.saatr.repo.UserRepository;

@SuppressWarnings("serial")
public class AuthenticationSession extends AbstractAuthenticatedWebSession {

    @SpringBean
    private UserRepository userRepository;

    public AuthenticationSession(Request request) {
        super(request);
        Injector.get().inject(this);
    }

    /**
     * @return true, if user is signed in
     */
    @Override
    public final boolean isSignedIn() {
        return userRepository.getCurrentUser() != null;
    }

    @Override
    public Roles getRoles() {
        Roles roles = new Roles();
        if (isSignedIn()) {
            for (User.Role role : userRepository.getCurrentUser().getRoles()) {
                roles.add(role.name());
            }
        }
        return roles;
    }

    public static AuthenticationSession get() {
        return (AuthenticationSession) Session.get();
    }
}
