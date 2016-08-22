package org.jboss.qa.tool.saatr.service;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jboss.qa.tool.saatr.entity.ConfigData;
import org.jboss.qa.tool.saatr.entity.PersistableWithProperties;
import org.jboss.qa.tool.saatr.entity.jaxb.config.Config;
import org.jboss.qa.tool.saatr.web.comp.config.ConfigProvider.ConfigFilter;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dsimko@redhat.com
 */
@Component
public class ConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigService.class);

    @Autowired
    private Datastore datastore;

    /**
     * Unmarshal XML data from the specified file and return the resulting
     * object.
     * 
     * @param xml
     * @return
     * @throws JAXBException
     */
    public Config unmarshal(InputStream inputStream) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Config document = (Config) jaxbUnmarshaller.unmarshal(inputStream);
        return document;
    }

    public String marshal(Config config) {
        StringWriter sw = new StringWriter();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(config, sw);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    public void prefillValues(ConfigData config, PersistableWithProperties persistable) {
        config.getProperties().forEach(cp -> {
            persistable.getProperties().stream().filter(p -> p.getName().equals(cp.getName())).findFirst()
                    .ifPresent(found -> cp.setValue(found.getValue()));
        });
    }

    public void save(ConfigData configData) {
        datastore.save(configData);
        LOG.info("Config successfully stored in MongoDB.");
    }

    public List<ConfigData> findAll() {
        return datastore.find(ConfigData.class).asList();
    }

    public void deleteAll() {
        datastore.delete(datastore.createQuery(ConfigData.class));
    }

    public Iterator<ConfigData> query(long first, long count, ConfigFilter filter) {
        final Query<ConfigData> query = createQueryAndApplyFilter(filter);
        query.limit((int) count);
        query.offset((int) first);
        query.order("-" + Mapper.ID_KEY);
        return query.iterator();
    }

    public long count(ConfigFilter filter) {
        return datastore.getCount(createQueryAndApplyFilter(filter));
    }

    private Query<ConfigData> createQueryAndApplyFilter(ConfigFilter filter) {
        final Query<ConfigData> query = datastore.createQuery(ConfigData.class);
        if (filter.getName() != null) {
            query.and(query.criteria("name").equal(filter.getName()));
        }
        return query;
    }

}
