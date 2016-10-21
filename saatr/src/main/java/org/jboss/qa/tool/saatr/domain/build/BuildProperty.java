package org.jboss.qa.tool.saatr.domain.build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.qa.tool.saatr.jaxb.surefire.Testsuite.Properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class BuildProperty implements Serializable, Comparable<BuildProperty> {

    private String name;

    private String value;

    public static List<BuildProperty> create(List<Properties> properties) {
        List<BuildProperty> list = new ArrayList<>();
        for (Properties props : properties) {
            for (Properties.Property prop : props.getProperty()) {
                list.add(new BuildProperty(prop.getName(), prop.getValue()));
            }
        }
        return list;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BuildProperty other = (BuildProperty) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public int compareTo(BuildProperty o) {
        if (this.name == null || o == null) {
            return 0;
        }
        return this.name.compareToIgnoreCase(o.name);
    }
}
