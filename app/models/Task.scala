package models

import play.api._
import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.concurrent._
import play.Logger
import scala.collection.mutable.ListBuffer
import net.liftweb.json.DefaultFormats
import net.liftweb.json._

case class Task(id: Long, name: String)

object Task {

	val neoRestBase = "http://localhost:7474/db/data"
	val create = "/node"
	val batch = "/batch"
	val find = "/index/auto/node/name/"
	val indexingCheck = "/index/auto/node/status"
	val indexedProperties = "/index/auto/node/properties"

	implicit val formats = net.liftweb.json.DefaultFormats

	def twoTasks(): List[Task] = {

		var tasks = new ListBuffer[Task]

		//batch REST Neo4j request

		val props1 = toJson(Map("method" -> "GET", "to" -> (find + "task1")))
		val props2 = toJson(Map("method" -> "GET", "to" -> (find + "CBynum")))

		Logger.info("Retrieving two Task nodes with string : ")
		Logger.info("[" + stringify(props1) + "," + stringify(props2) + "]")

		val wsresult: Promise[Response] = {
			WS.url((neoRestBase + batch)).withHeaders("Accept" -> "application/json",
				"Content-Type" -> "application/json").post(("[" + stringify(props1) + ","
					+ stringify(props2) + "]"))
		}

		Logger.info("Retrieve twoTasks response is: " + wsresult.value.get.body)

		val json = net.liftweb.json.parse(wsresult.value.get.body)
		val elements = (json \\ "data").children

		for (node <- elements){
			val x = node.extract[Task]
			tasks += x
		}

		//build list of Tasks

		tasks.toList
	}

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

	def create(name: String) {

		val props = toJson(Map("name" -> name, "id" -> "0"))

		val wsresult: Promise[Response] = {
			WS.url((neoRestBase + create)).withHeaders("Accept" -> "application/json",
				"Content-Type" -> "application/json").post(stringify(props))
		}

		Logger.info("Task creation response is: " + wsresult.value.get.body)

	}

	def delete(id: Long) {}
}