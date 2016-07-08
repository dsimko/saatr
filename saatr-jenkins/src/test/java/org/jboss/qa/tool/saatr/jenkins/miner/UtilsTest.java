package org.jboss.qa.tool.saatr.jenkins.miner;

import static org.hamcrest.CoreMatchers.is;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

    private static final String jenkinsBuildUrl1 = "https://qe-jenkins.mycompany.com/view/MyProduct/view/MyComponent/job/my-job/PARAM1=value1,PARAM2=value2/5/";
    private static final String jenkinsBuildUrl2 = "http://qe-jenkins.mycompany.com/view/MyProduct/view/MyComponent/job/my-job/PARAM1=value1,PARAM2=value2/5";
    private static final String jenkinsBuildUrl3 = "http://qe-jenkins.mycompany.com:8080/job/my-job/35/";

    @Test
    public void getJobName() throws MalformedURLException {
        Assert.assertThat(Utils.getJobName(new URL(jenkinsBuildUrl1)), is("my-job/PARAM1=value1,PARAM2=value2"));
        Assert.assertThat(Utils.getJobName(new URL(jenkinsBuildUrl2)), is("my-job/PARAM1=value1,PARAM2=value2"));
        Assert.assertThat(Utils.getJobName(new URL(jenkinsBuildUrl3)), is("my-job"));
    }

    @Test
    public void getBuildNumber() throws MalformedURLException {
        Assert.assertThat(Utils.getBuildNumber(new URL(jenkinsBuildUrl1)), is(5L));
        Assert.assertThat(Utils.getBuildNumber(new URL(jenkinsBuildUrl2)), is(5L));
        Assert.assertThat(Utils.getBuildNumber(new URL(jenkinsBuildUrl3)), is(35L));
    }

    @Test
    public void getPort() throws MalformedURLException {
        Assert.assertThat(Utils.getPort(new URL(jenkinsBuildUrl1)), is(443));
        Assert.assertThat(Utils.getPort(new URL(jenkinsBuildUrl2)), is(80));
        Assert.assertThat(Utils.getPort(new URL(jenkinsBuildUrl3)), is(8080));
    }

}
