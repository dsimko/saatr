
package org.jboss.qa.tool.saatr.web.comp.bootstrap;

import java.util.List;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxNavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.CSVDataExporter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.ExportToolbar;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.paging.ajax.BootstrapAjaxPagingNavigator;

@SuppressWarnings("serial")
public abstract class BootstrapTable<T, S> extends DataTable<T, S> {

    private final IModel<T> pageModel;

    private final ISortableDataProvider<T, S> dataProvider;

    public BootstrapTable(final String id, final List<? extends IColumn<T, S>> columns, final ISortableDataProvider<T, S> dataProvider, final int rowsPerPage,
            IModel<T> pageModel) {
        super(id, columns, dataProvider, rowsPerPage);
        setOutputMarkupId(true);
        this.pageModel = pageModel;
        this.dataProvider = dataProvider;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        initToolbars();
    }

    @Override
    protected Item<T> newRowItem(String id, int index, final IModel<T> model) {
        Item<T> row = new OddEvenItem<T>(id, index, model);
        if (model.getObject().equals(pageModel.getObject())) {
            row.add(new AttributeAppender("class", Model.of("active"), " "));
        }
        row.add(new AttributeAppender("class", Model.of("clicableTableRow"), " "));
        row.add(new AjaxEventBehavior("click") {

            @Override
            protected void onEvent(AjaxRequestTarget target) {
                onRowClicked(target, model.getObject());
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                // because of links on actions panel
                attributes.setPreventDefault(true);
            }
        });

        return row;
    }

    protected void initToolbars() {
        addTopToolbar(new AjaxNavigationToolbar(this) {

            @Override
            protected PagingNavigator newPagingNavigator(String navigatorId, final DataTable<?, ?> table) {
                return new BootstrapAjaxPagingNavigator(navigatorId, table);
            }
        });
        addTopToolbar(new AjaxFallbackHeadersToolbar<S>(this, dataProvider));
        addBottomToolbar(new NoRecordsToolbar(this));
        addBottomToolbar(new ExportToolbar(this).addDataExporter(new CSVDataExporter()));
    }

    protected abstract void onRowClicked(AjaxRequestTarget target, T t);

    @Override
    protected void onDetach() {
        super.onDetach();
        pageModel.detach();
    }
}
