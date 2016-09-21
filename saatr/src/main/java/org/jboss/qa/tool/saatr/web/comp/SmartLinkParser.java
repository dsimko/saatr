
package org.jboss.qa.tool.saatr.web.comp;

import org.apache.wicket.extensions.markup.html.basic.ILinkRenderStrategy;
import org.apache.wicket.extensions.markup.html.basic.LinkParser;

public class SmartLinkParser extends LinkParser {

    private static final String URL_PATTERN = "([a-zA-Z]+://[\\w\\.\\-\\:\\/~]+)[\\w\\.,:\\-/?&=%]*";

    public static final ILinkRenderStrategy URL_RENDER_STRATEGY = new ILinkRenderStrategy() {

        @Override
        public String buildLink(final String linkTarget) {
            return "<a target=\"_blank\" href=\"" + linkTarget + "\">" + linkTarget + "</a>";
        }
    };

    public SmartLinkParser() {
        addLinkRenderStrategy(URL_PATTERN, URL_RENDER_STRATEGY);
    }

}
