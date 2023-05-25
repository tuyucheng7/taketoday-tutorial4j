package cn.tuyucheng.taketoday.kotlin.tomap

import cn.tuyucheng.taketoday.kotlin.tomap.MapHelper.DATE_FORMAT
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import java.util.*

object KotlinSerializationMapHelper {

   val JSON = Json { encodeDefaults = true }

   inline fun <reified T> toMap(obj: T): Map<String, Any?> {
      return jsonObjectToMap(JSON.encodeToJsonElement(obj).jsonObject)
   }

   fun jsonObjectToMap(element: JsonObject): Map<String, Any?> {
      return element.entries.associate {
         it.key to extractValue(it.value)
      }
   }

   private fun extractValue(element: JsonElement): Any? {
      return when (element) {
         is JsonNull -> null
         is JsonPrimitive -> element.content
         is JsonArray -> element.map { extractValue(it) }
         is JsonObject -> jsonObjectToMap(element)
      }
   }

   object DateSerializer : KSerializer<Date> {
      override val descriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)
      override fun serialize(encoder: Encoder, value: Date) = encoder.encodeString(DATE_FORMAT.format(value))
      override fun deserialize(decoder: Decoder): Date = DATE_FORMAT.parse(decoder.decodeString())
   }

}