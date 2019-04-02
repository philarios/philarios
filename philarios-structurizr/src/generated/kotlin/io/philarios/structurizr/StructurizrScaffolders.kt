package io.philarios.structurizr

import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder

class WorkspaceScaffolder<in C>(internal val spec: WorkspaceSpec<C>) : Scaffolder<C, Workspace> {
    override fun createScaffold(context: C): Scaffold<Workspace> {
        val builder = WorkspaceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ModelScaffolder<in C>(internal val spec: ModelSpec<C>) : Scaffolder<C, Model> {
    override fun createScaffold(context: C): Scaffold<Model> {
        val builder = ModelShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class PersonScaffolder<in C>(internal val spec: PersonSpec<C>) : Scaffolder<C, Person> {
    override fun createScaffold(context: C): Scaffold<Person> {
        val builder = PersonShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class SoftwareSystemScaffolder<in C>(internal val spec: SoftwareSystemSpec<C>) : Scaffolder<C, SoftwareSystem> {
    override fun createScaffold(context: C): Scaffold<SoftwareSystem> {
        val builder = SoftwareSystemShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ContainerScaffolder<in C>(internal val spec: ContainerSpec<C>) : Scaffolder<C, Container> {
    override fun createScaffold(context: C): Scaffold<Container> {
        val builder = ContainerShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ComponentScaffolder<in C>(internal val spec: ComponentSpec<C>) : Scaffolder<C, Component> {
    override fun createScaffold(context: C): Scaffold<Component> {
        val builder = ComponentShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class RelationshipScaffolder<in C>(internal val spec: RelationshipSpec<C>) : Scaffolder<C, Relationship> {
    override fun createScaffold(context: C): Scaffold<Relationship> {
        val builder = RelationshipShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ViewSetScaffolder<in C>(internal val spec: ViewSetSpec<C>) : Scaffolder<C, ViewSet> {
    override fun createScaffold(context: C): Scaffold<ViewSet> {
        val builder = ViewSetShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class SystemLandscapeViewScaffolder<in C>(internal val spec: SystemLandscapeViewSpec<C>) : Scaffolder<C, SystemLandscapeView> {
    override fun createScaffold(context: C): Scaffold<SystemLandscapeView> {
        val builder = SystemLandscapeViewShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class SystemContextViewScaffolder<in C>(internal val spec: SystemContextViewSpec<C>) : Scaffolder<C, SystemContextView> {
    override fun createScaffold(context: C): Scaffold<SystemContextView> {
        val builder = SystemContextViewShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ContainerViewScaffolder<in C>(internal val spec: ContainerViewSpec<C>) : Scaffolder<C, ContainerView> {
    override fun createScaffold(context: C): Scaffold<ContainerView> {
        val builder = ContainerViewShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ComponentViewScaffolder<in C>(internal val spec: ComponentViewSpec<C>) : Scaffolder<C, ComponentView> {
    override fun createScaffold(context: C): Scaffold<ComponentView> {
        val builder = ComponentViewShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class DynamicViewScaffolder<in C>(internal val spec: DynamicViewSpec<C>) : Scaffolder<C, DynamicView> {
    override fun createScaffold(context: C): Scaffold<DynamicView> {
        val builder = DynamicViewShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ConfigurationScaffolder<in C>(internal val spec: ConfigurationSpec<C>) : Scaffolder<C, Configuration> {
    override fun createScaffold(context: C): Scaffold<Configuration> {
        val builder = ConfigurationShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class BrandingScaffolder<in C>(internal val spec: BrandingSpec<C>) : Scaffolder<C, Branding> {
    override fun createScaffold(context: C): Scaffold<Branding> {
        val builder = BrandingShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class FontScaffolder<in C>(internal val spec: FontSpec<C>) : Scaffolder<C, Font> {
    override fun createScaffold(context: C): Scaffold<Font> {
        val builder = FontShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class StylesScaffolder<in C>(internal val spec: StylesSpec<C>) : Scaffolder<C, Styles> {
    override fun createScaffold(context: C): Scaffold<Styles> {
        val builder = StylesShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ElementStyleScaffolder<in C>(internal val spec: ElementStyleSpec<C>) : Scaffolder<C, ElementStyle> {
    override fun createScaffold(context: C): Scaffold<ElementStyle> {
        val builder = ElementStyleShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class RelationshipStyleScaffolder<in C>(internal val spec: RelationshipStyleSpec<C>) : Scaffolder<C, RelationshipStyle> {
    override fun createScaffold(context: C): Scaffold<RelationshipStyle> {
        val builder = RelationshipStyleShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TerminologyScaffolder<in C>(internal val spec: TerminologySpec<C>) : Scaffolder<C, Terminology> {
    override fun createScaffold(context: C): Scaffold<Terminology> {
        val builder = TerminologyShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
