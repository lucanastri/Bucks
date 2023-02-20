package com.kizune.bucks.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FundDao {
    @Query(
        "SELECT * " +
                "FROM Fund f " +
                "ORDER BY f.creationDate DESC"
    )
    fun getFundsStream(): Flow<List<Fund>>

    @Query(
        "SELECT * " +
                "FROM Fund f " +
                "WHERE f.fundID = :id"
    )
    fun getFundStream(id: Long): Flow<Fund>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFund(fund: Fund): Long

    @Delete
    suspend fun deleteFund(fund: Fund)

    @Update
    suspend fun updateFund(fund: Fund)

    @Query(
        "SELECT * " +
                "FROM Movement m "
    )
    fun getMovementsStream(): Flow<List<Movement>>

    @Query(
        "SELECT * FROM Movement m " +
                "WHERE m.movementID = :id"
    )
    fun getMovementStream(id: Long): Flow<Movement>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(movement: Movement)

    @Delete
    suspend fun deleteMovement(movement: Movement)

    @Update
    suspend fun updateMovement(movement: Movement)

    @Transaction
    @Query(
        "SELECT * " +
                "FROM Fund f " +
                "WHERE f.fundID = :id"
    )
    fun getCompleteFundStream(id: Long): Flow<FundWithMovements>

    @Transaction
    @Query(
        "SELECT * " +
                "FROM Fund f " +
                "ORDER BY f.creationDate DESC"
    )
    fun getCompleteFundsStream(): Flow<List<FundWithMovements>>


    @Query(
        "SELECT * " +
                "FROM Fund f " +
                "WHERE f.fundID = :id"
    )
    suspend fun getFund(id: Long): Fund

    @Query(
        "SELECT * " +
                "FROM Fund f " +
                "ORDER BY f.creationDate DESC"
    )
    suspend fun getFunds(): List<Fund>

    @Query(
        "SELECT * " +
                "FROM Movement m "
    )
    suspend fun getMovements(): List<Movement>
}