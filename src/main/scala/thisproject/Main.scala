package thisproject

import java.time.LocalDateTime
import cats.effect.{ExitCode, IO, IOApp}

import thisproject.AggregatorOps
import thisproject.Data._
import thisproject.Mocks

object Main extends IOApp {
  def run(args: List[String]) = {
    val db:      Mocks.MockDB   = new Mocks.MockDB
    val service: WebService[IO] = new Mocks.MockWebService
    val timer:   Timer[IO]      = new Mocks.MockTimer(LocalDateTime.of(1900, 1, 1, 0, 0))

    val stocks: List[Stock] = List(Mocks.Examples.stock1, Mocks.Examples.stock2)

    val aggregator: AggregatorOps[IO] = new AggregatorOps(db, service, timer, stocks)

    for {
      result <- aggregator.processItems.take(9).compile.drain.as(ExitCode.Success)
      _ <- db.listContents
    } yield result
  }
}


