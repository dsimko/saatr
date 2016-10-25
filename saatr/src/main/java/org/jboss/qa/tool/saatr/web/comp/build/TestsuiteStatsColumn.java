
package org.jboss.qa.tool.saatr.web.comp.build;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.domain.build.Build.HtmlRenderer;

@SuppressWarnings("serial")
public class TestsuiteStatsColumn extends PropertyColumn<Build, String> {

    public TestsuiteStatsColumn() {
        super(Model.of("Testuites stats"), null);
    }

    @Override
    public void populateItem(final Item<ICellPopulator<Build>> item, final String componentId, final IModel<Build> rowModel) {
        item.add(new Label(componentId, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return HtmlRenderer.getTestsuiteStatisticsHtml(rowModel.getObject());
            }
        }).setEscapeModelStrings(false));
    }
 
}
