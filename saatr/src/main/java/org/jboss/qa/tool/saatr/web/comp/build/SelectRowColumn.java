
package org.jboss.qa.tool.saatr.web.comp.build;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.build.Build;

@SuppressWarnings("serial")
public class SelectRowColumn extends AbstractColumn<Build, String> {

    public SelectRowColumn() {
        super(Model.of(""), "id");
    }

    @Override
    public void populateItem(Item<ICellPopulator<Build>> cellItem, String componentId, IModel<Build> rowModel) {
        cellItem.add(new SelectRowPanel(componentId, rowModel));
    }

}
