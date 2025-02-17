/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring

import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.spring.processor.SpringProcessor
import io.openapiprocessor.spring.writer.java.HEADER
import io.openapiprocessor.test.TestSet


const val API_30 = "openapi30.yaml"
const val API_31 = "openapi31.yaml"


@Suppress("SameParameterValue")
fun testSet(
    name: String,
    parser: ParserType,
    openapi: String = "openapi.yaml",
    inputs: String = "inputs.yaml",
    generated: String = "generated.yaml"): TestSet {

    val testSet = TestSet()
    testSet.name = name
    testSet.processor = SpringProcessor()
    testSet.parser = parser.name
    testSet.openapi = openapi
    testSet.inputs = inputs
    testSet.generated = generated
    return testSet
}


fun setTestHeader() {
    HEADER = TEST_HEADER
}

private const val TEST_HEADER: String = """
/*
 * DO NOT MODIFY - this class was auto generated by openapi-processor-spring
 *
 * test
 * time
 * https://docs.openapiprocessor.io/spring 
 */

"""
