package org.jboss.qa.tool.saatr.jenkins.miner.crashrec;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.github.danielpacak.jenkins.ci.core.http.HttpInputMessage;
import com.github.danielpacak.jenkins.ci.core.http.HttpOutputMessage;
import com.github.danielpacak.jenkins.ci.core.http.converter.HttpMessageConverter;

public class CrashRecBuildHttpMessageConverter implements HttpMessageConverter<CrashRecBuild> {

    @Override
    public boolean canRead(Class<?> clazz) {
        return CrashRecBuild.class.equals(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz) {
        return false;
    }

    @Override
    public CrashRecBuild read(Class<? extends CrashRecBuild> clazz, HttpInputMessage inputMessage) throws IOException {
        CrashRecBuild build = new CrashRecBuild();
        String content = IOUtils.toString(inputMessage.getBody(), StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(content);
        String eapVersion = jsonObject.getJSONArray("actions").getJSONObject(0).getJSONArray("parameters").getJSONObject(0)
                .getString("value");
        String jobName = jsonObject.getString("fullDisplayName");
        String timestamp = String.valueOf(jsonObject.getLong("timestamp"));
        build.setEapVersion(eapVersion);
        build.setJobName(jobName);
        build.setTimestamp(timestamp);
        // TODO mine more data
        return build;
    }

    @Override
    public void write(CrashRecBuild t, String contentType, HttpOutputMessage outputMessage) throws IOException {
        throw new UnsupportedOperationException();
    }

}