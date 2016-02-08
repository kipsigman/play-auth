package kipsigman.play.auth.entity

import org.scalatest.Matchers
import org.scalatest.WordSpec

import kipsigman.play.auth.TestData

class UserOptionOwnedEntitySpec extends WordSpec with Matchers with TestData {
  val userOptionOwnedEntity: UserOptionOwnedEntity = new UserOptionOwnedEntity {
    override val id = Option(1)
    override val userIdOption = user.id
  }
  val anonymousEntity: UserOptionOwnedEntity = new UserOptionOwnedEntity {
    override val id = Option(1)
    override val userIdOption = None
  }
  
  "isOwnedBy" should {
    "return true if userId is defined and matches user" in {
      userOptionOwnedEntity.userIdOption.isDefined shouldBe true
      user.id.isDefined shouldBe true
      userOptionOwnedEntity.userIdOption shouldBe user.id
      userOptionOwnedEntity.isOwnedBy(Option(user)) shouldBe true
    }
    "return false if userId is defined but does not match user" in {
      userOptionOwnedEntity.userIdOption.isDefined shouldBe true
      user2.id.isDefined shouldBe true
      userOptionOwnedEntity.isOwnedBy(Option(user2)) shouldBe false
    }
    "return false if userId is not defined" in {
      anonymousEntity.userIdOption shouldBe None
      anonymousEntity.isOwnedBy(Option(user)) shouldBe false
    }
  }
}