package org.jboss.qa.tool.saatr.service;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jboss.qa.tool.saatr.entity.jaxb.config.Config;
import org.springframework.stereotype.Component;

/**
 * @author dsimko@redhat.com
 */
@Component
public class ConfigService {

    /**
     * Unmarshal XML data from the specified file and return the resulting
     * object.
     * 
     * @param xml
     * @return
     */
    public Config unmarshal(File xml) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Config document = (Config) jaxbUnmarshaller.unmarshal(xml);
            return document;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

}