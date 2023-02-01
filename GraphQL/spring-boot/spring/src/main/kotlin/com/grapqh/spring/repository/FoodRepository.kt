package com.grapqh.spring.repository

import com.grapqh.spring.domain.Food
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.graphql.data.GraphQlRepository

@GraphQlRepository
interface FoodRepository : JpaRepository<Food, Long> {
    fun findByName(name: String): Food?
}
