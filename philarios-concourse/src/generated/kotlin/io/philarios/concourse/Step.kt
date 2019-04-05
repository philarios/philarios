package io.philarios.concourse

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

sealed class Step

data class Get(
        val get: String,
        val resource: String?,
        val version: String?,
        val passed: List<String>,
        val params: Map<String, Any>,
        val trigger: Boolean?,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class Put(
        val put: String,
        val resource: String?,
        val params: Map<String, Any>,
        val get_params: Map<String, Any>,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class Task(
        val task: String,
        val config: TaskConfig,
        val file: String?,
        val privileged: String?,
        val params: Map<String, Any>,
        val image: String?,
        val input_mapping: Map<String, String>,
        val output_mapping: Map<String, String>,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class Aggregate(
        val aggregate: List<Step>,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class Do(
        val `do`: List<Step>,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class Try(
        val `try`: Step,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

sealed class StepSpec<in C, out T : Step>

class GetSpec<in C>(internal val body: GetBuilder<C>.() -> Unit) : StepSpec<C, Get>()

class PutSpec<in C>(internal val body: PutBuilder<C>.() -> Unit) : StepSpec<C, Put>()

class TaskSpec<in C>(internal val body: TaskBuilder<C>.() -> Unit) : StepSpec<C, Task>()

class AggregateSpec<in C>(internal val body: AggregateBuilder<C>.() -> Unit) : StepSpec<C, Aggregate>()

class DoSpec<in C>(internal val body: DoBuilder<C>.() -> Unit) : StepSpec<C, Do>()

class TrySpec<in C>(internal val body: TryBuilder<C>.() -> Unit) : StepSpec<C, Try>()

@DslBuilder
interface GetBuilder<out C> {
    val context: C

    fun get(value: String)

    fun resource(value: String)

    fun version(value: String)

    fun passed(value: String)

    fun passed(passed: List<String>)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun trigger(value: Boolean)

    fun <T : Step> on_success(spec: StepSpec<C, T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<C, T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<C, T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<C, T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)

    fun include(body: GetBuilder<C>.() -> Unit)

    fun include(spec: GetSpec<C>)

    fun <C2> include(context: C2, body: GetBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: GetSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: GetBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: GetSpec<C2>)
}

@DslBuilder
interface PutBuilder<out C> {
    val context: C

    fun put(value: String)

    fun resource(value: String)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun get_params(key: String, value: Any)

    fun get_params(pair: Pair<String, Any>)

    fun get_params(get_params: Map<String, Any>)

    fun <T : Step> on_success(spec: StepSpec<C, T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<C, T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<C, T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<C, T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)

    fun include(body: PutBuilder<C>.() -> Unit)

    fun include(spec: PutSpec<C>)

    fun <C2> include(context: C2, body: PutBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: PutSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: PutBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: PutSpec<C2>)
}

@DslBuilder
interface TaskBuilder<out C> {
    val context: C

    fun task(value: String)

    fun config(body: TaskConfigBuilder<C>.() -> Unit)

    fun config(spec: TaskConfigSpec<C>)

    fun config(ref: TaskConfigRef)

    fun config(value: TaskConfig)

    fun file(value: String)

    fun privileged(value: String)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun image(value: String)

    fun input_mapping(key: String, value: String)

    fun input_mapping(pair: Pair<String, String>)

    fun input_mapping(input_mapping: Map<String, String>)

    fun output_mapping(key: String, value: String)

    fun output_mapping(pair: Pair<String, String>)

    fun output_mapping(output_mapping: Map<String, String>)

    fun <T : Step> on_success(spec: StepSpec<C, T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<C, T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<C, T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<C, T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)

    fun include(body: TaskBuilder<C>.() -> Unit)

    fun include(spec: TaskSpec<C>)

    fun <C2> include(context: C2, body: TaskBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskSpec<C2>)
}

@DslBuilder
interface AggregateBuilder<out C> {
    val context: C

    fun <T : Step> aggregate(spec: StepSpec<C, T>)

    fun <T : Step> aggregate(ref: StepRef<T>)

    fun <T : Step> aggregate(value: T)

    fun <T : Step> on_success(spec: StepSpec<C, T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<C, T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<C, T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<C, T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)

    fun include(body: AggregateBuilder<C>.() -> Unit)

    fun include(spec: AggregateSpec<C>)

    fun <C2> include(context: C2, body: AggregateBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: AggregateSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: AggregateBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: AggregateSpec<C2>)
}

@DslBuilder
interface DoBuilder<out C> {
    val context: C

    fun <T : Step> `do`(spec: StepSpec<C, T>)

    fun <T : Step> `do`(ref: StepRef<T>)

    fun <T : Step> `do`(value: T)

    fun <T : Step> on_success(spec: StepSpec<C, T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<C, T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<C, T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<C, T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)

    fun include(body: DoBuilder<C>.() -> Unit)

    fun include(spec: DoSpec<C>)

    fun <C2> include(context: C2, body: DoBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: DoSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: DoBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: DoSpec<C2>)
}

@DslBuilder
interface TryBuilder<out C> {
    val context: C

    fun <T : Step> `try`(spec: StepSpec<C, T>)

    fun <T : Step> `try`(ref: StepRef<T>)

    fun <T : Step> `try`(value: T)

    fun <T : Step> on_success(spec: StepSpec<C, T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<C, T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<C, T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<C, T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)

    fun include(body: TryBuilder<C>.() -> Unit)

    fun include(spec: TrySpec<C>)

    fun <C2> include(context: C2, body: TryBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TrySpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TryBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TrySpec<C2>)
}

sealed class StepRef<T : Step> {
    internal abstract val key: String
}

class GetRef(override val key: String) : StepRef<Get>()

class PutRef(override val key: String) : StepRef<Put>()

class TaskRef(override val key: String) : StepRef<Task>()

class AggregateRef(override val key: String) : StepRef<Aggregate>()

class DoRef(override val key: String) : StepRef<Do>()

class TryRef(override val key: String) : StepRef<Try>()

internal sealed class StepShell

internal data class GetShell(
        var get: Scaffold<String>? = null,
        var resource: Scaffold<String>? = null,
        var version: Scaffold<String>? = null,
        var passed: List<Scaffold<String>>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var trigger: Scaffold<Boolean>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Get> {
    override suspend fun resolve(registry: Registry): Get {
        checkNotNull(get) { "Get is missing the get property" }
        coroutineScope {
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Get(
            get!!.let{ it.resolve(registry) },
            resource?.let{ it.resolve(registry) },
            version?.let{ it.resolve(registry) },
            passed.orEmpty().let{ it.map { it.resolve(registry) } },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            trigger?.let{ it.resolve(registry) },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class PutShell(
        var put: Scaffold<String>? = null,
        var resource: Scaffold<String>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var get_params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Put> {
    override suspend fun resolve(registry: Registry): Put {
        checkNotNull(put) { "Put is missing the put property" }
        coroutineScope {
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Put(
            put!!.let{ it.resolve(registry) },
            resource?.let{ it.resolve(registry) },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            get_params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class TaskShell(
        var task: Scaffold<String>? = null,
        var config: Scaffold<TaskConfig>? = null,
        var file: Scaffold<String>? = null,
        var privileged: Scaffold<String>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var image: Scaffold<String>? = null,
        var input_mapping: Map<Scaffold<String>, Scaffold<String>>? = null,
        var output_mapping: Map<Scaffold<String>, Scaffold<String>>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Task> {
    override suspend fun resolve(registry: Registry): Task {
        checkNotNull(task) { "Task is missing the task property" }
        checkNotNull(config) { "Task is missing the config property" }
        coroutineScope {
            config?.let{ launch { it.resolve(registry) } }
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Task(
            task!!.let{ it.resolve(registry) },
            config!!.let{ it.resolve(registry) },
            file?.let{ it.resolve(registry) },
            privileged?.let{ it.resolve(registry) },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            image?.let{ it.resolve(registry) },
            input_mapping.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            output_mapping.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class AggregateShell(
        var aggregate: List<Scaffold<Step>>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Aggregate> {
    override suspend fun resolve(registry: Registry): Aggregate {
        coroutineScope {
            aggregate?.let{ it.forEach { launch { it.resolve(registry) } } }
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Aggregate(
            aggregate.orEmpty().let{ it.map { it.resolve(registry) } },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class DoShell(
        var `do`: List<Scaffold<Step>>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Do> {
    override suspend fun resolve(registry: Registry): Do {
        coroutineScope {
            `do`?.let{ it.forEach { launch { it.resolve(registry) } } }
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Do(
            `do`.orEmpty().let{ it.map { it.resolve(registry) } },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class TryShell(
        var `try`: Scaffold<Step>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Try> {
    override suspend fun resolve(registry: Registry): Try {
        checkNotNull(`try`) { "Try is missing the try property" }
        coroutineScope {
            `try`?.let{ launch { it.resolve(registry) } }
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Try(
            `try`!!.let{ it.resolve(registry) },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class GetShellBuilder<out C>(override val context: C, internal var shell: GetShell = GetShell()) : GetBuilder<C> {
    override fun get(value: String) {
        shell = shell.copy(get = Wrapper(value))
    }

    override fun resource(value: String) {
        shell = shell.copy(resource = Wrapper(value))
    }

    override fun version(value: String) {
        shell = shell.copy(version = Wrapper(value))
    }

    override fun passed(value: String) {
        shell = shell.copy(passed = shell.passed.orEmpty() + Wrapper(value))
    }

    override fun passed(passed: List<String>) {
        shell = shell.copy(passed = shell.passed.orEmpty() + passed.map { Wrapper(it) })
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun trigger(value: Boolean) {
        shell = shell.copy(trigger = Wrapper(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = Deferred(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        shell = shell.copy(ensure = Wrapper(value))
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun timeout(value: String) {
        shell = shell.copy(timeout = Wrapper(value))
    }

    override fun attempts(value: Int) {
        shell = shell.copy(attempts = Wrapper(value))
    }

    override fun include(body: GetBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: GetSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: GetBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: GetSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: GetBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: GetSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): GetShellBuilder<C2> = GetShellBuilder(context, shell)

    private fun <C2> merge(other: GetShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class PutShellBuilder<out C>(override val context: C, internal var shell: PutShell = PutShell()) : PutBuilder<C> {
    override fun put(value: String) {
        shell = shell.copy(put = Wrapper(value))
    }

    override fun resource(value: String) {
        shell = shell.copy(resource = Wrapper(value))
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun get_params(key: String, value: Any) {
        shell = shell.copy(get_params = shell.get_params.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun get_params(pair: Pair<String, Any>) {
        shell = shell.copy(get_params = shell.get_params.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun get_params(get_params: Map<String, Any>) {
        shell = shell.copy(get_params = shell.get_params.orEmpty() + get_params.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = Deferred(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        shell = shell.copy(ensure = Wrapper(value))
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun timeout(value: String) {
        shell = shell.copy(timeout = Wrapper(value))
    }

    override fun attempts(value: Int) {
        shell = shell.copy(attempts = Wrapper(value))
    }

    override fun include(body: PutBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: PutSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: PutBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: PutSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: PutBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: PutSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): PutShellBuilder<C2> = PutShellBuilder(context, shell)

    private fun <C2> merge(other: PutShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class TaskShellBuilder<out C>(override val context: C, internal var shell: TaskShell = TaskShell()) : TaskBuilder<C> {
    override fun task(value: String) {
        shell = shell.copy(task = Wrapper(value))
    }

    override fun config(body: TaskConfigBuilder<C>.() -> Unit) {
        shell = shell.copy(config = TaskConfigScaffolder<C>(TaskConfigSpec<C>(body)).createScaffold(context))
    }

    override fun config(spec: TaskConfigSpec<C>) {
        shell = shell.copy(config = TaskConfigScaffolder<C>(spec).createScaffold(context))
    }

    override fun config(ref: TaskConfigRef) {
        shell = shell.copy(config = Deferred(ref.key))
    }

    override fun config(value: TaskConfig) {
        shell = shell.copy(config = Wrapper(value))
    }

    override fun file(value: String) {
        shell = shell.copy(file = Wrapper(value))
    }

    override fun privileged(value: String) {
        shell = shell.copy(privileged = Wrapper(value))
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun image(value: String) {
        shell = shell.copy(image = Wrapper(value))
    }

    override fun input_mapping(key: String, value: String) {
        shell = shell.copy(input_mapping = shell.input_mapping.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun input_mapping(pair: Pair<String, String>) {
        shell = shell.copy(input_mapping = shell.input_mapping.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun input_mapping(input_mapping: Map<String, String>) {
        shell = shell.copy(input_mapping = shell.input_mapping.orEmpty() + input_mapping.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun output_mapping(key: String, value: String) {
        shell = shell.copy(output_mapping = shell.output_mapping.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun output_mapping(pair: Pair<String, String>) {
        shell = shell.copy(output_mapping = shell.output_mapping.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun output_mapping(output_mapping: Map<String, String>) {
        shell = shell.copy(output_mapping = shell.output_mapping.orEmpty() + output_mapping.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = Deferred(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        shell = shell.copy(ensure = Wrapper(value))
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun timeout(value: String) {
        shell = shell.copy(timeout = Wrapper(value))
    }

    override fun attempts(value: Int) {
        shell = shell.copy(attempts = Wrapper(value))
    }

    override fun include(body: TaskBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TaskSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TaskBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TaskSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TaskBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TaskSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskShellBuilder<C2> = TaskShellBuilder(context, shell)

    private fun <C2> merge(other: TaskShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class AggregateShellBuilder<out C>(override val context: C, internal var shell: AggregateShell = AggregateShell()) : AggregateBuilder<C> {
    override fun <T : Step> aggregate(spec: StepSpec<C, T>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> aggregate(ref: StepRef<T>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + Deferred(ref.key))
    }

    override fun <T : Step> aggregate(value: T) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + Wrapper(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = Deferred(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        shell = shell.copy(ensure = Wrapper(value))
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun timeout(value: String) {
        shell = shell.copy(timeout = Wrapper(value))
    }

    override fun attempts(value: Int) {
        shell = shell.copy(attempts = Wrapper(value))
    }

    override fun include(body: AggregateBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AggregateSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AggregateBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AggregateSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AggregateBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AggregateSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AggregateShellBuilder<C2> = AggregateShellBuilder(context, shell)

    private fun <C2> merge(other: AggregateShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class DoShellBuilder<out C>(override val context: C, internal var shell: DoShell = DoShell()) : DoBuilder<C> {
    override fun <T : Step> `do`(spec: StepSpec<C, T>) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> `do`(ref: StepRef<T>) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + Deferred(ref.key))
    }

    override fun <T : Step> `do`(value: T) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + Wrapper(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = Deferred(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        shell = shell.copy(ensure = Wrapper(value))
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun timeout(value: String) {
        shell = shell.copy(timeout = Wrapper(value))
    }

    override fun attempts(value: Int) {
        shell = shell.copy(attempts = Wrapper(value))
    }

    override fun include(body: DoBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: DoSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: DoBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: DoSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: DoBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: DoSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DoShellBuilder<C2> = DoShellBuilder(context, shell)

    private fun <C2> merge(other: DoShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class TryShellBuilder<out C>(override val context: C, internal var shell: TryShell = TryShell()) : TryBuilder<C> {
    override fun <T : Step> `try`(spec: StepSpec<C, T>) {
        shell = shell.copy(`try` = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> `try`(ref: StepRef<T>) {
        shell = shell.copy(`try` = Deferred(ref.key))
    }

    override fun <T : Step> `try`(value: T) {
        shell = shell.copy(`try` = Wrapper(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = Deferred(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        shell = shell.copy(ensure = Wrapper(value))
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun timeout(value: String) {
        shell = shell.copy(timeout = Wrapper(value))
    }

    override fun attempts(value: Int) {
        shell = shell.copy(attempts = Wrapper(value))
    }

    override fun include(body: TryBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TrySpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TryBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TrySpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TryBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TrySpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TryShellBuilder<C2> = TryShellBuilder(context, shell)

    private fun <C2> merge(other: TryShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class StepScaffolder<in C, out T : Step>(internal val spec: StepSpec<C, T>) : Scaffolder<C, T> {
    override fun createScaffold(context: C): Scaffold<T> {
        val result = when (spec) {
            is GetSpec<C> -> GetScaffolder(spec).createScaffold(context)
            is PutSpec<C> -> PutScaffolder(spec).createScaffold(context)
            is TaskSpec<C> -> TaskScaffolder(spec).createScaffold(context)
            is AggregateSpec<C> -> AggregateScaffolder(spec).createScaffold(context)
            is DoSpec<C> -> DoScaffolder(spec).createScaffold(context)
            is TrySpec<C> -> TryScaffolder(spec).createScaffold(context)
        }
        return result as Scaffold<T>
    }
}

class GetScaffolder<in C>(internal val spec: GetSpec<C>) : Scaffolder<C, Get> {
    override fun createScaffold(context: C): Scaffold<Get> {
        val builder = GetShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class PutScaffolder<in C>(internal val spec: PutSpec<C>) : Scaffolder<C, Put> {
    override fun createScaffold(context: C): Scaffold<Put> {
        val builder = PutShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TaskScaffolder<in C>(internal val spec: TaskSpec<C>) : Scaffolder<C, Task> {
    override fun createScaffold(context: C): Scaffold<Task> {
        val builder = TaskShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class AggregateScaffolder<in C>(internal val spec: AggregateSpec<C>) : Scaffolder<C, Aggregate> {
    override fun createScaffold(context: C): Scaffold<Aggregate> {
        val builder = AggregateShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class DoScaffolder<in C>(internal val spec: DoSpec<C>) : Scaffolder<C, Do> {
    override fun createScaffold(context: C): Scaffold<Do> {
        val builder = DoShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TryScaffolder<in C>(internal val spec: TrySpec<C>) : Scaffolder<C, Try> {
    override fun createScaffold(context: C): Scaffold<Try> {
        val builder = TryShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
