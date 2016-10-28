
package org.jboss.qa.tool.saatr.web.comp.build;

import java.util.Set;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithID;

@SuppressWarnings("serial")
public class SelectRowColumn<T extends DocumentWithID<ObjectId>> extends AbstractColumn<T, String> {

    private final Set<ObjectId> selected;
    
    public SelectRowColumn(Set<ObjectId> selected) {
        super(Model.of(""), "id");
        this.selected = selected;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        cellItem.add(new SelectRowPanel<T>(componentId, rowModel, selected));
    }

}
