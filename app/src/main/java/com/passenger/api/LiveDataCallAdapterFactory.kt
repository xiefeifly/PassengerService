package updata.api

import androidx.lifecycle.LiveData
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        /** ParameterizedType（参数化类型）即泛型；例如：List< T>、Map< K,V>等带有参数化的对象 */
        val selfType = getRawType(returnType)
        if (selfType != LiveData::class.java) {
//            LogUtils.e("Response must be LiveData.class. error: type is $selfType")
            throw IllegalStateException("return type must be LiveData.class. error: type is $selfType")
        }
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = getRawType(observableType)
        if (rawObservableType != ApiResponse::class.java) {
            throw IllegalArgumentException("type must be a resource")
        }
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }
        val bodyType = getParameterUpperBound(0, observableType)
        return LiveDataCallAdapter<Any>(
            bodyType
        )
    }

    class LiveDataCallAdapter<T>(private val responseType: Type) :
        CallAdapter<T, LiveData<ApiResponse<T>>> {

        override fun responseType() = responseType

        override fun adapt(call: Call<T>): LiveData<ApiResponse<T>> {
            return object : LiveData<ApiResponse<T>>() {
                /** 确保多线程的情况下安全的运行,不会被其它线程打断，一直等到该方法执行完成，才由JVM从等待队列中选择其它线程进入 */
                private var started = AtomicBoolean(false)

                //处于active状态的观察者(observe)个数从0变为1，回调LiveData的onActive()方法
                override fun onActive() {
                    super.onActive()
                    //把当前对象值与expect相比较,如果相等，把对象值设置为update，并返回为true
                    if (started.compareAndSet(false, true)) {
                        call.enqueue(object : Callback<T> {
                            override fun onResponse(call: Call<T>, response: Response<T>) {
                                postValue(
                                    ApiResponse.create<T>(
                                        response
                                    )
                                )
                            }

                            override fun onFailure(call: Call<T>, throwable: Throwable) {
                                postValue(
                                    ApiResponse.create<T>(
                                        UNKNOWN_ERROR_CODE,
                                        throwable

                                    )
                                )
                            }
                        })
                    }
                }
            }
        }
    }
}