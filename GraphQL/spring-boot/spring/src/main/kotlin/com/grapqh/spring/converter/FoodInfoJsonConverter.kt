package com.grapqh.spring.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.grapqh.spring.domain.FoodInfo
import javax.persistence.AttributeConverter

class FoodInfoJsonConverter : AttributeConverter<FoodInfo, String> {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    override fun convertToDatabaseColumn(attribute: FoodInfo?): String? {
        return attribute?.let {
            objectMapper.writeValueAsString(it)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): FoodInfo? {
        return dbData?.let {
            objectMapper.readValue(it, FoodInfo::class.java)
        }
    }
}
