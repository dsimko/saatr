
package org.jboss.qa.tool.saatr.web.comp.build.testsuite.testcase;

import org.apache.wicket.markup.html.panel.GenericPanel;
import org.jboss.qa.tool.saatr.domain.build.TestcaseDocument.FailureData;
import org.jboss.qa.tool.saatr.web.comp.HideableLabel;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
class ErrorPanel extends GenericPanel<FailureData> {

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(getModelObject() != null);
    }

    public ErrorPanel(String id) {
        super(id);
        add(new HideableLabel("error.value"));
        add(new HideableLabel("error.message"));
        add(new HideableLabel("error.type"));
    }
}