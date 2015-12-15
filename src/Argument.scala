object Argument {
  val supportedArgs : List[Argument] = List[Argument](
    new ParamArgument("a"),
    new ParamArgument("L"),
    new Flag("weird")
  )

  def apply(arg : String) : Argument = {
    if(arg.startsWith("-")) {
      supportedArgs.find{a => arg.contains(a.name) /*|| arg.contains(a.short)*/} match {
        case None => new InvalidArgument(arg)
        case Some(a) => a
      }
    }
    else {
      new Input(arg)
    }
  }
}

abstract class Argument(val name : String){
  override def equals(obj : Any) = obj match {
    case Argument => obj.asInstanceOf[Argument].name == name// || obj.asInstanceOf[Argument].short == short
    case _ => false
  }
}

case class Flag(override val name : String) extends Argument(name){
  override def toString() = s"-$name"
}

case class Input(override val name : String) extends Argument(name){}

case class ParamArgument(override val name : String) extends Argument(name){
  var parameter : String = ""
  override def toString() = s"-$name $parameter"
}

case class InvalidArgument(override val name : String) extends Argument(name){}