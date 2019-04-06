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
package io.philarios.circleci

import io.philarios.core.DslBuilder
import kotlin.Boolean
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map

class CircleCISpec<in C>(internal val body: CircleCIBuilder<C>.() -> Unit)

@DslBuilder
interface CircleCIBuilder<out C> {
    val context: C

    fun version(value: String)

    fun jobs(key: String, body: JobBuilder<C>.() -> Unit)

    fun jobs(key: String, spec: JobSpec<C>)

    fun jobs(key: String, ref: JobRef)

    fun jobs(key: String, value: Job)

    fun workflows(key: String, body: WorkflowBuilder<C>.() -> Unit)

    fun workflows(key: String, spec: WorkflowSpec<C>)

    fun workflows(key: String, ref: WorkflowRef)

    fun workflows(key: String, value: Workflow)

    fun include(body: CircleCIBuilder<C>.() -> Unit)

    fun include(spec: CircleCISpec<C>)

    fun <C2> include(context: C2, body: CircleCIBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: CircleCISpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: CircleCIBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: CircleCISpec<C2>)
}

class CircleCIRef(internal val key: String)

class JobSpec<in C>(internal val body: JobBuilder<C>.() -> Unit)

@DslBuilder
interface JobBuilder<out C> {
    val context: C

    fun docker(body: DockerExecutorBuilder<C>.() -> Unit)

    fun docker(spec: DockerExecutorSpec<C>)

    fun docker(ref: DockerExecutorRef)

    fun docker(value: DockerExecutor)

    fun docker(docker: List<DockerExecutor>)

    fun resource_class(value: ResourceClass)

    fun machine(body: MachineExecutorBuilder<C>.() -> Unit)

    fun machine(spec: MachineExecutorSpec<C>)

    fun machine(ref: MachineExecutorRef)

    fun machine(value: MachineExecutor)

    fun macos(body: MacosExecutorBuilder<C>.() -> Unit)

    fun macos(spec: MacosExecutorSpec<C>)

    fun macos(ref: MacosExecutorRef)

    fun macos(value: MacosExecutor)

    fun shell(value: String)

    fun <T : Step> step(spec: StepSpec<C, T>)

    fun <T : Step> step(ref: StepRef<T>)

    fun <T : Step> step(value: T)

    fun working_directory(value: String)

    fun parallelism(value: Int)

    fun environment(key: String, value: String)

    fun environment(pair: Pair<String, String>)

    fun environment(environment: Map<String, String>)

    fun branches(key: String, value: String)

    fun branches(pair: Pair<String, String>)

    fun branches(branches: Map<String, String>)

    fun include(body: JobBuilder<C>.() -> Unit)

    fun include(spec: JobSpec<C>)

    fun <C2> include(context: C2, body: JobBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: JobSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: JobBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: JobSpec<C2>)
}

class JobRef(internal val key: String)

class DockerExecutorSpec<in C>(internal val body: DockerExecutorBuilder<C>.() -> Unit)

@DslBuilder
interface DockerExecutorBuilder<out C> {
    val context: C

    fun image(value: String)

    fun entrypoint(value: String)

    fun entrypoint(entrypoint: List<String>)

    fun command(value: String)

    fun command(command: List<String>)

    fun user(value: String)

    fun environment(key: String, value: String)

    fun environment(pair: Pair<String, String>)

    fun environment(environment: Map<String, String>)

    fun auth(body: AuthBuilder<C>.() -> Unit)

    fun auth(spec: AuthSpec<C>)

    fun auth(ref: AuthRef)

    fun auth(value: Auth)

    fun aws_auth(body: AwsAuthBuilder<C>.() -> Unit)

    fun aws_auth(spec: AwsAuthSpec<C>)

    fun aws_auth(ref: AwsAuthRef)

    fun aws_auth(value: AwsAuth)

    fun include(body: DockerExecutorBuilder<C>.() -> Unit)

    fun include(spec: DockerExecutorSpec<C>)

    fun <C2> include(context: C2, body: DockerExecutorBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: DockerExecutorSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: DockerExecutorBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: DockerExecutorSpec<C2>)
}

class DockerExecutorRef(internal val key: String)

class AuthSpec<in C>(internal val body: AuthBuilder<C>.() -> Unit)

@DslBuilder
interface AuthBuilder<out C> {
    val context: C

    fun username(value: String)

    fun password(value: String)

    fun include(body: AuthBuilder<C>.() -> Unit)

    fun include(spec: AuthSpec<C>)

    fun <C2> include(context: C2, body: AuthBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: AuthSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: AuthBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: AuthSpec<C2>)
}

class AuthRef(internal val key: String)

class AwsAuthSpec<in C>(internal val body: AwsAuthBuilder<C>.() -> Unit)

