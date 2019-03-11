package io.philarios.schema.translators.codegen.types.shell

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.philarios.core.Scaffold
import io.philarios.schema.*
import io.philarios.schema.translators.codegen.util.className
import io.philarios.util.tests.calling
import io.philarios.util.tests.with

class ScaffoldTypeNameSpec : FreeSpec({

    calling("scaffoldTypeName") {
        with("struct field without fields") {
            val field = Field("name", false, Struct("pkg", "Name", emptyList()))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldBe ParameterizedTypeName
                    .get(Scaffold::class.className, ClassName("pkg", "Name"))
                    .asNullable()
        }
        with("struct field with fields") {
            val field = Field("name", false, Struct("pkg", "Name", listOf(Field("", false, StringType))))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldBe ParameterizedTypeName
                    .get(Scaffold::class.className, ClassName("pkg", "Name"))
                    .asNullable()
        }
        with("union field without shapes") {
            val field = Field("name", false, Union("pkg", "Name", emptyList()))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldBe ParameterizedTypeName
                    .get(Scaffold::class.className, ClassName("pkg", "Name"))
                    .asNullable()
        }
        with("option type containing a string type") {
            val field = Field("name", false, OptionType(StringType))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldBe ParameterizedTypeName
                    .get(Scaffold::class.className, String::class.className)
                    .asNullable()
        }
        with("option type containing a struct type") {
            val field = Field("name", false, OptionType(Struct("pkg", "Name", listOf(Field("foo", false, StringType)))))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldBe ParameterizedTypeName
                    .get(Scaffold::class.className, ClassName("pkg", "Name"))
                    .asNullable()
        }
        with("list type containing a string list type") {
            val field = Field("name", false, ListType(StringType))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldBe ParameterizedTypeName
                    .get(List::class.className, ParameterizedTypeName
                            .get(Scaffold::class.className, String::class.className))
                    .asNullable()
        }
        with("list type containing a struct list type") {
            val field = Field("name", false, ListType(Struct("pkg", "Name", listOf(Field("foo", false, StringType)))))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldBe ParameterizedTypeName
                    .get(List::class.className, ParameterizedTypeName
                            .get(Scaffold::class.className, ClassName("pkg", "Name")))
                    .asNullable()
        }
        with("string field") {
            val field = Field("name", false, StringType)
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldBe ParameterizedTypeName
                    .get(Scaffold::class.className, String::class.className)
                    .asNullable()
        }
        with("list type containing map type containing a string to struct mapping") {
            val field = Field("name", false, ListType(
                    MapType(
                            StringType,
                            Struct("pkg", "Name", emptyList())
                    )
            ))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldBe ParameterizedTypeName.get(List::class.className,
                    ParameterizedTypeName.get(Map::class.className,
                            ParameterizedTypeName.get(Scaffold::class.className, String::class.className),
                            ParameterizedTypeName.get(Scaffold::class.className, ClassName("pkg", "Name"))
                    )
            ).asNullable()
        }
    }

})
