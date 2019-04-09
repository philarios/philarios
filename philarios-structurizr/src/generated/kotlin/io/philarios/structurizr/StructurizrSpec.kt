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

import io.philarios.core.DslBuilder
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

class WorkspaceSpec<in C>(internal val body: WorkspaceBuilder<C>.() -> Unit)

@DslBuilder
interface WorkspaceBuilder<out C> {
    val context: C

    fun name(value: String)

    fun description(value: String)

    fun model(body: ModelBuilder<C>.() -> Unit)

    fun model(spec: ModelSpec<C>)

    fun model(ref: ModelRef)

    fun model(value: Model)

    fun viewSet(body: ViewSetBuilder<C>.() -> Unit)

    fun viewSet(spec: ViewSetSpec<C>)

    fun viewSet(ref: ViewSetRef)

    fun viewSet(value: ViewSet)

    fun configuration(body: WorkspaceConfigurationBuilder<C>.() -> Unit)

    fun configuration(spec: WorkspaceConfigurationSpec<C>)

    fun configuration(ref: WorkspaceConfigurationRef)

    fun configuration(value: WorkspaceConfiguration)

    fun documentation(body: DocumentationBuilder<C>.() -> Unit)

    fun documentation(spec: DocumentationSpec<C>)

    fun documentation(ref: DocumentationRef)

    fun documentation(value: Documentation)

    fun include(body: WorkspaceBuilder<C>.() -> Unit)

    fun include(spec: WorkspaceSpec<C>)

    fun <C2> include(context: C2, body: WorkspaceBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkspaceSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkspaceBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkspaceSpec<C2>)
}

class WorkspaceRef(internal val key: String)

class ModelSpec<in C>(internal val body: ModelBuilder<C>.() -> Unit)

@DslBuilder
interface ModelBuilder<out C> {
    val context: C

    fun person(body: PersonBuilder<C>.() -> Unit)

    fun person(spec: PersonSpec<C>)

    fun person(ref: PersonRef)

    fun person(value: Person)

    fun people(people: List<Person>)

    fun softwareSystem(body: SoftwareSystemBuilder<C>.() -> Unit)

    fun softwareSystem(spec: SoftwareSystemSpec<C>)

    fun softwareSystem(ref: SoftwareSystemRef)

    fun softwareSystem(value: SoftwareSystem)

    fun softwareSystems(softwareSystems: List<SoftwareSystem>)

    fun include(body: ModelBuilder<C>.() -> Unit)

    fun include(spec: ModelSpec<C>)

    fun <C2> include(context: C2, body: ModelBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ModelSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ModelBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ModelSpec<C2>)
}

class ModelRef(internal val key: String)

class PersonSpec<in C>(internal val body: PersonBuilder<C>.() -> Unit)

@DslBuilder
interface PersonBuilder<out C> {
    val context: C

    fun id(value: String)

    fun name(value: String)

    fun description(value: String)

    fun location(value: Location)

    fun relationship(body: RelationshipBuilder<C>.() -> Unit)

    fun relationship(spec: RelationshipSpec<C>)

    fun relationship(ref: RelationshipRef)

    fun relationship(value: Relationship)

    fun relationships(relationships: List<Relationship>)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun include(body: PersonBuilder<C>.() -> Unit)

    fun include(spec: PersonSpec<C>)

    fun <C2> include(context: C2, body: PersonBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: PersonSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: PersonBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: PersonSpec<C2>)
}

class PersonRef(internal val key: String)

class SoftwareSystemSpec<in C>(internal val body: SoftwareSystemBuilder<C>.() -> Unit)

@DslBuilder
interface SoftwareSystemBuilder<out C> {
    val context: C

    fun id(value: String)

    fun name(value: String)

    fun description(value: String)

    fun location(value: Location)

    fun container(body: ContainerBuilder<C>.() -> Unit)

    fun container(spec: ContainerSpec<C>)

    fun container(ref: ContainerRef)

    fun container(value: Container)

    fun containers(containers: List<Container>)

    fun relationship(body: RelationshipBuilder<C>.() -> Unit)

    fun relationship(spec: RelationshipSpec<C>)

    fun relationship(ref: RelationshipRef)

