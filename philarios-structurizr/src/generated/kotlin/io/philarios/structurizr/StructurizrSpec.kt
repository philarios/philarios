// The builder interfaces needed to create type-safe specs.
//
// The specs and builders are located one layer below the model. While they need to reference the model classes
// for obvious reasons, they should still be as un-opinionated as possible and should not depend on any
// implementation details. This allows you to write specs without depending on how the specs are actually
// materialized.
//
// It is inevitable to take an at least somewhat opinionated approach while designing interfaces and some of the
// decisions reflect this. However, since comments or concerns are always welcome, please feel free to open an
// issue in the project's repository.
package io.philarios.structurizr

import io.philarios.core.Builder
import io.philarios.core.DslBuilder
import io.philarios.core.Spec
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List

class WorkspaceSpec(override val body: WorkspaceBuilder.() -> Unit) : Spec<WorkspaceBuilder>

@DslBuilder
interface WorkspaceBuilder : Builder<WorkspaceSpec, WorkspaceBuilder> {
    fun name(value: String)

    fun description(value: String)

    fun model(body: ModelBuilder.() -> Unit)

    fun model(spec: ModelSpec)

    fun model(ref: ModelRef)

    fun model(value: Model)

    fun viewSet(body: ViewSetBuilder.() -> Unit)

    fun viewSet(spec: ViewSetSpec)

    fun viewSet(ref: ViewSetRef)

    fun viewSet(value: ViewSet)

    fun configuration(body: WorkspaceConfigurationBuilder.() -> Unit)

    fun configuration(spec: WorkspaceConfigurationSpec)

    fun configuration(ref: WorkspaceConfigurationRef)

    fun configuration(value: WorkspaceConfiguration)

    fun documentation(body: DocumentationBuilder.() -> Unit)

    fun documentation(spec: DocumentationSpec)

    fun documentation(ref: DocumentationRef)

    fun documentation(value: Documentation)
}

class WorkspaceRef(internal val key: String)

class ModelSpec(override val body: ModelBuilder.() -> Unit) : Spec<ModelBuilder>

@DslBuilder
interface ModelBuilder : Builder<ModelSpec, ModelBuilder> {
    fun person(body: PersonBuilder.() -> Unit)

    fun person(spec: PersonSpec)

    fun person(ref: PersonRef)

    fun person(value: Person)

    fun people(people: List<Person>)

    fun softwareSystem(body: SoftwareSystemBuilder.() -> Unit)

    fun softwareSystem(spec: SoftwareSystemSpec)

    fun softwareSystem(ref: SoftwareSystemRef)

    fun softwareSystem(value: SoftwareSystem)

    fun softwareSystems(softwareSystems: List<SoftwareSystem>)
}

class ModelRef(internal val key: String)

class PersonSpec(override val body: PersonBuilder.() -> Unit) : Spec<PersonBuilder>

@DslBuilder
interface PersonBuilder : Builder<PersonSpec, PersonBuilder> {
    fun id(value: String)

    fun name(value: String)

    fun description(value: String)

    fun location(value: Location)

    fun relationship(body: RelationshipBuilder.() -> Unit)

    fun relationship(spec: RelationshipSpec)

    fun relationship(ref: RelationshipRef)

    fun relationship(value: Relationship)

    fun relationships(relationships: List<Relationship>)

    fun tag(value: String)

    fun tags(tags: List<String>)
}

class PersonRef(internal val key: String)

class SoftwareSystemSpec(override val body: SoftwareSystemBuilder.() -> Unit) : Spec<SoftwareSystemBuilder>

@DslBuilder
interface SoftwareSystemBuilder : Builder<SoftwareSystemSpec, SoftwareSystemBuilder> {
    fun id(value: String)

    fun name(value: String)

    fun description(value: String)

    fun location(value: Location)

    fun container(body: ContainerBuilder.() -> Unit)

    fun container(spec: ContainerSpec)

    fun container(ref: ContainerRef)

    fun container(value: Container)

    fun containers(containers: List<Container>)

    fun relationship(body: RelationshipBuilder.() -> Unit)

    fun relationship(spec: RelationshipSpec)