@DslBuilder
interface AwsAuthBuilder<out C> {
    val context: C

    fun aws_access_key_id(value: String)

    fun aws_secret_access_key(value: String)

    fun include(body: AwsAuthBuilder<C>.() -> Unit)

    fun include(spec: AwsAuthSpec<C>)

    fun <C2> include(context: C2, body: AwsAuthBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: AwsAuthSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: AwsAuthBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: AwsAuthSpec<C2>)
}

class AwsAuthRef(internal val key: String)

class MachineExecutorSpec<in C>(internal val body: MachineExecutorBuilder<C>.() -> Unit)

@DslBuilder
interface MachineExecutorBuilder<out C> {
    val context: C

    fun enabled(value: Boolean)

    fun image(value: String)

    fun include(body: MachineExecutorBuilder<C>.() -> Unit)

    fun include(spec: MachineExecutorSpec<C>)

    fun <C2> include(context: C2, body: MachineExecutorBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: MachineExecutorSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: MachineExecutorBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: MachineExecutorSpec<C2>)
}

class MachineExecutorRef(internal val key: String)

class MacosExecutorSpec<in C>(internal val body: MacosExecutorBuilder<C>.() -> Unit)

@DslBuilder
interface MacosExecutorBuilder<out C> {
    val context: C

    fun xcode(value: String)

    fun include(body: MacosExecutorBuilder<C>.() -> Unit)

    fun include(spec: MacosExecutorSpec<C>)

    fun <C2> include(context: C2, body: MacosExecutorBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: MacosExecutorSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: MacosExecutorBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: MacosExecutorSpec<C2>)
}

class MacosExecutorRef(internal val key: String)

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

class RunSpec<in C>(internal val body: RunBuilder<C>.() -> Unit)

@DslBuilder
interface RunBuilder<out C> {
    val context: C

    fun name(value: String)

    fun command(value: String)

    fun shell(value: String)

    fun environment(key: String, value: String)

    fun environment(pair: Pair<String, String>)

    fun environment(environment: Map<String, String>)

    fun background(value: Boolean)

    fun working_directory(value: String)

    fun no_output_timeout(value: String)

    fun `when`(value: When)

    fun include(body: RunBuilder<C>.() -> Unit)

    fun include(spec: RunSpec<C>)

    fun <C2> include(context: C2, body: RunBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: RunSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: RunBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: RunSpec<C2>)
}

class RunRef(internal val key: String)

class CheckoutSpec<in C>(internal val body: CheckoutBuilder<C>.() -> Unit)

@DslBuilder
interface CheckoutBuilder<out C> {
    val context: C

    fun path(value: String)

    fun include(body: CheckoutBuilder<C>.() -> Unit)

    fun include(spec: CheckoutSpec<C>)

    fun <C2> include(context: C2, body: CheckoutBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: CheckoutSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: CheckoutBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: CheckoutSpec<C2>)
}

class CheckoutRef(internal val key: String)

class SetupRemoteDockerSpec<in C>(internal val body: SetupRemoteDockerBuilder<C>.() -> Unit)

@DslBuilder
interface SetupRemoteDockerBuilder<out C> {
    val context: C

    fun docker_layer_caching(value: Boolean)

    fun version(value: String)

    fun include(body: SetupRemoteDockerBuilder<C>.() -> Unit)

    fun include(spec: SetupRemoteDockerSpec<C>)

    fun <C2> include(context: C2, body: SetupRemoteDockerBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: SetupRemoteDockerSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: SetupRemoteDockerBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: SetupRemoteDockerSpec<C2>)
}

class SetupRemoteDockerRef(internal val key: String)

class SaveCacheSpec<in C>(internal val body: SaveCacheBuilder<C>.() -> Unit)

@DslBuilder
interface SaveCacheBuilder<out C> {
    val context: C

    fun path(value: String)

    fun paths(paths: List<String>)

    fun key(value: String)

    fun `when`(value: When)

    fun name(value: String)

    fun include(body: SaveCacheBuilder<C>.() -> Unit)

    fun include(spec: SaveCacheSpec<C>)

    fun <C2> include(context: C2, body: SaveCacheBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: SaveCacheSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: SaveCacheBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: SaveCacheSpec<C2>)
}

class SaveCacheRef(internal val key: String)

class RestoreCacheSpec<in C>(internal val body: RestoreCacheBuilder<C>.() -> Unit)

@DslBuilder
interface RestoreCacheBuilder<out C> {
    val context: C

    fun key(value: String)

    fun keys(keys: List<String>)

    fun name(value: String)

    fun include(body: RestoreCacheBuilder<C>.() -> Unit)

    fun include(spec: RestoreCacheSpec<C>)

