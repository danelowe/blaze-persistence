/*
 * Copyright 2014 - 2020 Blazebit.
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

package com.blazebit.persistence.spi;

/**
 * The possible update join styles.
 *
 * @author Christian Beikov
 * @since 1.5.0
 */
public enum UpdateJoinStyle {
    /**
     * No support for joins in update statements.
     */
    NONE,
    /**
     * Requires a FROM clause for table references.
     */
    FROM,
    /**
     * Requires a FROM clause for table references but the table alias in the UPDATE clause.
     */
    FROM_ALIAS,
    /**
     * Requires table references to be appended after the update table.
     */
    REFERENCE,
    /**
     * Requires a MERGE statement.
     */
    MERGE;
}