    fun relationship(value: Relationship)

    fun relationships(relationships: List<Relationship>)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun include(body: SoftwareSystemBuilder<C>.() -> Unit)

    fun include(spec: SoftwareSystemSpec<C>)

    fun <C2> include(context: C2, body: SoftwareSystemBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: SoftwareSystemSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: SoftwareSystemBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: SoftwareSystemSpec<C2>)
}

class SoftwareSystemRef(internal val key: String)

class ContainerSpec<in C>(internal val body: ContainerBuilder<C>.() -> Unit)

@DslBuilder
interface ContainerBuilder<out C> {
    val context: C

    fun id(value: String)

    fun name(value: String)

    fun description(value: String)

    fun technology(value: String)

    fun technologies(technologies: List<String>)

    fun component(body: ComponentBuilder<C>.() -> Unit)

    fun component(spec: ComponentSpec<C>)

    fun component(ref: ComponentRef)

    fun component(value: Component)

    fun components(components: List<Component>)

    fun relationship(body: RelationshipBuilder<C>.() -> Unit)

    fun relationship(spec: RelationshipSpec<C>)

    fun relationship(ref: RelationshipRef)

    fun relationship(value: Relationship)

    fun relationships(relationships: List<Relationship>)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun include(body: ContainerBuilder<C>.() -> Unit)

    fun include(spec: ContainerSpec<C>)

    fun <C2> include(context: C2, body: ContainerBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ContainerSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ContainerBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ContainerSpec<C2>)
}

class ContainerRef(internal val key: String)

class ComponentSpec<in C>(internal val body: ComponentBuilder<C>.() -> Unit)

@DslBuilder
interface ComponentBuilder<out C> {
    val context: C

    fun id(value: String)

    fun name(value: String)

    fun description(value: String)

    fun technology(value: String)

    fun technologies(technologies: List<String>)

    fun relationship(body: RelationshipBuilder<C>.() -> Unit)

    fun relationship(spec: RelationshipSpec<C>)

    fun relationship(ref: RelationshipRef)

    fun relationship(value: Relationship)

    fun relationships(relationships: List<Relationship>)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun include(body: ComponentBuilder<C>.() -> Unit)

    fun include(spec: ComponentSpec<C>)

    fun <C2> include(context: C2, body: ComponentBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ComponentSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ComponentBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ComponentSpec<C2>)
}

class ComponentRef(internal val key: String)

class RelationshipSpec<in C>(internal val body: RelationshipBuilder<C>.() -> Unit)

@DslBuilder
interface RelationshipBuilder<out C> {
    val context: C

    fun destinationId(value: String)

    fun description(value: String)

    fun technology(value: String)

    fun technologies(technologies: List<String>)

    fun interactionStyle(value: InteractionStyle)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun include(body: RelationshipBuilder<C>.() -> Unit)

    fun include(spec: RelationshipSpec<C>)

    fun <C2> include(context: C2, body: RelationshipBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: RelationshipSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: RelationshipBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: RelationshipSpec<C2>)
}

class RelationshipRef(internal val key: String)

class ViewSetSpec<in C>(internal val body: ViewSetBuilder<C>.() -> Unit)

@DslBuilder
interface ViewSetBuilder<out C> {
    val context: C

    fun systemLandscapeView(body: SystemLandscapeViewBuilder<C>.() -> Unit)

    fun systemLandscapeView(spec: SystemLandscapeViewSpec<C>)

    fun systemLandscapeView(ref: SystemLandscapeViewRef)

    fun systemLandscapeView(value: SystemLandscapeView)

    fun systemLandscapeViews(systemLandscapeViews: List<SystemLandscapeView>)

    fun systemContextView(body: SystemContextViewBuilder<C>.() -> Unit)

    fun systemContextView(spec: SystemContextViewSpec<C>)

    fun systemContextView(ref: SystemContextViewRef)

    fun systemContextView(value: SystemContextView)

    fun systemContextViews(systemContextViews: List<SystemContextView>)

    fun containerView(body: ContainerViewBuilder<C>.() -> Unit)

    fun containerView(spec: ContainerViewSpec<C>)

    fun containerView(ref: ContainerViewRef)

    fun containerView(value: ContainerView)

