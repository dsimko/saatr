
package org.jboss.qa.tool.saatr.security;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.HashSet;
import java.util.Set;

import org.jboss.qa.tool.saatr.domain.User;
import org.jboss.qa.tool.saatr.domain.User.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private MongoTemplate template;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = template.findOne(Query.query(where("username").is(username)), User.class);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found.");
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.name()));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }
}