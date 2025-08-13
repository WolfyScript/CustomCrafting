plugins {
    id("com.wolfyscript.devtools.docker.run")
    id("com.wolfyscript.devtools.docker.minecraft_servers")
}

val debugPort: String = System.getenv("debugPort") ?: "5006"
val debugPortMapping = "${debugPort}:${debugPort}"

minecraftServers {
    serversDir.set(file("${System.getProperty("user.home")}/minecraft/test_servers_v5"))
}

minecraftDockerRun {
    // By default the container is removed when stopped.
    // That makes it impossible to know why a container may fail to start.
    // In that case disable it to debug and delete container manually.
//    clean.set(false)
    env.putAll(
        mapOf(
            // Limit each container memory
            "MEMORY" to "2G",
            "EULA" to "true",
//            "DEBUG" to "true",
            // Allows to attach the IntelliJ Debugger
            "JVM_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${debugPort}",
            "FORCE_REDOWNLOAD" to "false"
        )
    )
    arguments(
        // Constrain to only use 2 cpus to better align with real production servers
        "--cpus",
        "2",
        // allow console interactivity (docker attach)
        "-it"
    )
    ports.set(listOf(debugPortMapping))
}
