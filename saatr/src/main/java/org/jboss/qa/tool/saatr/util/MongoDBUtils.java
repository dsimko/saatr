package org.jboss.qa.tool.saatr.util;

import java.util.Iterator;

import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.web.WicketApplication;
import org.jboss.qa.tool.saatr.web.component.BuildProvider.BuildFilter;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for working with MongoDB.
 * 
 * @author dsimko@redhat.com
 *
 */
public class MongoDBUtils {

    private static final Logger LOG = LoggerFactory.getLogger(MongoDBUtils.class);

    public static void save(Build jobRun) {
        getDS().save(jobRun);
        LOG.info("Document successfully stored in MongoDB.");
    }

    public static Iterator<Build> query(long first, long count, BuildFilter filter) {
        final Query<Build> query = createQueryAndApplyFilter(filter);
        query.limit((int) count);
        query.offset((int) first);
        return query.iterator();
    }

    public static long count(BuildFilter filter) {
        return getDS().getCount(createQueryAndApplyFilter(filter));
    }

    private static Query<Build> createQueryAndApplyFilter(BuildFilter filter) {
        final Query<Build> query = getDS().createQuery(Build.class);
        if (filter.getBuildNumber() != null) {
            query.and(query.criteria("buildNumber").equal(filter.getBuildNumber()));
        }
        return query;
    }

    private static Datastore getDS() {
        return WicketApplication.get().getDatastore();
    }
}
