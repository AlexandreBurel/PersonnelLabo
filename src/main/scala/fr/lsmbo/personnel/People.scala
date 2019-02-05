package fr.lsmbo.personnel

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class People(var initiales: String = "",
                  var nom: Option[String] = None,
                  var prenom: Option[String] = None,
                  var corps: Option[Corps] = None,
                  var mail: Option[String] = None,
                  var batiment: Option[String] = None,
                  var bureau: Option[String] = None,
                  var telephone: Option[String] = None,
                  var masterOrigine: Option[String] = None,
                  var financementOuEtablissementDeRattachement: Option[String] = None,
                  var dateArrivee: Option[LocalDate] = None,
                  var dateDepart: Option[LocalDate] = None,
                  var dureeMois: Option[Int] = None,
                  var responsable1: Option[String] = None,
                  var responsable2: Option[String] = None,
                  var dateSoutenance: Option[LocalDate] = None,
                  var devenir: Option[String] = None,
                  var precisionDevenir: Option[String] = None,
                  var dateEmbauchePremierPoste: Option[LocalDate] = None,
                  var hdr: Option[LocalDate] = None,
                  var dateValiditeSST: Option[String] = None,
                  var commentaire: Option[String] = None,
                  var sexe: Option[String] = None,
                  var quotite: Option[Int] = None,
                  var putzLabo: Option[Boolean] = None,
                  var putzCafe: Option[Boolean] = None,
                  var putzTorchon: Option[Boolean] = None,
                  var photo: Option[String] = None,
                  var anniversaire: Option[LocalDate] = None,

                  var putzCounter: Int = 0,
                  var solvantCounter: Int = 0) {

  override def toString: String = s"${prenom.getOrElse("_")} ${nom.getOrElse("_")} ($initiales)"

  def printDetails(details: Int = 0): Unit = {
    details match {
      case 0 => // all details
        println(initiales + "\t" + prenom.getOrElse("") + "\t" + nom.getOrElse("") + "\t" + corps.getOrElse(new Corps(CorpsList.AUTRE, "Autre")).name + "\t" + mail.getOrElse("") + "\t" +
          batiment.getOrElse("") + "\t" + bureau.getOrElse("") + "\t" + telephone.getOrElse("") + "\t" + masterOrigine.getOrElse("") + "\t" +
          financementOuEtablissementDeRattachement.getOrElse("") + "\t" + dateArrivee.getOrElse("") + "\t" + dateDepart.getOrElse("") + "\t" +
          dureeMois.getOrElse("") + "\t" + responsable1.getOrElse("") + "\t" + responsable2.getOrElse("") + "\t" + dateSoutenance.getOrElse("") + "\t" +
          devenir.getOrElse("") + "\t" + precisionDevenir.getOrElse("") + "\t" + dateEmbauchePremierPoste.getOrElse("") + "\t" + hdr.getOrElse("") + "\t" +
          dateValiditeSST.getOrElse("") + "\t" + commentaire.getOrElse("") + "\t" + sexe.getOrElse("") + "\t" + quotite.getOrElse("") + "\t" +
          putzLabo.getOrElse("") + "\t" + putzCafe.getOrElse("") + "\t" + putzTorchon.getOrElse("") + "\t" + photo.getOrElse("") + "\t" + anniversaire.getOrElse(""))
      case 1 => // most details
        println(initiales + "\t" + prenom.getOrElse("") + "\t" + nom.getOrElse("") + "\t" + getCorps + "\t" + batiment.getOrElse("") + "\t" + bureau.getOrElse("") + "\t" +
          telephone.getOrElse("") + "\t" + putzLabo.getOrElse("") + "\t" + putzCafe.getOrElse("") + "\t" + putzTorchon.getOrElse("") + "\t" + photo.getOrElse("") + "\t" +
          anniversaire.getOrElse(""))
      case 2 => // less details
        println(initiales + "\t" + prenom.getOrElse("") + "\t" + nom.getOrElse("") + "\t" + getCorps + "\t" + anniversaire.getOrElse(""))
      case _ => toString
    }
  }

  def selectMe: String = {
    putzCounter += 1
    getInitiales
  }
  def selectMeForSolvants: String = {
    solvantCounter += 1
    selectMe
  }

  def getPicture: File = {
    if (!photo.isEmpty) {
      val file = new File(MyConfig.trombiPictureFolder.getAbsolutePath + "/" + photo.get)
      if (file.exists() && file.isFile()) file else MyConfig.defaultPicture
    } else MyConfig.defaultPicture
  }

  def merge(people: People): Unit = {
    // find most recent item
    val currentIsNewer = {
      if (!people.dateArrivee.isEmpty && !dateArrivee.isEmpty) dateArrivee.get.compareTo(people.dateArrivee.get) > 0
      else if (people.dateArrivee.isEmpty && !dateArrivee.isEmpty) true
      else if (!people.dateArrivee.isEmpty && dateArrivee.isEmpty) false
      else true
    }
    // merge information, prefer newer file if any ambiguity
    if (initiales.equals("")) initiales = people.initiales
    nom = mergeOptions(nom, people.nom, currentIsNewer)
    prenom = mergeOptions(prenom, people.prenom, currentIsNewer)
    corps = mergeOptions(corps, people.corps, currentIsNewer)
    mail = mergeOptions(mail, people.mail, currentIsNewer)
    batiment = mergeOptions(batiment, people.batiment, currentIsNewer)
    bureau = mergeOptions(bureau, people.bureau, currentIsNewer)
    telephone = mergeOptions(telephone, people.telephone, currentIsNewer)
    masterOrigine = mergeOptions(masterOrigine, people.masterOrigine, currentIsNewer)
    dateSoutenance = mergeOptions(dateSoutenance, people.dateSoutenance, currentIsNewer)
    dateEmbauchePremierPoste = mergeOptions(dateEmbauchePremierPoste, people.dateEmbauchePremierPoste, currentIsNewer)
    hdr = mergeOptions(hdr, people.hdr, currentIsNewer)
    dateValiditeSST = mergeOptions(dateValiditeSST, people.dateValiditeSST, currentIsNewer)
    commentaire = mergeOptions(commentaire, people.commentaire, currentIsNewer)
    sexe = mergeOptions(sexe, people.sexe, currentIsNewer)
    putzLabo = mergeOptions(putzLabo, people.putzLabo, currentIsNewer)
    putzCafe = mergeOptions(putzCafe, people.putzCafe, currentIsNewer)
    putzTorchon = mergeOptions(putzTorchon, people.putzTorchon, currentIsNewer)
    //    else
    photo = mergeOptions(photo, people.photo, currentIsNewer)
    anniversaire = mergeOptions(anniversaire, people.anniversaire, currentIsNewer)
    // these fields should only contain the newest value
    financementOuEtablissementDeRattachement = if (currentIsNewer) financementOuEtablissementDeRattachement else people.financementOuEtablissementDeRattachement
    dateArrivee = if (currentIsNewer) dateArrivee else people.dateArrivee
    dateDepart = if (currentIsNewer) dateDepart else people.dateDepart
    dureeMois = if (currentIsNewer) dureeMois else people.dureeMois
    responsable1 = if (currentIsNewer) responsable1 else people.responsable1
    responsable2 = if (currentIsNewer) responsable2 else people.responsable2
    devenir = if (currentIsNewer) devenir else people.devenir
    precisionDevenir = if (currentIsNewer) precisionDevenir else people.precisionDevenir
    quotite = if (currentIsNewer) quotite else people.quotite
  }

  private def mergeOptions[T](val1: Option[T], val2: Option[T], firstIsNewer: Boolean): Option[T] = {
    if (!val1.isEmpty && !val2.isEmpty) {
      if (firstIsNewer) val1 else val2
    } else if (!val1.isEmpty && val2.isEmpty) val1
    else if (val1.isEmpty && !val2.isEmpty) val2
    else None
  }

  private def mergeOptionsD[T](val1: Option[T], val2: Option[T], firstIsNewer: Boolean, verbose: Boolean = false): Option[T] = {
    if (!val1.isEmpty && !val2.isEmpty) {
      println("val1 ou val2 ? " + (if (firstIsNewer) "val1" else "val2"))
      if (firstIsNewer) val1 else val2
    } else if (!val1.isEmpty && val2.isEmpty) {
      println("val1")
      val1
    } else if (val1.isEmpty && !val2.isEmpty) {
      println("val2")
      val2
    } else {
      println("None")
      None
    }
  }

  def formattedBirthday(format: String = "dd MMMM"): String = {
    if (!anniversaire.isEmpty) {
      anniversaire.get.format(DateTimeFormatter.ofPattern(format))
    } else ""
  }

//  def isValid: Boolean = !initiales.equals("") && (dateDepart.isEmpty || dateDepart.get.compareTo(LocalDate.now()) > 0)
  def isValid: Boolean = !initiales.equals("") && (dateArrivee.isEmpty || dateArrivee.get.compareTo(LocalDate.now()) < 0) && (dateDepart.isEmpty || dateDepart.get.compareTo(LocalDate.now()) > 0)

  def getInitiales: String = {
    initiales match {
      case "AV" => "AVD"
      case "MS" => "JMS"
      case _ => initiales
    }
  }

  def getCorps: String = {
    corps.getOrElse(Corps(CorpsList.AUTRE, "Autre")).name
  }
}
