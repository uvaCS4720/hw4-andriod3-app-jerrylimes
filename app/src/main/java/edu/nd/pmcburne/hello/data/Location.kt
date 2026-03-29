package edu.nd.pmcburne.hello.data

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val id: Int,
    val name: String,
    val description: String,
    val tag_list: List<String>,
    val visual_center: VisualCenter
)

@Serializable
data class VisualCenter(
    val latitude: Double,
    val longitude: Double
)