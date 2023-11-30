package updata.api

import com.passenger.bean.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface KtService {

    @Multipart
    @POST("api/MinioFile/Upload")
    fun geturl(@Part file: MultipartBody.Part?): Call<UrlnameBean>

    @POST("api/TblPSMSideDoorAudit/SideDoorRegist")
    fun getSideDoorRegist(@Body body: ArrayList<DataItem>): Call<Boolean>

    @POST("api/TblPSMSideDoorAudit/SideDoorRegist")
    fun getSideDoorRegists(@Body body: ArrayList<DataItems>): Call<Boolean>


}