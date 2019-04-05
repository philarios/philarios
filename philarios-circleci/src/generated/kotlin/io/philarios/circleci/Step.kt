package io.philarios.circleci

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

sealed class Step

data class RunStep(val run: Run?) : Step()

data class CheckoutStep(val checkout: Checkout?) : Step()

data class SetupRemoteDockerStep(val setup_remote_docker: SetupRemoteDocker?) : Step()

data class SaveCacheStep(val save_cache: SaveCache?) : Step()

data class RestoreCacheStep(val restore_cache: RestoreCache?) : Step()

data class DeployStep(val deploy: Run?) : Step()

data class StoreArtifactsStep(val store_artifacts: StoreArtifacts?) : Step()

data class StoreTestResultsStep(val store_test_results: StoreTestResults?) : Step()

data class PersistToWorkspaceStep(val persist_to_workspace: PersistToWorkspace?) : Step()

data class AttachWorkspaceStep(val attach_workspace: AttachWorkspace?) : Step()

data class AddSshKeysStep(val add_ssh_keys: AddSshKeys?) : Step()

sealed class StepSpec<in C, out T : Step>

class RunStepSpec<in C>(internal val body: RunStepBuilder<C>.() -> Unit) : StepSpec<C, RunStep>()

class CheckoutStepSpec<in C>(internal val body: CheckoutStepBuilder<C>.() -> Unit) : StepSpec<C, CheckoutStep>()

class SetupRemoteDockerStepSpec<in C>(internal val body: SetupRemoteDockerStepBuilder<C>.() -> Unit) : StepSpec<C, SetupRemoteDockerStep>()

class SaveCacheStepSpec<in C>(internal val body: SaveCacheStepBuilder<C>.() -> Unit) : StepSpec<C, SaveCacheStep>()

class RestoreCacheStepSpec<in C>(internal val body: RestoreCacheStepBuilder<C>.() -> Unit) : StepSpec<C, RestoreCacheStep>()

class DeployStepSpec<in C>(internal val body: DeployStepBuilder<C>.() -> Unit) : StepSpec<C, DeployStep>()

class StoreArtifactsStepSpec<in C>(internal val body: StoreArtifactsStepBuilder<C>.() -> Unit) : StepSpec<C, StoreArtifactsStep>()

class StoreTestResultsStepSpec<in C>(internal val body: StoreTestResultsStepBuilder<C>.() -> Unit) : StepSpec<C, StoreTestResultsStep>()

class PersistToWorkspaceStepSpec<in C>(internal val body: PersistToWorkspaceStepBuilder<C>.() -> Unit) : StepSpec<C, PersistToWorkspaceStep>()

class AttachWorkspaceStepSpec<in C>(internal val body: AttachWorkspaceStepBuilder<C>.() -> Unit) : StepSpec<C, AttachWorkspaceStep>()

class AddSshKeysStepSpec<in C>(internal val body: AddSshKeysStepBuilder<C>.() -> Unit) : StepSpec<C, AddSshKeysStep>()

@DslBuilder
interface RunStepBuilder<out C> {
    val context: C

    fun run(body: RunBuilder<C>.() -> Unit)

    fun run(spec: RunSpec<C>)

    fun run(ref: RunRef)

    fun run(value: Run)

    fun include(body: RunStepBuilder<C>.() -> Unit)

    fun include(spec: RunStepSpec<C>)

    fun <C2> include(context: C2, body: RunStepBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: RunStepSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: RunStepBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: RunStepSpec<C2>)
}

@DslBuilder
interface CheckoutStepBuilder<out C> {
    val context: C

    fun checkout(body: CheckoutBuilder<C>.() -> Unit)

    fun checkout(spec: CheckoutSpec<C>)

    fun checkout(ref: CheckoutRef)

    fun checkout(value: Checkout)

    fun include(body: CheckoutStepBuilder<C>.() -> Unit)

    fun include(spec: CheckoutStepSpec<C>)

    fun <C2> include(context: C2, body: CheckoutStepBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: CheckoutStepSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: CheckoutStepBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: CheckoutStepSpec<C2>)
}

@DslBuilder
interface SetupRemoteDockerStepBuilder<out C> {
    val context: C

