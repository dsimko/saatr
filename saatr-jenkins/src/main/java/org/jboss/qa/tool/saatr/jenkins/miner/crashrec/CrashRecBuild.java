package org.jboss.qa.tool.saatr.jenkins.miner.crashrec;

import java.io.Serializable;

/**
 * Specific Build model class.
 * 
 * @author dsimko@redhat.com
 *
 */
public class CrashRecBuild implements Serializable {

    private static final long serialVersionUID = 1L;

    private String timestamp;
    private String jobName;
    private String eapVersion;
    // TODO add more fields

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getEapVersion() {
        return eapVersion;
    }

    public void setEapVersion(String eapVersion) {
        this.eapVersion = eapVersion;
    }

}
