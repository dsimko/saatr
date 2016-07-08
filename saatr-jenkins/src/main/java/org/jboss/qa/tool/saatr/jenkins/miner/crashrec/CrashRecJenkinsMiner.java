package org.jboss.qa.tool.saatr.jenkins.miner.crashrec;

import static com.github.danielpacak.jenkins.ci.core.client.JenkinsClient.SEGMENT_JOB;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jboss.qa.tool.saatr.jenkins.miner.AbstractJenkinsMiner;
import org.jboss.qa.tool.saatr.jenkins.miner.Utils;

import com.github.danielpacak.jenkins.ci.core.client.JenkinsClient;

public class CrashRecJenkinsMiner extends AbstractJenkinsMiner {

    @Override
    public Map<String, String> mine(URL buildUrl) {
        Map<String, String> map = new HashMap<>();
        CrashRecBuild build = getBuild(buildUrl);
        map.put("timestamp", build.getTimestamp());
        map.put("jobName", build.getJobName());
        map.put("eapVersion", build.getEapVersion());
        return map;
    }

    private CrashRecBuild getBuild(URL buildUrl) {
        JenkinsClient client = createClient(buildUrl);
        client.getMessageConverters().add(new CrashRecBuildHttpMessageConverter());
        return client.getForObject(SEGMENT_JOB + "/" + Utils.getJobName(buildUrl) + "/" + Utils.getBuildNumber(buildUrl) + "/api/json",
                CrashRecBuild.class);
    }

}
