package kipsigman.play.auth.entity

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }

import kipsigman.domain.entity.IdEntity

case class User(
  id: Option[Int],
  loginInfo: LoginInfo,
  firstName: Option[String],
  lastName: Option[String],
  email: String,
  avatarURL: Option[String],
  roles: Set[Role] = Set(Role.Member)) extends IdEntity with Identity {
  
  final lazy val name: String = firstName.getOrElse("") + lastName.map(ln => s" $ln").getOrElse("")
  
  /**
   * Checks for Role. Administrators are assumed to have all roles.
   */
  final def hasRole(role: Role): Boolean = roles.contains(role) || roles.contains(Role.Administrator)
  
  final lazy val isAdministrator: Boolean = hasRole(Role.Administrator)
  final lazy val isEditor: Boolean = hasRole(Role.Editor)
  final lazy val isMember: Boolean = hasRole(Role.Member)
}

sealed abstract class Role(val name: String) {
  override def toString: String = name
}

object Role {
  // Site admin
  case object Administrator extends Role("administrator")
  // Can edit ArticleTemplates
  case object Editor extends Role("editor")
  // Can customize and manage Articles
  case object Member extends Role("member")
  
  val all: Set[Role] = Set(Administrator, Editor, Member)
  
  def apply(name: String): Role = {
    all.find(s => s.name == name) match {
      case Some(role) => role
      case None => throw new IllegalArgumentException(s"Invalid Role: $name")
    }
  }
}
