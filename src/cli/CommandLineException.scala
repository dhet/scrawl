package cli

/**
  * Thrown whenever the command line interpreter encounters an error.
  * @param message  The message thrown
  */
class CommandLineException(message : String) extends Exception(message){}
