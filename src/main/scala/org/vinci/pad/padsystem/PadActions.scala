package org.vinci.padsystem

import java.io.File
import com.typesafe.scalalogging.LazyLogging
import org.clulab.odin._
import org.clulab.odin.impl.Taxonomy
import org.clulab.struct.Interval
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

class PadActions extends Actions with LazyLogging {

  def debug(ms: Seq[Mention], state: State): Seq[Mention] = {
    println("DEBUG ACTION")
    ms
  }

}

object PadActions extends Actions {

  val taxonomy = readTaxonomy("org/vinci/pad/padsystem/grammars/taxonomy.yml")

  private def readTaxonomy(path: String): Taxonomy = {
    val url = getClass.getClassLoader.getResource(path)
    val source = if (url == null) scala.io.Source.fromFile(path) else scala.io.Source.fromURL(url)
    val input = source.mkString
    source.close()
    val yaml = new Yaml(new Constructor(classOf[java.util.Collection[Any]]))
    val data = yaml.load(input).asInstanceOf[java.util.Collection[Any]]
    Taxonomy(data)
  }

}
