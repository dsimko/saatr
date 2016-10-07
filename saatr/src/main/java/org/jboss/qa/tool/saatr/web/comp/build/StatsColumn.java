
package org.jboss.qa.tool.saatr.web.comp.build;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;

@SuppressWarnings("serial")
public class StatsColumn extends PropertyColumn<BuildDocument, String> {

    public StatsColumn() {
        super(Model.of("Err/F/All S/E/F/A"), null);
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
        if (build.getId() == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<span class=\"text-danger\">");
        builder.append(build.getErrorTestsuites());
        builder.append("</span><span class=\"text-muted\">/</span>");
        builder.append("<span class=\"text-danger\">");
        builder.append(build.getFailedTestsuites());
        builder.append("</span><span class=\"text-muted\">/</span>");
        builder.append(build.getTestsuites().size());
        builder.append(" ");
        builder.append("<span class=\"text-warning\">");
        builder.append(build.getSkippedTestcases());
        builder.append("</span><span class=\"text-muted\">/</span><span class=\"text-danger\">");
        builder.append(build.getErrorTestcases());
        builder.append("</span><span class=\"text-muted\">/</span><span class=\"text-danger\">");
        builder.append(build.getFailedTestcases());
        builder.append("</span><span class=\"text-muted\">/</span>");
        builder.append(build.getTestcases());
        return builder.toString();
    }

}
