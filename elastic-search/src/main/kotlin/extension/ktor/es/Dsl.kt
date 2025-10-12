package extension.ktor.es

import co.elastic.clients.elasticsearch.core.DeleteRequest
import co.elastic.clients.elasticsearch.core.GetRequest
import co.elastic.clients.elasticsearch.core.IndexRequest
import co.elastic.clients.elasticsearch.core.UpdateRequest

/**TODO:
 *  index create 하기.
 *  index update 하기.
 *  index delete 하기.
 *  index 검색 DSL 만들기.
 */
fun main() {
    val goods = mapOf(
        "id" to 1,
        "name" to "Sample Goods",
        "price" to 100,
        "description" to "This is a sample goods description.",
        "adminMemo" to "Admin memo here"
    )

    val indexRequest = IndexRequest.of {
        it.index("goods")
            .document(goods)
    }

    elasticsearchAsyncClient.index(indexRequest).whenComplete { response, error ->
        if (error != null) {
            println("Error indexing document: ${error.message}")
        } else {
            println("Document indexed with ID: ${response.id()}")
        }
    }

    val getRequest = GetRequest.of {
        it.index("goods").id("1")
    }

    elasticsearchAsyncClient.get(getRequest, Map::class.java).whenComplete { response, error ->
        if (error != null) {
            println("Error getting document: ${error.message}")
        } else if (response.found()) {
            println("Document found: ${response.source()}")
        } else {
            println("Document not found")
        }
    }

    val deleteRequest = DeleteRequest.of {
        it.index("goods").id("1")
    }

    elasticsearchAsyncClient.delete(deleteRequest).whenComplete { response, error ->
        if (error != null) {
            println("Error deleting document: ${error.message}")
        } else {
            println("Document deleted with ID: ${response.id()}")
        }
    }

    val updateRequest = UpdateRequest.of<Any, Any> {
        it.index("goods")
            .id("1")
            .doc(mapOf("price" to 150))
    }

    elasticsearchAsyncClient.update<Any, Any>(updateRequest, Any::class.java).whenComplete { response, error ->
        if (error != null) {
            println("Error updating document: ${error.message}")
        } else {
            println("Document updated with ID: ${response.id()}")
        }
    }
}