    fun relationship(ref: RelationshipRef)

    fun relationship(value: Relationship)

    fun relationships(relationships: List<Relationship>)

    fun tag(value: String)

    fun tags(tags: List<String>)
}

class SoftwareSystemRef(internal val key: String)

class ContainerSpec(override val body: ContainerBuilder.() -> Unit) : Spec<ContainerBuilder>

@DslBuilder
interface ContainerBuilder : Builder<ContainerSpec, ContainerBuilder> {
    fun id(value: String)

    fun name(value: String)

    fun description(value: String)

    fun technology(value: String)

    fun technologies(technologies: List<String>)

    fun component(body: ComponentBuilder.() -> Unit)

    fun component(spec: ComponentSpec)

    fun component(ref: ComponentRef)

    fun component(value: Component)

    fun components(components: List<Component>)

    fun relationship(body: RelationshipBuilder.() -> Unit)

    fun relationship(spec: RelationshipSpec)

    fun relationship(ref: RelationshipRef)

    fun relationship(value: Relationship)

    fun relationships(relationships: List<Relationship>)

    fun tag(value: String)

    fun tags(tags: List<String>)
}

class ContainerRef(internal val key: String)

class ComponentSpec(override val body: ComponentBuilder.() -> Unit) : Spec<ComponentBuilder>

@DslBuilder
interface ComponentBuilder : Builder<ComponentSpec, ComponentBuilder> {
    fun id(value: String)

    fun name(value: String)

    fun description(value: String)

    fun technology(value: String)

    fun technologies(technologies: List<String>)

    fun relationship(body: RelationshipBuilder.() -> Unit)

    fun relationship(spec: RelationshipSpec)

    fun relationship(ref: RelationshipRef)

    fun relationship(value: Relationship)

    fun relationships(relationships: List<Relationship>)

    fun tag(value: String)

    fun tags(tags: List<String>)
}

class ComponentRef(internal val key: String)

class RelationshipSpec(override val body: RelationshipBuilder.() -> Unit) : Spec<RelationshipBuilder>

@DslBuilder
interface RelationshipBuilder : Builder<RelationshipSpec, RelationshipBuilder> {
    fun sourceId(value: String)

    fun destinationId(value: String)

    fun description(value: String)

    fun technology(value: String)

    fun technologies(technologies: List<String>)

    fun interactionStyle(value: InteractionStyle)

    fun tag(value: String)

    fun tags(tags: List<String>)
}

class RelationshipRef(internal val key: String)

class ViewSetSpec(override val body: ViewSetBuilder.() -> Unit) : Spec<ViewSetBuilder>

@DslBuilder
interface ViewSetBuilder : Builder<ViewSetSpec, ViewSetBuilder> {
    fun systemLandscapeView(body: SystemLandscapeViewBuilder.() -> Unit)

    fun systemLandscapeView(spec: SystemLandscapeViewSpec)

    fun systemLandscapeView(ref: SystemLandscapeViewRef)

    fun systemLandscapeView(value: SystemLandscapeView)

    fun systemLandscapeViews(systemLandscapeViews: List<SystemLandscapeView>)

    fun systemContextView(body: SystemContextViewBuilder.() -> Unit)

    fun systemContextView(spec: SystemContextViewSpec)

    fun systemContextView(ref: SystemContextViewRef)

    fun systemContextView(value: SystemContextView)

    fun systemContextViews(systemContextViews: List<SystemContextView>)

    fun containerView(body: ContainerViewBuilder.() -> Unit)

    fun containerView(spec: ContainerViewSpec)

    fun containerView(ref: ContainerViewRef)

    fun containerView(value: ContainerView)

    fun containerViews(containerViews: List<ContainerView>)

    fun componentView(body: ComponentViewBuilder.() -> Unit)

    fun componentView(spec: ComponentViewSpec)

    fun componentView(ref: ComponentViewRef)

    fun componentView(value: ComponentView)

    fun componentViews(componentViews: List<ComponentView>)

    fun dynamicView(body: DynamicViewBuilder.() -> Unit)

    fun dynamicView(spec: DynamicViewSpec)

