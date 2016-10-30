
package org.jboss.qa.tool.saatr.web.comp;

import org.apache.wicket.extensions.markup.html.basic.ILinkParser;
import org.junit.Assert;
import org.junit.Test;

public class SmartLinkParserTest extends Assert {

    @Test
    public void test1() {
        ILinkParser parser = SmartLinkParser.INSTANCE;
        assertNull(parser.parse(null));
        assertEquals("", parser.parse(""));
        assertEquals("test", parser.parse("test"));

        assertEquals("<a target=\"_blank\" href=\"http://www.test.com\">http://www.test.com</a>", parser.parse("http://www.test.com"));
        assertEquals("text (<a target=\"_blank\" href=\"http://www.test.com\">http://www.test.com</a>) text", parser.parse("text (http://www.test.com) text"));
        assertEquals("text <a target=\"_blank\" href=\"http://www.test.com\">http://www.test.com</a> text", parser.parse("text http://www.test.com text"));
        assertEquals("text <a target=\"_blank\" href=\"http://www.test.com:8080\">http://www.test.com:8080</a> text",
                parser.parse("text http://www.test.com:8080 text"));
        assertEquals("text <a target=\"_blank\" href=\"http://www.test.com/test/murx.jsp\">http://www.test.com/test/murx.jsp</a> text",
                parser.parse("text http://www.test.com/test/murx.jsp text"));
        assertEquals(
                "text <a target=\"_blank\" href=\"http://www.test.com/test/murx.jsp?query=test&q2=murx\">http://www.test.com/test/murx.jsp?query=test&q2=murx</a> text",
                parser.parse("text http://www.test.com/test/murx.jsp?query=test&q2=murx text"));

        assertEquals(
                "line 1 <a target=\"_blank\" href=\"http://www.test.com/test/murx.jsp\">http://www.test.com/test/murx.jsp</a> \nline2 murx@email.de \r\nline3",
                parser.parse("line 1 http://www.test.com/test/murx.jsp \nline2 murx@email.de \r\nline3"));
    }

    @Test
    public void dontParseMarkup() {
        String text = "<a href=\"http://some.url\">label</a>";
        ILinkParser parser = SmartLinkParser.INSTANCE;

        String parsed = parser.parse(text);
        assertEquals(text, parsed);
    }

    @Test
    public void parseStrangeUrl() {
        ILinkParser parser = SmartLinkParser.INSTANCE;
        assertEquals(
                "<a target=\"_blank\" href=\"http://jenkins.mw.lab.eng.bos.redhat.com/hudson/job/eap-71-jbossts-jbossas-cmr/LABEL=linux&&mem4G&&eap-sustaining,jdk=java1.8/6/\">http://jenkins.mw.lab.eng.bos.redhat.com/hudson/job/eap-71-jbossts-jbossas-cmr/LABEL=linux&&mem4G&&eap-sustaining,jdk=java1.8/6/</a>",
                parser.parse(
                        "http://jenkins.mw.lab.eng.bos.redhat.com/hudson/job/eap-71-jbossts-jbossas-cmr/LABEL=linux&&mem4G&&eap-sustaining,jdk=java1.8/6/"));
    }

}