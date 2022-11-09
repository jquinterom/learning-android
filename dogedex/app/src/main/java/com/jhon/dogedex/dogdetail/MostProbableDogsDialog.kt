package com.jhon.dogedex.dogdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhon.dogedex.R
import com.jhon.dogedex.dogdetail.ui.theme.DogedexTheme
import com.jhon.dogedex.model.Dog

@Composable
fun MostProbableDogsDialog(
    mostProbableDogs: MutableList<Dog>,
    onShowMostProbableDialogDismiss: () -> Unit,
    onItemClicked: (Dog) -> Unit
) {

    AlertDialog(onDismissRequest = {
        onShowMostProbableDialogDismiss()
    },
        title = {
            Text(
                text = stringResource(id = R.string.other_probable_dogs),
                color = colorResource(id = R.color.text_black),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium

            )
        },
        text = {
            MostProbableDogsList(mostProbableDogs) {
                onItemClicked(it)
                onShowMostProbableDialogDismiss()
            }
        },
        buttons = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onShowMostProbableDialogDismiss() }) {
                    Text(text = stringResource(id = R.string.dismiss))
                }
            }
        }
    )
}

@Composable
fun MostProbableDogsList(dogs: MutableList<Dog>, onItemClicked: (Dog) -> Unit) {
    Box(modifier = Modifier.height(250.dp)) {
        LazyColumn(
            content = {
                items(dogs) {
                    MostProbableDogItem(dog = it, onItemClicked)
                }
            })
    }
}

@Composable
fun MostProbableDogItem(dog: Dog, onItemClicked: (Dog) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(
                enabled = true,
                onClick = { onItemClicked(dog) }
            )
    ) {
        Text(
            text = dog.name,
            modifier = Modifier.padding(8.dp),
            color = colorResource(id = R.color.text_black)
        )
    }
}


@Composable
@Preview(showBackground = true)
fun MostProbableDogsDialogPreview() {
    DogedexTheme {
        Surface {
            MostProbableDogsDialog(
                mostProbableDogs = getFakeDogs(),
                onShowMostProbableDialogDismiss = { },
                onItemClicked = {}
            )

        }

    }
}

fun getFakeDogs(): MutableList<Dog> {
    val dogList = mutableListOf<Dog>()
    dogList.add(
        Dog(
            0, 1, "Perro 1", "Pug", "", "", "",
            "", "", "", "", inCollection = true
        ),

        )
    dogList.add(
        Dog(
            1, 2, "Perro 2", "Husky", "", "", "",
            "", "", "", "", inCollection = false
        )
    )

    return dogList
}
