package com.grapqh.kickstart.controller

import com.grapqh.kickstart.domain.Food
import com.grapqh.kickstart.domain.FoodType
import com.grapqh.kickstart.service.FoodService
import graphql.kickstart.tools.GraphQLMutationResolver
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component


@Component
class FoodController(private val foodService: FoodService) : GraphQLQueryResolver, GraphQLMutationResolver {

    fun save(input: CreateFoodInput): Food {
        return foodService.save(input.name, input.type, input.calorie, input.described)
    }

    fun getFood(name: String): Food {
        return foodService.getFood(name)
    }

    fun getFoods(): List<Food> {
        return foodService.getFoods()
    }
}
data class CreateFoodInput(val name: String, val type: FoodType?, val calorie: Int?, val described: String?)