    fun setup_remote_docker(body: SetupRemoteDockerBuilder<C>.() -> Unit)

    fun setup_remote_docker(spec: SetupRemoteDockerSpec<C>)

    fun setup_remote_docker(ref: SetupRemoteDockerRef)

    fun setup_remote_docker(value: SetupRemoteDocker)

    fun include(body: SetupRemoteDockerStepBuilder<C>.() -> Unit)

    fun include(spec: SetupRemoteDockerStepSpec<C>)

    fun <C2> include(context: C2, body: SetupRemoteDockerStepBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: SetupRemoteDockerStepSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: SetupRemoteDockerStepBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: SetupRemoteDockerStepSpec<C2>)
}

@DslBuilder
interface SaveCacheStepBuilder<out C> {
    val context: C

    fun save_cache(body: SaveCacheBuilder<C>.() -> Unit)

    fun save_cache(spec: SaveCacheSpec<C>)

    fun save_cache(ref: SaveCacheRef)

    fun save_cache(value: SaveCache)

    fun include(body: SaveCacheStepBuilder<C>.() -> Unit)

    fun include(spec: SaveCacheStepSpec<C>)

    fun <C2> include(context: C2, body: SaveCacheStepBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: SaveCacheStepSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: SaveCacheStepBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: SaveCacheStepSpec<C2>)
}

@DslBuilder
interface RestoreCacheStepBuilder<out C> {
    val context: C

    fun restore_cache(body: RestoreCacheBuilder<C>.() -> Unit)

    fun restore_cache(spec: RestoreCacheSpec<C>)

    fun restore_cache(ref: RestoreCacheRef)

    fun restore_cache(value: RestoreCache)

    fun include(body: RestoreCacheStepBuilder<C>.() -> Unit)

    fun include(spec: RestoreCacheStepSpec<C>)

    fun <C2> include(context: C2, body: RestoreCacheStepBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: RestoreCacheStepSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: RestoreCacheStepBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: RestoreCacheStepSpec<C2>)
}

@DslBuilder
interface DeployStepBuilder<out C> {
    val context: C

    fun deploy(body: RunBuilder<C>.() -> Unit)

    fun deploy(spec: RunSpec<C>)

    fun deploy(ref: RunRef)

    fun deploy(value: Run)

    fun include(body: DeployStepBuilder<C>.() -> Unit)

    fun include(spec: DeployStepSpec<C>)

    fun <C2> include(context: C2, body: DeployStepBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: DeployStepSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: DeployStepBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: DeployStepSpec<C2>)
}

@DslBuilder
interface StoreArtifactsStepBuilder<out C> {
    val context: C

    fun store_artifacts(body: StoreArtifactsBuilder<C>.() -> Unit)

    fun store_artifacts(spec: StoreArtifactsSpec<C>)

    fun store_artifacts(ref: StoreArtifactsRef)

    fun store_artifacts(value: StoreArtifacts)

    fun include(body: StoreArtifactsStepBuilder<C>.() -> Unit)

    fun include(spec: StoreArtifactsStepSpec<C>)

    fun <C2> include(context: C2, body: StoreArtifactsStepBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: StoreArtifactsStepSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: StoreArtifactsStepBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: StoreArtifactsStepSpec<C2>)
}

@DslBuilder
interface StoreTestResultsStepBuilder<out C> {
    val context: C

    fun store_test_results(body: StoreTestResultsBuilder<C>.() -> Unit)

    fun store_test_results(spec: StoreTestResultsSpec<C>)

    fun store_test_results(ref: StoreTestResultsRef)

    fun store_test_results(value: StoreTestResults)

    fun include(body: StoreTestResultsStepBuilder<C>.() -> Unit)

    fun include(spec: StoreTestResultsStepSpec<C>)

    fun <C2> include(context: C2, body: StoreTestResultsStepBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: StoreTestResultsStepSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: StoreTestResultsStepBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: StoreTestResultsStepSpec<C2>)
}

@DslBuilder
interface PersistToWorkspaceStepBuilder<out C> {
    val context: C

    fun persist_to_workspace(body: PersistToWorkspaceBuilder<C>.() -> Unit)

    fun persist_to_workspace(spec: PersistToWorkspaceSpec<C>)

    fun persist_to_workspace(ref: PersistToWorkspaceRef)

