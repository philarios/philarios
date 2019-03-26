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
