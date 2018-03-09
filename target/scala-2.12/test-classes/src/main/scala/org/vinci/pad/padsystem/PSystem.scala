package org.vinci.padsystem

import org.clulab.odin.{ExtractorEngine, Mention, State}
import org.clulab.processors.{Document, Processor}
import org.clulab.processors.fastnlp.FastNLPProcessor
import scala.io.Source

class PSystem(
  rulesPath: String = "/org/vinci/pad/padsystem/grammars/master.yml"
) {

  import PSystem._

  //defaults to FastNLPProcessor ir no processor is given
  val proc = new FastNLPProcessor()

  val rules: String = readRules(rulesPath)

  val actions  = new PadActions

  val entityEngine: ExtractorEngine = ExtractorEngine(rules, actions)
  val eventEngine: ExtractorEngine = ExtractorEngine(rules, actions)

  def extractFrom(text: String): Vector[Mention] = {
  val doc = proc.annotate(text)
  extractFrom(doc)
  }

  def extractEntitiesFrom(doc: Document, state: State = new State()): Vector[Mention] = {
  val res = entityEngine.extractFrom(doc, state).toVector
   res
  }

  def extractEventsFrom(doc: Document, state: State): Vector[Mention] = {
    val res = eventEngine.extractFrom(doc, state).toVector
    res
  }

  def extractFrom(doc: Document): Vector[Mention] = {
    // get entities
    val entities = extractEntitiesFrom(doc)
    // get events
    val res = extractEventsFrom(doc, State(entities)).distinct
    res
  }


}

object PSystem {

  def readRules(rulesPath: String): String = {
    println(s"rulesPath:\t$rulesPath")
    val source = io.Source.fromURL(getClass.getResource(rulesPath))
    val rules = source.mkString
    source.close()
    rules
  }

}