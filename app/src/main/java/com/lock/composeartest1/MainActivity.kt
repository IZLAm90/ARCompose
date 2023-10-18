package com.lock.composeartest1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.ar.core.Config
import com.lock.composeartest1.ui.theme.ComposeArTest1Theme
import com.lock.composeartest1.ui.theme.Trans
import io.github.sceneview.Scene
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode

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
                    Box(modifier = Modifier.fillMaxSize()){
                        val currentModel = remember {
                            mutableStateOf("pizza_free")
                        }
                        ARScreen(currentModel.value)
                        Menu(modifier = Modifier.align(Alignment.BottomCenter)){
                            currentModel.value = it
                        }

                    }
                }
            }
        }
    }
}
@Composable
fun Menu(modifier: Modifier,onClick:(String)->Unit) {
    var currentIndex by remember {
        mutableStateOf(0)
    }

    val itemsList = listOf(
        Food("burger",R.drawable.food1),
        Food("instant",R.drawable.s1),
        Food("momos",R.drawable.s2),
        Food("pizza",R.drawable.s3),
        Food("cookies_set",R.drawable.s4),
        Food("fast-food",R.drawable.food2),
        Food("pizza_free",R.drawable.food3),
        Food("Wolves",R.drawable.food4),
        )
    fun updateIndex(offset:Int){
        currentIndex = (currentIndex+offset + itemsList.size) % itemsList.size
        onClick(itemsList[currentIndex].name)
    }
    Row(modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = {
            updateIndex(-1)
        }) {
            Icon(painter = painterResource(id = R.drawable.back_icon), contentDescription ="previous" )
        }

        CircularImage( image=itemsList[currentIndex].imageId )

        IconButton(onClick = {
            updateIndex(1)
        }) {
            Icon(painter = painterResource(id = R.drawable.back_icon), contentDescription ="next")
        }
    }

}

//@Composable
//fun Menu(modifier: Modifier,onClick:(String)->Unit) {
//    var currentIndex by remember {
//        mutableStateOf(0)
//    }
//    val itemList = listOf(
//        Models("Wolves", R.drawable.food1),
//        Models("pizza_free", R.drawable.s1),
//        Models("fast-food", R.drawable.s1),
//        Models("cookies_set", R.drawable.s2),
//        Models("islamss", R.drawable.s3),
//        Models("isl", R.drawable.s4)
//    )
//
//    fun updateIndex(offset: Int) {
//        currentIndex = (currentIndex + offset + itemList.size) % itemList.size
//        onClick(itemList[currentIndex].name)
//    }
//    Row(
//        modifier.fillMaxSize(),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceAround
//    ) {
//        IconButton(onClick = { updateIndex(-1) }) {
//            Icon(painter = painterResource(id = R.drawable.back_icon), contentDescription = "back",
//                modifier.rotate(180F))
//        }
//        CircularImage(image = itemList.get(currentIndex).image)
//        IconButton(onClick = { updateIndex(1) }) {
//            Icon(
//                painter = painterResource(id = R.drawable.back_icon),
//                contentDescription = "forword"
//            )
//        }
//    }
//}

@Composable
fun CircularImage(modifier: Modifier = Modifier, image: Int) {
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .border(3.dp, Trans)
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = "islam",
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.FillBounds
        )

    }
}


@Composable
fun ARScreen(model:String) {
    val nodes = remember {
        mutableListOf<ArNode>()
    }
    val modelNode = remember {
        mutableStateOf<ArModelNode?>(null)
    }
    val placeModelButton = remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier.fillMaxSize()){
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = {arSceneView ->
                arSceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED
                arSceneView.planeRenderer.isShadowReceiver = false
                modelNode.value = ArModelNode(arSceneView.engine,PlacementMode.INSTANT).apply {
                    loadModelGlbAsync(
                        glbFileLocation = "model/${model}.glb",
                        scaleToUnits = 0.8f
                    ){

                    }
                    onAnchorChanged = {
                        placeModelButton.value = !isAnchored
                    }
                    onHitResult = {node, hitResult ->
                        placeModelButton.value = node.isTracking
                    }

                }
                nodes.add(modelNode.value!!)
            },
            onSessionCreate = {
                planeRenderer.isVisible = false
            }
        )
        if(placeModelButton.value){
            Button(onClick = {
                modelNode.value?.anchor()
            }, modifier = Modifier.align(Alignment.Center)) {
                Text(text = "Place It")
            }
        }
    }


    LaunchedEffect(key1 = model){
        modelNode.value?.loadModelGlbAsync(
            glbFileLocation = "model/${model}.glb",
            scaleToUnits = 0.8f
        )
        Log.e("errorloading","ERROR LOADING MODEL")
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
data class Food(var name:String,var imageId:Int)

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeArTest1Theme {
        Box(modifier = Modifier.fillMaxSize()) {
//            ArScreen()
//            Menu(Modifier.align(Alignment.BottomCenter))
        }    }
}