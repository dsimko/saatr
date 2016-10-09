
package org.jboss.qa.tool.saatr.web.comp.build;

import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;

@SuppressWarnings("serial")
public class VariableColumn extends PropertyColumn<BuildDocument, String> {

    private final String variableName;

    public VariableColumn(String label, String variableName) {
        super(Model.of(label), null);
        this.variableName = variableName;
    }

    @Override
    public IModel<?> getDataModel(IModel<BuildDocument> rowModel) {
        BuildDocument build = rowModel.getObject();
        for (PropertyData property : build.getVariables()) {
            if (variableName.equals(property.getName())) {
                return Model.of(property.getValue());
            }
        }
        return new Model<String>();
    }

}
