package com.kizune.bucks.data

import com.kizune.bucks.model.Fund
import com.kizune.bucks.model.FundWithMovements
import com.kizune.bucks.model.Movement
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun getFundsStream(): Flow<List<Fund>>
    fun getFundStream(id: Long): Flow<Fund?>
    suspend fun insertFund(fund: Fund)
    suspend fun deleteFund(fund: Fund)
    suspend fun updateFund(fund: Fund)

    fun getMovementsStream(): Flow<List<Movement>>
    fun getMovementStream(id: Long): Flow<Movement?>
    suspend fun insertMovement(movement: Movement)
    suspend fun deleteMovement(movement: Movement)
    suspend fun updateMovement(movement: Movement)

    fun getCompleteFundStream(id: Long): Flow<FundWithMovements?>
    fun getCompleteFundsStream(): Flow<List<FundWithMovements>>

    suspend fun getFund(id: Long): Fund
    suspend fun getFunds(): List<Fund>
    suspend fun getMovements(): List<Movement>
}