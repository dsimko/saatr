
package org.jboss.qa.tool.saatr.web.comp.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.basic.ILinkParser;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;
import org.jboss.qa.tool.saatr.domain.build.TestsuiteDocument;
import org.jboss.qa.tool.saatr.web.comp.SmartLinkParser;
import org.jboss.qa.tool.saatr.web.comp.build.properties.PropertiesPanel;
import org.jboss.qa.tool.saatr.web.comp.build.testsuite.TestsuiteModel;
import org.jboss.qa.tool.saatr.web.comp.build.testsuite.TestsuitePanel;
import org.springframework.util.StringUtils;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class BuildPanel extends GenericPanel<BuildDocument> {

    public BuildPanel(String id, final IModel<BuildDocument> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new Label("jobName"));
        add(new Label("buildNumber"));
        add(new Label("status", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return StatusColumn.getStatusHtml(getModelObject());
            }
        }).setEscapeModelStrings(false));
        add(DateLabel.forDatePattern("created", "yyyy-MM-dd' 'HH:mm:ss' 'Z"));
        add(new ExternalLink("consoleTextLink", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if (getModelObject() != null && getModelObject().getConsoleTextId() != null) {
                    return ConsoleTextResource.PATH + getModelObject().getConsoleTextId().toHexString();
                }
                return null;
            }
        }) {

            @Override
            public boolean isVisible() {
                return getModelObject() != null && getModelObject().getConsoleTextId() != null;
            }
        });
        add(new RefreshingView<PropertyData>("systemProperties") {

            @Override
            protected Iterator<IModel<PropertyData>> getItemModels() {
                return getModelObject().getSystemProperties().stream().filter(p -> !StringUtils.isEmpty(p.getValue())).sorted().map(
                        p -> (IModel<PropertyData>) new CompoundPropertyModel<>(p)).iterator();
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
                return getModelObject().getVariables().stream().filter(p -> !StringUtils.isEmpty(p.getValue())).sorted().map(
                        p -> (IModel<PropertyData>) new CompoundPropertyModel<>(p)).iterator();
            }

            @Override
            protected void populateItem(Item<PropertyData> item) {
                item.add(new Label("name"));
                item.add(new SmartLinkLabel("value") {

                    @Override
                    protected ILinkParser getLinkParser() {
                        return new SmartLinkParser();
                    }
                });
            }
        });

        add(new PropertiesPanel<>("properties", model));
        add(new RefreshingView<TestsuiteDocument>("testsuites") {

            @Override
            protected Iterator<IModel<TestsuiteDocument>> getItemModels() {
                List<IModel<TestsuiteDocument>> models = new ArrayList<>();
                List<TestsuiteDocument> testsuites = getModelObject().getTestsuites();
                Collections.sort(testsuites);
                for (TestsuiteDocument testsuiteData : testsuites) {
                    models.add(new TestsuiteModel(testsuiteData));
                }
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<TestsuiteDocument> item) {
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