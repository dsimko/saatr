/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.qa.tool.saatr.web.comp.build;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;

@SuppressWarnings("serial")
public class BuildExpansion implements Set<BuildDocument>, Serializable {

    private static MetaDataKey<BuildExpansion> KEY = new MetaDataKey<BuildExpansion>() {

    };

    private Set<Object> ids = new HashSet<>();

    private boolean inverse;

    public void expandAll() {
        ids.clear();

        inverse = true;
    }

    public void collapseAll() {
        ids.clear();

        inverse = false;
    }

    @Override
    public boolean add(BuildDocument build) {
        if (build == null) {
            return false;
        } else if (inverse) {
            return ids.remove(build.getJobName());
        } else {
            return ids.add(build.getJobName());
        }
    }

    @Override
    public boolean remove(Object o) {
        BuildDocument build = (BuildDocument) o;

        if (inverse) {
            return ids.add(build.getJobName());
        } else {
            return ids.remove(build.getJobName());
        }
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        BuildDocument build = (BuildDocument) o;

        if (inverse) {
            return !ids.contains(build.getJobName());
        } else {
            return ids.contains(build.getJobName());
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <A> A[] toArray(A[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<BuildDocument> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends BuildDocument> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the expansion for the session.
     * 
     * @return expansion
     */
    public static BuildExpansion get() {
        BuildExpansion expansion = Session.get().getMetaData(KEY);
        if (expansion == null) {
            expansion = new BuildExpansion();

            Session.get().setMetaData(KEY, expansion);
        }
        return expansion;
    }
}
