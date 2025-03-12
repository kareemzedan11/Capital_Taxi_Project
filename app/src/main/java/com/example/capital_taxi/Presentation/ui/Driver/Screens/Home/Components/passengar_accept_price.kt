import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun passengerAcceptPrice() {
    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()

                .height(100.dp)
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.fillMaxWidth(.6f)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.waiting_for_passenger_acceptance),
                            fontSize = responsiveTextSize(
                                fraction = 0.06f,
                                minSize = 12.sp,
                                maxSize = 14.sp
                            ),


                            fontFamily = CustomFontFamily,
                            color = Color.Black
                        )
                        Text(
                            text = stringResource(R.string.new_price_sent_to_passenger),
                            fontSize = responsiveTextSize(
                                fraction = 0.06f,
                                minSize = 12.sp,
                                maxSize = 16.sp
                            ),


                            fontFamily = CustomFontFamily,
                            color = Color.Gray
                        )

                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                CircularCountdown()


            }
        }
    }
}


@Composable
fun CircularCountdown() {
    val backgroundcolor = colorResource(R.color.primary_color)

    val countdownTime = 15 // Total countdown seconds
    var currentTime by remember { mutableStateOf(countdownTime) }
    val animatedProgress = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            for (time in countdownTime downTo 0) {
                currentTime = time
                animatedProgress.animateTo(
                    targetValue = time / countdownTime.toFloat(),
                    animationSpec = tween(durationMillis = 1000)
                )
                delay(1000)
            }
        }
    }

    Box(
        modifier = Modifier.size(45.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.LightGray,
                style = Stroke(width = 8.dp.toPx())
            )
            drawArc(
                color = backgroundcolor,
                startAngle = -90f,
                sweepAngle = 360 * animatedProgress.value,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx())
            )
        }


        Text(
            text = "$currentTime S",
            fontSize = 11.sp,
            color = Color.Black
        )
    }
}
