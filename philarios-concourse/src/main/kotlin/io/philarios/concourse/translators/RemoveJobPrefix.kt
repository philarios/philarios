//package io.philarios.concourse.translators
//
//import io.philarios.concourse.v0.Concourse
//import io.philarios.concourse.v0.Get
//import io.philarios.concourse.v0.Pipeline
//import io.philarios.core.v0.Translator
//
//object RemoveJobPrefix : Translator<Concourse, Concourse> {
//    override fun translate(context: Concourse): Concourse {
//        return context.let {
//            it.copy(teams = it.teams.map {
//                it.copy(pipelines = it.pipelines.map { it.removeJobPrefix() })
//            })
//        }
//    }
//
//    private fun Pipeline.removeJobPrefix(): Pipeline {
//        val commonPrefix = jobs.map { it.name }.commonPrefix()
//
//        return copy(
//                jobs = jobs.map {
//                    it.copy(
//                            name = it.name.removePrefix(commonPrefix),
//                            plan = it.plan.map {
//                                if (it is Get) {
//                                    it.copy(passed = it.passed.map { it.removePrefix(commonPrefix) })
//                                } else {
//                                    it
//                                }
//                            }
//                    )
//                }
//        )
//    }
//
//    private fun List<String>.commonPrefix(): String {
//        if (isEmpty()) {
//            return ""
//        }
//        return reduce { prefix, string -> prefix.commonPrefixWith(string) }
//    }
//}