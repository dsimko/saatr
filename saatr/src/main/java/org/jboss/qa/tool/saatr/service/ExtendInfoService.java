package org.jboss.qa.tool.saatr.service;

import java.util.List;
import java.util.stream.Stream;

import org.jboss.qa.tool.saatr.entity.Build.PropertyData;
import org.jboss.qa.tool.saatr.entity.Extensible;
import org.jboss.qa.tool.saatr.entity.jaxb.config.Config;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dsimko@redhat.com
 */
@Component
public class ExtendInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(ExtendInfoService.class);

    @Autowired
    private Datastore datastore;

    @SuppressWarnings("unchecked")
    public <T extends Extensible> void extend(T extensible, List<Config.Property> newProperties) {

        LOG.info("Extending {} by {}", extensible, newProperties);

        // clear properties
        datastore.update(extensible, datastore.createUpdateOperations((Class<T>) extensible.getClass()).unset("properties"));

        Stream<PropertyData> oldWithoutNewProperties = extensible.getProperties().stream()
                .filter(p -> !newProperties.contains(new Config.Property(p.getName(), null, null)));

        // merge old and new properties set
        Stream<PropertyData> allProperties = Stream.concat(oldWithoutNewProperties,
                newProperties.stream().map(p -> new PropertyData(p.getName(), p.getValue())));

        // update all properties
        allProperties.forEach(property -> {
            datastore.update(extensible, datastore.createUpdateOperations((Class<T>) extensible.getClass()).add("properties", property));
        });
    }

    public void prefillValues(Config config, Extensible extensible) {
        config.getProperties().forEach(cp -> {
            extensible.getProperties().stream().filter(p -> p.getName().equals(cp.getName())).findFirst()
                    .ifPresent(found -> cp.setValue(found.getValue()));
        });
    }

}
