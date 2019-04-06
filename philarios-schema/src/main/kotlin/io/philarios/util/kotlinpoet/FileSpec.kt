package io.philarios.util.kotlinpoet

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

fun FileSpec.Builder.addTypes(typeSpecs: List<TypeSpec>): FileSpec.Builder {
    return typeSpecs.fold(this) { builder, typeSpec ->
        builder.addType(typeSpec)
    }
}

fun FileSpec.Builder.addFunctions(funSpecs: List<FunSpec>): FileSpec.Builder {
    return funSpecs.fold(this) { builder, funSpec ->
        builder.addFunction(funSpec)
    }
}

data class Import(val packageName: String, val names: List<String>)

fun FileSpec.Builder.addStaticImport(import: Import) =
        addStaticImport(import.packageName, *import.names.toTypedArray())

fun FileSpec.Builder.addStaticImports(vararg imports: Import) =
        imports.fold(this) { builder, import ->
            builder.addStaticImport(import)
        }

fun FileSpec.Builder.addStaticImports(imports: List<Import>) = addStaticImports(*imports.toTypedArray())