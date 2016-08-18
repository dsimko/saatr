package org.jboss.qa.tool.saatr.web.comp;

import javax.inject.Inject;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.entity.PersistableWithProperties;
import org.jboss.qa.tool.saatr.service.BuildService;

@SuppressWarnings("serial")
public class EntityModel<T extends PersistableWithProperties> implements IModel<T> {

    @Inject
    private BuildService service;

    private ObjectId id;
    private Class<T> type;

    private transient T entity;

    public EntityModel(T entity) {
        Injector.get().inject(this);
        setObject(entity);
    }

    public EntityModel(Class<T> clazz, ObjectId id) {
        Injector.get().inject(this);
        this.type = clazz;
        this.id = id;
    }

    @Override
    public T getObject() {
        if (entity == null && id != null) {
            entity = service.findById(id, type);
        }
        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void setObject(T other) {
        if (other == null) {
            id = null;
            entity = null;
        } else {
            type = (Class<T>) other.getClass();
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