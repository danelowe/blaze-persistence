/*
 * Copyright 2014 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blazebit.persistence.impl.datanucleus;

import com.blazebit.persistence.impl.jpa.ObjectBuilderJPAQueryAdapter;
import com.blazebit.apt.service.ServiceProvider;
import com.blazebit.persistence.ObjectBuilder;
import com.blazebit.persistence.spi.QueryTransformer;
import java.util.logging.Logger;
import javax.persistence.TypedQuery;

/**
 *
 * @author Christian Beikov
 * @since 1.0
 */
@ServiceProvider(QueryTransformer.class)
public class DataNucleusQueryTransformer implements QueryTransformer {

    public DataNucleusQueryTransformer() {
        Logger.getLogger(DataNucleusQueryTransformer.class.getName()).warning("The DataNucleus integration is experimental and should not be used in production yet!");
    }

    @Override
    public <X> TypedQuery<X> transformQuery(TypedQuery<?> query, ObjectBuilder<X> objectBuilder) {
        return new ObjectBuilderJPAQueryAdapter<X>(query, objectBuilder);
    }

}