    fun persist_to_workspace(value: PersistToWorkspace)

    fun include(body: PersistToWorkspaceStepBuilder<C>.() -> Unit)

    fun include(spec: PersistToWorkspaceStepSpec<C>)

    fun <C2> include(context: C2, body: PersistToWorkspaceStepBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: PersistToWorkspaceStepSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: PersistToWorkspaceStepBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: PersistToWorkspaceStepSpec<C2>)
}

@DslBuilder
interface AttachWorkspaceStepBuilder<out C> {
    val context: C

    fun attach_workspace(body: AttachWorkspaceBuilder<C>.() -> Unit)

    fun attach_workspace(spec: AttachWorkspaceSpec<C>)

    fun attach_workspace(ref: AttachWorkspaceRef)

    fun attach_workspace(value: AttachWorkspace)

    fun include(body: AttachWorkspaceStepBuilder<C>.() -> Unit)

    fun include(spec: AttachWorkspaceStepSpec<C>)

    fun <C2> include(context: C2, body: AttachWorkspaceStepBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: AttachWorkspaceStepSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: AttachWorkspaceStepBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: AttachWorkspaceStepSpec<C2>)
}

@DslBuilder
interface AddSshKeysStepBuilder<out C> {
    val context: C

    fun add_ssh_keys(body: AddSshKeysBuilder<C>.() -> Unit)

    fun add_ssh_keys(spec: AddSshKeysSpec<C>)

    fun add_ssh_keys(ref: AddSshKeysRef)

    fun add_ssh_keys(value: AddSshKeys)

    fun include(body: AddSshKeysStepBuilder<C>.() -> Unit)

    fun include(spec: AddSshKeysStepSpec<C>)

    fun <C2> include(context: C2, body: AddSshKeysStepBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: AddSshKeysStepSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: AddSshKeysStepBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: AddSshKeysStepSpec<C2>)
}

sealed class StepRef<T : Step> {
    internal abstract val key: String
}

class RunStepRef(override val key: String) : StepRef<RunStep>()

class CheckoutStepRef(override val key: String) : StepRef<CheckoutStep>()

class SetupRemoteDockerStepRef(override val key: String) : StepRef<SetupRemoteDockerStep>()

class SaveCacheStepRef(override val key: String) : StepRef<SaveCacheStep>()

class RestoreCacheStepRef(override val key: String) : StepRef<RestoreCacheStep>()

class DeployStepRef(override val key: String) : StepRef<DeployStep>()

class StoreArtifactsStepRef(override val key: String) : StepRef<StoreArtifactsStep>()

class StoreTestResultsStepRef(override val key: String) : StepRef<StoreTestResultsStep>()

class PersistToWorkspaceStepRef(override val key: String) : StepRef<PersistToWorkspaceStep>()

class AttachWorkspaceStepRef(override val key: String) : StepRef<AttachWorkspaceStep>()

class AddSshKeysStepRef(override val key: String) : StepRef<AddSshKeysStep>()

internal sealed class StepShell