    fun dynamicView(ref: DynamicViewRef)

    fun dynamicView(value: DynamicView)

    fun dynamicViews(dynamicViews: List<DynamicView>)

    fun configuration(body: ConfigurationBuilder.() -> Unit)

    fun configuration(spec: ConfigurationSpec)

    fun configuration(ref: ConfigurationRef)

    fun configuration(value: Configuration)
}

class ViewSetRef(internal val key: String)

class SystemLandscapeViewSpec(override val body: SystemLandscapeViewBuilder.() -> Unit) : Spec<SystemLandscapeViewBuilder>

@DslBuilder
interface SystemLandscapeViewBuilder : Builder<SystemLandscapeViewSpec, SystemLandscapeViewBuilder> {
    fun key(value: String)

    fun description(value: String)

    fun title(value: String)

    fun paperSize(value: PaperSize)

    fun softwareSystem(value: String)

    fun softwareSystems(softwareSystems: List<String>)

    fun person(value: String)

    fun people(people: List<String>)
}

class SystemLandscapeViewRef(internal val key: String)

class SystemContextViewSpec(override val body: SystemContextViewBuilder.() -> Unit) : Spec<SystemContextViewBuilder>

@DslBuilder
interface SystemContextViewBuilder : Builder<SystemContextViewSpec, SystemContextViewBuilder> {
    fun softwareSystemId(value: String)

    fun key(value: String)

    fun description(value: String)

    fun title(value: String)

    fun paperSize(value: PaperSize)

    fun softwareSystem(value: String)

    fun softwareSystems(softwareSystems: List<String>)

    fun person(value: String)

    fun people(people: List<String>)
}

class SystemContextViewRef(internal val key: String)

class ContainerViewSpec(override val body: ContainerViewBuilder.() -> Unit) : Spec<ContainerViewBuilder>

@DslBuilder
interface ContainerViewBuilder : Builder<ContainerViewSpec, ContainerViewBuilder> {
    fun softwareSystemId(value: String)

    fun key(value: String)

    fun description(value: String)

    fun title(value: String)

    fun paperSize(value: PaperSize)

    fun softwareSystem(value: String)

    fun softwareSystems(softwareSystems: List<String>)

    fun person(value: String)

    fun people(people: List<String>)

    fun container(value: String)

    fun containers(containers: List<String>)
}

class ContainerViewRef(internal val key: String)

class ComponentViewSpec(override val body: ComponentViewBuilder.() -> Unit) : Spec<ComponentViewBuilder>

@DslBuilder
interface ComponentViewBuilder : Builder<ComponentViewSpec, ComponentViewBuilder> {
    fun containerId(value: String)

    fun key(value: String)

    fun description(value: String)

    fun title(value: String)

    fun paperSize(value: PaperSize)

    fun softwareSystem(value: String)

    fun softwareSystems(softwareSystems: List<String>)

    fun person(value: String)

    fun people(people: List<String>)

    fun container(value: String)

    fun containers(containers: List<String>)

    fun component(value: String)

    fun components(components: List<String>)
}

class ComponentViewRef(internal val key: String)

class DynamicViewSpec(override val body: DynamicViewBuilder.() -> Unit) : Spec<DynamicViewBuilder>

@DslBuilder
interface DynamicViewBuilder : Builder<DynamicViewSpec, DynamicViewBuilder> {
    fun key(value: String)

    fun description(value: String)

    fun title(value: String)

    fun paperSize(value: PaperSize)
}

class DynamicViewRef(internal val key: String)

class ConfigurationSpec(override val body: ConfigurationBuilder.() -> Unit) : Spec<ConfigurationBuilder>

@DslBuilder
interface ConfigurationBuilder : Builder<ConfigurationSpec, ConfigurationBuilder> {
    fun branding(body: BrandingBuilder.() -> Unit)

    fun branding(spec: BrandingSpec)

    fun branding(ref: BrandingRef)

    fun branding(value: Branding)

    fun styles(body: StylesBuilder.() -> Unit)

    fun styles(spec: StylesSpec)

    fun styles(ref: StylesRef)

    fun styles(value: Styles)

    fun terminology(body: TerminologyBuilder.() -> Unit)

