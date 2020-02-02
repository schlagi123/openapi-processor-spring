/*
 * Copyright 2019-2020 the original authors
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

package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.converter.ApiOptions
import com.github.hauner.openapi.spring.model.Api
import com.github.hauner.openapi.spring.model.DataTypes
import com.github.hauner.openapi.spring.model.Interface
import com.github.hauner.openapi.spring.model.datatypes.DataType

import com.github.hauner.openapi.spring.model.datatypes.MappedDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
import com.github.hauner.openapi.spring.model.datatypes.StringEnumDataType
import com.github.hauner.openapi.spring.support.Sl4jMockRule
import com.squareup.javapoet.TypeSpec
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.slf4j.Logger
import spock.lang.Specification

class ApiWriterSpec extends Specification {

    @Rule TemporaryFolder target

    def log = Mock Logger
    @Rule Sl4jMockRule rule = new Sl4jMockRule(ApiWriter, log)

    List<String> apiPkgPath = ['com', 'github', 'hauner', 'openapi', 'api']
    List<String> apiModelPath = ['com', 'github', 'hauner', 'openapi', 'model']

    void "creates package structure in target folder"() {
        def opts = new ApiOptions(
            packageName: 'com.github.hauner.openapi',
            targetDir: [target.root.toString (), 'java', 'src'].join (File.separator)
        )

        when:
        new ApiWriter (opts, Stub (InterfaceGenerator), null, null).write (new Api())

        then:
        def api = new File([opts.targetDir, 'com', 'github', 'hauner', 'openapi', 'api'].join(File.separator))
        def model = new File([opts.targetDir, 'com', 'github', 'hauner', 'openapi', 'model'].join(File.separator))
        api.exists ()
        api.isDirectory ()
        model.exists ()
        model.isDirectory ()
    }

    void "does not log error when the target folder structure already exists" () {
        def opts = new ApiOptions(
            packageName: 'com.github.hauner.openapi',
            targetDir: [target.root.toString (), 'java', 'src'].join (File.separator)
        )

        when:
        target.newFolder ('java', 'src', 'com', 'github', 'hauner', 'openapi', 'api')
        target.newFolder ('java', 'src', 'com', 'github', 'hauner', 'openapi', 'model')
        new ApiWriter (opts, Stub (InterfaceGenerator), null, null).write (new Api())

        then:
        0 * log.error (*_)
    }

    void "generates interface sources in api target folder"() {
        def interfaceWriter = Stub (InterfaceGenerator) {
            generateTypeSpec (_ as Interface) >> {
                TypeSpec.interfaceBuilder ('FooApi').build ()
            } >> {
                TypeSpec.interfaceBuilder ('BarApi').build ()
            }
        }

        def opts = new ApiOptions(
            packageName: 'com.github.hauner.openapi',
            targetDir: [target.root.toString (), 'java', 'src'].join (File.separator)
        )

        def api = new Api(interfaces: [
            new Interface(pkg: "${opts.packageName}.api", name: 'Foo'),
            new Interface(pkg: "${opts.packageName}.api", name: 'Bar')
        ])

        when:
        new ApiWriter (opts, interfaceWriter, null, null)
            .write (api)

        then:
        def fooSource = new File(getApiPath (opts.targetDir, 'FooApi.java'))
        fooSource.text == """\
// This class is auto generated by https://github.com/hauner/openapi-generatr-spring.
// DO NOT EDIT.
package com.github.hauner.openapi.api;

interface FooApi {
}
"""
        def barSource = new File(getApiPath (opts.targetDir, 'BarApi.java'))
        barSource.text == """\
// This class is auto generated by https://github.com/hauner/openapi-generatr-spring.
// DO NOT EDIT.
package com.github.hauner.openapi.api;

interface BarApi {
}
"""
    }

    void "generates model sources in model target folder"() {
        def dataTypeWriter = Stub (DataTypeGenerator) {
            generateTypeSpec (_ as ObjectDataType) >> {
                TypeSpec.classBuilder ('Foo').build ()
            } >> {
                TypeSpec.classBuilder ('Bar').build ()
            }
        }

        def opts = new ApiOptions(
            packageName: 'com.github.hauner.openapi',
            targetDir: [target.root.toString (), 'java', 'src'].join (File.separator)
        )

        def dt = new DataTypes()
        dt.add (new ObjectDataType(packageName: "${opts.packageName}.model", name: 'Foo'))
        dt.add (new ObjectDataType(packageName: "${opts.packageName}.model", name: 'Bar'))
        def api = new Api(dt)

        when:
        new ApiWriter (opts, Stub(InterfaceGenerator), dataTypeWriter, Stub(StringEnumGenerator))
            .write (api)

        then:
        def fooSource = new File(getModelPath (opts.targetDir, 'Foo.java'))
        fooSource.text == """\
// This class is auto generated by https://github.com/hauner/openapi-generatr-spring.
// DO NOT EDIT.
package com.github.hauner.openapi.model;

class Foo {
}
"""
        def barSource = new File(getModelPath (opts.targetDir, 'Bar.java'))
        barSource.text == """\
// This class is auto generated by https://github.com/hauner/openapi-generatr-spring.
// DO NOT EDIT.
package com.github.hauner.openapi.model;

class Bar {
}
"""
    }

    void "generates model enum sources in model target folder"() {
        def enumWriter = Stub (StringEnumGenerator) {
            generateTypeSpec (_ as StringEnumDataType) >> {
                TypeSpec.enumBuilder ('Foo')
                    .addEnumConstant ('foo')
                    .build ()
            } >> {
                TypeSpec.enumBuilder ('Bar')
                    .addEnumConstant ('bar')
                    .build ()
            }
        }

        def opts = new ApiOptions(
            packageName: 'com.github.hauner.openapi',
            targetDir: [target.root.toString (), 'java', 'src'].join (File.separator)
        )

        def dt = new DataTypes()
        dt.add (new StringEnumDataType(packageName: "${opts.packageName}.model", name: 'Foo'))
        dt.add (new StringEnumDataType(packageName: "${opts.packageName}.model", name: 'Bar'))
        def api = new Api(dt)

        when:
        new ApiWriter (opts, Stub(InterfaceGenerator), Stub(DataTypeGenerator), enumWriter)
            .write (api)

        then:
        def fooSource = new File(getModelPath (opts.targetDir, 'Foo.java'))
        fooSource.text == """\
// This class is auto generated by https://github.com/hauner/openapi-generatr-spring.
// DO NOT EDIT.
package com.github.hauner.openapi.model;

enum Foo {
    foo
}
"""
        def barSource = new File(getModelPath (opts.targetDir, 'Bar.java'))
        barSource.text == """\
// This class is auto generated by https://github.com/hauner/openapi-generatr-spring.
// DO NOT EDIT.
package com.github.hauner.openapi.model;

enum Bar {
    bar
}
"""
    }

    void "generates model for object data types only" () {
        def dataTypeWriter = Mock (DataTypeGenerator)

        def opts = new ApiOptions(
            packageName: 'com.github.hauner.openapi',
            targetDir: [target.root.toString (), 'java', 'src'].join (File.separator)
        )

        def dt = new DataTypes()
        dt.add (new ObjectDataType(packageName: "${opts.packageName}.model", name: 'Foo'))
        dt.add (new ObjectDataType(packageName: "${opts.packageName}.model", name: 'Bar'))
        dt.add (new MappedDataType(packageName: "mapped", name: 'Type'))
        dt.add ('simple', new StringDataType ())
        def api = new Api(dt)

        DataType foo = dt.getObjectDataTypes ().find { it.name == 'Foo' }
        DataType bar = dt.getObjectDataTypes ().find { it.name == 'Bar' }
        DataType simple = dt.getObjectDataTypes ().find { it.name == 'simple' }
        DataType type = dt.getObjectDataTypes ().find { it.name == 'Type' }

        when:
        new ApiWriter (opts, Stub(InterfaceGenerator), dataTypeWriter, Stub(StringEnumGenerator))
            .write (api)

        then:
        1 * dataTypeWriter.generateTypeSpec (foo) >> TypeSpec.classBuilder ("Foo").build ()
        1 * dataTypeWriter.generateTypeSpec (bar) >> TypeSpec.classBuilder ("Bar").build ()
        0 * dataTypeWriter.generateTypeSpec (simple)
        0 * dataTypeWriter.generateTypeSpec (type)
    }

    String getApiPath(String targetFolder, String clazzName) {
        ([targetFolder] + apiPkgPath + [clazzName]).join(File.separator)
    }

    String getModelPath(String targetFolder, String clazzName) {
        ([targetFolder] + apiModelPath + [clazzName]).join(File.separator)
    }
}
