package thisproject

import java.time.LocalDateTime

object Utils {

  def onSomes[V](fun: (V, V) => V, v1: Option[V], v2 : Option[V]): Option[V] = (v1, v2) match {
    case (None,     None)     => None
    case (None,     Some(v2)) => Some(v2)
    case (Some(v1), None)     => Some(v1)
    case (Some(v1), Some(v2)) => Some(fun(v1, v2))
  }

  // Applies a binary function on elements of two maps, whose keys are the same.
  // If a given key exists in only one map, it will be present with the
  // corresponding value in the returned map.
  def zipMapsWith[K, V](plus: (V, V) => V)(m1: Map[K, V], m2: Map[K, V]): Map[K,V] = {
    val keys = (m1.keys ++ m2.keys).toList.distinct

    val list = for {
      k <- keys
      v1 = m1.get(k)
      v2 = m2.get(k)
      v <- onSomes[V](plus, v1, v2)
    } yield (k, v)

    list.toMap
  }

  def roundToHours(dt: LocalDateTime): LocalDateTime = {
    val year   = dt.getYear
    val month  = dt.getMonthValue
    val day    = dt.getDayOfMonth
    val hour   = dt.getHour
    
    LocalDateTime.of(year, month, day, hour, 0)
  }
}