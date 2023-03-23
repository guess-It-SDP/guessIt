package com.github.freeman.bootcamp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.provider.Settings.Secure.getString
import android.provider.Settings.System.getString
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.TypedArrayUtils.getString
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.common.io.Resources.getResource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.grpc.internal.JsonUtil.getString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = Firebase.auth.currentUser


        setContent {
            val context = LocalContext.current


            val displayName = remember { mutableStateOf("Chris P. Bacon") }

            BootcampComposeTheme(darkTheme = false) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top appbar
                    TopAppbarProfile(context = context)
                    //TODO get info from database before creating profile
                    Profile(displayName = displayName)
                }
            }

        }
    }
}

private val optionsList: ArrayList<OptionsData> = ArrayList()


@Composable
fun TopAppbarProfile(context: Context) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarProfile"),
        title = {
            Text(
                text = "Profile",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(onClick = {
                Toast.makeText(context, "Nav Button", Toast.LENGTH_SHORT).show()
                val activity = (context as? Activity)
                activity?.finish()
            }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }
        }
    )
}

@Composable
fun Profile(context: Context = LocalContext.current, displayName: MutableState<String>) {

    // This indicates if the optionsList has data or not
    // Initially, the list is empty. So, its value is false.
    var listPrepared by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            optionsList.clear()

            // Add the data to optionsList
            prepareOptionsData()

            listPrepared = true
        }
    }

    if (listPrepared) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("profile")
        ) {

            item {
                // User's image, name, email and edit button
                UserDetails(context = context, displayName = displayName)
            }

            // Show the options
            items(optionsList) { item ->
                OptionsItemStyle(item = item, context = context)
            }
        }
    }
}

// This composable displays user's image, name, email and edit button
@Composable
private fun UserDetails(context: Context, displayName: MutableState<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("userDetails"),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // User's image
        Image(
            modifier = Modifier
                .size(72.dp)
                .clip(shape = CircleShape),
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Your Image"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 3f, fill = false)
                    .padding(start = 16.dp)
            ) {

                // User's name
                Text(
                    text = displayName.value, //TODO get name from database
                    style = TextStyle(
                        fontSize = 22.sp,
                        //fontFamily = FontFamily(Font(R.font.roboto_bold, FontWeight.Bold)),
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // User's email
                Text(
                    text = "email123@email.com",
                    style = TextStyle(
                        fontSize = 14.sp,
                        //fontFamily = FontFamily(Font(R.font.roboto_regular, FontWeight.Normal)),
                        color = Color.Gray,
                        letterSpacing = (0.8).sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Edit button
            IconButton(
                modifier = Modifier
                    .weight(weight = 1f, fill = false),
                onClick = {
                    context.startActivity(Intent(context, EditProfileActivity::class.java))
                    Toast.makeText(context, "Edit Button", Toast.LENGTH_SHORT).show()
                }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Details",
                    tint = MaterialTheme.colors.primary
                )
            }

        }
    }
}

// Row style for options
@Composable
private fun OptionsItemStyle(item: OptionsData, context: Context) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = true) {
                Toast
                    .makeText(context, item.title, Toast.LENGTH_SHORT)
                    .show()
            }
            .padding(all = 16.dp)
            .testTag("optionsItemStyle"),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Icon
        Icon(
            modifier = Modifier
                .size(32.dp),
            imageVector = item.icon,
            contentDescription = item.title,
            tint = MaterialTheme.colors.primary
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 3f, fill = false)
                    .padding(start = 16.dp)
            ) {

                // Title
                Text(
                    text = item.title,
                    style = TextStyle(
                        fontSize = 18.sp,
                        //fontFamily = FontFamily(Font(R.font.roboto_medium, FontWeight.Medium))
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Sub title
                Text(
                    text = item.subTitle,
                    style = TextStyle(
                        fontSize = 14.sp,
                        letterSpacing = (0.8).sp,
                        //fontFamily = FontFamily(Font(R.font.roboto_regular, FontWeight.Normal)),
                        color = Color.Gray
                    )
                )

            }

            // Right arrow icon
            Icon(
                modifier = Modifier
                    .weight(weight = 1f, fill = false),
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = item.title,
                tint = Color.Black.copy(alpha = 0.70f)
            )
        }

    }
}

private fun prepareOptionsData() {

    val appIcons = Icons.Rounded


    optionsList.add(
        OptionsData(
            icon = appIcons.PlayArrow,
            title = "Game Stats",
            subTitle = "Check your Game statistics"
        )
    )

    optionsList.add(
        OptionsData(
            icon = appIcons.Settings,
            title = "Parameters",
            subTitle = "App parameters"
        )
    )

    optionsList.add(
        OptionsData(
            icon = appIcons.Info,
            title = "Help",
            subTitle = "Get some help about how the app works"
        )
    )

}

data class OptionsData(val icon: ImageVector, val title: String, val subTitle: String)

//@Preview(showBackground = true)
//@Composable
//fun ProfileScreenPreview() {
//    ProfileScreen()
//}

@Preview
@Composable
fun TopAppBarPreview() {
    TopAppbarProfile(LocalContext.current)
}

