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

package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.bson.types.ObjectId;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class BuildFilterSelection implements Serializable {

    private static MetaDataKey<BuildFilterSelection> KEY = new MetaDataKey<BuildFilterSelection>() {

    };

    private Set<ObjectId> ids = new HashSet<>();

    /**
     * Get the selections for the session.
     * 
     * @return selections
     */
    public static BuildFilterSelection get() {
        BuildFilterSelection selections = Session.get().getMetaData(KEY);
        if (selections == null) {
            selections = new BuildFilterSelection();
            Session.get().setMetaData(KEY, selections);
        }
        return selections;
    }
}
