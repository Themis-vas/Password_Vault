package com.passguard.app.data.mapper

import com.passguard.app.data.local.entity.CategoryEntity
import com.passguard.app.domain.model.Category

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    iconRes = iconRes
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    iconRes = iconRes
)
