package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capital_taxi.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

@Composable
fun TripArrivedCard2(
    destination: String = "Unknown Destination", // قيمة افتراضية
    fare: String = "0", // قيمة افتراضية
    distance: String = "0", // قيمة افتراضية
    tripId: String = UUID.randomUUID().toString(), // قيمة افتراضية عشوائية
    driverId: String = UUID.randomUUID().toString(), // قيمة افتراضية عشوائية
    userId: String? = null, // يمكن أن تكون null
    onProblemSubmitted: () -> Unit = {},
    onclick: () -> Unit,
    userIdToRate:String
) {
    var rating by remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showProblemReport by remember { mutableStateOf(false) } // التحكم في عرض قسم الإبلاغ عن المشكلة
    var problemDescription by remember { mutableStateOf("") } // نص وصف المشكلة
    var isSubmitting by remember { mutableStateOf(false) }
    val problemCategories = listOf(
        ProblemCategory("harassment", "تحرش", Priority.HIGH),
        ProblemCategory("abuse", "سب أو إهانة", Priority.HIGH),
        ProblemCategory("payment", "مشكلة في الدفع", Priority.MEDIUM),
        ProblemCategory("behavior", "سلوك غير لائق", Priority.MEDIUM),
        ProblemCategory("route", "مشكلة في الطريق", Priority.LOW),
        ProblemCategory("other", "أخرى", Priority.LOW)
    )

    var selectedCategory by remember { mutableStateOf<ProblemCategory?>(null) }
    var showCategoryError by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                // Header Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.trip_arrived),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorResource(R.color.primary_color)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.destination),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = destination.take(20),
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        text = stringResource(R.string.arrived),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color.Gray, thickness = 1.dp)

                Spacer(modifier = Modifier.height(8.dp))

                // Fare and Distance Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.destination),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$distance Km" ,
                            fontSize = 14.sp
                        )
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.fare),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$fare EGP",
                            fontSize = 14.sp,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Report Problem Button - Small Button
                if (!showProblemReport) {
                    TextButton(
                        onClick = { showProblemReport = true },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Text(
                            text = "Report a problem?",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }

                // Problem Report Section (ظهر عند الضغط على الزر)
                if (showProblemReport) {
                    Column {
                        Text(
                            text = "Describe the problem",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // قسم اختيار نوع المشكلة
                        Text(
                            text = "Problem Category",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        // عرض تصنيفات المشكلات على شكل أزرار
                        LazyRow {
                            items(problemCategories) { category ->
                                FilterChip(
                                    selected = selectedCategory == category,
                                    onClick = {
                                        selectedCategory = category
                                        showCategoryError = false
                                    },
                                    label = { Text(category.name) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = colorResource(R.color.primary_color),
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }

                        if (showCategoryError) {
                            Text(
                                text = "Please select a problem category",
                                color = Color.Red,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextField(
                            value = problemDescription,
                            onValueChange = { problemDescription = it },
                            placeholder = {
                                Text("Please describe the problem you faced")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            // إرسال المشكلة
                            Button(
                                onClick = {
                                    if (selectedCategory == null) {
                                        showCategoryError = true
                                        return@Button
                                    }

                                    isSubmitting = true
                                    submitProblemToFirestore(
                                        tripId = tripId,
                                        driverId = driverId,
                                        userId = userId ?: "123",
                                        problemDescription = problemDescription,
                                        category = selectedCategory!!.id,
                                        priority = selectedCategory!!.priority.name,
                                        onSuccess = {
                                            isSubmitting = false
                                            showProblemReport = false
                                            problemDescription = ""
                                            selectedCategory = null
                                            showDialog = true
                                            onProblemSubmitted()
                                        },
                                        onFailure = {
                                            isSubmitting = false
                                            // يمكنك عرض رسالة خطأ هنا
                                        }
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red
                                ),
                                enabled = !isSubmitting && problemDescription.isNotEmpty()
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(color = Color.White)
                                } else {
                                    Text("Submit Problem")
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // إلغاء
                            OutlinedButton(
                                onClick = {
                                    showProblemReport = false
                                    problemDescription = ""
                                    selectedCategory = null
                                    showCategoryError = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                        }}}

                // Rating Section - Driver Rating User
                Text(
                    text = stringResource(R.string.rate_user),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Rating Bar (Stars)
                RatingBar(rating = rating, onRatingChanged = { newRating -> rating = newRating })

                Spacer(modifier = Modifier.height(16.dp))

                // Comment Section
                Text(
                    text = stringResource(R.string.leave_comment),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                TextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = {
                        Text(stringResource(R.string.comment_placeholder))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button (Gray until rating and/or comment provided)
                // أضف هذه المتغيرات في بداية Composable
                val firestore = FirebaseFirestore.getInstance()
                val currentUser = FirebaseAuth.getInstance().currentUser // المستخدم الحالي الذي يقوم بالتقييم

                Button(
                    onClick = {
                        showDialog = true
                  // استبدل هذا بمعرف المستخدم المراد تقييمه

                        // البحث عن المستند الذي يحتوي على id = userIdToRate
                        firestore.collection("users")
                            .whereEqualTo("id", userIdToRate)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                if (!querySnapshot.isEmpty) {
                                    // نحصل على أول مستند (من المفترض أن يكون واحدًا فقط)
                                    val document = querySnapshot.documents[0]

                                    // الحصول على البيانات الحالية
                                    val currentRatings = document.get("rating") as? Map<String, Any> ?: mapOf(
                                        "count" to 0,
                                        "total" to 0
                                    )

                                    val currentCount = (currentRatings["count"] as? Number)?.toInt() ?: 0
                                    val currentTotal = (currentRatings["total"] as? Number)?.toInt() ?: 0

                                    // تحديث القيم
                                    val newCount = currentCount + 1
                                    val newTotal = currentTotal + rating.toInt()

                                    // إنشاء Map جديد مع البيانات المحدثة
                                    val updatedRatings = mapOf(
                                        "count" to newCount,
                                        "total" to newTotal
                                    )

                                    // تحديث المستند في Firestore
                                    document.reference.update("rating", updatedRatings)

                                        .addOnSuccessListener {
                                            // إذا كان هناك تعليق، نضيفه إلى مجموعة منفصلة
                                            if (comment.isNotEmpty()) {
                                                val reviewData = hashMapOf(
                                                    "userId" to currentUser?.uid,
                                                    "rating" to rating,
                                                    "comment" to comment,
                                                    "timestamp" to FieldValue.serverTimestamp()
                                                )

                                                document.reference.collection("reviews")
                                                    .add(reviewData)
                                            }
                                        }
                                } else {
                                    // لا يوجد مستخدم بهذا المعرف
                                    Log.e("Rating", "No user found with id: $userIdToRate")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("Rating", "Error finding user", e)
                            }
                    },
                    enabled = rating > 0f || comment.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (rating > 0f || comment.isNotEmpty()) colorResource(
                            R.color.primary_color
                        ) else Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.submit_rating),
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Go to Home Button
                Button(
                    onClick = { onclick()},
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primary_color)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.go_to_home),
                        color = Color.Black
                    )
                }
            }
        }

        // Show Dialog when rating is submitted
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(stringResource(R.string.rating_submitted))
                },
                text = {
                    Text(stringResource(R.string.rating_submitted_message))
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false
                        onclick()}) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

fun submitProblemToFirestore(
    tripId: String,
    driverId: String,
    userId: String?,
    problemDescription: String,
    category: String,
    priority: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    // إعداد البيانات مع قيم افتراضية للبيانات المطلوبة
    val problemReport = hashMapOf(
        "tripId" to (tripId.ifEmpty { UUID.randomUUID().toString() }),
        "driverId" to (driverId.ifEmpty { UUID.randomUUID().toString() }),
        "userId" to (userId ?: "guest_${UUID.randomUUID()}"),
        "problemDescription" to (problemDescription.ifEmpty { "No description provided" }),
        "category" to (category.ifEmpty { "other" }),
        "priority" to (priority.ifEmpty { "MEDIUM" }),
        "timestamp" to FieldValue.serverTimestamp(),
        "status" to "pending",
        "resolvedAt" to Timestamp(0, 0),
        "resolved" to false, // إضافة حقل جديد
        "adminNotes" to "", // إضافة حقل جديد للملاحظات
        "attachmentUrl" to "" // إضافة حقل للمرفقات المستقبلية
    )

    db.collection("problemReports")
        .add(problemReport)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { e ->
            onFailure(e)
        }
}

// فئة تمثل تصنيف المشكلة مع قيم افتراضية
data class ProblemCategory(
    val id: String = "other",
    val name: String = "أخرى",
    val priority: Priority = Priority.MEDIUM
)

// enum للأولوية مع دالة لتحويل النص إلى Priority
enum class Priority {
    LOW, MEDIUM, HIGH;

    companion object {
        fun fromString(value: String?): Priority {
            return when (value?.uppercase()) {
                "HIGH" -> HIGH
                "LOW" -> LOW
                else -> MEDIUM // القيمة الافتراضية
            }
        }
    }
}

// دالة مساعدة لإنشاء معرّف فريد إذا لم يكن موجوداً
fun generateUniqueId(): String {
    return UUID.randomUUID().toString()
}