    fun <C2> include(context: C2, body: RestoreCacheBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: RestoreCacheSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: RestoreCacheBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: RestoreCacheSpec<C2>)
}

class RestoreCacheRef(internal val key: String)

class StoreArtifactsSpec<in C>(internal val body: StoreArtifactsBuilder<C>.() -> Unit)

@DslBuilder
interface StoreArtifactsBuilder<out C> {
    val context: C

    fun path(value: String)

    fun destination(value: String)

    fun include(body: StoreArtifactsBuilder<C>.() -> Unit)

    fun include(spec: StoreArtifactsSpec<C>)

    fun <C2> include(context: C2, body: StoreArtifactsBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: StoreArtifactsSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: StoreArtifactsBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: StoreArtifactsSpec<C2>)
}

class StoreArtifactsRef(internal val key: String)

class StoreTestResultsSpec<in C>(internal val body: StoreTestResultsBuilder<C>.() -> Unit)

@DslBuilder
interface StoreTestResultsBuilder<out C> {
    val context: C

    fun path(value: String)

    fun include(body: StoreTestResultsBuilder<C>.() -> Unit)

    fun include(spec: StoreTestResultsSpec<C>)

    fun <C2> include(context: C2, body: StoreTestResultsBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: StoreTestResultsSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: StoreTestResultsBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: StoreTestResultsSpec<C2>)
}

class StoreTestResultsRef(internal val key: String)

class PersistToWorkspaceSpec<in C>(internal val body: PersistToWorkspaceBuilder<C>.() -> Unit)

@DslBuilder
interface PersistToWorkspaceBuilder<out C> {
    val context: C

    fun root(value: String)

    fun path(value: String)

    fun paths(paths: List<String>)

    fun include(body: PersistToWorkspaceBuilder<C>.() -> Unit)

    fun include(spec: PersistToWorkspaceSpec<C>)

    fun <C2> include(context: C2, body: PersistToWorkspaceBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: PersistToWorkspaceSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: PersistToWorkspaceBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: PersistToWorkspaceSpec<C2>)
}

class PersistToWorkspaceRef(internal val key: String)

class AttachWorkspaceSpec<in C>(internal val body: AttachWorkspaceBuilder<C>.() -> Unit)

@DslBuilder
interface AttachWorkspaceBuilder<out C> {
    val context: C

    fun at(value: String)

    fun include(body: AttachWorkspaceBuilder<C>.() -> Unit)

    fun include(spec: AttachWorkspaceSpec<C>)

    fun <C2> include(context: C2, body: AttachWorkspaceBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: AttachWorkspaceSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: AttachWorkspaceBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: AttachWorkspaceSpec<C2>)
}

class AttachWorkspaceRef(internal val key: String)

class AddSshKeysSpec<in C>(internal val body: AddSshKeysBuilder<C>.() -> Unit)

@DslBuilder
interface AddSshKeysBuilder<out C> {
    val context: C

    fun fingerprint(value: String)

    fun fingerprints(fingerprints: List<String>)

    fun include(body: AddSshKeysBuilder<C>.() -> Unit)

    fun include(spec: AddSshKeysSpec<C>)

    fun <C2> include(context: C2, body: AddSshKeysBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: AddSshKeysSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: AddSshKeysBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: AddSshKeysSpec<C2>)
}

class AddSshKeysRef(internal val key: String)

class WorkflowSpec<in C>(internal val body: WorkflowBuilder<C>.() -> Unit)

@DslBuilder
interface WorkflowBuilder<out C> {
    val context: C

    fun trigger(body: WorkflowTriggerBuilder<C>.() -> Unit)

    fun trigger(spec: WorkflowTriggerSpec<C>)

    fun trigger(ref: WorkflowTriggerRef)

    fun trigger(value: WorkflowTrigger)

    fun triggers(triggers: List<WorkflowTrigger>)

    fun jobs(key: String, body: WorkflowJobBuilder<C>.() -> Unit)

    fun jobs(key: String, spec: WorkflowJobSpec<C>)

    fun jobs(key: String, ref: WorkflowJobRef)

    fun jobs(key: String, value: WorkflowJob)

    fun include(body: WorkflowBuilder<C>.() -> Unit)

    fun include(spec: WorkflowSpec<C>)

    fun <C2> include(context: C2, body: WorkflowBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkflowSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowSpec<C2>)
}

class WorkflowRef(internal val key: String)

class WorkflowTriggerSpec<in C>(internal val body: WorkflowTriggerBuilder<C>.() -> Unit)

@DslBuilder
interface WorkflowTriggerBuilder<out C> {
    val context: C

    fun schedule(body: WorkflowTriggerScheduleBuilder<C>.() -> Unit)

    fun schedule(spec: WorkflowTriggerScheduleSpec<C>)

    fun schedule(ref: WorkflowTriggerScheduleRef)

