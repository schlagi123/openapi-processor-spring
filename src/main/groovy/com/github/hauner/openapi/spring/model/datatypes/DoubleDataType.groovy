/*
 * Copyright 2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hauner.openapi.spring.model.datatypes

/**
 * @author Martin Hauner
 * @authro Bastian Wilhelm
 */
class DoubleDataType implements DataType {
    DataTypeConstraints constraints

    private static final String NAME = 'Double'
    private static final String PACKAGE_NAME = 'java.lang'

    static boolean isDouble (DataType dataType) {
        dataType != null && dataType.packageName == PACKAGE_NAME && dataType.name == NAME
    }

    @Override
    String getName () {
        return NAME
    }

    @Override
    String getPackageName () {
        return PACKAGE_NAME
    }

    @Override
    List<DataType> getGenerics () {
        return []
    }
}
