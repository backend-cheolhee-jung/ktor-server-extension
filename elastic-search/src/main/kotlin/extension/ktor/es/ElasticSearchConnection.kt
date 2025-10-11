package extension.ktor.es

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient

lateinit var elasticsearchAsyncClient: ElasticsearchAsyncClient

data class ElasticSearchConnection(
    val host: String,
    val port: Int,
)

object ElasticSearchConnectManager {
    var jsonMapper: JacksonJsonpMapper = JacksonJsonpMapper()

    fun connect(connection: ElasticSearchConnection) {
        val restClient = RestClient.builder(
            HttpHost.create("${connection.host}:${connection.port}"),
        ).build()

        val transport = RestClientTransport(restClient, jsonMapper)

        elasticsearchAsyncClient = ElasticsearchAsyncClient(transport)
    }
}