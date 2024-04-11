package thisproject.Aggregator

import java.time.LocalDateTime
import cats.effect.IO

import thisproject.Data
import thisproject.Utils

class AggregatorOps[F[_]](
  db: KeyValueDB[F, Bucket, Map[Stock, Aggregate]],
  service: Stream[F, ServiceItem],
) {
  private def putItem(item: AggregateItem): F[Unit] = for {
    current <- db.get(item.bucket)
    updated = zipMapsWith(_ + _)(current, item.data)
    _ <- db.put(item.bucket, updated)
  } yield Unit

  def processItems: Stream[F, Unit] = service.evalMap(item => putItem(AggregateItem.fromServiceItem(item)))
}

class WebServiceStream[F[_]](stocks: Array[Stock], service: WebService[F], timer: Timer[F]) {
  def stream: Stream[F, ServiceItem] = Stream.eval(for {
    timestamp <- timer.getTimestamp
    prices <- service.getPresentValue(stocks)
  } yield ServiceItem(timestamp, stocks.zip(prices).toMap))
}
