package analyze

import java.net.URL

/**
  * Created by nicohein on 05/03/16.
  */
sealed abstract class URLtypes
case class Inlink(url : URL) extends URLtypes
case class Outlink(url : URL) extends URLtypes
case class Mail(mail : URL) extends URLtypes
