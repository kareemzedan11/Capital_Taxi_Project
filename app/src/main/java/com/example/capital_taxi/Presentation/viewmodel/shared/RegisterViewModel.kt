//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.capital_taxi.data.repository.AuthRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
////class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
////    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
////    val registerState: StateFlow<RegisterState> get() = _registerState
////
////    private val _message = MutableStateFlow<String?>(null)
////    val message: StateFlow<String?> get() = _message
////
////    fun register(name: String, phone: String, email: String, password: String, role: String) {
////        _registerState.value = RegisterState.Loading
////        viewModelScope.launch {
////            try {
////                val response = authRepository.register(name, phone, email, password, role)
////                _registerState.value = RegisterState.Success(response.token)
////                _message.value = "تم إرسال البيانات بنجاح!" // رسالة نجاح
////            } catch (e: Exception) {
////                _registerState.value = RegisterState.Error(e.message ?: "حدث خطأ ما")
////                _message.value = "فشل إرسال البيانات: ${e.message}" // رسالة خطأ
////            }
////        }
////    }
////
////    fun clearMessage() {
////        _message.value = null // مسح الرسالة بعد عرضها
////    }
////}
////
////sealed class RegisterState {
////    object Idle : RegisterState()
////    object Loading : RegisterState()
////    data class Success(val token: String) : RegisterState()
////    data class Error(val message: String) : RegisterState()
////}