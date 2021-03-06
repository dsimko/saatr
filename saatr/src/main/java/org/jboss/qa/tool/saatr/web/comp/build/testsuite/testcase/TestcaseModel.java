
package org.jboss.qa.tool.saatr.web.comp.build.testsuite.testcase;

import java.util.UUID;

import javax.inject.Inject;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.TestCase;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;

@SuppressWarnings("serial")
public class TestcaseModel implements IModel<TestCase> {

    @Inject
    private BuildRepository buildRepository;

    private UUID id;

    private Integer index;

    private transient TestCase entity;

    public TestcaseModel(TestCase entity) {
        Injector.get().inject(this);
        setObject(entity);
    }

    @Override
    public TestCase getObject() {
        if (entity == null && id != null && index != null) {
            entity = buildRepository.findTestcaseById(id, index);
        }
        return entity;
    }

    @Override
    public final void setObject(TestCase other) {
        if (other == null) {
            id = null;
            entity = null;
            index = null;
        } else {
            id = other.getId();
            index = other.getIndex();
            entity = other;
        }
    }

    @Override
    public void detach() {
        if (entity != null && entity.getId() != null) {
            entity = null;
        }
    }
}