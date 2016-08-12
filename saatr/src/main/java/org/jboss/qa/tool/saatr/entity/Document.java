package org.jboss.qa.tool.saatr.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Main Domain class which also servers for unmarshalling configuration from xml
 * and serializing document to JSON for MongoDB.
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "document")
@XmlAccessorType(XmlAccessType.FIELD)
public class Document implements Serializable {

    @XmlElement
    private String name;
    @JsonIgnore
    @XmlElement
    private String jenkinsMinerClass;
    @XmlElement(name = "field")
    @XmlElementWrapper(name = "fields")
    private List<Field> fields = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJenkinsMinerClass() {
        return jenkinsMinerClass;
    }

    public void setJenkinsMinerClass(String jenkinsMinerClass) {
        this.jenkinsMinerClass = jenkinsMinerClass;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Field getField(String name) {
        for (Field field : fields) {
            if (name.equals(field.getName())) {
                return field;
            }
        }
        Field field = new Field(name);
        fields.add(field);
        return field;
    }
}
