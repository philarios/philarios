package io.philarios.concourse.v0

import io.philarios.schema.v0.*

object ConcourseSchemaSpec : SchemaSpec<Any?>({
    name("Concourse")
    pkg("io.philarios.concourse.v0")

    type(Struct {
        name("Concourse")
        field(Field {
            name("teams")
            type(ListType {
                type(RefType {
                    name("Team")
                })
            })
        })
    })

    type(Struct {
        name("Team")
        field(Field {
            name("name")
            type(StringType {})
        })
        field(Field {
            name("pipelines")
            type(ListType {
                type(RefType {
                    name("Pipeline")
                })
            })
        })
    })

    type(Struct {
        name("Pipeline")
        field(Field {
            name("name")
            type(StringType {})
        })
        field(Field {
            name("jobs")
            type(ListType {
                type(RefType {
                    name("Job")
                })
            })
        })
        field(Field {
            name("resources")
            type(ListType {
                type(RefType {
                    name("Resource")
                })
            })
        })
        field(Field {
            name("resource_types")
            type(ListType {
                type(RefType {
                    name("ResourceType")
                })
            })
        })
        field(Field {
            name("groups")
            type(ListType {
                type(RefType {
                    name("Group")
                })
            })
        })
    })

    type(Struct {
        name("Job")
        field(Field {
            name("name")
            type(StringType {})
        })
        field(Field {
            name("plan")
            type(ListType {
                type(RefType {
                    name("Step")
                })
            })
        })
        field(Field {
            name("serial")
            type(OptionType {
                type(BooleanType {})
            })
        })
        field(Field {
            name("build_logs_to_retain")
            type(OptionType {
                type(IntType {})
            })
        })
        field(Field {
            name("serial_groups")
            type(ListType {
                type(StringType {})
            })
        })
        field(Field {
            name("max_in_flight")
            type(OptionType {
                type(IntType {})
            })
        })
        field(Field {
            name("public")
            type(OptionType {
                type(BooleanType {})
            })
        })
        field(Field {
            name("disable_manual_trigger")
            type(OptionType {
                type(BooleanType {})
            })
        })
        field(Field {
            name("interruptible")
            type(OptionType {
                type(BooleanType {})
            })
        })
        field(Field {
            name("on_success")
            type(OptionType {
                type(RefType {
                    name("Step")
                })
            })
        })
        field(Field {
            name("on_failure")
            type(OptionType {
                type(RefType {
                    name("Step")
                })
            })
        })
        field(Field {
            name("on_abort")
            type(OptionType {
                type(RefType {
                    name("Step")
                })
            })
        })
        field(Field {
            name("ensure")
            type(OptionType {
                type(RefType {
                    name("Step")
                })
            })
        })
    })

    type(Union {
        name("Step")
        shape(Struct {
            name("Get")
            field(Field {
                name("get")
                type(StringType {})
            })
            field(Field {
                name("resource")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("version")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("passed")
                type(ListType {
                    type(StringType {})
                })
            })
            field(Field {
                name("params")
                type(MapType {
                    keyType(StringType {})
                    valueType(AnyType {})
                })
            })
            field(Field {
                name("trigger")
                type(OptionType {
                    type(BooleanType {})
                })
            })

            field(Field {
                name("on_success")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("on_failure")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("on_abort")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("ensure")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("tags")
                type(ListType {
                    type(StringType {})
                })
            })
            field(Field {
                name("timeout")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("attempts")
                type(OptionType {
                    type(IntType {})
                })
            })
        })
        shape(Struct {
            name("Put")
            field(Field {
                name("put")
                type(StringType {})
            })
            field(Field {
                name("resource")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("params")
                type(MapType {
                    keyType(StringType {})
                    valueType(AnyType {})
                })
            })
            field(Field {
                name("get_params")
                type(MapType {
                    keyType(StringType {})
                    valueType(AnyType {})
                })
            })

            field(Field {
                name("on_success")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("on_failure")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("on_abort")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("ensure")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("tags")
                type(ListType {
                    type(StringType {})
                })
            })
            field(Field {
                name("timeout")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("attempts")
                type(OptionType {
                    type(IntType {})
                })
            })
        })
        shape(Struct {
            name("Task")
            field(Field {
                name("task")
                type(StringType {})
            })
            field(Field {
                name("config")
                type(RefType {
                    name("TaskConfig")
                })
            })
            field(Field {
                name("file")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("privileged")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("params")
                type(MapType {
                    keyType(StringType {})
                    valueType(AnyType {})
                })
            })
            field(Field {
                name("image")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("input_mapping")
                type(MapType {
                    keyType(StringType {})
                    valueType(StringType {})
                })
            })
            field(Field {
                name("output_mapping")
                type(MapType {
                    keyType(StringType {})
                    valueType(StringType {})
                })
            })

            field(Field {
                name("on_success")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("on_failure")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("on_abort")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("ensure")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("tags")
                type(ListType {
                    type(StringType {})
                })
            })
            field(Field {
                name("timeout")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("attempts")
                type(OptionType {
                    type(IntType {})
                })
            })
        })
        shape(Struct {
            name("Aggregate")
            field(Field {
                name("aggregate") // TODO fix reserved words
                type(ListType {
                    type(RefType {
                        name("Step")
                    })
                })
            })

            field(Field {
                name("on_success")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("on_failure")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("on_abort")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("ensure")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("tags")
                type(ListType {
                    type(StringType {})
                })
            })
            field(Field {
                name("timeout")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("attempts")
                type(OptionType {
                    type(IntType {})
                })
            })
        })
        shape(Struct {
            name("Do")
            field(Field {
                name("doIt") // TODO fix reserved words
                type(ListType {
                    type(RefType {
                        name("Step")
                    })
                })
            })

            field(Field {
                name("on_success")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("on_failure")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("on_abort")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("ensure")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("tags")
                type(ListType {
                    type(StringType {})
                })
            })
            field(Field {
                name("timeout")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("attempts")
                type(OptionType {
                    type(IntType {})
                })
            })
        })
        shape(Struct {
            name("Try")
            field(Field {
                name("tryIt") // TODO fix reserved words
                type(RefType {
                    name("Step")
                })
            })

            field(Field {
                name("on_success")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("on_failure")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("on_abort")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("ensure")
                type(OptionType {
                    type(RefType {
                        name("Step")
                    })
                })
            })
            field(Field {
                name("tags")
                type(ListType {
                    type(StringType {})
                })
            })
            field(Field {
                name("timeout")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("attempts")
                type(OptionType {
                    type(IntType {})
                })
            })
        })
    })

    type(Struct {
        name("TaskConfig")
        field(Field {
            name("platform")
            type(StringType {})
        })
        field(Field {
            name("image_resource")
            type(RefType {
                name("TaskResource")
            })
        })
        field(Field {
            name("rootfs_uri")
            type(OptionType {
                type(StringType {})
            })
        })
        field(Field {
            name("inputs")
            type(ListType {
                type(RefType {
                    name("TaskInput")
                })
            })
        })
        field(Field {
            name("outputs")
            type(ListType {
                type(RefType {
                    name("TaskOutput")
                })
            })
        })
        field(Field {
            name("caches")
            type(ListType {
                type(RefType {
                    name("TaskCache")
                })
            })
        })
        field(Field {
            name("run")
            type(OptionType {
                type(RefType {
                    name("TaskRunConfig")
                })
            })
        })
        field(Field {
            name("params")
            type(MapType {
                keyType(StringType {})
                valueType(AnyType {})
            })
        })
    })

    type(Struct {
        name("TaskResource")
        field(Field {
            name("type")
            type(StringType {})
        })
        field(Field {
            name("source")
            type(MapType {
                keyType(StringType {})
                valueType(AnyType {})
            })
        })
        field(Field {
            name("params")
            type(MapType {
                keyType(StringType {})
                valueType(AnyType {})
            })
        })
        field(Field {
            name("version")
            type(MapType {
                keyType(StringType {})
                valueType(StringType {})
            })
        })
    })

    type(Struct {
        name("TaskInput")
        field(Field {
            name("name")
            type(StringType {})
        })
        field(Field {
            name("path")
            type(OptionType {
                type(StringType {})
            })
        })
        field(Field {
            name("optional")
            type(BooleanType {})
        })
    })

    type(Struct {
        name("TaskOutput")
        field(Field {
            name("name")
            type(StringType {})
        })
        field(Field {
            name("path")
            type(OptionType {
                type(StringType {})
            })
        })
    })

    type(Struct {
        name("TaskCache")
        field(Field {
            name("path")
            type(StringType {})
        })
    })

    type(Struct {
        name("TaskRunConfig")
        field(Field {
            name("path")
            type(StringType {})
        })
        field(Field {
            name("args")
            type(ListType {
                type(StringType {})
            })
        })
        field(Field {
            name("dir")
            type(OptionType {
                type(StringType {})
            })
        })
        field(Field {
            name("user")
            type(OptionType {
                type(StringType {})
            })
        })
    })

    type(Struct {
        name("Resource")
        field(Field {
            name("name")
            type(StringType {})
        })
        field(Field {
            name("type")
            type(StringType {})
        })
        field(Field {
            name("source")
            type(MapType {
                keyType(StringType {})
                valueType(AnyType {})
            })
        })
        field(Field {
            name("check_every")
            type(OptionType {
                type(StringType {})
            })
        })
        field(Field {
            name("tags")
            type(ListType {
                type(StringType {})
            })
        })
        field(Field {
            name("webhook_token")
            type(OptionType {
                type(StringType {})
            })
        })
    })

    type(Struct {
        name("ResourceType")
        field(Field {
            name("name")
            type(StringType {})
        })
        field(Field {
            name("type")
            type(StringType {})
        })
        field(Field {
            name("source")
            type(MapType {
                keyType(StringType {})
                valueType(AnyType {})
            })
        })
        field(Field {
            name("privileged")
            type(OptionType {
                type(BooleanType {})
            })
        })
        field(Field {
            name("params")
            type(MapType {
                keyType(StringType {})
                valueType(AnyType {})
            })
        })
        field(Field {
            name("tags")
            type(ListType {
                type(StringType {})
            })
        })
    })

    type(Struct {
        name("Group")
        field(Field {
            name("name")
            type(StringType {})
        })
        field(Field {
            name("jobs")
            type(ListType {
                type(StringType {})
            })
        })
        field(Field {
            name("resources")
            type(ListType {
                type(StringType {})
            })
        })
    })

})