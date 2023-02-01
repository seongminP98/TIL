package com.grapqh.kickstart.repository

import com.grapqh.kickstart.domain.Food
import org.springframework.data.jpa.repository.JpaRepository

interface FoodRepository : JpaRepository<Food, Long> {
    fun findByName(name: String): Food?
}
