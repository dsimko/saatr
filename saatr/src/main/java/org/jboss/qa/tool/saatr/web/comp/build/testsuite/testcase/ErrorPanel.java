
package org.jboss.qa.tool.saatr.web.comp.build.testsuite.testcase;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jboss.qa.tool.saatr.domain.build.TestCase.Fragment;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.comp.HideableLabel;
import org.jboss.qa.tool.saatr.web.comp.build.filter.BuildsFilterPanel;
import org.jboss.qa.tool.saatr.web.page.BuildPage;

/**
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
class ErrorPanel extends GenericPanel<Fragment> {

    @SpringBean
    private BuildRepository buildRepository; 
    
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
        add(new Link<Fragment>("error") {
           @Override
            public void onClick() {
               PageParameters parameters = new PageParameters();
               parameters.set(BuildsFilterPanel.FILTER_PARAM_NAME, buildRepository.findSimilar(getModelObject().getMessage()));
               setResponsePage(BuildPage.class, parameters);
            } 
        });
    }
}