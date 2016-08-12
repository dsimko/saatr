package org.jboss.qa.tool.saatr.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jboss.qa.tool.saatr.entity.Document;
import org.jboss.qa.tool.saatr.entity.Field;
import org.jboss.qa.tool.saatr.jenkins.IJenkinsMiner;
import org.junit.Assert;
import org.junit.Test;

public class DocumentUtilsTest {

    @Test
    public void unmarshal() {
        File xml = new File(DocumentUtilsTest.class.getResource("config.xml").getPath());
        Assert.assertNotNull(xml);
        Document document = DocumentUtils.unmarshal(xml);
        Assert.assertEquals("CrashRecFailed", document.getName());
        Assert.assertEquals("org.jboss.qa.tool.saatr.jenkins.miner.crashrec.CrashRecJenkinsMiner", document.getJenkinsMinerClass());
        Assert.assertEquals(6, document.getFields().size());
        Field field = document.getFields().get(document.getFields().size() - 1);
        Assert.assertEquals("txType", field.getName());
        assertThat(field.getOptions(), is(Arrays.asList("JTA", "JTS")));
    }

    @Test
    public void populate() {
        Document document = new Document();
        document.setJenkinsMinerClass("org.jboss.qa.tool.saatr.util.DocumentUtilsTest$MockMiner");
        DocumentUtils.populateFromJenkinsMiner(document, null);
        assertThat(document.getFields(), is(Arrays.asList(new Field("field1", "value1"), new Field("field2", "value2"))));
    }

    public static class MockMiner implements IJenkinsMiner {

        @Override
        public Map<String, String> mine(URL jenkinsBuildURL) {
            Map<String, String> map = new HashMap<>();
            map.put("field1", "value1");
            map.put("field2", "value2");
            return map;
        }

    }
}
