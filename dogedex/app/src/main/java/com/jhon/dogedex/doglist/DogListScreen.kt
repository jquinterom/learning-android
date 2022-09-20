package com.jhon.dogedex.doglist

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.composables.BackNavigationIcon
import com.jhon.dogedex.composables.ErrorDialog
import com.jhon.dogedex.composables.LoadingWheel
import com.jhon.dogedex.model.Dog

private const val GRID_SPAN_COUNT = 3

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalMaterialApi
@Composable
fun DogListScreen(
    onNavigationIconClick: () -> Unit,
    onDogClicked: (Dog) -> Unit,
    viewModel: DogListViewModel = hiltViewModel(),
) {
    val status = viewModel.status.value
    val dogList = viewModel.dogList.value

    Scaffold(
        topBar = { DogListScreenTopBar(onNavigationIconClick) }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(GRID_SPAN_COUNT), content = {
                items(dogList) {
                    DogGridItem(dog = it, onDogClicked = onDogClicked)
                }
            },
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }

    if (status is ApiResponseStatus.Loading) {
        LoadingWheel()
    } else if (status is ApiResponseStatus.Error) {
        ErrorDialog(
            messageId = status.messageId,
            onErrorDialogDismiss = { viewModel.resetApiResponseStatus() })
    }
}

@Composable
fun DogListScreenTopBar(onClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.y_dog_collection)) },
        backgroundColor = Color.White,
        contentColor = Color.Black,
        navigationIcon = { BackNavigationIcon(onClick = onClick) },
        elevation = 4.dp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}


@ExperimentalMaterialApi
@Composable
fun DogGridItem(dog: Dog, onDogClicked: (Dog) -> Unit) {
    if (dog.inCollection) {
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .height(100.dp)
                .width(100.dp),
            onClick = { onDogClicked(dog) },
            shape = RoundedCornerShape(4.dp)
        ) {
            Image(
                modifier = Modifier
                    .background(Color.White),
                painter = rememberAsyncImagePainter(model = dog.imageUrl),
                contentDescription = null
            )
        }
    } else {
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .height(100.dp)
                .width(100.dp),
            color = Color.Red,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = dog.index.toString(),
                color = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
