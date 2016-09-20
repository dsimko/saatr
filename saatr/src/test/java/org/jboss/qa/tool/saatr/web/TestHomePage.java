// package org.jboss.qa.tool.saatr.web;
//
// import org.apache.wicket.util.tester.WicketTester;
// import org.junit.Before;
// import org.junit.Rule;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.test.context.ContextConfiguration;
// import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
// import com.github.fakemongo.junit.FongoRule;
//
// @RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(locations = { "classpath:/applicationContext.xml" })
// public class TestHomePage {
//
// @Rule
// public FongoRule fongoRule = new FongoRule();
//
// @Autowired
// private WicketApplication wicketApplication;
//
// private WicketTester tester;
//
// @Before
// public void setUp() {
// tester = new WicketTester(wicketApplication);
// }
//
// @Test
// public void homepageRendersSuccessfully() {
// // start and render the test page
// tester.startPage(wicketApplication.getHomePage());
//
// // assert rendered page class
// tester.assertRenderedPage(wicketApplication.getHomePage());
// }
// }
