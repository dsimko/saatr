package org.jboss.qa.tool.saatr.web.component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 * Converts from and to URLs.
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class URLConverter implements IConverter<URL> {

    @Override
    public URL convertToObject(String value, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        }
        try {
            return new URL(value.toString());
        } catch (MalformedURLException e) {
            throw new ConversionException("'" + value + "' is not a valid URL");
        }
    }

    @Override
    public String convertToString(URL value, Locale locale) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}