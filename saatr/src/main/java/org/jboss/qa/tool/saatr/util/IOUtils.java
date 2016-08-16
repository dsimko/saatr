package org.jboss.qa.tool.saatr.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.jboss.qa.tool.saatr.entity.jaxb.surefire.Testsuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
public class IOUtils {

    private static final Logger LOG = LoggerFactory.getLogger(IOUtils.class);

    /**
     * Loads properties from a classpath resource
     *
     * @param resource
     * @return loaded properties
     */
    public static Properties loadFromClassPath(String resource) {
        URL url = IOUtils.class.getClassLoader().getResource(resource);
        if (url == null) {
            throw new IllegalStateException("Could not find classpath properties resource: " + resource);
        }
        try {
            Properties props = new Properties();
            InputStream is = url.openStream();
            try {
                props.load(url.openStream());
            } finally {
                is.close();
            }
            return props;
        } catch (IOException e) {
            throw new RuntimeException("Could not read properties at classpath resource: " + resource, e);
        }
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

    public static String toJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
