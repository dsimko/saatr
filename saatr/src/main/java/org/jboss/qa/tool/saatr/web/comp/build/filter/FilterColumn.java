
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter.PropertyDto;

@SuppressWarnings("serial")
public class FilterColumn extends PropertyColumn<BuildFilter, String> {

    public FilterColumn() {
        super(Model.of(""), null);
    }

    @Override
    public IModel<?> getDataModel(IModel<BuildFilter> rowModel) {
        return Model.of(geData(rowModel.getObject()));
    }

    // TODO escapeMarkup
    public static String geData(BuildFilter filter) {
        StringBuilder builder = new StringBuilder();
        if (filter.getJobName() != null) {
            append(filter.getJobName(), builder);
        }
        if (filter.getBuildNumber() != null) {
            append(filter.getBuildNumber(), builder);
        }
        if (filter.getStatus() != null) {
            append(filter.getStatus(), builder);
        }
        if (filter.getCreatedFrom() != null) {
            append(filter.getCreatedFrom(), builder);
        }
        if (filter.getCreatedTo() != null) {
            append(filter.getCreatedTo(), builder);
        }
        if (!filter.getBuildProperties().isEmpty()) {
            appendProperties(filter.getBuildProperties(), builder);
        }
        if (!filter.getSystemProperties().isEmpty()) {
            appendProperties(filter.getSystemProperties(), builder);
        }
        if (!filter.getProperties().isEmpty()) {
            appendProperties(filter.getProperties(), builder);
        }
        if (filter.getErrorMessage() != null) {
            append(filter.getErrorMessage(), builder);
        }
        if (filter.getFailureMessage() != null) {
            append(filter.getFailureMessage(), builder);
        }
        if (filter.getTestsuiteName() != null) {
            append(filter.getTestsuiteName(), builder);
        }
        if (!filter.getSelected().isEmpty()) {
            append(filter.getSelected(), builder);
        }
        return builder.toString();
    }

    private static <T> void append(T t, StringBuilder builder) {
        if (builder.length() > 0) {
            builder.append(" ");
        }
        builder.append(t);
    }

    private static void appendProperties(List<PropertyDto> list, StringBuilder builder) {
        for (PropertyDto property : list) {
            if (property.getName() != null) {
                StringBuilder b = new StringBuilder();
                b.append(property.getName());
                b.append(property.getOperation().getLabel());
                b.append(property.getValue());
                append(b, builder);
            }
        }
    }
}
