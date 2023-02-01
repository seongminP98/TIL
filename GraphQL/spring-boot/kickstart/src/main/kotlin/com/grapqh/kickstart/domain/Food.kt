package com.grapqh.kickstart.domain

import com.grapqh.kickstart.converter.FoodInfoJsonConverter
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Food(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String?,

    val type: FoodType?,

    @Convert(converter = FoodInfoJsonConverter::class)
    @Column(columnDefinition = "TEXT")
    var info: FoodInfo? = null,
)
