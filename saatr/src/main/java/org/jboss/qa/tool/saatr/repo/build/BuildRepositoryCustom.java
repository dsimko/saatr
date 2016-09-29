
package org.jboss.qa.tool.saatr.repo.build;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;
import org.jboss.qa.tool.saatr.domain.build.TestcaseDocument;
import org.jboss.qa.tool.saatr.domain.build.TestsuiteDocument;
import org.jboss.qa.tool.saatr.domain.config.ConfigDocument.ConfigProperty;
import org.jboss.qa.tool.saatr.jaxb.surefire.Testsuite;
import org.jboss.qa.tool.saatr.web.comp.build.BuildFilter;

/**
 * The interface for repository functionality that will be implemented manually.
 * 
 * @author dsimko@redhat.com
 */
interface BuildRepositoryCustom {

    Iterator<BuildDocument> query(long first, long count, BuildFilter filter);

    long count(BuildFilter filter);

    void fillBuildByTestsuites(List<Testsuite> input, BuildDocument build);

    void addIfAbsent(PropertyData property, Set<PropertyData> properties);

    <T extends DocumentWithProperties<?>> void addOrUpdateProperties(T persistable, Set<ConfigProperty> configProperties);

    Iterable<String> findDistinctVariableNames();

    Iterable<String> findDistinctVariableValues();

    TestsuiteDocument findTestsuiteById(UUID id);

    TestcaseDocument findTestcaseById(UUID id, int index);

    String aggregate(String query);

    List<BuildDocument> findFailedWithoutAdditionalInfo();

    void addConsoleText(BuildDocument buildDocument, String response);

    Iterator<BuildDocument> getRoots(BuildFilter filter);
    
    Iterator<? extends BuildDocument> getChildren(BuildDocument parent, BuildFilter filter);


}
