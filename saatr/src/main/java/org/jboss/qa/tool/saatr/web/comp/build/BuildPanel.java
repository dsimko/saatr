
package org.jboss.qa.tool.saatr.web.comp.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.domain.build.Build.HtmlRenderer;
import org.jboss.qa.tool.saatr.domain.build.BuildProperty;
import org.jboss.qa.tool.saatr.domain.build.TestSuite;
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
public class BuildPanel extends GenericPanel<Build> {

    public BuildPanel(String id, final IModel<Build> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new Label("fullName"));
        add(new Label("buildNumber"));
        add(new Label("status", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return HtmlRenderer.getStatusHtml(getModelObject());
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
        add(new RefreshingView<BuildProperty>("systemProperties") {

            @Override
            protected Iterator<IModel<BuildProperty>> getItemModels() {
                return getModelObject().getSystemProperties().stream().filter(p -> !StringUtils.isEmpty(p.getValue())).sorted().map(
                        p -> (IModel<BuildProperty>) new CompoundPropertyModel<>(p)).iterator();
            }

            @Override
            protected void populateItem(Item<BuildProperty> item) {
                item.add(new Label("name"));
                item.add(new Label("value"));
            }
        });
        add(new RefreshingView<BuildProperty>("variables") {

            @Override
            protected Iterator<IModel<BuildProperty>> getItemModels() {
                return getModelObject().getBuildProperties().stream().filter(p -> !StringUtils.isEmpty(p.getValue())).sorted().map(
                        p -> (IModel<BuildProperty>) new CompoundPropertyModel<>(p)).iterator();
            }

            @Override
            protected void populateItem(Item<BuildProperty> item) {
                item.add(new Label("name"));
                item.add(new Label("value") {

                    @Override
                    public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
                        String text = getDefaultModelObjectAsString();
                        replaceComponentTagBody(markupStream, openTag, SmartLinkParser.INSTANCE.parse(text.replaceAll("&amp;", "&")));
                    }

                });
            }
        });

        add(new PropertiesPanel<>("properties", model));
        add(new RefreshingView<TestSuite>("testsuites") {

            @Override
            protected Iterator<IModel<TestSuite>> getItemModels() {
                List<IModel<TestSuite>> models = new ArrayList<>();
                List<TestSuite> testsuites = getModelObject().getTestsuites();
                Collections.sort(testsuites);
                for (TestSuite testsuiteData : testsuites) {
                    models.add(new TestsuiteModel(testsuiteData));
                }
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<TestSuite> item) {
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