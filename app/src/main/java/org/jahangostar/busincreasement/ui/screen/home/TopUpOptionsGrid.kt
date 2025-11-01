package org.jahangostar.busincreasement.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jahangostar.busincreasement.data.model.Credit
import org.jahangostar.busincreasement.ui.components.AmountChip
import org.jahangostar.busincreasement.util.Constants.formatCreditValue

@Composable
fun TopUpOptionsGrid(
    modifier: Modifier = Modifier,
    amounts: List<Credit>,
    onAmountSelected: (Int) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier.height(130.dp),
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(amounts, key = { it.id }) { amount ->
            AmountChip(
                displayAmount = "${formatCreditValue(amount.price)} تومان",
                onClick = { onAmountSelected(amount.price) }
            )
        }
    }
}