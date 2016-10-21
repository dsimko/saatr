
package org.jboss.qa.tool.saatr;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration test for {@link BuildRepository}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest({ "spring.data.mongodb.port=0" })
public class BuildRepositoryIntegrationTest {

    @Autowired
    BuildRepository repository;

    @Autowired
    MongoOperations operations;

    Build build;

    @Before
    public void setUp() {
        repository.deleteAll();
        build = repository.save(new Build());
    }

    /**
     * Test case to show that automatically generated ids are assigned to the domain
     * objects.
     */
    @Test
    public void setsIdOnSave() {

        Build dave = repository.save(new Build());
        assertThat(dave.getId(), is(notNullValue()));
    }
}
