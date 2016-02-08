package kipsigman.play.auth.entity

import kipsigman.domain.entity.IdEntity

trait UserOptionOwnedEntity extends IdEntity {
  def userIdOption: Option[Int]
  
  def isOwnedBy(userOption: Option[User]): Boolean = UserOptionOwnedEntity.isOwnedBy(userIdOption, userOption)
}

object UserOptionOwnedEntity {
  def isOwnedBy(entityUserIdOption: Option[Int], userOption: Option[User]): Boolean = (entityUserIdOption, userOption) match {
    case (Some(definedUserId), Some(user)) => user.isId(definedUserId)
    case _ => false
  }
}

trait UserOwnedEntity extends UserOptionOwnedEntity {
  def userId: Int
  
  override def userIdOption = Option(userId)
}