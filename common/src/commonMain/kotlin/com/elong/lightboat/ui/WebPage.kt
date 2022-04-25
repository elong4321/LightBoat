package com.elong.lightboat.ui

import android.util.Log
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.elong.lightboat.SearchBar

val ANIMATION_DURATION = 300

@Composable
fun WebPage(webview: @Composable (MutableState<String>, Modifier?)->Unit,
            searchBar: @Composable (MutableState<String>, Modifier?)->Unit = @Composable { urlString, modifier ->
                SearchBar(urlString, modifier = (modifier ?: Modifier).fillMaxWidth().height(60.dp)) },
            arrangement: Arrangement.Vertical = Arrangement.Bottom) {
    var url = remember{ mutableStateOf("") }
    var webviewContainerHeight = remember{ mutableStateOf(0) }
    var focused = remember { mutableStateOf(false) }
    var marginTop = animateIntAsState(targetValue = if (focused.value) 0 else webviewContainerHeight.value/2,
        animationSpec = tween(ANIMATION_DURATION))

    Box(modifier = Modifier.fillMaxSize().onSizeChanged {
        webviewContainerHeight.value = it.height;
        Log.d("webpage", "height= " + webviewContainerHeight);
    }) {
        webview(url, Modifier)
        Log.d("webpage", "searchBar.= " + webviewContainerHeight);
        searchBar(url, Modifier.onFocusChanged {
            focused.value = it.isFocused
            Log.d("webpage", "searchBar onFocusChanged=" + it.isFocused + " " + it.hasFocus)
        }.padding(20.dp, marginTop.value.dp, 20.dp, 0.dp))
    }

//    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = arrangement) {
//        webview(url, Modifier.weight(1F))
//        searchBar(url, Modifier)
//    }
}