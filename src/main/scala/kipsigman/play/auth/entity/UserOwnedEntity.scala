package kipsigman.play.auth.entity

import kipsigman.domain.entity.IdEntity

trait UserOptionOwnedEntity extends IdEntity {
  def userIdOption: Option[Int]
  
  def isOwnedBy(userOption: Option[User]): Boolean = UserOptionOwnedEntity.isOwnedBy(userIdOption, userOption)
  
  def canEdit(userOption: Option[User]): Boolean = UserOptionOwnedEntity.canEdit(userIdOption, userOption)
}

object UserOptionOwnedEntity {
  def isOwnedBy(entityUserIdOption: Option[Int], userOption: Option[User]): Boolean = (entityUserIdOption, userOption) match {
    case (Some(definedUserId), Some(user)) => user.isId(definedUserId)
    case _ => false
  }
  
  def canEdit(entityUserIdOption: Option[Int], userOption: Option[User]): Boolean = {
    (entityUserIdOption, userOption) match {
      // Entity has no owner, any User can edit
      case (None, _) => true
      // User has owner, only owner can edit
      case (Some(definedId), _) => isOwnedBy(entityUserIdOption, userOption)
    }
  }
}

trait UserOwnedEntity extends UserOptionOwnedEntity {
  def userId: Int
  
  override def userIdOption = Option(userId)
}