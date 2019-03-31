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