    fun terminology(spec: TerminologySpec)

    fun terminology(ref: TerminologyRef)

    fun terminology(value: Terminology)

    fun viewSortOrder(value: ViewSortOrder)
}

class ConfigurationRef(internal val key: String)

class BrandingSpec(override val body: BrandingBuilder.() -> Unit) : Spec<BrandingBuilder>

@DslBuilder
interface BrandingBuilder : Builder<BrandingSpec, BrandingBuilder> {
    fun logo(value: String)

    fun font(body: FontBuilder.() -> Unit)

    fun font(spec: FontSpec)

    fun font(ref: FontRef)

    fun font(value: Font)
}

class BrandingRef(internal val key: String)

class FontSpec(override val body: FontBuilder.() -> Unit) : Spec<FontBuilder>

@DslBuilder
interface FontBuilder : Builder<FontSpec, FontBuilder> {
    fun name(value: String)

    fun url(value: String)
}

class FontRef(internal val key: String)

class StylesSpec(override val body: StylesBuilder.() -> Unit) : Spec<StylesBuilder>

@DslBuilder
interface StylesBuilder : Builder<StylesSpec, StylesBuilder> {
    fun element(body: ElementStyleBuilder.() -> Unit)

    fun element(spec: ElementStyleSpec)

    fun element(ref: ElementStyleRef)

    fun element(value: ElementStyle)

    fun elements(elements: List<ElementStyle>)

    fun relationship(body: RelationshipStyleBuilder.() -> Unit)

    fun relationship(spec: RelationshipStyleSpec)

    fun relationship(ref: RelationshipStyleRef)

    fun relationship(value: RelationshipStyle)

    fun relationships(relationships: List<RelationshipStyle>)
}

class StylesRef(internal val key: String)

class ElementStyleSpec(override val body: ElementStyleBuilder.() -> Unit) : Spec<ElementStyleBuilder>

@DslBuilder
interface ElementStyleBuilder : Builder<ElementStyleSpec, ElementStyleBuilder> {
    fun tag(value: String)

    fun width(value: Int)

    fun height(value: Int)

    fun background(value: String)

    fun color(value: String)

    fun fontSize(value: Int)

    fun shape(value: Shape)

    fun icon(value: String)

    fun border(value: Border)

    fun opacity(value: Int)

    fun metadata(value: Boolean)

    fun description(value: Boolean)
}

class ElementStyleRef(internal val key: String)

class RelationshipStyleSpec(override val body: RelationshipStyleBuilder.() -> Unit) : Spec<RelationshipStyleBuilder>

@DslBuilder
interface RelationshipStyleBuilder : Builder<RelationshipStyleSpec, RelationshipStyleBuilder> {
    fun tag(value: String)

    fun thickness(value: Int)

    fun color(value: String)

    fun fontSize(value: Int)

    fun width(value: Int)

    fun dashed(value: Boolean)

    fun routing(value: Routing)

    fun position(value: Int)

    fun opacity(value: Int)
}

class RelationshipStyleRef(internal val key: String)

class TerminologySpec(override val body: TerminologyBuilder.() -> Unit) : Spec<TerminologyBuilder>

@DslBuilder
interface TerminologyBuilder : Builder<TerminologySpec, TerminologyBuilder> {
    fun enterprise(value: String)

    fun person(value: String)

    fun softwareSystem(value: String)

    fun container(value: String)

    fun component(value: String)

    fun code(value: String)

    fun deploymentNode(value: String)

    fun relationship(value: String)
}

class TerminologyRef(internal val key: String)

class WorkspaceConfigurationSpec(override val body: WorkspaceConfigurationBuilder.() -> Unit) : Spec<WorkspaceConfigurationBuilder>

@DslBuilder
interface WorkspaceConfigurationBuilder : Builder<WorkspaceConfigurationSpec, WorkspaceConfigurationBuilder> {
    fun user(body: UserBuilder.() -> Unit)

    fun user(spec: UserSpec)

    fun user(ref: UserRef)

    fun user(value: User)

    fun users(users: List<User>)
}

class WorkspaceConfigurationRef(internal val key: String)

