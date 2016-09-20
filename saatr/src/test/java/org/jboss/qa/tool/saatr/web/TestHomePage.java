package org.jboss.qa.tool.saatr.web;

import org.apache.wicket.util.tester.WicketTester;
import org.jboss.qa.tool.saatr.SaatrApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest({ "spring.data.mongodb.port=0" })
public class TestHomePage {

	@Autowired
	private SaatrApplication saatrApplication;

	private WicketTester tester;

	@Before
	public void setUp() {
		tester = new WicketTester(saatrApplication);
	}

	@Test
	public void homepageRendersSuccessfully() {
		// start and render the test page
		tester.startPage(saatrApplication.getHomePage());

		// assert rendered page class
		tester.assertRenderedPage(saatrApplication.getHomePage());
	}
}
