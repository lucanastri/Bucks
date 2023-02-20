package com.kizune.bucks.data

import com.kizune.bucks.model.Fund
import com.kizune.bucks.model.FundDao
import com.kizune.bucks.model.FundWithMovements
import com.kizune.bucks.model.Movement
import kotlinx.coroutines.flow.Flow

class FundRepository(private val fundDao: FundDao) : DatabaseRepository {
    override fun getFundsStream(): Flow<List<Fund>> {
        return fundDao.getFundsStream()
    }

    override fun getFundStream(id: Long): Flow<Fund?> {
        return fundDao.getFundStream(id)
    }

    override suspend fun insertFund(fund: Fund) {
        fundDao.insertFund(fund)
    }

    override suspend fun deleteFund(fund: Fund) {
        fundDao.deleteFund(fund)
    }

    override suspend fun updateFund(fund: Fund) {
        fundDao.updateFund(fund)
    }

    override fun getMovementsStream(): Flow<List<Movement>> {
        return fundDao.getMovementsStream()
    }

    override fun getMovementStream(id: Long): Flow<Movement?> {
        return fundDao.getMovementStream(id)
    }

    override suspend fun insertMovement(movement: Movement) {
        fundDao.insertMovement(movement)
    }

    override suspend fun deleteMovement(movement: Movement) {
        fundDao.deleteMovement(movement)
    }

    override suspend fun updateMovement(movement: Movement) {
        fundDao.updateMovement(movement)
    }

    override fun getCompleteFundStream(id: Long): Flow<FundWithMovements> {
        return fundDao.getCompleteFundStream(id)
    }

    override fun getCompleteFundsStream(): Flow<List<FundWithMovements>> {
        return fundDao.getCompleteFundsStream()
    }

    override suspend fun getFund(id: Long): Fund {
        return fundDao.getFund(id)
    }

    override suspend fun getFunds(): List<Fund> {
        return fundDao.getFunds()
    }

    override suspend fun getMovements(): List<Movement> {
        return fundDao.getMovements()
    }
}