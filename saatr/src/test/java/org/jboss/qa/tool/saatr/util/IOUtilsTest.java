
package org.jboss.qa.tool.saatr.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.jboss.qa.tool.saatr.domain.build.TestCase;
import org.jboss.qa.tool.saatr.domain.build.TestSuite;
import org.jboss.qa.tool.saatr.domain.build.TestCase.Fragment;
import org.jboss.qa.tool.saatr.jaxb.surefire.Testsuite;
import org.junit.BeforeClass;
import org.junit.Test;

public class IOUtilsTest {

    private static List<Testsuite> testsuites;

    private static TestSuite testsuiteData;

    @BeforeClass
    public static void init() throws Exception {
        testsuites = IOUtils.unzipAndUnmarshalTestsuite(new FileInputStream(new File(IOUtilsTest.class.getResource("TEST-surefire-report.xml.zip").getPath())));
        testsuiteData = TestSuite.create(testsuites.get(0));
    }

    @Test
    public void unzipAndUnmarshalTestsuite() throws Exception {
        assertEquals(1, testsuites.size());
    }

    @Test
    public void checkTestsuite() {
        assertEquals("org.jboss.as.test.jbossts.crashrec.test.JPAJdbcCrashRecoveryTestCase(jta)", testsuiteData.getName());
        assertEquals(Double.valueOf(107.416), testsuiteData.getTime());
        assertSame(16, testsuiteData.getTests());
        assertSame(0, testsuiteData.getErrors());
        assertSame(12, testsuiteData.getSkipped());
        assertSame(4, testsuiteData.getFailures());
    }

    @Test
    public void checkTestcases() {
        List<TestCase> testcases = testsuiteData.getTestcases();
        assertSame(8, testcases.size());

        TestCase testcaseData = testcases.get(0);
        assertEquals("commitHaltAtPhaseEnd", testcaseData.getName());
        assertEquals("org.jboss.as.test.jbossts.crashrec.test.JPALrcoCrashRecoveryTestCase(jta)", testcaseData.getClassname());
        assertEquals(Double.valueOf("81.127"), testcaseData.getTime());
        assertEquals(TestCase.Status.Success, testcaseData.getStatus());

        testcaseData = testcases.get(1);
        assertEquals(TestCase.Status.Skipped, testcaseData.getStatus());

        testcaseData = testcases.get(2);
        assertEquals(TestCase.Status.Skipped, testcaseData.getStatus());
        assertEquals("Unignore when JBEAP-3944 is included in EAP 7.x.x", testcaseData.getSkipped().getMessage());
        assertEquals("INFO  [org.jboss.as.server.deployment] (MSC service thread 1-7) WFLYSRV0028:", testcaseData.getSystemOut());

        testcaseData = testcases.get(3);
        assertEquals(TestCase.Status.Failure, testcaseData.getStatus());
        List<Fragment> failures = testcaseData.getFailure();
        assertEquals(1, failures.size());
        assertEquals("Incorrect data in database after crash recovery. expected", failures.get(0).getMessage());
        assertEquals("java.lang.AssertionError", failures.get(0).getType());
        assertEquals("java.lang.AssertionError: Incorrect data in", failures.get(0).getValue());
        assertEquals("16:52:09,464 INFO  [org.jboss.as.test.jbossts.junit.JUnitRuleProcessAnnotation]", testcaseData.getSystemOut());

        failures = testcaseData.getRerunFailure();
        assertEquals(1, failures.size());
        assertEquals("Another Incorrect data in database after crash recovery.", failures.get(0).getMessage());
        assertEquals("java.lang.AssertionError", failures.get(0).getType());
        // assertEquals("database after crash recovery expected",
        // rerunFailures.get(0).getValue());

        testcaseData = testcases.get(4);
        assertEquals(TestCase.Status.FlakyFailure, testcaseData.getStatus());
        failures = testcaseData.getFlakyFailures();
        assertEquals(1, failures.size());
        assertEquals("JBoss log parsed file", failures.get(0).getMessage());
        assertEquals("java.lang.AssertionError", failures.get(0).getType());
        assertEquals("java.lang.AssertionError: JBoss log parsed", failures.get(0).getValue());

        testcaseData = testcases.get(5);
        assertEquals(TestCase.Status.Failure, testcaseData.getStatus());
        failures = testcaseData.getFailure();
        assertEquals(1, failures.size());
        assertEquals("JBoss log parsed file", failures.get(0).getMessage());
        assertEquals("java.lang.AssertionError", failures.get(0).getType());
        assertEquals("java.lang.AssertionError: JBoss log parsed file", failures.get(0).getValue());
        assertEquals("12:50:34,693 INFO Newly created connection:", testcaseData.getSystemOut());

        testcaseData = testcases.get(6);
        assertEquals(TestCase.Status.Error, testcaseData.getStatus());
        Fragment errorData = testcaseData.getError();
        assertEquals("Cannot deploy: tx-inconsistent-when-higher-load.jar", errorData.getMessage());
        assertEquals("org.jboss.arquillian.container.spi.client.container.DeploymentException", errorData.getType());
        assertEquals("org.jboss.arquillian.container.spi.client", errorData.getValue());
        assertEquals("12:55:54,242 INFO  [org.jboss.as.test.jbossts.client.utils.JDBCDriverManager", testcaseData.getSystemOut());

        testcaseData = testcases.get(7);
        assertEquals(TestCase.Status.FlakyError, testcaseData.getStatus());
        failures = testcaseData.getFlakyErrors();
        assertEquals(1, failures.size());
        assertEquals("Transaction rolled back", failures.get(0).getMessage());
        assertEquals("javax.ejb.EJBTransactionRolledbackException", failures.get(0).getType());
        // assertEquals("javax.ejb.EJBTransactionRolledbackException:
        // Transaction rolled back", failures.get(0).getValue());
        // assertEquals("02:01:11,226 INFO
        // [org.jboss.as.test.jbossts.junit.JUnitRuleIgnoreIfNameContained",
        // testcaseData.getSystemOut());
    }

}
