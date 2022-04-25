package com.elong.lightboat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material.OutlinedTextField
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp


@Composable
fun SearchBar(url: MutableState<String>, modifier: Modifier = Modifier) {
    val input = remember { mutableStateOf("") }
    OutlinedTextField(input.value, {
        input.value = it
    }, modifier,
        trailingIcon = @Composable { Image(imageVector = Icons.Filled.Search, "Search",
            modifier = Modifier.clickable {
                url.value = input.value
            }) },
        textStyle = TextStyle(color = Color.Blue, fontSize = 20.sp))
}