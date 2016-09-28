
package org.jboss.qa.tool.saatr.web.comp.build;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebApplication;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.Status;

@SuppressWarnings("serial")
public class StatusColumn extends PropertyColumn<BuildDocument, String> {

    public StatusColumn() {
        super(Model.of("Status"), null);
    }

    @Override
    public void populateItem(final Item<ICellPopulator<BuildDocument>> item, final String componentId, final IModel<BuildDocument> rowModel) {
        item.add(new Label(componentId, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return getStatusHtml(rowModel.getObject());
            }
        }).setEscapeModelStrings(false));
    }

    public static String getStatusHtml(BuildDocument build) {
        StringBuilder builder = new StringBuilder();
        builder.append("<img src=\"");
        builder.append(WebApplication.get().getServletContext().getContextPath());
        builder.append("/images/");
        if (build == null || (build.getStatus() == null && build.getJobStatus() == null)) {
            builder.append("aborted16.png\" />");
            return builder.toString();
        } else {
            Status status = build.getStatus();
            if (status == null) {
                status = build.getJobStatus() == 0 ? Status.Success : Status.Failed;
            }
            if (status == Status.Failed) {
                builder.append("yellow16.png");
            } else {
                builder.append("blue16.png");
            }
            builder.append("\" /> ");
            builder.append(status);
            return builder.toString();
        }
    }

}
