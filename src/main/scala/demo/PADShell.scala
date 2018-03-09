package demo

import java.io.File

import jline.console.ConsoleReader
import jline.console.history.FileHistory
import org.clulab.odin.Mention
import org.clulab.processors.Document
import org.vinci.padsystem.PSystem
import utils._

import scala.collection.immutable.ListMap
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
//import com.typesafe.config.ConfigFactory
//import ai.lum.common.ConfigUtils._
//import org.clulab.sequences.LexiconNER

import scala.io.Source

/**
  * Interactive shell for parsing RAPs
  */
object PADShell extends App {

  //TODO: Load the parameters to the system through a config file
  //  val config = ConfigFactory.load()         // load the configuration file
  //  val quantifierKBFile: String = config[String]("wmseed.quantifierKB")
  //  val domainParamKBFile: String = config[String]("wmseed.domainParamKB")

  val history = new FileHistory(new File(System.getProperty("user.home"), ".agroshellhistory"))
  sys addShutdownHook {
    history.flush() // flush file before exiting
  }

  val reader = new ConsoleReader
  reader.setHistory(history)

  val commands = ListMap(
    ":help" -> "show commands",
    // ":reload" -> "reload grammar",
    ":exit" -> "exit system"
  )

  // creates an extractor engine using the rules and the default actions
  val ieSystem = new PSystem()

  var proc = ieSystem.proc
  //val ner = LexiconNER(Seq("org/clulab/wm/lexicons/Quantifier.tsv"), caseInsensitiveMatching = true)
  //val grounder = ieSystem.gradableAdjGroundingModel

  reader.setPrompt("(PAD)>>> ")
  println("\nWelcome to the PADShell!")
  //  println(s"Loading the gradable adjectives grounding model from : $quantifierKBFile")
  printCommands()

  var running = true

  while (running) {
    reader.readLine match {
      case ":help" =>
        printCommands()

      case ":reload" =>
        println("Not supported yet.")
      // TODO

      case ":exit" | null =>
        running = false

      case text =>
        extractFrom(text)
    }
  }

  // manual terminal cleanup
  reader.getTerminal.restore()
  reader.shutdown()

  // summarize available commands
  def printCommands(): Unit = {
    println("\nCOMMANDS:")
    for ((cmd, msg) <- commands)
      println(s"\t$cmd\t=> $msg")
    println()
  }

  def extractFrom(text:String): Unit = {

    // preprocessing
    val doc = proc.annotate(text)
    //doc.sentences.foreach(s => s.entities = Some(ner.find(s)))

    // extract mentions from annotated document
    val mentions = ieSystem.extractFrom(doc).sortBy(m => (m.sentence, m.getClass.getSimpleName))

    // debug display the mentions
    displayMentions(mentions, doc)

    // pretty display
    prettyDisplay(mentions, doc)

  }

  def prettyDisplay(mentions: Seq[Mention], doc: Document): Unit = {
    val events = mentions.filter(_ matches "Event")
    val params = new mutable.HashMap[String, ListBuffer[(String, String, Option[Seq[String]])]]()
    for(e <- events) {
      val f = formal(e)
      if(f.isDefined) {
        val just = e.text
        val sent = e.sentenceObj.getSentenceText
        val quantifiers = e.arguments.get("quantifier") match {
          case Some(quantifierMentions) => Some(quantifierMentions.map(_.text))
          case None => None
        }
        params.getOrElseUpdate(f.get, new ListBuffer[(String, String, Option[Seq[String]])]) += new Tuple3(just, sent, quantifiers)
      }
    }

    if(params.nonEmpty) {
      println("PAD Variables:")
      for (k <- params.keySet) {
        val evidence = params.get(k).get
        println(s"$k: ${evidence.size} instances:")
        for (e <- evidence) {
          println(s"\tJustification: [${e._1}]")
          println(s"""\tSentence: "${e._2}"""")
        }
        println()
      }
    }
  }

  // Returns Some(string) if there is an INCREASE or DECREASE event with a Param, otherwise None
  def formal(e:Mention):Option[String] = {
    var t = ""
    if(e matches "Decrease") t = "DECREASE"
    else if(e matches "Increase") t = "INCREASE"
    else return None

    Some(s"$t of ${e.arguments.get("theme").get.head.label}")
  }

}