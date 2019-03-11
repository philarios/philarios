package io.philarios.schema

import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper

class SchemaScaffolder<in C>(internal val spec: SchemaSpec<C>) : Scaffolder<C, Schema> {
    override fun createScaffold(context: C): Scaffold<Schema> {
        val builder = SchemaShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TypeScaffolder<in C, out T : Type>(internal val spec: TypeSpec<C, T>) : Scaffolder<C, T> {
    override fun createScaffold(context: C): Scaffold<T> {
        val result = when (spec) {
            is StructSpec<C> -> StructScaffolder(spec).createScaffold(context)
            is UnionSpec<C> -> UnionScaffolder(spec).createScaffold(context)
            is EnumTypeSpec<C> -> EnumTypeScaffolder(spec).createScaffold(context)
            is RefTypeSpec<C> -> RefTypeScaffolder(spec).createScaffold(context)
            is OptionTypeSpec<C> -> OptionTypeScaffolder(spec).createScaffold(context)
            is ListTypeSpec<C> -> ListTypeScaffolder(spec).createScaffold(context)
            is MapTypeSpec<C> -> MapTypeScaffolder(spec).createScaffold(context)
            is BooleanTypeSpec<C> -> BooleanTypeScaffolder(spec).createScaffold(context)
            is DoubleTypeSpec<C> -> DoubleTypeScaffolder(spec).createScaffold(context)
            is FloatTypeSpec<C> -> FloatTypeScaffolder(spec).createScaffold(context)
            is LongTypeSpec<C> -> LongTypeScaffolder(spec).createScaffold(context)
            is IntTypeSpec<C> -> IntTypeScaffolder(spec).createScaffold(context)
            is ShortTypeSpec<C> -> ShortTypeScaffolder(spec).createScaffold(context)
            is ByteTypeSpec<C> -> ByteTypeScaffolder(spec).createScaffold(context)
            is CharacterTypeSpec<C> -> CharacterTypeScaffolder(spec).createScaffold(context)
            is StringTypeSpec<C> -> StringTypeScaffolder(spec).createScaffold(context)
            is AnyTypeSpec<C> -> AnyTypeScaffolder(spec).createScaffold(context)
        }
        return result as Scaffold<T>
    }
}

class StructScaffolder<in C>(internal val spec: StructSpec<C>) : Scaffolder<C, Struct> {
    override fun createScaffold(context: C): Scaffold<Struct> {
        val builder = StructShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class UnionScaffolder<in C>(internal val spec: UnionSpec<C>) : Scaffolder<C, Union> {
    override fun createScaffold(context: C): Scaffold<Union> {
        val builder = UnionShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class EnumTypeScaffolder<in C>(internal val spec: EnumTypeSpec<C>) : Scaffolder<C, EnumType> {
    override fun createScaffold(context: C): Scaffold<EnumType> {
        val builder = EnumTypeShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class RefTypeScaffolder<in C>(internal val spec: RefTypeSpec<C>) : Scaffolder<C, RefType> {
    override fun createScaffold(context: C): Scaffold<RefType> {
        val builder = RefTypeShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class OptionTypeScaffolder<in C>(internal val spec: OptionTypeSpec<C>) : Scaffolder<C, OptionType> {
    override fun createScaffold(context: C): Scaffold<OptionType> {
        val builder = OptionTypeShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ListTypeScaffolder<in C>(internal val spec: ListTypeSpec<C>) : Scaffolder<C, ListType> {
    override fun createScaffold(context: C): Scaffold<ListType> {
        val builder = ListTypeShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class MapTypeScaffolder<in C>(internal val spec: MapTypeSpec<C>) : Scaffolder<C, MapType> {
    override fun createScaffold(context: C): Scaffold<MapType> {
        val builder = MapTypeShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class BooleanTypeScaffolder<in C>(internal val spec: BooleanTypeSpec<C>) : Scaffolder<C, BooleanType> {
    override fun createScaffold(context: C): Scaffold<BooleanType> = Wrapper(BooleanType)
}

class DoubleTypeScaffolder<in C>(internal val spec: DoubleTypeSpec<C>) : Scaffolder<C, DoubleType> {
    override fun createScaffold(context: C): Scaffold<DoubleType> = Wrapper(DoubleType)
}

class FloatTypeScaffolder<in C>(internal val spec: FloatTypeSpec<C>) : Scaffolder<C, FloatType> {
    override fun createScaffold(context: C): Scaffold<FloatType> = Wrapper(FloatType)
}

class LongTypeScaffolder<in C>(internal val spec: LongTypeSpec<C>) : Scaffolder<C, LongType> {
    override fun createScaffold(context: C): Scaffold<LongType> = Wrapper(LongType)
}

class IntTypeScaffolder<in C>(internal val spec: IntTypeSpec<C>) : Scaffolder<C, IntType> {
    override fun createScaffold(context: C): Scaffold<IntType> = Wrapper(IntType)
}

class ShortTypeScaffolder<in C>(internal val spec: ShortTypeSpec<C>) : Scaffolder<C, ShortType> {
    override fun createScaffold(context: C): Scaffold<ShortType> = Wrapper(ShortType)
}

class ByteTypeScaffolder<in C>(internal val spec: ByteTypeSpec<C>) : Scaffolder<C, ByteType> {
    override fun createScaffold(context: C): Scaffold<ByteType> = Wrapper(ByteType)
}

class CharacterTypeScaffolder<in C>(internal val spec: CharacterTypeSpec<C>) : Scaffolder<C, CharacterType> {
    override fun createScaffold(context: C): Scaffold<CharacterType> = Wrapper(CharacterType)
}

class StringTypeScaffolder<in C>(internal val spec: StringTypeSpec<C>) : Scaffolder<C, StringType> {
    override fun createScaffold(context: C): Scaffold<StringType> = Wrapper(StringType)
}

class AnyTypeScaffolder<in C>(internal val spec: AnyTypeSpec<C>) : Scaffolder<C, AnyType> {
    override fun createScaffold(context: C): Scaffold<AnyType> = Wrapper(AnyType)
}

class FieldScaffolder<in C>(internal val spec: FieldSpec<C>) : Scaffolder<C, Field> {
    override fun createScaffold(context: C): Scaffold<Field> {
        val builder = FieldShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
