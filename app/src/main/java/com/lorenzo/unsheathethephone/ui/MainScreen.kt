package com.lorenzo.unsheathethephone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.adaptive.AdaptiveBanner

@Composable
fun MainScreen() {
    var isChecked by remember { mutableStateOf(false) }
    var sensibility by remember { mutableStateOf(150) }
    val sounds = listOf("Sword", "Fart", "Lightsaber", "Whip")
    var selectedSound by remember { mutableStateOf(sounds[0]) }
    val accuracy = listOf("Normal", "High")
    var selectedAccuracy by remember { mutableStateOf(accuracy[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(id = R.string.check_text), fontSize = 20.sp)
            Switch(checked = isChecked, onCheckedChange = { isChecked = it })
        }

        Text(text = stringResource(id = R.string.choose_sounds), fontSize = 17.sp, modifier = Modifier.padding(top = 30.dp))
        Text(
            text = selectedSound,
            fontSize = 20.sp,
            color = Color.Blue,
            modifier = Modifier
                .padding(vertical = 5.dp)
                .background(Color.LightGray)
                .padding(12.dp)
                .clickable { /* Show sound selector dialog */ }
        )

        Text(text = stringResource(id = R.string.sens), fontSize = 17.sp, modifier = Modifier.padding(top = 30.dp))
        Slider(
            value = sensibility.toFloat(),
            onValueChange = { sensibility = it.toInt() },
            valueRange = 0f..300f,
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = stringResource(id = R.string.acc), fontSize = 17.sp, modifier = Modifier.padding(top = 30.dp))
        Text(text = stringResource(id = R.string.acc), fontSize = 17.sp, modifier = Modifier.padding(top = 30.dp))
        Column {
            RadioButton(
                selected = selectedAccuracy == "Normal",
                onClick = { selectedAccuracy = "Normal" },
                modifier = Modifier.padding(8.dp)
            )
            Text(text = stringResource(id = R.string.acc_norm))
            RadioButton(
                selected = selectedAccuracy == "High",
                onClick = { selectedAccuracy = "High" },
                modifier = Modifier.padding(8.dp)
            )
            Text(text = stringResource(id = R.string.acc_high))
        }

        Spacer(modifier = Modifier.weight(1f))

        AdaptiveBanner(
            adUnitId = stringResource(id = R.string.banner_ad_unit_id),
            modifier = Modifier.fillMaxWidth()
        )
    }
}