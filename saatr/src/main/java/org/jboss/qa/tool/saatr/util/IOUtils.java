package org.jboss.qa.tool.saatr.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jboss.qa.tool.saatr.jaxb.surefire.Testsuite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@Slf4j
public class IOUtils {

    public static List<Testsuite> unzipAndUnmarshalTestsuite(InputStream inputStream) throws Exception {
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        List<Testsuite> testsuites = new ArrayList<>();
        JAXBContext jaxbContext = JAXBContext.newInstance(Testsuite.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            log.debug("Unzipping: " + entry.getName());
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

    /**
     * Unmarshal XML data from the specified file and return the resulting
     * object.
     * 
     * @param xml
     * @return
     * @throws JAXBException
     */
    @SuppressWarnings("unchecked")
	public static <T> T unmarshal(InputStream inputStream, Class<T> entityClass) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(entityClass);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        T document = (T) jaxbUnmarshaller.unmarshal(inputStream);
        return document;
    }

    public static <T> String marshal(T t, Class<T> entityClass) {
        StringWriter sw = new StringWriter();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(entityClass);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(t, sw);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

}
