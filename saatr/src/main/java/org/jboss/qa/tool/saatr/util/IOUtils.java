package org.jboss.qa.tool.saatr.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jboss.qa.tool.saatr.entity.Document;
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
