package com.aracecultura.arace.ui.components.carrinho

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.ui.theme.GoogleSans
import java.util.Locale
import kotlin.text.ifEmpty

@Composable
fun ProdutoCardItem(produto: Produto, onDeleteClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)), // Fundo levemente cinza
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {

            AsyncImage(
                model = produto.imagens.firstOrNull(), // Pega a primeira URL da lista, se existir
                contentDescription = produto.nome,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder), // Mostra enquanto carrega
                error = painterResource(id = R.drawable.placeholder), // Mostra se a URL for nula/inválida
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.35f)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )

            // Detalhes do Produto
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = produto.nome.ifEmpty { "Produto" },
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        fontFamily = GoogleSans,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Valor: R$ ${String.format(Locale("pt", "BR"), "%.2f", produto.preco)}",
                        fontSize = 16.sp,
                        fontFamily = GoogleSans,
                        color = Color.DarkGray
                    )
                }

                // Ícone de Lixeira
                Icon(
                    painter = painterResource(id = R.drawable.ic_deletar),
                    contentDescription = "Editar Perfil",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .clickable { onDeleteClick()}
                )
            }
        }
    }
}