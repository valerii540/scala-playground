package other

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import com.github.mjakubowski84.parquet4s.{ParquetStreams, ParquetWriter, RowParquetRecord, ValueCodecConfiguration}
import org.apache.parquet.hadoop.ParquetFileWriter
import org.apache.parquet.hadoop.metadata.CompressionCodecName
import org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.BINARY
import org.apache.parquet.schema.Type.Repetition.OPTIONAL
import org.apache.parquet.schema.{LogicalTypeAnnotation, MessageType, Types}

import java.time.format.DateTimeFormatter
import java.time.{ZoneOffset, ZonedDateTime}
import java.util.{TimeZone, UUID}
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.Random

object Parquet4s extends App {
  implicit val system: ActorSystem = ActorSystem("Parquet4s-system")

  implicit val schema: MessageType = Types.buildMessage
    .addFields(
      Types.required(BINARY).as(LogicalTypeAnnotation.stringType).named("file_id"),
      Types.required(BINARY).as(LogicalTypeAnnotation.stringType).named("timestamp"),
      Types.requiredList.element(BINARY, OPTIONAL).as(LogicalTypeAnnotation.stringType).named("entities")
    )
    .named("other_entities")

  println(schema.toString)

  val fileId   = UUID.randomUUID().toString
  val time     = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
  val entities = List.fill(10)(Random.alphanumeric.take(5).mkString)

  val writeOptions = ParquetWriter.Options(
    writeMode = ParquetFileWriter.Mode.CREATE,
    compressionCodecName = CompressionCodecName.SNAPPY
  )

  val vcc = ValueCodecConfiguration(TimeZone.getTimeZone(ZoneOffset.UTC))

  val data = List(
    (fileId, time, entities)
  ).map { case (id, t, e) =>
    RowParquetRecord.empty
      .add("file_id", id, vcc)
      .add("timestamp", t, vcc)
      .add("entities", e, vcc)
  }

//  val data = Seq(OtherRow(fileId, time, entities))

//  ParquetWriter.writeAndClose("file:///home/vbosiak/test.parquet", data, options = writeOptions)

  val future =
    Source(data)
      .runWith(
        ParquetStreams.toParquetSingleFile(
          path = "file:///home/vbosiak/test_parquet",
          options = writeOptions
        )
      )

  Await.result(future, 5.seconds)
  Await.result(system.terminate(), 5.seconds)
}