class UserSpec(override val body: UserBuilder.() -> Unit) : Spec<UserBuilder>

@DslBuilder
interface UserBuilder : Builder<UserSpec, UserBuilder> {
    fun username(value: String)

    fun role(value: Role)
}

class UserRef(internal val key: String)

class DocumentationSpec(override val body: DocumentationBuilder.() -> Unit) : Spec<DocumentationBuilder>

@DslBuilder
interface DocumentationBuilder : Builder<DocumentationSpec, DocumentationBuilder> {
    fun decision(body: DecisionBuilder.() -> Unit)

    fun decision(spec: DecisionSpec)

    fun decision(ref: DecisionRef)

    fun decision(value: Decision)

    fun decisions(decisions: List<Decision>)
}

class DocumentationRef(internal val key: String)

class DecisionSpec(override val body: DecisionBuilder.() -> Unit) : Spec<DecisionBuilder>

@DslBuilder
interface DecisionBuilder : Builder<DecisionSpec, DecisionBuilder> {
    fun elementId(value: String)

    fun id(value: String)

    fun date(value: String)

    fun title(value: String)

    fun status(value: DecisionStatus)

    fun content(value: String)

    fun format(value: Format)
}

class DecisionRef(internal val key: String)

fun workspace(body: WorkspaceBuilder.() -> Unit): WorkspaceSpec = WorkspaceSpec(body)

fun model(body: ModelBuilder.() -> Unit): ModelSpec = ModelSpec(body)

fun person(body: PersonBuilder.() -> Unit): PersonSpec = PersonSpec(body)

fun softwareSystem(body: SoftwareSystemBuilder.() -> Unit): SoftwareSystemSpec = SoftwareSystemSpec(body)

fun container(body: ContainerBuilder.() -> Unit): ContainerSpec = ContainerSpec(body)

fun component(body: ComponentBuilder.() -> Unit): ComponentSpec = ComponentSpec(body)

fun relationship(body: RelationshipBuilder.() -> Unit): RelationshipSpec = RelationshipSpec(body)

fun viewSet(body: ViewSetBuilder.() -> Unit): ViewSetSpec = ViewSetSpec(body)

fun systemLandscapeView(body: SystemLandscapeViewBuilder.() -> Unit): SystemLandscapeViewSpec = SystemLandscapeViewSpec(body)

fun systemContextView(body: SystemContextViewBuilder.() -> Unit): SystemContextViewSpec = SystemContextViewSpec(body)

fun containerView(body: ContainerViewBuilder.() -> Unit): ContainerViewSpec = ContainerViewSpec(body)

fun componentView(body: ComponentViewBuilder.() -> Unit): ComponentViewSpec = ComponentViewSpec(body)

fun dynamicView(body: DynamicViewBuilder.() -> Unit): DynamicViewSpec = DynamicViewSpec(body)

fun configuration(body: ConfigurationBuilder.() -> Unit): ConfigurationSpec = ConfigurationSpec(body)

fun branding(body: BrandingBuilder.() -> Unit): BrandingSpec = BrandingSpec(body)

fun font(body: FontBuilder.() -> Unit): FontSpec = FontSpec(body)

fun styles(body: StylesBuilder.() -> Unit): StylesSpec = StylesSpec(body)

fun elementStyle(body: ElementStyleBuilder.() -> Unit): ElementStyleSpec = ElementStyleSpec(body)

fun relationshipStyle(body: RelationshipStyleBuilder.() -> Unit): RelationshipStyleSpec = RelationshipStyleSpec(body)

fun terminology(body: TerminologyBuilder.() -> Unit): TerminologySpec = TerminologySpec(body)

fun workspaceConfiguration(body: WorkspaceConfigurationBuilder.() -> Unit): WorkspaceConfigurationSpec = WorkspaceConfigurationSpec(body)

fun user(body: UserBuilder.() -> Unit): UserSpec = UserSpec(body)

fun documentation(body: DocumentationBuilder.() -> Unit): DocumentationSpec = DocumentationSpec(body)

fun decision(body: DecisionBuilder.() -> Unit): DecisionSpec = DecisionSpec(body)
