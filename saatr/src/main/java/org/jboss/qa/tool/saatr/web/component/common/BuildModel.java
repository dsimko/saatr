package org.jboss.qa.tool.saatr.web.component.common;

import javax.inject.Inject;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.service.BuildService;

@SuppressWarnings("serial")
public class BuildModel implements IModel<Build> {

    @Inject
    private BuildService buildService;

    private ObjectId id;
    private transient Build build;

    public BuildModel() {
        this(null);
    }

    public BuildModel(Build build) {
        Injector.get().inject(this);
        setObject(build);
    }

    @Override
    public Build getObject() {
        if (build == null && id != null) {
            build = buildService.findById(id);
        }
        return build;
    }

    @Override
    public final void setObject(Build other) {
        if (other == null) {
            id = null;
            build = null;
        } else {
            id = other.getId();
            build = other;
        }
    }

    @Override
    public void detach() {
        if (build != null && build.getId() != null) {
            build = null;
        }
    }
}