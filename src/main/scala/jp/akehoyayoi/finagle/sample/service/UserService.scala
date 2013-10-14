package jp.akehoyayoi.finagle.sample.service

import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.util.CharsetUtil.UTF_8
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.exp.mysql._
import com.twitter.finagle.http.{Request, Response, RichHttp,Http}
import jp.akehoyayoi.finagle.sample.model.User

/**
 * Created with IntelliJ IDEA.
 * User: okayayohei
 * Date: 2013/10/14
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */

class UserServiceFactory(client : Client) {

  def createService = new CreateService
  def insertService(id : Long , name : String) = new InsertService(id , name)
  def updateService(id : Long , name : String) = new UpdateService(id , name)
  def selectService = new SelectService

  def createUser = User.create(client)
  def insertUser(id : Long , name : String) = User.insert(client,List(User(id,name)))
  def updateUser(id : Long , name : String) = User.update(client,List(User(id,name)))
  def selectUser = User.select(client)

  class CreateService extends Service[Request, Response] {
    def apply(request: Request) = {
      createUser.map(p => {
        val response = Response()
        response.setContent(copiedBuffer("create", UTF_8))
        response
      })
    }
  }

  class InsertService(id : Long , name : String) extends Service[Request, Response] {
    def apply(request: Request) = {
      insertUser(id , name).map(p => {
        val response = Response()
        response.setContent(copiedBuffer("insert", UTF_8))
        response
      })
    }
  }

  class UpdateService(id : Long , name : String) extends Service[Request, Response] {
    def apply(request : Request) = {
      updateUser(id,name).map(p => {
        val response = Response()
        response.setContent(copiedBuffer("update", UTF_8))
        response
      })
    }
  }

  class SelectService extends Service[Request,Response] {
    def apply(request : Request) = {
      selectUser.map(p => {
        val response = Response()
        response.setContent(copiedBuffer("[" + p.mkString(",") + "]", UTF_8))
        response
      })
    }
  }
}
