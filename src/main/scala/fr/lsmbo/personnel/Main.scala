package fr.lsmbo.personnel

import java.io.File
//import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import fr.lsmbo.personnel.putz.PutzGenerator
import fr.lsmbo.personnel.trombi.Trombinoscope

object Main extends App {
  
  override def main(args: Array[String]): Unit = {
    
    args.foreach(arg => {
      arg match {
        case "putz" => new PutzGenerator
        case "trombi" => new Trombinoscope
        case _ => 
      }
    })

  }

}
