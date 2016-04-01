package kipsigman.play.auth

import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import kipsigman.play.auth.entity.User
import kipsigman.play.auth.repository.UserRepository

@Singleton
class UserService @Inject() (userRepository: UserRepository)(implicit ec: ExecutionContext) extends IdentityService[User] {
  
  def find(id: Int): Future[Option[User]] = userRepository.find(id)

  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userRepository.find(loginInfo)

  def save(user: User) = userRepository.save(user)

  /**
   * Saves the social profile for a user.
   *
   * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
   *
   * @param profile The social profile to save.
   * @return The user for whom the profile was saved.
   */
  def save(profile: CommonSocialProfile) = {
    userRepository.find(profile.loginInfo).flatMap {
      case Some(user) => // Update user with profile
        userRepository.save(user.copy(
          firstName = profile.firstName,
          lastName = profile.lastName,
          email = profile.email.getOrElse(user.email),
          avatarURL = profile.avatarURL
        ))
      case None => // Insert a new user
        userRepository.save(User(
          None,
          loginInfo = profile.loginInfo,
          firstName = profile.firstName,
          lastName = profile.lastName,
          email = profile.email.get, // Assume this is required
          avatarURL = profile.avatarURL
        ))
    }
  }
}
