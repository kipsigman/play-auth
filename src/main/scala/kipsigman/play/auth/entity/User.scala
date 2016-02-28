package kipsigman.play.auth.entity

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import kipsigman.domain.entity.Role

case class User(
  id: Option[Int],
  loginInfo: LoginInfo,
  firstName: Option[String],
  lastName: Option[String],
  email: String,
  avatarURL: Option[String],
  roles: Set[Role] = Set(Role.Member)) extends kipsigman.domain.entity.User with Identity
