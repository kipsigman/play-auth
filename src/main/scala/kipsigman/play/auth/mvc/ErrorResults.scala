package kipsigman.play.auth.mvc

import play.api.i18n.I18nSupport
import play.api.mvc.Controller
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import play.twirl.api.Html

import kipsigman.play.auth.entity.User

/**
 * Base controller for Authenticated/Authorized Actions.
 */
trait ErrorResults extends Controller with I18nSupport {
  
  ////////////////////
  // 4xx Client Errors
  ////////////////////
  /**
   * Implement in end app.
   */
  protected def badRequestView(message: Option[String] = None)(implicit request: RequestHeader, user: Option[User]): Html

  protected def badRequest(message: Option[String] = None)(implicit request: RequestHeader, user: Option[User]): Result =
    BadRequest(badRequestView(message))
    
  /**
   * Implement in end app.
   */
  protected def notAuthenticatedView(implicit request: RequestHeader, user: Option[User]): Html

  protected def notAuthenticated(implicit request: RequestHeader, user: Option[User]): Result =
    Unauthorized(notAuthenticatedView)
  
  
  /**
   * Implement in end app.
   */
  protected def notAuthorizedView(message: Option[String] = None)(implicit request: RequestHeader, user: Option[User]): Html

  protected def notAuthorized(message: Option[String] = None)(implicit request: RequestHeader, user: Option[User]): Result =
    Forbidden(notAuthorizedView(message))
  
  
  /**
   * Implement in end app.
   */
  protected def notFoundView(implicit request: RequestHeader, user: Option[User]): Html

  protected def notFound(implicit request: RequestHeader, user: Option[User]): Result =
    NotFound(notFoundView)
    
  /**
   * Implement in end app.
   */
  protected def clientErrorView(statusCode: Int, message: Option[String] = None)(implicit request: RequestHeader, user: Option[User]): Html
  
  protected def clientError(statusCode: Int, message: Option[String] = None)(implicit request: RequestHeader, user: Option[User]): Result =
    Status(statusCode)(clientErrorView(statusCode, message))
  
  
  ////////////////////
  // 5xx Server Errors
  ////////////////////
  /**
   * Implement in end app.
   */
  protected def serverErrorView(exception: Throwable)(implicit request: RequestHeader, user: Option[User]): Html

  protected def serverError(exception: Throwable)(implicit request: RequestHeader, user: Option[User]): Result =
    InternalServerError(serverErrorView(exception))
}

trait DefaultErrorResults extends ErrorResults {
  import play.api.http.HttpErrorHandlerExceptions
  
  override protected def badRequestView(message: Option[String] = None)(implicit request: RequestHeader, user: Option[User]): Html =
    views.html.defaultpages.badRequest(request.method, request.uri, message.getOrElse(""))
  
  override protected def notAuthenticatedView(implicit request: RequestHeader, user: Option[User]): Html =
    views.html.defaultpages.unauthorized()
  
  override protected def notAuthorizedView(message: Option[String] = None)(implicit request: RequestHeader, user: Option[User]): Html =
    views.html.defaultpages.unauthorized()
  
  override protected def notFoundView(implicit request: RequestHeader, user: Option[User]): Html =
    views.html.defaultpages.notFound(request.method, request.uri)
  
  override protected def clientErrorView(statusCode: Int, message: Option[String] = None)
    (implicit request: RequestHeader, user: Option[User]): Html =
    views.html.defaultpages.badRequest(request.method, request.uri, message.getOrElse(""))
  
  override protected def serverErrorView(exception: Throwable)(implicit request: RequestHeader, user: Option[User]): Html = {
    val usefulException = HttpErrorHandlerExceptions.throwableToUsefulException(None, true, exception)
    views.html.defaultpages.error(usefulException)
  }
  
}