    fun containerViews(containerViews: List<ContainerView>)

    fun componentView(body: ComponentViewBuilder<C>.() -> Unit)

    fun componentView(spec: ComponentViewSpec<C>)

    fun componentView(ref: ComponentViewRef)

    fun componentView(value: ComponentView)

    fun componentViews(componentViews: List<ComponentView>)

    fun dynamicView(body: DynamicViewBuilder<C>.() -> Unit)

    fun dynamicView(spec: DynamicViewSpec<C>)

    fun dynamicView(ref: DynamicViewRef)

    fun dynamicView(value: DynamicView)

    fun dynamicViews(dynamicViews: List<DynamicView>)

    fun configuration(body: ConfigurationBuilder<C>.() -> Unit)

    fun configuration(spec: ConfigurationSpec<C>)

    fun configuration(ref: ConfigurationRef)

    fun configuration(value: Configuration)

    fun include(body: ViewSetBuilder<C>.() -> Unit)

    fun include(spec: ViewSetSpec<C>)

    fun <C2> include(context: C2, body: ViewSetBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ViewSetSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ViewSetBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ViewSetSpec<C2>)
}

class ViewSetRef(internal val key: String)

class SystemLandscapeViewSpec<in C>(internal val body: SystemLandscapeViewBuilder<C>.() -> Unit)

@DslBuilder
interface SystemLandscapeViewBuilder<out C> {
    val context: C

    fun key(value: String)

    fun description(value: String)

    fun title(value: String)

    fun paperSize(value: PaperSize)

    fun softwareSystem(value: String)

    fun softwareSystems(softwareSystems: List<String>)

    fun person(value: String)

    fun people(people: List<String>)

    fun include(body: SystemLandscapeViewBuilder<C>.() -> Unit)

    fun include(spec: SystemLandscapeViewSpec<C>)

    fun <C2> include(context: C2, body: SystemLandscapeViewBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: SystemLandscapeViewSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: SystemLandscapeViewBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: SystemLandscapeViewSpec<C2>)
}

class SystemLandscapeViewRef(internal val key: String)

class SystemContextViewSpec<in C>(internal val body: SystemContextViewBuilder<C>.() -> Unit)

@DslBuilder
interface SystemContextViewBuilder<out C> {
    val context: C

    fun softwareSystemId(value: String)

    fun key(value: String)

    fun description(value: String)

    fun title(value: String)

    fun paperSize(value: PaperSize)

    fun softwareSystem(value: String)

    fun softwareSystems(softwareSystems: List<String>)

    fun person(value: String)

    fun people(people: List<String>)

    fun include(body: SystemContextViewBuilder<C>.() -> Unit)

    fun include(spec: SystemContextViewSpec<C>)

    fun <C2> include(context: C2, body: SystemContextViewBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: SystemContextViewSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: SystemContextViewBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: SystemContextViewSpec<C2>)
}

class SystemContextViewRef(internal val key: String)

class ContainerViewSpec<in C>(internal val body: ContainerViewBuilder<C>.() -> Unit)

@DslBuilder
interface ContainerViewBuilder<out C> {
    val context: C

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

    fun include(body: ContainerViewBuilder<C>.() -> Unit)

    fun include(spec: ContainerViewSpec<C>)

    fun <C2> include(context: C2, body: ContainerViewBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ContainerViewSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ContainerViewBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ContainerViewSpec<C2>)
}

class ContainerViewRef(internal val key: String)

class ComponentViewSpec<in C>(internal val body: ComponentViewBuilder<C>.() -> Unit)

@DslBuilder
interface ComponentViewBuilder<out C> {
    val context: C

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

    fun include(body: ComponentViewBuilder<C>.() -> Unit)

    fun include(spec: ComponentViewSpec<C>)

    fun <C2> include(context: C2, body: ComponentViewBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ComponentViewSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ComponentViewBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ComponentViewSpec<C2>)
}

class ComponentViewRef(internal val key: String)

class DynamicViewSpec<in C>(internal val body: DynamicViewBuilder<C>.() -> Unit)

@DslBuilder
interface DynamicViewBuilder<out C> {
    val context: C

    fun key(value: String)

    fun description(value: String)

    fun title(value: String)

