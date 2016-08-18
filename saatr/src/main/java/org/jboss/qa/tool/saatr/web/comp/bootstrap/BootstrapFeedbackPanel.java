package org.jboss.qa.tool.saatr.web.comp.bootstrap;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * Wicket FeedbackPanel enriched with bootstrap css classes.
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class BootstrapFeedbackPanel extends FeedbackPanel {

    public static final IFeedbackMessageFilter SESSION_MESSAGES_FILTER = new SessionMessagesFilter();

    public BootstrapFeedbackPanel(String id) {
        super(id);
    }

    public BootstrapFeedbackPanel(final String id, IFeedbackMessageFilter filter) {
        super(id, filter);
    }

    @Override
    protected String getCSSClass(FeedbackMessage message) {
        String cssClass = "alert";
        switch (message.getLevel()) {
        case FeedbackMessage.UNDEFINED:
        case FeedbackMessage.DEBUG:
        case FeedbackMessage.INFO:
            return cssClass + " alert-info";
        case FeedbackMessage.SUCCESS:
            return cssClass + " alert-success";
        case FeedbackMessage.WARNING:
            return cssClass + " alert-warning";
        case FeedbackMessage.ERROR:
        case FeedbackMessage.FATAL:
            return cssClass + " alert-danger";
        default:
            return super.getCSSClass(message);
        }
    }

    private static final class SessionMessagesFilter implements IFeedbackMessageFilter {
        @Override
        public boolean accept(FeedbackMessage message) {
            // accept messages only from session
            return message.getReporter() == null;
        }
    }
}
