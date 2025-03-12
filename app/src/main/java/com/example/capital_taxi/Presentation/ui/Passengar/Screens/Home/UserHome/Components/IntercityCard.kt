import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.FareViewModel

@Composable
fun IntercityCard(text: String) {
    val displayText by remember { mutableStateOf(text) }
  val  fareViewModel: FareViewModel = viewModel()
    val fare by fareViewModel.fare.observeAsState(0.0)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(5.dp, Color.White, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.background(Color.White)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().background(Color.White)
            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(R.drawable.uber),
                    contentDescription = "car logo"
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            displayText, // Use the state variable directly
                            fontSize = 20.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("4", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.W500)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Person, contentDescription = null)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            "$fare EGP ",
                            fontSize = 21.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.W600
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "3:45am dropoff",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.W300
                    )
                }
            }
        }
    }
}
