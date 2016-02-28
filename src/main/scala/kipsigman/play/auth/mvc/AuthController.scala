package kipsigman.play.auth.mvc

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import kipsigman.domain.entity.Role
import play.api.i18n.Messages
import play.api.i18n.MessagesApi
import play.api.mvc.Request

import kipsigman.play.auth.entity.User

/**
 * Base controller for Authenticated/Authorized Actions.
 * Must mixin ErrorResults implementation.
 */
abstract class AuthController (
  val messagesApi: MessagesApi,
  protected val env: Environment[User, CookieAuthenticator])
  (implicit ec: ExecutionContext)
  extends AuthHttpErrorHandler {
  this: ErrorResults =>
  
  
  protected object FlashKey {
    val error = "error"
    val info = "info"
    val success = "success"
    val warning = "warning"
  }
  
  /**
 * Only allows those user that have at least a role of the selected.
 * Administrator role is always allowed.
 * Ex: WithRole(Editor, Member)
 * @see http://silhouette.mohiva.com/docs/authorization
 */
  case class WithRole(anyOf: Role*) extends Authorization[User, CookieAuthenticator] {
    def isAuthorized[A](user: User, authenticator: CookieAuthenticator)(implicit r: Request[A], m: Messages) = Future.successful {
      WithRole.isAuthorized(user, anyOf: _*)
    }
  }
  object WithRole {
    def isAuthorized(user: User, anyOf: Role*): Boolean =
      anyOf.intersect(user.roles.toSeq).size > 0 || user.roles.contains(Role.Administrator)
  }
}