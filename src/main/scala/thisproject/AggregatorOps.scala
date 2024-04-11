package thisproject

import java.time.LocalDateTime
import cats.effect.IO
import cats.FlatMap
import cats.implicits._
import fs2.Stream

import thisproject.Data._
import thisproject.Utils.{zipMapsWith}

class AggregatorOps[F[_]: FlatMap](
  db:      KeyValueDB[F, Bucket, Map[Stock, Aggregate]],
  service: WebService[F],
  timer:   Timer[F],
  stocks:  List[Stock],
) {

  // Turn the web service into a stream of data
  private def serviceStream: Stream[F, ServiceItem] = Stream.repeatEval(for {
    timestamp <- timer.getTimestamp
    prices <- service.getPresentValue(stocks)
  } yield ServiceItem(timestamp, stocks.zip(prices).toMap))

  // Update the aggregate or put the new one in the database if the
  // corresponding aggregate is not there.
  private def putItem(item: AggregateItem): F[Unit] = for {
    current <- db.get(item.bucket)
    updated = current.map(c => zipMapsWith(Aggregate.+)(c, item.data)).getOrElse(item.data)
    _ <- db.put(item.bucket, updated)
  } yield ()

  // Collect the stream data from the web service and store it in the databases
  def processItems: Stream[F, Unit] = serviceStream.evalMap(item => putItem(AggregateItem.fromServiceItem(item)))
}