    fun schedule(value: WorkflowTriggerSchedule)

    fun include(body: WorkflowTriggerBuilder<C>.() -> Unit)

    fun include(spec: WorkflowTriggerSpec<C>)

    fun <C2> include(context: C2, body: WorkflowTriggerBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkflowTriggerSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowTriggerBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowTriggerSpec<C2>)
}

class WorkflowTriggerRef(internal val key: String)

class WorkflowTriggerScheduleSpec<in C>(internal val body: WorkflowTriggerScheduleBuilder<C>.() -> Unit)

@DslBuilder
interface WorkflowTriggerScheduleBuilder<out C> {
    val context: C

    fun cron(value: String)

    fun filters(body: WorkflowTriggerScheduleBuilder<C>.() -> Unit)

    fun filters(spec: WorkflowTriggerScheduleSpec<C>)

    fun filters(ref: WorkflowTriggerScheduleRef)

    fun filters(value: WorkflowTriggerSchedule)

    fun include(body: WorkflowTriggerScheduleBuilder<C>.() -> Unit)

    fun include(spec: WorkflowTriggerScheduleSpec<C>)

    fun <C2> include(context: C2, body: WorkflowTriggerScheduleBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkflowTriggerScheduleSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowTriggerScheduleBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowTriggerScheduleSpec<C2>)
}

class WorkflowTriggerScheduleRef(internal val key: String)

class WorkflowTriggerScheduleFiltersSpec<in C>(internal val body: WorkflowTriggerScheduleFiltersBuilder<C>.() -> Unit)

@DslBuilder
interface WorkflowTriggerScheduleFiltersBuilder<out C> {
    val context: C

    fun branches(body: FilterBuilder<C>.() -> Unit)

    fun branches(spec: FilterSpec<C>)

    fun branches(ref: FilterRef)

    fun branches(value: Filter)

    fun include(body: WorkflowTriggerScheduleFiltersBuilder<C>.() -> Unit)

    fun include(spec: WorkflowTriggerScheduleFiltersSpec<C>)

    fun <C2> include(context: C2, body: WorkflowTriggerScheduleFiltersBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkflowTriggerScheduleFiltersSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowTriggerScheduleFiltersBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowTriggerScheduleFiltersSpec<C2>)
}

class WorkflowTriggerScheduleFiltersRef(internal val key: String)

class WorkflowJobSpec<in C>(internal val body: WorkflowJobBuilder<C>.() -> Unit)

@DslBuilder
interface WorkflowJobBuilder<out C> {
    val context: C

    fun require(value: String)

    fun requires(requires: List<String>)

    fun context(value: String)

    fun type(value: WorkflowJobType)

    fun filters(body: WorkflowJobFilterBuilder<C>.() -> Unit)

    fun filters(spec: WorkflowJobFilterSpec<C>)

    fun filters(ref: WorkflowJobFilterRef)

    fun filters(value: WorkflowJobFilter)

    fun include(body: WorkflowJobBuilder<C>.() -> Unit)

    fun include(spec: WorkflowJobSpec<C>)

    fun <C2> include(context: C2, body: WorkflowJobBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkflowJobSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowJobBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowJobSpec<C2>)
}

class WorkflowJobRef(internal val key: String)

class WorkflowJobFilterSpec<in C>(internal val body: WorkflowJobFilterBuilder<C>.() -> Unit)

@DslBuilder
interface WorkflowJobFilterBuilder<out C> {
    val context: C

    fun branches(body: FilterBuilder<C>.() -> Unit)

    fun branches(spec: FilterSpec<C>)

    fun branches(ref: FilterRef)

    fun branches(value: Filter)

    fun tags(body: FilterBuilder<C>.() -> Unit)

    fun tags(spec: FilterSpec<C>)

    fun tags(ref: FilterRef)

    fun tags(value: Filter)

    fun include(body: WorkflowJobFilterBuilder<C>.() -> Unit)

    fun include(spec: WorkflowJobFilterSpec<C>)

    fun <C2> include(context: C2, body: WorkflowJobFilterBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkflowJobFilterSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowJobFilterBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowJobFilterSpec<C2>)
}

class WorkflowJobFilterRef(internal val key: String)

class FilterSpec<in C>(internal val body: FilterBuilder<C>.() -> Unit)

@DslBuilder
interface FilterBuilder<out C> {
    val context: C

    fun only(value: String)

    fun only(only: List<String>)

    fun ignore(value: String)

    fun ignore(ignore: List<String>)

    fun include(body: FilterBuilder<C>.() -> Unit)

    fun include(spec: FilterSpec<C>)

    fun <C2> include(context: C2, body: FilterBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: FilterSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: FilterBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: FilterSpec<C2>)
}

class FilterRef(internal val key: String)
