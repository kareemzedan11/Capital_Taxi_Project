
import com.example.capital_taxi.domain.shared.LoginResponse
import com.example.capital_taxi.domain.shared.RegisterResponse
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.Part
import java.io.File

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("role") val role: String // سيتم تحديده داخل `registerUser`
)
data class RegisterDriver(
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("role") val role: String
)




//
interface AuthApiService {
    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>



    @Multipart
    @POST("auth/register")
    suspend fun registerDriver(
        @Part("name") name: RequestBody,
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("role") role: RequestBody,
        @Part nationalIdFront: MultipartBody.Part?,
        @Part nationalIdBack: MultipartBody.Part?,
        @Part licenseFront: MultipartBody.Part?,
        @Part licenseBack: MultipartBody.Part?,
        @Part carLicenseFront: MultipartBody.Part?,
        @Part carLicenseBack: MultipartBody.Part?
    ): Response<RegisterResponse>

}



data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("message") val message: String
)



data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String,

)

interface LoginApiService {
    @POST("auth/login")
    suspend fun loginuser(@Body request: LoginRequest): Response<LoginResponse>
}