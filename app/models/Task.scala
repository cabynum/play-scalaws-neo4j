package models

import play.api._
import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.concurrent._
import play.Logger

case class Task(id: Long, label: String)

object Task {

	val neoRestBase = "http://localhost:7474/db/data/"
	val create = "node"
	val find = "index/auto/node/name/"
	val indexingCheck = "index/auto/node/status"
	val indexedProperties = "index/auto/node/properties"

	def all(): List[Task] = Nil

	def find(name: String) {

		//If finding a node with spaces in the name, spaces should be replaced by %20
		val resultNode: Promise[Response] = {
			WS.url((neoRestBase + find + name)).withHeaders("Accept" -> "application/json").get()
		}

		Logger.info("Specific Node - " + resultNode.value.get.body)

	}

	def printIndexingStatus() {

		val status: Promise[Response] = {
			WS.url((neoRestBase + indexingCheck)).withHeaders("Accept" -> "application/json"
				).get()
		}

		Logger.info("Auto Index Turned On? - " + status.value.get.body)
	}

	def printIndexedProperties() {

		val properties: Promise[Response] = {
			WS.url((neoRestBase + indexedProperties)).withHeaders("Accept" -> "application/json"
				).get()
		}

		Logger.info("Properties Being Indexed - " + properties.value.get.body)
	}

	def create(label: String) {

		val props = toJson(Map("name" -> label))

		val wsresult: Promise[Response] = {
			WS.url((neoRestBase + create)).withHeaders("Accept" -> "application/json",
				"Content-Type" -> "application/json").post(stringify(props))
		}

		Logger.info("Task creation response is: " + wsresult.value.get.body)

	}

	def delete(id: Long) {}
}