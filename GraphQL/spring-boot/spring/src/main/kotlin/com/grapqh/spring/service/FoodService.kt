package com.grapqh.spring.service

import com.grapqh.spring.domain.Food
import com.grapqh.spring.domain.FoodInfo
import com.grapqh.spring.domain.FoodType
import com.grapqh.spring.repository.FoodRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FoodService(
    private val foodRepository: FoodRepository,
) {
    fun save(name: String, type: FoodType?, calorie: Int?, described: String?): Food {
        val food = Food(
            name = name,
            type = type,
            info = FoodInfo(
                calorie = calorie,
                described = described,
            )
        )
        return foodRepository.save(food)
    }

    fun getFood(name: String): Food {
        return foodRepository.findByName(name) ?: throw IllegalArgumentException()
    }

    fun getFoods(): List<Food> {
        return foodRepository.findAll()
    }
}
