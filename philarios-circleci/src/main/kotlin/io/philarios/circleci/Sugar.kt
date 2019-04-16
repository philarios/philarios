package io.philarios.circleci

fun JobBuilder.run(body: RunBuilder.() -> Unit) {
    step(RunStepSpec {
        run(body)
    })
}

fun JobBuilder.run(command: String, body: RunBuilder.() -> Unit = {}) {
    run {
        command(command)
        apply(body)
    }
}

fun JobBuilder.run(name: String, command: String, body: RunBuilder.() -> Unit = {}) {
    run {
        name(name)
        command(command)
        apply(body)
    }
}

fun JobBuilder.runFromResource(name: String, resource: String, body: RunBuilder.() -> Unit = {}) {
    run {
        name(name)
        command(readResource(resource))
        apply(body)
    }
}

fun readResource(resource: String) = ClassLoader.getSystemResourceAsStream(resource).bufferedReader().readText()

fun JobBuilder.checkout(body: CheckoutBuilder.() -> Unit) {
    step(CheckoutStepSpec {
        checkout(body)
    })
}

fun JobBuilder.checkout() {
    checkout {
        path(".")
    }
}

fun JobBuilder.setup_remote_docker(body: SetupRemoteDockerBuilder.() -> Unit) {
    step(SetupRemoteDockerStepSpec {
        setup_remote_docker(body)
    })
}

fun JobBuilder.save_cache(body: SaveCacheBuilder.() -> Unit) {
    step(SaveCacheStepSpec {
        save_cache(body)
    })
}

fun JobBuilder.restore_cache(body: RestoreCacheBuilder.() -> Unit) {
    step(RestoreCacheStepSpec {
        restore_cache(body)
    })
}

fun JobBuilder.deploy(body: RunBuilder.() -> Unit) {
    step(DeployStepSpec {
        deploy(body)
    })
}

fun JobBuilder.store_artifacts(body: StoreArtifactsBuilder.() -> Unit) {
    step(StoreArtifactsStepSpec {
        store_artifacts(body)
    })
}

fun JobBuilder.store_test_results(body: StoreTestResultsBuilder.() -> Unit) {
    step(StoreTestResultsStepSpec {
        store_test_results(body)
    })
}

fun JobBuilder.persist_to_workspace(body: PersistToWorkspaceBuilder.() -> Unit) {
    step(PersistToWorkspaceStepSpec {
        persist_to_workspace(body)
    })
}

fun JobBuilder.attach_workspace(body: AttachWorkspaceBuilder.() -> Unit) {
    step(AttachWorkspaceStepSpec {
        attach_workspace(body)
    })
}

fun JobBuilder.add_ssh_keys(body: AddSshKeysBuilder.() -> Unit) {
    step(AddSshKeysStepSpec {
        add_ssh_keys(body)
    })
}
