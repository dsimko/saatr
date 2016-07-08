package org.jboss.qa.tool.saatr.web.page;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

/**
 * Displays resulting document.
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class InfoPage extends BasePage<String> {

    public InfoPage() {
        redirectToHomePage();
    }

    public InfoPage(final String json) {
        add(new Label("json", json).setEscapeModelStrings(false));
        add(new Link<Void>("goBack") {
            @Override
            public void onClick() {
                redirectToHomePage();
            }
        });

    }

}