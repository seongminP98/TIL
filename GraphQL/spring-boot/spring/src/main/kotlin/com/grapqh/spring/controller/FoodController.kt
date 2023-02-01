package com.grapqh.spring.controller

import com.grapqh.spring.domain.Food
import com.grapqh.spring.domain.FoodType
import com.grapqh.spring.service.FoodService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Component

@Component
class FoodController(
    private val foodService: FoodService,
) {

    @MutationMapping
    fun save(
        @Argument name: String,
        @Argument type: FoodType?,
        @Argument calorie: Int?,
        @Argument described: String?,
    ): Food {
        return foodService.save(name, type, calorie, described)
    }

    @QueryMapping
    fun getFood(@Argument name: String): Food {
        return foodService.getFood(name)
    }

    @QueryMapping
    fun getFoods(): List<Food> {
        return foodService.getFoods()
    }
}
