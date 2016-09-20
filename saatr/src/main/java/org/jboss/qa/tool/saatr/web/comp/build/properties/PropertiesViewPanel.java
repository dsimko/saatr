package org.jboss.qa.tool.saatr.web.comp.build.properties;

import java.util.Iterator;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
class PropertiesViewPanel<T extends DocumentWithProperties<?>> extends GenericPanel<T> {

	public PropertiesViewPanel(String id, final IModel<T> model) {
		super(id, new CompoundPropertyModel<>(model));
		add(new RefreshingView<PropertyData>("properties") {
			@Override
			protected Iterator<IModel<PropertyData>> getItemModels() {
				return getModelObject().getProperties().stream().filter(p -> p.getValue() != null).sorted()
						.map(p -> (IModel<PropertyData>) new CompoundPropertyModel<>(p)).iterator();
			}

			@Override
			protected void populateItem(Item<PropertyData> item) {
				item.add(new Label("name"));
				item.add(new Label("value"));
			}
		});
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(!getModelObject().getProperties().isEmpty());
	}

}