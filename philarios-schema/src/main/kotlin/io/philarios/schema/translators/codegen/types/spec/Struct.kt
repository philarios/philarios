package io.philarios.schema.translators.codegen.types.spec

import com.squareup.kotlinpoet.*
import io.philarios.core.Spec
import io.philarios.schema.Struct
import io.philarios.schema.translators.codegen.util.*
import io.philarios.schema.util.kotlinpoet.Statement
import io.philarios.schema.util.kotlinpoet.addStatements

internal val Struct.specTypeSpec
    get() = when {
        fields.isEmpty() -> null
        else -> dataClassSpecTypeSpec
    }

private val Struct.dataClassSpecTypeSpec
    get() = TypeSpec.classBuilder(specClassName.rawType)
            .addTypeVariable(TypeVariableName("C", KModifier.IN))
            .addSuperinterface(ParameterizedTypeName.get(Spec::class.className, TypeVariableName("C"), className))
            .addProperty(PropertySpec
                    .builder("body", bodyLambdaTypeName)
                    .addModifiers(KModifier.INTERNAL)
                    .initializer("body")
                    .build())
            .primaryConstructor(FunSpec.constructorBuilder()
                    .addParameter(bodyParameterSpec)
                    .build())
            .addFunction(FunSpec.builder("connect")
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(contextParameterSpec)
                    .returns(scaffoldTypeName)
                    .addStatements(
                            Statement("val builder = %T(context)", listOf(shellBuilderClassName)),
                            Statement("builder.apply(body)"),
                            Statement("return builder.shell")
                    )
                    .build())
            .build()
