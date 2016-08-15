package org.jboss.qa.tool.saatr.service;

import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.qa.tool.saatr.entity.Document;
import org.jboss.qa.tool.saatr.entity.Field;
import org.jboss.qa.tool.saatr.jenkins.IJenkinsMiner;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility methods for working with document.
 * 
 * @author dsimko@redhat.com
 *
 */
@Component
public class DocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    private Datastore datastore;

    /**
     * Populate given document by values from Jenkins Miner
     * {@link IJenkinsMiner}. Miner class name is specified in the document and
     * must be on classpath.
     * 
     * @param document
     *            will be filled
     * @param jenkinsBuild
     *            input parameter for converter
     */
    public void populateFromJenkinsMiner(Document document, URL jenkinsBuild) {
        IJenkinsMiner miner = createClassInstance(document.getJenkinsMinerClass());
        Map<String, String> properties = miner.mine(jenkinsBuild);
        for (Entry<String, String> entry : properties.entrySet()) {
            Field field = document.getField(entry.getKey());
            field.setValue(entry.getValue());
        }
    }

    /**
     * Make an instance of document persistent.
     * 
     * @param document
     * @return
     * @throws JsonProcessingException
     */
    public String persist(Document document) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        // WicketApplication.get().getMongoCollection().insertOne(org.bson.Document.parse(mapper.writeValueAsString(document)));
        datastore.save(document);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(document);
        LOG.info("Document successfully stored in MongoDB.");
        LOG.trace(json);
        return json;
    }

    @SuppressWarnings("unchecked")
    private <T> T createClassInstance(String className) {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
