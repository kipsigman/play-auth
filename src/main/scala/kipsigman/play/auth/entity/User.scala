package kipsigman.play.auth.entity

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import kipsigman.domain.entity.Role
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class User(
  id: Option[Int],
  loginInfo: LoginInfo,
  firstName: Option[String],
  lastName: Option[String],
  email: String,
  avatarURL: Option[String],
  roles: Set[Role] = Set(Role.Member)) extends kipsigman.domain.entity.User with Identity

object User {
  implicit val roleWrites: Writes[Role] = new Writes[Role] {
    def writes(role: Role) = JsString(role.name)
  }
  
  implicit val writes: Writes[User] = (
    (JsPath \ "id").writeNullable[Int] and
    (JsPath \ "firstName").writeNullable[String] and
    (JsPath \ "lastName").writeNullable[String] and
    (JsPath \ "email").write[String] and
    (JsPath \ "avatarURL").writeNullable[String] and
    (JsPath \ "roles").write[Set[Role]]
  )((user: User) => (user.id, user.firstName, user.lastName, user.email, user.avatarURL, user.roles))
}