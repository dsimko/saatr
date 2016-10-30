
package org.jboss.qa.tool.saatr.util;

import java.util.Comparator;

import org.jboss.qa.tool.saatr.domain.DocumentWithName;

public class NameComparator<T extends DocumentWithName> implements Comparator<T> {

    @Override
    public int compare(DocumentWithName o1, DocumentWithName o2) {
        if (o1 == null || o2 == null || o1.getName() == null) {
            return 0;
        } else {
            return o1.getName().compareTo(o2.getName());
        }
    }

}
