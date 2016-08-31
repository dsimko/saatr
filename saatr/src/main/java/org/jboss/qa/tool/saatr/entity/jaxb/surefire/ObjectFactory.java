//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.11 at 01:02:15 PM CEST 
//

package org.jboss.qa.tool.saatr.entity.jaxb.surefire;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the generated package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TestsuiteTestcaseSystemErr_QNAME = new QName("", "system-err");
    private final static QName _TestsuiteTestcaseError_QNAME = new QName("", "error");
    private final static QName _TestsuiteTestcaseSkipped_QNAME = new QName("", "skipped");
    private final static QName _TestsuiteTestcaseSystemOut_QNAME = new QName("", "system-out");

    /**
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Testsuite }
     * 
     */
    public Testsuite createTestsuite() {
        return new Testsuite();
    }

    /**
     * Create an instance of {@link Testsuite.Testcase }
     * 
     */
    public Testsuite.Testcase createTestsuiteTestcase() {
        return new Testsuite.Testcase();
    }

    /**
     * Create an instance of {@link Testsuite.Properties }
     * 
     */
    public Testsuite.Properties createTestsuiteProperties() {
        return new Testsuite.Properties();
    }

    /**
     * Create an instance of {@link Testsuite.Testcase.Failure }
     * 
     */
    public Testsuite.Testcase.Failure createTestsuiteTestcaseFailure() {
        return new Testsuite.Testcase.Failure();
    }

    /**
     * Create an instance of {@link Testsuite.Testcase.Failure }
     * 
     */
    public Testsuite.Testcase.FlakyError createTestsuiteTestcaseFlakyError() {
        return new Testsuite.Testcase.FlakyError();
    }

    /**
     * Create an instance of {@link Testsuite.Testcase.RerunFailure }
     * 
     */
    public Testsuite.Testcase.RerunFailure createTestsuiteTestcaseRerunFailure() {
        return new Testsuite.Testcase.RerunFailure();
    }

    /**
     * Create an instance of {@link Testsuite.Testcase.Skipped }
     * 
     */
    public Testsuite.Testcase.Skipped createTestsuiteTestcaseSkipped() {
        return new Testsuite.Testcase.Skipped();
    }

    /**
     * Create an instance of {@link Testsuite.Testcase.Error }
     * 
     */
    public Testsuite.Testcase.Error createTestsuiteTestcaseError() {
        return new Testsuite.Testcase.Error();
    }

    /**
     * Create an instance of {@link Testsuite.Properties.Property }
     * 
     */
    public Testsuite.Properties.Property createTestsuitePropertiesProperty() {
        return new Testsuite.Properties.Property();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }
     * {@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "system-err", scope = Testsuite.Testcase.class)
    public JAXBElement<Object> createTestsuiteTestcaseSystemErr(Object value) {
        return new JAXBElement<Object>(_TestsuiteTestcaseSystemErr_QNAME, Object.class, Testsuite.Testcase.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link Testsuite.Testcase.Error }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "error", scope = Testsuite.Testcase.class)
    public JAXBElement<Testsuite.Testcase.Error> createTestsuiteTestcaseError(Testsuite.Testcase.Error value) {
        return new JAXBElement<Testsuite.Testcase.Error>(_TestsuiteTestcaseError_QNAME, Testsuite.Testcase.Error.class,
                Testsuite.Testcase.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link Testsuite.Testcase.Skipped }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "skipped", scope = Testsuite.Testcase.class)
    public JAXBElement<Testsuite.Testcase.Skipped> createTestsuiteTestcaseSkipped(Testsuite.Testcase.Skipped value) {
        return new JAXBElement<Testsuite.Testcase.Skipped>(_TestsuiteTestcaseSkipped_QNAME, Testsuite.Testcase.Skipped.class,
                Testsuite.Testcase.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }
     * {@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "system-out", scope = Testsuite.Testcase.class)
    public JAXBElement<Object> createTestsuiteTestcaseSystemOut(Object value) {
        return new JAXBElement<Object>(_TestsuiteTestcaseSystemOut_QNAME, Object.class, Testsuite.Testcase.class, value);
    }

}
