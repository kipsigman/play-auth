package kipsigman.play.auth.mvc

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.SecuredErrorHandler
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import play.api.http.HttpErrorHandler
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import play.api.mvc.AnyContentAsEmpty
import play.api.mvc.Request
import play.api.mvc.RequestHeader
import play.api.mvc.Result

import kipsigman.play.auth.entity.User

/**
 * Error handler that extends Silhouette's SecuredErrorHandler.
 */
trait AuthErrorHandler extends SecuredErrorHandler with HttpErrorHandler with Silhouette[User, CookieAuthenticator] {
  this: ErrorResults =>
    
  protected implicit def request2UserOption(implicit request: UserAwareRequest[play.api.mvc.AnyContent]): Option[User] = request.identity
  protected implicit def request2User(implicit request: SecuredRequest[play.api.mvc.AnyContent]): User = request.identity
  protected implicit def user2UserOption(implicit user: User): Option[User] = Option(user)
    
  override def onNotAuthenticated(request: RequestHeader, messages: Messages): Option[Future[Result]] = {
    val result = userAwareResult(request) {implicit r =>
      notAuthenticated
    }
    
    Some(result)
  }
  
  override def onNotAuthorized(request: RequestHeader, messages: Messages): Option[Future[Result]] = {
    val result = userAwareResult(request) {implicit r =>
      notAuthorized(None)
    }
    
    Some(result)
  }
  
  protected def userAwareResult(requestHeader: RequestHeader)(block: UserAwareRequest[AnyContent] => Result): Future[Result] = 
    UserAwareAction(block).apply(Request(requestHeader, AnyContentAsEmpty))
    
}

/**
 * Error handler that extends Silhouette's SecuredErrorHandler and Play's HttpErrorHandler.
 * Must mixin ErrorResults implementation.
 */
trait AuthHttpErrorHandler extends AuthErrorHandler with HttpErrorHandler {
  this: ErrorResults =>
    
  override def onClientError(request: RequestHeader, statusCode: Int, message: String = ""): Future[Result] = {
    val messageOption = Option(message)
    
    userAwareResult(request) {implicit r =>
      statusCode match {
        case BAD_REQUEST => badRequest(messageOption)
        case UNAUTHORIZED => notAuthenticated
        case FORBIDDEN => notAuthorized(messageOption)
        case NOT_FOUND => notFound
        case _ => clientError(statusCode, messageOption)
      }
    }
  }
  
  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    userAwareResult(request) {implicit r =>
      logger.error("Internal Server Error", exception)
      serverError(exception)
    }
  }
}