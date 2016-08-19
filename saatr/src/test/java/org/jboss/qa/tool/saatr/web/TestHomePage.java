package org.jboss.qa.tool.saatr.web;

import org.apache.wicket.util.tester.WicketTester;
import org.jboss.qa.tool.saatr.web.page.ConfigPage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage {
    private WicketTester tester;

    @Before
    public void setUp() {
        tester = new WicketTester(new WicketApplication());
    }

    @Test
    @Ignore
    public void homepageRendersSuccessfully() {
        // start and render the test page
        tester.startPage(ConfigPage.class);

        // assert rendered page class
        tester.assertRenderedPage(ConfigPage.class);
    }
}
