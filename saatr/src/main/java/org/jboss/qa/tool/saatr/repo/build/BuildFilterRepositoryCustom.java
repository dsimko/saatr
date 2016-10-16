
package org.jboss.qa.tool.saatr.repo.build;

import java.util.Iterator;

import org.jboss.qa.tool.saatr.domain.build.BuildFilter;

/**
 * The interface for repository functionality that will be implemented manually.
 * 
 * @author dsimko@redhat.com
 */
interface BuildFilterRepositoryCustom {

    BuildFilter findAndUpdateLastUsed(String id);

    Iterator<BuildFilter> query(long first, long count);

}
