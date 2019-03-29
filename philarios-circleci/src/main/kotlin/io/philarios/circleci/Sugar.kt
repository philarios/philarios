package io.philarios.circleci

fun <C> JobBuilder<C>.run(body: RunBuilder<C>.() -> Unit) {
    step(RunStepSpec {
        run(body)
    })
}

fun <C> JobBuilder<C>.run(command: String, body: RunBuilder<C>.() -> Unit = {}) {
    run {
        command(command)
        apply(body)
    }
}

fun <C> JobBuilder<C>.run(name: String, command: String, body: RunBuilder<C>.() -> Unit = {}) {
    run {
        name(name)
        command(command)
        apply(body)
    }
}

fun <C> JobBuilder<C>.runFromResource(name: String, resource: String, body: RunBuilder<C>.() -> Unit = {}) {
    run {
        name(name)
        command(readResource(resource))
        apply(body)
    }
}

fun readResource(resource: String) = ClassLoader.getSystemResourceAsStream(resource).bufferedReader().readText()

fun <C> JobBuilder<C>.checkout(body: CheckoutBuilder<C>.() -> Unit) {
    step(CheckoutStepSpec {
        checkout(body)
    })
}

fun <C> JobBuilder<C>.checkout() {
    checkout {
        path(".")
    }
}

fun <C> JobBuilder<C>.setup_remote_docker(body: SetupRemoteDockerBuilder<C>.() -> Unit) {
    step(SetupRemoteDockerStepSpec {
        setup_remote_docker(body)
    })
}

fun <C> JobBuilder<C>.save_cache(body: SaveCacheBuilder<C>.() -> Unit) {
    step(SaveCacheStepSpec {
        save_cache(body)
    })
}

fun <C> JobBuilder<C>.restore_cache(body: RestoreCacheBuilder<C>.() -> Unit) {
    step(RestoreCacheStepSpec {
        restore_cache(body)
    })
}

fun <C> JobBuilder<C>.deploy(body: RunBuilder<C>.() -> Unit) {
    step(DeployStepSpec {
        deploy(body)
    })
}

fun <C> JobBuilder<C>.store_artifacts(body: StoreArtifactsBuilder<C>.() -> Unit) {
    step(StoreArtifactsStepSpec {
        store_artifacts(body)
    })
}

fun <C> JobBuilder<C>.store_test_results(body: StoreTestResultsBuilder<C>.() -> Unit) {
    step(StoreTestResultsStepSpec {
        store_test_results(body)
    })
}

fun <C> JobBuilder<C>.persist_to_workspace(body: PersistToWorkspaceBuilder<C>.() -> Unit) {
    step(PersistToWorkspaceStepSpec {
        persist_to_workspace(body)
    })
}

fun <C> JobBuilder<C>.attach_workspace(body: AttachWorkspaceBuilder<C>.() -> Unit) {
    step(AttachWorkspaceStepSpec {
        attach_workspace(body)
    })
}

fun <C> JobBuilder<C>.add_ssh_keys(body: AddSshKeysBuilder<C>.() -> Unit) {
    step(AddSshKeysStepSpec {
        add_ssh_keys(body)
    })
}