    fun paperSize(value: PaperSize)

    fun include(body: DynamicViewBuilder<C>.() -> Unit)

    fun include(spec: DynamicViewSpec<C>)

    fun <C2> include(context: C2, body: DynamicViewBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: DynamicViewSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: DynamicViewBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: DynamicViewSpec<C2>)
}

class DynamicViewRef(internal val key: String)

class ConfigurationSpec<in C>(internal val body: ConfigurationBuilder<C>.() -> Unit)

@DslBuilder
interface ConfigurationBuilder<out C> {
    val context: C

    fun branding(body: BrandingBuilder<C>.() -> Unit)

    fun branding(spec: BrandingSpec<C>)

    fun branding(ref: BrandingRef)

    fun branding(value: Branding)

    fun styles(body: StylesBuilder<C>.() -> Unit)

    fun styles(spec: StylesSpec<C>)

    fun styles(ref: StylesRef)

    fun styles(value: Styles)

    fun terminology(body: TerminologyBuilder<C>.() -> Unit)

    fun terminology(spec: TerminologySpec<C>)

    fun terminology(ref: TerminologyRef)

    fun terminology(value: Terminology)

    fun viewSortOrder(value: ViewSortOrder)

    fun include(body: ConfigurationBuilder<C>.() -> Unit)

    fun include(spec: ConfigurationSpec<C>)

    fun <C2> include(context: C2, body: ConfigurationBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ConfigurationSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ConfigurationBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ConfigurationSpec<C2>)
}

class ConfigurationRef(internal val key: String)

class BrandingSpec<in C>(internal val body: BrandingBuilder<C>.() -> Unit)

@DslBuilder
interface BrandingBuilder<out C> {
    val context: C

    fun logo(value: String)

    fun font(body: FontBuilder<C>.() -> Unit)

    fun font(spec: FontSpec<C>)

    fun font(ref: FontRef)

    fun font(value: Font)

    fun include(body: BrandingBuilder<C>.() -> Unit)

    fun include(spec: BrandingSpec<C>)

    fun <C2> include(context: C2, body: BrandingBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: BrandingSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: BrandingBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: BrandingSpec<C2>)
}

class BrandingRef(internal val key: String)

class FontSpec<in C>(internal val body: FontBuilder<C>.() -> Unit)

@DslBuilder
interface FontBuilder<out C> {
    val context: C

    fun name(value: String)

    fun url(value: String)

    fun include(body: FontBuilder<C>.() -> Unit)

    fun include(spec: FontSpec<C>)

    fun <C2> include(context: C2, body: FontBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: FontSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: FontBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: FontSpec<C2>)
}

class FontRef(internal val key: String)

class StylesSpec<in C>(internal val body: StylesBuilder<C>.() -> Unit)

@DslBuilder
interface StylesBuilder<out C> {
    val context: C

    fun element(body: ElementStyleBuilder<C>.() -> Unit)

    fun element(spec: ElementStyleSpec<C>)

    fun element(ref: ElementStyleRef)

    fun element(value: ElementStyle)

    fun elements(elements: List<ElementStyle>)

    fun relationship(body: RelationshipStyleBuilder<C>.() -> Unit)

    fun relationship(spec: RelationshipStyleSpec<C>)

    fun relationship(ref: RelationshipStyleRef)

    fun relationship(value: RelationshipStyle)

    fun relationships(relationships: List<RelationshipStyle>)

    fun include(body: StylesBuilder<C>.() -> Unit)

    fun include(spec: StylesSpec<C>)

    fun <C2> include(context: C2, body: StylesBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: StylesSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: StylesBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: StylesSpec<C2>)
}

class StylesRef(internal val key: String)

class ElementStyleSpec<in C>(internal val body: ElementStyleBuilder<C>.() -> Unit)

@DslBuilder
interface ElementStyleBuilder<out C> {
    val context: C

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

    fun include(body: ElementStyleBuilder<C>.() -> Unit)

    fun include(spec: ElementStyleSpec<C>)

    fun <C2> include(context: C2, body: ElementStyleBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ElementStyleSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ElementStyleBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ElementStyleSpec<C2>)
}

class ElementStyleRef(internal val key: String)

