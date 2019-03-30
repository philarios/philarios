package io.philarios.schema.entities.codegen.types.shell

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import io.philarios.core.Scaffold
import io.philarios.schema.*
import io.philarios.schema.entities.codegen.util.className
import org.amshove.kluent.shouldEqual
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ScaffoldTypeNameSpec : Spek({

    describe("scaffoldTypeName") {
        it("works with struct field without fields") {
            val field = Field("name", false, Struct("pkg", "Name", emptyList()))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldEqual ParameterizedTypeName
                    .get(Scaffold::class.className, ClassName("pkg", "Name"))
                    .asNullable()
        }
        it("works with struct field with fields") {
            val field = Field("name", false, Struct("pkg", "Name", listOf(Field("", false, StringType))))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldEqual ParameterizedTypeName
                    .get(Scaffold::class.className, ClassName("pkg", "Name"))
                    .asNullable()
        }
        it("works with union field without shapes") {
            val field = Field("name", false, Union("pkg", "Name", emptyList()))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldEqual ParameterizedTypeName
                    .get(Scaffold::class.className, ClassName("pkg", "Name"))
                    .asNullable()
        }
        it("works with option type containing a string type") {
            val field = Field("name", false, OptionType(StringType))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldEqual ParameterizedTypeName
                    .get(Scaffold::class.className, String::class.className)
                    .asNullable()
        }
        it("works with option type containing a struct type") {
            val field = Field("name", false, OptionType(Struct("pkg", "Name", listOf(Field("foo", false, StringType)))))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldEqual ParameterizedTypeName
                    .get(Scaffold::class.className, ClassName("pkg", "Name"))
                    .asNullable()
        }
        it("works with list type containing a string list type") {
            val field = Field("name", false, ListType(StringType))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldEqual ParameterizedTypeName
                    .get(List::class.className, ParameterizedTypeName
                            .get(Scaffold::class.className, String::class.className))
                    .asNullable()
        }
        it("works with list type containing a struct list type") {
            val field = Field("name", false, ListType(Struct("pkg", "Name", listOf(Field("foo", false, StringType)))))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldEqual ParameterizedTypeName
                    .get(List::class.className, ParameterizedTypeName
                            .get(Scaffold::class.className, ClassName("pkg", "Name")))
                    .asNullable()
        }
        it("works with string field") {
            val field = Field("name", false, StringType)
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldEqual ParameterizedTypeName
                    .get(Scaffold::class.className, String::class.className)
                    .asNullable()
        }
        it("works with list type containing map type containing a string to struct mapping") {
            val field = Field("name", false, ListType(
                    MapType(
                            StringType,
                            Struct("pkg", "Name", emptyList())
                    )
            ))
            val scaffoldTypeName = field.scaffoldTypeName(emptyMap())
            scaffoldTypeName shouldEqual ParameterizedTypeName.get(List::class.className,
                    ParameterizedTypeName.get(Map::class.className,
                            ParameterizedTypeName.get(Scaffold::class.className, String::class.className),
                            ParameterizedTypeName.get(Scaffold::class.className, ClassName("pkg", "Name"))
                    )
            ).asNullable()
        }
    }

})
