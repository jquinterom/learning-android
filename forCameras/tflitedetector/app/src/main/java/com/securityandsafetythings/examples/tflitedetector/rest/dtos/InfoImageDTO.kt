package com.securityandsafetythings.examples.tflitedetector.rest.dtos

import com.google.gson.annotations.SerializedName

class InfoImageDTO(
        name: String,
) {
    @SerializedName("imageName")
    private var mName: String = name
}