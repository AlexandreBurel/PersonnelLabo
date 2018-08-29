package fr.lsmbo.personnel

import fr.lsmbo.personnel.anniv.AnnivMaker
import fr.lsmbo.personnel.putz.PutzMaker
import fr.lsmbo.personnel.trombi.TrombiMaker

object Main extends App {
  
  override def main(args: Array[String]): Unit = {

//    println("initiales\tprenom\tnom\tcorps\tmail\tbatiment\tbureau\ttelephone\tmasterOrigine\tfinancementOuEtablissementDeRattachement\tdateArrivee\tdateDepart\tdureeMois\tresponsable1\tresponsable2\tdateSoutenance\tdevenir\tprecisionDevenir\tdateEmbauchePremierPoste\thdr\tdateValiditeSST\tcommentaire\tsexe\tquotite\tputzLabo\tputzCafe\tputzTorchon\tphoto\tanniversaire")
//    TableauDuPersonnel.getPersonnel().foreach(p => {
////      println(p.toString + "\t" + p.putzLabo.getOrElse("") + "\t" + p.putzCafe.getOrElse("") + "\t" + p.putzTorchon.getOrElse(""))
//      p.printDetails
//    })

    args.foreach(_ match{
      case "putz" => new PutzMaker
      case "trombi" => new TrombiMaker
      case "anniv" => new AnnivMaker
      case _ =>
    })
//    args.foreach(arg => {
//      arg match {
////        case "putz" => new PutzGenerator
//        case "putz" => new PutzMaker
////        case "trombi" => new Trombinoscope
//        case "trombi" => new TrombiMaker
//        case _ =>
//      }
//    })

  }

}
