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
import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.bson.types.ObjectId;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class BuildSelection implements Serializable {

    private static MetaDataKey<BuildSelection> KEY = new MetaDataKey<BuildSelection>() {

    };

    private Set<String> parents = new HashSet<>();

    private Set<ObjectId> ids = new HashSet<>();


    /**
     * Get the selections for the session.
     * 
     * @return selections
     */
    public static BuildSelection get() {
        BuildSelection selections = Session.get().getMetaData(KEY);
        if (selections == null) {
            selections = new BuildSelection();

            Session.get().setMetaData(KEY, selections);
        }
        return selections;
    }
}
