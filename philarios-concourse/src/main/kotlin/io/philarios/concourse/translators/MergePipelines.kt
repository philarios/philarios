//package io.philarios.concourse.translators
//
//import io.philarios.concourse.v0.Concourse
//import io.philarios.concourse.v0.Pipeline
//import io.philarios.concourse.v0.Team
//import io.philarios.core.v0.Translator
//
//object MergePipelines : Translator<Concourse, Concourse> {
//    override fun translate(context: Concourse): Concourse {
//        return context.let {
//            it.copy(teams = it.teams
//                    .merge({ name }, Team::merge)
//                    .map {
//                        it.copy(pipelines = it.pipelines.merge({ name }, Pipeline::merge))
//                    })
//        }
//    }
//}
//
//private fun <T, C : Comparable<C>> List<T>.merge(selector: T.() -> C, merge: T.(T) -> T): List<T> {
//    return sortedBy { it.selector() }
//            .mergeSorted(selector, merge)
//}
//
//private fun <T, C : Comparable<C>> List<T>.mergeSorted(selector: T.() -> C, merge: T.(T) -> T): List<T> {
//    if (isEmpty()) {
//        return emptyList()
//    }
//
//    val output = mutableListOf<T>()
//    var current = this[0]
//    for (i in 1 until size) {
//        val next = this[i]
//        if (current.selector() == next.selector()) {
//            current = current.merge(next)
//        } else {
//            output += current
//            current = next
//        }
//    }
//    output += current
//    return output
//}
//
//private fun Team.merge(other: Team): Team {
//    return copy(pipelines = pipelines + other.pipelines)
//}
//
//private fun Pipeline.merge(other: Pipeline): Pipeline {
//    return copy(
//            jobs = jobs + other.jobs,
//            resources = resources + other.resources,
//            resource_types = resource_types + other.resource_types,
//            groups = groups + other.groups
//    )
//}