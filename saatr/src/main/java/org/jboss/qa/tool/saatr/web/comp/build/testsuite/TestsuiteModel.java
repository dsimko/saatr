
package org.jboss.qa.tool.saatr.web.comp.build.testsuite;

import java.util.UUID;

import javax.inject.Inject;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.TestsuiteDocument;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;

@SuppressWarnings("serial")
public class TestsuiteModel implements IModel<TestsuiteDocument> {

    @Inject
    private BuildRepository buildRepository;

    private UUID id;

    private transient TestsuiteDocument entity;

    public TestsuiteModel(TestsuiteDocument entity) {
        Injector.get().inject(this);
        setObject(entity);
    }

    @Override
    public TestsuiteDocument getObject() {
        if (entity == null && id != null) {
            entity = buildRepository.findTestsuiteById(id);
        }
        return entity;
    }

    @Override
    public final void setObject(TestsuiteDocument other) {
        if (other == null) {
            id = null;
            entity = null;
        } else {
            id = other.getId();
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