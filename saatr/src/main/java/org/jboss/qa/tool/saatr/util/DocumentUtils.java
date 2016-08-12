package org.jboss.qa.tool.saatr.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.entity.Document;
import org.jboss.qa.tool.saatr.entity.Field;
import org.jboss.qa.tool.saatr.entity.TestsuiteData;
import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite;
import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite.Properties;
import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite.Properties.Property;
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
        // WicketApplication.get().getMongoCollection().insertOne(org.bson.Document.parse(mapper.writeValueAsString(document)));
        WicketApplication.get().getDatastore().save(document);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(document);
        LOG.info("Document successfully stored in MongoDB.");
        LOG.trace(json);
        return json;
    }

    public static List<Testsuite> unzipAndUnmarshalTestsuite(InputStream inputStream) throws Exception {
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        List<Testsuite> testsuites = new ArrayList<>();
        JAXBContext jaxbContext = JAXBContext.newInstance(Testsuite.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            LOG.debug("Unzipping: " + entry.getName());
            byte[] buffer = new byte[4096];
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                BufferedOutputStream bos = new BufferedOutputStream(outputStream, buffer.length);
                int size;
                while ((size = zipInputStream.read(buffer, 0, buffer.length)) != -1) {
                    bos.write(buffer, 0, size);
                }
                bos.flush();
                testsuites.add((Testsuite) jaxbUnmarshaller.unmarshal(new StringReader(new String(outputStream.toByteArray()))));
            }
        }
        return testsuites;
    }

    @SuppressWarnings("unchecked")
    private static <T> T createClassInstance(String className) {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Build jobRun) {
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jobRun);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    public static void fillBuildByTestsuites(List<Testsuite> input, Build build) {
        for (Testsuite testsuite : input) {
            TestsuiteData testsuiteData = TestsuiteData.create(testsuite);
            // only first testsuite properties are added to the build
            if (build.getTestsuites().isEmpty()) {
                fillBuildByProperties(testsuite.getProperties(), build);
            }
            build.getTestsuites().add(testsuiteData);
        }
    }

    private static void fillBuildByProperties(List<Properties> input, Build build) {
        for (Properties properties : input) {
            for (Property property : properties.getProperty()) {
                org.jboss.qa.tool.saatr.entity.Property prop = new org.jboss.qa.tool.saatr.entity.Property();
                prop.setName(property.getName());
                prop.setValue(property.getValue());
                build.getProps().add(prop);
            }
        }
    }

}
