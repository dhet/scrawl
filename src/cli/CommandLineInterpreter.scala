package cli

/**
  * Class for interpreting command line arguments
  */
class CommandLineInterpreter {

  /**
    * Interpret a number of string arguments. Then executes the action defined for each argument. Argument behavior is
    * defined in the [[cli.Argument]] companion object.
    * @param args The arguments to interpret
    * @return     A list of all the interpreted arguments. If no errors occur this list corresponds to the input list
    *             but with [[cli.Argument]]s instead of Strings
    * @throws cli.CommandLineException In case an error occurs during the parsing process
    */
  def interpretArguments(args : Array[String]) : List[Argument] = {
    var activeArguments: List[Argument] = Nil
    for (argument <- args.map(Argument(_))) {
      argument match {
        case Flag(_) => activeArguments :+= argument
        case ParamArgument(_) => activeArguments :+= argument
        case InvalidArgument(_) => throw new CommandLineException(s"""Unsupported Command: "{$argument.toString}".""")
        case Value(_) =>
          if (activeArguments.nonEmpty && activeArguments.last.isInstanceOf[ParamArgument]) {
            activeArguments.last.asInstanceOf[ParamArgument].parameter = argument
          } else {
            activeArguments :+= argument
          }
      }
    }
    val onlyInterpretable = activeArguments.filter(arg => arg.isInstanceOf[Flag] || arg.isInstanceOf[ParamArgument])
    applyArguments(onlyInterpretable)
    activeArguments
  }

  /**
    * Execute the action defined for an argument. Argument actions are defined in the [[cli.Argument]] companion object.
    * @param args The list of arguments to execute
    */
  private def applyArguments(args : List[Argument]) = {
    for(arg <- args){
      try{
        arg.executeAction
      } catch {
        case e : Exception => throw new CommandLineException(s"""Wrong usage of argument "$arg".""")
      }
    }
  }

}
