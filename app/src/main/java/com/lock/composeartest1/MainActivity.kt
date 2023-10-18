package com.lock.composeartest1

import android.graphics.Bitmap.Config
import android.os.Bundle
import android.transition.CircularPropagation
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lock.composeartest1.ui.theme.ComposeArTest1Theme
import com.lock.composeartest1.ui.theme.Trans
import io.github.sceneview.Scene
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeArTest1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ArScreen()
                        Menu(Modifier.align(Alignment.BottomCenter))
                    }
                }
            }
        }
    }
}

@Composable
fun Menu(modifier: Modifier) {
    var currentIndex by remember {
        mutableStateOf(0)
    }
    val itemList = listOf(
        Models("Burgur", R.drawable.food1),
        Models("pizzza", R.drawable.s1),
        Models("islam", R.drawable.s1),
        Models("islams", R.drawable.s2),
        Models("islamss", R.drawable.s3),
        Models("isl", R.drawable.s4)
    )

    fun updateIndex(offset: Int) {
        currentIndex = (currentIndex + offset + itemList.size) / itemList.size
    }
    Row(
        modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = { updateIndex(-1) }) {
            Icon(painter = painterResource(id = R.drawable.back_icon), contentDescription = "back")
        }
        CircularImage(image = itemList.get(currentIndex).image)
        IconButton(onClick = { updateIndex(1) }) {
            Icon(
                painter = painterResource(id = R.drawable.back_icon),
                contentDescription = "forword"
            )
        }
    }
}

@Composable
fun CircularImage(modifier: Modifier = Modifier, image: Int) {
    Box(
        modifier = modifier
            .size(140.dp)
            .clip(CircleShape)
            .border(3.dp, Trans)
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = "islam",
            modifier = Modifier.size(140.dp),
            contentScale = ContentScale.FillBounds
        )

    }
}
//ZAL1234man
//zal

@Composable
fun ArScreen() {
    val nodes = remember {
        mutableStateListOf<ArNode>()
    }
    val modelNode = remember {
        mutableStateOf<ArModelNode?>(null)
    }
    val placeModel = remember {
        mutableStateOf(false)
    }
    ARScene(
        modifier = Modifier.fillMaxSize(),
        nodes = nodes,
        planeRenderer = true,
        onCreate = { arSceneView ->
            arSceneView.lightEstimationMode = com.google.ar.core.Config.LightEstimationMode.DISABLED
            arSceneView.planeRenderer.isShadowReceiver = false
            modelNode.value = ArModelNode(arSceneView.engine, PlacementMode.INSTANT).apply {
                loadModelGlbAsync(glbFileLocation = "model/pizza_free.glb"){

                }
                onAnchorChanged={
                    placeModel.value=!isAnchored
                }
                onHitResult = {node, hitResult ->
                    placeModel.value = node.isTracking

                }
                nodes.add(modelNode.value!!)
            }
        }
    )
    if (placeModel.value){
        Button(onClick = { modelNode.value?.anchor() }) {
            Text(text = "placement")
        }
    }


}

data class Models(val name: String, val image: Int)

@Composable
fun Greeting(
    name: String, modifier: Modifier
    = Modifier
) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeArTest1Theme {
        Box(modifier = Modifier.fillMaxSize()) {
            ArScreen()
            Menu(Modifier.align(Alignment.BottomCenter))
        }    }
}