package example

import com.spotify.scio._

object WordCount {
  def main(cmdlineArgs: Array[String]): Unit = {
    val (sc, args) = ContextAndArgs(cmdlineArgs)

    sc.textFile(args.getOrElse("input", "something.txt"))
      .transform("counter") {
        // Split input lines, filter out empty tokens and expand into a collection of tokens
        _.flatMap(_.split("[^a-zA-Z']+").filter(_.nonEmpty))
        // Count occurrences of each unique `String` to get `(String, Long)`
        .countByValue
      }
      // Map `(String, Long)` tuples into strings
      .map(t => t._1 + ": " + t._2)
      // Save result as text files under the output path
      .saveAsTextFile(path = args("output"), numShards = 1)

    // Close the context and execute the pipeline
    sc.run()
    ()
  }
}