package com.davsilvam.alcohol_or_gas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davsilvam.alcohol_or_gas.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Wrapper {
                    Home()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Wrapper(content: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Álcool ou Gasolina") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}

@Composable
fun Home(modifier: Modifier = Modifier) {
    var alcoholText by remember { mutableStateOf("") }
    var gasText by remember { mutableStateOf("") }
    var gasStationName by remember { mutableStateOf("") }
    var use75Percent by rememberSaveable { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }

    val threshold = if (use75Percent) 0.75 else 0.7

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = alcoholText,
            onValueChange = { alcoholText = it },
            label = { Text("Preço do Álcool (R$)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = gasText,
            onValueChange = { gasText = it },
            label = { Text("Preço da Gasolina (R$)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = gasStationName,
            onValueChange = { gasStationName = it },
            label = { Text("Nome do Posto (opcional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (use75Percent) "Usando 75%" else "Usando 70%",
                style = MaterialTheme.typography.bodyLarge
            )

            Switch(
                checked = use75Percent,
                onCheckedChange = { use75Percent = it }
            )
        }

        Button(
            onClick = {
                val alcohol = alcoholText.toDoubleOrNull()
                val gas = gasText.toDoubleOrNull()

                resultMessage = calculateBestFuel(
                    alcohol,
                    gas,
                    threshold,
                    gasStationName
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Calcular",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (resultMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = resultMessage,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

fun calculateBestFuel(
    alcoholPrice: Double?,
    gasPrice: Double?,
    threshold: Double = 0.7,
    gasStationName: String = ""
): String {
    if (alcoholPrice == null || gasPrice == null) {
        return "❌ Por favor, insira valores válidos para ambos os combustíveis."
    }
    if (alcoholPrice <= 0 || gasPrice <= 0) {
        return "❌ Os preços devem ser maiores que zero."
    }

    val ratio = alcoholPrice / gasPrice
    val stationInfo = if (gasStationName.isNotBlank()) " no posto $gasStationName" else ""

    return if (ratio <= threshold) {
        "✅ Abasteça com ÁLCOOL$stationInfo.\n" +
                "O álcool está ${
                    String.format(
                        "%.1f",
                        ratio * 100
                    )
                }% do preço da gasolina (limite ${threshold * 100}%)."
    } else {
        "✅ Abasteça com GASOLINA$stationInfo.\n" +
                "O álcool está ${
                    String.format(
                        "%.1f",
                        ratio * 100
                    )
                }% do preço da gasolina (acima de ${threshold * 100}%)."
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomePreview() {
    AppTheme {
        Wrapper {
            Home()
        }
    }
}
