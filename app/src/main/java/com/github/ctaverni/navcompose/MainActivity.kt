package com.github.ctaverni.navcompose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.github.ctaverni.navcompose.ui.theme.BootcampComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                MainScreen()
            }
        }
    }
}

fun settings(context: Context) {
    context.startActivity(Intent(context, SettingsActivity::class.java))
}

@Composable
fun MainScreen() {
    NavigationDrawer()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mainScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.testTag("homeScreen"),
            text = "Home Screen"
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawer() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val items = listOf(Icons.Default.PlayArrow, Icons.Default.Settings, Icons.Default.Info)
    ModalNavigationDrawer(
        modifier = Modifier.testTag("navigationDrawer"),
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                items.forEach { item ->
                    NavigationDrawerItem(
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        icon = {
                            Icon(
                                imageVector = item,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(
                                text = item.name
                            )
                        },
                        onClick = {
                           if (item == Icons.Default.Settings) {
                               settings(context)
                           }
                        },
                        selected = true
                    )
                }
            }
        },
        content = {
            Scaffold(
                topBar = {},
                content = { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { }
                    }
                }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
