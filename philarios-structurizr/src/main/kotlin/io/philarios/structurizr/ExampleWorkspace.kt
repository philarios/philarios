package io.philarios.structurizr


val workspace = WorkspaceSpec<Any?> {
    name("Test")
    description("This is a test workspace")

    model {
        people {
            name("Admin")
            description("An admin user of the system")
            location(Location.Internal)
        }
        people {
            name("User")
            description("A normal user")
            location(Location.External)
        }
        softwareSystem {
            name("Todo App")
            description("A simple todo app")

            container {
                name("app")
                description("displays the todo list")
                technology("Android")
            }
            container {
                name("api")
                description("answers requests from the app")
                technology("Java")

                component {
                    name("controller")
                    description("answers requests from the app")
                    technology("Spring MVC")
                }
                component {
                    name("service")
                    description("contains the logic and use cases")
                    technology("Java")
                }
                component {
                    name("repository")
                    description("queries the database")
                    technology("JPA")
                }
            }
            container {
                name("database")
                description("the main database")
                technology("MySQL")
            }
        }
    }
}
