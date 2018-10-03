package io.philarios.concourse.v0

import io.philarios.schema.v0.*

object ConcourseSchemaSpec : SchemaSpec<Any?>({
    name("Concourse")
    pkg("io.philarios.concourse.v0")

    type(StructSpec {
        name("Concourse")
        field(FieldSpec {
            name("teams")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Team")
                })
            })
        })
    })

    type(StructSpec {
        name("Team")
        field(FieldSpec {
            name("name")
            type(StringTypeSpec {})
        })
        field(FieldSpec {
            name("pipelines")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Pipeline")
                })
            })
        })
    })

    type(StructSpec {
        name("Pipeline")
        field(FieldSpec {
            name("name")
            type(StringTypeSpec {})
        })
        field(FieldSpec {
            name("jobs")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Job")
                })
            })
        })
        field(FieldSpec {
            name("resources")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Resource")
                })
            })
        })
        field(FieldSpec {
            name("resource_types")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("ResourceType")
                })
            })
        })
        field(FieldSpec {
            name("groups")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Group")
                })
            })
        })
    })

    type(StructSpec {
        name("Job")
        field(FieldSpec {
            name("name")
            type(StringTypeSpec {})
        })
        field(FieldSpec {
            name("plan")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Step")
                })
            })
        })
        field(FieldSpec {
            name("serial")
            type(OptionTypeSpec {
                type(BooleanTypeSpec {})
            })
        })
        field(FieldSpec {
            name("build_logs_to_retain")
            type(OptionTypeSpec {
                type(IntTypeSpec {})
            })
        })
        field(FieldSpec {
            name("serial_groups")
            type(ListTypeSpec {
                type(StringTypeSpec {})
            })
        })
        field(FieldSpec {
            name("max_in_flight")
            type(OptionTypeSpec {
                type(IntTypeSpec {})
            })
        })
        field(FieldSpec {
            name("public")
            type(OptionTypeSpec {
                type(BooleanTypeSpec {})
            })
        })
        field(FieldSpec {
            name("disable_manual_trigger")
            type(OptionTypeSpec {
                type(BooleanTypeSpec {})
            })
        })
        field(FieldSpec {
            name("interruptible")
            type(OptionTypeSpec {
                type(BooleanTypeSpec {})
            })
        })
        field(FieldSpec {
            name("on_success")
            type(OptionTypeSpec {
                type(RefTypeSpec {
                    name("Step")
                })
            })
        })
        field(FieldSpec {
            name("on_failure")
            type(OptionTypeSpec {
                type(RefTypeSpec {
                    name("Step")
                })
            })
        })
        field(FieldSpec {
            name("on_abort")
            type(OptionTypeSpec {
                type(RefTypeSpec {
                    name("Step")
                })
            })
        })
        field(FieldSpec {
            name("ensure")
            type(OptionTypeSpec {
                type(RefTypeSpec {
                    name("Step")
                })
            })
        })
    })

    type(UnionSpec {
        name("Step")
        shape(StructSpec {
            name("Get")
            field(FieldSpec {
                name("get")
                type(StringTypeSpec {})
            })
            field(FieldSpec {
                name("resource")
                type(OptionTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("version")
                type(OptionTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("passed")
                type(ListTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("params")
                type(MapTypeSpec {
                    keyType(StringTypeSpec {})
                    valueType(AnyTypeSpec {})
                })
            })
            field(FieldSpec {
                name("trigger")
                type(OptionTypeSpec {
                    type(BooleanTypeSpec {})
                })
            })

            field(FieldSpec {
                name("on_success")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("on_failure")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("on_abort")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("ensure")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("tags")
                type(ListTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("timeout")
                type(OptionTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("attempts")
                type(OptionTypeSpec {
                    type(IntTypeSpec {})
                })
            })
        })
        shape(StructSpec {
            name("Put")
            field(FieldSpec {
                name("put")
                type(StringTypeSpec {})
            })
            field(FieldSpec {
                name("resource")
                type(OptionTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("params")
                type(MapTypeSpec {
                    keyType(StringTypeSpec {})
                    valueType(AnyTypeSpec {})
                })
            })
            field(FieldSpec {
                name("get_params")
                type(MapTypeSpec {
                    keyType(StringTypeSpec {})
                    valueType(AnyTypeSpec {})
                })
            })

            field(FieldSpec {
                name("on_success")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("on_failure")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("on_abort")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("ensure")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("tags")
                type(ListTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("timeout")
                type(OptionTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("attempts")
                type(OptionTypeSpec {
                    type(IntTypeSpec {})
                })
            })
        })
        shape(StructSpec {
            name("Task")
            field(FieldSpec {
                name("task")
                type(StringTypeSpec {})
            })
            field(FieldSpec {
                name("config")
                type(RefTypeSpec {
                    name("TaskConfig")
                })
            })
            field(FieldSpec {
                name("file")
                type(OptionTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("privileged")
                type(OptionTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("params")
                type(MapTypeSpec {
                    keyType(StringTypeSpec {})
                    valueType(AnyTypeSpec {})
                })
            })
            field(FieldSpec {
                name("image")
                type(OptionTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("input_mapping")
                type(MapTypeSpec {
                    keyType(StringTypeSpec {})
                    valueType(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("output_mapping")
                type(MapTypeSpec {
                    keyType(StringTypeSpec {})
                    valueType(StringTypeSpec {})
                })
            })

            field(FieldSpec {
                name("on_success")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("on_failure")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("on_abort")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("ensure")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("tags")
                type(ListTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("timeout")
                type(OptionTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("attempts")
                type(OptionTypeSpec {
                    type(IntTypeSpec {})
                })
            })
        })
        shape(StructSpec {
            name("Aggregate")
            field(FieldSpec {
                name("aggregate") // TODO fix reserved words
                type(ListTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })

            field(FieldSpec {
                name("on_success")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("on_failure")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("on_abort")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("ensure")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("tags")
                type(ListTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("timeout")
                type(OptionTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("attempts")
                type(OptionTypeSpec {
                    type(IntTypeSpec {})
                })
            })
        })
        shape(StructSpec {
            name("Do")
            field(FieldSpec {
                name("doIt") // TODO fix reserved words
                type(ListTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })

            field(FieldSpec {
                name("on_success")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("on_failure")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("on_abort")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("ensure")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("tags")
                type(ListTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("timeout")
                type(OptionTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("attempts")
                type(OptionTypeSpec {
                    type(IntTypeSpec {})
                })
            })
        })
        shape(StructSpec {
            name("Try")
            field(FieldSpec {
                name("tryIt") // TODO fix reserved words
                type(RefTypeSpec {
                    name("Step")
                })
            })

            field(FieldSpec {
                name("on_success")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("on_failure")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("on_abort")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("ensure")
                type(OptionTypeSpec {
                    type(RefTypeSpec {
                        name("Step")
                    })
                })
            })
            field(FieldSpec {
                name("tags")
                type(ListTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("timeout")
                type(OptionTypeSpec {
                    type(StringTypeSpec {})
                })
            })
            field(FieldSpec {
                name("attempts")
                type(OptionTypeSpec {
                    type(IntTypeSpec {})
                })
            })
        })
    })

    type(StructSpec {
        name("TaskConfig")
        field(FieldSpec {
            name("platform")
            type(StringTypeSpec {})
        })
        field(FieldSpec {
            name("image_resource")
            type(RefTypeSpec {
                name("TaskResource")
            })
        })
        field(FieldSpec {
            name("rootfs_uri")
            type(OptionTypeSpec {
                type(StringTypeSpec {})
            })
        })
        field(FieldSpec {
            name("inputs")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("TaskInput")
                })
            })
        })
        field(FieldSpec {
            name("outputs")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("TaskOutput")
                })
            })
        })
        field(FieldSpec {
            name("caches")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("TaskCache")
                })
            })
        })
        field(FieldSpec {
            name("run")
            type(OptionTypeSpec {
                type(RefTypeSpec {
                    name("TaskRunConfig")
                })
            })
        })
        field(FieldSpec {
            name("params")
            type(MapTypeSpec {
                keyType(StringTypeSpec {})
                valueType(AnyTypeSpec {})
            })
        })
    })

    type(StructSpec {
        name("TaskResource")
        field(FieldSpec {
            name("type")
            type(StringTypeSpec {})
        })
        field(FieldSpec {
            name("source")
            type(MapTypeSpec {
                keyType(StringTypeSpec {})
                valueType(AnyTypeSpec {})
            })
        })
        field(FieldSpec {
            name("params")
            type(MapTypeSpec {
                keyType(StringTypeSpec {})
                valueType(AnyTypeSpec {})
            })
        })
        field(FieldSpec {
            name("version")
            type(MapTypeSpec {
                keyType(StringTypeSpec {})
                valueType(StringTypeSpec {})
            })
        })
    })

    type(StructSpec {
        name("TaskInput")
        field(FieldSpec {
            name("name")
            type(StringTypeSpec {})
        })
        field(FieldSpec {
            name("path")
            type(OptionTypeSpec {
                type(StringTypeSpec {})
            })
        })
        field(FieldSpec {
            name("optional")
            type(BooleanTypeSpec {})
        })
    })

    type(StructSpec {
        name("TaskOutput")
        field(FieldSpec {
            name("name")
            type(StringTypeSpec {})
        })
        field(FieldSpec {
            name("path")
            type(OptionTypeSpec {
                type(StringTypeSpec {})
            })
        })
    })

    type(StructSpec {
        name("TaskCache")
        field(FieldSpec {
            name("path")
            type(StringTypeSpec {})
        })
    })

    type(StructSpec {
        name("TaskRunConfig")
        field(FieldSpec {
            name("path")
            type(StringTypeSpec {})
        })
        field(FieldSpec {
            name("args")
            type(ListTypeSpec {
                type(StringTypeSpec {})
            })
        })
        field(FieldSpec {
            name("dir")
            type(OptionTypeSpec {
                type(StringTypeSpec {})
            })
        })
        field(FieldSpec {
            name("user")
            type(OptionTypeSpec {
                type(StringTypeSpec {})
            })
        })
    })

    type(StructSpec {
        name("Resource")
        field(FieldSpec {
            name("name")
            type(StringTypeSpec {})
        })
        field(FieldSpec {
            name("type")
            type(StringTypeSpec {})
        })
        field(FieldSpec {
            name("source")
            type(MapTypeSpec {
                keyType(StringTypeSpec {})
                valueType(AnyTypeSpec {})
            })
        })
        field(FieldSpec {
            name("check_every")
            type(OptionTypeSpec {
                type(StringTypeSpec {})
            })
        })
        field(FieldSpec {
            name("tags")
            type(ListTypeSpec {
                type(StringTypeSpec {})
            })
        })
        field(FieldSpec {
            name("webhook_token")
            type(OptionTypeSpec {
                type(StringTypeSpec {})
            })
        })
    })

    type(StructSpec {
        name("ResourceType")
        field(FieldSpec {
            name("name")
            type(StringTypeSpec {})
        })
        field(FieldSpec {
            name("type")
            type(StringTypeSpec {})
        })
        field(FieldSpec {
            name("source")
            type(MapTypeSpec {
                keyType(StringTypeSpec {})
                valueType(AnyTypeSpec {})
            })
        })
        field(FieldSpec {
            name("privileged")
            type(OptionTypeSpec {
                type(BooleanTypeSpec {})
            })
        })
        field(FieldSpec {
            name("params")
            type(MapTypeSpec {
                keyType(StringTypeSpec {})
                valueType(AnyTypeSpec {})
            })
        })
        field(FieldSpec {
            name("tags")
            type(ListTypeSpec {
                type(StringTypeSpec {})
            })
        })
    })

    type(StructSpec {
        name("Group")
        field(FieldSpec {
            name("name")
            type(StringTypeSpec {})
        })
        field(FieldSpec {
            name("jobs")
            type(ListTypeSpec {
                type(StringTypeSpec {})
            })
        })
        field(FieldSpec {
            name("resources")
            type(ListTypeSpec {
                type(StringTypeSpec {})
            })
        })
    })

})