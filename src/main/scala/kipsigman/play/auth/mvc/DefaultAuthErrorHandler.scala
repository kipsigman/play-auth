package kipsigman.play.auth.mvc

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import javax.inject.Inject
import javax.inject.Singleton
import kipsigman.play.auth.entity.User
import play.api.i18n.MessagesApi

@Singleton
class DefaultAuthErrorHandler @Inject() (
  val messagesApi: MessagesApi,
  protected val env: Environment[User, CookieAuthenticator])
  extends AuthHttpErrorHandler with DefaultErrorResults