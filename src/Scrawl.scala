object Scrawl {
  def main(args : Array[String]) = {
    var websites : List[String] = List[String]()
    var activeArguments : Set[Argument] = Set[Argument]() //kann es nicht auf die Reihenfolge ankommen? Ist das schneller als eine Liste?
    if(args.length < 1){
      exit("Enter at least one website to crawl.")
      //can we display -help here?
    } else {
      var paramExpected = false
      for (arg <- args) {
        val argument : Argument = Argument(arg)
        argument match {
          case Flag(_) => activeArguments += argument
          case InvalidArgument(_) => exit(s"Unsupported Command $arg")
          case Input(_) =>
            if(paramExpected){
              println(s"$arg param")
              paramExpected = false //sollen wir das problem mit List expected oder -L (level: int) hiermit lösen, odermithilfe einer Param hierachie?
            } else{
              websites :+= arg
            }
          case ParamArgument(_) =>
            activeArguments += argument
            paramExpected = true
        }
      }

      println(s"Crawl the sites $websites with the arguments $activeArguments")
    }
  }

  def exit(message : String) = {
    println(message)
    println("Exiting...")
    sys.exit(1)
  }
}
