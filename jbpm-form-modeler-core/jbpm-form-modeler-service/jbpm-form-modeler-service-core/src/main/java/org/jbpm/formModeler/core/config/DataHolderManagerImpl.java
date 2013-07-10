/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.core.config;


import org.jbpm.formModeler.core.config.builders.DataHolderBuilder;
import org.jbpm.formModeler.api.model.DataHolder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class DataHolderManagerImpl implements DataHolderManager {

    @Inject
    protected Instance<DataHolderBuilder> holderBuilders;

    @Override
    public DataHolderBuilder getBuilderByType(String builderId) {
        for(DataHolderBuilder builder : holderBuilders) {
            if (builder.getId().equals(builderId)) return builder;
        }
        return null;
    }

    @Override
    public DataHolder createDataHolderByType(String type, Map<String, Object> config) {
        DataHolderBuilder builder = getBuilderByType(type);
        if (builder == null) return null;

        return builder.buildDataHolder(config);
    }
}
