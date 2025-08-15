package love.forte.simbot.codegen.codegen


/**
 *
 * @author ForteScarlet
 */
enum class SpringComponent(
    val display: String,
    val group: String,
    val artifactId: String,
    /**
     * 用在 libs.versions.toml 里的名字，例如 `jackson-module-kotlin`
     */
    val depName: String,
    /**
     * 用在build.gradle.kts 等引用 libs.versions.toml 依赖的路径，例如 `jackson.module.kotlin`
     */
    val libPath: String = depName.replace('-', '.'),
    val selectable: Boolean = true,
    val forJava: Boolean = true,
    val forKotlin: Boolean = true,
) {
    // for Kotlin
    JACKSON_MODULE_KOTLIN(
        display = "jackson-module-kotlin",
        group = "com.fasterxml.jackson.module",
        artifactId = "jackson-module-kotlin",
        depName = "jackson-module-kotlin",
        selectable = false
    ),
    KOTLIN_REFLECT(
        display = "kotlin-reflect",
        group = "org.jetbrains.kotlin",
        artifactId = "kotlin-reflect",
        depName = "kotlin-reflect",
        selectable = false
    ),

    // selectable components

    WEB(
        display = "web",
        group = "org.springframework.boot",
        artifactId = "spring-boot-starter-web",
        depName = "spring-web",
    ),

    // TODO reactive dependencies - will be added later
//    WEB_FLUX(
//        "webflux",
//        "org.springframework.boot",
//        "spring-boot-starter-webflux"
//    ),
//    DATA_R2DBC(
//        "data-r2dbc",
//        "org.springframework.boot",
//        "spring-boot-starter-data-r2dbc"
//    ),

    // Data access
    DATA_JDBC(
        display = "data-jdbc",
        group = "org.springframework.boot",
        artifactId = "spring-boot-starter-data-jdbc",
        depName = "spring-data-jdbc"
    ),
    DATA_JPA(
        display = "data-jpa",
        group = "org.springframework.boot",
        artifactId = "spring-boot-starter-data-jpa",
        depName = "spring-data-jpa"
    ),
    DATA_JOOQ(
        display = "data-jooq",
        group = "org.springframework.boot",
        artifactId = "spring-boot-starter-jooq",
        depName = "spring-data-jooq"
    ),

    // Database drivers
    MYSQL(
        display = "mysql",
        group = "com.mysql",
        artifactId = "mysql-connector-j",
        depName = "mysql-connector"
    ),
    H2(
        display = "h2",
        group = "com.h2database",
        artifactId = "h2",
        depName = "h2-database"
    ),
    POSTGRESQL(
        display = "postgresql",
        group = "org.postgresql",
        artifactId = "postgresql",
        depName = "postgresql-driver"
    ),
    SQL_SERVER(
        display = "sqlserver",
        group = "com.microsoft.sqlserver",
        artifactId = "mssql-jdbc",
        depName = "mssql-jdbc"
    ),
    ORACLE(
        display = "oracle",
        group = "com.oracle.database.jdbc",
        artifactId = "ojdbc8",
        depName = "oracle-jdbc"
    ),

    // NoSQL and caching
    DATA_REDIS(
        display = "data-redis",
        group = "org.springframework.boot",
        artifactId = "spring-boot-starter-data-redis",
        depName = "spring-data-redis"
    ),
    DATA_MONGODB(
        display = "data-mongodb",
        group = "org.springframework.boot",
        artifactId = "spring-boot-starter-data-mongodb",
        depName = "spring-data-mongodb"
    ),
    DATA_ELASTICSEARCH(
        display = "data-elasticsearch",
        group = "org.springframework.boot",
        artifactId = "spring-boot-starter-data-elasticsearch",
        depName = "spring-data-elasticsearch"
    ),

    // Communication
    WEBSOCKET(
        display = "websocket",
        group = "org.springframework.boot",
        artifactId = "spring-boot-starter-websocket",
        depName = "spring-websocket"
    ),
    AMQP(
        display = "amqp",
        group = "org.springframework.boot",
        artifactId = "spring-boot-starter-amqp",
        depName = "spring-amqp"
    )
}
