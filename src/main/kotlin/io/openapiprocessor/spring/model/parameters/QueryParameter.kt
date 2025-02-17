/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.model.parameters

import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.parameters.ParameterBase
import io.openapiprocessor.core.model.datatypes.MappedDataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType

val Maps = listOf(
    Map::class.java.name,
    "org.springframework.util.MultiValueMap"
)

/**
 * OpenAPI query parameter.
 */
class QueryParameter(
    name: String,
    dataType: DataType,
    required: Boolean = false,
    deprecated: Boolean = false,
    description: String? = null
): ParameterBase(name, dataType, required, deprecated, description) {

    /**
     * controls if a parameter should have a {@code @RequestParam} annotation.
     */
    override val withAnnotation: Boolean
        get() {
            // Map should be annotated
            if (isMappedMap) {
                return true
            }

            // Pojo's should NOT be annotated
            if (dataType is ObjectDataType) {
                return false
            }

            // Mapped should NOT be annotated if it was object schema
            // Mapped should be annotated if it was a simple schema
            if (dataType is MappedDataType) {
                return dataType.simpleDataType
            }

            return true
        }

    /**
     * controls if a {@code @RequestParam} should have any parameters.
     */
    override val withParameters: Boolean
        get() {
            // Map should not have parameters
            if (isMappedMap) {
                return false
            }

            // Pojo should not have parameters
            if (dataType is ObjectDataType) {
                return false
            }

            return true
        }

    private val isMappedMap: Boolean
        get() {
            if (dataType !is MappedDataType) {
                return false
            }

            val type = dataType.getImports().first()
            return Maps.contains(type)
        }
}
