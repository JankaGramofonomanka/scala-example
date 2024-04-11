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

  private def serviceStream: Stream[F, ServiceItem] = Stream.repeatEval(for {
    timestamp <- timer.getTimestamp
    prices <- service.getPresentValue(stocks)
  } yield ServiceItem(timestamp, stocks.zip(prices).toMap))

  private def putItem(item: AggregateItem): F[Unit] = for {
    current <- db.get(item.bucket)
    updated: Map[Stock, Aggregate] = current.map(c => zipMapsWith(Aggregate.+)(c, item.data)).getOrElse(item.data)
    _ <- db.put(item.bucket, updated)
  } yield ()

  def processItems: Stream[F, Unit] = serviceStream.evalMap(item => putItem(AggregateItem.fromServiceItem(item)))
}
