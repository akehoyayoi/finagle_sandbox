package jp.akehoyayoi.finagle.sample.model

import com.twitter.util.Future
import com.twitter.finagle.exp.mysql._
import java.net.InetSocketAddress

/**
 * Created with IntelliJ IDEA.
 * User: okayayohei
 * Date: 2013/10/14
 * Time: 13:33
 * To change this template use File | Settings | File Templates.
 */
case class User(id : Long , name : String) {
  override def toString = {
    def q(s: String) = "'" + s + "'"
    "{ userId : " + id + ", name : " + q(name) + "}"
  }
}

object User {
  val createSQL =
    """
     CREATE TABLE IF NOT EXISTS User (
       `id` bigint(20) unsigned NOT NULL,
       `name` varchar(40) DEFAULT NULL,
       PRIMARY KEY (`id`)
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8
    """
  val insertSQL = "INSERT INTO User(id , name) VALUE (?,?)"
  val updateSQL = "UPDATE User SET name = ? WHERE id = ?"
  val selectSQL = "SELECT * FROM User"

  def create(client : Client) : Future[Result] = {
    client.query(createSQL)
  }

  def insert(client : Client , users : List[User]) : Future[Seq[Result]] = {
    client.prepare(insertSQL) flatMap { ps =>
      val results = users.map(user => {
        ps.parameters = Array(user.id,user.name)
        client.execute(ps)
      })
      Future.collect(results) ensure {
        client.closeStatement(ps)
      }
    }
  }

  def update(client : Client , users : List[User]) : Future[Seq[Result]] = {
    client.prepare(updateSQL) flatMap { ps =>
      val results = users.map(user => {
        ps.parameters = Array(user.name,user.id)
        client.execute(ps)
      })
      Future.collect(results) ensure {
        client.closeStatement(ps)
      }
    }
  }

  def select(client : Client) : Future[Seq[User]] = {
    client.select(selectSQL) { row =>
      val LongValue(id) = row("id").get
      val StringValue(name) = row("name").get
      User(id,name)
    }
  }
}