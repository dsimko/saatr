package org.jboss.qa.tool.saatr.web.comp.build;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.entity.Build.TestsuiteData;
import org.jboss.qa.tool.saatr.web.comp.EntityModel;
import org.jboss.qa.tool.saatr.web.comp.build.properties.PropertiesPanel;
import org.jboss.qa.tool.saatr.web.comp.build.testsuite.TestsuitePanel;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class BuildPanel extends GenericPanel<Build> {

    public BuildPanel(String id, final IModel<Build> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new Label("jobName"));
        add(new Label("buildNumber"));
        add(new Label("duration"));
        add(new Label("status"));
        add(new Label("timestamp") {
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return new IConverter<C>() {

                    @Override
                    public C convertToObject(String value, Locale locale) throws ConversionException {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public String convertToString(C value, Locale locale) {
                        if (value instanceof Long) {
                            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date((Long) value * 1000L));
                        }
                        return String.valueOf(value);
                    }
                };
            }
        });
        add(new PropertiesPanel<>("properties", model));
        add(new RefreshingView<TestsuiteData>("testsuites") {
            @Override
            protected Iterator<IModel<TestsuiteData>> getItemModels() {
                List<IModel<TestsuiteData>> models = new ArrayList<>();
                for (TestsuiteData testsuiteData : getModelObject().getTestsuites()) {
                    models.add(new EntityModel<>(testsuiteData));
                }
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<TestsuiteData> item) {
                item.add(new TestsuitePanel("testsuite", item.getModel()));
            }
        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(getModelObject() != null);
    }

}