class RelationshipStyleSpec<in C>(internal val body: RelationshipStyleBuilder<C>.() -> Unit)

@DslBuilder
interface RelationshipStyleBuilder<out C> {
    val context: C

    fun tag(value: String)

    fun thickness(value: Int)

    fun color(value: String)

    fun fontSize(value: Int)

    fun width(value: Int)

    fun dashed(value: Boolean)

    fun routing(value: Routing)

    fun position(value: Int)

    fun opacity(value: Int)

    fun include(body: RelationshipStyleBuilder<C>.() -> Unit)

    fun include(spec: RelationshipStyleSpec<C>)

    fun <C2> include(context: C2, body: RelationshipStyleBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: RelationshipStyleSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: RelationshipStyleBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: RelationshipStyleSpec<C2>)
}

class RelationshipStyleRef(internal val key: String)

class TerminologySpec<in C>(internal val body: TerminologyBuilder<C>.() -> Unit)

@DslBuilder
interface TerminologyBuilder<out C> {
    val context: C

    fun enterprise(value: String)

    fun person(value: String)

    fun softwareSystem(value: String)

    fun container(value: String)

    fun component(value: String)

    fun code(value: String)

    fun deploymentNode(value: String)

    fun relationship(value: String)

    fun include(body: TerminologyBuilder<C>.() -> Unit)

    fun include(spec: TerminologySpec<C>)

    fun <C2> include(context: C2, body: TerminologyBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TerminologySpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TerminologyBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TerminologySpec<C2>)
}

class TerminologyRef(internal val key: String)

class WorkspaceConfigurationSpec<in C>(internal val body: WorkspaceConfigurationBuilder<C>.() -> Unit)

@DslBuilder
interface WorkspaceConfigurationBuilder<out C> {
    val context: C

    fun user(body: UserBuilder<C>.() -> Unit)

    fun user(spec: UserSpec<C>)

    fun user(ref: UserRef)

    fun user(value: User)

    fun users(users: List<User>)

    fun include(body: WorkspaceConfigurationBuilder<C>.() -> Unit)

    fun include(spec: WorkspaceConfigurationSpec<C>)

    fun <C2> include(context: C2, body: WorkspaceConfigurationBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkspaceConfigurationSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkspaceConfigurationBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkspaceConfigurationSpec<C2>)
}

class WorkspaceConfigurationRef(internal val key: String)

class UserSpec<in C>(internal val body: UserBuilder<C>.() -> Unit)

@DslBuilder
interface UserBuilder<out C> {
    val context: C

    fun username(value: String)

    fun role(value: Role)

    fun include(body: UserBuilder<C>.() -> Unit)

    fun include(spec: UserSpec<C>)

    fun <C2> include(context: C2, body: UserBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: UserSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: UserBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: UserSpec<C2>)
}

class UserRef(internal val key: String)

class DocumentationSpec<in C>(internal val body: DocumentationBuilder<C>.() -> Unit)

@DslBuilder
interface DocumentationBuilder<out C> {
    val context: C

    fun decision(body: DecisionBuilder<C>.() -> Unit)

    fun decision(spec: DecisionSpec<C>)

    fun decision(ref: DecisionRef)

    fun decision(value: Decision)

    fun decisions(decisions: List<Decision>)

    fun include(body: DocumentationBuilder<C>.() -> Unit)

    fun include(spec: DocumentationSpec<C>)

    fun <C2> include(context: C2, body: DocumentationBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: DocumentationSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: DocumentationBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: DocumentationSpec<C2>)
}

class DocumentationRef(internal val key: String)

class DecisionSpec<in C>(internal val body: DecisionBuilder<C>.() -> Unit)

@DslBuilder
interface DecisionBuilder<out C> {
    val context: C

    fun elementId(value: String)

    fun id(value: String)

    fun date(value: String)

    fun title(value: String)

    fun status(value: DecisionStatus)

    fun content(value: String)

    fun format(value: Format)

    fun include(body: DecisionBuilder<C>.() -> Unit)

    fun include(spec: DecisionSpec<C>)

    fun <C2> include(context: C2, body: DecisionBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: DecisionSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: DecisionBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: DecisionSpec<C2>)
}

class DecisionRef(internal val key: String)
