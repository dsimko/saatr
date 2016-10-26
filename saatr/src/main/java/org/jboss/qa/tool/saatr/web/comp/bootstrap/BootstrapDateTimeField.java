
package org.jboss.qa.tool.saatr.web.comp.bootstrap;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

@SuppressWarnings("serial")
public class BootstrapDateTimeField extends DateTimeField {

    public BootstrapDateTimeField(String id) {
        super(id);
        visitChildren(TextField.class, new IVisitor<TextField<?>, Void>() {

            @Override
            public void component(TextField<?> object, IVisit<Void> visit) {
                object.add(new AttributeAppender("class", "form-control input-sm", " "));
            }
        });
    }

    @Override
    protected boolean use12HourFormat() {
        return false;
    }

}
