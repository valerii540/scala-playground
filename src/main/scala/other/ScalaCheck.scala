package other

import org.scalacheck.Arbitrary._
import org.scalacheck.Gen.oneOf
import org.scalacheck.{Arbitrary, Gen}
import other.FileStatuses.FileStatus

import java.util.UUID

object FileStatuses extends Enumeration {
  type FileStatus = Value

  val NotUploaded: FileStatus = Value(0, "Not uploaded")
  val Processing: FileStatus  = Value(1, "Processing")
}

case class Dummy(id: UUID, name: String, description: Option[String], aId: UUID, uploadDate: Long, enum: FileStatus)

object ScalaCheck extends App {
  def option[T](g: Gen[T]): Gen[Option[T]] = Arbitrary(Gen.option(g)).arbitrary
  val UUIDGen: Gen[UUID]                   = arbitrary[UUID]
  val LongGen: Gen[Long]                   = Arbitrary(Gen.posNum[Long]).arbitrary
  val StringGen: Gen[String]               = Arbitrary(Gen.alphaStr).arbitrary

  protected val suppressionListGen: Gen[Dummy] = for {
    suppressionListId  <- UUIDGen
    name               <- Arbitrary(Gen.alphaStr).arbitrary
    description        <- option(Gen.alphaStr)
    kek                <- option(Gen.alphaStr)
    advertiserId       <- UUIDGen
    uploadDate         <- arbitrary[Long].map(math.abs)
    fileStatus         <- Arbitrary(oneOf(FileStatuses.values)).arbitrary
//    offerId            <- UUIDGen
//    manualId           <- LongGen
//    name               <- StringGen
//    description        <- option(Gen.alphaStr)
//    creationDate       <- LongGen
//    metadataUpdateDate <- LongGen
//    manualUploadDate   <- LongGen
//    autoUploadDate     <- option(Gen.posNum[Long])
//    sourceLink         <- option(Gen.alphaStr)
//    advertiserId       <- UUIDGen
//    suppressionListId  <- UUIDGen
  } yield Dummy(suppressionListId, name, description, advertiserId, uploadDate, fileStatus)

  (0 until 100_000).foreach { _ =>
    suppressionListGen.sample.get
  }
}
