package org.jboss.qa.tool.saatr.util;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jboss.qa.tool.saatr.domain.Document;
import org.jboss.qa.tool.saatr.domain.Field;
import org.jboss.qa.tool.saatr.jenkins.IJenkinsMiner;
import org.jboss.qa.tool.saatr.web.WicketApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility methods for working with document.
 * 
 * @author dsimko@redhat.com
 *
 */
public class DocumentUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentUtils.class);

    /**
     * Unmarshal XML data from the specified file and return the resulting
     * document.
     * 
     * @param xml
     * @return
     */
    public static Document unmarshal(File xml) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Document document = (Document) jaxbUnmarshaller.unmarshal(xml);
            return document;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

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
    public static void populateFromJenkinsMiner(Document document, URL jenkinsBuild) {
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
    public static String persist(Document document) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        WicketApplication.get().getMongoCollection().insertOne(org.bson.Document.parse(mapper.writeValueAsString(document)));
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(document);
        LOG.info("Document successfully stored in MongoDB.");
        LOG.info(json);
        return json;
    }

    @SuppressWarnings("unchecked")
    private static <T> T createClassInstance(String className) {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
