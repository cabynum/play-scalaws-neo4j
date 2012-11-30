package models

import play.api._
import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.concurrent._
import play.Logger

case class Task(id: Long, label: String)

object Task {
	
	def all(): List[Task] = Nil

	def getByName(name: String) {

	}

	def create(label: String) {

		Logger.info("About to create task: " + label);

		val props = toJson(Map("name" -> label))

		val autoIndexStatus: Promise[Response] = {
			WS.url("http://localhost:7474/db/data/index/auto/node/status").withHeaders("Accept" -> "application/json"
				).get()
		}

		Logger.info("Auto Index Turned On? - " + autoIndexStatus.value.get.body)

		val indexedProperties: Promise[Response] = {
			WS.url("http://localhost:7474/db/data/index/auto/node/properties").withHeaders("Accept" -> "application/json"
				).get()
		}

		Logger.info("Properties Being Indexed - " + indexedProperties.value.get.body)

		val wsresult: Promise[Response] = {
			WS.url("http://localhost:7474/db/data/node").withHeaders("Accept" -> "application/json",
				"Content-Type" -> "application/json").post(stringify(props))
		}

		Logger.info("WS response is: " + wsresult.value.get.body)

		//If finding a node with spaces in the name, spaces should be replaced by %20

		val nodeByNameURL = "http://localhost:7474/db/data/index/auto/node/name/" + label

		val resultNode: Promise[Response] = {
			WS.url(nodeByNameURL).withHeaders("Accept" -> "application/json").get()
		}

		Logger.info("Specific Node - " + resultNode.value.get.body)

		
	}

	def delete(id: Long) {}
}