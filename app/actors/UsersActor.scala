package actors

import actors.UsersActor._
import akka.actor.Actor

case class User(username: String, lastName: String, firstName: String)

class UsersActor extends Actor {

  def behaviour(users: List[User]): Actor.Receive = {
    case GetList => sender() ! users

    case GetByUsername(username) => sender() ! users.find(_.username == username).map(FoundUser).getOrElse(UserNotFoundError)

    case Add(username, lastName, firstName) =>
      context.become(behaviour(users :+ User(username, lastName, firstName)))
      sender() ! UserSuccessfullyCreated

    case Remove(username) =>
      users.find(_.username == username) match {
        case Some(u) =>
          context.become(behaviour(users.filterNot(_ == u)))
          sender() ! UserSuccessfullyRemoved
        case None => sender() ! UserNotFoundError
      }
  }

  override def receive = behaviour(Nil)
}

object UsersActor {

  case class Add(username: String, lastName: String, firstName: String)

  case class Remove(username: String)

  case class GetByUsername(username: String)

  case object GetList


  sealed abstract class UsersMessage

  case class FoundUser(user: User) extends UsersMessage

  case object UserSuccessfullyCreated extends UsersMessage

  case object UserSuccessfullyRemoved extends UsersMessage

  case object UserNotFoundError extends UsersMessage

  case object UserAlreadyExistError extends UsersMessage

}