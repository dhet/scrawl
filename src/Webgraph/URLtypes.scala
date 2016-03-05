package Webgraph

import java.net.URI

/**
  * Created by nicohein on 05/03/16.
  */
sealed abstract class URLtypes
case class Inlink(url : URI) extends URLtypes
case class Outlink(url : URI) extends URLtypes
case class Mail(mail : URI) extends URLtypes
