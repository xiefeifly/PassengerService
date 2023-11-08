package updata.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.passenger.bean.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface KtService {
//    @POST("api/TblAppUpdateConfig/ApiSearch")
//    fun getUpdateConfigApiSearch(@Body body: UpdataReq): Call<UpdateRep>

//    @POST("api/MinioFile/Upload")
//    fun getUpdateConfigApiSearch(@Body body: UpdataReq): Call<UpdateRep>

    @Multipart
    @POST("api/MinioFile/Upload")
    fun geturl(@Part file: MultipartBody.Part?): Call<UrlnameBean>
//    @Multipart
//    @POST("api/MinioFile/Upload")
//    fun geturl(@Body body:  MultipartBody): Call<UrlnameBean>

    @POST("api/TblPSMSideDoorAudit/SideDoorRegist")
    fun getSideDoorRegist(@Body body: ArrayList<DataItem>): Call<Boolean>

    @POST("api/TblPSMSideDoorAudit/SideDoorRegist")
    fun getSideDoorRegists(@Body body: ArrayList<DataItems>): Call<Boolean>
//    @POST("api/TblPSMSideDoorAudit/SideDoorRegist")
//    fun getSideDoorRegist(@Body body: ArrayList<DataItem>): LiveData<ArrayList<RsktItem>>

//    @POST("TblUser/LoginV2")
//    fun getUserInfo1(@Body body: LoginReq): Call<String>
//
//    @POST("TblUser/LoginV2")
//    fun getUserInfo2(@Body body: LoginReq): Call<NetRwsponse>
//
//    @POST("TblUser/LoginV2")
//    fun getUserInfo(@Body body: LoginReq): Call<String>
//
//    @POST("TblUser/LoginV2")
//    fun getLogin(@Body body: LoginReqBody): Call<String>


}