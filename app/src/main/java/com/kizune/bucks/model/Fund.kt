package com.kizune.bucks.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kizune.bucks.R
import kotlinx.serialization.Serializable

enum class FundType(
    @StringRes val id: Int,
) {
    Wallet(R.string.fund_type_wallet),
    Stash(R.string.fund_type_stash),
    Safe(R.string.fund_type_safe),
    BankAccount(R.string.fund_type_bankaccount),
    CreditCard(R.string.fund_type_creditcard),
    DebitCard(R.string.fund_type_debitcard),
    PrepaidCard(R.string.fund_type_prepaidcard)
}

enum class FundCategory(
    @StringRes val id: Int,
) {
    Savings(R.string.fund_category_savings),
    Retirement(R.string.fund_category_retirement),
    Education(R.string.fund_category_education),
    Sanitary(R.string.fund_category_sanitary),
    Expenses(R.string.fund_category_expenses),
    Other(R.string.fund_category_other)
}

enum class FundNetwork(
    @StringRes val id: Int,
    @DrawableRes val icon: Int
) {
    Mastercard(R.string.fund_network_mastercard, R.drawable.icon_mastercard),
    Maestro(R.string.fund_network_maestro, R.drawable.icon_maestro),
    Visa(R.string.fund_network_visa, R.drawable.icon_visa),
    AmericanExpress(R.string.fund_network_americanexpress, R.drawable.icon_americanexpress),
    Other(R.string.fund_network_other, R.drawable.logo_other)
}

enum class FundBank(
    @StringRes val id: Int,
    @DrawableRes val icon: Int
) {
    Unicredit(R.string.fund_bank_unicredit, R.drawable.logo_unicredit),
    Sanpaolo(R.string.fund_bank_sanpaolo, R.drawable.logo_sanpaolo),
    Montepaschi(R.string.fund_bank_siena, R.drawable.logo_montepaschi),
    BNL(R.string.fund_bank_lavoro, R.drawable.logo_bnl),
    Paypal(R.string.fund_bank_Paypal, R.drawable.logo_paypal),
    Other(R.string.fund_bank_other, R.drawable.logo_other)
}

@Entity
@Serializable
data class Fund(
    @PrimaryKey var fundID: Long = 0,
    val title: String = "",
    val type: FundType = FundType.Wallet,
    val category: FundCategory = FundCategory.Savings,
    var cash: Double = 0.0,
    val serialNumber: String = "",
    val network: FundNetwork = FundNetwork.Mastercard,
    val iban: String = "",
    val bank: FundBank = FundBank.Unicredit,
    val brand: String = "",
    val creationDate: Long = System.currentTimeMillis()
)

fun Fund.getColorsForCard(): Pair<Int, Int> {
    return when (type) {
        FundType.Wallet -> Pair(R.color.wallet, R.color.on_wallet)
        FundType.Safe, FundType.Stash -> Pair(R.color.stash, R.color.on_stash)
        else -> when (bank) {
            FundBank.Unicredit -> Pair(R.color.unicredit, R.color.on_unicredit)
            FundBank.Sanpaolo -> Pair(R.color.sanpaolo, R.color.on_sanpaolo)
            FundBank.Montepaschi -> Pair(R.color.siena, R.color.on_siena)
            FundBank.BNL -> Pair(R.color.bnl, R.color.on_bnl)
            FundBank.Paypal -> Pair(R.color.paypal, R.color.on_paypal)
            FundBank.Other -> Pair(R.color.unicredit, R.color.on_unicredit)
        }
    }
}

fun Fund.getBankLogoResForCard(): Int =
    when (type) {
        FundType.BankAccount, FundType.PrepaidCard,
        FundType.DebitCard, FundType.CreditCard -> bank.icon
        else -> R.drawable.logo_other
    }

fun Fund.getNetworkIconResForCard(): Int =
    when (type) {
        FundType.PrepaidCard,
        FundType.DebitCard, FundType.CreditCard -> network.icon
        else -> R.drawable.logo_other
    }

fun Fund.hasSerial(): Boolean =
    when (type) {
        FundType.CreditCard, FundType.DebitCard, FundType.PrepaidCard -> true
        else -> false
    }

val mockPrepaid = Fund(
    fundID = 0L,
    title = "Genius card",
    type = FundType.PrepaidCard,
    category = FundCategory.Savings,
    cash = 50.0,
    serialNumber = "1242231231242321",
    network = FundNetwork.Mastercard,
    iban = "",
    bank = FundBank.Unicredit,
    creationDate = 1000L
)

val mockBankAccount = Fund(
    fundID = 1L,
    title = "Conto online",
    type = FundType.BankAccount,
    category = FundCategory.Savings,
    cash = 500.0,
    iban = "120300",
    bank = FundBank.Paypal,
    creationDate = 1030L
)

val mockWallet = Fund(
    fundID = 2L,
    title = "Il mio portafoglio",
    type = FundType.Wallet,
    category = FundCategory.Savings,
    cash = 52.0,
    creationDate = 1050L,
    brand = "Gucci"
)

val mockStash = Fund(
    fundID = 3L,
    title = "Il mio deposito",
    type = FundType.Stash,
    category = FundCategory.Savings,
    cash = 52000.0,
    creationDate = 2050L,
)

val mockFunds = listOf(
    mockPrepaid,
    mockBankAccount,
    mockWallet,
    mockStash
)

val mockPrepaidWithMovements = FundWithMovements(
    fund = mockPrepaid,
    movementsIn = listOf(),
    movementsOut = listOf()
)

val mockWalletWithMovements = FundWithMovements(
    fund = mockWallet,
    movementsIn = listOf(),
    movementsOut = listOf()
)

val mockBankAccountWithMovements = FundWithMovements(
    fund = mockBankAccount,
    movementsIn = listOf(),
    movementsOut = listOf()
)

val mockFundWithMovements = listOf(
    mockPrepaidWithMovements,
    mockWalletWithMovements,
    mockBankAccountWithMovements
)

