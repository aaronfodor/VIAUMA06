package hu.gyeben.communityparking.server

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import hu.gyeben.communityparking.server.models.db.Reports
import hu.gyeben.communityparking.server.models.db.Users
import io.ktor.application.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

@KtorExperimentalAPI
fun Application.initDatabase() {
    val configPath = environment.config.property("hikariconfig").getString()
    val dbConfig = HikariConfig(configPath)
    val dataSource = HikariDataSource(dbConfig)
    Database.connect(dataSource)
    createTables()
    LoggerFactory.getLogger(Application::class.simpleName).info("Connected to database")
}

private fun createTables() = transaction {
    SchemaUtils.create(Users)
    SchemaUtils.create(Reports)
}