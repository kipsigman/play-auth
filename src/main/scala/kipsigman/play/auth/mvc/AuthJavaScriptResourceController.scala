package kipsigman.play.auth.mvc

import scala.concurrent.ExecutionContext

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator

import kipsigman.play.auth.entity.User
import kipsigman.play.mvc.JavaScriptResourceController
import play.api.i18n.MessagesApi
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.twirl.api.JavaScript

/**
 * Extends kipsigman.play.mvc.JavaScriptResourceController by adding action for making authenticated user
 * available to JavaScript assets.
 *
 * To use, mixin this trait and define routes. For example:
 * {{{
 * object Application extends Controller with AuthJavaScriptResourceController {
 *
 *   override protected def javaScriptReverseRoutes: Seq[JavaScriptReverseRoute] = Seq(
 *     routes.javascript.Assets.at,
 *     ...
 *   )
 *
 * }
 * }}}
 * {{{
 * GET  /app/config.js    controllers.AppController.configJs
 * GET  /app/messages.js  controllers.AppController.messagesJs
 * GET  /app/routes.js    controllers.AppController.routesJs
 * GET  /app/user.js      controllers.AppController.userJs
 * }}}
 */
abstract class AuthJavaScriptResourceController (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator])
  (implicit ec: ExecutionContext) extends AuthController(messagesApi, env) with JavaScriptResourceController {
  
  // Dependencies
  this: ErrorResults =>
  
  // Naming configuration
  protected def userObjectName: String = "user"
  
  // Actions
  def userJs = UserAwareAction {implicit request =>
    request.identity match {
      case Some(user) => {
        val objectName = namespacedName(userObjectName)
        val userJson: JsValue = Json.toJson(user)
        val userJsonStr = Json.stringify(userJson)
        val javascriptStr = s"var $objectName=$userJsonStr"
        val javascript = JavaScript(javascriptStr)
        Ok(javascript)    
      }
      case None => Ok(JavaScript(""))
    }
  }
}