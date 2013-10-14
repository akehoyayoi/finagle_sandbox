package jp.akehoyayoi.finagle.sample

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import java.net.InetSocketAddress
import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http.{Request, Response, RichHttp,Http}
import jp.akehoyayoi.finagle.sample.model.User
import com.twitter.finagle.exp.mysql._
import java.util.logging.{Logger, Level}
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.path._
import jp.akehoyayoi.finagle.sample.service.UserServiceFactory
import scala.xml.XML
import scala.io.Source

object HttpServer {

  def main(args: Array[String]) {
    val source = Source.fromURL(getClass.getResource("/db.xml"))
    val db = XML.loadString(source.mkString)
    val name = (db \ "name").text
    val password = (db \ "password").text
    val database = (db \ "database").text
    val host = new InetSocketAddress("localhost", 3306)
    val client = Client(host.getHostName+":"+host.getPort, name, password, database, Level.OFF)
    val userServiceFactory = new UserServiceFactory(client)

    val routingService = RoutingService.byPathObject {
      case Root / "create" => userServiceFactory.createService
      case Root / "insert" / Long(userId) / name => userServiceFactory.insertService(userId,name)
      case Root / "update" / Long(userId) / name => userServiceFactory.updateService(userId,name)
      case Root / "select" => userServiceFactory.selectService
    }

    val server : Server = ServerBuilder()
      .codec(RichHttp[Request](Http()))
      .bindTo(new InetSocketAddress(8080))
      .name("httpserver")
      .build(routingService)
  }

}
