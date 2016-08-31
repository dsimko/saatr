package org.jboss.qa.tool.saatr.web.comp.build;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.entity.TestsuiteData;
import org.jboss.qa.tool.saatr.entity.Build.PropertyData;
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
                            StringBuilder builder = new StringBuilder();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                            builder.append(value);
                            builder.append(" (");
                            builder.append(dateFormat.format(new Date((Long) value * 1000L)));
                            builder.append(" UTC)");
                            return builder.toString();
                        }
                        return String.valueOf(value);
                    }
                };
            }
        });
        add(new RefreshingView<PropertyData>("systemProperties") {
            @Override
            protected Iterator<IModel<PropertyData>> getItemModels() {
                return getModelObject().getSystemProperties().stream().sorted()
                        .map(p -> (IModel<PropertyData>) new CompoundPropertyModel<>(p)).iterator();
            }

            @Override
            protected void populateItem(Item<PropertyData> item) {
                item.add(new Label("name"));
                item.add(new Label("value"));
            }
        });
        add(new RefreshingView<PropertyData>("variables") {
            @Override
            protected Iterator<IModel<PropertyData>> getItemModels() {
                return getModelObject().getVariables().stream().sorted().map(p -> (IModel<PropertyData>) new CompoundPropertyModel<>(p))
                        .iterator();
            }

            @Override
            protected void populateItem(Item<PropertyData> item) {
                item.add(new Label("name"));
                item.add(new Label("value"));
            }
        });

        add(new PropertiesPanel<>("properties", model));
        add(new RefreshingView<TestsuiteData>("testsuites") {
            @Override
            protected Iterator<IModel<TestsuiteData>> getItemModels() {
                List<IModel<TestsuiteData>> models = new ArrayList<>();
                List<TestsuiteData> testsuites = getModelObject().getTestsuites();
                Collections.sort(testsuites);
                for (TestsuiteData testsuiteData : testsuites) {
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