package org.jboss.qa.tool.saatr.jenkins;

import java.net.URL;
import java.util.Map;

/**
 * Purpose of the Miner is getting as much information from the given Jenkins
 * build as possible. Implementation can use for example REST API or SSH client
 * in order to get these information. Each peace of information is put to the
 * map e.g.:
 * 
 * <pre>
 * public class FailedBuildMiner implements IJenkinsMiner {
 * 
 *     &#64;Override
 *     public Map<String, String> mine(URL buildUrl) {
 *         Map<String, String> map = new HashMap<>();
 *         Build build = getBuildData(buildUrl);
 *         map.put("timestamp", build.getTimestamp());
 *         map.put("jobName", build.geJobName());
 *         map.put("eapVersion", build.getParam("eapVersion"));
 *         ...
 *         return map;
 *     }
 * }
 * </pre>
 * 
 * @author dsimko@redhat.com
 *
 */
public interface IJenkinsMiner {

    /**
     * Obtains as much data as possible from given Jenkins build URL.
     * 
     * @param jenkinsBuildURL
     * @return
     */
    Map<String, String> mine(URL jenkinsBuildURL);

}
