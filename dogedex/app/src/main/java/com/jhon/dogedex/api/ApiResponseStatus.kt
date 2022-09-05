package com.jhon.dogedex.api

import com.jhon.dogedex.Dog

sealed class ApiResponseStatus() {
    class Success(val dogList: List<Dog>) : ApiResponseStatus()
    class Loading() : ApiResponseStatus()
    class Error(val messageId: Int) : ApiResponseStatus()
}