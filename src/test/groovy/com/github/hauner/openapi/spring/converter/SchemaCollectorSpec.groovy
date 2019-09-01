/*
 * Copyright 2019 the original authors
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

package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.generatr.DefaultApiOptions
import com.github.hauner.openapi.spring.model.DataTypes
import io.swagger.v3.oas.models.media.Schema
import spock.lang.Specification

class SchemaCollectorSpec extends Specification {

    void "collects component schemas" () {
        def dataTypes = new DataTypes()
        def collector = new SchemaCollector(converter: new DataTypeConverter(new DefaultApiOptions()))

        def schemas = [
            'Book': new Schema (type: 'object', properties: [:] as Map)
        ]

        when:
        collector.collect (schemas, dataTypes)

        then:
        dataTypes.size () == 1
        dataTypes.find('Book')
    }

}


