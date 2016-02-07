package kipsigman.play.auth

import com.mohiva.play.silhouette.api.LoginInfo

import kipsigman.play.auth.entity.Role
import kipsigman.play.auth.entity.User

trait TestData {
  val user = User(Option(66), LoginInfo("", ""), Option("Johnny"), Option("Utah"), "johnny.utah@fbi.gov", None, Set(Role.Member))
  implicit val userOption: Option[User] = Option(user)
  val editor: Option[User] = Option(user.copy(roles = user.roles + Role.Editor))
  
  val user2 = User(Option(67), LoginInfo("", ""), Option("Angelo"), Option("Pappas"), "angelo.pappas@fbi.gov", None, Set())
}