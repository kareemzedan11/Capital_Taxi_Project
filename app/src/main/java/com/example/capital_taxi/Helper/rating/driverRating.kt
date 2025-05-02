package com.example.capital_taxi.Helper.rating
import android.content.Context
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.R
import com.example.capital_taxi.data.repository.graphhopper_response.Path
import com.example.capital_taxi.data.utils.DirectionsPrefs
import com.example.capital_taxi.domain.DirectionsViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateDriverBottomSheet(
    onSubmit: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var rating by remember { mutableStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("قيّم السائق", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            Row {
                (1..5).forEach { star ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (star <= rating) Color.Yellow else Color.Gray,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { rating = star }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(onClick = { onSubmit(rating) }, enabled = rating > 0) {
                Text("إرسال التقييم")
            }
        }
    }
}
fun submitRatingToFirebase(driverId: String, newRating: Int) {
    val db = FirebaseFirestore.getInstance()

    db.collection("drivers")
        .whereEqualTo("id", driverId)
        .get()
        .addOnSuccessListener { snapshot ->
            if (!snapshot.isEmpty) {
                val driverDoc = snapshot.documents[0]
                val data = driverDoc.data
                val currentTotal = (data?.get("rating") as? Map<*, *>)?.get("total")?.toString()?.toDoubleOrNull() ?: 0.0
                val currentCount = (data?.get("rating") as? Map<*, *>)?.get("count")?.toString()?.toIntOrNull() ?: 0

                val newTotal = currentTotal + newRating
                val newCount = currentCount + 1

                val updatedRating = mapOf(
                    "rating.total" to newTotal,
                    "rating.count" to newCount
                )

                driverDoc.reference.update(updatedRating)
                    .addOnSuccessListener {
                        Log.d("Rating", "✅ تم تحديث تقييم السائق")
                    }
                    .addOnFailureListener {
                        Log.e("Rating", "❌ فشل تحديث التقييم: ${it.message}")
                    }
            } else {
                Log.e("Rating", "❌ لم يتم العثور على السائق")
            }
        }
        .addOnFailureListener {
            Log.e("Rating", "❌ خطأ في جلب السائق: ${it.message}")
        }
}
