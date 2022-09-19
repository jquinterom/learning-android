package com.jhon.dogedex.dogdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.model.Dog

@Composable
fun DogDetailScreen(
    dog: Dog,
    status: ApiResponseStatus<Any>? = null,
    onButtonClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.secondary_background))
            .padding(start = 8.dp, end = 8.dp, bottom = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        DogInformation(dog)

        Image(
            modifier = Modifier
                .width(270.dp)
                .padding(top = 80.dp),
            painter = rememberAsyncImagePainter(dog.imageUrl),
            contentDescription = dog.name
        )

        FloatingActionButton(
            modifier = Modifier.align(alignment = Alignment.BottomCenter),
            onClick = { onButtonClicked() }) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = null)
        }

        if (status is ApiResponseStatus.Loading) LoadingWheel()
    }
}


@Composable
fun LoadingWheel() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.Red
        )
    }
}

@Composable
fun DogInformation(dog: Dog) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 180.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            color = colorResource(
                id = android.R.color.white
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.dog_index_format, dog.index),
                    fontSize = 32.sp,
                    color = colorResource(id = R.color.text_black),
                    textAlign = TextAlign.End
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 32.dp, bottom = 8.dp, start = 8.dp, end = 8.dp
                        ),
                    text = dog.name,
                    color = colorResource(id = R.color.text_black),
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium,
                )

                LifeIcon()

                Text(
                    text = stringResource(
                        id = R.string.dog_life_expectancy_format,
                        dog.lifeExpectancy
                    ),
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.text_black),
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = dog.temperament,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.text_black),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Divider(
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 16.dp),
                    color = colorResource(id = R.color.divider),
                    thickness = 1.dp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DogDataColumn(
                        stringResource(id = R.string.female),
                        dog.weightFemale,
                        dog.heightFemale,
                        modifier = Modifier.weight(1f)
                    )

                    VerticalDivider()

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = dog.type,
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.text_black),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp),
                        )

                        Text(
                            text = stringResource(id = R.string.group),
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.dark_gray),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }

                    VerticalDivider()

                    DogDataColumn(
                        stringResource(id = R.string.male),
                        dog.weightMale,
                        dog.heightMale,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun VerticalDivider() {
    Divider(
        modifier = Modifier
            .height(42.dp)
            .width(1.dp),
        color = colorResource(id = R.color.divider)
    )
}

@Composable
private fun DogDataColumn(
    gender: String, weight: String, height: String,
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = gender,
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.text_black),
            modifier = Modifier.padding(top = 8.dp),
        )

        Text(
            text = weight,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = colorResource(id = R.color.text_black),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp),
        )

        Text(
            text = stringResource(id = R.string.weight),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = colorResource(id = R.color.dark_gray),
        )

        Text(
            text = height,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = colorResource(id = R.color.text_black),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp),
        )

        Text(
            text = stringResource(id = R.string.height),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = colorResource(id = R.color.dark_gray),
        )
    }
}


@Preview
@Composable
fun DogDetailScreenPreview() {
    val dog = Dog(
        1L, 88, "Pug", "Herding", "70", "33",
        "https://firebasestorage.googleapis.com/v0/b/perrodex-app.appspot.com/o/dog_details_images%2Fn02086079-pekinese.png?alt=media&token=f3cb4225-6690-42f2-a492-b77fcdeb5ee3",
        "10-11", "Friendly", "5", "6"
    )
    DogDetailScreen(dog, onButtonClicked = {})
}

@Composable
private fun LifeIcon() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 80.dp, end = 80.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = colorResource(id = R.color.color_primary)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_hearth_white),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .padding(4.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(bottomEnd = 2.dp, topEnd = 2.dp),
            modifier = Modifier
                .width(200.dp)
                .height(6.dp),
            color = colorResource(id = R.color.color_primary)
        ) {}
    }
}