internal data class RunStepShell(var run: Scaffold<Run>? = null) : StepShell(), Scaffold<RunStep> {
    override suspend fun resolve(registry: Registry): RunStep {
        coroutineScope {
            run?.let{ launch { it.resolve(registry) } }
        }
        val value = RunStep(
            run?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class CheckoutStepShell(var checkout: Scaffold<Checkout>? = null) : StepShell(),
        Scaffold<CheckoutStep> {
    override suspend fun resolve(registry: Registry): CheckoutStep {
        coroutineScope {
            checkout?.let{ launch { it.resolve(registry) } }
        }
        val value = CheckoutStep(
            checkout?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class SetupRemoteDockerStepShell(var setup_remote_docker: Scaffold<SetupRemoteDocker>? = null) : StepShell(),
        Scaffold<SetupRemoteDockerStep> {
    override suspend fun resolve(registry: Registry): SetupRemoteDockerStep {
        coroutineScope {
            setup_remote_docker?.let{ launch { it.resolve(registry) } }
        }
        val value = SetupRemoteDockerStep(
            setup_remote_docker?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class SaveCacheStepShell(var save_cache: Scaffold<SaveCache>? = null) : StepShell(),
        Scaffold<SaveCacheStep> {
    override suspend fun resolve(registry: Registry): SaveCacheStep {
        coroutineScope {
            save_cache?.let{ launch { it.resolve(registry) } }
        }
        val value = SaveCacheStep(
            save_cache?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class RestoreCacheStepShell(var restore_cache: Scaffold<RestoreCache>? = null) : StepShell(),
        Scaffold<RestoreCacheStep> {
    override suspend fun resolve(registry: Registry): RestoreCacheStep {
        coroutineScope {
            restore_cache?.let{ launch { it.resolve(registry) } }
        }
        val value = RestoreCacheStep(
            restore_cache?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class DeployStepShell(var deploy: Scaffold<Run>? = null) : StepShell(),
        Scaffold<DeployStep> {
    override suspend fun resolve(registry: Registry): DeployStep {
        coroutineScope {
            deploy?.let{ launch { it.resolve(registry) } }
        }
        val value = DeployStep(
            deploy?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class StoreArtifactsStepShell(var store_artifacts: Scaffold<StoreArtifacts>? = null) : StepShell(),
        Scaffold<StoreArtifactsStep> {
    override suspend fun resolve(registry: Registry): StoreArtifactsStep {
        coroutineScope {
            store_artifacts?.let{ launch { it.resolve(registry) } }
        }
        val value = StoreArtifactsStep(
            store_artifacts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class StoreTestResultsStepShell(var store_test_results: Scaffold<StoreTestResults>? = null) : StepShell(),
        Scaffold<StoreTestResultsStep> {
    override suspend fun resolve(registry: Registry): StoreTestResultsStep {
        coroutineScope {
            store_test_results?.let{ launch { it.resolve(registry) } }
        }
        val value = StoreTestResultsStep(
            store_test_results?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class PersistToWorkspaceStepShell(var persist_to_workspace: Scaffold<PersistToWorkspace>? = null) : StepShell(),
        Scaffold<PersistToWorkspaceStep> {
    override suspend fun resolve(registry: Registry): PersistToWorkspaceStep {
        coroutineScope {
            persist_to_workspace?.let{ launch { it.resolve(registry) } }
        }
        val value = PersistToWorkspaceStep(
            persist_to_workspace?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class AttachWorkspaceStepShell(var attach_workspace: Scaffold<AttachWorkspace>? = null) : StepShell(),
        Scaffold<AttachWorkspaceStep> {
    override suspend fun resolve(registry: Registry): AttachWorkspaceStep {
        coroutineScope {
            attach_workspace?.let{ launch { it.resolve(registry) } }
        }
        val value = AttachWorkspaceStep(
            attach_workspace?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class AddSshKeysStepShell(var add_ssh_keys: Scaffold<AddSshKeys>? = null) : StepShell(),
        Scaffold<AddSshKeysStep> {
    override suspend fun resolve(registry: Registry): AddSshKeysStep {
        coroutineScope {
            add_ssh_keys?.let{ launch { it.resolve(registry) } }
        }
        val value = AddSshKeysStep(
            add_ssh_keys?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class RunStepShellBuilder<out C>(override val context: C, internal var shell: RunStepShell = RunStepShell()) : RunStepBuilder<C> {
    override fun run(body: RunBuilder<C>.() -> Unit) {
        shell = shell.copy(run = RunScaffolder<C>(RunSpec<C>(body)).createScaffold(context))
    }

    override fun run(spec: RunSpec<C>) {
        shell = shell.copy(run = RunScaffolder<C>(spec).createScaffold(context))
    }

    override fun run(ref: RunRef) {
        shell = shell.copy(run = Deferred(ref.key))
    }

    override fun run(value: Run) {
        shell = shell.copy(run = Wrapper(value))
    }

    override fun include(body: RunStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: RunStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: RunStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: RunStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: RunStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: RunStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RunStepShellBuilder<C2> = RunStepShellBuilder(context, shell)

    private fun <C2> merge(other: RunStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class CheckoutStepShellBuilder<out C>(override val context: C, internal var shell: CheckoutStepShell = CheckoutStepShell()) : CheckoutStepBuilder<C> {
    override fun checkout(body: CheckoutBuilder<C>.() -> Unit) {
        shell = shell.copy(checkout = CheckoutScaffolder<C>(CheckoutSpec<C>(body)).createScaffold(context))
    }

    override fun checkout(spec: CheckoutSpec<C>) {
        shell = shell.copy(checkout = CheckoutScaffolder<C>(spec).createScaffold(context))
    }

    override fun checkout(ref: CheckoutRef) {
        shell = shell.copy(checkout = Deferred(ref.key))
    }

    override fun checkout(value: Checkout) {
        shell = shell.copy(checkout = Wrapper(value))
    }

    override fun include(body: CheckoutStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: CheckoutStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: CheckoutStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: CheckoutStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: CheckoutStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: CheckoutStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): CheckoutStepShellBuilder<C2> = CheckoutStepShellBuilder(context, shell)

    private fun <C2> merge(other: CheckoutStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class SetupRemoteDockerStepShellBuilder<out C>(override val context: C, internal var shell: SetupRemoteDockerStepShell = SetupRemoteDockerStepShell()) : SetupRemoteDockerStepBuilder<C> {
    override fun setup_remote_docker(body: SetupRemoteDockerBuilder<C>.() -> Unit) {
        shell = shell.copy(setup_remote_docker = SetupRemoteDockerScaffolder<C>(SetupRemoteDockerSpec<C>(body)).createScaffold(context))
    }

    override fun setup_remote_docker(spec: SetupRemoteDockerSpec<C>) {
        shell = shell.copy(setup_remote_docker = SetupRemoteDockerScaffolder<C>(spec).createScaffold(context))
    }

    override fun setup_remote_docker(ref: SetupRemoteDockerRef) {
        shell = shell.copy(setup_remote_docker = Deferred(ref.key))
    }

    override fun setup_remote_docker(value: SetupRemoteDocker) {
        shell = shell.copy(setup_remote_docker = Wrapper(value))
    }

    override fun include(body: SetupRemoteDockerStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SetupRemoteDockerStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SetupRemoteDockerStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SetupRemoteDockerStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SetupRemoteDockerStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SetupRemoteDockerStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SetupRemoteDockerStepShellBuilder<C2> = SetupRemoteDockerStepShellBuilder(context, shell)

    private fun <C2> merge(other: SetupRemoteDockerStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class SaveCacheStepShellBuilder<out C>(override val context: C, internal var shell: SaveCacheStepShell = SaveCacheStepShell()) : SaveCacheStepBuilder<C> {
    override fun save_cache(body: SaveCacheBuilder<C>.() -> Unit) {
        shell = shell.copy(save_cache = SaveCacheScaffolder<C>(SaveCacheSpec<C>(body)).createScaffold(context))
    }

    override fun save_cache(spec: SaveCacheSpec<C>) {
        shell = shell.copy(save_cache = SaveCacheScaffolder<C>(spec).createScaffold(context))
    }

    override fun save_cache(ref: SaveCacheRef) {
        shell = shell.copy(save_cache = Deferred(ref.key))
    }

    override fun save_cache(value: SaveCache) {
        shell = shell.copy(save_cache = Wrapper(value))
    }

    override fun include(body: SaveCacheStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SaveCacheStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SaveCacheStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SaveCacheStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SaveCacheStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SaveCacheStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SaveCacheStepShellBuilder<C2> = SaveCacheStepShellBuilder(context, shell)

    private fun <C2> merge(other: SaveCacheStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class RestoreCacheStepShellBuilder<out C>(override val context: C, internal var shell: RestoreCacheStepShell = RestoreCacheStepShell()) : RestoreCacheStepBuilder<C> {
    override fun restore_cache(body: RestoreCacheBuilder<C>.() -> Unit) {
        shell = shell.copy(restore_cache = RestoreCacheScaffolder<C>(RestoreCacheSpec<C>(body)).createScaffold(context))
    }

    override fun restore_cache(spec: RestoreCacheSpec<C>) {
        shell = shell.copy(restore_cache = RestoreCacheScaffolder<C>(spec).createScaffold(context))
    }

    override fun restore_cache(ref: RestoreCacheRef) {
        shell = shell.copy(restore_cache = Deferred(ref.key))
    }

    override fun restore_cache(value: RestoreCache) {
        shell = shell.copy(restore_cache = Wrapper(value))
    }

    override fun include(body: RestoreCacheStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: RestoreCacheStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: RestoreCacheStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: RestoreCacheStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: RestoreCacheStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: RestoreCacheStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RestoreCacheStepShellBuilder<C2> = RestoreCacheStepShellBuilder(context, shell)

    private fun <C2> merge(other: RestoreCacheStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class DeployStepShellBuilder<out C>(override val context: C, internal var shell: DeployStepShell = DeployStepShell()) : DeployStepBuilder<C> {
    override fun deploy(body: RunBuilder<C>.() -> Unit) {
        shell = shell.copy(deploy = RunScaffolder<C>(RunSpec<C>(body)).createScaffold(context))
    }

    override fun deploy(spec: RunSpec<C>) {
        shell = shell.copy(deploy = RunScaffolder<C>(spec).createScaffold(context))
    }

    override fun deploy(ref: RunRef) {
        shell = shell.copy(deploy = Deferred(ref.key))
    }

    override fun deploy(value: Run) {
        shell = shell.copy(deploy = Wrapper(value))
    }

    override fun include(body: DeployStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: DeployStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: DeployStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: DeployStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: DeployStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: DeployStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DeployStepShellBuilder<C2> = DeployStepShellBuilder(context, shell)

    private fun <C2> merge(other: DeployStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class StoreArtifactsStepShellBuilder<out C>(override val context: C, internal var shell: StoreArtifactsStepShell = StoreArtifactsStepShell()) : StoreArtifactsStepBuilder<C> {
    override fun store_artifacts(body: StoreArtifactsBuilder<C>.() -> Unit) {
        shell = shell.copy(store_artifacts = StoreArtifactsScaffolder<C>(StoreArtifactsSpec<C>(body)).createScaffold(context))
    }

    override fun store_artifacts(spec: StoreArtifactsSpec<C>) {
        shell = shell.copy(store_artifacts = StoreArtifactsScaffolder<C>(spec).createScaffold(context))
    }

    override fun store_artifacts(ref: StoreArtifactsRef) {
        shell = shell.copy(store_artifacts = Deferred(ref.key))
    }

    override fun store_artifacts(value: StoreArtifacts) {
        shell = shell.copy(store_artifacts = Wrapper(value))
    }

    override fun include(body: StoreArtifactsStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: StoreArtifactsStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: StoreArtifactsStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: StoreArtifactsStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: StoreArtifactsStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: StoreArtifactsStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): StoreArtifactsStepShellBuilder<C2> = StoreArtifactsStepShellBuilder(context, shell)

    private fun <C2> merge(other: StoreArtifactsStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class StoreTestResultsStepShellBuilder<out C>(override val context: C, internal var shell: StoreTestResultsStepShell = StoreTestResultsStepShell()) : StoreTestResultsStepBuilder<C> {
    override fun store_test_results(body: StoreTestResultsBuilder<C>.() -> Unit) {
        shell = shell.copy(store_test_results = StoreTestResultsScaffolder<C>(StoreTestResultsSpec<C>(body)).createScaffold(context))
    }

    override fun store_test_results(spec: StoreTestResultsSpec<C>) {
        shell = shell.copy(store_test_results = StoreTestResultsScaffolder<C>(spec).createScaffold(context))
    }

    override fun store_test_results(ref: StoreTestResultsRef) {
        shell = shell.copy(store_test_results = Deferred(ref.key))
    }

    override fun store_test_results(value: StoreTestResults) {
        shell = shell.copy(store_test_results = Wrapper(value))
    }

    override fun include(body: StoreTestResultsStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: StoreTestResultsStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: StoreTestResultsStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: StoreTestResultsStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: StoreTestResultsStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: StoreTestResultsStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): StoreTestResultsStepShellBuilder<C2> = StoreTestResultsStepShellBuilder(context, shell)

    private fun <C2> merge(other: StoreTestResultsStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class PersistToWorkspaceStepShellBuilder<out C>(override val context: C, internal var shell: PersistToWorkspaceStepShell = PersistToWorkspaceStepShell()) : PersistToWorkspaceStepBuilder<C> {
    override fun persist_to_workspace(body: PersistToWorkspaceBuilder<C>.() -> Unit) {
        shell = shell.copy(persist_to_workspace = PersistToWorkspaceScaffolder<C>(PersistToWorkspaceSpec<C>(body)).createScaffold(context))
    }

    override fun persist_to_workspace(spec: PersistToWorkspaceSpec<C>) {
        shell = shell.copy(persist_to_workspace = PersistToWorkspaceScaffolder<C>(spec).createScaffold(context))
    }

    override fun persist_to_workspace(ref: PersistToWorkspaceRef) {
        shell = shell.copy(persist_to_workspace = Deferred(ref.key))
    }

    override fun persist_to_workspace(value: PersistToWorkspace) {
        shell = shell.copy(persist_to_workspace = Wrapper(value))
    }

    override fun include(body: PersistToWorkspaceStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: PersistToWorkspaceStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: PersistToWorkspaceStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: PersistToWorkspaceStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: PersistToWorkspaceStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: PersistToWorkspaceStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): PersistToWorkspaceStepShellBuilder<C2> = PersistToWorkspaceStepShellBuilder(context, shell)

    private fun <C2> merge(other: PersistToWorkspaceStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class AttachWorkspaceStepShellBuilder<out C>(override val context: C, internal var shell: AttachWorkspaceStepShell = AttachWorkspaceStepShell()) : AttachWorkspaceStepBuilder<C> {
    override fun attach_workspace(body: AttachWorkspaceBuilder<C>.() -> Unit) {
        shell = shell.copy(attach_workspace = AttachWorkspaceScaffolder<C>(AttachWorkspaceSpec<C>(body)).createScaffold(context))
    }

    override fun attach_workspace(spec: AttachWorkspaceSpec<C>) {
        shell = shell.copy(attach_workspace = AttachWorkspaceScaffolder<C>(spec).createScaffold(context))
    }

    override fun attach_workspace(ref: AttachWorkspaceRef) {
        shell = shell.copy(attach_workspace = Deferred(ref.key))
    }

    override fun attach_workspace(value: AttachWorkspace) {
        shell = shell.copy(attach_workspace = Wrapper(value))
    }

    override fun include(body: AttachWorkspaceStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AttachWorkspaceStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AttachWorkspaceStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AttachWorkspaceStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AttachWorkspaceStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AttachWorkspaceStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AttachWorkspaceStepShellBuilder<C2> = AttachWorkspaceStepShellBuilder(context, shell)

    private fun <C2> merge(other: AttachWorkspaceStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class AddSshKeysStepShellBuilder<out C>(override val context: C, internal var shell: AddSshKeysStepShell = AddSshKeysStepShell()) : AddSshKeysStepBuilder<C> {
    override fun add_ssh_keys(body: AddSshKeysBuilder<C>.() -> Unit) {
        shell = shell.copy(add_ssh_keys = AddSshKeysScaffolder<C>(AddSshKeysSpec<C>(body)).createScaffold(context))
    }

    override fun add_ssh_keys(spec: AddSshKeysSpec<C>) {
        shell = shell.copy(add_ssh_keys = AddSshKeysScaffolder<C>(spec).createScaffold(context))
    }

    override fun add_ssh_keys(ref: AddSshKeysRef) {
        shell = shell.copy(add_ssh_keys = Deferred(ref.key))
    }

    override fun add_ssh_keys(value: AddSshKeys) {
        shell = shell.copy(add_ssh_keys = Wrapper(value))
    }

    override fun include(body: AddSshKeysStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AddSshKeysStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AddSshKeysStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AddSshKeysStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AddSshKeysStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AddSshKeysStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AddSshKeysStepShellBuilder<C2> = AddSshKeysStepShellBuilder(context, shell)

    private fun <C2> merge(other: AddSshKeysStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class StepScaffolder<in C, out T : Step>(internal val spec: StepSpec<C, T>) : Scaffolder<C, T> {
    override fun createScaffold(context: C): Scaffold<T> {
        val result = when (spec) {
            is RunStepSpec<C> -> RunStepScaffolder(spec).createScaffold(context)
            is CheckoutStepSpec<C> -> CheckoutStepScaffolder(spec).createScaffold(context)
            is SetupRemoteDockerStepSpec<C> -> SetupRemoteDockerStepScaffolder(spec).createScaffold(context)
            is SaveCacheStepSpec<C> -> SaveCacheStepScaffolder(spec).createScaffold(context)
            is RestoreCacheStepSpec<C> -> RestoreCacheStepScaffolder(spec).createScaffold(context)
            is DeployStepSpec<C> -> DeployStepScaffolder(spec).createScaffold(context)
            is StoreArtifactsStepSpec<C> -> StoreArtifactsStepScaffolder(spec).createScaffold(context)
            is StoreTestResultsStepSpec<C> -> StoreTestResultsStepScaffolder(spec).createScaffold(context)
            is PersistToWorkspaceStepSpec<C> -> PersistToWorkspaceStepScaffolder(spec).createScaffold(context)
            is AttachWorkspaceStepSpec<C> -> AttachWorkspaceStepScaffolder(spec).createScaffold(context)
            is AddSshKeysStepSpec<C> -> AddSshKeysStepScaffolder(spec).createScaffold(context)
        }
        return result as Scaffold<T>
    }
}

class RunStepScaffolder<in C>(internal val spec: RunStepSpec<C>) : Scaffolder<C, RunStep> {
    override fun createScaffold(context: C): Scaffold<RunStep> {
        val builder = RunStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class CheckoutStepScaffolder<in C>(internal val spec: CheckoutStepSpec<C>) : Scaffolder<C, CheckoutStep> {
    override fun createScaffold(context: C): Scaffold<CheckoutStep> {
        val builder = CheckoutStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class SetupRemoteDockerStepScaffolder<in C>(internal val spec: SetupRemoteDockerStepSpec<C>) : Scaffolder<C, SetupRemoteDockerStep> {
    override fun createScaffold(context: C): Scaffold<SetupRemoteDockerStep> {
        val builder = SetupRemoteDockerStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class SaveCacheStepScaffolder<in C>(internal val spec: SaveCacheStepSpec<C>) : Scaffolder<C, SaveCacheStep> {
    override fun createScaffold(context: C): Scaffold<SaveCacheStep> {
        val builder = SaveCacheStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class RestoreCacheStepScaffolder<in C>(internal val spec: RestoreCacheStepSpec<C>) : Scaffolder<C, RestoreCacheStep> {
    override fun createScaffold(context: C): Scaffold<RestoreCacheStep> {
        val builder = RestoreCacheStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class DeployStepScaffolder<in C>(internal val spec: DeployStepSpec<C>) : Scaffolder<C, DeployStep> {
    override fun createScaffold(context: C): Scaffold<DeployStep> {
        val builder = DeployStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class StoreArtifactsStepScaffolder<in C>(internal val spec: StoreArtifactsStepSpec<C>) : Scaffolder<C, StoreArtifactsStep> {
    override fun createScaffold(context: C): Scaffold<StoreArtifactsStep> {
        val builder = StoreArtifactsStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class StoreTestResultsStepScaffolder<in C>(internal val spec: StoreTestResultsStepSpec<C>) : Scaffolder<C, StoreTestResultsStep> {
    override fun createScaffold(context: C): Scaffold<StoreTestResultsStep> {
        val builder = StoreTestResultsStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class PersistToWorkspaceStepScaffolder<in C>(internal val spec: PersistToWorkspaceStepSpec<C>) : Scaffolder<C, PersistToWorkspaceStep> {
    override fun createScaffold(context: C): Scaffold<PersistToWorkspaceStep> {
        val builder = PersistToWorkspaceStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class AttachWorkspaceStepScaffolder<in C>(internal val spec: AttachWorkspaceStepSpec<C>) : Scaffolder<C, AttachWorkspaceStep> {
    override fun createScaffold(context: C): Scaffold<AttachWorkspaceStep> {
        val builder = AttachWorkspaceStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class AddSshKeysStepScaffolder<in C>(internal val spec: AddSshKeysStepSpec<C>) : Scaffolder<C, AddSshKeysStep> {
    override fun createScaffold(context: C): Scaffold<AddSshKeysStep> {
        val builder = AddSshKeysStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
