package kipsigman.play.auth.mvc

import javax.inject.Provider

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.LoginEvent
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.LogoutEvent
import com.mohiva.play.silhouette.api.SignUpEvent
import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.i18n.MessagesApi
import play.api.mvc._
import play.api.OptionalSourceMapper
import play.api.routing.Router
import play.twirl.api.Html

import kipsigman.play.auth.entity.User
import kipsigman.play.auth.UserService

/**
 * Controller for User Authentication/Authorization flows: Login, Logout, Sign-up, User profile
 * Must mixin ErrorResults implementation.
 */
abstract class UserController (
  config: Config,
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  credentialsProvider: CredentialsProvider,
  avatarService: AvatarService,
  passwordHasher: PasswordHasher,
  clock: Clock)(implicit ec: ExecutionContext) extends AuthController(messagesApi, env) {
  
  this: ErrorResults =>
  
  protected def homeRoute: Call
  protected def signInRoute: Call
  protected def signInView(form: Form[SignInForm.Data])(implicit request: RequestHeader, user: Option[User]): Html
  protected def signUpRoute: Call
  protected def signUpView(form: Form[SignUpForm.Data])(implicit request: RequestHeader, user: Option[User]): Html
  protected def userView(implicit request: RequestHeader, user: User): Html
  
  def user = SecuredAction.async { implicit request =>
    Future.successful(Ok(userView))
  }

  def signIn = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Redirect(homeRoute))
      case None => Future.successful(Ok(signInView(SignInForm.form)))
    }
  }
  
  def signInPost = UserAwareAction.async { implicit request =>
    SignInForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(signInView(form))),
      data => {
        val credentials = Credentials(data.email, data.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          val result = Redirect(homeRoute)
          userService.retrieve(loginInfo).flatMap {
            case Some(user) =>
              env.authenticatorService.create(loginInfo).map {
                case authenticator if data.rememberMe =>
                  authenticator.copy(
                    expirationDateTime = clock.now + config.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry"),
                    idleTimeout = config.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout"),
                    cookieMaxAge = config.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.cookieMaxAge")
                  )
                case authenticator => authenticator
              }.flatMap { authenticator =>
                env.eventBus.publish(LoginEvent(user, request, request2Messages))
                env.authenticatorService.init(authenticator).flatMap { v =>
                  env.authenticatorService.embed(v, result)
                }
              }
            case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
          }
        }.recover {
          case e: ProviderException =>
            Redirect(signInRoute).flashing(FlashKey.error -> Messages("auth.sign.in.error.invalid"))
        }
      }
    )
  }

  def signOut = SecuredAction.async { implicit request =>
    val result = Redirect(homeRoute)
    env.eventBus.publish(LogoutEvent(request.identity, request, request2Messages))

    env.authenticatorService.discard(request.authenticator, result)
  }

  def signUp = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Redirect(homeRoute))
      case None => Future.successful(Ok(signUpView(SignUpForm.form)))
    }
  }
  
  def signUpPost = UserAwareAction.async { implicit request =>
    SignUpForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(signUpView(form))),
      data => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) =>
            Future.successful(Redirect(signUpRoute).flashing(FlashKey.error -> Messages("user.error.exists")))
          case None =>
            val authInfo = passwordHasher.hash(data.password)
            val user = User(
              None,
              loginInfo = loginInfo,
              firstName = Option(data.firstName),
              lastName = Option(data.lastName),
              email = data.email,
              avatarURL = None
            )
            for {
              avatar <- avatarService.retrieveURL(data.email)
              user <- userService.save(user.copy(avatarURL = avatar))
              authInfo <- authInfoRepository.add(loginInfo, authInfo)
              authenticator <- env.authenticatorService.create(loginInfo)
              value <- env.authenticatorService.init(authenticator)
              result <- env.authenticatorService.embed(value, Redirect(homeRoute))
            } yield {
              env.eventBus.publish(SignUpEvent(user, request, request2Messages))
              env.eventBus.publish(LoginEvent(user, request, request2Messages))
              result
            }
        }
      }
    )
  }
}

object SignInForm {
  val form = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText,
      "rememberMe" -> boolean
    )(Data.apply)(Data.unapply)
  )

  case class Data(
    email: String,
    password: String,
    rememberMe: Boolean)
}

object SignUpForm {
  val form = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(
    firstName: String,
    lastName: String,
    email: String,
    password: String)
}