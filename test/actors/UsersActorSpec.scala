package actors

import actors.UsersActor.{Add, GetList, UserSuccessfullyCreated}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest._

class UsersActorSpec
  extends TestKit(ActorSystem("MySpec"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with BeforeAndAfterEach {

  var usersActor: ActorRef = _

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  override def beforeEach {
    usersActor = system.actorOf(Props[UsersActor])
  }

  "An Users Actor" must {

    "send empty list" in {
      usersActor ! GetList
      expectMsg(Nil)
    }

    "add new user to list" in {
      usersActor ! Add("vader", "Zhuma", "Vova")

      expectMsg(UserSuccessfullyCreated)
    }

    "add user " in {
      usersActor ! Add("vader", "Zhuma", "Vova")

      expectMsg(UserSuccessfullyCreated)

      usersActor ! GetList

      val users = expectMsgType[List[User]]

      users.length should be (1)
      users.head.username should be ("vader")
    }
  }

}
