package io.philarios.structurizr

class WorkspaceSpec<in C>(internal val body: WorkspaceBuilder<C>.() -> Unit)

class ModelSpec<in C>(internal val body: ModelBuilder<C>.() -> Unit)

class PersonSpec<in C>(internal val body: PersonBuilder<C>.() -> Unit)

class SoftwareSystemSpec<in C>(internal val body: SoftwareSystemBuilder<C>.() -> Unit)

class ContainerSpec<in C>(internal val body: ContainerBuilder<C>.() -> Unit)

class ComponentSpec<in C>(internal val body: ComponentBuilder<C>.() -> Unit)

class RelationshipSpec<in C>(internal val body: RelationshipBuilder<C>.() -> Unit)

class ViewSetSpec<in C>(internal val body: ViewSetBuilder<C>.() -> Unit)

class ConfigurationSpec<in C>(internal val body: ConfigurationBuilder<C>.() -> Unit)

class BrandingSpec<in C>(internal val body: BrandingBuilder<C>.() -> Unit)

class FontSpec<in C>(internal val body: FontBuilder<C>.() -> Unit)

class StylesSpec<in C>(internal val body: StylesBuilder<C>.() -> Unit)

class ElementStyleSpec<in C>(internal val body: ElementStyleBuilder<C>.() -> Unit)

class RelationshipStyleSpec<in C>(internal val body: RelationshipStyleBuilder<C>.() -> Unit)

class TerminologySpec<in C>(internal val body: TerminologyBuilder<C>.() -> Unit)
