package kipsigman.play.auth.mvc

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator

import play.api.Configuration
import play.api.Mode
import play.api.OptionalSourceMapper
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.MessagesApi
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import play.api.routing.Router
import play.core.SourceMapper

import kipsigman.play.auth.entity.User

/**
 * Easy to use error handler that extends Play's DefaultHttpErrorHandler but also provides Silhouette implementations.
 */
@Singleton
class DefaultAuthErrorHandler @Inject() (
  environment: play.api.Environment,
  config: Configuration,
  sourceMapper: OptionalSourceMapper,
  router: Provider[Router],
  val messagesApi: MessagesApi,
  protected val env: Environment[User, CookieAuthenticator])
  extends DefaultHttpErrorHandler(environment, config, sourceMapper, router)
  with AuthErrorHandler with DefaultErrorResults {
  
  ////////////////////
  // 4xx Client Errors
  ////////////////////
  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = statusCode match {
    case UNAUTHORIZED => {
      userAwareResult(request) {implicit r =>
        notAuthenticated
      }
    }
    case _ => super.onClientError(request, statusCode, message)
  }
  
  override protected def onForbidden(request: RequestHeader, message: String): Future[Result] = {
    userAwareResult(request) {implicit r =>
      notAuthorized(Option(message))
    }
  }
  
  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    userAwareResult(request) {implicit r =>
      environment.mode match {
        case Mode.Prod => notFound
        case _ => NotFound(views.html.defaultpages.devNotFound(request.method, request.uri, Option(router.get)))
      }
    }
  }
  
  ////////////////////
  // 5xx Server Errors
  ////////////////////
  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    userAwareResult(request) {implicit r =>
      serverError(exception)
    }
  }
}