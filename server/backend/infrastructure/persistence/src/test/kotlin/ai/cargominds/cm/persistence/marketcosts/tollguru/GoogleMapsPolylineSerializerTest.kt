package ai.cargominds.cm.persistence.marketcosts.tollguru

import ai.cargominds.cm.domain.marketcosts.Coordinates
import assertk.assertThat
import assertk.assertions.isNotEmpty
import kotlin.test.Test
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class GoogleMapsPolylineSerializerTest {
    @Test
    fun `can deserialize polyline`() {
        // GIVEN
        val json = """{
            "polyline": "${getPolyline()}"
            }
           """

        // WHEN
        val polylineHolder = Json.Default.decodeFromString<PolylineHolder>(json)

        // THEN
        assertThat(polylineHolder.polyline).isNotEmpty()
    }

    private fun getPolyline(): String {
        return javaClass.classLoader.getResourceAsStream("test_polyline.raw")!!
            .bufferedReader().readText()
    }
}

@Serializable
data class PolylineHolder(
    @Serializable(GoogleMapsPolylineSerializer::class)
    val polyline: List<Coordinates>,
)
